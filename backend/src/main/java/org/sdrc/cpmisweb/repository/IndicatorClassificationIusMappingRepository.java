/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 10-Jul-2019
 */
package org.sdrc.cpmisweb.repository;

import org.sdrc.cpmisweb.domain.IndicatorClassification;
import org.sdrc.cpmisweb.domain.IndicatorClassificationIndicatorUnitSubgroupMapping;
import org.sdrc.cpmisweb.domain.IndicatorUnitSubgroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicatorClassificationIusMappingRepository
		extends JpaRepository<IndicatorClassificationIndicatorUnitSubgroupMapping, Integer> {

	public IndicatorClassificationIndicatorUnitSubgroupMapping findByIndicatorClassificationAndIndicatorUnitSubgroup(
			IndicatorClassification icSubSector, IndicatorUnitSubgroup ius);

}
