package org.sdrc.cpmisweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.sdrc.cpmisweb.domain.FacilitySubmission;
import org.sdrc.cpmisweb.domain.Submission;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.repository.SubmissionRepository;
import org.sdrc.cpmisweb.service.SubmissionService;
import org.sdrc.cpmisweb.service.SubmissionServiceImpl;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import in.co.sdrc.sdrcdatacollector.models.ReceiveEventModel;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;
import in.co.sdrc.sdrcdatacollector.service.FormsService;

@Controller
@ResponseBody
@RequestMapping(value = "/api")
public class DataEntryController {

	@Autowired
	private FormsService dataEntryService;

	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	SubmissionRepository submissionRepository;	

	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@GetMapping("/getQuestion")
	public Map<String, Map<String, List<Map<String, List<QuestionModel>>>>> getQuestions(@RequestParam("type") String type,HttpSession session) {
		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> map = new HashMap<>();
		return dataEntryService.getQuestions(map, session, user, (Integer) user.getDesignationIds().toArray()[0],type);
	}

	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@RequestMapping(value = "/saveData", method = { RequestMethod.POST, RequestMethod.OPTIONS })
	public ResponseEntity<Object> sendNewSubmissionCommand(@RequestBody ReceiveEventModel receiveEventModel,
			Principal principal)  {
		try {
			UserModel model = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Account acc = new Account();
			acc.setId((int) model.getUserId());
			Submission submission = submissionRepository.findByTimeperiodTimeperiodIdAndCreatedBy(receiveEventModel.getTimeperiodId(), acc);
			if(submission != null && submission.getFormStatus().equals(FormStatus.FINALIZED))
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Form has already been submitted for this time period.");
			
			if (submissionService.saveSubmission(receiveEventModel, 0, principal)) {
				String fileName = SubmissionServiceImpl.pdfFileDestination;
				if(fileName != null){
					File file = new File(fileName);
					InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
					HttpHeaders headers = new HttpHeaders();
					headers.add("Content-Disposition", String.format("attachment;filename=\"%s\"", file.getName()));
					headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
					headers.add("Pragma", "no-cache");
					headers.add("Expires", "O");
					
					ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/pdf")).body(resource);
					return responseEntity;
				}else{
					return ResponseEntity.noContent().build();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	
	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@RequestMapping(value = "/saveFacilityData", method = { RequestMethod.POST, RequestMethod.OPTIONS })
	public ResponseEntity<Object> sendNewSubmissionCommandForFacility(@RequestBody ReceiveEventModel receiveEventModel,
			Principal principal)  {
		try {
			if (submissionService.saveFacilitySubmission(receiveEventModel, 0, principal)) {
				String fileName = SubmissionServiceImpl.pdfFileDestination;
				if(fileName != null){
					File file = new File(fileName);
					InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
					HttpHeaders headers = new HttpHeaders();
					headers.add("Content-Disposition", String.format("attachment;filename=\"%s\"", file.getName()));
					headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
					headers.add("Pragma", "no-cache");
					headers.add("Expires", "O");
					
					ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/pdf")).body(resource);
					return responseEntity;
				}else{
					return ResponseEntity.noContent().build();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@GetMapping("/getDataForReview")
	public ReviewPageModel getDataForReview(@RequestParam(value="formId",required=false) Integer formId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, HttpSession session) {

		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> paramKeyValMap = new HashMap<>();

		return dataEntryService.getDataForReview(formId, startDate, endDate, paramKeyValMap, session,
				user.getUserId(), (Integer) user.getDesignationIds().toArray()[0]);
	}
	
	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@GetMapping("/getDataForFacilityReview")
	public FacilitySubmission getDataForFacilityReview(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, HttpSession session) {

		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> paramKeyValMap = new HashMap<>();

		return dataEntryService.getDataForFacilityReview(startDate, endDate, paramKeyValMap, session,
				user, (Integer) user.getDesignationIds().toArray()[0]);
	}
	
	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@RequestMapping(value = "/checkDataEntryStatus", method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public ResponseEntity<Boolean> sendNewSubmissionCommand() throws Exception {
		if (submissionService.isDataEntryDoneForCurrentMonth()) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<>(false, HttpStatus.OK);
	}
	
	
	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@RequestMapping(value = "/getCurrentFormEntryMomthYear", method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public ResponseEntity<String> getCurrentFormEntryMomthYear() {
		LocalDate current  = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		current = current.minusMonths(1);
		String monthYear = current.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " - "+ current.getYear();
		return new ResponseEntity<>(monthYear, HttpStatus.OK);
	}
	
}
