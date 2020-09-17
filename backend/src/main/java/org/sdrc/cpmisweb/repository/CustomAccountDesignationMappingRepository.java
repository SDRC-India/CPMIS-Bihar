package org.sdrc.cpmisweb.repository;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomAccountDesignationMappingRepository extends JpaRepository<AccountDesignationMapping, Integer>{

	AccountDesignationMapping findByAccountAndDesignation(Account acc, Designation deg);
}
