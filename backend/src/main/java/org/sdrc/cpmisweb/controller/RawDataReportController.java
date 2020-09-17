/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 24-Jul-2019
 */
package org.sdrc.cpmisweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.cpmisweb.service.RawDataReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.sdrcdatacollector.models.MessageModel;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class RawDataReportController {
	@Autowired
	private RawDataReportService rawDataReportService;
	
	@RequestMapping(value = "/exportRawData")
	@ResponseBody
	public ResponseEntity<MessageModel> exportRawaData(@RequestParam("formId") Integer formId, Integer timeperiodId,Principal principal) {

		return rawDataReportService.exportRawaData(formId, timeperiodId, principal, null);

	}
	
	@PostMapping("/downloadRawReport")
	public void downLoad(@RequestParam("fileName") String name, HttpServletResponse response) throws IOException {

		String fileName = name.replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%2C", ",")
				.replaceAll("\\+", " ").replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%5C", "/");
		try(InputStream inputStream = new FileInputStream(fileName)) {
			
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new java.io.File(fileName).getName());
			response.setHeader(headerKey, headerValue);
			response.setContentType("application/octet-stream");
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			outputStream.flush();
			outputStream.close();
			Files.delete(Paths.get(fileName));

		} catch (IOException e) {
			log.error("error-while downloading raw data report with payload : {}", name, e);
		}
	}

}
