/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 31-Jul-2019
 */
package org.sdrc.cpmisweb.service;

import java.text.ParseException;
import java.util.List;

import org.json.simple.JSONArray;
import org.sdrc.cpmisweb.model.DataCollectionModel;
import org.sdrc.cpmisweb.model.LineSeries;
import org.sdrc.cpmisweb.model.ValueObject;

public interface DashboardService {

	JSONArray fetchSectorsForDashboard();
	List<ValueObject> fetchIndicators(String sector);
	List<ValueObject> fetchUtTimeperiod(int sourceNid);
	DataCollectionModel fetchData(String indicatorId, String sourceId, String parentAreaCode, String timeperiodId, Integer childLevel);
	List<List<LineSeries>> fetchChartData(Integer iusNid, Integer areaNid) throws ParseException;
	String exportPDF(List<String> svgs, String indicatorId, String sourceId, String parentAreaCode, String timeperiodId, Integer childLevel);
	String exportPDFLine(List<String> svgs, Integer iusNid, Integer areaNid);
}
