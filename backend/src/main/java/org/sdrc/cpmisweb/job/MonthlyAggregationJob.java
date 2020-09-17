package org.sdrc.cpmisweb.job;

import org.quartz.JobExecutionException;
import org.sdrc.cpmisweb.repository.TimePeriodRepository;
import org.sdrc.cpmisweb.service.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pratyush(pratyush@sdrc.co.in) created on : 03-Mar-2018 11:37:43 am
 */
@Service
@EnableScheduling
@Slf4j
public class MonthlyAggregationJob{
	@Autowired
	AggregationService aggregationServiceImpl;
	
	@Autowired
	TimePeriodRepository timePeriodRepository;

	
	//runs on first day of every month at 00:00 O'clock
//	<second> <minute> <hour> <day-of-month> <month> <day-of-week>
	@Scheduled(cron="0 0 0 1 * *")
	public void execute() throws JobExecutionException {
		try {
			System.out.println("Creating current month in Timeperiod");
			aggregationServiceImpl.createCurrentMonthTimePeriod();
			System.out.println("Finished time period creation");
		} catch (Exception e) {
			log.error("Critical: Monthly timeperiod creation cron job failed.",e);
		}
		
	}
}