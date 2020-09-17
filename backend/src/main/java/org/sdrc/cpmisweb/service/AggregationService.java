/**
 * 
 */
package org.sdrc.cpmisweb.service;

import org.sdrc.cpmisweb.domain.Timeperiod;

/**
 * @author Pratyush(pratyush@sdrc.co.in) created on : 03-Mar-2018 11:56:18 am
 */
public interface AggregationService {

	Timeperiod createCurrentMonthTimePeriod() throws Exception;
	
	Timeperiod createQuaterlyTimePeriod() throws Exception;
	
//	public boolean callAggregation(Timeperiod timeperiod, String timeperiodIds)  throws Exception;
}
