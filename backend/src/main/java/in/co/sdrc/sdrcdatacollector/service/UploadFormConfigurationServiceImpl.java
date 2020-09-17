package in.co.sdrc.sdrcdatacollector.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.sdrc.cpmisweb.model.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.Type;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.TypeRepository;
import in.co.sdrc.sdrcdatacollector.util.Status;

@Service
public class UploadFormConfigurationServiceImpl implements UploadFormConfigurationService {

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private TypeDetailRepository typeDetailsRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private EngineFormRepository formRepository;


	/**
	 * @author Azhar
	 */
	@Override
	@Transactional
	public ResponseEntity<String> importQuestionData() {
		
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("templates/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {
			System.out.println(files[f].toString());
		}
		List<Question> questionList = new ArrayList<>();
		Map<String, Question> qmap = new LinkedHashMap<>();
		for (int f = 0; f < files.length; f++) {
	
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(files[f]);

				for (int sheet = 0; sheet < workbook.getNumberOfSheets(); sheet++) {
					int sectionCount = 1;
					int subsectionCount =1;
					int questionCount = 0;
					int questionAfterHeadingCount=0;
					
					String sectionName="",subsectionName="";
					XSSFSheet questionSheet = workbook.getSheetAt(sheet);
					if(questionSheet.getSheetName().equals("choices") || questionSheet.getSheetName().equals("translations") || questionSheet.getSheetName().equals("choices-translations")) {
						continue;
					}
					for (int row = 1; row <= questionSheet.getLastRowNum(); row++) {// row
																					// loop
						if (questionSheet.getRow(row) == null)
							break;

						XSSFRow xssfRow = questionSheet.getRow(row);
						int formId = 0; // initialize with outer loop when looping
										// multiple forms
										//
						for (int cols = 0; cols < 19; cols++) {// column loop
							Cell cell = xssfRow.getCell(cols);
							switch (cols) {
							case 0:
								if (cell == null)
									break;
								if (cell != null && (CellType.STRING == cell.getCellTypeEnum())) {
									formId = Integer.valueOf(cell.getStringCellValue());
								} else if (cell != null && (CellType.NUMERIC == cell.getCellTypeEnum())) {
									formId = (int) cell.getNumericCellValue();
								}

								break;
							case 10:
								if (cell != null && (CellType.STRING == cell.getCellTypeEnum())) {
									String typeName = cell.getStringCellValue();
									// System.out.println(typeName);
									if (typeName != null && !typeName.trim().isEmpty()) {

										Type type = typeRepository
												.findByTypeNameAndFormId(StringUtils.trimWhitespace(typeName), formId);
										if (type == null) {
											type = new Type();
											type.setTypeName(StringUtils.trimWhitespace(typeName));
											type.setDescription(StringUtils.trimWhitespace(typeName));
											type.setSlugId(typeRepository.findAll().size() + 1);
											type.setFormId(formId);
											type = typeRepository.save(type);
										}

									}
								}

								break;
							case 11:
								// read type details
								if (cell != null && (CellType.STRING == cell.getCellTypeEnum())) {

									String typeDetails = StringUtils.trimWhitespace(cell.getStringCellValue());
									int order = 0;
									if (typeDetails != null && !typeDetails.trim().isEmpty()
											&& !typeDetails.contains("@@PARENT@@")) {

										String typeDetailNames[] = (typeDetails.split(":")[1].split("@AND@"));
										Type type = typeRepository.findByTypeNameAndFormId(
												StringUtils.trimWhitespace(typeDetails.split(":")[0]), formId);
										if (type == null) {
											throw new IllegalArgumentException("Type not found for typeDetail.Invalid typeName provided : ("+ StringUtils.trimWhitespace(typeDetails.split(":")[0])+") for QNAME :"+ xssfRow.getCell(6).getStringCellValue());
										}
										for (String typeDetailName : typeDetailNames) {
											TypeDetail typeDetail = typeDetailsRepository.findByTypeAndNameAndFormId(
													type,
													StringUtils.trimWhitespace(typeDetailName).split("@@")[0].trim(),
													formId);
											if (typeDetail == null) {
												typeDetail = new TypeDetail();
												typeDetail.setName(StringUtils.trimWhitespace(typeDetailName));
												typeDetail.setType(type);
												typeDetail.setSlugId(typeDetailsRepository.findAll().size() + 1);
												typeDetail.setOrderLevel(order++);
												typeDetail.setFormId(formId);
												if (typeDetail.getName().contains("@@score")) {
													typeDetail.setName(
															StringUtils.trimWhitespace(typeDetailName).split("@@")[0]
																	.trim());
													String score = typeDetailName.split("=")[1];
													typeDetail.setScore(score);
												}
												typeDetailsRepository.save(typeDetail);
												// Facility Level : L1 @@PARENT@@=Facility Type:[Non 24x7 PHC @AND@ SC
												// @AND@ Non-FRU CHC @AND@ 24x7 PHC]
												// @AND@ L2 @@PARENT@@=Facility Type:[24x7 PHC @AND@ SDH @AND@ AH] @AND@
												// L3 @@PARENT@@=Facility Type:[MC @AND@ FRU CHC @AND@ AH]
											}

										}
									} else if (typeDetails != null && !typeDetails.trim().isEmpty()
											&& typeDetails.contains("@@PARENT@@")) {// code in case of parent
										String parentExpression = StringUtils.trimWhitespace(
												typeDetails.substring(typeDetails.indexOf(":"), typeDetails.length()))
												.replaceFirst(":", "");
										String parentExpressions[] = parentExpression.split("]");
										Type type = typeRepository.findByTypeNameAndFormId(
												StringUtils.trimWhitespace(typeDetails.split(":")[0]), formId);
										if (type == null) {
											throw new IllegalArgumentException("Type not found for typeDetail.Invalid typeName provided : ("+ StringUtils.trimWhitespace(typeDetails.split(":")[0])+") for QNAME :"+ xssfRow.getCell(6).getStringCellValue());
										}
										for (String optionExp : parentExpressions) {
											// L1 @@PARENT@@Facility Type:[Non 24x7 PHC @AND@ SC @AND@ Non-FRU CHC @AND@
											// 24x7 PHC]
											String optionLabel = optionExp.split("@@PARENT@@")[0].replaceAll("@AND@",
													"");
											String parentExpArray = optionExp.split("@@PARENT@@")[1];
											String dependentTypeName = StringUtils
													.trimWhitespace(parentExpArray.split(":")[0]);
											String typeDetailNames[] = StringUtils
													.trimWhitespace(parentExpArray.split(":")[1]).replaceAll("\\[", "")
													.split("@AND@");

											String[] trimTypeDetails = Arrays.asList(typeDetailNames).stream()
													.map(t -> t.trim()).collect(Collectors.toList())
													.toArray(typeDetailNames);

											Type dependentType = typeRepository
													.findByTypeNameAndFormId(dependentTypeName, formId);

											if (dependentType == null) {
												throw new IllegalArgumentException("Type not found for typeDetail.Invalid typeName provided : ("+ StringUtils.trimWhitespace(typeDetails.split(":")[0])+") for QNAME :"+ xssfRow.getCell(6).getStringCellValue());
											}

											List<Integer> dependentTypeDetailIds = typeDetailsRepository
													.findByTypeAndFormIdAndNameIn(dependentType, formId,
															trimTypeDetails)
													.stream().map(result -> result.getSlugId())
													.collect(Collectors.toList());

											if (dependentTypeDetailIds.size() != trimTypeDetails.length) {
												throw new IllegalArgumentException(
														"Type detail names does not match the provided typedetails"
																+ trimTypeDetails);
											}

											TypeDetail typeDetail = typeDetailsRepository.findByTypeAndNameAndFormId(
													type, StringUtils.trimWhitespace(optionLabel).split("@@")[0].trim(),
													formId);
											if (typeDetail == null) {
												typeDetail = new TypeDetail();
												typeDetail.setName(StringUtils.trimWhitespace(optionLabel));
												typeDetail.setType(type);
												typeDetail.setSlugId(typeDetailsRepository.findAll().size() + 1);
												typeDetail.setOrderLevel(order++);
												typeDetail.setFormId(formId);

												typeDetail.setParentIds(null);
												if (typeDetail.getName().contains("@@score")) {
													typeDetail.setName(
															StringUtils.trimWhitespace(optionLabel).split("@@")[0]
																	.trim());
													String score = optionLabel.split("=")[1];
													typeDetail.setScore(score);
												}
												typeDetailsRepository.save(typeDetail);
											}

										}
									}
								}

								break;
							}

						}
					}
					System.out.println("Rows Count::" + questionSheet.getLastRowNum());
					Question question = null;
					for (int row = 1; row <= questionSheet.getLastRowNum(); row++) {// row
																					// loop
						if (questionSheet.getRow(row) == null)
							break;

						question = new Question();

						XSSFRow xssfRow = questionSheet.getRow(row);

						for (int cols = 0; cols < 29; cols++) {// column loop

							Cell cell = xssfRow.getCell(cols);
							if (cols == 0 && (cell == null))
								break;
							// if (cell == null || (cell.getCellTypeEnum() == CellType.STRING &&
							// (cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty()) ?
							// true : false) || cell.getCellTypeEnum() == CellType.NUMERIC ? true:false)
							// break ;

							switch (cols) {

							case 0:
								
								question.setSlugId(questionList.size() + 1);
								question.setFormId((int) cell.getNumericCellValue());
								break;
							case 1:
								System.out.println("question No ::" + questionList.size());
								question.setQuestionId(questionList.size() + 1);
								question.setQuestionOrder(questionList.size() + 1);
								break;
							case 2:
								question.setSection(cell.getStringCellValue());
								if(sectionName.equals("")) {
									sectionName = cell.getStringCellValue();
								}else if(!sectionName.equals(cell.getStringCellValue())) {
									sectionName = cell.getStringCellValue();
									sectionCount ++;
									subsectionCount =1;
									subsectionName="";
								}
								break;
							case 3:
								question.setSubsection(cell.getStringCellValue());
								if(subsectionName.equals("")) {
									subsectionName = cell.getStringCellValue();
									questionCount = 0;
								}else if(!subsectionName.equals(cell.getStringCellValue())) {
									subsectionName = cell.getStringCellValue();
									subsectionCount ++;
									questionCount = 0;
								}
								break;
							case 4:
								System.out.println("sheet Name::" + questionSheet.getSheetName() + "::"
										+ cell.getRowIndex() + "::" + cell.getCellType());
								question.setRepeatSubSection((String) cell.getStringCellValue());
								break;
							case 5:
								
								question.setQuestion(cell.getStringCellValue());
								break;
							case 6:
								question.setColumnName(cell.getStringCellValue());
								break;
							case 7:
								question.setControllerType(cell.getStringCellValue().equals("dropdown-score")
										|| cell.getStringCellValue().equals("segment-score") ? "dropdown"
												: cell.getStringCellValue());
								question.setDisplayScore(
										cell.getStringCellValue().equals("dropdown-score") ? true : false);
								break;
							case 8:
								// question.setDependecy(cell.getBooleanCellValue());
								question.setFieldType(cell.getStringCellValue());
								break;
							case 9:
								question.setParentColumnName(cell != null ? cell.getStringCellValue().trim() : "");
								break;

							case 10:
								question.setTypeId(null);
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									if (cell.getStringCellValue() != null) {
										Type type = typeRepository.findByTypeNameAndFormId(
												cell.getStringCellValue().trim(), question.getFormId());
										question.setTypeId(type);
									}
								}
								break;

							case 11:

								break;

							case 12:
								// relevance
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									String expression = cell.getStringCellValue();
									System.out.println("Original Expression:::" + expression);
									String expressionTransformed = "";
									for (String str : expression.split("}")) {
										// System.out.println("Outer Expression:::"
										// + str);
										String[] expressions = str.split(":");
										for (int i = 0; i < expressions.length; i++) {

											String exp = StringUtils.trimWhitespace(expressions[i]);
											// System.out.println("Inner
											// Expression:::" + exp);
											switch (exp) {
											case ")":
												expressionTransformed = expressionTransformed + exp;
												break;
											case "(":
												expressionTransformed = expressionTransformed + exp;
												break;
											case "@AND":
												expressionTransformed = expressionTransformed + ":" + exp + ":";
												break;
											case "@OR":
												expressionTransformed = expressionTransformed + ":" + exp + ":";
												break;
											case "{":
												expressionTransformed = expressionTransformed + exp;
												break;
											case "}":
												expressionTransformed = expressionTransformed + exp + exp;
												break;
											case "":
												break;
											default:
												expressionTransformed = expressionTransformed + exp + ":";
												break;
											case "optionEquals": {
												String typeName = (StringUtils.trimWhitespace(expressions[i + 1]));
												String typeDetailsName = (StringUtils
														.trimWhitespace(expressions[i + 2]));
												if (typeDetailsName == "") {
													throw new NullPointerException(
															"TypeDetail Name cannot be empty for string :"
																	+ expressions);
												}
												TypeDetail td = typeDetailsRepository.findByTypeAndNameAndFormId(
														typeRepository.findByTypeNameAndFormId(
																StringUtils.trimWhitespace(typeName),
																question.getFormId()),
														StringUtils.trimWhitespace(typeDetailsName),
														question.getFormId());
												if (td == null) {
													throw new NullPointerException(
															"No TypeDetail object found with typeDetailsName :"
																	+ typeDetailsName + " , and type name:" + typeName
																	+ ", and formId:" + question.getFormId());
												}
												expressionTransformed = expressionTransformed + exp + ":"
														+ td.getSlugId();
												i = i + 2;
											}
												break;
											case "optionEqualsMultiple": {

												String typeName = (StringUtils.trimWhitespace(expressions[i + 1]));
												String typeDetailsName = StringUtils.trimWhitespace(expressions[i + 2]);
												if (typeDetailsName == "") {
													throw new NullPointerException(
															"TypeDetail Name cannot be empty for string :"
																	+ expressions);
												}
												String details = "";
												for (String tdName : typeDetailsName.split(",")) {
													TypeDetail td = typeDetailsRepository.findByTypeAndNameAndFormId(
															typeRepository.findByTypeNameAndFormId(
																	StringUtils.trimWhitespace(typeName),
																	question.getFormId()),
															StringUtils.trimWhitespace(tdName), question.getFormId());
													if (td == null) {
														throw new NullPointerException(
																"No TypeDetail object found with name :"
																		+ typeDetailsName);
													}
													details = details + td.getSlugId() + ",";

												}
												details = details.substring(0, details.length() - 1);
												expressionTransformed = expressionTransformed + exp + ":" + details;
												i = i + 2;
											}

												break;
											case "textEquals":
											case "equals":
											case "greaterThan":
											case "greaterThanEquals":
											case "lessThan":
											case "lessThanEquals":

											{
												String num = (StringUtils.trimWhitespace(expressions[i + 1]));
												if (num == "") {
													throw new NullPointerException(
															"Number cannot be empty for string :" + expressions);
												}

												expressionTransformed = expressionTransformed + exp + ":" + num;
												// System.out.println("Replaced:::"
												// + expressionTransformed);
												i = i + 1;
											}
												break;

											}
										}

										expressionTransformed = expressionTransformed + "}";
									}
									expressionTransformed = expressionTransformed
											.lastIndexOf("}") == expressionTransformed.length() - 1
													? expressionTransformed.substring(0,
															expressionTransformed.length() - 1)
													: expressionTransformed;
									System.out.println("Final Transformed Expression:::" + expressionTransformed);

									// System.out.println(expressionTransformed);
									question.setRelevance(expressionTransformed);

								}
								break;

							case 13:
								// constraints
								//
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									question.setConstraints(StringUtils.trimWhitespace(cell.getStringCellValue()));
								}
								break;
							case 14:
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									String featureName = cell.getStringCellValue();
									if (featureName != null && featureName.contains("fetch_tables")) {
										for (String feature : featureName.split("@AND")) {
											switch (feature.split(":")[0]) {
											case "fetch_tables": {
												question.setTableName(feature.split(":")[1]);
											}
												break;

											}
										}
									}
									question.setFeatures(StringUtils.trimWhitespace(cell.getStringCellValue()));
								}
								break;

							case 15:
								// default settings

								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									String[] settings = cell.getStringCellValue().split(",");
									String defaultSettings = "";
									for (String setting : settings) {
										switch (setting.split(":")[0]) {
										case "prefetchDropdownWithValue":
											String typeName = StringUtils.trimWhitespace(setting.split(":")[1]);
											String typeDetailsName = StringUtils.trimWhitespace(setting.split(":")[2]);
											TypeDetail td = typeDetailsRepository.findByTypeAndNameAndFormId(
													typeRepository.findByTypeNameAndFormId(
															StringUtils.trimWhitespace(typeName), question.getFormId()),
													StringUtils.trimWhitespace(typeDetailsName), question.getFormId());
											defaultSettings = defaultSettings + ("prefetchDropdownWithValue:")
													+ (td.getSlugId()) + ",";
											break;
										case "disabled":
											defaultSettings = defaultSettings + "disabled,";
											break;
										case "prefetchText":
											defaultSettings = defaultSettings + "prefetchText:"
													+ StringUtils.trimWhitespace(setting.split(":")[1]+",");
											break;

										case "prefetchDate":
											defaultSettings = defaultSettings + "prefetchDate:"
													+ StringUtils.trimWhitespace(setting.split(":")[1]);
											;
											break;
										case "prefetchTime":
											defaultSettings = defaultSettings + "prefetchTime:"
													+ StringUtils.trimWhitespace(setting.split(":")[1]);
											break;

										default:
											defaultSettings = defaultSettings + setting + ",";
											break;
										}

									}
									defaultSettings = defaultSettings.length() > 0
											&& defaultSettings.trim().charAt(defaultSettings.length() - 1) == ','
													? defaultSettings.substring(0, defaultSettings.length() - 1)
													: defaultSettings;
									question.setDefaultSettings(defaultSettings);
								}
								break;

							case 16:
								// finalize mandatory
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									question.setFinalizeMandatory(StringUtils.trimWhitespace(cell.getStringCellValue())
											.toLowerCase().equals("yes") ? true : false);
								}
								break;

							case 17:
								// save manatory
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									question.setSaveMandatory(StringUtils.trimWhitespace(cell.getStringCellValue())
											.toLowerCase().equals("yes") ? true : false);
								}
								break;
							case 18:
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									question.setQuery(StringUtils.trimWhitespace(cell.getStringCellValue()));
								}

								break;
							case 19:
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									question.setFileExtensions(StringUtils.trimWhitespace(cell.getStringCellValue()));
								}
								break;
							case 20:
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
									question.setReviewHeader(StringUtils.trimWhitespace(cell.getStringCellValue()));
								}
								break;
							case 21: // score expression
								if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {

									String expressionArray[] = cell.getStringCellValue().split("\\+");
									String modifiedExp = "";

									for (String expression : expressionArray) {
										if (expression.contains("selected")) { // set typedetail id in case of dropdown
																				// only

											for (int i = 0; i < expression.split("").length; i++) {

												char ch = expression.charAt(i);

												if (ch == '}') {
													String typeTypeDetails = "";
													for (int j = i + 2; j < expression.split("").length; j++) {

														char chNext = expression.charAt(j);
														if (chNext == ')') {
															break;
														}

														typeTypeDetails = typeTypeDetails + chNext;
													}

													String typeName = typeTypeDetails.split(":")[0];
													String typeDetailsName = typeTypeDetails.split(":")[1];

													TypeDetail td = typeDetailsRepository.findByTypeAndNameAndFormId(
															typeRepository.findByTypeNameAndFormId(
																	StringUtils.trimWhitespace(typeName),
																	question.getFormId()),
															StringUtils.trimWhitespace(typeDetailsName),
															question.getFormId());

													String newExp = expression.replace(typeTypeDetails,
															td.getSlugId().toString());
													modifiedExp = modifiedExp + newExp + " + ";
												}
											}

										} else {
											modifiedExp = modifiedExp + expression + " + ";

										}
									}

									modifiedExp = modifiedExp.substring(0, modifiedExp.length() - 3);
									question.setScoreExp(modifiedExp);

								}
								break;
							case 22:
								if(cell!=null && cell.getCellTypeEnum() == CellType.STRING) {
									question.setCmsg(cell.getStringCellValue());
								}
								break;
							case 23:
								//column table name
								if(cell!=null && cell.getCellTypeEnum() == CellType.STRING) {
									question.setColumnTableName(cell.getStringCellValue());
								}
								break;
							
							}
						}
						System.out.println(question);
						if (question.getQuestionOrder() != null) {
					
							questionList.add(question);
							qmap.put(question.getColumnName(), question);
						}
						
						
					}
					questionRepository.save(questionList);
				}

			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				try {
					workbook.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}

		}

		return new ResponseEntity<>("successfull", HttpStatus.OK);
	}

	/**
	 * @author Subham Ashish(subham@sdrc.co.in)
	 */
	@Override
	public Boolean configureRoleFormMapping() {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("templates/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;

			try {
				workbook = new XSSFWorkbook(files[f]);
				XSSFSheet sheet = workbook.getSheetAt(1);

				for (int row = 1; row <= sheet.getLastRowNum(); row++) {// row
																		// loop

					if (sheet.getRow(row) == null)
						break;
					// System.out.println("Rows::" + row);

					XSSFRow xssfRow = sheet.getRow(row);
					String formName = null;
					String roleCode = null;
					Integer roleId = null;
					Integer formId = null;
					String active = null;

					for (int cols = 0; cols < 4; cols++) {
						// column loop
						Cell cell = xssfRow.getCell(cols);

						switch (cols) {

						case 0:// form
							if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {

								// "1:Community Facilitator Input Form"

								formId = Integer
										.valueOf(StringUtils.trimWhitespace(cell.getStringCellValue().split(":")[0]));
								formName = StringUtils.trimWhitespace(cell.getStringCellValue().split(":")[1]);
								active = StringUtils.trimWhitespace(cell.getStringCellValue().split(":")[2]);

								/**
								 * Check the status while uploading form: if found active- check whether the
								 * same form is present, if not fount insert. if found inactive - check whether
								 * the form exist, if exist set its status to inactive and update in db
								 */
								if (active != null) {

									if (active.equals("active")) {

										EnginesForm engineForm = formRepository.findByNameAndFormIdAndStatus(
												StringUtils.trimWhitespace(cell.getStringCellValue().split(":")[1]),
												Integer.valueOf(StringUtils
														.trimWhitespace(cell.getStringCellValue().split(":")[0])),
												Status.ACTIVE);

										if (engineForm == null) {

											EnginesForm form = new EnginesForm();
											form.setFormId(formId);
											form.setName(formName);
											formRepository.save(form);

										}

									} else if (active.equals("inactive")) {

										EnginesForm engineForm = formRepository.findByNameAndFormId(
												StringUtils.trimWhitespace(cell.getStringCellValue().split(":")[1]),
												Integer.valueOf(StringUtils
														.trimWhitespace(cell.getStringCellValue().split(":")[0])));

										if (engineForm != null) {
											engineForm.setStatus(Status.INACTIVE);
											formRepository.save(engineForm);
										}

									} else {
										throw new RuntimeException("Invalid active value while uploading FORM-NAME");
									}
								} else {
									throw new RuntimeException(
											"active or inactive status is not found while uploading sheet");
								}

							}
							break;

						case 1:// role-DATAENTRY
//							if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
//
//								// "'FACILITATOR:002:2','ROLENAME:ROLECODE:ROLEID'"
//
//								List<String> roleDataEntry = Arrays.asList((cell.getStringCellValue().split(",")));
//								for (String rdt : roleDataEntry) {
//
//									// insert in role document
//									roleCode = StringUtils.trimWhitespace(rdt.split(":")[1]);
//									roleId = Integer.valueOf(StringUtils.trimWhitespace(rdt.split(":")[2]));
//									active = StringUtils.trimWhitespace(rdt.split(":")[3]);
//
//									if (roleRepository
//											.findByRoleCode(StringUtils.trimWhitespace(rdt.split(":")[1])) == null) {
//
//										EngineRole role = new EngineRole();
//										role.setRoleId(roleId);
//										role.setRoleCode(roleCode);
//										role.setRoleName(StringUtils.trimWhitespace(rdt.split(":")[0]));
//										roleRepository.save(role);
//									}
//
//									EnginesRoleFormMapping enginesRoleFormMapping = null;
//
//									/**
//									 * Check the status while uploading form: if found active- check whether the
//									 * same form is present, if not fount insert. if found inactive - check whether
//									 * the form exist, if exist set its status to inactive and update in db
//									 */
//									if (active != null) {
//										if (active.equals("active")) {
//											enginesRoleFormMapping = roleFormMappingRepository
//													.findByRoleRoleIdAndFormFormIdAndAccessTypeAndStatus(roleId, formId,
//															AccessType.DATA_ENTRY, Status.ACTIVE);
//
//											if (enginesRoleFormMapping == null) {
//												EnginesRoleFormMapping rfMapping = new EnginesRoleFormMapping();
//												rfMapping.setForm(formRepository.findByName(formName));
//												rfMapping.setRole(roleRepository.findByRoleCode(roleCode));
//												rfMapping.setAccessType(AccessType.DATA_ENTRY);
//												// rfMapping.setRoleFormMappingId(
//												// roleFormMappingRepository.findAll().size() + 1);
//												roleFormMappingRepository.save(rfMapping);
//											}
//
//										} else if (active.equals("inactive")) {
//											enginesRoleFormMapping = roleFormMappingRepository
//													.findByRoleRoleIdAndFormFormIdAndAccessType(roleId, formId,
//															AccessType.DATA_ENTRY);
//											if (enginesRoleFormMapping != null) {
//												enginesRoleFormMapping.setStatus(Status.INACTIVE);
//												roleFormMappingRepository.save(enginesRoleFormMapping);
//											}
//										} else {
//											throw new RuntimeException(
//													"Invalid active value while uploading ENGINEROLE(ROLENAME:ROLECODE:ROLEID)-DATAENTRY");
//										}
//									} else {
//
//										throw new RuntimeException(
//												"active or inactive status is not found while uploading sheet");
//									}
//
//								}
//
//							}
							break;

						case 2:// role-REVIEW

//							if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
//
//								// "'FACILITATOR:002:2','ROLENAME:ROLECODE:ROLEID'"
//
//								List<String> roleDataEntry = Arrays.asList((cell.getStringCellValue().split(",")));
//								for (String rdt : roleDataEntry) {
//
//									// insert in role document
//									roleCode = StringUtils.trimWhitespace(rdt.split(":")[1]);
//									roleId = Integer.valueOf(StringUtils.trimWhitespace(rdt.split(":")[2]));
//									active = StringUtils.trimWhitespace(rdt.split(":")[3]);
//
//									if (roleRepository
//											.findByRoleCode(StringUtils.trimWhitespace(rdt.split(":")[1])) == null) {
//
//										EngineRole role = new EngineRole();
//										role.setRoleId(roleId);
//										role.setRoleCode(roleCode);
//										role.setRoleName(StringUtils.trimWhitespace(rdt.split(":")[0]));
//										roleRepository.save(role);
//									}
//
//									EnginesRoleFormMapping enginesRoleFormMapping = null;
//
//									/**
//									 * Check the status while uploading form: if found active- check whether the
//									 * same form is present, if not fount insert. if found inactive - check whether
//									 * the form exist, if exist set its status to inactive and update in db
//									 */
//									if (active != null) {
//										if (active.equals("active")) {
//											enginesRoleFormMapping = roleFormMappingRepository
//													.findByRoleRoleIdAndFormFormIdAndAccessTypeAndStatus(roleId, formId,
//															AccessType.REVIEW, Status.ACTIVE);
//
//											if (enginesRoleFormMapping == null) {
//												EnginesRoleFormMapping rfMapping = new EnginesRoleFormMapping();
//												rfMapping.setForm(formRepository.findByName(formName));
//												rfMapping.setRole(roleRepository.findByRoleCode(roleCode));
//												rfMapping.setAccessType(AccessType.REVIEW);
//												// rfMapping.setRoleFormMappingId(
//												// roleFormMappingRepository.findAll().size() + 1);
//												roleFormMappingRepository.save(rfMapping);
//											}
//
//										} else if (active.equals("inactive")) {
//											enginesRoleFormMapping = roleFormMappingRepository
//													.findByRoleRoleIdAndFormFormIdAndAccessType(roleId, formId,
//															AccessType.REVIEW);
//											if (enginesRoleFormMapping != null) {
//												enginesRoleFormMapping.setStatus(Status.INACTIVE);
//												roleFormMappingRepository.save(enginesRoleFormMapping);
//											}
//										} else {
//											throw new RuntimeException(
//													"Invalid active value while uploading ENGINEROLE(ROLENAME:ROLECODE:ROLEID)-DATAENTRY");
//										}
//									} else {
//
//										throw new RuntimeException(
//												"active or inactive status is not found while uploading sheet");
//									}
//
//								}
//
//							}
							break;
						case 3:// role-RAWDATA-REPORT

//							if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
//
//								// "'FACILITATOR:002:2','ROLENAME:ROLECODE:ROLEID'"
//
//								List<String> roleDataEntry = Arrays.asList((cell.getStringCellValue().split(",")));
//								for (String rdt : roleDataEntry) {
//
//									// insert in role document
//									roleCode = StringUtils.trimWhitespace(rdt.split(":")[1]);
//									roleId = Integer.valueOf(StringUtils.trimWhitespace(rdt.split(":")[2]));
//									active = StringUtils.trimWhitespace(rdt.split(":")[3]);
//
//									if (roleRepository
//											.findByRoleCode(StringUtils.trimWhitespace(rdt.split(":")[1])) == null) {
//
//										EngineRole role = new EngineRole();
//										role.setRoleId(roleId);
//										role.setRoleCode(roleCode);
//										role.setRoleName(StringUtils.trimWhitespace(rdt.split(":")[0]));
//										roleRepository.save(role);
//									}
//
//									EnginesRoleFormMapping enginesRoleFormMapping = null;
//
//									/**
//									 * Check the status while uploading form: if found active- check whether the
//									 * same form is present, if not fount insert. if found inactive - check whether
//									 * the form exist, if exist set its status to inactive and update in db
//									 */
//									if (active != null) {
//										if (active.equals("active")) {
//											enginesRoleFormMapping = roleFormMappingRepository
//													.findByRoleRoleIdAndFormFormIdAndAccessTypeAndStatus(roleId, formId,
//															AccessType.DOWNLOAD_RAW_DATA, Status.ACTIVE);
//
//											if (enginesRoleFormMapping == null) {
//												EnginesRoleFormMapping rfMapping = new EnginesRoleFormMapping();
//												rfMapping.setForm(formRepository.findByName(formName));
//												rfMapping.setRole(roleRepository.findByRoleCode(roleCode));
//												rfMapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
//												// rfMapping.setRoleFormMappingId(
//												// roleFormMappingRepository.findAll().size() + 1);
//												roleFormMappingRepository.save(rfMapping);
//											}
//
//										} else if (active.equals("inactive")) {
//											enginesRoleFormMapping = roleFormMappingRepository
//													.findByRoleRoleIdAndFormFormIdAndAccessType(roleId, formId,
//															AccessType.DOWNLOAD_RAW_DATA);
//											if (enginesRoleFormMapping != null) {
//												enginesRoleFormMapping.setStatus(Status.INACTIVE);
//												roleFormMappingRepository.save(enginesRoleFormMapping);
//											}
//										} else {
//											throw new RuntimeException(
//													"Invalid active value while uploading ENGINEROLE(ROLENAME:ROLECODE:ROLEID)-DATAENTRY");
//										}
//									} else {
//
//										throw new RuntimeException(
//												"active or inactive status is not found while uploading sheet");
//									}
//
//								}
//
//							}
							break;

						}
					}

					workbook.close();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
		return true;
	}

	@Transactional
	@Override
	public Boolean importLanguages() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("templates/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();
		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}
		for (int f = 0; f < files.length; f++) {
			System.out.println(files[f].toString());
		}

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(files[f]);
				XSSFSheet questionSheet = workbook.getSheet("translations");

				for (int row = 1; row <= questionSheet.getLastRowNum(); row++) {// row
																				// loop
					if (questionSheet.getRow(row) == null)
						break;

					XSSFRow xssfRow = questionSheet.getRow(row);

					String qName = "";
					Integer formId = 0;
					Question q = null;
					for (int cols = 0; cols < 29; cols++) {// column loop

						Cell cell = xssfRow.getCell(cols);

						switch (cols) {
						case 0:// qName
							formId = (int) cell.getNumericCellValue();
							break;
						case 1:// qName
							qName = cell.getStringCellValue();
							q = questionRepository.findAllByFormIdAndColumnName(formId,cell.getStringCellValue());
							if(q==null) {
								throw new NullPointerException("no question with key is available with:"+cell.getStringCellValue()+", form Id:"+formId);
							}
							break;
						case 2:// english british
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = q.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.EN_UK.toString(), cell.getStringCellValue());
								q.setLanguages(o);
							}

							break;
						case 3:// hindi
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = q.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.HINDI.toString(), cell.getStringCellValue());
								q.setLanguages(o);

							}
							break;
						case 4:// oriya
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = q.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.ORIYA.toString(), cell.getStringCellValue());
								q.setLanguages(o);

							}
							break;
						case 5:// tamil
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = q.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.TAMIL.toString(), cell.getStringCellValue());
								q.setLanguages(o);

							}
							break;
						case 6:// spanish
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = q.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.SPANISH.toString(), cell.getStringCellValue());
								q.setLanguages(o);

							}
							break;
						case 7:// english american
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = q.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.EN_US.toString(), cell.getStringCellValue());
								q.setLanguages(o);

							}
							break;
						}
					}
					questionRepository.save(q);
				}

				
			} catch (Exception e) {
				if (workbook != null) {
					try {

						workbook.close();
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				}

				throw new RuntimeException(e);
			}
		}
		return true;
	}

	public void importChoices() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("templates/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();
		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}
		for (int f = 0; f < files.length; f++) {
			System.out.println(files[f].toString());
		}

		for (int f = 0; f < files.length; f++) {
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(files[f]);
				XSSFSheet questionSheet = workbook.getSheet("choice-translations");

				for (int row = 1; row <= questionSheet.getLastRowNum(); row++) {// row
																				// loop
					if (questionSheet.getRow(row) == null)
						break;

					XSSFRow xssfRow = questionSheet.getRow(row);

					String typeDetailName = "";
					Integer formId = 0;
					Type type = null;
					TypeDetail typeDetail = null;
					for (int cols = 0; cols < 29; cols++) {// column loop

						Cell cell = xssfRow.getCell(cols);

						switch (cols) {
						case 0:
							formId = (int) cell.getNumericCellValue();
							break;
						case 1:
							type = typeRepository.findByTypeNameAndFormId(cell.getStringCellValue(), formId);
							if (type == null) {
								throw new NullPointerException("No Type object found with typeName "
										+ cell.getStringCellValue() + ", and formId:" + formId);
							}
							break;
						case 2:
							typeDetailName = cell.getStringCellValue();
							typeDetail = typeDetailsRepository.findByTypeAndNameAndFormId(type, typeDetailName, formId);
							break;
						case 3:// english british
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = typeDetail.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.EN_UK.toString(), cell.getStringCellValue());
							}

							break;
						case 4:// hindi
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = typeDetail.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.HINDI.toString(), cell.getStringCellValue());
							}
							break;
						case 5:// oriya
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = typeDetail.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.ORIYA.toString(), cell.getStringCellValue());
							}
							break;
						case 6:// tamil
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = typeDetail.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.TAMIL.toString(), cell.getStringCellValue());
							}
							break;
						case 7:// spanish
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = typeDetail.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.SPANISH.toString(), cell.getStringCellValue());
								
							}
							break;
						case 8:// english american
							if (cell != null && cell.getStringCellValue() != null) {
								JsonNode node = typeDetail.getLanguages();
								ObjectNode o = null;
								if (node != null) {
									o = (ObjectNode) node;
								} else {
									o = JsonNodeFactory.instance.objectNode();
								}
								o.put(Language.EN_US.toString(), cell.getStringCellValue());
							}
							break;
						}
					}
					typeDetailsRepository.save(typeDetail);
				}
			} catch (Exception e) {
				if (workbook != null) {
					try {

						workbook.close();
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				}

				throw new RuntimeException(e);
			}
		}
	}
}
