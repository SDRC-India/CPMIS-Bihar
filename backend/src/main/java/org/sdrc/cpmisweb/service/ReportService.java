/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 10-Jul-2019
 */
package org.sdrc.cpmisweb.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sdrc.cpmisweb.model.TimeperiodModel;
import org.sdrc.cpmisweb.model.ValueObject;

public interface ReportService {

	JSONArray fetchAllSectors();

	List<ValueObject> fetchIndicatorsForReport(String param);
	
	List<TimeperiodModel> fetchTimeperiodForReport();
	
	public JSONObject getReportdata(Integer indicatorId, Integer timeperiodId);

	boolean getRawDataReport(Integer timeperiodId, Integer userTypeId, HttpServletResponse response);

	List<ValueObject> fetchUserTypesForRawDataReport();
	
	List<ValueObject> fetchTimeperiodForRawDataReport(Integer userTypeId);
}
