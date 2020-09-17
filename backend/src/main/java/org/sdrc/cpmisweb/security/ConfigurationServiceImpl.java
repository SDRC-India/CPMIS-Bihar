/**
 * @author Pratyush(pratyush@sdrc.co.in)
 * created on 17-Apr-2019
 */
package org.sdrc.cpmisweb.security;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.cpmisweb.domain.AccountDetails;
import org.sdrc.cpmisweb.domain.DesignationFormMapping;
import org.sdrc.cpmisweb.domain.Indicator;
import org.sdrc.cpmisweb.domain.IndicatorClassification;
import org.sdrc.cpmisweb.domain.IndicatorUnitSubgroup;
import org.sdrc.cpmisweb.domain.InstitutionDetails;
import org.sdrc.cpmisweb.domain.InstitutionUserMapping;
import org.sdrc.cpmisweb.domain.Subgroup;
import org.sdrc.cpmisweb.domain.Unit;
import org.sdrc.cpmisweb.model.FormType;
import org.sdrc.cpmisweb.repository.AccountDetailsRepository;
import org.sdrc.cpmisweb.repository.AreaRepository;
import org.sdrc.cpmisweb.repository.DesignationFormMappingRepository;
import org.sdrc.cpmisweb.repository.IndicatorClassificationRepository;
import org.sdrc.cpmisweb.repository.IndicatorRepository;
import org.sdrc.cpmisweb.repository.IndicatorUnitSubgroupRepository;
import org.sdrc.cpmisweb.repository.InstitutionDetailsRepository;
import org.sdrc.cpmisweb.repository.InstitutionUserMappingRepository;
import org.sdrc.cpmisweb.repository.SubgroupRepository;
import org.sdrc.cpmisweb.repository.UnitRepository;
import org.sdrc.cpmisweb.repository.UserDomainRepository;
import org.sdrc.cpmisweb.repository.customAccountRepository;
import org.sdrc.cpmisweb.util.Constant;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountAuthorityMapping;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.domain.Authority;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.domain.DesignationAuthorityMapping;
import org.sdrc.usermgmt.model.AuthorityControlType;
import org.sdrc.usermgmt.repository.AccountAuthorityMappingRepository;
import org.sdrc.usermgmt.repository.AccountDesignationMappingRepository;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.sdrc.usermgmt.repository.AuthorityRepository;
import org.sdrc.usermgmt.repository.DesignationAuthorityMappingRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
	
	@Autowired
	DesignationRepository designationRepository;
	
	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	DesignationAuthorityMappingRepository designationAuthorityMappingRepository;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	AccountDesignationMappingRepository accountDesignationMappingRepository;

	@Autowired
	AccountAuthorityMappingRepository accountAuthorityMappingRepository;
	
	@Autowired
	EngineFormRepository engineFormRepository;
	
	@Autowired
	DesignationFormMappingRepository designationFormMappingRepository;
	
	@Autowired
	AreaRepository areaRepository;
	
	@Autowired
	UserDomainRepository userDomainRepository;
	
	@Autowired
	AccountDetailsRepository accountDetailsRepository;
	
	@Autowired
	InstitutionDetailsRepository institutionDetailsRepository;
	
	@Autowired
	InstitutionUserMappingRepository institutionUserMappingRepository;
	
	@Autowired
	customAccountRepository customAccountRepository;
	
	@Override
	public boolean createDesignation() {
		Designation designation = new Designation();
		designation.setName("ADMIN");
		designation.setCode("ADMIN");
		designationRepository.save(designation);
		
		designation = new Designation();
		designation.setName("SLU");
		designation.setCode("SLU");
		designationRepository.save(designation);
		
		userDomainRepository.findAll().forEach(u -> {
			if(!u.getScreenName().equals("slu")){
				Designation deoDesignation = new Designation();
				deoDesignation.setName(u.getScreenName());
				deoDesignation.setCode(u.getScreenName());
				designationRepository.save(deoDesignation);
			}
				
		});
		
		return true;
	}

	@Override
	public boolean createAuthority() {
		Authority authority = new Authority();
		authority.setAuthority("CREATE_USER");
		authority.setDescription("Allow user to CREATE USER module");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("USER_MGMT_ALL_API");
		authority.setDescription("Allow user to manage usermanagement module");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("CHANGE_PASSWORD");
		authority.setDescription("Allow user to access changepassword API");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("UPDATE_USER");
		authority.setDescription("Allow user to access updateuser API");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("ENABLE_DISABLE_USER");
		authority.setDescription("Allow user to access enable/disable user API");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("RESET_PASSWORD");
		authority.setDescription("Allow user to access reset password API");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("DASHBOARD_VIEW");
		authority.setDescription("Allow user to access dashboard");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("DATA_ENTRY");
		authority.setDescription("Allow user to data entry module");
		authorityRepository.save(authority);
		
		authority = new Authority();
		authority.setAuthority("MIS_REPORT");
		authority.setDescription("Allow user to mis report module");
		authorityRepository.save(authority);
		
		authority = new Authority();
		authority.setAuthority("RAWDATA_REPORT");
		authority.setDescription("Allow user to raw data report module");
		authorityRepository.save(authority);
		
		authority = new Authority();
		authority.setAuthority("SUBMISSION_STATUS_REPORT");
		authority.setDescription("Allow user to submission status report module");
		authorityRepository.save(authority);

		return true;
	}

	@Override
	public boolean createDesignationAuthorityMapping() {
		
		Authority dataEntry = authorityRepository.findByAuthority("DATA_ENTRY");
		Authority dashboard = authorityRepository.findByAuthority("DASHBOARD_VIEW");
		Authority changePassword = authorityRepository.findByAuthority("CHANGE_PASSWORD");
		
		designationRepository.findAll().stream().forEach(designation->{
			//dashboard
			DesignationAuthorityMapping damDashboard = new DesignationAuthorityMapping();
			damDashboard.setDesignation(designation);
			damDashboard.setAuthority(dashboard);
			designationAuthorityMappingRepository.save(damDashboard);
			
			//change password
			DesignationAuthorityMapping damChangePassword = new DesignationAuthorityMapping();
			damChangePassword.setDesignation(designation);
			damChangePassword.setAuthority(changePassword);
			designationAuthorityMappingRepository.save(damChangePassword);
			
			//dataentry
			if(designation.getName()!="ADMIN" || designation.getName()!="SLU"){
				DesignationAuthorityMapping designationAuthorityMapping = new DesignationAuthorityMapping();
				designationAuthorityMapping.setDesignation(designation);
				designationAuthorityMapping.setAuthority(dataEntry);
				designationAuthorityMappingRepository.save(designationAuthorityMapping);		
			}
		});
		
		return true;
	}

	@Override
	public boolean createAreaLevel() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean createStateLevelUsers() {
		XSSFWorkbook workbook = null;
		try(FileInputStream fis = new FileInputStream(ResourceUtils.getFile("classpath:state level users.xlsx"))){
			workbook = new XSSFWorkbook(fis);

			XSSFSheet sheet = null;
				sheet = workbook.getSheetAt(0);
				
				for (int row = 1; row <= sheet.getLastRowNum(); row++) {
					XSSFRow xssfRow = sheet.getRow(row);
					Account user = new Account();
					AccountDetails accountDetails = new AccountDetails();
					// Starting cells
					Iterator<Cell> cellIterator = xssfRow.cellIterator();
					int cols = 0;
					Cell cell = null;
					
					while (cellIterator.hasNext()) {
						cell = cellIterator.next();
						switch (cols) {
						case 1:
							user.setUserName(cell.getStringCellValue());
							break;
						case 2:
							user.setPassword(passwordEncoder.encode(cell.getStringCellValue()));
							break;
						default:
							break;
						}
						cols++;
					}
//					set other properties here
					user.setEmail(null);
					user.setInvalidAttempts((short) 0);
					user.setOtp(null);
					user.setOtpGeneratedDateTime(null);
					user.setAuthorityControlType(AuthorityControlType.DESIGNATION);
					if(accountRepository.findByUserName(user.getUserName()) == null){
						user = accountRepository.save(user);
						
						accountDetails.setArea(areaRepository.findOne(2));
						accountDetails.setUserTypeId(userDomainRepository.findByUserTypeId(11));
						accountDetails.setAccount(user);
						accountDetails.setSjpuAccess(true);
						accountDetails = accountDetailsRepository.save(accountDetails);
						
						AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
						accountDesignationMapping.setDesignation(designationRepository.findByCode("SLU"));
						accountDesignationMapping.setAccount(user);
						List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
						accountDesignationMappingList.add(accountDesignationMapping);
						accountDesignationMappingRepository.save(accountDesignationMappingList);
						
						AccountAuthorityMapping accountAuthorityMapping = new AccountAuthorityMapping();
						accountAuthorityMapping.setAuthority(authorityRepository.findByAuthority("USER_MGMT_ALL_API"));
						accountAuthorityMapping.setAccount(user);
						
						List<AccountAuthorityMapping> accountAuthorityMappings = new ArrayList<>();
						accountAuthorityMappings.add(accountAuthorityMapping);
						accountAuthorityMappingRepository.save(accountAuthorityMappings);
						
					}
				}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	@Transactional
	public boolean createOtherUser() {
		return createAllUsers();
	}
	
	private boolean createAllUsers(){
		XSSFWorkbook workbook = null;
		try(FileInputStream fis = new FileInputStream(ResourceUtils.getFile("classpath:cpmisusers.xlsx"))){
			workbook = new XSSFWorkbook(fis);

			XSSFSheet sheet = null;
			for(int i = 0; i < workbook.getNumberOfSheets(); i++){
				sheet = workbook.getSheetAt(i);
				
				for (int row = 1; row <= sheet.getLastRowNum(); row++) {
					XSSFRow xssfRow = sheet.getRow(row);
					Account user = new Account();
					AccountDetails accountDetails = new AccountDetails();
					InstitutionDetails institutionDetails = new InstitutionDetails();
					InstitutionUserMapping institutionUserMapping = new InstitutionUserMapping();
					// Starting cells
					Iterator<Cell> cellIterator = xssfRow.cellIterator();
					int cols = 0;
					Cell cell = null;
					while (cellIterator.hasNext()) {
						cell = cellIterator.next();
						switch (cols) {
						case 0:
							user.setEmail(cell.getStringCellValue().equals("NA")?null:cell.getStringCellValue());
							break;
						case 1:
							user.setUserName(cell.getStringCellValue());
							break;
						case 2:
							accountDetails.setArea(areaRepository.findOne((int) cell.getNumericCellValue()));
							break;
						case 3:
							accountDetails.setUserTypeId(userDomainRepository.findByUserTypeId((int) cell.getNumericCellValue()));
							break;
						case 4:
							institutionDetails.setInstitutionName(cell.getStringCellValue());
							break;
						case 5:
							institutionDetails.setInmateType(cell.getStringCellValue());
							break;
						default:
							break;
						}
						cols++;
					}
//					set other properties here
					String password = (user.getUserName().split("_")[0]+user.getUserName().split("_")[1].substring(0, 3)).toLowerCase()+"123";
					user.setPassword(passwordEncoder.encode(password));
					user.setInvalidAttempts((short) 0);
					user.setOtp(null);
					user.setOtpGeneratedDateTime(null);
					user.setAuthorityControlType(AuthorityControlType.DESIGNATION);
					if(accountRepository.findByUserName(user.getUserName()) == null){
						user = accountRepository.save(user);
						
						accountDetails.setAccount(user);
						accountDetails.setSjpuAccess(true);
						accountDetails = accountDetailsRepository.save(accountDetails);
						
						AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
						accountDesignationMapping.setDesignation(designationRepository.findByCode(accountDetails.getUserTypeId().getScreenName()));
						accountDesignationMapping.setAccount(user);
						List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
						accountDesignationMappingList.add(accountDesignationMapping);
						accountDesignationMappingRepository.save(accountDesignationMappingList);
						
						AccountAuthorityMapping accountAuthorityMapping = new AccountAuthorityMapping();
						accountAuthorityMapping.setAuthority(authorityRepository.findByAuthority("DATA_ENTRY"));
						accountAuthorityMapping.setAccount(user);
						List<AccountAuthorityMapping> accountAuthorityMappings = new ArrayList<>();
						accountAuthorityMappings.add(accountAuthorityMapping);
						accountAuthorityMappingRepository.save(accountAuthorityMappings);
						
						institutionDetails.setDistrictId(accountDetails.getArea());
						institutionDetails.setUserDomain(accountDetails.getUserTypeId());
						
						if(institutionUserMappingRepository.findByUserId(user) == null){
							institutionDetails = institutionDetailsRepository.save(institutionDetails);
							
							institutionUserMapping.setArea(institutionDetails.getDistrictId());
							institutionUserMapping.setInstitutionId(institutionDetails);
							institutionUserMapping.setUserId(user);
							
							institutionUserMappingRepository.save(institutionUserMapping);
						}
					}
				}
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean updatePassword() {
		List<Account> allAccount = accountRepository.findAll();
		for(Account user : allAccount){
			AccountDetails ad = accountDetailsRepository.findByAccount(user);
			if(!user.getUserName().equals("admin") || !user.getUserName().equals("d")){
				if(ad.getUserTypeId().getUserTypeId()!=Constant.STATE_LEVEL_USER_TYPE_ID){
					System.out.println("updating password for :: "+user.getUserName());
					String password = (user.getUserName().split("_")[0]+user.getUserName().split("_")[1].substring(0, 3)).toLowerCase()+"123";
					user.setPassword(passwordEncoder.encode(password));
					accountRepository.save(user);
				}	
			}
		}
		return true;
	}

	@Override
	public ResponseEntity<String> createAdmin() {
		Account user = new Account();
		user.setInvalidAttempts((short) 0);

		user.setUserName("admin");
		user.setPassword(passwordEncoder.encode("****"));

		user.setOtp(null);
		user.setOtpGeneratedDateTime(null);
		user.setEmail("pratyush@sdrc.co.in");

		user = accountRepository.save(user);

		AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
		accountDesignationMapping.setDesignation(designationRepository.findByCode("ADMIN"));
		accountDesignationMapping.setAccount(user);
		List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
		accountDesignationMappingList.add(accountDesignationMapping);
		accountDesignationMappingRepository.save(accountDesignationMappingList);

		return new ResponseEntity<>("success", HttpStatus.OK);
	}
	
	@Override
	public boolean createEngineForms(){
		
		EnginesForm cwcForm = new EnginesForm();
		cwcForm.setFormId(1);
		cwcForm.setName("CWC FORM");
		cwcForm.setStatus(Status.ACTIVE);
		cwcForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(cwcForm);

		EnginesForm jjbForm = new EnginesForm();
		jjbForm.setFormId(2);
		jjbForm.setName("JJB FORM");
		jjbForm.setStatus(Status.ACTIVE);
		jjbForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(jjbForm);

		EnginesForm cciOhForm = new EnginesForm();
		cciOhForm.setFormId(3);
		cciOhForm.setName("CCIOH FORM");
		cciOhForm.setStatus(Status.ACTIVE);
		cciOhForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(cciOhForm);


		EnginesForm cciChForm = new EnginesForm();
		cciChForm.setFormId(4);
		cciChForm.setName("CCICH FORM");
		cciChForm.setStatus(Status.ACTIVE);
		cciChForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(cciChForm);

		EnginesForm cciOsForm = new EnginesForm();
		cciOsForm.setFormId(5);
		cciOsForm.setName("CCIOS FORM");
		cciOsForm.setStatus(Status.ACTIVE);
		cciOsForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(cciOsForm);

		EnginesForm cciPosForm = new EnginesForm();
		cciPosForm.setFormId(6);
		cciPosForm.setName("CCIPOS FORM");
		cciPosForm.setStatus(Status.ACTIVE);
		cciPosForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(cciPosForm);

		EnginesForm cciShform = new EnginesForm();
		cciShform.setFormId(7);
		cciShform.setName("CCISH FORM");
		cciShform.setStatus(Status.ACTIVE);
		cciShform.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(cciShform);

		EnginesForm sjpuForm = new EnginesForm();
		sjpuForm.setFormId(8);
		sjpuForm.setName("SJPU FORM");
		sjpuForm.setStatus(Status.ACTIVE);
		sjpuForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(sjpuForm);
		
		EnginesForm dcpuForm = new EnginesForm();
		dcpuForm.setFormId(9);
		dcpuForm.setName("DCPU FORM");
		dcpuForm.setStatus(Status.ACTIVE);
		dcpuForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(dcpuForm);
		
		EnginesForm saaForm = new EnginesForm();
		saaForm.setFormId(10);
		saaForm.setName("SAA FORM");
		saaForm.setStatus(Status.ACTIVE);
		saaForm.setFormType(FormType.DATA_ENTRY_TYPE);
		engineFormRepository.save(saaForm);
		
		EnginesForm form = new EnginesForm();
		form.setFormId(21);
		form.setName("CWC FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);

		form = new EnginesForm();
		form.setFormId(22);
		form.setName("JJB FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);

		form = new EnginesForm();
		form.setFormId(23);
		form.setName("CCIOH FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);


		form = new EnginesForm();
		form.setFormId(24);
		form.setName("CCICH FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);

		form = new EnginesForm();
		form.setFormId(25);
		form.setName("CCIOS FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);

		form = new EnginesForm();
		form.setFormId(26);
		form.setName("CCIPOS FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);

		form = new EnginesForm();
		form.setFormId(27);
		form.setName("CCISH FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);

		form = new EnginesForm();
		form.setFormId(28);
		form.setName("SJPU FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);
		
		form = new EnginesForm();
		form.setFormId(29);
		form.setName("DCPU FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);
		
		form = new EnginesForm();
		form.setFormId(30);
		form.setName("SAA FACILITY FORM");
		form.setStatus(Status.ACTIVE);
		form.setFormType(FormType.FACILITY_ENTRY_TYPE);
		engineFormRepository.save(form);
		
		
		
		
		//Designation Form Mapping
		
		DesignationFormMapping mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("cwc-data-entry"));
		mapping.setForm(cwcForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("jjb-data-entry"));
		mapping.setForm(jjbForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("observation-home-data-entry"));
		mapping.setForm(cciOhForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);

		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("children-home-data-entry"));
		mapping.setForm(cciChForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("open-shelter-data-entry"));
		mapping.setForm(cciOsForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("place-of-safety-data-entry"));
		mapping.setForm(cciPosForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("special-home-data-entry"));
		mapping.setForm(cciShform);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("sjpu-data-entry"));
		mapping.setForm(sjpuForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("dcpu-data-entry"));
		mapping.setForm(dcpuForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("saa-data-entry"));
		mapping.setForm(saaForm);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("cwc-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(21));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("jjb-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(22));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("observation-home-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(23));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);

		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("children-home-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(24));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("open-shelter-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(25));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("place-of-safety-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(26));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("special-home-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(27));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("sjpu-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(28));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("dcpu-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(29));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		mapping = new DesignationFormMapping();
		mapping.setDesignation(designationRepository.findByCode("saa-data-entry"));
		mapping.setForm(engineFormRepository.findByFormId(30));
		mapping.setAccessType(AccessType.DATA_ENTRY);
		mapping.setCreatedDate(new Date());
		designationFormMappingRepository.save(mapping);
		
		return true;
		
	}
	@Autowired IndicatorClassificationRepository indicatorClassificationRepository;
	
	@Autowired IndicatorRepository indicatorRepository;
	
	@Autowired SubgroupRepository subgroupRepository;
	
	@Autowired UnitRepository unitRepository;
	
	@Autowired IndicatorUnitSubgroupRepository indicatorUnitSubgroupRepository;

	@Override
	@Transactional
	public boolean insertIntoIndicatorSubgroupAndIUS() {
		XSSFWorkbook workbook = null;
		try(FileInputStream fis = new FileInputStream(ResourceUtils.getFile("classpath:Indicator mapping for CPMIS report.xlsx"))){
			workbook = new XSSFWorkbook(fis);

			XSSFSheet sheet = null;
			sheet = workbook.getSheetAt(0);
				
			for (int row = 1; row <= sheet.getLastRowNum(); row++) {
				XSSFRow xssfRow = sheet.getRow(row);
				// Starting cells
				Iterator<Cell> cellIterator = xssfRow.cellIterator();
				int cols = 0;
				Cell cell = null;
				
				Indicator indicator = null;
				IndicatorClassification icSector = null;
				IndicatorClassification icSubSector = null;
				Subgroup subgroup = null;
				Unit unit = null;
				IndicatorUnitSubgroup ius = new IndicatorUnitSubgroup();
//				while (cellIterator.hasNext()) {
//					cell = cellIterator.next();
//					switch (cols) {
//					case 0:
//						System.out.println("Column 0::sl no::"+cell.getNumericCellValue());
//						break;
//					case 1:
//						System.out.println("Column 1::"+cell.getNumericCellValue());
//						ius.setFormId((int) cell.getNumericCellValue());
//						String iclName = userDomainRepository.findByUserTypeId((int)cell.getNumericCellValue()).getDescription();
//						icSector = indicatorClassificationRepository.findByNameAndParentIsNull(iclName);
//						
//						if(icSector == null){
//							IndicatorClassification icl = new IndicatorClassification();
//							icl.setIndicatorClassificationType(IndicatorClassificationType.SC);
//							icl.setName(iclName);
//							indicatorClassificationRepository.save(icl);
//						}
//						icSector = indicatorClassificationRepository.findByNameAndParentIsNull(iclName);
//						break;
//					case 2:
//						icSubSector = indicatorClassificationRepository.findByNameAndParent(cell.getStringCellValue().trim(), icSector);
//						if(icSubSector == null && !cell.getStringCellValue().trim().isEmpty()){
//							IndicatorClassification icl = new IndicatorClassification();
//							
//							icl.setIndicatorClassificationType(IndicatorClassificationType.SC);
//							icl.setName(cell.getStringCellValue());
//							icl.setParent(icSector);
//							
//							indicatorClassificationRepository.save(icl);
//						}
//						icSubSector = indicatorClassificationRepository.findByNameAndParent(cell.getStringCellValue().trim(), icSector);
//						break;
//					case 3:
//						indicator = indicatorRepository.findByIndicatorName(cell.getStringCellValue().trim());
//						if (indicator == null) {
//							indicator = new Indicator();
//							indicator.setHighIsGood(true);
//							indicator.setIndicatorClassification(icSubSector);
//							indicator.setIndicatorMetadata(null);
//							indicator.setIndicatorName(cell.getStringCellValue());
//							indicator = indicatorRepository.save(indicator);
//
//							System.out.println("Indicator Name :" + cell.getStringCellValue());
//						}
//						break;
//					case 4:
//						System.out.println("Subgroup::" + cell.getStringCellValue().trim());
//						subgroup = subgroupRepository.findBySubgroupVal(cell.getStringCellValue().trim());
//						if(subgroup == null){
//							Subgroup subg = new Subgroup();
//							subg.setSubgroupVal(cell.getStringCellValue().trim());
//							subg.setSubgroupValOrder(row);
//							subgroupRepository.save(subg);
//						}
//						subgroup = subgroupRepository.findBySubgroupVal(cell.getStringCellValue().trim());
//						break;
//					case 5:
//						System.out.println("Unit Name ::" + cell.getStringCellValue());
//						unit = unitRepository.findByUnitName(cell.getStringCellValue().trim());
//						break;
//					case 7:
//						System.out.println("Numerator::"+cell.getStringCellValue());
//						ius.setNumeratorColName(cell.getStringCellValue().trim());
//						break;
//					case 8:
//						System.out.println("Numerator parent::"+cell.getStringCellValue());
//						ius.setNumeratorParentColName(cell.getStringCellValue().trim());
//						break;
//					case 10:
//						System.out.println("Denominator::"+cell.getStringCellValue());
//						ius.setDenominatorColName(cell.getStringCellValue().trim());
//						break;
//					case 11:
//						System.out.println("Denominator parent::"+cell.getStringCellValue());
//						ius.setDenominatorParentColName(cell.getStringCellValue().trim());
//						break;
//					default:
//						break;
//					}
//					cols++;
//				}
//					set other properties here
//				ius.setIndicator(indicator);
//				ius.setUnit(unit);
//				ius.setSubgroup(subgroup);
//				
//				if(indicatorUnitSubgroupRepository.findByIndicatorAndUnitAndSubgroup(indicator, unit, subgroup) == null){
//					ius = indicatorUnitSubgroupRepository.save(ius);
//				}else{
//					System.out.println(ius);
//				}
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}