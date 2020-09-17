package org.sdrc.cpmisweb.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.sdrc.cpmisweb.model.FormStatus;
import org.sdrc.usermgmt.domain.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

/**
 * @author Subham Ashish (subham@sdrc.co.in)
 *
 */
@Entity
@Table(name = "facility_submission")
@TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class)
public class FacilitySubmission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb")
	private JsonNode data;

	@Column(nullable=false)
	private Integer formId;

	private Date createdDate;

	private Date updatedDate;

	private String uniqueId;

	private Date submissionDate;

	@ManyToOne
	@JoinColumn(name = "created_by_acc_id_fk")
	@JsonIgnore
	private Account createdBy;

	@Version
	private Integer version;

	@Enumerated(EnumType.STRING)
	@Column
	private FormStatus formStatus;

	@Column(name = "is_active")
	private boolean isActive;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JsonNode getData() {
		return data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Account getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Account createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public FormStatus getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(FormStatus formStatus) {
		this.formStatus = formStatus;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
