/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 09-Jul-2019
 */
package org.sdrc.cpmisweb.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Unit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer unitId;
	
	@Column(name="unit_name")
	private String unitName;
	
}
