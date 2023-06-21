package com.mashreq.paymentTracker.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.service.DataSourceConfigService;

import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/dataSource")
public class DataSourceConfigController {

	private static final Logger log = LoggerFactory.getLogger(DataSourceConfigController.class);
	private static final String FILENAME = "DataSourceConfigController";
	@Autowired
	private DataSourceConfigService dataSourceConfigService;

	@PostMapping("/save")
	public ResponseEntity<String> saveDataSourceConfig(
			@Valid @RequestBody DataSource dataSourceConfigurationRequest) {
		try {
			log.info(FILENAME + "[saveDataSourceConfig Request]--->" + dataSourceConfigurationRequest.toString());
			DataSource dataSourceConfigResponse = dataSourceConfigService
					.saveDataSourceConfiguration(dataSourceConfigurationRequest);
			log.info(FILENAME + "[saveDataSourceConfig Response]--->" + dataSourceConfigResponse.toString());
			return new ResponseEntity<String>(ApplicationConstants.DATA_SOURCE_CREATION_MSG, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(FILENAME + "[Exception Occured]" + e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{dataSourceId}")
	public ResponseEntity<DataSource> getDataSourceConfig(@PathVariable Long dataSourceId) {
		log.info(FILENAME + "[getDataSourceConfig for DatasourceId]--->" + dataSourceId);
		DataSource dataSourceConfigResponse = dataSourceConfigService.getDataSourceConfigById(dataSourceId);
		log.info(FILENAME + "[getDataSourceConfig Response]--->" + dataSourceConfigResponse.toString());
		return ResponseEntity.ok(dataSourceConfigResponse);
	}

	@DeleteMapping("/{dataSourceId}")
	public ResponseEntity<String> deleteDataSourceConfig(@PathVariable long dataSourceId) {
		log.info(FILENAME + "[deleteDataSourceConfig for DatasourceId]--->" + dataSourceId);
		dataSourceConfigService.deleteDataSourceConfigById(dataSourceId);
		log.info(FILENAME + "[deleteDataSourceConfig deleted for this ID]--->" + dataSourceId);
		return new ResponseEntity<String>(ApplicationConstants.DATA_SOURCE_DELETION_MSG, HttpStatus.ACCEPTED);

	}

	@GetMapping("/allDataSource")
	public ResponseEntity<List<DataSource>> allDataSourceConfig() {
		log.info(FILENAME + "[allDataSourceConfig] Started");
		List<DataSource> dataSourceConfigurationListResponse = dataSourceConfigService.allDataSourceConfig();
		log.info(FILENAME + "[allDataSourceConfig] Ended with this response-->"
				+ dataSourceConfigurationListResponse.toString());
		return ResponseEntity.ok(dataSourceConfigurationListResponse);
	}

	@PutMapping()
	public ResponseEntity<String> updateDataSourceConfig(@Valid @RequestBody DataSource dataSourceupdateRequest) {
		log.info(FILENAME + "[updateDataSourceConfig] Request from UI-->" + dataSourceupdateRequest.toString());
		dataSourceConfigService.updateDataSourceConfigById(dataSourceupdateRequest);
		log.info(FILENAME + "[updateDataSourceConfig] Response-->" + ApplicationConstants.DATA_SOURCE_UPDATE_MSG);
		return new ResponseEntity<String>(ApplicationConstants.DATA_SOURCE_UPDATE_MSG, HttpStatus.ACCEPTED);

	}
}
