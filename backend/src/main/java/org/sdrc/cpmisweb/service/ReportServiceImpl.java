/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 10-Jul-2019
 */
package org.sdrc.cpmisweb.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sdrc.cpmisweb.domain.AccountDetails;
import org.sdrc.cpmisweb.domain.Indicator;
import org.sdrc.cpmisweb.domain.IndicatorClassification;
import org.sdrc.cpmisweb.domain.Timeperiod;
import org.sdrc.cpmisweb.model.IndicatorClassificationType;
import org.sdrc.cpmisweb.model.QuarterlyAggregateRule;
import org.sdrc.cpmisweb.model.TimeperiodModel;
import org.sdrc.cpmisweb.model.ValueObject;
import org.sdrc.cpmisweb.repository.AccountDetailsRepository;
import org.sdrc.cpmisweb.repository.IndicatorClassificationRepository;
import org.sdrc.cpmisweb.repository.IndicatorRepository;
import org.sdrc.cpmisweb.repository.TimePeriodRepository;
import org.sdrc.cpmisweb.util.Constant;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {
	
	@Autowired IndicatorClassificationRepository indicatorClassificationRepository;
	
	@Autowired AccountRepository accountRepository;
	
	@Autowired AccountDetailsRepository accountDetailsRepository;
	
	@Autowired IndicatorRepository indicatorRepository;
	
	@Autowired TimePeriodRepository timePeriodRepository;
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONArray fetchAllSectors() {
		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account acc = accountRepository.findById((Integer)user.getUserId());
		AccountDetails accountDetails = accountDetailsRepository.findByAccount(acc);
		
		List<IndicatorClassification> indicatorClassifications = null;
		
		if(accountDetails.getUserTypeId().getUserTypeId().equals(Constant.STATE_LEVEL_USER_TYPE_ID) || accountDetails.getUserTypeId().getUserTypeId().equals(Constant.DCPU_USER_TYPE_ID))
			indicatorClassifications = indicatorClassificationRepository.findByIndicatorClassificationType(IndicatorClassificationType.SC);
		else
			indicatorClassifications = indicatorClassificationRepository.findByIndicatorClassificationTypeAndUserDomainUserTypeId(IndicatorClassificationType.SC, accountDetails.getUserTypeId().getUserTypeId());
		
		if(!accountDetails.getSjpuAccess())
			indicatorClassifications = indicatorClassificationRepository.findByIndicatorClassificationTypeAndNameNotLike(IndicatorClassificationType.SC, "%(SJPU%");
		
		JSONArray allSectorArr = new JSONArray();
		for (IndicatorClassification indicatorClassification : indicatorClassifications) {
			JSONObject sectorObj = new JSONObject();
			if (indicatorClassification.getParent() == null) {
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
	public List<ValueObject> fetchIndicatorsForReport(String sector) {
		IndicatorClassification sectorIC = new IndicatorClassification();
		sectorIC.setIndicatorClassificationId(new Integer(sector));

		List<Object[]> listofIndicators = indicatorRepository.findByIndicatorClassificationAndLiveIsTrue(sectorIC);

		List<ValueObject> list = new ArrayList<>();

		for (int i = 0; i < listofIndicators.size(); i++) {
			Object obj = listofIndicators.get(i);

			ValueObject vObject = new ValueObject();
			String indName = "";
			Indicator indicator = (Indicator) obj;
			indName = indicator.getIndicatorName();
			vObject.setKey(Integer.toString(indicator.getIndicatorId()));
			vObject.setValue(indName);
			list.add(vObject);
		}
		return list;
	}

	@Override
	public List<TimeperiodModel> fetchTimeperiodForReport() {
		List<Timeperiod> utTimeperiods = null;
		List<TimeperiodModel> timeperiodModels = new ArrayList<>();

		utTimeperiods = timePeriodRepository.findAllByOrderByTimeperiodIdDesc();

		for (Timeperiod utTimeperiod : utTimeperiods) {
			TimeperiodModel timeperiodModel = new TimeperiodModel();
			timeperiodModel.setTimeperiodId(utTimeperiod.getTimeperiodId());
			timeperiodModel.setStartDate(utTimeperiod.getCreatedeDate());
			timeperiodModel.setEndDate(utTimeperiod.getEndDate());
			timeperiodModel.setPeriodicity(utTimeperiod.getPeriodicity());
			timeperiodModel.setQuarter(utTimeperiod.getQuarter());
			timeperiodModel.setTimePeriod(utTimeperiod.getTimePeriod());
			timeperiodModel.setGroupName(utTimeperiod.getPeriodicity()==1?"month":"quarter");
			
			timeperiodModels.add(timeperiodModel);
		}

		return timeperiodModels;
	}
	
	@Value("${spring.datasource.url}") private String springDatasourceUrl;
	@Value("${spring.datasource.username}") private String springDatasourceUsername;
	@Value("${spring.datasource.password}") private String springDatasourcePassword;

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getReportdata(Integer indicatorId, Integer timeperiodId) {
		Timeperiod tp = timePeriodRepository.findByTimeperiodId(timeperiodId);
		String timeperiodIds="";
		String firstTimeperiodIdOfQuarter="";
		String lastTimeperiodIdOfQuarter="";
		if(tp.getPeriodicity() == Constant.PERIODICITY_FOR_MONTH){
			timeperiodIds = tp.getTimeperiodId().toString();
		}else if(tp.getPeriodicity() == Constant.PERIODICITY_FOR_QUARTER){
			List<Timeperiod> timeperiods = timePeriodRepository.findByQuarterTimeperiodIdOrderByTimeperiodIdAsc(tp.getTimeperiodId());
			timeperiodIds = timeperiods.stream().map(v->String.valueOf(v.getTimeperiodId())).collect(Collectors.joining(","));
			firstTimeperiodIdOfQuarter = timeperiods.get(0).getTimeperiodId().toString();
			lastTimeperiodIdOfQuarter = timeperiods.get(timeperiods.size()-1).getTimeperiodId().toString();
		}
        try (Connection conn = DriverManager.getConnection(springDatasourceUrl, springDatasourceUsername, springDatasourcePassword)) {
        	Indicator indicator = indicatorRepository.findOne(indicatorId);
        	if(indicator.getQuarterlyAggregateRule()!=null && tp.getPeriodicity()==2){
        		if(indicator.getQuarterlyAggregateRule().equals(QuarterlyAggregateRule.FIRSTMONTH))
        			timeperiodIds = firstTimeperiodIdOfQuarter;
        		else if(indicator.getQuarterlyAggregateRule().equals(QuarterlyAggregateRule.LASTMONTH))
        			timeperiodIds = lastTimeperiodIdOfQuarter;
        	}
        	int formId = indicator.getIndicatorClassification().getParent().getForm().getFormId();
        	try (CallableStatement function = conn.prepareCall("{ call aggregatedata(?,?,?) }")) {
        		function.setString(1, timeperiodIds);
		        function.setInt(2,formId);
		        function.setInt(3, indicatorId);
		        function.execute();
		        try(ResultSet resultSet =  function.getResultSet()){
        	
		        	JSONObject reportObj = new JSONObject();
		        	List<String> headers = new ArrayList<>();
		    		List<String> districts = new ArrayList<>();
		            while (resultSet.next()) {
		                String res = resultSet.getString(1);
		                if(res!=null){
		                org.json.JSONArray jsonObj = new org.json.JSONArray(res);
		                for(int i=0; i<jsonObj.length(); i++){
		                	org.json.JSONObject obj = (org.json.JSONObject)jsonObj.get(i);
		                	headers.add(obj.getString("subgroup"));
		        			headers = headers.stream().distinct().collect(Collectors.toList());
		        			
		        			districts.add(obj.getString("area"));
		        			districts = districts.stream().distinct().collect(Collectors.toList());
		                }
		                UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		        		Account acc = accountRepository.findById((Integer)user.getUserId());
		        		AccountDetails ad = accountDetailsRepository.findByAccount(acc);
		        		
		        		if(districts.contains(ad.getArea().getAreaName()) && ad.getUserTypeId().getUserTypeId() != Constant.STATE_LEVEL_USER_TYPE_ID){
		        			districts = new ArrayList<>();
		        			districts.add(ad.getArea().getAreaName());
		        		}else if(!districts.contains(ad.getArea().getAreaName()) && ad.getUserTypeId().getUserTypeId() != Constant.STATE_LEVEL_USER_TYPE_ID){
		        			reportObj.put("tableDetails", new ArrayList<JSONObject>());
		        			return reportObj;
		        		}
		        		
		                List<JSONObject> tableDetailsArr = new ArrayList<>();
		                JSONObject stateTableDetails = new JSONObject();
		                for(int i=0; i<districts.size(); i++){
		        			JSONObject tableDetails = new JSONObject();
		        			List<JSONObject> valueArray = new ArrayList<>();
		        			tableDetails.put("serialNo", i+1);
		        			tableDetails.put("districtName", districts.get(i));
		        			
		                    for(int j=0; j<jsonObj.length(); j++){
		                    	org.json.JSONObject obj = (org.json.JSONObject)jsonObj.get(j);
		                    	JSONObject valueObj = new JSONObject();
		        				valueObj.put("col_name", obj.getString("subgroup"));
		        				for(int k=0; k<jsonObj.length(); k++){
		        					org.json.JSONObject obj1 = (org.json.JSONObject)jsonObj.get(k);
		        					if(obj.getString("subgroup").equals(obj1.getString("subgroup")) && obj1.getString("area").equals(tableDetails.get("districtName"))
		        							&& Integer.toString(obj1.getInt("dataValue")) != null){
		        						switch (obj1.getInt("unit")) {
		        						case 1:
		        							valueObj.put("number", Integer.toString(obj1.getInt("dataValue")));
		        							break;
		        						case 2:
		        							valueObj.put("row_per", Double.toString(obj1.getDouble("dataValue")));
		        							break;
		        						case 3:
		        							valueObj.put("col_per", Double.toString(obj1.getDouble("dataValue")));
		        							break;
		        						case 4:
		        							valueObj.put("number", Integer.toString(obj1.getInt("dataValue")));
		        							break;
		        						case 5:
		        							valueObj.put("row_per", Double.toString(obj1.getDouble("dataValue")));
		        							break;
		        						case 6:
		        							valueObj.put("col_per", Double.toString(obj1.getDouble("dataValue")));
		        							break;
		
										default:
											break;
										}
		        					}
		        				}
		        				 //null value will be set to '-' here
		        				if(!valueObj.containsKey("number")) valueObj.put("number", "-");
		        				if(!valueObj.containsKey("row_per")) valueObj.put("row_per", "-");
		        				if(!valueObj.containsKey("col_per")) valueObj.put("col_per", "-");
		        				
		        				if(!valueArray.contains(valueObj)) valueArray.add(valueObj);
		                    }//end j
		                 
		        			tableDetails.put("valueArray", valueArray);
		        			if(tableDetails.get("districtName").equals("Bihar"))
		        				stateTableDetails = tableDetails;
		        			else
		        				tableDetailsArr.add(tableDetails);
		                }//end i
		                reportObj.put("headers", headers);
		                tableDetailsArr.add(stateTableDetails);
		        		reportObj.put("tableDetails", tableDetailsArr);
		            }
		        		return reportObj;
		            }//end while
	        	}
        	}    
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.sdrc.cpmisweb.service.ReportService#getRawDataReport(java.lang.Integer, java.lang.Integer, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public boolean getRawDataReport(Integer timeperiodId, Integer userTypeId, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sdrc.cpmisweb.service.ReportService#fetchUserTypesForRawDataReport()
	 */
	@Override
	public List<ValueObject> fetchUserTypesForRawDataReport() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sdrc.cpmisweb.service.ReportService#fetchTimeperiodForRawDataReport(java.lang.Integer)
	 */
	@Override
	public List<ValueObject> fetchTimeperiodForRawDataReport(Integer userTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

}