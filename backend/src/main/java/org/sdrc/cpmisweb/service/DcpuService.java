package org.sdrc.cpmisweb.service;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;

public interface DcpuService {

	String findPreviousMonthData(String columnName, JsonNode node);

	Integer findPreviousMonthOptionData(QuestionModel questionModel, Question question, JsonNode node, Map<String, String> typeDetailsMap);

	Integer[] findPreviousMonthCheckboxData(QuestionModel questionModel, Question question, JsonNode node);
	
}
