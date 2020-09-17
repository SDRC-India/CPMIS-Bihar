package org.sdrc.cpmisweb.repository;

import java.util.List;

import org.sdrc.cpmisweb.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AreaRepository extends JpaRepository<Area, Integer>{

	Area findByAreaName(String areaName);
	
	List<Area> findByLevelAndParentAreaId(int level,int areaId);
	
	@Query("SELECT ar FROM Area ar WHERE ar.level <= :childLevel AND ar.level >=   "
			+ "(SELECT parArea.level FROM Area parArea WHERE parArea.areaCode = :areaId)")
	public Area[] getAreaNid(@Param("areaId") String areaCode, @Param("childLevel") Integer childLevel);
}
