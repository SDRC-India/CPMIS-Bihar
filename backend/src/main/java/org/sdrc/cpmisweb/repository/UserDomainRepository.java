package org.sdrc.cpmisweb.repository;

import java.util.List;

import org.sdrc.cpmisweb.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDomainRepository extends JpaRepository<UserDomain, Integer>{

	UserDomain findByUserTypeName(String userTypeName);
	
	UserDomain findByUserTypeId(Integer typeId);

	List<UserDomain> findAllByOrderByUserTypeId();
}
