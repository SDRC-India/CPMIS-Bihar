/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 09-Jul-2019
 */
package org.sdrc.cpmisweb.repository;

import org.sdrc.cpmisweb.domain.Indicator;
import org.sdrc.cpmisweb.domain.IndicatorUnitSubgroup;
import org.sdrc.cpmisweb.domain.Subgroup;
import org.sdrc.cpmisweb.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicatorUnitSubgroupRepository extends JpaRepository<IndicatorUnitSubgroup, Integer> {

	IndicatorUnitSubgroup findByIndicatorAndUnitAndSubgroup(Indicator indicator,Unit unit,Subgroup subgroup);
	IndicatorUnitSubgroup findByIndicatorUnitSubgroupId(Integer id);
}
