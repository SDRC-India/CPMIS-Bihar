package org.sdrc.cpmisweb.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.sdrc.cpmisweb.domain.AccountDetails;
import org.sdrc.cpmisweb.domain.Area;
import org.sdrc.cpmisweb.domain.InstitutionUserMapping;
import org.sdrc.cpmisweb.domain.Submission;
import org.sdrc.cpmisweb.domain.UserDomain;
import org.sdrc.cpmisweb.model.DistrictListModel;
import org.sdrc.cpmisweb.model.FacilityTypeModel;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.model.StatusReportModel;
import org.sdrc.cpmisweb.repository.AccountDetailsRepository;
import org.sdrc.cpmisweb.repository.AreaRepository;
import org.sdrc.cpmisweb.repository.InstitutionUserMappingRepository;
import org.sdrc.cpmisweb.repository.SubmissionRepository;
import org.sdrc.cpmisweb.repository.UserDomainRepository;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.models.SubmissionStatus;

@Service
public class StatusReportServiceImpl implements StatusReportService{

	@Autowired
	private InstitutionUserMappingRepository institutionUserMappingRepository;
	
	@Autowired
	private SubmissionRepository submissionRepository;
	
	@Autowired
	private AccountDetailsRepository accountDetailsRepository;
	
	@Autowired
	private UserDomainRepository userDomainRepository;
	
	@Autowired
	private AreaRepository areaRepository;
	
	/**
	 * This method is used to fetch all types facility including All
	 */
	
	@Override
	public List<FacilityTypeModel> fetchFacilitytypeForReport() {
		
		List<FacilityTypeModel> facilityTypeModelList = new ArrayList<>();
		FacilityTypeModel ftModelFirst = new FacilityTypeModel();
		ftModelFirst.setFacilityTypeId(0);
		ftModelFirst.setFacilityTypeName("All");
		facilityTypeModelList.add(ftModelFirst);
		List<UserDomain> facilityTypes = userDomainRepository.findAll();
		facilityTypes=facilityTypes.stream().filter(d->d.getUserTypeId()!=11).sorted(Comparator.comparing(UserDomain::getDescription)).collect(Collectors.toList());

		for (UserDomain ud : facilityTypes) {
			FacilityTypeModel ftModel = new FacilityTypeModel();
			ftModel.setFacilityTypeId(ud.getUserTypeId());
			ftModel.setFacilityTypeName(ud.getDescription());
			facilityTypeModelList.add(ftModel);
		}

		return facilityTypeModelList;
		
	}
	
	/**
	 * This method is used to fetch all types District including All
	 */
	
	@Override
	public List<DistrictListModel> fetchDistrictForReport() {
		List<DistrictListModel> distModelList = new ArrayList<>();
		DistrictListModel dlModelFirst = new DistrictListModel();
		dlModelFirst.setDistrictId(0);
		dlModelFirst.setDistrictName("All");
		distModelList.add(dlModelFirst);
		List<Area> areaList = areaRepository.findByLevelAndParentAreaId(3,2);
		areaList=areaList.stream().sorted(Comparator.comparing(Area::getAreaName)).collect(Collectors.toList());

		for (Area area : areaList) {
			DistrictListModel dModel = new DistrictListModel();
			dModel.setDistrictId(area.getAreaId());
			dModel.setDistrictName(area.getAreaName());
			distModelList.add(dModel);
		}

		return distModelList;
	}
	
	/**
	 * This method is used to fetch submission staus of facilities based on 
	 * Logged in user type,Timeperiod,District and Facility types
	 */
	
	@Override
	public List<StatusReportModel> getStatusReportdata(Integer timeperiodId,Integer ftId,Integer distId) {
		
		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account acc = new Account();
		acc.setId((int) user.getUserId());
		List<StatusReportModel> statusReportModelList=new ArrayList<>();
		List<Submission> submissionListFortheTimeperiod= submissionRepository.findAllByFormStatusAndTimeperiodTimeperiodId(FormStatus.FINALIZED, timeperiodId);
		List<InstitutionUserMapping> iumappingList=new ArrayList<>();
		int desgId=(Integer) user.getDesignationIds().toArray()[0];
		switch (desgId) {
		case 2:
				if(distId==0){
					iumappingList=institutionUserMappingRepository.findAll();
					iumappingList=(ftId==0)?iumappingList:iumappingList.stream().filter(b -> b.getInstitutionId().getUserDomain().getUserTypeId()==ftId).collect(Collectors.toList());
				}
				else{
					iumappingList=institutionUserMappingRepository.findByAreaAreaId(distId);
					iumappingList=(ftId==0)?iumappingList:iumappingList.stream().filter(b -> b.getInstitutionId().getUserDomain().getUserTypeId()==ftId).collect(Collectors.toList());
				}
				
				break;
			
		case 11:
			
				AccountDetails accountDetails = accountDetailsRepository.findByAccount(acc);
				iumappingList=institutionUserMappingRepository.findByAreaAreaId(accountDetails.getArea().getAreaId());
				iumappingList=(ftId==0)?iumappingList:iumappingList.stream().filter(b -> b.getInstitutionId().getUserDomain().getUserTypeId()==ftId).collect(Collectors.toList());
			
				break;
		}
		int tempNum=1;
		for(InstitutionUserMapping ium:iumappingList) {
				List<Submission> submissionSlu=submissionListFortheTimeperiod.stream().filter(b -> b.getCreatedBy().getId()==ium.getUserId().getId()).collect(Collectors.toList());
				StatusReportModel srmodel=new StatusReportModel();
				srmodel.setSlno(tempNum);
				srmodel.setAreaName(ium.getInstitutionId().getDistrictId().getAreaName());
				srmodel.setFacilityName(ium.getInstitutionId().getInstitutionName());
				srmodel.setFacilityType(ium.getInstitutionId().getUserDomain().getDescription());
				srmodel.setSubmissionStatus((submissionSlu.size()!=0)?SubmissionStatus.Yes:SubmissionStatus.No);
				srmodel.setSubmissionDate((submissionSlu.size()!=0)?submissionSlu.get(0).getSubmissionDate():null);
				statusReportModelList.add(srmodel);
				tempNum++;
			}
		return statusReportModelList;
	}

}
