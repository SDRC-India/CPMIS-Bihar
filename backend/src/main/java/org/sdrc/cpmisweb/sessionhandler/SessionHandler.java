/**
 * @author Pratyush(pratyush@sdrc.co.in)
 * created on 16-Apr-2019
 */
package org.sdrc.cpmisweb.sessionhandler;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sdrc.cpmisweb.domain.AccountDetails;
import org.sdrc.cpmisweb.domain.Submission;
import org.sdrc.cpmisweb.domain.Timeperiod;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.model.FormType;
import org.sdrc.cpmisweb.model.TimeperiodModel;
import org.sdrc.cpmisweb.repository.AccountDetailsRepository;
import org.sdrc.cpmisweb.repository.DesignationFormMappingRepository;
import org.sdrc.cpmisweb.repository.SubmissionRepository;
import org.sdrc.cpmisweb.repository.TimePeriodRepository;
import org.sdrc.cpmisweb.util.Constant;
import org.sdrc.usermgmt.core.util.IUserManagementHandler;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.Designation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;

@Component
public class SessionHandler implements IUserManagementHandler {
	
	@Autowired
	DesignationFormMappingRepository designationFormMappingRepository;
	
	@Autowired
	AccountDetailsRepository accountDetailsRepository;
	
	@Autowired
	SubmissionRepository submissionRepository;
	
	@Autowired
	TimePeriodRepository timePeriodRepository;
	
	@Override
	public Map<String, Object> sessionMap(Object account) {
		Account acc = (Account) account;
		AccountDetails ad = accountDetailsRepository.findByAccount(acc);
		Map<String, Object> sessionMap = new HashMap<>();
		Designation d = acc.getAccountDesignationMapping().get(0).getDesignation();
		Timeperiod accountDataEntryStartTimeperiod = accountDetailsRepository.findByAccount(acc).getDataEntryStartTimeperiod();
		Timeperiod latestTimePeriod = timePeriodRepository.findTopByPeriodicityOrderByTimeperiodIdDesc(1);
		Integer designationId = (Integer) d.getId();
		Integer assignedFormId = designationFormMappingRepository.findByDesignationIdAndAccessTypeAndFormStatusAndFormFormType(designationId, AccessType.DATA_ENTRY, Status.ACTIVE,FormType.DATA_ENTRY_TYPE).isEmpty()?null: 
				designationFormMappingRepository.findByDesignationIdAndAccessTypeAndFormStatusAndFormFormType(designationId, AccessType.DATA_ENTRY, Status.ACTIVE,FormType.DATA_ENTRY_TYPE).get(0).getForm().getFormId();
		
		sessionMap.put("assignedFormId", designationFormMappingRepository.findByDesignationIdAndAccessTypeAndFormStatusAndFormFormType(designationId, AccessType.DATA_ENTRY, Status.ACTIVE,FormType.DATA_ENTRY_TYPE));
		sessionMap.put("designation", d.getName());
		sessionMap.put("districtName", accountDetailsRepository.findByAccount(acc).getArea().getAreaName());
		String sessionMapDataEntryTimeperiodKey = "dataEntryTimeperiod";
		ObjectMapper mapper = new ObjectMapper();
		if(ad.getUserTypeId().getUserTypeId() != Constant.STATE_LEVEL_USER_TYPE_ID){
			if(submissionRepository.findMaxTimePeriodByAccountIdAndFormId(acc.getId(), assignedFormId) == null){//1st login(not saved or submitted)
				sessionMap.put(sessionMapDataEntryTimeperiodKey, mapper.convertValue(accountDataEntryStartTimeperiod, TimeperiodModel.class));
			}else{//either saved or submitted
				Timeperiod userMaxTimePeriod = timePeriodRepository.findByTimeperiodId(submissionRepository.findMaxTimePeriodByAccountIdAndFormId(acc.getId(), assignedFormId));
				Submission submission = submissionRepository.findByCreatedByIdAndTimeperiodTimeperiodId(acc.getId(), userMaxTimePeriod.getTimeperiodId());
				
				if(userMaxTimePeriod.getTimeperiodId() < latestTimePeriod.getTimeperiodId()){
					if(submission.getFormStatus().equals(FormStatus.FINALIZED)){
						Timeperiod nextTimeperiod = timePeriodRepository.findNextTimeperiodOfGivenId(userMaxTimePeriod.getTimeperiodId());
						sessionMap.put(sessionMapDataEntryTimeperiodKey, mapper.convertValue(nextTimeperiod, TimeperiodModel.class));
					}else{
						sessionMap.put(sessionMapDataEntryTimeperiodKey, mapper.convertValue(userMaxTimePeriod, TimeperiodModel.class));
					}
				}else if(userMaxTimePeriod.getTimeperiodId() == latestTimePeriod.getTimeperiodId()){
					sessionMap.put(sessionMapDataEntryTimeperiodKey, mapper.convertValue(userMaxTimePeriod, TimeperiodModel.class));
				}
			}
		}	
		return sessionMap;
	}
	
	@Override
	public boolean saveAccountDetails(Map<String, Object> arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean updateAccountDetails(Map<String, Object> arg0, Object arg1, Principal arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<?> getAllAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

}
