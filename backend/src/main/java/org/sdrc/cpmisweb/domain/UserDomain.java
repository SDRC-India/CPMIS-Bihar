/**
 * @author Pratyush(pratyush@sdrc.co.in)
 * created on 16-Apr-2019
 */
package org.sdrc.cpmisweb.domain;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="user_type_view")
@Data
public class UserDomain {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_type_id")
	private Integer userTypeId;
	
	@Column(name="user_type_name")
	private String userTypeName;
	
	@Column(name="screen_name")
	private String screenName;
	
	@OneToMany(mappedBy = "userTypeId", fetch = FetchType.LAZY)
	private List<AccountDetails> accountDetails;

	@Column(name="description")
	private String description;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "last_updated_date")
	private Timestamp lastUpdatedDate;
}
