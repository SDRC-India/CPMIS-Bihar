/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 09-Jul-2019
 */
package org.sdrc.cpmisweb.repository;

import java.util.List;

import org.sdrc.cpmisweb.domain.Indicator;
import org.sdrc.cpmisweb.domain.IndicatorClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IndicatorRepository extends JpaRepository<Indicator, Integer> {

	Indicator findByIndicatorName(String indicatorName);
	
	public List<Object[]> findByIndicatorClassificationAndLiveIsTrue(IndicatorClassification sectorNid);
	
	@Query("SELECT uticius, utUnit, " + "  utIn, " + "  subEn   "
			+ " FROM IndicatorClassificationIndicatorUnitSubgroupMapping uticius JOIN uticius.indicatorUnitSubgroup utius "
			+ " JOIN utius.indicator utIn JOIN  utius.unit utUnit JOIN utius.subgroup subEn "
			+ " WHERE uticius.indicatorClassification = :sectorNid and utUnit.unitId in :units Order by uticius.indicatorUnitSubgroup ")
	public List<Object[]> findByICType(@Param("sectorNid") IndicatorClassification sectorNid, @Param("units") Integer[] units);
}
