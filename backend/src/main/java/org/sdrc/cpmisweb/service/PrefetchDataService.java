package org.sdrc.cpmisweb.service;

import com.fasterxml.jackson.databind.JsonNode;

import in.co.sdrc.sdrcdatacollector.jpadomains.Question;

public interface PrefetchDataService {
	String getCwcPreviousMonthData(Question question, JsonNode node);

	String getJjbPreviousMonthData(Question question, JsonNode node);

	String getCciOhPreviousMonthData(Question question, JsonNode node);

	String getCciChPreviousMonthData(Question question, JsonNode node);

	String getCciOsPreviousMonthData(Question question, JsonNode node);

	String getCciPosPreviousMonthData(Question question, JsonNode node);

	String getCciShPreviousMonthData(Question question, JsonNode node);
	
	String getSaaPreviousMonthData(Question question, JsonNode node);
}
