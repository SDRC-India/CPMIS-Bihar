package org.sdrc.cpmisweb.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityInfoMapDashboardModel {

	private Long id;
	private int formId;
	private String imagePath;
	private double latitudeValue;
	private double longitudeValue;
	private Date submissionDate;
	private String formStatus;
	private String lastUpdatedDate;
	private int districtId;
	private String districtName;
	private int institutionId;
	private String institutionName;
	private String contactNumbers;
	private String emailId;
	private String uniqueSubmissionId;
	private String address;
		
}
