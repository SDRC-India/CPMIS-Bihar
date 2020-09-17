/**
 * @author Pratyush(pratyush@sdrc.co.in)
 * created on 16-Apr-2019
 */
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

import org.sdrc.usermgmt.domain.Account;

import lombok.Data;

@Data
@Entity 
@Table(name="account_details")
public class AccountDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acc_details_id")
	private Integer accountDetailsId;
	
	@OneToOne
	@JoinColumn(name="acc_id_fk")
	private Account account;
	
	@ManyToOne
	@JoinColumn(name = "user_type_id", nullable = false)
	private UserDomain userTypeId;
	
	@ManyToOne
	@JoinColumn(name="district_id")
	private Area area;
	
	@Column(name = "sjpu_access")
	private Boolean sjpuAccess;
	
	@ManyToOne
	@JoinColumn(name="data_entry_start_timeperiod_id_fk")
	private Timeperiod dataEntryStartTimeperiod;

}
