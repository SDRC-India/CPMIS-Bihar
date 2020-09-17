package org.sdrc.cpmisweb.model;

import java.util.Date;

import in.co.sdrc.sdrcdatacollector.models.SubmissionStatus;
import lombok.Data;

@Data
public class StatusReportModel {
	
	private int slno;
	private String areaName;
	private String facilityType;
	private String  facilityName;
	private SubmissionStatus submissionStatus;
	private Date submissionDate;
}
