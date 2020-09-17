package org.sdrc.cpmisweb.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="area_level")
public class AreaLevel {

	@Id
	@Column(name = "area_level")
	private int areaLevel;
	
	@Column(name = "area_level_name", nullable = false)
	private String areaLevelName;
}
