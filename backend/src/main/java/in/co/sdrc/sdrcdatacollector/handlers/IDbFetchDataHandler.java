package in.co.sdrc.sdrcdatacollector.handlers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.RawDataModel;

public interface IDbFetchDataHandler {

	List<DataModel> fetchDataFromDb(EnginesForm form, String type, Map<Integer, String> mapOfForms, Map<String, Object> paramKeyValMap, HttpSession session, Object user);

	RawDataModel findAllByRejectedFalseAndSyncDateBetween(Integer formId, Date startDate, Date endDate);

}
