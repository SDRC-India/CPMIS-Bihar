/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 10-Jul-2019
 */
package org.sdrc.cpmisweb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.cpmisweb.model.TimeperiodModel;
import org.sdrc.cpmisweb.model.ValueObject;
import org.sdrc.cpmisweb.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {
	
	@Autowired
	ReportService reportService;

	@GetMapping("/getSectorsForReport")
	public org.json.simple.JSONArray fetchAllSectors(){
		return reportService.fetchAllSectors();
	}
	
	@GetMapping("/getIndicatorsForReport")
	public List<ValueObject> fetchIndicators(@RequestParam(required = false) String sector, HttpServletRequest re){
		List<ValueObject> valueObjects = new ArrayList<>();
		if (sector != null) {
			valueObjects = reportService.fetchIndicatorsForReport(sector);
		}
		return valueObjects;
	}
	
	@GetMapping("/getTimeperiodForReport")
	public List<TimeperiodModel> fetchUtTimeperiod(@RequestParam(required = false) String iusnid, @RequestParam(required = false) String sourceNid){

		return reportService.fetchTimeperiodForReport();
	}
	
	@PreAuthorize(value="hasAuthority('MIS_REPORT')")
	@GetMapping("/getReports")
	public org.json.simple.JSONObject getReports(@RequestParam Integer indicatorId, @RequestParam Integer timeperiodId){
		
		return reportService.getReportdata(indicatorId, timeperiodId);
	}
	//done upto here
	@GetMapping("/getUserTypesForRawDataReport")
	public List<ValueObject> getUserTypesRawDataReport(){

		return reportService.fetchUserTypesForRawDataReport();
	}
	
	@GetMapping("/getTimeperiodForRawDataReport")
	public List<ValueObject> getTimeperiodForRawDataReport(@RequestParam Integer userTypeId){

		return reportService.fetchTimeperiodForRawDataReport(userTypeId);
	}
	
	@GetMapping("/rawDataReport/getRawDataReport")
	public boolean getRawDataReport(@RequestParam Integer timeperiodId, @RequestParam Integer userTypeId, HttpServletResponse response){
		return reportService.getRawDataReport(timeperiodId, userTypeId, response);
	}
	
}