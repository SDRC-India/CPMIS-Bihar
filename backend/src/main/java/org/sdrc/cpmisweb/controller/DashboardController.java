/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 31-Jul-2019
 */
package org.sdrc.cpmisweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.sdrc.cpmisweb.model.DataCollectionModel;
import org.sdrc.cpmisweb.model.LineSeries;
import org.sdrc.cpmisweb.model.ValueObject;
import org.sdrc.cpmisweb.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@PreAuthorize(value="hasAuthority('DASHBOARD_VIEW')")
public class DashboardController {
	
	@Autowired DashboardService dashboardService;

	@GetMapping("/getSectors")
	public JSONArray fetchAllSectors(){
		return dashboardService.fetchSectorsForDashboard();
	}
	
	@GetMapping("/indicators")
	public List<ValueObject> fetchIndicators(@RequestParam(required = false) String sector){
		List<ValueObject> valueObjects = new ArrayList<>();
		if (sector != null) {
			valueObjects = dashboardService.fetchIndicators(sector);
		}
		return valueObjects;
	}
	
//	@GetMapping("/sources")
//	public List<ValueObject> fetchSources(@RequestParam(required = false) String iusnid){
//		List<ValueObject> valueObjects = new ArrayList<>();
//		if (iusnid != null) {
//			valueObjects = dashboardService.fetchSources(iusnid);
//		}
//		return valueObjects;
//	}
	
	@GetMapping("/timeperiod")
	public List<ValueObject> fetchUtTimeperiod(@RequestParam String sourceNid) throws Exception {

		List<ValueObject> valueObjects = new ArrayList<>();
		try {
			if (sourceNid != null) {
				valueObjects = dashboardService.fetchUtTimeperiod(Integer.parseInt(sourceNid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return valueObjects;
	}
	
	@GetMapping("/data")
	public DataCollectionModel fetchData(@RequestParam(required = false) String indicatorId, @RequestParam(required = false) String sourceNid, @RequestParam(required = false) String areaId, @RequestParam(required = false) String timeperiodId, @RequestParam(required = false) Integer childLevel) throws Exception {

		DataCollectionModel valList = new DataCollectionModel();
		if (indicatorId != null && sourceNid != null && timeperiodId != null) {
			valList = dashboardService.fetchData(indicatorId, sourceNid, areaId, timeperiodId, childLevel);
		}
		return valList;
	}
	
	@GetMapping("/lineData")
	@ResponseBody
	public List<List<LineSeries>> fetchLineData(@RequestParam(required = false) Integer iusNid, @RequestParam(required = false) Integer areaNid) throws Exception {

		return dashboardService.fetchChartData(iusNid, areaNid);
	}
	
	@PostMapping("/exportPDF")
	public String exportPDF(@RequestBody List<String> svgs, @RequestParam(required = false) String indicatorId, @RequestParam(required = false) String sourceNid, @RequestParam(required = false) String areaId, @RequestParam(required = false) String timeperiodId, @RequestParam(required = false) Integer childLevel) {
		return dashboardService.exportPDF(svgs, indicatorId, sourceNid, areaId, timeperiodId, childLevel);
	}
	
	@PostMapping("/exportLineChart")
	@ResponseBody
	public String exportToPDFLine(@RequestBody List<String> svgs, @RequestParam(value = "iusNid", required = false) Integer iusNid, @RequestParam(value = "areaNid", required = false) Integer areaNid, HttpServletRequest re) {

		return dashboardService.exportPDFLine(svgs, iusNid, areaNid);
	}
	
	@PostMapping(value = "/downloadPDF")
	public void downLoad(@RequestParam("fileName") String name, HttpServletResponse response) throws IOException {
		InputStream inputStream;
		String fileName = "";
		try {
			fileName = name.replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%5C", "/").replaceAll("%2C", ",").replaceAll("\\+", " ").replaceAll("%22", "").replaceAll("%3F", "?").replaceAll("%3D", "=");
			inputStream = new FileInputStream(fileName);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", new java.io.File(fileName).getName());
			response.setHeader(headerKey, headerValue);
			response.setContentType("application/octet-stream"); // for all file
																	// type
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			new File(fileName).delete();
		}
	}
}
