package com.audit.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditClient {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Value("${audit.serverUrl}")
	private String serverUrl;

	private boolean mustAudit = true;
	private String sourceObjId;
	private String sourceObjName;
	private Object sourceObj;

	@Value("${audit.createdBy}")
	private String callingProgramCreatedBy;
	@Value("${audit.lastModifiedBy}")
	private String callingProgramLastModifiedBy;
	@Value("${audit.createdDate}")
	private String callingProgramCreatedDate;
	@Value("${audit.lastModifiedDate}")
	private String callingProgramLastModifiedDate;
	private HashMap<String, String> columnMap;

	public String getSourceObjId() {
		return sourceObjId;
	}

	public void setSourceObjId(String sourceObjId) {
		this.sourceObjId = sourceObjId;
	}

	public String getSourceObjName() {
		return sourceObjName;
	}

	public void setSourceObjName(String sourceObjName) {
		this.sourceObjName = sourceObjName;
	}

	public HashMap<String, String> getColumnMap() {
		this.columnMap = getMappingOfFieldNames();
		return columnMap;
	}

	public HashMap<String, String> getMappingOfFieldNames() {
		HashMap<String, String> ColumnMap = new HashMap<String, String>();
		// Put elements to the map
		ColumnMap.put("createdby", callingProgramCreatedBy);
		ColumnMap.put("createddate", callingProgramCreatedDate);
		ColumnMap.put("lastmodifiedBy", callingProgramLastModifiedBy);
		ColumnMap.put("lastmodifiedDate", callingProgramLastModifiedDate);

		return ColumnMap;
	}

	public Object getSourceObj() {
		return sourceObj;
	}

	public void setSourceObj(Object sourceObj) {
		this.sourceObj = sourceObj;
	}

	public boolean isMustAudit() {
		return mustAudit;
	}

	public void setMustAudit(boolean mustAudit) {
		this.mustAudit = mustAudit;
	}

	private void handleException(RuntimeException runTimeException) {
		if (!mustAudit) {
			LOGGER.error("Audit Logger is down. Not Audited source object with unique id " + this.sourceObjId
					+ " Entity Name " + this.sourceObjName);
		} else {
			runTimeException.getStackTrace();
			throw new AuditLoggerException("Audit Logger is down");
		}
	}

	public ResponseEntity<Map> createAuditLog(boolean mustAudit, String sourceObjId, String sourceObjName,
			Object sourceObj) throws AuditLoggerException {

		this.mustAudit = mustAudit;
		final String uri = this.serverUrl + "/audit/";

		LOGGER.debug("URL of Audit Servive is " + uri);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Map> response;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAll(getMappingOfFieldNames());

			// set your entity to send
			this.sourceObjId = sourceObjId;
			this.sourceObjName = sourceObjName;

			HashMap<String, Object> entityObject = new HashMap<String, Object>();
			entityObject.put("sourceObjId", sourceObjId);
			entityObject.put("sourceObjName", sourceObjName);
			entityObject.put("sourceObj", sourceObj);

			@SuppressWarnings("unchecked")
			HttpEntity entity = new HttpEntity(entityObject, headers);

			LOGGER.debug("Headers  For  Audit Servive is " + headers);

			response = restTemplate.exchange(uri, HttpMethod.POST, entity, Map.class);
			return response;

		} catch (RuntimeException runTimeException) {
			runTimeException.printStackTrace();
			handleException(runTimeException);

		}

		LOGGER.debug("Something Snapped");

		return null;

	}

	@SuppressWarnings("unchecked")
	public ResponseEntity<Map> createAuditLog(String sourceObjId, String sourceObjName, Object sourceObj)
			throws AuditLoggerException {
		LOGGER.info("Audit Servive is mandatory. If Service is down then main program will fail");
		return createAuditLog(true, sourceObjId, sourceObjName, sourceObj);
	}

	public List<Map> getAuditLog(String sourceObjId, String sourceObjName) throws AuditLoggerException {

		final String uri = this.serverUrl + "/audit/history/" + sourceObjId + "/" + sourceObjName;

		LOGGER.debug("URL of Audit Servive is " + uri);

		RestTemplate restTemplate = new RestTemplate();
		List<Map> response;

		try {
			response = restTemplate.getForObject(uri,  List.class);
		
			return response;

		} catch (RuntimeException runTimeException) {
			runTimeException.printStackTrace();
			handleException(runTimeException);

		}

		LOGGER.debug("Something Snapped");

		return null;

	}
	public Map getOneHistoryLog(String historyObjectId) throws AuditLoggerException {

		final String uri = this.serverUrl + "/audit/" + historyObjectId ;

		LOGGER.debug("URL of Audit Servive is " + uri);

		RestTemplate restTemplate = new RestTemplate();
		Map response;

		try {
			response = restTemplate.getForObject(uri,  Map.class);
			LOGGER.debug("Response is  is " + response);
			return response;

		} catch (RuntimeException runTimeException) {
			runTimeException.printStackTrace();
			handleException(runTimeException);

		}

		LOGGER.debug("Something Snapped");

		return null;

	}

}