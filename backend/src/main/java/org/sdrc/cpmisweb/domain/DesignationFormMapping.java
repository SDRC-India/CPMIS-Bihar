package org.sdrc.cpmisweb.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.sdrc.usermgmt.domain.Designation;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;
import lombok.Data;

@Entity
@Data
public class DesignationFormMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "designation_id_fk")
	private Designation designation;

	@ManyToOne
	@JoinColumn(name = "form_id_fk", referencedColumnName="formId")
	private EnginesForm form;

	@CreationTimestamp
	private Date createdDate;

	private AccessType accessType;

	private Status status = Status.ACTIVE;

}
