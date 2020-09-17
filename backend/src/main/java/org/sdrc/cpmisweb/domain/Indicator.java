/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 08-Jul-2019
 */
package org.sdrc.cpmisweb.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.sdrc.cpmisweb.model.QuarterlyAggregateRule;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Indicator {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "indicator_id")
	private Integer indicatorId;

	@Column(name = "indicator_name")
	private String indicatorName;

	@Column(name = "indicator_metadata", length = 65536)
	private String indicatorMetadata;

	@Column(name = "high_is_good")
	private boolean highIsGood;

	@ManyToOne
	@JoinColumn(name = "indicator_classification_id")
	private IndicatorClassification indicatorClassification;
	
	@Column
	private boolean live;
	
	@Transient
	private double normalalizedValue;
	
	@Enumerated(EnumType.STRING)
	@Column
	private QuarterlyAggregateRule quarterlyAggregateRule;
}