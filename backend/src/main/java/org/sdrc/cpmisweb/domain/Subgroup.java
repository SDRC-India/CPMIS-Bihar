/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 09-Jul-2019
 */
package org.sdrc.cpmisweb.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "subgroup_val")
public class Subgroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subgroup_val_id")
	private Integer subgroupValueId;

	@Column(name = "subgroup_val")
	private String subgroupVal;

	@Column(name = "subgroup_val_order")
	private int subgroupValOrder;

	public Subgroup() {
		super();
	}
	
}