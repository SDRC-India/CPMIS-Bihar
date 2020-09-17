/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 21-Aug-2019
 */
package org.sdrc.cpmisweb.job;

import org.quartz.JobExecutionException;
import org.sdrc.cpmisweb.service.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@EnableScheduling
@Slf4j
public class QuarterlyAggregationJob {
	
	@Autowired
	AggregationService aggregationServiceImpl;

	//<second> <minute> <hour> <day-of-month> <month> <day-of-week>
	@Scheduled(cron="0 5 0 1 APR,JUL,OCT,JAN *")
	public void execute() throws JobExecutionException {
		try {
			System.out.println("Creating current month in Timeperiod");
			aggregationServiceImpl.createQuaterlyTimePeriod();
			System.out.println("Finished quarterly time period creation");
		} catch (Exception e) {
			log.error("Critical: Quarterly timeperiod creation cron job failed.",e);
		}
		
	}
}
