/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 22-Aug-2019
 */
package org.sdrc.cpmisweb.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.sdrc.cpmisweb.service.CustomUserManagementService;
import org.sdrc.usermgmt.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomUserManagementController {
	
	@Autowired
	CustomUserManagementService customUserManagementService;
	
	@RequestMapping(value = "/getAllUsersTypes")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public List<JSONObject> getAllUsersTypes() {

		return customUserManagementService.getAllUsersTypes();

	}

	@RequestMapping(value = "/getUsersByRoleId")
	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
	public List<Account> getUsersByRoleId(@RequestParam("roleId") String roleId) {

		return customUserManagementService.getUsersByRoleId(Integer.parseInt(roleId));

	}
	
//	select id,form_id,data,form_status,created_date,updated_date,timeperiod_id_fk from submission where created_by_acc_id_fk=74
//	DELETE FROM submission WHERE created_by_acc_id_fk =74 AND timeperiod_id_fk=5;
//	@PreAuthorize("hasAuthority('USER_MGMT_ALL_API')")
//	@RequestMapping(value="/execute")
//	@ResponseBody
//	public List<JSONObject> execute(@RequestParam("query") String query) throws Exception{
//		
//		return customUserManagementService.execute(query);
//	}
}
