package org.sdrc.cpmisweb.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import in.co.sdrc.sdrcdatacollector.jpadomains.Question;

@Service
public class PrefetchDataServiceImpl implements PrefetchDataService {

	@Override
	public String getCwcPreviousMonthData(Question question, JsonNode node) {
		String fieldValue = "";
		switch (question.getColumnName().trim()) {
		case "a1Boys0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaBoys0to6").asText();
			break;
		case "a1Boys7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaBoys7to11").asText();
			break;
		case "a1Boys12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaBoys12to15").asText();
			break;
		case "a1Boys16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaBoys16to18").asText();
			break;
		case "a1BoysGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaBoysGreaterThan18").asText();
			break;
		case "a1BoysTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaBoysTotal").asText();
			break;
		case "a1Girls0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaGirls0to6").asText();
			break;
		case "a1Girls7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaGirls7to11").asText();
			break;
		case "a1Girls12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaGirls12to15").asText();
			break;
		case "a1Girls16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaGirls16to18").asText();
			break;
		case "a1GirlsGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaGirlsGreaterThan18").asText();
			break;
		case "a1GirlsTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaGirlsTotal").asText();
			break;
		case "a1GrandTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(0).get("eaGrandTotal").asText();
			break;
		case "a2Boys0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebBoys0to6").asText();
			break;
		case "a2Boys7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebBoys7to11").asText();
			break;
		case "a2Boys12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebBoys12to15").asText();
			break;
		case "a2Boys16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebBoys16to18").asText();
			break;
		case "a2BoysGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebBoysGreaterThan18").asText();
			break;
		case "a2BoysTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebBoysTotal").asText();
			break;
		case "a2Girls0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebGirls0to6").asText();
			break;
		case "a2Girls7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebGirls7to11").asText();
			break;
		case "a2Girls12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebGirls12to15").asText();
			break;
		case "a2Girls16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebGirls16to18").asText();
			break;
		case "a2GirlsGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebGirlsGreaterThan18").asText();
			break;
		case "a2GirlsTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebGirlsTotal").asText();
			break;
		case "a2GrandTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(1).get("ebGrandTotal").asText();
			break;
		case "a3Boys0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecBoys0to6").asText();
			break;
		case "a3Boys7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecBoys7to11").asText();
			break;
		case "a3Boys12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecBoys12to15").asText();
			break;
		case "a3Boys16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecBoys16to18").asText();
			break;
		case "a3BoysGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecBoysGreaterThan18").asText();
			break;
		case "a3BoysTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecBoysTotal").asText();
			break;
		case "a3Girls0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecGirls0to6").asText();
			break;
		case "a3Girls7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecGirls7to11").asText();
			break;
		case "a3Girls12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecGirls12to15").asText();
			break;
		case "a3Girls16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecGirls16to18").asText();
			break;
		case "a3GirlsGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecGirlsGreaterThan18").asText();
			break;
		case "a3GirlsTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecGirlsTotal").asText();
			break;
		case "a3GrandTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(2).get("ecGrandTotal").asText();
			break;
		case "a4Boys0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edBoys0to6").asText();
			break;
		case "a4Boys7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edBoys7to11").asText();
			break;
		case "a4Boys12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edBoys12to15").asText();
			break;
		case "a4Boys16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edBoys16to18").asText();
			break;
		case "a4BoysGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edBoysGreaterThan18").asText();
			break;
		case "a4BoysTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edBoysTotal").asText();
			break;
		case "a4Girls0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edGirls0to6").asText();
			break;
		case "a4Girls7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edGirls7to11").asText();
			break;
		case "a4Girls12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edGirls12to15").asText();
			break;
		case "a4Girls16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edGirls16to18").asText();
			break;
		case "a4GirlsGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edGirlsGreaterThan18").asText();
			break;
		case "a4GirlsTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edGirlsTotal").asText();
			break;
		case "a4GrandTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(3).get("edGrandTotal").asText();
			break;
		case "a5Boys0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeBoys0to6").asText();
			break;
		case "a5Boys7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeBoys7to11").asText();
			break;
		case "a5Boys12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeBoys12to15").asText();
			break;
		case "a5Boys16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeBoys16to18").asText();
			break;
		case "a5BoysGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeBoysGreaterThan18").asText();
			break;
		case "a5BoysTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeBoysTotal").asText();
			break;
		case "a5Girls0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeGirls0to6").asText();
			break;
		case "a5Girls7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeGirls7to11").asText();
			break;
		case "a5Girls12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeGirls12to15").asText();
			break;
		case "a5Girls16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeGirls16to18").asText();
			break;
		case "a5GirlsGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeGirlsGreaterThan18").asText();
			break;
		case "a5GirlsTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeGirlsTotal").asText();
			break;
		case "a5GrandTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(4).get("eeGrandTotal").asText();
			break;
		case "a6Boys0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efBoys0to6").asText();
			break;
		case "a6Boys7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efBoys7to11").asText();
			break;
		case "a6Boys12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efBoys12to15").asText();
			break;
		case "a6Boys16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efBoys16to18").asText();
			break;
		case "a6BoysGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efBoysGreaterThan18").asText();
			break;
		case "a6BoysTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efBoysTotal").asText();
			break;
		case "a6Girls0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efGirls0to6").asText();
			break;
		case "a6Girls7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efGirls7to11").asText();
			break;
		case "a6Girls12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efGirls12to15").asText();
			break;
		case "a6Girls16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efGirls16to18").asText();
			break;
		case "a6GirlsGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efGirlsGreaterThan18").asText();
			break;
		case "a6GirlsTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efGirlsTotal").asText();
			break;
		case "a6GrandTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(5).get("efGrandTotal").asText();
			break;
		case "a7Boys0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egBoys0to6").asText();
			break;
		case "a7Boys7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egBoys7to11").asText();
			break;
		case "a7Boys12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egBoys12to15").asText();
			break;
		case "a7Boys16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egBoys16to18").asText();
			break;
		case "a7BoysGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egBoysGreaterThan18").asText();
			break;
		case "a7BoysTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egBoysTotal").asText();
			break;
		case "a7Girls0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egGirls0to6").asText();
			break;
		case "a7Girls7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egGirls7to11").asText();
			break;
		case "a7Girls12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egGirls12to15").asText();
			break;
		case "a7Girls16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egGirls16to18").asText();
			break;
		case "a7GirlsGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egGirlsGreaterThan18").asText();
			break;
		case "a7GirlsTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egGirlsTotal").asText();
			break;
		case "a7GrandTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(6).get("egGrandTotal").asText();
			break;
		case "a8Boys0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eBoys0to6").asText();
			break;
		case "a8Boys7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eBoys7to11").asText();
			break;
		case "a8Boys12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eBoys12to15").asText();
			break;
		case "a8Boys16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eBoys16to18").asText();
			break;
		case "a8BoysGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eBoysGreaterThan18").asText();
			break;
		case "a8BoysTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eBoysTotal").asText();
			break;
		case "a8Girls0to6":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eGirls0to6").asText();
			break;
		case "a8Girls7to11":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eGirls7to11").asText();
			break;
		case "a8Girls12to15":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eGirls12to15").asText();
			break;
		case "a8Girls16to18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eGirls16to18").asText();
			break;
		case "a8GirlsGreaterThan18":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eGirlsGreaterThan18").asText();
			break;
		case "a8GirlsTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eGirlsTotal").asText();
			break;
		case "a8GrandTotal":
			fieldValue = node.get("cwc_tbl_e2_dt").get(7).get("eGrandTotal").asText();
			break;

		case "g1AbandonedBoys":
			fieldValue = node.get("cwc_tbl_g4_dt").get(0).get("g4AbandonedBoys").asText();
			break;
		case "g1AbandonedGirls":
			fieldValue = node.get("cwc_tbl_g4_dt").get(0).get("g4AbandonedGirls").asText();
			break;
		case "g1AbandonedTotal":
			fieldValue = node.get("cwc_tbl_g4_dt").get(0).get("g4AbandonedTotal").asText();
			break;
		case "g1OrphanedBoys":
			fieldValue = node.get("cwc_tbl_g4_dt").get(1).get("g4OrphanedBoys").asText();
			break;
		case "g1OrphanedGirls":
			fieldValue = node.get("cwc_tbl_g4_dt").get(1).get("g4OrphanedGirls").asText();
			break;
		case "g1OrphanedTotal":
			fieldValue = node.get("cwc_tbl_g4_dt").get(1).get("g4OrphanedTotal").asText();
			break;
		case "g1OthersBoys":
			fieldValue = node.get("cwc_tbl_g4_dt").get(2).get("g4OthersBoys").asText();
			break;
		case "g1OthersGirls":
			fieldValue = node.get("cwc_tbl_g4_dt").get(2).get("g4OthersGirls").asText();
			break;
		case "g1OthersTotal":
			fieldValue = node.get("cwc_tbl_g4_dt").get(2).get("g4OthersTotal").asText();
			break;
		case "g1TotalBoys":
			fieldValue = node.get("cwc_tbl_g4_dt").get(3).get("g4TotalBoys").asText();
			break;
		case "g1TotalGirls":
			fieldValue = node.get("cwc_tbl_g4_dt").get(3).get("g4TotalGirls").asText();
			break;
		case "g1Total":
			fieldValue = node.get("cwc_tbl_g4_dt").get(3).get("g4Total").asText();
			break;
		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
	}

	@Override
	public String getJjbPreviousMonthData(Question question, JsonNode node) {
		String fieldValue = "";
		switch (question.getColumnName().trim()) {
		case "a1TotalCases":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1TotalCases").asText();
			break;
		case "a1Boys7to11":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1Boys7to11").asText();
			break;
		case "a1Boys12to15":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1Boys12to15").asText();
			break;
		case "a1Boys16to18":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1Boys16to18").asText();
			break;
		case "a1BoysGreaterThan18":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1BoysGreaterThan18").asText();
			break;
		case "a1BoysTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1BoysTotal").asText();
			break;
		case "a1Girls7to11":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1Girls7to11").asText();
			break;
		case "a1Girls12to15":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1Girls12to15").asText();
			break;
		case "a1Girls16to18":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1Girls16to18").asText();
			break;
		case "a1GirlsGreaterThan18":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1GirlsGreaterThan18").asText();
			break;
		case "a1GirlsTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1GirlsTotal").asText();
			break;
		case "a1GrandTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(0).get("f1GrandTotal").asText();
			break;
		case "a2TotalCases":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2TotalCases").asText();
			break;
		case "a2Boys7to11":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2Boys7to11").asText();
			break;
		case "a2Boys12to15":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2Boys12to15").asText();
			break;
		case "a2Boys16to18":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2Boys16to18").asText();
			break;
		case "a2BoysGreaterThan18":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2BoysGreaterThan18").asText();
			break;
		case "a2BoysTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2BoysTotal").asText();
			break;
		case "a2Girls7to11":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2Girls7to11").asText();
			break;
		case "a2Girls12to15":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2Girls12to15").asText();
			break;
		case "a2Girls16to18":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2Girls16to18").asText();
			break;
		case "a2GirlsGreaterThan18":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2GirlsGreaterThan18").asText();
			break;
		case "a2GirlsTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2GirlsTotal").asText();
			break;
		case "a2GrandTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(1).get("f2GrandTotal").asText();
			break;
		case "aTotalCases":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3TotalCases").asText();
			break;
		case "aBoys7to11":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3Boys7to11").asText();
			break;
		case "aBoys12to15":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3Boys12to15").asText();
			break;
		case "aBoys16to18":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3Boys16to18").asText();
			break;
		case "aBoysGreaterThan18":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3BoysGreaterThan18").asText();
			break;
		case "aBoysTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3BoysTotal").asText();
			break;
		case "aGirls7to11":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3Girls7to11").asText();
			break;
		case "aGirls12to15":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3Girls12to15").asText();
			break;
		case "aGirls16to18":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3Girls16to18").asText();
			break;
		case "aGirlsGreaterThan18":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3GirlsGreaterThan18").asText();
			break;
		case "aGirlsTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3GirlsTotal").asText();
			break;
		case "aGrandTotal":
			fieldValue = node.get("jjb_table_f1_dt").get(2).get("f3GrandTotal").asText();
			break;
		
		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
		
	}

	@Override
	public String getCciOhPreviousMonthData(Question question, JsonNode node) {
		String fieldValue = "";
		switch (question.getColumnName().trim()) {
		case "aYears7to11":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2Years7to11").asText();
			break;
		case "aYears12to15":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2Years12to15").asText();
			break;
		case "aYears16to18":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2Years16to18").asText();
			break;
		case "aYearsGreaterThan18":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2YearsGreaterThan18").asText();
			break;
		case "aTotal":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2Total").asText();
			break;
		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
	}

	@Override
	public String getCciChPreviousMonthData(Question question, JsonNode node) {
		
		String fieldValue = "";
		switch (question.getColumnName().trim()) {
		case "aYears0to6":
			fieldValue = node.get("ch_tbl_e1_dt").get(0).get("e1Years0to6").asText();
			break;
		case "aYears7to11":
			fieldValue = node.get("ch_tbl_e1_dt").get(0).get("e1Years7to11").asText();
			break;
		case "aYears12to15":
			fieldValue = node.get("ch_tbl_e1_dt").get(0).get("e1Years12to15").asText();
			break;
		case "aYears16to18":
			fieldValue = node.get("ch_tbl_e1_dt").get(0).get("e1Years16to18").asText();
			break;
		case "aYearsGreaterThan18":
			fieldValue = node.get("ch_tbl_e1_dt").get(0).get("e1YearsGreaterThan18").asText();
			break;
		case "aTotal":
			fieldValue = node.get("ch_tbl_e1_dt").get(0).get("e1Total").asText();
			break;
		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
	}

	@Override
	public String getCciOsPreviousMonthData(Question question, JsonNode node) {
		String fieldValue = "";
		switch (question.getColumnName().trim()) {
		case "aYears0to6":
			fieldValue = node.get("os_tbl_e1_dt").get(0).get("eYears0to6").asText();
			break;
		case "aYears7to11":
			fieldValue = node.get("os_tbl_e1_dt").get(0).get("eYears7to11").asText();
			break;
		case "aYears12to15":
			fieldValue = node.get("os_tbl_e1_dt").get(0).get("eYears12to15").asText();
			break;
		case "aYears16to18":
			fieldValue = node.get("os_tbl_e1_dt").get(0).get("eYears16to18").asText();
			break;
		case "aYearsGreaterThan18":
			fieldValue = node.get("os_tbl_e1_dt").get(0).get("eYearsGreaterThan18").asText();
			break;
		case "aTotal":
			fieldValue = node.get("os_tbl_e1_dt").get(0).get("eTotal").asText();
			break;

		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
	}

	@Override
	public String getCciPosPreviousMonthData(Question question, JsonNode node) {
		String fieldValue = "";
		switch (question.getColumnName().trim()) {
		case "aYears16to18":
			fieldValue = node.get("pos_tbl_e1_dt").get(0).get("e1Years16to18").asText();
			break;
		case "aYears19to21":
			fieldValue = node.get("pos_tbl_e1_dt").get(0).get("e1Years19to21").asText();
			break;
		case "aYearsGreaterThan21":
			fieldValue = node.get("pos_tbl_e1_dt").get(0).get("e1YearsGreaterThan21").asText();
			break;
		case "aTotal":
			fieldValue = node.get("pos_tbl_e1_dt").get(0).get("e1Total").asText();
			break;

		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
	}

	@Override
	public String getCciShPreviousMonthData(Question question, JsonNode node) {
		String fieldValue = "";
		switch (question.getColumnName().trim()) {
		case "aYears7to11":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2Years7to11").asText();
			break;
		case "aYears12to15":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2Years12to15").asText();
			break;
		case "aYears16to18":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2Years16to18").asText();
			break;
		case "aYearsGreaterThan18":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2YearsGreaterThan18").asText();
			break;
		case "aTotal":
			fieldValue = node.get("f3tbl_e1_dt").get(1).get("e2Total").asText();
			break;

		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
	}	
	
	@Override
	public String getSaaPreviousMonthData(Question question, JsonNode node) {
		String fieldValue = "";
		switch (question.getColumnName().trim()) {
		
		case "c1BoysZeroToTwo":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1BoysZeroToTwo").asText();
			break;
		case "c1BoysTwoToFour":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1BoysTwoToFour").asText();
			break;
		case "c1BoysFourToSix":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1BoysFourToSix").asText();
			break;
		case "c1BoysGreaterThanSix":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1BoysGreaterThanSix").asText();
			break;
		case "c1BoysTotal":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1BoysTotal").asText();
			break;
		case "c1GirlsZeroToTwo":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1GirlsZeroToTwo").asText();
			break;
		case "c1GirlsTwoToFour":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1GirlsTwoToFour").asText();
			break;
		case "c1GirlsFourToSix":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1GirlsFourToSix").asText();
			break;
		case "c1GirlsGreaterThanSix":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1GirlsGreaterThanSix").asText();
			break;
		case "c1GirlsTotal":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1GirlsTotal").asText();
			break;
		case "c1GrandTotal":
			fieldValue = node.get("saa_tbl_g1_dt").get(0).get("g1GrandTotal").asText();
			break;
		case "c2BoysZeroToTwo":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2BoysZeroToTwo").asText();
			break;
		case "c2BoysTwoToFour":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2BoysTwoToFour").asText();
			break;
		case "c2BoysFourToSix":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2BoysFourToSix").asText();
			break;
		case "c2BoysGreaterThanSix":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2BoysGreaterThanSix").asText();
			break;
		case "c2BoysTotal":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2BoysTotal").asText();
			break;
		case "c2GirlsZeroToTwo":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2GirlsZeroToTwo").asText();
			break;
		case "c2GirlsTwoToFour":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2GirlsTwoToFour").asText();
			break;
		case "c2GirlsFourToSix":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2GirlsFourToSix").asText();
			break;
		case "c2GirlsGreaterThanSix":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2GirlsGreaterThanSix").asText();
			break;
		case "c2GirlsTotal":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2GirlsTotal").asText();
			break;
		case "c2GrandTotal":
			fieldValue = node.get("saa_tbl_g1_dt").get(1).get("g2GrandTotal").asText();
			break;
			
		case "ha1OrphanBoys":
			fieldValue = node.get("saa_tbl_h1_dt").get(3).get("ha4OrphanBoys").asText();
			break;
		case "ha1OrphanGirls":
			fieldValue = node.get("saa_tbl_h1_dt").get(3).get("ha4OrphanGirls").asText();
			break;
		case "ha1AbandonedBoys":
			fieldValue = node.get("saa_tbl_h1_dt").get(3).get("ha4AbandonedBoys").asText();
			break;
		case "ha1AbandonedGirls":
			fieldValue = node.get("saa_tbl_h1_dt").get(3).get("ha4AbandonedGirls").asText();
			break;
		case "ha1SurrenderedBoys":
			fieldValue = node.get("saa_tbl_h1_dt").get(3).get("ha4SurrenderedBoys").asText();
			break;
		case "ha1SurrenderedGirls":
			fieldValue = node.get("saa_tbl_h1_dt").get(3).get("ha4SurrenderedGirls").asText();
			break;
		case "ha1TotalBoys":
			fieldValue = node.get("saa_tbl_h1_dt").get(3).get("ha4TotalBoys").asText();
			break;
		case "ha1TotalGirls":
			fieldValue = node.get("saa_tbl_h1_dt").get(3).get("ha4TotalGirls").asText();
			break;			
			
		case "ia1":
			fieldValue = node.get("saa_tbl_i1_dt").get(4).get("ia5").asText();
			break;	
				

		default:
			break;
		}
		if(fieldValue.equals("null")) fieldValue = null;
		return fieldValue;
	}

}
