package in.co.sdrc.sdrcdatacollector.handlers;

import java.util.Map;

import org.springframework.stereotype.Component;

import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.DataObject;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Component
public class IDbReviewQueryHandlerImpl implements IDbReviewQueryHandler {

	@Override
	public DataObject setReviewHeaders(DataObject dataObject, Question question,
			Map<Integer, TypeDetail> typeDetailsMap, DataModel submissionData, String type) {

	
		
		return dataObject;
	}
}
