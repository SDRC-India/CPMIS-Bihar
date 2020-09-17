package org.sdrc.cpmisweb.service;

import java.util.List;

import org.sdrc.cpmisweb.model.DistrictListModel;
import org.sdrc.cpmisweb.model.FacilityTypeModel;
import org.sdrc.cpmisweb.model.StatusReportModel;

public interface StatusReportService {

	public List<StatusReportModel> getStatusReportdata(Integer timeperiodId,Integer ftId,Integer distId);
	
	List<FacilityTypeModel> fetchFacilitytypeForReport();
	
	List<DistrictListModel> fetchDistrictForReport();
}
