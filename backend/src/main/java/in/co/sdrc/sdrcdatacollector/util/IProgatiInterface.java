package in.co.sdrc.sdrcdatacollector.util;

import java.util.List;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.models.AccessType;

public interface IProgatiInterface {

	public List<EnginesForm> getAssignesFormsForDataEntry(AccessType dataEntr,String type) ;
	
	public List<EnginesForm> getAssignesFormsForReview(AccessType review);
}
