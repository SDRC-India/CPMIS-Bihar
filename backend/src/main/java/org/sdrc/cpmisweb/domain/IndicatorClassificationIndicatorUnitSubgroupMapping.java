/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 10-Jul-2019
 */
package org.sdrc.cpmisweb.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="ic_ius_mapping")
public class IndicatorClassificationIndicatorUnitSubgroupMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ic_ius_id")
	private Integer iCIusId;

	@ManyToOne
	@JoinColumn(name = "ic_fk")
	private IndicatorClassification indicatorClassification;

	@ManyToOne
	@JoinColumn(name = "ius_fk")
	private IndicatorUnitSubgroup indicatorUnitSubgroup;
}
