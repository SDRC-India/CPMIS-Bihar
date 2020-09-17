package org.sdrc.cpmisweb.repository;

import java.util.Date;
import java.util.List;

import org.sdrc.cpmisweb.domain.Area;
import org.sdrc.cpmisweb.domain.InstitutionUserMapping;
import org.sdrc.cpmisweb.domain.Submission;
import org.sdrc.cpmisweb.domain.Timeperiod;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<Submission,Long>{

	List<Submission> findTop6ByFacilityOrderBySubmissionDateAsc(InstitutionUserMapping facility);
	
	List<Submission> findByCreatedByOrderByTimeperiodTimeperiodIdDesc(Account createdBy);

	Submission findByUniqueIdAndCreatedBy(String uniqueId, Account acc);
	
	Submission findByTimeperiodTimeperiodIdAndCreatedBy(Integer timeperiodId, Account acc);

//	Submission findTop1ByFacilityAndCreatedByOrderBySubmissionDateDesc(Facility facility, Account account);
//
	Submission findTop1ByFormIdAndCreatedByOrderBySubmissionDateDesc(Integer formId,	Account account);
	
	Submission findByFormIdAndCreatedByAndTimeperiod(Integer formId, Account account, Timeperiod tp);

	List<Submission> findAllByFormStatusAndFormIdAndSubmissionDateBetween(FormStatus finalized, Integer formId,
			Date startDate, Date endDate);

	List<Submission> findByIdInAndFormId(List<String> submissionIds, Integer formId);

	List<Submission> findAllByFormStatusAndSubmissionDateBetween(FormStatus finalized, Date startDate, Date endDate);

	Submission findTop1ByOrderBySubmissionDateAsc();

	Submission findTop1BySubmissionDateBetweenOrderBySubmissionDateAsc(Date startDate, Date endDate);

	@Query(value = "SELECT AVG( utdata.percentage) as avg , utdata.district_id_fk as areaID, area.area_name as areaName, "
			+ "icius.ic_fk as icNid , ic.classification_name as icName "
			+ "FROM data as utdata, "
			+ "ic_ius_mapping as icius , "
			+ "indicator_classification as ic, "
			+ "area as area "
			+ " where icius.ius_fk = utdata.indicator_unit_subgroup_id_fk  and icius.ic_fk = ic.indicator_classification_id "
			+ " and area.area_id = utdata.district_id_fk and "
			+ " utdata.time_period_id_fk = :timePeriod and utdata.district_id_fk IN (:areaList) "
			+ " and ic.parent_id = :index and ic.indicatorclassificationtype = 'SC' and utdata.source_id_or_ic_id_fk = 27 "
			+ " group by utdata.district_id_fk, icius.ic_fk,ic.classification_name,area.area_name "
			+ " ORDER BY utdata.district_id_fk", nativeQuery = true)
	List<Object[]> findByArea(@Param("areaList") List<Integer> areaList,
			@Param("timePeriod") Integer timePeriod ,@Param("index")int index );

	@Query(value = "SELECT data.percentage as dataValue, ic.indicator_classification_id as icNId, ic.classification_name as icName, indi.indicator_id as indNId, "
			+ "indi.indicator_name as indName, data.district_id_fk as areaNId, area.area_name as areaName "
			+ "FROM data as data, "
			+ "ic_ius_mapping as icius, "
			+ "indicator_classification as ic, "
			+ "indicator as indi, "
			+ "area as area "
			+ "where icius.ic_fk=ic.indicator_classification_id and data.indicator_unit_subgroup_id_fk=icius.ius_fk and indi.indicator_id=data.indicator_id_fk"
			+ " and data.district_id_fk=area.area_id and "
			+ "data.time_period_id_fk =:timePeriod and ic.parent_id=:parentSectorId AND "
			+ "data.district_id_fk IN (:areaList) ORDER BY data.district_id_fk,ic.classification_name", nativeQuery = true)
	List<Object[]> findInByArea(@Param("areaList") List<Integer> areaList,
			@Param("timePeriod") Integer timePeriod,
			@Param("parentSectorId") Integer parentSectorId);

	List<Submission> findAllByFormStatusAndFormIdAndSubmissionDateBetweenOrderBySubmissionDateDesc(FormStatus finalized,
			Integer formId, Date startDate, Date endDate);

	List<Submission> findAllByFormStatusAndFormIdAndFacilityAreaInAndSubmissionDateBetweenOrderBySubmissionDateDesc(
			FormStatus finalized, Integer formId, List<Area> districts, Date startDate, Date endDate);
	
	List<Submission> findAllByFormStatusAndFormIdAndTimeperiodTimeperiodId(FormStatus finalized, Integer formId, Integer timeperiodId);

	@Query(value="select * from submission where id IN ( select s from (select f.facility_id,(select s.id from  submission s where s.facility_id_fk=f.facility_id order by s.submission_date desc limit 1) as s from facility f where f.facility_type IS NOT NULL) as x)"
			,nativeQuery=true)
	List<Submission> findAllLastestSubmissionOfEachFacility();
	
	@Query(value="select max (timeperiod_id_fk) from submission where created_by_acc_id_fk=:userId and form_id=:assignedFormId", nativeQuery=true)
	Integer findMaxTimePeriodByAccountIdAndFormId(@Param("userId")Integer userId, @Param("assignedFormId")Integer assignedFormId);
	
	Submission findByCreatedByIdAndTimeperiodTimeperiodId(Integer accId, Integer timeperiodId);
	
	Submission findByFormStatusAndFormIdAndTimeperiod(FormStatus finalized,	Integer formId, Timeperiod timeperiod);
	
	@Query(value="select y.timeperiod,y.id,y.timeperiod_id,y.start_date,y.end_date from (select t.*,x.* from time_period t LEFT JOIN "
			+ "(select s.* from submission s where s.created_by_acc_id_fk =:accId) as x on t.timeperiod_id = x.timeperiod_id_fk where t.periodicity=1"
			+ "ORDER BY t.start_date desc LIMIT 12) as y "
			+ "where y.timeperiod_id >=(select data_entry_start_timeperiod_id_fk from account_details where acc_id_fk=:accId) "
			+ "ORDER BY y.start_date ASC", nativeQuery=true)
	List<Object[]> findSubmissionForDraftPage(@Param("accId") Integer accId);
	
	@Query(value="SELECT timeperiod_id,timeperiod FROM time_period WHERE timeperiod_id IN "
			+ "(SELECT DISTINCT timeperiod_id_fk FROM submission WHERE form_id=:formId AND form_status=:formStatus) ORDER BY timeperiod_id DESC", nativeQuery=true)
	List<Object[]> findTimeperiodByFormIdAndFormStatus(@Param("formId") Integer formId, @Param("formStatus") String formStatus);
	
	List<Submission> findAllByFormStatusAndTimeperiodTimeperiodId(FormStatus finalized,int tpId);
	
	@Query(value="SELECT timeperiod_id_fk FROM submission WHERE created_by_acc_id_fk =:accountId ORDER BY timeperiod_id_fk DESC OFFSET 1 LIMIT 1", nativeQuery=true)
	Integer findLastSubmissionTimeperiodId(@Param("accountId") Integer accountId);
}
