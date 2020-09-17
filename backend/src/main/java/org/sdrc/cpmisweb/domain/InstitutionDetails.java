package org.sdrc.cpmisweb.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="institution_details")
public class InstitutionDetails {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="institution_id")
	private Integer institutionId;
	
	@Column(name="institution_name")
	private String institutionName;

	@ManyToOne
	@JoinColumn(name = "district_id_fk")
	private Area districtId;
	
	@Column(name = "inmate_type")
	private String inmateType;
	
	@ManyToOne
	@JoinColumn(name="institution_type_id_fk")
	private UserDomain userDomain;
	
	@OneToOne(mappedBy="institutionId")
	private InstitutionUserMapping institutionUserMapping;

}