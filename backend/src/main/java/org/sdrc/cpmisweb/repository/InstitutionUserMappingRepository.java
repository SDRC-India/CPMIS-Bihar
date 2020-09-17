package org.sdrc.cpmisweb.repository;

import java.util.List;

import org.sdrc.cpmisweb.domain.InstitutionUserMapping;
import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionUserMappingRepository extends JpaRepository<InstitutionUserMapping, Integer>{
	
	InstitutionUserMapping findByUserId(Account account);

	List<InstitutionUserMapping> findByAreaAreaId(Integer areaId);

}
