package org.sdrc.cpmisweb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sdrc.cpmisweb.domain.DesignationFormMapping;
import org.sdrc.cpmisweb.model.FormType;
import org.sdrc.cpmisweb.repository.DesignationFormMappingRepository;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.IProgatiInterface;
import in.co.sdrc.sdrcdatacollector.util.Status;

@Service
public class IProgatiHandler implements IProgatiInterface {

	@Autowired
	private DesignationFormMappingRepository designationFormMappingRepository;
	@Autowired
	DesignationRepository designationRepository;

	@Override
	public List<EnginesForm> getAssignesFormsForDataEntry(AccessType dataEntry,String type) {

		UserModel userModel = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<EnginesForm> formList = new ArrayList<>();

		Set<Object> roleIds = userModel.getDesignationIds();

		List<Integer> designationIds = new ArrayList<>();
		roleIds.forEach(e -> {
			designationIds.add((Integer) e);
		});

		List<Designation> designations = designationRepository.findByIdIn(designationIds);

		List<DesignationFormMapping> designationFormMappingList=new ArrayList<>();
		
		switch(type) {
		case "DATA_ENTRY_TYPE" :
			designationFormMappingList = designationFormMappingRepository
			.findByDesignationInAndAccessTypeAndStatusAndFormFormType(designations, dataEntry, Status.ACTIVE,FormType.DATA_ENTRY_TYPE);
			break;
		
		case "FACILITY_ENTRY_TYPE" :
			designationFormMappingList = designationFormMappingRepository
			.findByDesignationInAndAccessTypeAndStatusAndFormFormType(designations, dataEntry, Status.ACTIVE,FormType.FACILITY_ENTRY_TYPE);
			break;
		}

		if(!designationFormMappingList.isEmpty())
			designationFormMappingList.forEach(desgFormMapping -> formList.add(desgFormMapping.getForm()));

		return formList;
	}

	@Override
	public List<EnginesForm> getAssignesFormsForReview(AccessType review) {

		UserModel userModel = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<EnginesForm> formList = new ArrayList<>();

		Set<Object> roleIds = userModel.getDesignationIds();

		List<Integer> designationIds = new ArrayList<>();
		roleIds.forEach(e -> {
			designationIds.add((Integer) e);
		});
		List<Designation> designations = designationRepository.findByIdIn(designationIds);

		List<DesignationFormMapping> designationFormMappingList = designationFormMappingRepository
				.findByDesignationInAndAccessTypeAndStatusAndFormFormType(designations, review, Status.ACTIVE,FormType.DATA_ENTRY_TYPE);

		designationFormMappingList.forEach(desgFormMapping -> formList.add(desgFormMapping.getForm()));

		return formList;
	}
}
