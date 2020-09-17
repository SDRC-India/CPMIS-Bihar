/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 12-Jul-2019
 */
package org.sdrc.cpmisweb.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SectorSourceMapping {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="sector_id_fk")
	private IndicatorClassification indicatorClassificationSc;
	
	@OneToOne
	@JoinColumn(name="source_id_fk")
	private IndicatorClassification indicatorClassificationSr;

}
