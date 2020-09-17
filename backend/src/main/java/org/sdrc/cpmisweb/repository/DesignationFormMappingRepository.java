package org.sdrc.cpmisweb.repository;

import java.util.List;

import org.sdrc.cpmisweb.domain.DesignationFormMapping;
import org.sdrc.cpmisweb.model.FormType;
import org.sdrc.usermgmt.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;

public interface DesignationFormMappingRepository extends JpaRepository<DesignationFormMapping, Integer>{

//	List<DesignationFormMapping> findByDesignationInAndAccessTypeAndStatus(List<Designation> designations, AccessType dataEntry,
//			Status active);

//	List<DesignationFormMapping> findByDesignationIdAndAccessTypeAndFormStatus(Integer designationId, AccessType dataEntry, Status active);

	List<DesignationFormMapping> findByDesignationIdAndAccessTypeAndFormStatusAndFormFormType(Integer designationId, AccessType dataEntry,
			Status active, FormType dataEntryType); 
	List<DesignationFormMapping> findByDesignationInAndAccessTypeAndStatusAndFormFormType(List<Designation> designations, AccessType dataEntry,
			Status active, FormType dataEntryType);

} 
