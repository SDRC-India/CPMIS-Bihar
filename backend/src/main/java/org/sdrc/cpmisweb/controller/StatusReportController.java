package org.sdrc.cpmisweb.controller;

import java.util.List;

import org.sdrc.cpmisweb.model.DistrictListModel;
import org.sdrc.cpmisweb.model.FacilityTypeModel;
import org.sdrc.cpmisweb.model.StatusReportModel;
import org.sdrc.cpmisweb.model.TimeperiodModel;
import org.sdrc.cpmisweb.service.ReportService;
import org.sdrc.cpmisweb.service.StatusReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusReportController {
	
	@Autowired
	ReportService reportService;
	
	@Autowired
	StatusReportService statusReportService; 
	
	
	@GetMapping("/getTimeperiodForStatusReport")
	public List<TimeperiodModel> fetchUtTimeperiod(){

		return reportService.fetchTimeperiodForReport();
	}
	
	@GetMapping("/getFacilityTypesForStatusReport")
	public List<FacilityTypeModel> fetchFacilityTypeForReport(){

		return statusReportService.fetchFacilitytypeForReport();
	}
	
	@GetMapping("/getDistrictForStatusReport")
	public List<DistrictListModel> fetchDistrictForReport(){

		return statusReportService.fetchDistrictForReport();
	}
	
	
	
	@PreAuthorize(value="hasAuthority('SUBMISSION_STATUS_REPORT')")
	@GetMapping("/getStatusReportForTimeperiodAndFaciltyType")
	public List<StatusReportModel> getStatusReportData(@RequestParam(value="tpId",required=false) Integer timeperiodId,
			@RequestParam(value="ftId",required=false) Integer ftId,
			@RequestParam(value="distId",required=false) Integer distId){
		return statusReportService.getStatusReportdata(timeperiodId,ftId,distId);
	}
	
	

}
