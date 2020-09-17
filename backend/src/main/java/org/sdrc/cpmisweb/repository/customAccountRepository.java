package org.sdrc.cpmisweb.repository;

import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface customAccountRepository extends JpaRepository<Account, Integer> {

	Account findByUserName(String userName);
}
