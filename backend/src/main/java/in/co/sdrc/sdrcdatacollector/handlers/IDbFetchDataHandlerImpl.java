package in.co.sdrc.sdrcdatacollector.handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.sdrc.cpmisweb.domain.FacilitySubmission;
import org.sdrc.cpmisweb.domain.Submission;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.model.FormType;
import org.sdrc.cpmisweb.repository.FacilitySubmissionRepository;
import org.sdrc.cpmisweb.repository.InstitutionUserMappingRepository;
import org.sdrc.cpmisweb.repository.SubmissionRepository;
import org.sdrc.cpmisweb.repository.TimePeriodRepository;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.RawDataModel;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Azaruddin (azaruddin@sdrc.co.in)
 */
@Component
public class IDbFetchDataHandlerImpl implements IDbFetchDataHandler {

	@Autowired
	DesignationRepository designationRepository;

	@Autowired
	SubmissionRepository submissionRepository;

	@Autowired
	InstitutionUserMappingRepository institutionUserMappingRepository;

	@Autowired
	TimePeriodRepository timeperiodRepository;

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	private FacilitySubmissionRepository facilitySubmissionRepository;

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

	@SuppressWarnings("unchecked")
	@Override
	public List<DataModel> fetchDataFromDb(EnginesForm form, String type, Map<Integer, String> mapOfForms,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user) {

		List<DataModel> submissionDatas = new ArrayList<>();
//		UserModel userModel = (UserModel) user;
		ObjectMapper mapper = new ObjectMapper();
		
		if (form.getFormType().equals(FormType.DATA_ENTRY_TYPE)) {

			Account account = accountRepository.findById((Integer) user);

			List<Object[]> entriesList = submissionRepository.findSubmissionForDraftPage(account.getId());
			List<Object[]> nullDataList = new ArrayList<>();
			for (Object[] obj : entriesList) {
				if (obj[1] == null) {
					nullDataList.add(obj);
				}
			}
			boolean dueFlag = false;
			boolean dueFlag1 = false;
			for (Object[] obj : entriesList) {
				if (obj[1] != null) {
					Submission data = submissionRepository.findOne(Long.valueOf((obj[1].toString())));

					Map<String, Object> extraKeys = new HashMap<>();
					extraKeys.put("submissionId", data.getId());
					if (data.getFormStatus().equals(FormStatus.SAVED)) {
						extraKeys.put("view", "edit");
						dueFlag1 = true;
					} else {
						extraKeys.put("view", "preview");
						dueFlag1 = false;
					}
					extraKeys.put("monthYearOfSubmission", data.getTimeperiod().getTimePeriod());
					extraKeys.put("timeperiodId", obj[2]);
					extraKeys.put("startDate", obj[3]);
					extraKeys.put("endDate", obj[4]);
					extraKeys.put("createdOn", sdf.format(data.getCreatedDate()));
					extraKeys.put("updatedOn", sdf.format(data.getUpdatedDate()));
					DataModel model = new DataModel();
					model.setCreatedDate(data.getCreatedDate());
					model.setData(mapper.convertValue(data.getData(), Map.class));
					model.setFormId(data.getFormId());
					model.setId(data.getId() + "");
					model.setRejected(false);
					model.setUniqueId(data.getUniqueId());
					model.setUpdatedDate(data.getUpdatedDate());
					model.setUserId(data.getCreatedBy().getId() + "");
					model.setUserName(data.getCreatedBy().getUserName());
					model.setExtraKeys(extraKeys);
					model.setTimeperiod(data.getTimeperiod());
					submissionDatas.add(model);
				} else {
					Map<String, Object> extraKeys = new HashMap<>();
					extraKeys.put("view", "due");
					if (dueFlag1 == true) {
						extraKeys.put("enables", false);
					} else if (!dueFlag && !nullDataList.isEmpty()) {
						extraKeys.put("enables", true);
						dueFlag = true;
					} else {
						extraKeys.put("enables", false);
					}
					extraKeys.put("monthYearOfSubmission", obj[0].toString());
					extraKeys.put("timeperiodId", obj[2]);
					extraKeys.put("startDate", obj[3]);
					extraKeys.put("endDate", obj[4]);
					extraKeys.put("createdOn", null);
					extraKeys.put("updatedOn", null);

					DataModel model = new DataModel();
					model.setFormId(form.getFormId());
					model.setUserId(account.getId() + "");
					model.setData(null);
					model.setExtraKeys(extraKeys);

					submissionDatas.add(model);
				}
			}
		} else {

			
			List<FacilitySubmission> datas = facilitySubmissionRepository.findByCreatedByIdAndFormIdOrderByUpdatedDateDesc(Integer.parseInt(user.toString()),form.getFormId());
			
			FacilitySubmission data=null;
			if(!datas.isEmpty())
				data=datas.get(0);
			
			if(data!=null) {
				Map<String, Object> extraKeys = new HashMap<>();
				extraKeys.put("submissionId", data.getId());
				extraKeys.put("createdOn", sdf.format(data.getCreatedDate()));
				extraKeys.put("updatedOn", sdf.format(data.getUpdatedDate()));
				
				DataModel model = new DataModel();
				model.setCreatedDate(data.getCreatedDate());
				model.setData(mapper.convertValue(data.getData(), Map.class));
				model.setFormId(data.getFormId());
				model.setId(data.getId() + "");
				model.setRejected(false);
				model.setUniqueId(data.getUniqueId());
				model.setUpdatedDate(data.getUpdatedDate());
				model.setUserId(data.getCreatedBy().getId() + "");
				model.setUserName(data.getCreatedBy().getUserName());
				model.setExtraKeys(extraKeys);
				submissionDatas.add(model);
				
			}else {
				Account account = accountRepository.findById((Integer) user);
				Map<String, Object> extraKeys = new HashMap<>();

				DataModel model = new DataModel();
				model.setFormId(form.getFormId());
				model.setUserId(account.getId() + "");
				model.setData(null);
				model.setExtraKeys(extraKeys);

				submissionDatas.add(model);
			}
			
		}

		return submissionDatas;
	}

	@Override
	public RawDataModel findAllByRejectedFalseAndSyncDateBetween(Integer formId, Date startDate, Date endDate) {

		return null;
	}

}
