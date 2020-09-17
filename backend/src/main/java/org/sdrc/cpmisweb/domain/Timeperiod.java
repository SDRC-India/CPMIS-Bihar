package org.sdrc.cpmisweb.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@Table(name = "time_period",indexes = { @Index(name = "i_timeperiod", columnList = "timeperiod") },
	   uniqueConstraints={@UniqueConstraint(name="unq_startdate_enddate_periodicity_timeperiod",
	   columnNames={"start_date","end_date","periodicity","timeperiod"})})
public class Timeperiod implements Serializable{

	private static final long serialVersionUID = -2468070725966527580L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "timeperiod_id")
	private Integer timeperiodId;

	@Column(name = "timeperiod")
	private String timePeriod;

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@Column(name = "periodicity")
	private Integer periodicity;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdeDate;

    @PrePersist
    private void onCreate() {
    	createdeDate = new Date();
    }
	
	@ManyToOne
	@JoinColumn(name="quarter_id")
	private Timeperiod quarter;
	
	@JsonIgnore
	@OneToMany(mappedBy="quarter")
	private List<Timeperiod> months;
}
