package org.sdrc.cpmisweb.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;

@Service
public class DcpuServiceImpl implements DcpuService {

	@Override
	public String findPreviousMonthData(String columnName, JsonNode node) {
		String fieldValue = "";
		switch (columnName) {
		case "b1OrphansBoysYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansBoysYearsBelow6").asText();
			break;
		case "b1OrphansGirlsYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansGirlsYearsBelow6").asText();
			break;
		case "b1OrphansTotalYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansTotalYearsBelow6").asText();
			break;
		case "b1OrphansBoysYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansBoysYears6to18").asText();
			break;
		case "b1OrphansGirlsYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansGirlsYears6to18").asText();
			break;
		case "b1OrphansTotalYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansTotalYears6to18").asText();
			break;
		case "b1OrphansBoysTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansBoysTotal").asText();
			break;
		case "b1OrphansGirlsTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansGirlsTotal").asText();
			break;
		case "b1OrphansTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(0).get("b5OrphansTotal").asText();
			break;
		case "b1HIVBoysYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVBoysYearsBelow6").asText();
			break;
		case "b1HIVGirlsYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVGirlsYearsBelow6").asText();
			break;
		case "b1HIVTotalYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVTotalYearsBelow6").asText();
			break;
		case "b1HIVBoysYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVBoysYears6to18").asText();
			break;
		case "b1HIVGirlsYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVGirlsYears6to18").asText();
			break;
		case "b1HIVTotalYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVTotalYears6to18").asText();
			break;
		case "b1HIVBoysTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVBoysTotal").asText();
			break;
		case "b1HIVGirlsTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVGirlsTotal").asText();
			break;
		case "b1HIVTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(1).get("b5HIVTotal").asText();
			break;
		case "b1LeprosyBoysYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyBoysYearsBelow6").asText();
			break;
		case "b1LeprosyGirlsYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyGirlsYearsBelow6").asText();
			break;
		case "b1LeprosyTotalYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyTotalYearsBelow6").asText();
			break;
		case "b1LeprosyBoysYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyBoysYears6to18").asText();
			break;
		case "b1LeprosyGirlsYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyGirlsYears6to18").asText();
			break;
		case "b1LeprosyTotalYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyTotalYears6to18").asText();
			break;
		case "b1LeprosyBoysTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyBoysTotal").asText();
			break;
		case "b1LeprosyGirlsTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyGirlsTotal").asText();
			break;
		case "b1LeprosyTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(2).get("b5LeprosyTotal").asText();
			break;
		case "b1TotalBoysYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5TotalBoysYearsBelow6").asText();
			break;
		case "b1TotalGirlsYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5TotalGirlsYearsBelow6").asText();
			break;
		case "b1TotalTotalYearsBelow6":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5TotalTotalYearsBelow6").asText();
			break;
		case "b1TotalBoysYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5TotalBoysYears6to18").asText();
			break;
		case "b1TotalGirlsYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5TotalGirlsYears6to18").asText();
			break;
		case "b1TotalTotalYears6to18":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5TotalTotalYears6to18").asText();
			break;
		case "b1TotalBoysTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5TotalBoysTotal").asText();
			break;
		case "b1TotalGirlsTotal":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5TotalGirlsTotal").asText();
			break;
		case "b1Total":
			fieldValue = node.get("f9tbl_b5_dt").get(3).get("b5Total").asText();
			break;
		case"b6a":
			fieldValue = node.get("f9tbl_b6_dt").get(3).get("b6d").asText();
			break;
		case"c1":
			fieldValue = node.get("f9tbl_c1_dt").get(2).get("c3").asText();
			break;
		case"c4a":
			fieldValue = node.get("f9tbl_c2_dt").get(3).get("c4d").asText();
			break;
			
		case"d1BoysYears0to6":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5BoysYears0to6").asText();
			break;
		case"d1BoysYears7to11":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5BoysYears7to11").asText();
			break;
		case"d1BoysYears12to15":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5BoysYears12to15").asText();
			break;
		case"d1BoysYears16to18":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5BoysYears16to18").asText();
			break;
		case"d1BoysTotal":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5BoysTotal").asText();
			break;
		case"d1GirlsYears0to6":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5GirlsYears0to6").asText();
			break;
		case"d1GirlsYears7to11":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5GirlsYears7to11").asText();
			break;
		case"d1GirlsYears12to15":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5GirlsYears12to15").asText();
			break;
		case"d1GirlsYears16to18":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5GirlsYears16to18").asText();
			break;
		case"d1GirlsTotal":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5GirlsTotal").asText();
			break;
		case"d1GrandTotal":
			fieldValue = node.get("f9tbl_d1_dt").get(4).get("d5GrandTotal").asText();
			break;
			
		case"e1BoysYears16to18":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5BoysYears16to18").asText();
			break;
		case"e1BoysYears19to21":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5BoysYears19to21").asText();
			break;
		case"e1BoysYearsGreaterThan21":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5BoysYearsGreaterThan21").asText();
			break;
		case"e1BoysTotal":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5BoysTotal").asText();
			break;
		case"e1GirlsYears16to18":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5GirlsYears16to18").asText();
			break;
		case"e1GirlsYears19to21":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5GirlsYears19to21").asText();
			break;
		case"e1GirlsYearsGreaterThan21":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5GirlsYearsGreaterThan21").asText();
			break;
		case"e1GirlsTotal":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5GirlsTotal").asText();
			break;
		case"e1GrandTotal":
			fieldValue = node.get("f9tbl_e1_dt").get(4).get("e5GrandTotal").asText();
			break;
			
		case"f1BoysYears0to6":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5BoysYears0to6").asText();
			break;
		case"f1BoysYears7to11":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5BoysYears7to11").asText();
			break;
		case"f1BoysYears12to15":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5BoysYears12to15").asText();
			break;
		case"f1BoysYears16to18":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5BoysYears16to18").asText();
			break;
		case"f1BoysTotal":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5BoysTotal").asText();
			break;
		case"f1GirlsYears0to6":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5GirlsYears0to6").asText();
			break;
		case"f1GirlsYears7to11":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5GirlsYears7to11").asText();
			break;
		case"f1GirlsYears12to15":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5GirlsYears12to15").asText();
			break;
		case"f1GirlsYears16to18":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5GirlsYears16to18").asText();
			break;
		case"f1GirlsTotal":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5GirlsTotal").asText();
			break;
		case"f1GrandTotal":
			fieldValue = node.get("f9tbl_f1_dt").get(4).get("f5GrandTotal").asText();
			break;
			
		case"g1aCases":
			fieldValue = node.get("f9tbl_g1_dt").get(4).get("g3Cases").asText();
			break;
		case"g1aBoys":
			fieldValue = node.get("f9tbl_g1_dt").get(4).get("g3Boys").asText();
			break;
		case"g1aGirls":
			fieldValue = node.get("f9tbl_g1_dt").get(4).get("g3Girls").asText();
			break;
		case"g1aTotalCICL":
			fieldValue = node.get("f9tbl_g1_dt").get(4).get("g3TotalCICL").asText();
			break;
			
		case"g4a":
			fieldValue = node.get("f9tbl_g2_dt").get(3).get("g4d").asText();
			break;
			
		case"h1aCases":
			fieldValue = node.get("f9tbl_h1_dt").get(4).get("h3Cases").asText();
			break;
		case"h1aBoys":
			fieldValue = node.get("f9tbl_h1_dt").get(4).get("h3Boys").asText();
			break;
		case"h1aGirls":
			fieldValue = node.get("f9tbl_h1_dt").get(4).get("h3Girls").asText();
			break;
		case"h1aTotalCICL":
			fieldValue = node.get("f9tbl_h1_dt").get(4).get("h3TotalCICL").asText();
			break;
			
		case"h4a":
			fieldValue = node.get("f9tbl_h2_dt").get(3).get("h4d").asText();
			break;
			
		case"j1aCases":
			fieldValue = node.get("f9tbl_J1_dt").get(4).get("j3Cases").asText();
			break;
		case"j1aBoys":
			fieldValue = node.get("f9tbl_J1_dt").get(4).get("j3Boys").asText();
			break;
		case"j1aGirls":
			fieldValue = node.get("f9tbl_J1_dt").get(4).get("j3Girls").asText();
			break;
		case"j1aTotalCICL":
			fieldValue = node.get("f9tbl_J1_dt").get(4).get("j3TotalCICL").asText();
			break;
			
		case"l1iaBoys":
			fieldValue = node.get("f9tbl_L1_dt").get(4).get("l1ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l1iaGirls":
			fieldValue = node.get("f9tbl_L1_dt").get(4).get("l1ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l1iaTotalCNCP":
			fieldValue = node.get("f9tbl_L1_dt").get(4).get("l1ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;


		case"l1iiaEcost":
			fieldValue = node.get("f9tbl_L2_dt").get(3).get("l1iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l1iiaPcost":
			fieldValue = node.get("f9tbl_L2_dt").get(3).get("l1iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l1iiaTotalCost":
			fieldValue = node.get("f9tbl_L2_dt").get(3).get("l1iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l2iaBoys":
			fieldValue = node.get("f9tbl_L3_dt").get(4).get("l2ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l2iaGirls":
			fieldValue = node.get("f9tbl_L3_dt").get(4).get("l2ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l2iaTotalCNCP":
			fieldValue = node.get("f9tbl_L3_dt").get(4).get("l2ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l2iiaEcost":
			fieldValue = node.get("f9tbl_L4_dt").get(3).get("l2iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l2iiaPcost":
			fieldValue = node.get("f9tbl_L4_dt").get(3).get("l2iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l2iiaTotalCost":
			fieldValue = node.get("f9tbl_L4_dt").get(3).get("l2iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l3iaBoys":
			fieldValue = node.get("f9tbl_L5_dt").get(4).get("l3ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l3iaGirls":
			fieldValue = node.get("f9tbl_L5_dt").get(4).get("l3ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l3iaTotalCNCP":
			fieldValue = node.get("f9tbl_L5_dt").get(4).get("l3ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l3iiaEcost":
			fieldValue = node.get("f9tbl_L6_dt").get(3).get("l3iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l3iiaPcost":
			fieldValue = node.get("f9tbl_L6_dt").get(3).get("l3iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l3iiaTotalCost":
			fieldValue = node.get("f9tbl_L6_dt").get(3).get("l3iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l4iaChildrenOnFirstDay":
			fieldValue = node.get("f9tbl_L7_dt").get(4).get("l4ieTotalChildrenLastday").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l4iiaEcost":
			fieldValue = node.get("f9tbl_L8_dt").get(3).get("l4iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l4iiaPcost":
			fieldValue = node.get("f9tbl_L8_dt").get(3).get("l4iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l4iiaTotalCost":
			fieldValue = node.get("f9tbl_L8_dt").get(3).get("l4iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l5iaChildrenFirstDay":
			fieldValue = node.get("f9tbl_L9_dt").get(4).get("l5ieTotalChildrenOnLastday").asText();
			if(fieldValue=="null") fieldValue="0";
			break;

		case"l5iiaEcost":
			fieldValue = node.get("f9tbl_L10_dt").get(3).get("l5iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l5iiaPcost":
			fieldValue = node.get("f9tbl_L10_dt").get(3).get("l5iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l5iiaTotalCost":
			fieldValue = node.get("f9tbl_L10_dt").get(3).get("l5iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l6iaBoys":
			fieldValue = node.get("f9tbl_L11_dt").get(4).get("l6ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l6iaGirls":
			fieldValue = node.get("f9tbl_L11_dt").get(4).get("l6ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l6iaTotalCNCP":
			fieldValue = node.get("f9tbl_L11_dt").get(4).get("l6ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l6iiaEcost":
			fieldValue = node.get("f9tbl_L12_dt").get(3).get("l6iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l6iiaPcost":
			fieldValue = node.get("f9tbl_L12_dt").get(3).get("l6iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l6iiaTotalCost":
			fieldValue = node.get("f9tbl_L12_dt").get(3).get("l6iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l7iaBoys":
			fieldValue = node.get("f9tbl_L13_dt").get(4).get("l7ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l7iaGirls":
			fieldValue = node.get("f9tbl_L13_dt").get(4).get("l7ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l7iaTotalCNCP":
			fieldValue = node.get("f9tbl_L13_dt").get(4).get("l7ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l7iiaEcost":
			fieldValue = node.get("f9tbl_L14_dt").get(3).get("l7iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l7iiaPcost":
			fieldValue = node.get("f9tbl_L14_dt").get(3).get("l7iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l7iiaTotalCost":
			fieldValue = node.get("f9tbl_L14_dt").get(3).get("l7iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l8iaBoys":
			fieldValue = node.get("f9tbl_L15_dt").get(4).get("l8ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l8iaGirls":
			fieldValue = node.get("f9tbl_L15_dt").get(4).get("l8ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l8iaTotalCNCP":
			fieldValue = node.get("f9tbl_L15_dt").get(4).get("l8ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l8iiaEcost":
			fieldValue = node.get("f9tbl_L16_dt").get(3).get("l8iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l8iiaPcost":
			fieldValue = node.get("f9tbl_L16_dt").get(3).get("l8iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l8iiaTotalCost":
			fieldValue = node.get("f9tbl_L16_dt").get(3).get("l8iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l9iaBoys":
			fieldValue = node.get("f9tbl_L17_dt").get(4).get("l9ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l9iaGirls":
			fieldValue = node.get("f9tbl_L17_dt").get(4).get("l9ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l9iaTotalCNCP":
			fieldValue = node.get("f9tbl_L17_dt").get(4).get("l9ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l9iiaEcost":
			fieldValue = node.get("f9tbl_L18_dt").get(3).get("l9iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l9iiaPcost":
			fieldValue = node.get("f9tbl_L18_dt").get(3).get("l9iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l9iiaTotalCost":
			fieldValue = node.get("f9tbl_L18_dt").get(3).get("l9iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;

		case"l10iaChildrenOnFirstDay":
			fieldValue = node.get("f9tbl_L19_dt").get(4).get("l10ieTotalChildrenLastday").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l10iiaEcost":
			fieldValue = node.get("f9tbl_L20_dt").get(3).get("l10iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l10iiaPcost":
			fieldValue = node.get("f9tbl_L20_dt").get(3).get("l10iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l10iiaTotalCost":
			fieldValue = node.get("f9tbl_L20_dt").get(3).get("l10iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l11iaChildrenFirstDay":
			fieldValue = node.get("f9tbl_L21_dt").get(4).get("l11ieTotalChildrenOnLastday").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l11iiaEcost":
			fieldValue = node.get("f9tbl_L22_dt").get(3).get("l11iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l11iiaPcost":
			fieldValue = node.get("f9tbl_L22_dt").get(3).get("l11iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l11iiaTotalCost":
			fieldValue = node.get("f9tbl_L22_dt").get(3).get("l11iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l12iaBoys":
			fieldValue = node.get("f9tbl_L23_dt").get(4).get("l12ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l12iaGirls":
			fieldValue = node.get("f9tbl_L23_dt").get(4).get("l12ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l12iaTotalCNCP":
			fieldValue = node.get("f9tbl_L23_dt").get(4).get("l12ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l12iiaEcost":
			fieldValue = node.get("f9tbl_L24_dt").get(3).get("l12iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l12iiaPcost":
			fieldValue = node.get("f9tbl_L24_dt").get(3).get("l12iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l12iiaTotalCost":
			fieldValue = node.get("f9tbl_L24_dt").get(3).get("l12iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l13iaBoys":
			fieldValue = node.get("f9tbl_L25_dt").get(4).get("l13ieBoys").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l13iaGirls":
			fieldValue = node.get("f9tbl_L25_dt").get(4).get("l13ieGirls").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l13iaTotalCNCP":
			fieldValue = node.get("f9tbl_L25_dt").get(4).get("l13ieTotalCNCP").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
			
		case"l13iiaEcost":
			fieldValue = node.get("f9tbl_L26_dt").get(3).get("l13iidEcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l13iiaPcost":
			fieldValue = node.get("f9tbl_L26_dt").get(3).get("l13iidPcost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"l13iiaTotalCost":
			fieldValue = node.get("f9tbl_L26_dt").get(3).get("l13iidTotalCost").asText();
			if(fieldValue=="null") fieldValue="0";
			break;
		case"m23aEcost":
			fieldValue = node.get("f9tbl_m7_dt").get(3).get("m23dEcost").asText();
			break;
		case"m23aPcost":
			fieldValue = node.get("f9tbl_m7_dt").get(3).get("m23dPcost").asText();
			break;
		case"m23aTotalCost":
			fieldValue = node.get("f9tbl_m7_dt").get(3).get("m23dTotalCost").asText();
			break;
			
		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
	}

	@Override
	public Integer findPreviousMonthOptionData(QuestionModel questionModel, Question question, JsonNode node, Map<String, String> typeDetailsMap) {
		Integer fieldValue = null;
		switch (question.getColumnName()) {
		case "govtHomesAvailable":
			for(int i = 0; i < questionModel.getOptions().size(); i++){
				if(questionModel.getOptions().get(i).getKey() == node.get("govtHomesAvailable").asInt()){
					questionModel.getOptions().get(i).setIsSelected(true);
					questionModel.getOptions().get(i).setValue(typeDetailsMap.get(node.get("govtHomesAvailable").asText()));
				}
			}
			fieldValue = node.get("govtHomesAvailable").asInt();
			break;
		case "ngoHomesAvailable":
			for(int i = 0; i < questionModel.getOptions().size(); i++){
				if(questionModel.getOptions().get(i).getKey() == node.get("ngoHomesAvailable").asInt()){
					questionModel.getOptions().get(i).setIsSelected(true);
					questionModel.getOptions().get(i).setValue(typeDetailsMap.get(node.get("ngoHomesAvailable").asText()));
				}
			}
			fieldValue = node.get("ngoHomesAvailable").asInt();
			break;
		default:
			break;
		}
			
		return fieldValue;
	}

	@Override
	public Integer[] findPreviousMonthCheckboxData(QuestionModel questionModel, Question question, JsonNode node) {
		Integer[] checkboxFieldValues = new Integer[0];
		
		switch (question.getColumnName()) {
		case "govtHomesList":
			checkboxFieldValues = new Integer[node.get("govtHomesList").size()];
			for(int i = 0; i < questionModel.getOptions().size(); i++){
				for(int j = 0; j < node.get("govtHomesList").size(); j++){
					if(questionModel.getOptions().get(i).getKey() == node.get("govtHomesList").get(j).asInt()){
						questionModel.getOptions().get(i).setIsSelected(true);
						checkboxFieldValues[j] = node.get("govtHomesList").get(j).asInt();
					}
				}
			}
			break;
		case "ngoHomesList":
			checkboxFieldValues = new Integer[node.get("ngoHomesList").size()];
			for(int i = 0; i < questionModel.getOptions().size(); i++){
				for(int j = 0; j < node.get("ngoHomesList").size(); j++){
					if(questionModel.getOptions().get(i).getKey() == node.get("ngoHomesList").get(j).asInt()){
						questionModel.getOptions().get(i).setIsSelected(true);
						checkboxFieldValues[j] = node.get("ngoHomesList").get(j).asInt();
					}
				}
			}
			break;
		default:
			break;
		}
		
		return checkboxFieldValues;
	}

}
