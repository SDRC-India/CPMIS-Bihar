/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 08-Jul-2019
 */
package org.sdrc.cpmisweb.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.sdrc.cpmisweb.model.IndicatorClassificationType;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class IndicatorClassification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "indicator_classification_id")
	private Integer indicatorClassificationId;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private IndicatorClassification parent;

	@OneToMany(mappedBy = "parent")
	private List<IndicatorClassification> children;

	@Column(name = "classification_name")
	private String name;

	@Column(name="indicatorclassificationtype")
	@Enumerated(EnumType.STRING)
	private IndicatorClassificationType indicatorClassificationType;

	@Column(name = "ic_order")
	private int icOrder;
	
	@OneToOne
	@JoinColumn(name = "form_id_fk")
	private EnginesForm form;
	
	@ManyToOne
	@JoinColumn(name = "user_type")
	private UserDomain userDomain;
	
}