/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 22-Aug-2019
 */
package org.sdrc.cpmisweb.service;

import java.util.List;

import org.json.simple.JSONObject;
import org.sdrc.usermgmt.domain.Account;

public interface CustomUserManagementService {

	List<JSONObject> getAllUsersTypes();
	
	List<Account> getUsersByRoleId(Integer roleId);

	List<JSONObject> execute(String query) throws Exception;
}
