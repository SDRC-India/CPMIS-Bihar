/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 24-Jul-2019
 */
package org.sdrc.cpmisweb.service;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import in.co.sdrc.sdrcdatacollector.models.MessageModel;

public interface RawDataReportService {

	ResponseEntity<MessageModel> exportRawaData(Integer formId, Integer timeperiodId, Principal principal,
			OAuth2Authentication auth);
}
