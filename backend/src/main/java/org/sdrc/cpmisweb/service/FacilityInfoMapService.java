/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 17-Aug-2019
 */
package org.sdrc.cpmisweb.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.sdrc.cpmisweb.model.FacilityInfoMapDashboardModel;
import org.sdrc.cpmisweb.model.FacilityTypeModel;

import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;

public interface FacilityInfoMapService {

	List<FacilityTypeModel> getFacilityTypes();

	List<FacilityInfoMapDashboardModel> getFacilityDetailsData(Integer formId);

	ReviewPageModel getDetialsOfFacilty(Long facilitySubmissionId, HttpSession session);
	
}
