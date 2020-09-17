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
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Area {

	@Id
	@Column(name = "area_id")
	private Integer areaId;

	@Column(name = "area_name")
	private String areaName;

	@Column(name = "parent_area_id")
	private int parentAreaId;

	@Column(name = "level")
	private int level;

	@Column(name = "created_date")
	private Timestamp createdDate;
	
	@Column(name = "updated_date")
	private Timestamp updatedDate;

	@Column(name = "is_live")
	private boolean isLive;

	@Column(name = "area_code")
	private String areaCode;

	@Column(name = "short_name")
	private String shortName;
	
	@OneToMany(mappedBy="area", fetch=FetchType.LAZY)
	private List<AccountDetails> cpmisUserDetails;

	public Area() {
		super();
	}
}
