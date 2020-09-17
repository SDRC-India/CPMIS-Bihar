package org.sdrc.cpmisweb.service;

import java.security.Principal;

import in.co.sdrc.sdrcdatacollector.models.ReceiveEventModel;

public interface SubmissionService {

	boolean saveSubmission(ReceiveEventModel receiveEventModel, Integer facilityId, Principal principal);

	boolean isDataEntryDoneForCurrentMonth();

	boolean saveFacilitySubmission(ReceiveEventModel receiveEventModel, int i, Principal principal);
	
}
