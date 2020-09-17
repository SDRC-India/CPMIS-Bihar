/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 31-Jul-2019
 */
package org.sdrc.cpmisweb.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sdrc.cpmisweb.domain.AccountDetails;
import org.sdrc.cpmisweb.domain.Area;
import org.sdrc.cpmisweb.domain.Indicator;
import org.sdrc.cpmisweb.domain.IndicatorClassification;
import org.sdrc.cpmisweb.domain.IndicatorClassificationIndicatorUnitSubgroupMapping;
import org.sdrc.cpmisweb.domain.IndicatorUnitSubgroup;
import org.sdrc.cpmisweb.domain.Subgroup;
import org.sdrc.cpmisweb.domain.Timeperiod;
import org.sdrc.cpmisweb.domain.Unit;
import org.sdrc.cpmisweb.model.Data;
import org.sdrc.cpmisweb.model.DataCollectionModel;
import org.sdrc.cpmisweb.model.DataModel;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.model.IndicatorClassificationType;
import org.sdrc.cpmisweb.model.LineSeries;
import org.sdrc.cpmisweb.model.ValueObject;
import org.sdrc.cpmisweb.repository.AccountDetailsRepository;
import org.sdrc.cpmisweb.repository.AreaRepository;
import org.sdrc.cpmisweb.repository.IndicatorClassificationRepository;
import org.sdrc.cpmisweb.repository.IndicatorRepository;
import org.sdrc.cpmisweb.repository.IndicatorUnitSubgroupRepository;
import org.sdrc.cpmisweb.repository.SubmissionRepository;
import org.sdrc.cpmisweb.repository.TimePeriodRepository;
import org.sdrc.cpmisweb.repository.UnitRepository;
import org.sdrc.cpmisweb.util.Constant;
import org.sdrc.cpmisweb.util.DashboardHeaderFooter;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {
	
	@Autowired AccountRepository accountRepository;
	@Autowired AccountDetailsRepository accountDetailsRepository;
	@Autowired IndicatorClassificationRepository indicatorClassificationRepository;
	@Autowired IndicatorRepository indicatorRepository;
	@Autowired SubmissionRepository submissionRepository;
	@Autowired AreaRepository areaRepository;
	@Autowired IndicatorUnitSubgroupRepository indicatorUnitSubgroupRepository;
	@Autowired TimePeriodRepository timePeriodRepository;
	@Autowired UnitRepository unitRepository;
	@Autowired ServletContext context;
	@Autowired Environment environment;
	
	private Map<String, List<LineSeries>> dataByArea = null;

	private Map<String, Integer> ranks = null;

	private Map<String, Map<String, List<ValueObject>>> dataByTPBYSource = null;
	
	private List<String> topPerformers = null;

	private List<String> bottomPerformers = null;
	
	private static DecimalFormat df = new DecimalFormat(".#");
	
	@Value("${spring.datasource.url}") private String springDatasourceUrl;
	@Value("${spring.datasource.username}") private String springDatasourceUsername;
	@Value("${spring.datasource.password}") private String springDatasourcePassword;

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray fetchSectorsForDashboard() {
		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account acc = accountRepository.findById((Integer)user.getUserId());
		AccountDetails accountDetails = accountDetailsRepository.findByAccount(acc);
		List<IndicatorClassification> indicatorClassifications = new ArrayList<>();
		
		if(accountDetails.getUserTypeId().getUserTypeId() == Constant.STATE_LEVEL_USER_TYPE_ID)
			indicatorClassifications = indicatorClassificationRepository.findByIndicatorClassificationType(IndicatorClassificationType.SC);
		else
			indicatorClassifications = indicatorClassificationRepository
				.findByIndicatorClassificationTypeAndUserDomainUserTypeId(IndicatorClassificationType.SC, accountDetails.getUserTypeId().getUserTypeId());
		
		if(!accountDetails.getSjpuAccess())
			indicatorClassifications = indicatorClassificationRepository.findByIndicatorClassificationTypeAndNameNotLike(IndicatorClassificationType.SC, "%(SJPU%");
		
		JSONArray allSectorArr = new JSONArray();
		for (IndicatorClassification indicatorClassification : indicatorClassifications) {
			JSONObject sectorObj = new JSONObject();
			if (null == indicatorClassification.getParent()) {
				sectorObj.put("key", indicatorClassification.getIndicatorClassificationId());
				sectorObj.put("value", indicatorClassification.getName());
				sectorObj.put("formId", indicatorClassification.getUserDomain().getUserTypeId());
				sectorObj.put("description", -1);
			} else {
				sectorObj.put("key", indicatorClassification.getIndicatorClassificationId());
				sectorObj.put("value", indicatorClassification.getName());
				sectorObj.put("formId", indicatorClassification.getUserDomain().getUserTypeId());
				sectorObj.put("description", indicatorClassification.getParent().getIndicatorClassificationId());
			}
			allSectorArr.add(sectorObj);
		}
		return allSectorArr;
	}

	@Override
	public List<ValueObject> fetchIndicators(String sector) {
		IndicatorClassification sectorIC = new IndicatorClassification();
		sectorIC.setIndicatorClassificationId(new Integer(sector));

		List<Object[]> listofIndicators = indicatorRepository.findByICType(sectorIC, Constant.UNITS_TO_BE_SHOWN_IN_DASHBOARD);

		List<ValueObject> list = new ArrayList<>();

		for (int i = 0; i < listofIndicators.size(); i++) {
			Object[] objects = listofIndicators.get(i);

			ValueObject vObject = new ValueObject();
			String indName = "";
			String unitName = "";
			String subName = "";
			for (Object obj : objects) {
				if (obj instanceof Indicator) {
					Indicator utIUS = (Indicator) obj;
					indName = utIUS.getIndicatorName();
					vObject.setKey(Integer.toString(utIUS.getIndicatorId()));
				} else if (obj instanceof Unit) {
					Unit unitEn = (Unit) obj;
					unitName = unitEn.getUnitName();
				} else if (obj instanceof Subgroup) {
					Subgroup subgroupValsEn = (Subgroup) obj;
					subName = subgroupValsEn.getSubgroupVal();
				} else if (obj instanceof IndicatorClassificationIndicatorUnitSubgroupMapping) {
					IndicatorClassificationIndicatorUnitSubgroupMapping utIUS = (IndicatorClassificationIndicatorUnitSubgroupMapping) obj;
					vObject.setDescription(Integer.toString(utIUS.getIndicatorUnitSubgroup().getIndicatorUnitSubgroupId()));
				}
			}
			if(unitName.equals("COL PERCENT"))
				unitName = "PERCENT";
			//if the customer needs to have percentage in indicator list then add '3' in the following constant value
			if(Constant.UNITS_TO_BE_SHOWN_IN_DASHBOARD.length == 1)
				vObject.setValue(indName + ", " + subName);
			else
				vObject.setValue(indName + ", " + subName + " (" + unitName + ")");
			
			list.add(vObject);
		}
		return list;
	}

	@Override
	public List<ValueObject> fetchUtTimeperiod(int sourceNid) {
		List<ValueObject> valueObjects = new ArrayList<>();
		List<Object[]> timeperiods = submissionRepository.findTimeperiodByFormIdAndFormStatus(sourceNid, FormStatus.FINALIZED.toString());

		for (Object[] utTimeperiod : timeperiods) {
			ValueObject object = new ValueObject();
			object.setKey(utTimeperiod[0].toString());
			object.setValue(utTimeperiod[1].toString());
			valueObjects.add(object);
		}
		return valueObjects;
	}

	@Override
	public DataCollectionModel fetchData(String indicatorId, String sourceId, String parentAreaCode,
			String timeperiodId, Integer childLevel) {

		try{
		dataByArea = null;
		ranks = null;
		dataByTPBYSource = null;
		// get all areas less than or equal to the selected level
		Area[] utAreas = areaRepository.getAreaNid(parentAreaCode, childLevel);

		// get parentArea from the area list
		Area area = getParentUtArea(utAreas, parentAreaCode);

		ArrayList<Area> list = new ArrayList<>();
		// get children of the select area.
		getChildren(utAreas, childLevel, area.getAreaId(), list);

		Integer[] areaNids = new Integer[list.size()];

		IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorUnitSubgroupId(Integer.parseInt(indicatorId));

		boolean isIndicatorPositive = ius.getIndicator().isHighIsGood();

		int i = 0;
		for (Area utAreaEn : list) {
			areaNids[i] = utAreaEn.getAreaId();
			i++;
		}

		DataCollectionModel utDataCollection = getUtdataCollection(indicatorId, timeperiodId, sourceId, areaNids, isIndicatorPositive);

		return utDataCollection;
		}catch (Exception e) {
			log.error("Critical:: exception while fetching dashboard data.",e);
			return null;
		}
		
	}
	
	private Area getParentUtArea(Area[] utAreas, String areaId) {
		Area utAreaen = null;
		for (Area utAreaEn : utAreas) {
			if (utAreaEn.getAreaCode().equalsIgnoreCase(areaId)) {
				utAreaen = utAreaEn;
				break;
			}
		}
		return utAreaen;
	}
	
	private void getChildren(Area[] utAreas, int i, int parentNid, ArrayList<Area> list) {

		for (Area utAreaEn : utAreas) {
			if (utAreaEn.getParentAreaId() == parentNid) {
				if (utAreaEn.getLevel() == i)
					list.add(utAreaEn);
				else
					getChildren(utAreas, i, utAreaEn.getAreaId(), list);
			}
		}

	}
	
	@Transactional(readOnly = true)
	public DataCollectionModel getUtdataCollection(String indicatorId, String timePeriodNid, String sourceId, Integer[] areaNid, boolean isIndicatorPositive) throws ParseException {

		DataCollectionModel collection = new DataCollectionModel();

		// this will fetch the data for the selected time-period
		// fetch the data for the selected time-period
		List<Data> data = new ArrayList<>();
		 try (Connection conn = DriverManager.getConnection(springDatasourceUrl, springDatasourceUsername, springDatasourcePassword)) {
			 int formId = Integer.parseInt(sourceId);
			 try (CallableStatement function = conn.prepareCall("{ call getdashboarddata(?,?,?) }")) {
				 	function.setString(1, timePeriodNid);
			        function.setInt(2,formId);
			        function.setInt(3, Integer.parseInt(indicatorId));
			        function.execute();
			        try(ResultSet resultSet =  function.getResultSet()){
			        	 while (resultSet.next()) {
			        		 String res = resultSet.getString(1);
			                 if(res!=null){
				                 org.json.JSONArray jsonObj = new org.json.JSONArray(res);
				                 for(int i=0; i<jsonObj.length(); i++){
				                	 Data d = new Data();
				                	 org.json.JSONObject obj1 = (org.json.JSONObject)jsonObj.get(i);
				                	 d.setPercentage(Double.parseDouble(obj1.get("dataValue").toString()));
				                	 d.setArea(areaRepository.findByAreaName(obj1.get("area_name").toString()));
				                	 d.setTimePeriod(timePeriodRepository.findByTimeperiodId(Integer.parseInt(timePeriodNid)));
				                	 d.setUnit(unitRepository.findOne(Integer.parseInt(obj1.get("unit").toString())));
				                	 
				                	 data.add(d);
				                 }
			                 }
			        	 }//end of while
			        } catch (JSONException e) {
						log.error("Exception while fetching dashboard data.",e);
					}
			 }
			 
		 }catch (SQLException e) {
	        System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
	     }
		 data = data.stream().sorted(Comparator.comparing(Data::getPercentage)).collect(Collectors.toList());
		if (data != null && !data.isEmpty()) {
			List<ValueObject> list = new ArrayList<>();

			// this will fetch the data for the selected time-period and
			// populate the legend

			list = populateLegends(data, isIndicatorPositive);
			collection.setLegends(list);
			if (data.get(0).getPercentage() - (data.get(data.size()-1).getPercentage()) == 0) {
				List<String> blankList = new ArrayList<>();
				collection.setTopPerformers(blankList);
				collection.setBottomPerformers(blankList);
			} else {
				collection.setTopPerformers(topPerformers);
				collection.setBottomPerformers(bottomPerformers);
			}

			Data utData = null;
			Area utAreaEn = null;
			Double value = null;

			for (Data dataObject : data) {
				DataModel utDataModel = new DataModel();

				utData = dataObject;
				utAreaEn = dataObject.getArea();

				value = utData.getPercentage();

				if ((utData.getPercentage()) > 1)
					utDataModel.setValue(df.format(utData.getPercentage()));
				else
					utDataModel.setValue("0" + df.format(utData.getPercentage()));

				utDataModel.setAreaCode(utAreaEn.getAreaCode());
				utDataModel.setAreaName(utAreaEn.getAreaName());
				utDataModel.setAreaNid(utAreaEn.getAreaId());

				utDataModel.setRank(ranks != null && ranks.get(utAreaEn.getAreaCode()) != null ? Integer.toString(ranks.get(utAreaEn.getAreaCode())) : null);
				if (list != null) {
					setCssForDataModel(list, utDataModel, value, indicatorId, sourceId, isIndicatorPositive);
				}
				utDataModel.setUnit("percent");
				if(utData.getUnit().getUnitId() == 1)
					utDataModel.setValue(String.valueOf(Math.round(Double.parseDouble(utDataModel.getValue()))));
				
				collection.dataCollection.add(utDataModel);
			}
		}
		return collection;
	}
	
	@Transactional(readOnly = true)
	private List<ValueObject> populateLegends(List<Data> data, boolean isIndicatorPositive) throws ParseException {
		// TO DO: make this configuration based.
		int range = 5;
		Double minDataValue = null;
		Double maxDataValue = null;
		Integer minDataValue1 = null;
		Integer maxDataValue1 = null;
		String firstslices = Constant.Slices.FIRST_SLICE;
		String secondslices = Constant.Slices.SECOND_SLICE;
		String thirdslices = Constant.Slices.THIRD_SLICE;
		String fourthslices = Constant.Slices.FOUTRH_SLICE;
		String fifthslices = Constant.Slices.FIFTH_SLICE;
		String sixthslices = Constant.Slices.SIXTH_SLICE;
		List<ValueObject> list = new ArrayList<ValueObject>();

		if (data != null && !data.isEmpty()) {
				minDataValue = getFormattedDouble(data.get(0).getPercentage());
				minDataValue1 = Integer.parseInt(String.valueOf(Math.round(minDataValue)));
				maxDataValue = getFormattedDouble(data.get(data.size()-1).getPercentage());
				maxDataValue1 = Integer.parseInt(String.valueOf(Math.round(maxDataValue)));
				Double difference = (maxDataValue - minDataValue) / range;
				if (difference == 0) {
					String firstSliceValue = String.valueOf(minDataValue1) + " - " + String.valueOf(minDataValue1);
					list.add(!isIndicatorPositive ? new ValueObject(firstSliceValue, firstslices) : new ValueObject(firstSliceValue, fourthslices));
					list.add(new ValueObject("Not Available", fifthslices));
				} else {
	
					String firstSliceValue = String.valueOf(minDataValue1) + " - " + String.valueOf(Math.round(getFormattedDouble(minDataValue1 + difference)));
					String sixthSliceValue = String.valueOf(Math.round(getFormattedDouble(minDataValue1 + difference + 1))) + " - " + String.valueOf(minDataValue1 + (2 * Math.round(difference)));
					String secondSliceValue = String.valueOf(minDataValue1 + 1 + (2 * Math.round(difference))) + " - " + String.valueOf(minDataValue1 + (3 * Math.round(difference)));
					String thirdSliceValue = String.valueOf(minDataValue1 + 1 +  (3 * Math.round(difference))) + " - " + String.valueOf(minDataValue1 + (4 * Math.round(difference)));
					String fourthSliceValue = String.valueOf(minDataValue1 + 1 +  (4 * Math.round(difference))) + " - " + String.valueOf((maxDataValue1));
	
					if (isIndicatorPositive) {
						list.add(new ValueObject(firstSliceValue, firstslices));
						list.add(new ValueObject(sixthSliceValue, sixthslices));
						list.add(new ValueObject(secondSliceValue, secondslices));
						list.add(new ValueObject(thirdSliceValue, thirdslices));
						list.add(new ValueObject(fourthSliceValue, fourthslices));
						list.add(new ValueObject("Not Available", fifthslices));
					} else {
						list.add(new ValueObject(firstSliceValue, fourthslices));
						list.add(new ValueObject(sixthSliceValue, thirdslices));
						list.add(new ValueObject(secondSliceValue, secondslices));
						list.add(new ValueObject(thirdSliceValue, sixthslices));
						list.add(new ValueObject(fourthSliceValue, firstslices));
						list.add(new ValueObject("Not Available", fifthslices));
					}
				}
			
		}

		// calculates the rank for the area codes for the selected time-period
		populateRank(data, isIndicatorPositive);

		return list != null && !list.isEmpty() ? list : null;

	}
	
	public Double getFormattedDouble(Double value) {
		Double formattedValue = value != null ? new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() : null;
		return formattedValue;
	}
	
	private void setCssForDataModel(List<ValueObject> list, DataModel data, Double value, String indicatorId, String sourceId, boolean isIndicatorPositive) {

		for (int index = 0; index < list.size(); index++) {
			ValueObject vObject = list.get(index);
			String[] vArray = vObject != null ? ((String) vObject.getKey()).split(" - ") : null;

			if (list.size() == 2) {
				if (isIndicatorPositive) {
					data.setCssClass(Constant.Slices.FOUTRH_SLICE);
				} else {
					data.setCssClass(Constant.Slices.FIRST_SLICE);
				}
			}

			else if (index == 5 || (vArray != null && new Double(vArray[0]).doubleValue() <= value && value <= new Double(vArray[1]).doubleValue())) {

				if (isIndicatorPositive) {
					switch (index) {
					case 0:
						data.setCssClass(Constant.Slices.FIRST_SLICE);
						break;
					case 1:
						data.setCssClass(Constant.Slices.SIXTH_SLICE);
						break;
					case 2:
						data.setCssClass(Constant.Slices.SECOND_SLICE);
						break;

					case 3:
						data.setCssClass(Constant.Slices.THIRD_SLICE);
						break;
					case 4:
						data.setCssClass(Constant.Slices.FOUTRH_SLICE);
						break;

					}
				} else {
					switch (index) {
					case 0:
						data.setCssClass(Constant.Slices.FOUTRH_SLICE);
						break;
					case 1:
						data.setCssClass(Constant.Slices.THIRD_SLICE);
						break;
					case 2:
						data.setCssClass(Constant.Slices.SECOND_SLICE);
						break;

					case 3:
						data.setCssClass(Constant.Slices.SIXTH_SLICE);
						break;
					case 4:
						data.setCssClass(Constant.Slices.FIRST_SLICE);
						break;
					}
				}

			}

		}
	}
	
	private void populateRank(List<Data> data, boolean isIndicatorPositive) {

		ranks = new HashMap<String, Integer>();
		topPerformers = new ArrayList<String>();
		bottomPerformers = new ArrayList<String>();
		String finalData;
		if (data != null && !data.isEmpty()) {
			int rank = 0;
			double dataValue = 0.0;
			Area utArea = null;
			Data utData = null;
			if (isIndicatorPositive) {
				for (int index = data.size() - 1; index >= 0; index--) {
					utData = data.get(index);
					utArea = data.get(index).getArea();

					finalData = utData.getUnit().getUnitId()==1?String.valueOf(Math.round(utData.getPercentage())):String.valueOf(utData.getPercentage());
					// populate the performance by area list
					if ((data.get(0)).getPercentage() == (data.get(data.size()-1).getPercentage())) {
						ranks.put(utArea.getAreaCode(), 1);

						topPerformers.clear();
						bottomPerformers.clear();
					} else {
						if (data.size() >= 6) {
							if (index >= data.size() - 3) {

								topPerformers.add(utArea.getAreaName() + " - " + finalData);
							}
							if (index < 3) {
								bottomPerformers.add(utArea.getAreaName() + " - " + finalData);

							}
						} else if (index <= 2) {
							bottomPerformers.add(utArea.getAreaName() + " - " + (finalData));
						} else {
							topPerformers.add(utArea.getAreaName() + " - " + (finalData));
						}

						if (dataValue == utData.getPercentage() && index != data.size() - 1) {
							ranks.put(utArea.getAreaCode(), rank);
							continue;
						}
						rank++;
						dataValue = utData.getPercentage();

						ranks.put(utArea.getAreaCode(), rank);
					}

				}
			} else {
				for (int index = 0; index < data.size(); index++) {
					utData = data.get(index);
					utArea = data.get(index).getArea();

					finalData = utData.getUnit().getUnitId()==1?String.valueOf(Math.round(utData.getPercentage())):String.valueOf(utData.getPercentage());
					// populate the performance by area list

					if (data.get(0).getPercentage() == (data.get(data.size()-1).getPercentage())) {
						ranks.put(utArea.getAreaCode(), 1);
						topPerformers.clear();
						bottomPerformers.clear();
					} else {
						if (data.size() >= 6) {
							if (index < 3) {

								topPerformers.add(utArea.getAreaName() + " - " + (finalData));
							}
							if (index >= data.size() - 3) {
								bottomPerformers.add(utArea.getAreaName() + " - " + (finalData));
							}
						} else if (index <= 2) {
							topPerformers.add(utArea.getAreaName() + " - " + (finalData));
						} else {
							bottomPerformers.add(utArea.getAreaName() + " - " + (finalData));
						}

						if (dataValue == utData.getPercentage() && index != 0) {
							ranks.put(utArea.getAreaCode(), rank);
							continue;
						}
						rank++;
						dataValue = utData.getPercentage();

						ranks.put(utArea.getAreaCode(), rank);
					}

					ranks.put(utArea.getAreaCode(), rank);
				}
			}

		}
	}

	@Override
	public List<List<LineSeries>> fetchChartData(Integer iusNid, Integer areaNid) throws ParseException {
		List<List<LineSeries>> LineCharts = new ArrayList<>();
		List<LineSeries> dataSeries = new ArrayList<>();
		List<Data> data = new ArrayList<>();
		int formId = indicatorRepository.findOne(indicatorUnitSubgroupRepository.findOne(iusNid).getIndicator().getIndicatorId()).getIndicatorClassification().getParent().getForm().getFormId();
		String source = indicatorClassificationRepository.findByIndicatorClassificationTypeAndFormFormId(IndicatorClassificationType.SR, formId).getName();
		 try (Connection conn = DriverManager.getConnection(springDatasourceUrl, springDatasourceUsername, springDatasourcePassword)) {
	        	try (CallableStatement function = conn.prepareCall("{ call getlinechartdata(?,?,?) }")) {
	        		function.setInt(1, formId);
			        function.setInt(2,iusNid);
			        function.setInt(3, areaNid);
			        function.execute();
			        try(ResultSet resultSet =  function.getResultSet()){
			        	 while (resultSet.next()) {
			        		 String res = resultSet.getString(1);
			                 if(res!=null){
				                 org.json.JSONArray jsonObj = new org.json.JSONArray(res);
				                 for(int i=0; i<jsonObj.length(); i++){
				                	 Data d = new Data();
				                	 org.json.JSONObject obj1 = (org.json.JSONObject)jsonObj.get(i);
				                	 d.setPercentage(Double.parseDouble(obj1.get("dataValue").toString()));
				                	 d.setArea(areaRepository.findOne(areaNid));
				                	 d.setTimePeriod(timePeriodRepository.findByTimeperiodId(Integer.parseInt(obj1.get("timeperiod").toString())));
				                	 
				                	 data.add(d);
				                 }
			                 }
			        	 }//end of while
			        }
	        	}
		 }catch (Exception e) {
	            log.error("Error while fetching linechart data.",e);
	     }
//		List<Object[]> listData = dataRepository.findData(iusNid, areaNid);
		populateDataByTimePeroid(data, source);

		IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorUnitSubgroupId(iusNid);

		boolean isPositveIndicator = ius.getIndicator().isHighIsGood();

		if (!data.isEmpty()) {
			for (int i = 0; i < data.size(); i++) {
				
				LineSeries lineChat = new LineSeries();
				lineChat.setSource(source);
				lineChat.setValue(String.valueOf(Math.round(data.get(i).getPercentage())));
				lineChat.setDate(data.get(i).getTimePeriod().getTimePeriod());
					

				for (Data dataObject : data) {
					Area utAreaEn = dataObject.getArea();
					String areaCode = utAreaEn.getAreaCode();
					List<ValueObject> list = dataByTPBYSource.get(areaCode).get(source);

					BigDecimal percentChange = null;
					
					percentChange = list.size() >= 2 && (Double) (list.get(list.size() - 2).getValue()) > 0 ? new BigDecimal(((Math.abs((Double) (list.get(list.size() - 2).getValue()) - (Double) (list.get(list.size() - 1).getValue()))) / ((Double) (list.get(list.size() - 2).getValue()))) * 100).setScale(1, BigDecimal.ROUND_HALF_UP)
							: new BigDecimal(((Double) (list.get(list.size() - 1).getValue())) * 100);

					if (data.size() > 1) {
						lineChat.setPercentageChange(percentChange != null ? ((Double) (list.get(data.size() - 1).getValue()) >= (Double) (list.get(data.size() - 2).getValue()) ? percentChange.toString() : "-" + percentChange.toString()) : null);
					} else {
						lineChat.setPercentageChange(percentChange.toString());
					}
					lineChat.setIsPositive(percentChange != null && list.size() >= 2 ? (Double) (list.get(data.size() - 1).getValue()) > (Double) (list.get(data.size() - 2).getValue()) ? isPositveIndicator ? true : false : isPositveIndicator ? false : true : false);

					if (isPositveIndicator && ((list.size() < 2) || (((Double) (list.get(data.size() - 1).getValue()) > (Double) (list.get(data.size() - 2).getValue()))))) {
						lineChat.setCssClass("uptrend");
						lineChat.setIsUpward(true);
					} else if (isPositveIndicator && ((list.size() < 2) || (((Double) (list.get(data.size() - 1).getValue()) < (Double) (list.get(data.size() - 2).getValue()))))) {
						lineChat.setCssClass("downtrend");
						lineChat.setIsUpward(false);
					} else if (!isPositveIndicator && ((list.size() < 2) || (((Double) (list.get(data.size() - 1).getValue()) < (Double) (list.get(data.size() - 2).getValue()))))) {
						lineChat.setCssClass("uptrend");
						lineChat.setIsUpward(false);
					}
					if (!isPositveIndicator && ((list.size() < 2) || (((Double) (list.get(data.size() - 1).getValue()) > (Double) (list.get(data.size() - 2).getValue()))))) {
						lineChat.setCssClass("downtrend");
						lineChat.setIsUpward(true);
					}

				}

				dataSeries.add(lineChat);
			}
		}
		LineCharts.add(dataSeries);
		return LineCharts;

	}
	
	private void populateDataByTimePeroid(List<Data> listData, String source) throws ParseException {
		dataByArea = new HashMap<>();

		dataByTPBYSource = new HashMap<>();

		if (listData != null && !listData.isEmpty()) {
			Data utData = null;
			Area utAreaEn = null;
			Timeperiod utTimeperiod = null;

			for (Data dataObject : listData) {
				utData = dataObject;
				utAreaEn = dataObject.getArea();
				utTimeperiod = dataObject.getTimePeriod();

				if (dataByTPBYSource.containsKey(utAreaEn.getAreaCode())) {
					Map<String, List<ValueObject>> dataByTPMap = dataByTPBYSource.get(utAreaEn.getAreaCode());

					if (dataByTPMap.containsKey(source)) {
						List<ValueObject> objects = dataByTPMap.get(source);
						objects.add(new ValueObject(utTimeperiod.getTimePeriod(), utData.getPercentage()));
					} else {
						List<ValueObject> objects = new ArrayList<>();
						objects.add(new ValueObject(utTimeperiod.getTimePeriod(), utData.getPercentage()));
						dataByTPMap.put(source, objects);
					}

				} else {

					Map<String, List<ValueObject>> dataByTPMap = new HashMap<>();
					List<ValueObject> objects = new ArrayList<>();
					objects.add(new ValueObject(utTimeperiod.getTimePeriod(), utData.getPercentage()));
					dataByTPMap.put(source, objects);

					dataByTPBYSource.put(utAreaEn.getAreaCode(), dataByTPMap);

				}

				if (dataByArea.containsKey(utAreaEn.getAreaCode())) {
					List<LineSeries> lineSeries = dataByArea.get(utAreaEn.getAreaCode());
					lineSeries.add(new LineSeries(source, utTimeperiod.getTimePeriod(), utData.getPercentage()));
				} else {
					List<LineSeries> lineSeries = new ArrayList<>();
					lineSeries.add(new LineSeries(source, utTimeperiod.getTimePeriod(), utData.getPercentage()));
					dataByArea.put(utAreaEn.getAreaCode(), lineSeries);
				}
			}
		}

	}
	
	@SuppressWarnings({ "resource", "unused" })
	@Override
	public String exportPDF(List<String> svgs, String indicatorId, String sourceId, String parentAreaCode, String timeperiodId, Integer childLevel) {

		String file = null;
		try {
			DataCollectionModel valList = fetchData(indicatorId, sourceId, parentAreaCode, timeperiodId, childLevel);
			IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findOne(Integer.parseInt(indicatorId));
			String indicator = indicatorRepository.findOne(ius.getIndicator().getIndicatorId()).getIndicatorName()+", "+ius.getSubgroup().getSubgroupVal();
			Timeperiod timeperiod = timePeriodRepository.findByTimeperiodId(Integer.parseInt(timeperiodId));

			new FileOutputStream(new File(environment.getProperty(Constant.OUTPUT_PATH_PDF_LINECHART) + "/map.svg")).write(svgs.get(2).getBytes());

			Font smallBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
			Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);

			Document document = new Document(PageSize.A4.rotate());
			String outputPath = "CPMIS-Dashboard-"+new Date().getTime()+".pdf";
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
			// setting Header Footer.PLS Refer to
			// org.sdrc.cpisico.util.HeaderFooter
			DashboardHeaderFooter headerFooter = new DashboardHeaderFooter(environment.getProperty("domain.name"));
			writer.setPageEvent(headerFooter);

			document.open();

			BaseColor cellColor = WebColors.getRGBColor("#E8E3E2");
			BaseColor headerCOlor = WebColors.getRGBColor("#333a3b");
			BaseColor siNoColor = WebColors.getRGBColor("#a6bdd9");

			Paragraph dashboardTitle = new Paragraph();
			dashboardTitle.setAlignment(Element.ALIGN_CENTER);
			dashboardTitle.setSpacingAfter(10);
			Chunk dashboardChunk = new Chunk("Score Card");
			dashboardTitle.add(dashboardChunk);

			Paragraph blankSpace = new Paragraph();
			blankSpace.setAlignment(Element.ALIGN_CENTER);
			blankSpace.setSpacingAfter(10);
			Chunk blankSpaceChunk = new Chunk("          ");
			blankSpace.add(blankSpaceChunk);

			Paragraph numberOfFacility = new Paragraph();
			numberOfFacility.setAlignment(Element.ALIGN_CENTER);
			Chunk numberOfFacilityChunk = new Chunk();
			numberOfFacility.add(numberOfFacilityChunk);

			Paragraph spiderDataParagraph = new Paragraph();
			spiderDataParagraph.setAlignment(Element.ALIGN_CENTER);
			spiderDataParagraph.setSpacingAfter(10);
			Chunk spiderChunk = new Chunk("Indicator : " + indicator + "     Timeperiod : " + timeperiod.getTimePeriod());
			spiderDataParagraph.add(spiderChunk);

			byte[] legendImage = Base64.decodeBase64(svgs.get(1).split(",")[1]);
			Image legendImageInstance = Image.getInstance(legendImage);

			legendImageInstance.setAbsolutePosition(620, 80);
			legendImageInstance.scalePercent(70);
			
			Image performersImageInstance = null;
			if(!svgs.get(0).equals("No top bottom")){
				byte[] performersImage = Base64.decodeBase64(svgs.get(0).split(",")[1]);
				performersImageInstance = Image.getInstance(performersImage);
				
				performersImageInstance.setAbsolutePosition(10, 80);
				performersImageInstance.scalePercent(70);
			}
			Paragraph googleMapParagraph = new Paragraph();
			googleMapParagraph.setAlignment(Element.ALIGN_CENTER);
			googleMapParagraph.setSpacingAfter(10);
			Chunk googleMapChunk = new Chunk();
			googleMapParagraph.add(googleMapChunk);

			String css = "svg {" + "shape-rendering: geometricPrecision;" + "text-rendering:  geometricPrecision;" + "color-rendering: optimizeQuality;" + "image-rendering: optimizeQuality;" + "}";
			File cssFile = File.createTempFile("batik-default-override-", ".css");
			FileUtils.writeStringToFile(cssFile, css);

			String svg_URI_input = Paths.get(new File(environment.getProperty(Constant.OUTPUT_PATH_PDF_LINECHART) + "/map.svg").getPath()).toUri().toURL().toString();
			TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
			// Step-2: Define OutputStream to PNG Image and attach to
			// TranscoderOutput
			ByteArrayOutputStream png_ostream = new ByteArrayOutputStream();
			TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
			// Step-3: Create PNGTranscoder and define hints if required
			PNGTranscoder my_converter = new PNGTranscoder();
			// Step-4: Convert and Write output
			my_converter.transcode(input_svg_image, output_png_image);
			png_ostream.flush();

			Image spiderImage = Image.getInstance(png_ostream.toByteArray());

			int indentation = 0;
			
			float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentation) / spiderImage.getWidth()) * 110;
			spiderImage.setAbsolutePosition(190, 80);

			PdfPTable mapDataTable = new PdfPTable(4);

			float[] mapDatacolumnWidths = new float[] { 8f, 30f, 30f, 30f };
			mapDataTable.setWidths(mapDatacolumnWidths);

			PdfPCell mapDataCell0 = new PdfPCell(new Paragraph("Sl.No.", smallBold));
			PdfPCell mapDataCell1 = new PdfPCell(new Paragraph("District ", smallBold));

			PdfPCell mapDataCell3 = new PdfPCell(new Paragraph("Data Value", smallBold));
			PdfPCell mapDataCell4 = new PdfPCell(new Paragraph("Rank", smallBold));

			mapDataCell0.setBackgroundColor(siNoColor);
			mapDataCell1.setBackgroundColor(headerCOlor);

			mapDataCell3.setBackgroundColor(headerCOlor);
			mapDataCell4.setBackgroundColor(headerCOlor);

			mapDataCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell4.setHorizontalAlignment(Element.ALIGN_CENTER);

			mapDataCell0.setBorderColor(BaseColor.WHITE);
			mapDataCell1.setBorderColor(BaseColor.WHITE);
			mapDataCell3.setBorderColor(BaseColor.WHITE);
			mapDataCell4.setBorderColor(BaseColor.WHITE);

			mapDataTable.addCell(mapDataCell0);
			mapDataTable.addCell(mapDataCell1);

			mapDataTable.addCell(mapDataCell3);
			mapDataTable.addCell(mapDataCell4);

			int i = 1;
			for (DataModel mapData : valList.getDataCollection()) {

				PdfPCell data0 = new PdfPCell(new Paragraph(Integer.toString(i), dataFont));
				data0.setHorizontalAlignment(Element.ALIGN_CENTER);
				data0.setBorderColor(BaseColor.WHITE);
				PdfPCell data1 = new PdfPCell(new Paragraph(mapData.getAreaName(), dataFont));
				data1.setHorizontalAlignment(Element.ALIGN_LEFT);
				data1.setBorderColor(BaseColor.WHITE);

				PdfPCell data3 = new PdfPCell(new Paragraph(mapData.getValue(), dataFont));
				data3.setHorizontalAlignment(Element.ALIGN_CENTER);
				data3.setBorderColor(BaseColor.WHITE);

				PdfPCell data4 = new PdfPCell(new Paragraph(mapData.getRank(), dataFont));
				data4.setHorizontalAlignment(Element.ALIGN_CENTER);
				data4.setBorderColor(BaseColor.WHITE);

				if (i % 2 == 0) {
					data0.setBackgroundColor(cellColor);
					data1.setBackgroundColor(cellColor);
					data3.setBackgroundColor(cellColor);
					data4.setBackgroundColor(cellColor);
				} else {
					data0.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data3.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data4.setBackgroundColor(BaseColor.LIGHT_GRAY);
				}

				mapDataTable.addCell(data0);
				mapDataTable.addCell(data1);

				mapDataTable.addCell(data3);
				mapDataTable.addCell(data4);

				i++;

			}
			// Spider Data Table

			document.add(blankSpace);

			document.add(spiderDataParagraph);

			document.add(spiderImage);

			document.add(legendImageInstance);
			
			if (performersImageInstance != null) document.add(performersImageInstance);

			document.newPage();
			document.add(mapDataTable);

			document.close();
			file = outputPath;
		}

		catch (Exception e) {

			e.printStackTrace();
		}

		return file;
	}
	
	@SuppressWarnings("resource")
	@Override
	public String exportPDFLine(List<String> svgs, Integer iusNid, Integer areaNid) {

		String file = null;
		try {

			new FileOutputStream(new File(environment.getProperty(Constant.OUTPUT_PATH_PDF_LINECHART) + "/trendSVG.svg")).write(svgs.get(0).getBytes());
			byte[] trendImage = Base64.decodeBase64(svgs.get(0).split(",")[1]);
			Image trendImageInstance = Image.getInstance(trendImage);
			
			trendImageInstance.setAbsolutePosition(100, 80);
			trendImageInstance.scalePercent(100);

			List<List<LineSeries>> lineSeries = fetchChartData(iusNid, areaNid);

			Font smallBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
			Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);

			Document document = new Document(PageSize.A4.rotate());

			String outputPath = "CPMIS-Linechart"+new Date().getTime()+".pdf";

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
			// setting Header Footer.PLS Refer to
			// org.sdrc.cpisico.util.HeaderFooter
			DashboardHeaderFooter headerFooter = new DashboardHeaderFooter(environment.getProperty("domain.name"));
			writer.setPageEvent(headerFooter);

			document.open();

			BaseColor cellColor = WebColors.getRGBColor("#E8E3E2");
			BaseColor headerCOlor = WebColors.getRGBColor("#333a3b");
			BaseColor siNoColor = WebColors.getRGBColor("#a6bdd9");

			Paragraph dashboardTitle = new Paragraph();
			dashboardTitle.setAlignment(Element.ALIGN_CENTER);
			dashboardTitle.setSpacingAfter(10);
			Chunk dashboardChunk = new Chunk("Line Chart Score Card");
			dashboardTitle.add(dashboardChunk);

			Paragraph blankSpace = new Paragraph();
			blankSpace.setAlignment(Element.ALIGN_CENTER);
			blankSpace.setSpacingAfter(10);
			Chunk blankSpaceChunk = new Chunk("          ");
			blankSpace.add(blankSpaceChunk);

			Paragraph numberOfFacility = new Paragraph();
			numberOfFacility.setAlignment(Element.ALIGN_CENTER);
			Chunk numberOfFacilityChunk = new Chunk();
			numberOfFacility.add(numberOfFacilityChunk);

			Paragraph spiderDataParagraph = new Paragraph();
			spiderDataParagraph.setAlignment(Element.ALIGN_CENTER);
			spiderDataParagraph.setSpacingAfter(10);
			Chunk spiderChunk = new Chunk("Indicator : " + svgs.get(2) + "  Area : " + svgs.get(1));
			spiderDataParagraph.add(spiderChunk);

			String css = "svg {" + "shape-rendering: geometricPrecision;" + "text-rendering:  geometricPrecision;" + "color-rendering: optimizeQuality;" + "image-rendering: optimizeQuality;" + "}";
			File cssFile = File.createTempFile("batik-default-override-", ".css");
			FileUtils.writeStringToFile(cssFile, css);

			Paragraph googleMapParagraph = new Paragraph();
			googleMapParagraph.setAlignment(Element.ALIGN_CENTER);
			googleMapParagraph.setSpacingAfter(10);
			Chunk googleMapChunk = new Chunk();
			googleMapParagraph.add(googleMapChunk);

			PdfPTable mapDataTable = new PdfPTable(3);

			float[] mapDatacolumnWidths = new float[] { 8f, 15f, 15f };
			mapDataTable.setWidths(mapDatacolumnWidths);

			PdfPCell mapDataCell0 = new PdfPCell(new Paragraph("Sl.No.", smallBold));
			PdfPCell mapDataCell1 = new PdfPCell(new Paragraph("Data Value ", smallBold));

			PdfPCell mapDataCell3 = new PdfPCell(new Paragraph("Time Period", smallBold));

			mapDataCell0.setBackgroundColor(siNoColor);
			mapDataCell1.setBackgroundColor(headerCOlor);
			mapDataCell3.setBackgroundColor(headerCOlor);

			mapDataCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell3.setHorizontalAlignment(Element.ALIGN_CENTER);

			mapDataCell0.setBorderColor(BaseColor.WHITE);
			mapDataCell1.setBorderColor(BaseColor.WHITE);
			mapDataCell3.setBorderColor(BaseColor.WHITE);

			mapDataTable.addCell(mapDataCell0);
			mapDataTable.addCell(mapDataCell1);

			mapDataTable.addCell(mapDataCell3);

			int i = 1;
			for (LineSeries seriesData : lineSeries.get(0)) {

				PdfPCell data0 = new PdfPCell(new Paragraph(Integer.toString(i), dataFont));
				data0.setHorizontalAlignment(Element.ALIGN_CENTER);
				data0.setBorderColor(BaseColor.WHITE);
				PdfPCell data1 = new PdfPCell(new Paragraph(seriesData.getValue().toString(), dataFont));
				data1.setHorizontalAlignment(Element.ALIGN_LEFT);
				data1.setBorderColor(BaseColor.WHITE);

				PdfPCell data3 = new PdfPCell(new Paragraph(seriesData.getDate(), dataFont));
				data3.setHorizontalAlignment(Element.ALIGN_CENTER);
				data3.setBorderColor(BaseColor.WHITE);

				if (i % 2 == 0) {
					data0.setBackgroundColor(cellColor);
					data1.setBackgroundColor(cellColor);
					data3.setBackgroundColor(cellColor);

				} else {
					data0.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data3.setBackgroundColor(BaseColor.LIGHT_GRAY);
				}

				mapDataTable.addCell(data0);
				mapDataTable.addCell(data1);

				mapDataTable.addCell(data3);

				i++;

			}
			// Spider Data Table

			document.add(blankSpace);

			document.add(spiderDataParagraph);

			document.add(trendImageInstance);

			document.newPage();
			document.add(mapDataTable);

			document.close();
			file = outputPath;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return file;
	}

}