package org.sdrc.cpmisweb.model;

import java.util.Date;

import org.sdrc.cpmisweb.domain.Timeperiod;

import lombok.Data;

@Data
public class TimeperiodModel {

	private Integer timeperiodId;
	private String timePeriod;
	private Date startDate;
	private Date endDate;
	private Integer periodicity;
    private Date createdeDate;
	private Timeperiod quarter;
	private String groupName;
	
}
