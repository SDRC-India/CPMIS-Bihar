/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 09-Jul-2019
 */
package org.sdrc.cpmisweb.repository;

import java.util.List;

import org.sdrc.cpmisweb.domain.IndicatorClassification;
import org.sdrc.cpmisweb.model.IndicatorClassificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicatorClassificationRepository extends JpaRepository<IndicatorClassification, Integer> {

	public IndicatorClassification findByNameAndParentIsNull(String name);
	public IndicatorClassification findByNameAndParent(String name, IndicatorClassification icSector);
	public List<IndicatorClassification> findByIndicatorClassificationType(IndicatorClassificationType indicatorClassificationType);
	public List<IndicatorClassification> findByIndicatorClassificationTypeAndNameNotLike(IndicatorClassificationType icType, String sjpu);
	public List<IndicatorClassification> findByIndicatorClassificationTypeAndUserDomainUserTypeId(IndicatorClassificationType sc, Integer userTypeId);
	public IndicatorClassification findByIndicatorClassificationTypeAndFormFormId(IndicatorClassificationType icType, Integer formId);
}
