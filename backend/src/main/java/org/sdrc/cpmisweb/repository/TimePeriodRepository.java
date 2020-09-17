package org.sdrc.cpmisweb.repository;

import java.util.Date;
import java.util.List;

import org.sdrc.cpmisweb.domain.Timeperiod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Biswa Ranjan CPMISWEB, Pratyush(pratyush@sdrc.c.in)
 * 06-Jan-2018 5:35:28 pm
 */
public interface TimePeriodRepository extends JpaRepository<Timeperiod,Integer>{

	public Timeperiod findByTimePeriod(String timePeriod);

	public Timeperiod findByTimeperiodId(int timePeriod);
	
	public Timeperiod findByStartDateAndEndDate(Date startDate, Date endDate);
	
	public List<Timeperiod> findAllByOrderByTimeperiodIdDesc();
	
	public Timeperiod findTopByPeriodicityOrderByTimeperiodIdDesc(Integer periodicityId);
	
	@Query(value="select t.* from time_period t where t.periodicity=1 and t.timeperiod_id > :id "
			+ "order by timeperiod_id asc limit 1",nativeQuery=true)
	Timeperiod findNextTimeperiodOfGivenId(@Param("id") Integer id);

	@Modifying
	@Transactional
	@Query(value="update time_period set quarter_id=:timeperiodId where start_date between "
			+ ":startDate and :endDate and periodicity=1",nativeQuery=true)
	public void update(@Param("timeperiodId") Integer timeperiodId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	public List<Timeperiod> findByQuarterTimeperiodIdOrderByTimeperiodIdAsc(Integer quarterId);
	
	@Query(value="SELECT MAX(timeperiod_id) from time_period WHERE periodicity = :periodicityId AND"
			+ " timeperiod_id < (SELECT MAX(timeperiod_id) from time_period WHERE periodicity = :periodicityId)",nativeQuery=true)
	public Integer findPreviousMonthId(@Param("periodicityId") Integer periodicityId);

}
