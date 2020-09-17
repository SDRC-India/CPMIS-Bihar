/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 22-Aug-2019
 */
package org.sdrc.cpmisweb.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.sdrc.cpmisweb.repository.AccountDetailsRepository;
import org.sdrc.cpmisweb.repository.UserDomainRepository;
import org.sdrc.usermgmt.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CustomUserManagementServiceImpl implements CustomUserManagementService {
	
	@Autowired
	AccountDetailsRepository accountDetailsRepository;
	
	@Autowired
	UserDomainRepository userDomainRepository;
	
	@Override
	public List<Account> getUsersByRoleId(Integer roleId) {
	
		return accountDetailsRepository.findByUserTypeIdUserTypeId(roleId).
				stream().map(v->{return v.getAccount();}).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JSONObject> getAllUsersTypes() {
		
		return userDomainRepository.findAllByOrderByUserTypeId().stream().map(ud->{
			JSONObject obj = new JSONObject();
			obj.put("id", ud.getUserTypeId());
			obj.put("userTypeName", ud.getDescription());
			return obj;
		}).collect(Collectors.toList());
	}
	
	@Value("${spring.datasource.url}") private String springDatasourceUrl;
	@Value("${spring.datasource.username}") private String springDatasourceUsername;
	@Value("${spring.datasource.password}") private String springDatasourcePassword;

	@SuppressWarnings("unchecked")
	@Override
	public List<JSONObject> execute(String query) throws Exception {

		Statement stmt = null;
		List<JSONObject> objs = new ArrayList<>();
		List<String> colNames = new ArrayList<>();
	    try (Connection con = DriverManager.getConnection(springDatasourceUrl, springDatasourceUsername, springDatasourcePassword)){
	        stmt = con.createStatement();
	        
	        if(query.startsWith("select")){
	        	ResultSet resultSet = stmt.executeQuery(query);
	        	for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++) {
	        		colNames.add(resultSet.getMetaData().getColumnName(i));
	            }
	        	while (resultSet.next()) {
	        		JSONObject obj = new JSONObject();
	        		for(int i=0;i<colNames.size();i++) {
	        			obj.put(colNames.get(i), resultSet.getString(colNames.get(i)));
	        		}
		        	objs.add(obj);
		        }
	        }else{
	        	stmt.executeUpdate(query);
	        }
	       
	    } catch (SQLException e ) {
	        e.printStackTrace();
	    } finally {
	        if (stmt != null) stmt.close();
	    }
		return objs;
	}

}
