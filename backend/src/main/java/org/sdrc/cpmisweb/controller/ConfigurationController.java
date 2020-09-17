/**
 * @author Pratyush(pratyush@sdrc.co.in)
 * created on 16-Apr-2019
 */
package org.sdrc.cpmisweb.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.sdrc.cpmisweb.security.ConfigurationService;
import org.sdrc.cpmisweb.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import in.co.sdrc.sdrcdatacollector.service.UploadFormConfigurationService;

@Controller
@RequestMapping(value="/bypass")
public class ConfigurationController {

	@Autowired
	ConfigurationService configurationService;
	
	@Autowired
	SubmissionService submissionService;
	
	@Autowired
	UploadFormConfigurationService uploadFormConfigurationService;
	
	@RequestMapping(value="/createDesignations")
	@ResponseBody
	public boolean generateDesignation(){
		boolean designationTable = configurationService.createDesignation();
		boolean authorityTable = configurationService.createAuthority();
		boolean designationAuthorityMappingTable = configurationService.createDesignationAuthorityMapping();
		return designationTable && authorityTable && designationAuthorityMappingTable;
	}

	@RequestMapping(value="/createAreaLevel")
	@ResponseBody
	public boolean generateAreaLevel(){
		
		return configurationService.createAreaLevel();
	}
	
	@RequestMapping(value="/createAccount")
	@ResponseBody
	public boolean generateAccount(){
		
		return configurationService.createOtherUser();
	}
	
	
	@RequestMapping(value="/updatePassword")
	@ResponseBody
	public boolean updatePassword(){
		
		return configurationService.updatePassword();
	}
	
	@RequestMapping(value="/createAdmin")
	@ResponseBody
	public ResponseEntity<String> createAdmin(){
		return configurationService.createAdmin();
	}
	
	@RequestMapping(value="/createStateLevelUsers")
	@ResponseBody
	public boolean createStateLevelUsers(){
		return configurationService.createStateLevelUsers();
	}
	
	@RequestMapping("importQuestions")
	@ResponseBody
	public ResponseEntity<String> importQuestions(){
		
		return uploadFormConfigurationService.importQuestionData();
	}
	
	@RequestMapping("createEngineForms")
	@ResponseBody
	public boolean createEngineForms(){
		
		return configurationService.createEngineForms();
	}
	
	@GetMapping(value = "crontest")
	@ResponseBody
	public String cronTest() {
//									second minute hour day-of-month month day-of-week
		CronSequenceGenerator cron1 = new CronSequenceGenerator("0 5 0 1 APR,JUL,OCT,JAN *");
		CronSequenceGenerator cron2 = new CronSequenceGenerator("0 0 0 1 * *");
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss"); 
		  
		System.out.println("current date: "+sdf.format(cal.getTime()));
		System.out.println("Next cron trigger for quarterly: "+cron1.next(cal.getTime()));
		System.out.println("Next cron trigger for monthly: "+cron2.next(cal.getTime()));
		
		return "Next 5 quarterly scheduled dates: \n\n"+cron1.next(cal.getTime())+"\n"
		+cron1.next(cron1.next(cal.getTime()))+"\n"
		+cron1.next(cron1.next(cron1.next(cal.getTime())))+"\n"
		+cron1.next(cron1.next(cron1.next(cron1.next(cal.getTime()))))+"\n"
		+cron1.next(cron1.next(cron1.next(cron1.next(cron1.next(cal.getTime())))))+
		"\n \nNext 5 monthly scheduled dates: \n\n"+cron2.next(cal.getTime())+"\n"
		+cron2.next(cron2.next(cal.getTime()))+"\n"
		+cron2.next(cron2.next(cron2.next(cal.getTime())))+"\n"
		+cron2.next(cron2.next(cron2.next(cron2.next(cal.getTime()))))+"\n"
		+cron2.next(cron2.next(cron2.next(cron2.next(cron2.next(cal.getTime())))));
	}
	
	@RequestMapping("insertIntoIndicatorSubgroupAndIUS")
	@ResponseBody
	public boolean insertIntoIndicatorSubgroupAndIUS(){
		
		return configurationService.insertIntoIndicatorSubgroupAndIUS();
	}
}
