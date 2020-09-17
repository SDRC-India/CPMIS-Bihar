package org.sdrc.cpmisweb.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sdrc.cpmisweb.domain.AccountDetails;
import org.sdrc.cpmisweb.domain.FacilitySubmission;
import org.sdrc.cpmisweb.domain.InstitutionUserMapping;
import org.sdrc.cpmisweb.domain.Submission;
import org.sdrc.cpmisweb.domain.Timeperiod;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.model.FormType;
import org.sdrc.cpmisweb.repository.AccountDetailsRepository;
import org.sdrc.cpmisweb.repository.DesignationFormMappingRepository;
import org.sdrc.cpmisweb.repository.FacilitySubmissionRepository;
import org.sdrc.cpmisweb.repository.InstitutionUserMappingRepository;
import org.sdrc.cpmisweb.repository.SubmissionRepository;
import org.sdrc.cpmisweb.repository.TimePeriodRepository;
import org.sdrc.cpmisweb.util.Constant;
import org.sdrc.cpmisweb.util.HeaderFooter;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import in.co.sdrc.sdrcdatacollector.models.ReceiveEventModel;
import in.co.sdrc.sdrcdatacollector.service.FormsService;
import in.co.sdrc.sdrcdatacollector.util.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

	@Autowired
	private Environment environment;
	
	@Autowired
	SubmissionRepository submissionRepository;

	@Autowired
	QuestionRepository questionRepository;

	@Autowired
	TypeDetailRepository typeDetailRepository;

	@Autowired
	FormsService formsService;
	
	@Autowired
	TimePeriodRepository timePeriodRepository;
	
	@Autowired
	private FacilitySubmissionRepository facilitySubmissionRepository;
	
	@Autowired
	private InstitutionUserMappingRepository institutionUserMappingRepository;
	
	@Autowired
	private EngineFormRepository formRepository;
	
	public static String pdfFileDestination;
	
	@Override
	public boolean saveSubmission(ReceiveEventModel receiveEventModel, Integer facilityI, Principal principal) {
		UserModel model = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (receiveEventModel.getFormStatus() == null || !(receiveEventModel.getFormStatus().equals("save")
				|| receiveEventModel.getFormStatus().equals("finalized"))) {
			throw new IllegalArgumentException("Invalid parameter sent in payload for formStatus.");
		}
		Account acc = new Account();
		acc.setId((int) model.getUserId());
		InstitutionUserMapping iumapping=institutionUserMappingRepository.findByUserId(acc);
		Submission submission = submissionRepository.findByTimeperiodTimeperiodIdAndCreatedBy(receiveEventModel.getTimeperiodId(), acc);

		if (submission == null) {
			submission = new Submission();
			submission.setData(receiveEventModel.getSubmissionData());
			submission.setAttachmentCount(receiveEventModel.getAttachmentCount());
			submission.setFormId(receiveEventModel.getFormId());
			submission.setUniqueId(receiveEventModel.getUniqueId());
			submission.setCreatedDate(new Date());
			submission.setUpdatedDate(new Date());
			submission.setSubmissionDate(new Date());
			submission.setFormId(receiveEventModel.getFormId());
			submission.setFacility(iumapping);
			submission.setCreatedBy(acc);
			submission.setFormStatus(receiveEventModel.getFormStatus().equals("save") ? FormStatus.SAVED : FormStatus.FINALIZED);
			submission.setTimeperiod(timePeriodRepository.findByTimeperiodId(receiveEventModel.getTimeperiodId()));
		} else {
			submission.setData(receiveEventModel.getSubmissionData());
			submission.setSubmissionDate(new Date());
			submission.setFormStatus(receiveEventModel.getFormStatus().equals("save") ? FormStatus.SAVED : FormStatus.FINALIZED);
			submission.setUpdatedDate(new Date());
		}

		submissionRepository.save(submission);
		try {
			if(receiveEventModel.getFormStatus().equals("finalized"))
				pdfFileDestination = downloadSubmissionPDF(submission, receiveEventModel);
		} catch (Exception e) {
			log.error("Error during generating pdf after submission.", e);
		}
		return true;
	}
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	DesignationFormMappingRepository designationFormMappingRepository;
	
	@Autowired
	AccountDetailsRepository accountDetailsRepository;

	@Override
	public boolean isDataEntryDoneForCurrentMonth() {
		boolean flag = false;
		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account acc = accountRepository.findById((Integer)user.getUserId());
		AccountDetails ad = accountDetailsRepository.findByAccount(acc);
		Designation d = acc.getAccountDesignationMapping().get(0).getDesignation();
		Timeperiod latestTimePeriod = timePeriodRepository.findTopByPeriodicityOrderByTimeperiodIdDesc(1);
		Integer designationId = (Integer) d.getId();
		Integer assignedFormId = designationFormMappingRepository.findByDesignationIdAndAccessTypeAndFormStatusAndFormFormType
				(designationId, AccessType.DATA_ENTRY, Status.ACTIVE,FormType.DATA_ENTRY_TYPE).isEmpty()?null: 
				designationFormMappingRepository.findByDesignationIdAndAccessTypeAndFormStatusAndFormFormType
				(designationId, AccessType.DATA_ENTRY, Status.ACTIVE,FormType.DATA_ENTRY_TYPE).get(0).getForm().getFormId();
		
		if(ad.getUserTypeId().getUserTypeId()!=Constant.STATE_LEVEL_USER_TYPE_ID){
			if(submissionRepository.findMaxTimePeriodByAccountIdAndFormId(acc.getId(), assignedFormId) == null){//1st login(not saved or submitted)
				flag = false;
			}else{//either saved or submitted
				Timeperiod userMaxTimePeriod = timePeriodRepository.findByTimeperiodId(submissionRepository.findMaxTimePeriodByAccountIdAndFormId(acc.getId(), assignedFormId));
				Submission submission = submissionRepository.findByCreatedByIdAndTimeperiodTimeperiodId(acc.getId(), userMaxTimePeriod.getTimeperiodId());
				
				if(userMaxTimePeriod.getTimeperiodId() < latestTimePeriod.getTimeperiodId()){
					if(submission.getFormStatus().equals(FormStatus.FINALIZED)) flag = false; else flag = true;
					
				}else if(userMaxTimePeriod.getTimeperiodId() == latestTimePeriod.getTimeperiodId()){
					flag = true;
				}
			}
		}	
		return flag;
	}
	
	public String downloadSubmissionPDF(Submission submission, ReceiveEventModel rem) throws Exception {

		List<TypeDetail> typeDetails = typeDetailRepository.findByFormId(rem.getFormId());
		Map<String, String> typeDetailsMap = new HashMap<>();
		typeDetails.forEach(e -> {
			typeDetailsMap.put(e.getSlugId().toString(), e.getName());
		});
		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String timeperiod = submission.getTimeperiod().getTimePeriod();
		//set the pdf directory and name here
		String fileName = user.getUsername()+"_"+timeperiod+"_submission.pdf";
		String dest = Paths.get(environment.getProperty(Constant.SUBMISSION_PDF_OUTPUT_PATH)).toAbsolutePath()+File.separator+fileName;
		
		Document document = new Document();
		document.setMargins(36, 36, 60, 36);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(dest)));
		HeaderFooter headerFooter = new HeaderFooter(environment.getProperty(Constant.DOMAIN_NAME), user, timeperiod);
		writer.setPageEvent(headerFooter);
		document.open();
	    
		Map<String, Object> paramKeyValMap = new HashMap<>();

		EnginesForm form = formRepository.findByFormId(submission.getFormId());
		Map<String, Map<String, List<Map<String, List<QuestionModel>>>>> questionMap = formsService
				.getQuestions(paramKeyValMap, null, user, (Integer) user.getDesignationIds().toArray()[0],form.getFormType().toString());

		Iterator<String> iterator = questionMap.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Map<String, List<Map<String, List<QuestionModel>>>> sectionMap = questionMap.get(key);

			Iterator<String> sectionIterator = sectionMap.keySet().iterator();

			while (sectionIterator.hasNext()) {
				String sectionNameAsKey = sectionIterator.next();
				Paragraph section = new Paragraph(sectionNameAsKey);
				section.setSpacingAfter(0f);
				document.add(section);
				document.add(new LineSeparator(1, 100, Color.GREEN.darker(), 0, -5));
				document.add(new Paragraph("\n"));
				
				List<Map<String, List<QuestionModel>>> subsections = sectionMap.get(sectionNameAsKey);

				for (Map<String, List<QuestionModel>> subsectionMap : subsections) {

					for (Entry<String, List<QuestionModel>> subsection : subsectionMap.entrySet()) {

						for (QuestionModel question : subsection.getValue()) {
							switch (question.getControlType()) {
							case "textbox":
							case "textarea":
							case "MultiDate Widget":
							case "Date Widget": {
								PdfPTable table = new PdfPTable(2);
								table.setWidthPercentage(100);
								table.setSpacingBefore(0f);
								table.setSpacingAfter(0f);

								// Set Column widths
								float[] columnWidths = { 1f, 1f };
								table.setWidths(columnWidths);

								PdfPCell cell1 = new PdfPCell(new Paragraph(question.getLabel()));
								cell1.setBorderColor(Color.BLACK);
								cell1.setPaddingLeft(10);
								cell1.setBorder(Rectangle.NO_BORDER);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								String value = submission.getData().get(question.getColumnName()).toString();
								if(value != null && !value.equals("null") && value.startsWith("\"") && value.endsWith("\""))
									value = value.substring(1, value.length()-1);
								PdfPCell cell2 = new PdfPCell(new Paragraph(value != null && value.equals("null")?"":value));
								cell2.setBorderColor(Color.BLACK);
								cell2.setPaddingLeft(10);
								cell2.setBorder(Rectangle.NO_BORDER);
								cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
								cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

								table.addCell(cell1);
								table.addCell(cell2);

								document.add(table);
							}
								break;
							case "dropdown": {
								String value = "";
								switch (question.getFieldType()) {
								
								case "checkbox":
									String values = submission.getData().get(question.getColumnName()).toString().replace("[","").replace("]","");
									for(String v : values.split(",")){
										value += typeDetailsMap.get(v)+", ";
									}
									value = value.substring(0, value.length()-2);
									break;

								default:
									value = typeDetailsMap.get(submission.getData().get(question.getColumnName()).toString());
									break;
								}
								
								PdfPTable table = new PdfPTable(2);
								table.setWidthPercentage(100);
								table.setSpacingBefore(5f);
								table.setSpacingAfter(0f);

								// Set Column widths
								float[] columnWidths = { 1f, 1f };
								table.setWidths(columnWidths);

								PdfPCell cell1 = new PdfPCell(new Paragraph(question.getLabel()));
								cell1.setBorderColor(Color.BLACK);
								cell1.setPaddingLeft(10);
								cell1.setBorder(Rectangle.NO_BORDER);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
//								submission.getData().get(question.getColumnName()).toString()
								PdfPCell cell2 = new PdfPCell(new Paragraph(value != null && value.equals("null")?"":value));
								cell2.setBorderColor(Color.BLACK);
								cell2.setPaddingLeft(10);
								cell2.setBorder(Rectangle.NO_BORDER);
								cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
								cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

								table.addCell(cell1);
								table.addCell(cell2);

								document.add(table);
							}
								break;
							case "tableWithRowWiseArithmetic":
							case "table": 
							case "heading":{
								if(question.getControlType().equals("heading")){
									document.add(new Paragraph(question.getLabel()));
								}else{
									List<Map<String, Object>> tableModel = (List<Map<String, Object>>) question
											.getTableModel();
									int cols = 0;
									for (Map<String, Object> entry : tableModel) {
										if (cols > 0) {
											break;
										}
										for (Map.Entry<String, Object> e : entry.entrySet()) {
											if (cols > 0) {
												break;
											}
											if (e.getValue() instanceof QuestionModel) {
												cols = entry.entrySet().size();
												break;
											}
										}
									}
	
									PdfPTable table = new PdfPTable(cols);
									table.setWidthPercentage(100);
									table.setSpacingBefore(0f);
									table.setSpacingAfter(5f);
	
									// Set Column widths
									float[] columnWidths = new float[cols];
									for (int i = 0; i < cols; i++) {
										columnWidths[i] = 1f;
									}
									columnWidths[0] = 2f;
									table.setWidths(columnWidths);
									Integer rowIndex = 0;
									
									for (Map<String, Object> map : tableModel) {
										Map<String, Object> ro = map;
										if (rowIndex == 0) {
											ro.forEach((row, columns) -> {
												PdfPCell cell01 = new PdfPCell(new Paragraph(row));
												cell01.setBorderColor(Color.BLACK);
												cell01.setPaddingLeft(2);
												cell01.setPaddingRight(2);
												cell01.setHorizontalAlignment(Element.ALIGN_CENTER);
												cell01.setVerticalAlignment(Element.ALIGN_MIDDLE);
												cell01.setNoWrap(false);
												table.addCell(cell01);
											});
											rowIndex++;
										}
									}
									for (Map<String, Object> map : tableModel) {
										for(Map.Entry<String, Object> rowColumnMap : map.entrySet()){
											if(rowColumnMap.getValue() instanceof String){
												PdfPCell cell01 = new PdfPCell(new Paragraph(rowColumnMap.getValue().toString()));
												cell01.setBorderColor(Color.BLACK);
												cell01.setPaddingLeft(2);
												cell01.setPaddingRight(2);
												cell01.setHorizontalAlignment(Element.ALIGN_CENTER);
												cell01.setVerticalAlignment(Element.ALIGN_MIDDLE);
												cell01.setNoWrap(false);
	
												table.addCell(cell01);
											}else{
												QuestionModel qm = (QuestionModel)rowColumnMap.getValue();
												
												String parentColumnName = qm.getParentColumnName();
												String value = submission.getData().get(parentColumnName).findValue(qm.getColumnName()).toString();
												if(value.startsWith("\"") && value.endsWith("\""))
													value = value.substring(1, value.length()-1);
												if(!value.equals("null") && qm.getControlType().equals("dropdown") && qm.getFieldType().equals("option")){
													value = typeDetailsMap.get(value);
												}
												PdfPCell cell03 = new PdfPCell(new Paragraph(value.equals("null")?"N/A":value));
												cell03.setBorderColor(Color.BLACK);
												cell03.setPaddingLeft(2);
												cell03.setPaddingRight(2);
												cell03.setHorizontalAlignment(Element.ALIGN_CENTER);
												cell03.setVerticalAlignment(Element.ALIGN_MIDDLE);
												cell03.setNoWrap(false);
												table.addCell(cell03);
											}
										}
									
									}
									
									document.add(table);
								}
							}

								break;
							case "beginrepeat":
								List<List<QuestionModel>> qmsList = question.getBeginRepeat();
								
								for(List<QuestionModel> qms : qmsList){
									outer2:for(QuestionModel qm : qms){
										for(int i = 0; i < submission.getData().get(qm.getParentColumnName()).size(); i++){
											Paragraph p = new Paragraph("Training Details "+Integer.toString(i+1));
											if(qm.getParentColumnName().equals("saa_BEGINREPEAT001") || qm.getParentColumnName().equals("saa_BEGINREPEAT002"))
												p = new Paragraph("Child Details "+Integer.toString(i+1));
											section.setSpacingAfter(0f);
											document.add(p);
											
											for(int j=0; j < submission.getData().get(qm.getParentColumnName()).get(i).size(); j++){
												switch (qms.get(j).getControlType()) {
												case "textbox":
												case "textarea":
												case "MultiDate Widget":
												case "Date Widget":{
													PdfPTable table = new PdfPTable(2);
													table.setWidthPercentage(100);
													table.setSpacingBefore(5f);
													table.setSpacingAfter(0f);
	
													// Set Column widths
													float[] columnWidths = { 1f, 1f };
													table.setWidths(columnWidths);
													PdfPCell cell1 = new PdfPCell(new Paragraph(qms.get(j).getLabel()));
													cell1.setBorderColor(Color.BLACK);
													cell1.setPaddingLeft(10);
													cell1.setBorder(Rectangle.NO_BORDER);
													cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
													cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
													
													String value = "";
													if(submission.getData().get(qms.get(j).getParentColumnName()).get(i).has(qms.get(j).getColumnName().split("-")[3]))
														value = submission.getData().get(qms.get(j).getParentColumnName()).get(i).get(qms.get(j).getColumnName().split("-")[3]).toString();
													else
														value = null;
													if(value != null && !value.equals("null") && value.startsWith("\"") && value.endsWith("\""))
														value = value.substring(1, value.length()-1);
													
													PdfPCell cell2 = new PdfPCell(new Paragraph(value != null && value.equals("null")?"":value));
													cell2.setBorderColor(Color.BLACK);
													cell2.setPaddingLeft(10);
													cell2.setBorder(Rectangle.NO_BORDER);
													cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
													cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
													
													table.addCell(cell1);
													table.addCell(cell2);
	
													document.add(table);
												}
													break;
												case "dropdown": {
													String value = "";
													switch (qms.get(j).getFieldType()) {
													
													case "checkbox":
														String values = submission.getData().get(qms.get(j).getParentColumnName()).get(i).get(qms.get(j).getColumnName().split("-")[3]).toString().replace("[","").replace("]","");
														for(String v : values.split(",")){
															value += typeDetailsMap.get(v)+", ";
														}
														value = value.substring(0, value.length()-2);
														break;

													default:
														value = typeDetailsMap.get(submission.getData().get(qms.get(j).getParentColumnName()).get(i).get(qms.get(j).getColumnName().split("-")[3]).toString());
														break;
													}
													
													PdfPTable table = new PdfPTable(2);
													table.setWidthPercentage(100);
													table.setSpacingBefore(5f);
													table.setSpacingAfter(0f);

													// Set Column widths
													float[] columnWidths = { 1f, 1f };
													table.setWidths(columnWidths);

													PdfPCell cell1 = new PdfPCell(new Paragraph(qms.get(j).getLabel()));
													cell1.setBorderColor(Color.BLACK);
													cell1.setPaddingLeft(10);
													cell1.setBorder(Rectangle.NO_BORDER);
													cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
													cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
//													submission.getData().get(question.getColumnName()).toString()
													PdfPCell cell2 = new PdfPCell(new Paragraph(value != null && value.equals("null")?"":value));
													cell2.setBorderColor(Color.BLACK);
													cell2.setPaddingLeft(10);
													cell2.setBorder(Rectangle.NO_BORDER);
													cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
													cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

													table.addCell(cell1);
													table.addCell(cell2);

													document.add(table);
												}
												break;

												default:
													break;
												}
													
											}
											
											if(i == submission.getData().get(qm.getParentColumnName()).size()-1)
												break outer2;
										}
									}
								}
								
								break;

							default:
								break;
							}

						}

					}

				}

			}

		}

		document.close();
		writer.close();

		return dest;
	}

	@Override
	public boolean saveFacilitySubmission(ReceiveEventModel receiveEventModel, int i, Principal principal) {
		UserModel model = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (receiveEventModel.getFormStatus() == null || !(receiveEventModel.getFormStatus().equals("save")
				|| receiveEventModel.getFormStatus().equals("finalized"))) {
			throw new IllegalArgumentException("Invalid parameter sent in payload for formStatus.");
		}
		Account acc = new Account();
		acc.setId((int) model.getUserId());
//		FacilitySubmission submission = facilitySubmissionRepository.findByUniqueIdAndCreatedBy(receiveEventModel.getUniqueId(), acc);

//		if (submission == null) {
			FacilitySubmission submission = new FacilitySubmission();
			submission.setData(receiveEventModel.getSubmissionData());
			submission.setActive(true);
			submission.setFormId(receiveEventModel.getFormId());
			submission.setUniqueId(receiveEventModel.getUniqueId());
			submission.setCreatedDate(new Date());
			submission.setUpdatedDate(new Date());
			submission.setSubmissionDate(new Date());
			submission.setFormId(receiveEventModel.getFormId());
			submission.setCreatedBy(acc);
//			if finalize than isactive--false >> and in save status is active true
			submission.setActive(receiveEventModel.getFormStatus().equals("save") ? true : false);
			submission.setFormStatus(receiveEventModel.getFormStatus().equals("save") ? FormStatus.SAVED : FormStatus.FINALIZED);
			submission.setUpdatedDate(new Date());
			
//		} else {
//			submission.setData(receiveEventModel.getSubmissionData());
//			submission.setSubmissionDate(new Date());
////			if finalize than isactive--false >> and in save status is active true
//			submission.setActive(receiveEventModel.getFormStatus().equals("save") ? true : false);
//			submission.setFormStatus(receiveEventModel.getFormStatus().equals("save") ? FormStatus.SAVED : FormStatus.FINALIZED);
//			submission.setUpdatedDate(new Date());
//		}

		facilitySubmissionRepository.save(submission);
//		try {
//			if(receiveEventModel.getFormStatus().equals("finalized"))
//				pdfFileDestination = downloadSubmissionPDF(submission, receiveEventModel);
//		} catch (Exception e) {
//			log.error("Error during generating pdf after submission.", e);
//		}
		return true;
	}

}