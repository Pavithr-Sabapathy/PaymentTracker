package com.mashreq.paymentTracker.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.DataSourceRequestDTO;
import com.mashreq.paymentTracker.dto.DataSourceResponseDTO;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.service.DataSourceConfigService;

import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/dataSource")
public class DataSourceController {

	private static final Logger log = LoggerFactory.getLogger(DataSourceController.class);
	private static final String FILENAME = "DataSourceConfigController";
	@Autowired
	private DataSourceConfigService dataSourceConfigService;

	@PostMapping("/save")
	public ResponseEntity<DataSourceResponseDTO> saveDataSource(
			@Valid @RequestBody DataSourceRequestDTO dataSourceRequest) {
		log.info(FILENAME + "[saveDataSourceConfig Request]--->" + dataSourceRequest.toString());
		DataSourceResponseDTO dataSourceReponse = dataSourceConfigService
				.saveDataSourceConfiguration(dataSourceRequest);
		return new ResponseEntity<DataSourceResponseDTO>(dataSourceReponse, HttpStatus.CREATED);
	}

	@GetMapping("id/{dataSourceId}")
	public ResponseEntity<DataSourceResponseDTO> getDataSourceById(@PathVariable("dataSourceId") Long dataSourceId) {
		DataSourceResponseDTO dataSourceConfigResponse = dataSourceConfigService.getDataSourceConfigById(dataSourceId);
		return ResponseEntity.ok(dataSourceConfigResponse);
	}

	@DeleteMapping("/{dataSourceId}")
	public ResponseEntity<String> deleteDataSourceById(@PathVariable long dataSourceId) {
		log.info(FILENAME + "[deleteDataSourceById Requested DatasourceId]--->" + dataSourceId);
		dataSourceConfigService.deleteDataSourceById(dataSourceId);
		log.info(FILENAME + "[deleteDataSourceConfig deleted for this ID]--->" + dataSourceId);
		return new ResponseEntity<String>(ApplicationConstants.DATA_SOURCE_DELETION_MSG, HttpStatus.ACCEPTED);

	}

	@GetMapping("/allDataSource")
	public ResponseEntity<Map<String, Object>> getAllDataSource(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,desc") List<String> sort) {
		log.info(FILENAME + "[allDataSourceConfig] Started");
		Map<String, Object> dataSourceConfigurationListResponse = dataSourceConfigService.allDataSourceConfig(page,
				size, sort);
		log.info(FILENAME + "[allDataSourceConfig] Ended with this response-->"
				+ dataSourceConfigurationListResponse.toString());
		return ResponseEntity.ok(dataSourceConfigurationListResponse);
	}

	@PutMapping("/{dataSourceId}")
	public ResponseEntity<DataSourceResponseDTO> updateDataSourceById(
			@Valid @RequestBody DataSourceRequestDTO dataSourceRequest,
			@PathVariable("dataSourceId") Long dataSourceId) {
		log.info(FILENAME + "[updateDataSourceConfig] Request from UI-->" + dataSourceRequest.toString());
		DataSourceResponseDTO dataSourceReponse = dataSourceConfigService.updateDataSourceById(dataSourceRequest,
				dataSourceId);
		log.info(FILENAME + "[updateDataSourceConfig] Response-->" + ApplicationConstants.DATA_SOURCE_UPDATE_MSG);
		return new ResponseEntity<DataSourceResponseDTO>(dataSourceReponse, HttpStatus.ACCEPTED);

	}

	@GetMapping("/allActive")
	public ResponseEntity<Map<String, Object>> allActive(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,desc") List<String> sort) {
		Map<String, Object> dataSourceDTOList = dataSourceConfigService.allActiveDataSource(page, size, sort);
		log.info(FILENAME + "[allDataSourceConfig] Ended with this response-->" + dataSourceDTOList.toString());
		return ResponseEntity.ok(dataSourceDTOList);
	}

}
