/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 17-Aug-2019
 */
package org.sdrc.cpmisweb.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.sdrc.cpmisweb.domain.FacilitySubmission;
import org.sdrc.cpmisweb.domain.InstitutionUserMapping;
import org.sdrc.cpmisweb.model.FacilityInfoMapDashboardModel;
import org.sdrc.cpmisweb.model.FacilityTypeModel;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.model.FormType;
import org.sdrc.cpmisweb.repository.FacilitySubmissionRepository;
import org.sdrc.cpmisweb.repository.InstitutionUserMappingRepository;
import org.sdrc.usermgmt.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;
import in.co.sdrc.sdrcdatacollector.service.FormsService;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class FacilityInfoMapServiceImpl implements FacilityInfoMapService{

	@Autowired
	private EngineFormRepository engineFormRepository;
	
	@Autowired
	private FacilitySubmissionRepository facilitySubmissionRepository;
	
	@Autowired
	private InstitutionUserMappingRepository institutionUserMappingRepository;
	
	@Autowired
	private FormsService dataEntryService;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	
	@Override
	public List<FacilityTypeModel> getFacilityTypes() {
		
		List<FacilityTypeModel> ftmList=new ArrayList<>();
		FacilityTypeModel ftm1=new FacilityTypeModel();
		ftm1.setFacilityTypeId(0);
		ftm1.setFacilityTypeName("All");
		ftmList.add(ftm1);
		
		List<EnginesForm> forms=engineFormRepository.findAll();		
		forms=forms.stream().filter(d->d.getFormType()==FormType.FACILITY_ENTRY_TYPE).sorted(Comparator.comparing(EnginesForm::getFormId)).collect(Collectors.toList());
	
		for(EnginesForm eform:forms) {			
			FacilityTypeModel ftm=new FacilityTypeModel();
			ftm.setFacilityTypeId(eform.getFormId());
			ftm.setFacilityTypeName(eform.getName().split(" ")[0]);
			ftm.setCCiType(Arrays.asList(23,24,25,26,27,30).contains(eform.getFormId())?true:false);
			switch (eform.getFormId()) {
			case 21:
                ftm.setIconPath( "./assets/images/pushpins/cwc.png");
                break;
            case 22:
                ftm.setIconPath( "./assets/images/pushpins/jjb.png");
                break;
            case 23:
                ftm.setIconPath( "./assets/images/pushpins/cciOH.png");
                break;
            case 24:
                ftm.setIconPath( "./assets/images/pushpins/cciCH.png");
                break;
            case 25:
                ftm.setIconPath( "./assets/images/pushpins/cciOS.png");
                break;
            case 26:
                ftm.setIconPath( "./assets/images/pushpins/cciPOS.png");
                break;
            case 27:
                ftm.setIconPath( "./assets/images/pushpins/cciSH.png");
                break;
            case 28:
                ftm.setIconPath( "./assets/images/pushpins/sjpu.png");
                break;
            case 29:
                ftm.setIconPath( "./assets/images/pushpins/dcpu.png");
                break;
            case 30:
                ftm.setIconPath( "./assets/images/pushpins/saa.png");
                break;
            default:
				break;
			}
			ftmList.add(ftm);
		}
		
		FacilityTypeModel ftm2=new FacilityTypeModel();
		ftm2.setFacilityTypeId(-1);
		ftm2.setFacilityTypeName("Child Care Institutions");
		ftmList.add(ftm2);
		
		return ftmList;
	}

	@Override
	public List<FacilityInfoMapDashboardModel> getFacilityDetailsData(Integer formId) {

		List<FacilityInfoMapDashboardModel> modelList=new ArrayList<>();
		
		List<FacilitySubmission> fsubList=(formId==0)?facilitySubmissionRepository.findByFormStatusAndIsActiveFalseOrderByUpdatedDateDesc(FormStatus.FINALIZED):facilitySubmissionRepository.findByFormIdAndFormStatusAndIsActiveFalseOrderByUpdatedDateDesc(formId,FormStatus.FINALIZED);
		
		if(fsubList!=null) {
		Set<Integer> st=new HashSet<>();
		for(FacilitySubmission fs:fsubList) {
			st.add(fs.getCreatedBy().getId());
		}
		List<Integer> list = new ArrayList<Integer>(st);
		for (int i = 0; i < list.size(); i++) {
			FacilityInfoMapDashboardModel fimdModel =new FacilityInfoMapDashboardModel();
			FacilitySubmission fsub=facilitySubmissionRepository.findTop1ByCreatedByIdAndFormStatusAndIsActiveFalseOrderByUpdatedDateDesc(list.get(i),FormStatus.FINALIZED);
			
			InstitutionUserMapping ium=institutionUserMappingRepository.findByUserId(fsub.getCreatedBy());
			fimdModel.setId(fsub.getId());
			fimdModel.setFormId(fsub.getFormId());
			switch (fsub.getFormId()) {
			case 21:
                fimdModel.setImagePath( "./assets/images/pushpins/cwc.png");
                fimdModel.setEmailId(fsub.getData().get("s1cwcq8").asText());
                fimdModel.setAddress(fsub.getData().get("s1cwcq4").asText());
                break;
            case 22:
                fimdModel.setImagePath( "./assets/images/pushpins/jjb.png");
                fimdModel.setEmailId(fsub.getData().get("s1jjbq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1jjbq4").asText());
                break;
            case 23:
                fimdModel.setImagePath( "./assets/images/pushpins/cciOH.png");
                fimdModel.setEmailId(fsub.getData().get("s1ohq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1ohq4").asText());
                break;
            case 24:
                fimdModel.setImagePath( "./assets/images/pushpins/cciCH.png");
                fimdModel.setEmailId(fsub.getData().get("s1ohq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1ohq4").asText());
                break;
            case 25:
                fimdModel.setImagePath( "./assets/images/pushpins/cciOS.png");
                fimdModel.setEmailId(fsub.getData().get("s1ohq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1ohq4").asText());
                break;
            case 26:
                fimdModel.setImagePath( "./assets/images/pushpins/cciPOS.png");
                fimdModel.setEmailId(fsub.getData().get("s1ohq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1ohq4").asText());
                break;
            case 27:
                fimdModel.setImagePath( "./assets/images/pushpins/cciSH.png");
                fimdModel.setEmailId(fsub.getData().get("s1ohq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1ohq4").asText());
                break;
            case 28:
                fimdModel.setImagePath( "./assets/images/pushpins/sjpu.png");
                fimdModel.setEmailId(fsub.getData().get("s1sjpuq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1sjpuq4").asText());
                break;
            case 29:
                fimdModel.setImagePath( "./assets/images/pushpins/dcpu.png");
                fimdModel.setEmailId(fsub.getData().get("s1dcpuq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1dcpuq4").asText());
                break;
            case 30:
                fimdModel.setImagePath( "./assets/images/pushpins/saa.png");
                fimdModel.setEmailId(fsub.getData().get("s1saaq6").asText());
                fimdModel.setAddress(fsub.getData().get("s1saaq4").asText());
                break;
            default:
				break;
			}  
			fimdModel.setFormStatus(fsub.getFormStatus().toString());
			fimdModel.setLastUpdatedDate(sdf.format(fsub.getUpdatedDate()));
			fimdModel.setSubmissionDate(fsub.getSubmissionDate());
			fimdModel.setLatitudeValue(Double.valueOf(fsub.getData().get("geoloc1").asText()));
			fimdModel.setLongitudeValue(Double.valueOf(fsub.getData().get("geoloc2").asText()));
			fimdModel.setContactNumbers(fsub.getData().get("contactNumber0").asText());
			fimdModel.setDistrictId(ium.getArea().getAreaId());
			fimdModel.setDistrictName(ium.getArea().getAreaName());
			fimdModel.setInstitutionId(ium.getInstitutionId().getInstitutionId());
			fimdModel.setInstitutionName(ium.getInstitutionId().getInstitutionName());	
			fimdModel.setUniqueSubmissionId(fsub.getUniqueId());
			modelList.add(fimdModel);
		}
		
	}
		
		return modelList;
	}
	
	@Override
	public ReviewPageModel getDetialsOfFacilty(Long facilitySubmissionId, HttpSession session) {
		Map<String, Object> paramKeyValMap = new HashMap<>();
		try{
			FacilitySubmission facilitySubmission = facilitySubmissionRepository.findOne(facilitySubmissionId);
			Account account = facilitySubmission.getCreatedBy();
			
			return dataEntryService.getDataForReview(null, null, null, paramKeyValMap, session, account.getId(), null);
		}catch(Exception e){
			log.error("Exception while fetching facility data in infomap, for facility submissionId {}",facilitySubmissionId, e);
			return null;
		}
	}
	
}