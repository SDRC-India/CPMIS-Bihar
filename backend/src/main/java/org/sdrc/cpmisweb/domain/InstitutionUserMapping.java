package org.sdrc.cpmisweb.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.sdrc.usermgmt.domain.Account;

import lombok.Data;

@Entity
@Data
@Table(name="institution_user_mapping")
public class InstitutionUserMapping {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="institution_user_mapping_id")
	private Integer institutionUserMappingId;
		
	@OneToOne
	@JoinColumn(name="institution_id_fk", nullable = false)
	private InstitutionDetails institutionId;
	
	@OneToOne
	@JoinColumn(name = "user_id_fk", nullable = false)
	private Account userId;
	
	@ManyToOne
	@JoinColumn(name="area_id_fk")
	private Area area;
	
	@Column(name = "created_date")
	private Timestamp createdDate;

}