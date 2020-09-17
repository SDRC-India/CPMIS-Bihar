/**
 * @author Pratyush(pratyush@sdrc.co.in)
 * created on 17-Apr-2019
 */
package org.sdrc.cpmisweb.security;

import org.springframework.http.ResponseEntity;

public interface ConfigurationService {

public boolean createDesignation();
	
	public boolean createAuthority();
	
	public boolean createDesignationAuthorityMapping();
	
	public boolean createAreaLevel();
	
	public boolean createOtherUser();
	
	public ResponseEntity<String> createAdmin();
	
	public boolean createEngineForms();

	public boolean updatePassword();

	public boolean insertIntoIndicatorSubgroupAndIUS();

	public boolean createStateLevelUsers();

}
