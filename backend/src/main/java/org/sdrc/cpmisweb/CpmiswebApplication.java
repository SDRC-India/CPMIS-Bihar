package org.sdrc.cpmisweb;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.sdrc.cpmisweb.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = { "org.sdrc.usermgmt.core", "org.sdrc.cpmisweb", "in.co.sdrc.sdrcdatacollector" })
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan(basePackages = {"org.sdrc.cpmisweb.domain", "in.co.sdrc.sdrcdatacollector.jpadomains"})
@EnableJpaRepositories(basePackages = { "org.sdrc.cpmisweb.repository", "in.co.sdrc.sdrcdatacollector.jparepositories" })
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class,MongoRepositoriesAutoConfiguration.class,MongoDataAutoConfiguration.class})
public class CpmiswebApplication {
	
	@Autowired
	private Environment environment;
		
	private static final Logger log = LoggerFactory.getLogger(CpmiswebApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CpmiswebApplication.class, args);
	}
	
	@PostConstruct
	public void init(){
		try {
			if(!Paths.get(environment.getProperty(Constant.CPMISWEB_ROOT_PATH)).toFile().exists())
				Files.createDirectory(Paths.get(environment.getProperty(Constant.CPMISWEB_ROOT_PATH)));
			
			if(!Paths.get(environment.getProperty(Constant.SUBMISSION_PDF_OUTPUT_PATH)).toFile().exists())
				Files.createDirectory(Paths.get(environment.getProperty(Constant.SUBMISSION_PDF_OUTPUT_PATH)));
			
			if(!Paths.get(environment.getProperty(Constant.RAWDATA_REPORT_PATH)).toFile().exists())
				Files.createDirectory(Paths.get(environment.getProperty(Constant.RAWDATA_REPORT_PATH)));
			
			if(!Paths.get(environment.getProperty(Constant.OUTPUT_PATH_DASHBOARD_PDF)).toFile().exists())
				Files.createDirectory(Paths.get(environment.getProperty(Constant.OUTPUT_PATH_DASHBOARD_PDF)));
			
			if(!Paths.get(environment.getProperty(Constant.OUTPUT_PATH_PDF_LINECHART)).toFile().exists())
				Files.createDirectory(Paths.get(environment.getProperty(Constant.OUTPUT_PATH_PDF_LINECHART)));
			
		} catch (Exception e) {
			log.error("failed to create directories on application startup. ", e);
		}
	}

}
