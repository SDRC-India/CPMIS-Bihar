package org.sdrc.cpmisweb.repository;

import java.util.List;

import org.sdrc.cpmisweb.domain.AccountDetails;
import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails, Integer> {

	AccountDetails findByAccount(Account acc);
	
	AccountDetails findByAccountId(Integer accId);
	
	List<AccountDetails> findByUserTypeIdUserTypeId(Integer userTypeId);
	
}
