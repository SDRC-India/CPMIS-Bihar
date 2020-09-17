/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 24-Jul-2019
 */
package org.sdrc.cpmisweb.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.cpmisweb.domain.Area;
import org.sdrc.cpmisweb.domain.Submission;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.repository.AccountDetailsRepository;
import org.sdrc.cpmisweb.repository.AreaRepository;
import org.sdrc.cpmisweb.repository.SubmissionRepository;
import org.sdrc.cpmisweb.repository.TimePeriodRepository;
import org.sdrc.cpmisweb.util.Constant;
import org.sdrc.cpmisweb.util.ExcelStyleSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.models.MessageModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RawDataReportServiceImpl implements RawDataReportService {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private SubmissionRepository submissionRepository;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired
	private EngineFormRepository formRepository;

	@Autowired
	private TypeDetailRepository typeDetailRepository;

	@Autowired
	AccountDetailsRepository accountDetailsRepository;

	@Autowired
	TimePeriodRepository timeperiodRepository;

	private SimpleDateFormat dtSDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@Override
	public ResponseEntity<MessageModel> exportRawaData(Integer formId, Integer timeperiodId, Principal principal,
			OAuth2Authentication auth) {

		try {

			String timeperiod = timeperiodRepository.findOne(timeperiodId).getTimePeriod();
			List<Question> questionList = questionRepository
					.findAllByFormIdAndActiveTrueOrderByQuestionOrderAsc(formId);
			// List<Question> questionList = questionss.stream().filter(v ->
			// !"heading".equals(v.getControllerType()))
			// .collect(Collectors.toList());

			String result = "";
			List<Submission> datas = null;
			datas = submissionRepository.findAllByFormStatusAndFormIdAndTimeperiodTimeperiodId(FormStatus.FINALIZED,
					formId, timeperiodId);

			/**
			 * if no data available return
			 */
			if (datas.isEmpty()) {

				MessageModel setMessageModel = setMessageModelDataNotFound();
				return new ResponseEntity<>(setMessageModel, HttpStatus.OK);
			}

			result = getExportDatasResult(datas, questionList, formId, principal, timeperiod);

			MessageModel model = new MessageModel();
			model.setMessage(result);
			model.setStatusCode(200);

			return new ResponseEntity<>(model, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error while generating Raw Data Report with payload {},{},{}", formId, timeperiodId, e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * setting the message model here when list is empty
	 * 
	 * @return
	 */
	private MessageModel setMessageModelDataNotFound() {

		MessageModel model = new MessageModel();
		model.setMessage("Data of selected user type for selected time period is not available.");
		model.setStatusCode(204);
		return model;
	}

	private String getExportDatasResult(List<Submission> datas, List<Question> questionList, Integer formId,
			Principal principal, String timeperiod) {

		EnginesForm form = formRepository.findOne(formId);
		String formSheetName = form.getName();
		String fileName = form.getName();
		String name = form.getName();

		return exportData(datas, questionList, formId, formSheetName, fileName, name, principal, timeperiod);

	}

	@SuppressWarnings("unchecked")
	private String exportData(Object obj, List<Question> questionList, int formId, String sheetName, String fileName,
			String formName, Principal principal, String timeperiod) {

		List<?> list = (List<Submission>) obj;
		/*
		 * column name as a key
		 */
		Map<String, Question> questionMap = questionList.stream()
				.collect(Collectors.toMap(Question::getColumnName, question -> question));

		Map<String, Question> questionMapWithQuestionKey = questionList.stream()
				.filter(v -> !v.getControllerType().equals("tableWithRowWiseArithmetic"))
				.collect(Collectors.toMap(k -> k.getQuestion() + "_" + k.getParentColumnName(), question -> question));

		/*
		 * getting all the typeDetails by passing formId
		 */
		List<TypeDetail> typeDetailList = typeDetailRepository.findByFormId(formId);

		/*
		 * KEY-SlugId value-name
		 */
		Map<Integer, TypeDetail> typeDetailMap = typeDetailList.stream()
				.collect(Collectors.toMap(TypeDetail::getSlugId, typeDe -> typeDe));

		/**
		 * get all Area(only areaid and areaname)
		 */
		List<Area> areaList = areaRepository.findAll();

		Map<Integer, String> areaMap = areaList.stream().collect(Collectors.toMap(Area::getAreaId, Area::getAreaName));
		ObjectMapper mapper = new ObjectMapper();
		try {

			XSSFWorkbook workbook = null;
			XSSFSheet sheet = null;
			XSSFRow row;
			XSSFCell cell;

			/*
			 * creating a questions heading
			 */
			workbook = setQuestions(workbook, sheet, questionList, sheetName, formName, timeperiod);

			/*
			 * get style for odd cell
			 */
			CellStyle colStyleOdd = ExcelStyleSheetUtils.getStyleForOddCell(workbook);
			/*
			 * get style for even cell
			 */
			CellStyle colStyleEven = ExcelStyleSheetUtils.getStyleForEvenCell(workbook);
			/*
			 * Iterating to set submission values
			 */
			Integer rowIndex = 6;
			Integer columnIndex = 0;
			Integer mapSize = null;

			/*
			 * begin-repeat variables
			 */
			Row beginRepeatRow = null;
			Cell beginRepeatcell = null;
			Integer beginRepeatRowCount = 1;
			Integer beginRepeatcellCount = 0;
			for (int i = 0; i < list.size(); i++) {

				sheet = workbook.getSheet(sheetName);
				row = sheet.createRow(rowIndex);
				/*
				 * setting serial number value
				 */
				cell = row.createCell(columnIndex);
				cell.setCellValue(i + 1);
				cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
				columnIndex++;

				Submission submission = null;

				/*
				 * key is column name here,object is submitted value
				 */
				Map<String, Object> submissionDataMap = null;

				/*
				 * checking the type of List
				 */
				if (list.get(i) instanceof Submission) {
					submission = (Submission) list.get(i);

					submissionDataMap = mapper.convertValue(submission.getData(), Map.class);

					/**
					 * setting unique id
					 */
					cell = row.createCell(columnIndex);
					cell.setCellValue(submission.getUniqueId());
					cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
					columnIndex++;

					/*
					 * setting DISTRICT name
					 */
					cell = row.createCell(columnIndex);
					cell.setCellValue(
							accountDetailsRepository.findByAccount(submission.getCreatedBy()).getArea().getAreaName());
					cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
					columnIndex++;

					/*
					 * setting USER name
					 */
					cell = row.createCell(columnIndex);
					cell.setCellValue(submission.getCreatedBy().getUserName());
					cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
					columnIndex++;

					/*
					 * setting submission date
					 */
					cell = row.createCell(columnIndex);
					cell.setCellValue(dtSDateFormat.format(submission.getSubmissionDate()));
					cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
					columnIndex++;

					/**
					 * setting MONTH - YEAR record status
					 */

					cell = row.createCell(columnIndex);
					cell.setCellValue(submission.getTimeperiod().getTimePeriod());
					cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
					columnIndex++;

					/**
					 * setting submission status
					 */
					cell = row.createCell(columnIndex);
					cell.setCellValue(submission.getFormStatus().equals(FormStatus.FINALIZED) ? "Submitted" : "Saved");
					cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
					columnIndex++;

					/**
					 * setting aggregation status-
					 */
					cell = row.createCell(columnIndex);
					cell.setCellValue(
							submission.getFormStatus() == FormStatus.FINALIZED ? "Aggregated" : "Not aggregated");
					cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
					columnIndex++;
				}
				for (int j = 0; j < questionList.size(); j++) {
					String fieldValue = "";
					String colName = null;

					Question question = questionList.get(j);

					switch (question.getControllerType()) {
					case "heading":
						break;
					case "dropdown":
					case "segment": {
						/*
						 * get sheet
						 */
						sheet = workbook.getSheet(sheetName);
						/*
						 * get column name
						 */
						colName = question.getColumnName();

						/*
						 * with column name against find value which is SlugId
						 * of TypeDetail documnet
						 */
						List<Integer> values = null;

						if (question.getFieldType().equals("checkbox")) {
							values = submissionDataMap.get(colName) != null
									? (List<Integer>) (submissionDataMap.get(colName)) : null;
						} else {
							Integer v = submissionDataMap.get(colName) != null ? (int) (submissionDataMap.get(colName))
									: null;
							if (v != null) {
								values = new ArrayList<>();
								values.add(v);
							}
						}

						if (values != null && !values.isEmpty()) {
							/*
							 * find typedetailname against this value
							 */
							String temp = null;
							for (Integer value : values) {

								if (question.getTableName() == null) {
									/*
									 * if there is no score present in
									 * typeDetails
									 */
									if (typeDetailMap.get(value).getScore() == null)
										temp = typeDetailMap.get(value).getName();
									else// if score is present
									{
										temp = typeDetailMap.get(value).getName().concat(" - score = ")
												.concat(typeDetailMap.get(value).getScore());
									}
								}

								else {
									/*
									 * find areaname against this value
									 */
									// area_group:F1Q2@ANDfetch_tables:area$$arealevel=district
									switch (question.getTableName().split("\\$\\$")[0].trim()) {

									case "area": {

										temp = areaMap.get(value);
									}
										break;

									}
								}

								if (fieldValue.equals("")) {
									fieldValue = temp;
								} else
									fieldValue = fieldValue + "," + temp;
							}

						}

						cell = row.createCell(columnIndex);
						cell.setCellValue(fieldValue != null ? fieldValue : "");
						if(fieldValue != null){
							Integer val=null;
							try {
								val = Integer.parseInt(fieldValue);
								cell.setCellType(CellType.NUMERIC);
								cell.setCellValue(val);
							} catch (Exception e) {
								
							}
						}
						cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
						columnIndex++;
						fieldValue = "";
					}
						break;

					case "beginrepeat": {
						String brSheeetName = null;
						brSheeetName = question.getQuestion();
						/*
						 * check the size of brSheeetName is size>=28 than
						 * reduce the sheetName
						 */
						if (brSheeetName.length() >= 28) {
							brSheeetName = brSheeetName.substring(0, 17);
						}
						/*
						 * it returns rowValue to be used
						 */
						sheet = workbook.getSheet(brSheeetName);
						/*
						 * get last row num creted in the sheet
						 */
						int rowValue = sheet.getLastRowNum();
						/*
						 * assign to beginRepeatRowCount to create row
						 */
						beginRepeatRowCount = rowValue + 1;
						/*
						 * get column name of question
						 */
						colName = question.getColumnName();
						/*
						 * beginRepeat type
						 */
						List<Map<String, Object>> beginRepeatMap = (List<Map<String, Object>>) submissionDataMap
								.get(colName);

						/*
						 * get size of map
						 */
						Map<String, Object> map2 = beginRepeatMap.get(0);
						mapSize = map2.size();
						int k = 1;
						int bgCount = 0;
						if (mapSize == 0) {
							for (Question q : questionList) {
								if (q.getParentColumnName().equals(colName)) {
									bgCount++;
								}
							}
						}

						/*
						 * iterating list of submission of beginrepeat, list
						 */
						for (Map<String, Object> map : beginRepeatMap) {

							/*
							 * creating row
							 */
							beginRepeatRow = sheet.createRow(beginRepeatRowCount);
							/*
							 * creating cell
							 */
							beginRepeatcell = beginRepeatRow.createCell(beginRepeatcellCount);
							/*
							 * setting reference-id column value
							 */
							beginRepeatcell.setCellValue(i + 1);
							/*
							 * setting style
							 */
							beginRepeatcell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
							beginRepeatcellCount++;
							/*
							 * iterating Map of beginrepeat
							 */
							if (bgCount != 0) {
								for (int bg = 0; bg < bgCount; bg++) {
									j++;
									beginRepeatcell = beginRepeatRow.createCell(beginRepeatcellCount);
									beginRepeatcell.setCellValue("");
									beginRepeatcell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
									fieldValue = "";
									beginRepeatcellCount++;
								}
							} else {
								for (Map.Entry<String, Object> entry : map.entrySet()) {

									if (k == 1)
										j++;// for question-loop it should
											// increment
											// with size 1 only as value is
											// multiple
											// but qstn is one

									if (sheet.getRow(0).getCell(beginRepeatcellCount) != null) {// added
																								// during
																								// SAA
										List<Integer> values = null;
										Question currentQuestion = questionMapWithQuestionKey.get(sheet.getRow(0)
												.getCell(beginRepeatcellCount).toString().replaceAll(":", "@@split@@")
												+ "_"
												+ questionMap.get(questionMap.get(entry.getKey()).getParentColumnName())
														.getColumnName());
										String keyForMap = currentQuestion.getColumnName();
										if (currentQuestion.getControllerType().equals("dropdown")) {
											if (currentQuestion.getFieldType().equals("checkbox")) {
												values = map.get(keyForMap) != null ? (List<Integer>) map.get(keyForMap)
														: null;
											} else {
												Integer v = map.get(keyForMap) != null ? (int) map.get(keyForMap)
														: null;
												if (v != null) {
													values = new ArrayList<>();
													values.add(v);
												}
											}
											if (values != null && !values.isEmpty()) {
												String tempValue = null;
												for (Integer value : values) {
													if (questionMap.get(entry.getKey()).getTableName() == null) {
														tempValue = typeDetailMap.get(value).getName();
													}
													if (fieldValue.equals("")) {
														fieldValue = tempValue;
													} else {
														fieldValue = fieldValue + "," + tempValue;
													}
												}

											}

										} else {
											fieldValue = map.get(keyForMap) == null ? null
													: map.get(keyForMap).toString();
										}
									}

									beginRepeatcell = beginRepeatRow.createCell(beginRepeatcellCount);
									beginRepeatcell.setCellValue(fieldValue != null ? fieldValue : "");
									beginRepeatcell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
									fieldValue = "";
									beginRepeatcellCount++;

								} // end of map - innerloop
							}

							if (beginRepeatMap.size() > 1) {
								beginRepeatRowCount++;
								beginRepeatcellCount = 0;
							}
							k++;
						}

						beginRepeatcellCount = 0;
					}
						break;

					case "tableWithRowWiseArithmetic": {
						/*
						 * getting sheet
						 */
						sheet = workbook.getSheet(sheetName);
						/*
						 * column name of question
						 */
						colName = question.getColumnName();
						/*
						 * tableWithRowWiseArithmetic type
						 */
						List<Map<String, Object>> beginRepeatMap = (List<Map<String, Object>>) submissionDataMap
								.get(colName);

						for (Map<String, Object> map : beginRepeatMap) {

							for (Map.Entry<String, Object> entry : map.entrySet()) {

								j++;// for question-loop

								switch (questionMap.get(entry.getKey()).getControllerType()) {
								case "heading":
									break;
								case "dropdown":
								case "segment": {
									/*
									 * key == column name with column name
									 * against find value which is SlugId of
									 * TypeDetail documnet
									 */

									List<Integer> values = null;
									if (questionMap.get(entry.getKey()).getFieldType().equals("checkbox")) {

										values = entry.getValue() != null ? (List<Integer>) entry.getValue() : null;

									} else {
										Integer v = entry.getValue() != null ? (int) entry.getValue() : null;

										if (v != null) {
											values = new ArrayList<>();
											values.add(v);
										}

									}

									if (values != null && !values.isEmpty()) {
										/*
										 * find typedetailname against this
										 * value
										 */
										for (Integer value : values) {

											String tempValue = null;

											if (questionMap.get(entry.getKey()).getTableName() == null)
												tempValue = typeDetailMap.get(value).getName();
											else {
												/*
												 * find areaname against this
												 * value
												 */
												switch (questionMap.get(entry.getKey()).getTableName()
														.split("\\$\\$")[0].trim()) {

												case "area": {

													tempValue = areaMap.get(value);
												}
													break;

												}
											}

											if (fieldValue.equals("")) {
												fieldValue = tempValue;
											} else
												fieldValue = fieldValue + "," + tempValue;
										}

									}

									/*
									 * write in excel cell
									 */
									cell = row.createCell(columnIndex);
									cell.setCellValue(fieldValue != null ? fieldValue : "");
									cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
									fieldValue = "";
									columnIndex++;

								}
									break;

								default: {
									switch (questionMap.get(entry.getKey()).getFieldType()) {
									case "tel":{
										if (sheet.getRow(5).getCell(columnIndex) != null) {// added
																							// during
																							// SAA
												String keyForMap = questionMapWithQuestionKey.get(sheet.getRow(5)
													.getCell(columnIndex).toString().replaceAll(":", "@@split@@") + "_"
													+ questionMap.get(questionMap.get(entry.getKey()).getParentColumnName())
															.getColumnName())
													.getColumnName();
												
												fieldValue = map.get(keyForMap) == null ? null : map.get(keyForMap).toString();
												/**
												* write in excel cell
												*/
												}
												cell = row.createCell(columnIndex);
												cell.setCellType(CellType.NUMERIC);
												if(fieldValue != null)
													cell.setCellValue(Integer.parseInt(fieldValue));
												else
													cell.setCellType(CellType.BLANK);
												
												cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
												fieldValue = "";
												columnIndex++;
												}
										
										break;

									default:{
										if (sheet.getRow(5).getCell(columnIndex) != null) {// added
																							// during
																							// SAA
												String keyForMap = questionMapWithQuestionKey.get(sheet.getRow(5)
													.getCell(columnIndex).toString().replaceAll(":", "@@split@@") + "_"
													+ questionMap.get(questionMap.get(entry.getKey()).getParentColumnName())
															.getColumnName())
													.getColumnName();
												
												fieldValue = map.get(keyForMap) == null ? null : map.get(keyForMap).toString();
												/**
												* write in excel cell
												*/
												}
												cell = row.createCell(columnIndex);
												cell.setCellValue(fieldValue != null ? fieldValue : "");
												cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
												fieldValue = "";
												columnIndex++;
									}
										
									}
									
								}

								}// end of switch

							} // end of map - innerloop
						}
					}
						break;

					default: {

						sheet = workbook.getSheet(sheetName);

						colName = question.getColumnName();
						/*
						 * fieldValue- type int or string and writing in excel
						 */
						cell = row.createCell(columnIndex);

						if (question.getFieldType().equals("tel")) {

							if (submissionDataMap.get(colName) == null
									|| submissionDataMap.get(colName).toString().trim().equals("")) {
								cell.setCellType(CellType.STRING);
								cell.setCellValue("");
								cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);

							} else {

								fieldValue = submissionDataMap.get(colName).toString();
								/*
								 * check length of value if it is >9 make it
								 * string and write it in excel else make it
								 * Long type
								 */
								if (fieldValue.contains(".")) {
									cell.setCellType(CellType.STRING);
									cell.setCellValue((String.valueOf(fieldValue)));
									cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
									fieldValue = "";
								} else if (fieldValue.length() > 9) {
									cell.setCellType(CellType.STRING);
									cell.setCellValue(fieldValue);
									cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
									fieldValue = "";
								} else {
									cell.setCellValue((Long.valueOf(fieldValue)));
									cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
									fieldValue = "";
								}

							}

						} else if (question.getFieldType().equals("singledecimal")
								|| question.getFieldType().equals("doubledecimal")
								|| question.getFieldType().equals("threedecimal")) {

							if (submissionDataMap.get(colName) == null
									|| submissionDataMap.get(colName).toString().trim().equals("")) {
								cell.setCellType(CellType.STRING);
								cell.setCellValue("");
							} else {

								cell.setCellValue(Double.valueOf((String) submissionDataMap.get(colName)));
							}

							cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);

						} else {

							if (submissionDataMap.get(colName) == null
									|| submissionDataMap.get(colName).toString().trim().equals("")) {
								cell.setCellType(CellType.STRING);
								cell.setCellValue("");

							} else {
								fieldValue = (String) submissionDataMap.get(colName);
								if (formId == 5 && fieldValue.length() > 30) {

									sheet.setColumnWidth(cell.getColumnIndex(), 12000);
								}
								cell.setCellValue(fieldValue);
							}

							cell.setCellStyle(i % 2 == 0 ? colStyleEven : colStyleOdd);
							fieldValue = "";
						}
						columnIndex++;
					}
					}
				}
				rowIndex++;
				columnIndex = 0;
			}

			String name = fileName + "_" + principal.getName() + "_"
					+ new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date()) + ".xls";
			String dest = Paths.get(configurableEnvironment.getProperty(Constant.RAWDATA_REPORT_PATH)).toAbsolutePath()
					+ File.separator + name;

			FileOutputStream fos = new FileOutputStream(new File(dest));
			workbook.write(fos);
			fos.close();
			workbook.close();
			return dest;

		} catch (Exception e) {
			log.error("error while generating report with payload formId : {} : " + formId, e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * setting all the questions in excel first row and making different sheets
	 * for begin repeat type
	 * 
	 * @param workbook
	 * @param sheet
	 * @param questionList
	 * @param sheetName
	 * @return
	 */
	private XSSFWorkbook setQuestions(XSSFWorkbook workbook, XSSFSheet sheet, List<Question> questionList,
			String sheetName, String formName, String timeperiod) {

		/**
		 * make parent-id as a key, for begin-repeat
		 */
		List<Question> qList = new ArrayList<>();
		List<Question> sqList = new ArrayList<>();
		List<Question> ssubqList = new ArrayList<>();

		Map<String, List<Question>> questionMapBr = new LinkedHashMap<>();

		Map<String, List<Question>> sectionMap = new LinkedHashMap<>();
		Map<String, List<Question>> subSectionMap = new LinkedHashMap<>();

		Map<String, Integer> sectionMapMerge = new LinkedHashMap<>();
		Map<String, Integer> subSectionMapMerge = new LinkedHashMap<>();
		Map<String, String> headingMapMerge = new LinkedHashMap<>();

		for (Question q : questionList) {

			if (sectionMap.containsKey(q.getSection())) {
				sectionMap.get(q.getSection()).add(q);
			} else {
				sqList = new ArrayList<>();
				sqList.add(q);
				sectionMap.put(q.getSection(), sqList);
			}

			if (subSectionMap.containsKey(q.getSection())) {
				subSectionMap.get(q.getSubsection()).add(q);
			} else {
				ssubqList = new ArrayList<>();
				ssubqList.add(q);
				subSectionMap.put(q.getSubsection(), ssubqList);
			}

			// Merge cell calculation
			if (((q.getParentColumnName() != null && !q.getControllerType().equals("cell"))|| q.getParentColumnName() != null)
					&& (!q.getControllerType().equals("tableWithRowWiseArithmetic")&& !q.getParentColumnName().contains("BEGINREPEAT")
							&& !q.getControllerType().equals("beginrepeat"))) {
				switch (q.getControllerType().trim()) {
				case "heading":
					headingMapMerge.put(q.getSubsection(), q.getQuestion());
					break;
				default:
					if (sectionMapMerge.containsKey(q.getSection())) {
						sectionMapMerge.put(q.getSection(), sectionMapMerge.get(q.getSection()) + 1);
					} else {
						sectionMapMerge.put(q.getSection(), 1);
					}

					if (headingMapMerge.containsKey(q.getSubsection())) {
						if (subSectionMapMerge.containsKey(headingMapMerge.get(q.getSubsection()))) {
							subSectionMapMerge.put(headingMapMerge.get(q.getSubsection()),
									subSectionMapMerge.get(headingMapMerge.get(q.getSubsection())) + 1);
						} else {
							subSectionMapMerge.put(headingMapMerge.get(q.getSubsection()), 1);
						}
					} else {
						if (subSectionMapMerge.containsKey(q.getSubsection())) {
							subSectionMapMerge.put(q.getSubsection(), subSectionMapMerge.get(q.getSubsection()) + 1);
						} else {
							subSectionMapMerge.put(q.getSubsection(), 1);
						}
					}

					break;
				}
			}

			if (questionMapBr.containsKey(q.getParentColumnName())) {
				questionMapBr.get(q.getParentColumnName()).add(q);
			} else {
				qList = new ArrayList<>();
				qList.add(q);
				questionMapBr.put(q.getParentColumnName(), qList);
			}
		}

		int mergeRange = 0;
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet(sheetName);
		Row row;
		Cell cell;
		/*
		 * style for headers
		 */
		XSSFCellStyle styleForHeading = ExcelStyleSheetUtils.getStyleForHeading(workbook);

		try {
			/*
			 * SINO
			 */
			row = sheet.createRow(5);
			row.setHeight((short) 1500);
			cell = row.createCell(0);
			sheet.setColumnWidth(cell.getColumnIndex(), 1700);
			sheet.setHorizontallyCenter(true);
			cell.setCellStyle(styleForHeading);
			cell.setCellValue("Sl. No.");
			mergeRange++;

			/*
			 * user name
			 */
			cell = row.createCell(1);
			sheet.setColumnWidth(cell.getColumnIndex(), 3500);
			sheet.setHorizontallyCenter(true);
			cell.setCellStyle(styleForHeading);
			cell.setCellValue("Unique-Id");

			mergeRange++;
			/*
			 * submission date
			 */
			cell = row.createCell(2);
			sheet.setColumnWidth(cell.getColumnIndex(), 4000);
			sheet.setHorizontallyCenter(true);
			cell.setCellStyle(styleForHeading);
			cell.setCellValue("District");
			mergeRange++;

			/*
			 * submission status---on/late submission
			 */
			cell = row.createCell(3);
			sheet.setColumnWidth(cell.getColumnIndex(), 4000);
			sheet.setHorizontallyCenter(true);
			cell.setCellStyle(styleForHeading);
			cell.setCellValue("User ID");
			mergeRange++;

			/*
			 * rejection status-true or false
			 */
			cell = row.createCell(4);
			sheet.setColumnWidth(cell.getColumnIndex(), 4000);
			sheet.setHorizontallyCenter(true);
			cell.setCellStyle(styleForHeading);
			cell.setCellValue("Submission Date");
			mergeRange++;

			/*
			 * Data Submitted For Month Of
			 */
			cell = row.createCell(5);
			sheet.setColumnWidth(cell.getColumnIndex(), 4000);
			sheet.setHorizontallyCenter(true);
			cell.setCellStyle(styleForHeading);
			cell.setCellValue("Data Submitted For Month Of");
			mergeRange++;

			/*
			 * Data Submitted For Month Of
			 */
			cell = row.createCell(6);
			sheet.setColumnWidth(cell.getColumnIndex(), 4000);
			sheet.setHorizontallyCenter(true);
			cell.setCellStyle(styleForHeading);
			cell.setCellValue("Submission Status");
			mergeRange++;

			/*
			 * aggregation status -- true or false
			 */
			cell = row.createCell(7);
			sheet.setColumnWidth(cell.getColumnIndex(), 4000);
			sheet.setHorizontallyCenter(true);
			cell.setCellStyle(styleForHeading);
			cell.setCellValue("Aggregation Status");
			mergeRange++;

			/*
			 * Setting all the questions
			 */
			int columnIndex = 8;
			int brColumnIndex = 1;
			String qstnValue = null;
			for (int i = 0; i < questionList.size(); i++) {

				Question question = questionList.get(i);

				switch (question.getControllerType()) {
				case "heading":
					break;
				case "beginrepeat": {

					String brSheeetName = null;
					brSheeetName = question.getQuestion();
					/*
					 * check the size of brSheeetName is size>=28 than reduce
					 * the sheetName
					 */
					if (brSheeetName.length() >= 28) {
						brSheeetName = brSheeetName.substring(0, 17);
					}
					brColumnIndex = 1;
					sheet = workbook.createSheet(brSheeetName);
					Row row1 = sheet.createRow(0);
					Cell cell1 = row1.createCell(0);
					sheet.setColumnWidth(cell1.getColumnIndex(), 4000);
					sheet.setHorizontallyCenter(true);
					cell1.setCellStyle(styleForHeading);
					cell1.setCellValue("Reference_Id");

					List<Question> qHeaderlist = questionMapBr.get(question.getColumnName());

					for (Question que : qHeaderlist) {

						cell1 = row1.createCell(brColumnIndex);
						cell1.setCellStyle(styleForHeading);
						cell1.setCellValue(que.getQuestion());
						sheet.setColumnWidth(cell1.getColumnIndex(), 4000);

						brColumnIndex++;
						i++;
					}

				}
					break;

				case "tableWithRowWiseArithmetic": {

					sheet = workbook.getSheet(sheetName);
					qstnValue = question.getQuestion();

					List<Question> qHeaderlist = questionMapBr.get(question.getColumnName());

					for (Question que : qHeaderlist) {

						cell = row.createCell(columnIndex);
						cell.setCellStyle(styleForHeading);
						cell.setCellValue(que.getQuestion().replaceAll("@@split@@", ":"));
						sheet.setColumnWidth(cell.getColumnIndex(), 9500);
						columnIndex++;
						i++;
						mergeRange++;

					}

				}
					break;

				case "camera": {
					sheet = workbook.getSheet(sheetName);
					/**
					 * check image count, and make question accordingly
					 */

					String split = question.getConstraints().split(":")[1];

					for (Integer count = 1; count <= Integer.parseInt(split); count++) {

						cell = row.createCell(columnIndex);
						cell.setCellStyle(styleForHeading);
						cell.setCellValue(question.getQuestion().concat("-" + count));
						sheet.setColumnWidth(cell.getColumnIndex(), 4000);
						columnIndex++;
						mergeRange++;
					}

				}
					break;

				default: {

					sheet = workbook.getSheet(sheetName);
					cell = row.createCell(columnIndex);
					cell.setCellStyle(styleForHeading);
					cell.setCellValue(question.getQuestion());
					sheet.setColumnWidth(cell.getColumnIndex(), 4000);
					columnIndex++;
					mergeRange++;
				}

				}

			}
		} catch (Exception e) {

			log.error("error : ", e);
			throw new RuntimeException(e);
		}

		sheet = workbook.getSheet(sheetName);
		/*
		 * heading for raw data
		 */
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellStyle(styleForHeading);
		cell.setCellValue("CPMIS : Submission Report " + "(" + formName + ")");
		sheet = ExcelStyleSheetUtils.doMerge(0, 0, 0, mergeRange - 1, sheet);
		/*
		 * report generation date
		 */
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellStyle(styleForHeading);
		cell.setCellValue("Date of Report Generation : " + new SimpleDateFormat("dd-MM-yyy").format(new Date()));
		sheet = ExcelStyleSheetUtils.doMerge(1, 1, 0, mergeRange - 1, sheet);

		/*
		 * from date and to date
		 */
		row = sheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellStyle(styleForHeading);
		cell.setCellValue("Timeperiod: " + timeperiod);
		sheet = ExcelStyleSheetUtils.doMerge(2, 2, 0, mergeRange - 1, sheet);

		Integer lastSectionMergeLength = 8, lastSubSectionMergeLength = 8, sectionItr = 0, subsectionItr = 0;

		row = sheet.createRow(3);

		for (Map.Entry<String, Integer> entry : sectionMapMerge.entrySet()) {
			cell = row.createCell(lastSectionMergeLength);
			cell.setCellValue(entry.getKey().contains("_") ? entry.getKey().split("_")[1] : entry.getKey());
			cell.setCellStyle(styleForHeading);
			sheet = ExcelStyleSheetUtils.doMerge(3, 3, lastSectionMergeLength,
					entry.getValue() + lastSectionMergeLength - 1, sheet);
			sectionItr++;
			lastSectionMergeLength = entry.getValue() + lastSectionMergeLength;
		}

		row = sheet.createRow(4);

		for (Map.Entry<String, Integer> entry : subSectionMapMerge.entrySet()) {

			cell = row.createCell(lastSubSectionMergeLength);

			cell.setCellValue(entry.getKey().contains("_") ? entry.getKey().split("_")[1] : entry.getKey());
			cell.setCellStyle(styleForHeading);
			if(entry.getValue()!=1){
				sheet = ExcelStyleSheetUtils.doMerge(4, 4, lastSubSectionMergeLength,
						entry.getValue() + lastSubSectionMergeLength - 1, sheet);
			}
			subsectionItr++;
			lastSubSectionMergeLength = entry.getValue() + lastSubSectionMergeLength;
		}

		return workbook;
	}

}
