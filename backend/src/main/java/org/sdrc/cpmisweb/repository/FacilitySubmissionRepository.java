package org.sdrc.cpmisweb.repository;

import java.util.List;

import org.sdrc.cpmisweb.domain.FacilitySubmission;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Subham Ashish (subham@sdrc.co.in)
 *
 */
public interface FacilitySubmissionRepository extends JpaRepository<FacilitySubmission, Long> {

	FacilitySubmission findByUniqueIdAndCreatedBy(String uniqueId, Account acc);

	FacilitySubmission findByCreatedByIdAndFormStatusAndFormIdAndIsActiveTrue(int parseInt, FormStatus saved,
			Integer formId);

	List<FacilitySubmission> findByCreatedByIdAndFormStatusAndFormIdAndIsActiveFalseOrderByUpdatedDateDesc(int parseInt,
			FormStatus finalized, Integer formId);

	List<FacilitySubmission> findByCreatedByIdAndFormIdOrderByUpdatedDateDesc(int userId, Integer formId);

	FacilitySubmission findTop1ByFormIdAndFormStatusAndIsActiveFalseOrderByUpdatedDateDesc(Integer formId,FormStatus formStatus);

	List<FacilitySubmission> findByFormIdAndFormStatusAndIsActiveFalseOrderByUpdatedDateDesc(Integer formId,
			FormStatus finalized);

	FacilitySubmission findTop1ByCreatedByIdAndFormStatusAndIsActiveFalseOrderByUpdatedDateDesc(
			Integer integer,FormStatus finalized);

	List<FacilitySubmission> findByFormStatusAndIsActiveFalseOrderByUpdatedDateDesc(FormStatus finalized);

}
