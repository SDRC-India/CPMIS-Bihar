/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 10-Jul-2019
 */
package org.sdrc.cpmisweb.model;

import org.sdrc.cpmisweb.domain.Area;
import org.sdrc.cpmisweb.domain.Indicator;
import org.sdrc.cpmisweb.domain.IndicatorClassification;
import org.sdrc.cpmisweb.domain.IndicatorUnitSubgroup;
import org.sdrc.cpmisweb.domain.Subgroup;
import org.sdrc.cpmisweb.domain.Timeperiod;
import org.sdrc.cpmisweb.domain.Unit;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Data {

	private IndicatorUnitSubgroup indicatorUnitSubgroup;

	private Indicator indicator;

	private Unit unit;

	private Subgroup subgroup;

	private Timeperiod timePeriod;

	private IndicatorClassification source;

	private Area area;
	
	private Double percentage;

}