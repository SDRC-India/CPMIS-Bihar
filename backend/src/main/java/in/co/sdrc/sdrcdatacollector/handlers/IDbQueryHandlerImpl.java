package in.co.sdrc.sdrcdatacollector.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.sdrc.cpmisweb.domain.Area;
import org.sdrc.cpmisweb.domain.Submission;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.cpmisweb.repository.SubmissionRepository;
import org.sdrc.cpmisweb.service.DcpuService;
import org.sdrc.cpmisweb.service.PrefetchDataService;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.jparepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.models.OptionModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 *
 */
@Component
public class IDbQueryHandlerImpl implements IDbQueryHandler {

@Autowired
SubmissionRepository submissionRepository;

@Autowired
PrefetchDataService prefetchDataService;

@Autowired
DcpuService dcpuService;

@Autowired
TypeDetailRepository typeDetailRepository;


	@Override
	public List<OptionModel> getOptions(QuestionModel questionModel, Map<Integer, TypeDetail> typeDetailsMap,
			Question question, String checkedValue, Object user1) {

		List<OptionModel> listOfOptions = new ArrayList<>();
//		String tableName = questionModel.getTableName().split("\\$\\$")[0].trim();
//		String areaLevel = questionModel.getTableName().split("\\$\\$")[1].trim().split("=")[1];
//		List<Area> areas = null;
//		UserModel user = (UserModel) user1;

		return listOfOptions;

	}

	@Override
	public String getDropDownValueForRawData(String tableName, Integer dropdownId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuestionModel setValueForTextBoxFromExternal(QuestionModel questionModel, Question question,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user) {

		String featureName = questionModel.getFeatures();
		UserModel userModel = (UserModel) user;
		if (featureName != null && featureName.contains("fetch_from_external")) {
			Integer formId = question.getFormId();
			Account account = new Account((int) userModel.getUserId());
			Submission submission = submissionRepository.findTop1ByFormIdAndCreatedByOrderBySubmissionDateDesc(formId, account);
			List<TypeDetail> typeDetails = typeDetailRepository.findByFormId(formId);
			Map<String, String> typeDetailsMap = new HashMap<>();
			typeDetails.forEach(e -> {
				typeDetailsMap.put(e.getSlugId().toString(), e.getName());
			});
			for (String feature : featureName.split("@AND")) {
				switch (feature.split(":")[0].trim()) {
				case "fetch_from_external_another_question": {
					
					switch (feature.split(":")[1].trim()) {
					case "previous_month": {
						if (submission == null || submission.getFormStatus().equals(FormStatus.SAVED)) {
							//removing disabled property on first login
							String defaultSettings = questionModel.getDefaultSettings();
							String features = questionModel.getFeatures();
							if(defaultSettings != null && !features.contains("editable") && !features.contains("exp:")){
								for(String ds : defaultSettings.split(",")){
									if(ds.equals("disabled"))
										defaultSettings = defaultSettings.replace(ds, "");
								}
								questionModel.setDefaultSettings(defaultSettings.length()==0?null:defaultSettings);
							}
							return questionModel;
						}
						JsonNode node = submission.getData();
						String fieldValue = "";
						Integer optionFieldValue = null;
						Integer[] checkboxFieldValues = null;
						if(question.getControllerType().equals("cell")){
							switch(formId){
							case 1:
								fieldValue = prefetchDataService.getCwcPreviousMonthData(question, node);
								break;
							case 2:
								fieldValue = prefetchDataService.getJjbPreviousMonthData(question, node);
								break;
							case 3:
								fieldValue = prefetchDataService.getCciOhPreviousMonthData(question, node);
								break;
							case 4:
								fieldValue = prefetchDataService.getCciChPreviousMonthData(question, node);
								break;
							case 5:
								fieldValue = prefetchDataService.getCciOsPreviousMonthData(question, node);
								break;
							case 6:
								fieldValue = prefetchDataService.getCciPosPreviousMonthData(question, node);
								break;
							case 7:
								fieldValue = prefetchDataService.getCciShPreviousMonthData(question, node);
								break;
							case 9:
								fieldValue = dcpuService.findPreviousMonthData(question.getColumnName(), node);
								questionModel.setRelevantValue(fieldValue);
								break;
							case 10:
								fieldValue = prefetchDataService.getSaaPreviousMonthData(question, node);
								break;
							default:
								break;
							}
						}else if(question.getControllerType().equals("dropdown")){
							switch (formId) {
							case 9:
								if(question.getFieldType().equals("option")){
									optionFieldValue = dcpuService.findPreviousMonthOptionData(questionModel, question, node, typeDetailsMap);
								}else if(question.getFieldType().equals("checkbox")){
									checkboxFieldValues = dcpuService.findPreviousMonthCheckboxData(questionModel, question, node);
								}
								break;

							default:
								break;
							}
						}
						else
							fieldValue = node.get(questionModel.getColumnName()).asText();
						
						if(!feature.contains("editable")){
							String defaultSetting = questionModel.getDefaultSettings();
							defaultSetting = defaultSetting==null?"disabled":defaultSetting.concat(",disabled");
							questionModel.setDefaultSettings(defaultSetting);
							
							//removing expression during prefetch
							String features = questionModel.getFeatures();
							for(String f : features.split("@AND")){
								if(f.startsWith("exp")) features = features.replace(f, "");
							}
							questionModel.setFeatures(features);
						}
						
						if(question.getFieldType().equals("option")){
							questionModel.setValue(optionFieldValue);
							questionModel.setDefaultValue(optionFieldValue);
						}else if(question.getControllerType().equals("cell")){
							questionModel.setValue(fieldValue);
							questionModel.setDefaultValue(fieldValue);
						}else if(question.getFieldType().equals("checkbox")){
							questionModel.setValue(checkboxFieldValues);
							questionModel.setDefaultValue(checkboxFieldValues);
						}
					}
						break;
						
					default:
							break;
					}
				}
					break;
				}
			}
		}
		return questionModel;
	}

}
