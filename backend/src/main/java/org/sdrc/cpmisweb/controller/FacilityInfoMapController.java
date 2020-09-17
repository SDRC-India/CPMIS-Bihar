/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 17-Aug-2019
 */
package org.sdrc.cpmisweb.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.sdrc.cpmisweb.model.FacilityInfoMapDashboardModel;
import org.sdrc.cpmisweb.model.FacilityTypeModel;
import org.sdrc.cpmisweb.service.FacilityInfoMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;

@RestController
public class FacilityInfoMapController {

	@Autowired
	private FacilityInfoMapService facilityInfoMapService;
	
	@GetMapping("/getFacilityTypesForInfoDashboard")
	public List<FacilityTypeModel> getFacilityTypes() {		
		
		return facilityInfoMapService.getFacilityTypes();
		
	}
	
	@GetMapping("/getFacilityDetailsData")
	public List<FacilityInfoMapDashboardModel>  getFacilityDetails(@RequestParam(value="formId") Integer formId) {
		return facilityInfoMapService.getFacilityDetailsData(formId);
	}
	
	@GetMapping("/getDetialsOfFacilty")
	public ReviewPageModel getDetialsOfFacilty(@RequestParam(value="facilitySubmissionId") Long facilitySubmissionId, HttpSession session){
		
		return facilityInfoMapService.getDetialsOfFacilty(facilitySubmissionId, session);
	}
}
