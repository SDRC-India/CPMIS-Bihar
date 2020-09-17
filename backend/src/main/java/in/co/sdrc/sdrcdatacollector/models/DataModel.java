package in.co.sdrc.sdrcdatacollector.models;

import java.util.Date;
import java.util.Map;

import org.sdrc.cpmisweb.domain.Timeperiod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataModel {

	private String id;

	private String userName;
	
	private Map<String,Object> extraKeys;

	private String userId;

	private Date createdDate;

	private Date updatedDate;

	private Map<String, Object> data;

	private Integer formId;

	private Timeperiod timeperiod;
	
	private String uniqueId;
	
	private boolean rejected;
	
	private String  uniqueName;
	
}
