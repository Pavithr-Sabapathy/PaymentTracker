package com.mashreq.paymentTracker.service;

import java.util.List;
import java.util.Map;

import com.mashreq.paymentTracker.dto.DataSourceRequestDTO;
import com.mashreq.paymentTracker.dto.DataSourceResponseDTO;

import jakarta.validation.Valid;

public interface DataSourceConfigService {

	DataSourceResponseDTO saveDataSourceConfiguration(DataSourceRequestDTO dataSourceConfigurationRequest);

	DataSourceResponseDTO getDataSourceConfigById(long dataSourceId);

	void deleteDataSourceById(long dataSourceId);

	Map<String, Object> allDataSourceConfig(int page, int size, List<String> sort);

	Map<String, Object> allActiveDataSource(int page, int size, List<String> sort);

	DataSourceResponseDTO updateDataSourceById(@Valid DataSourceRequestDTO dataSourceRequest, Long datasourceId);
}