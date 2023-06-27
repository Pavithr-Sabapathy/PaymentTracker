package com.mashreq.paymentTracker.service;

import java.util.List;
import java.util.Map;

import com.mashreq.paymentTracker.dto.DataSourceDTO;
import com.mashreq.paymentTracker.model.DataSource;

import jakarta.validation.Valid;

public interface DataSourceConfigService {

	DataSource saveDataSourceConfiguration(DataSourceDTO dataSourceConfigurationRequest);

	DataSourceDTO getDataSourceConfigById(long dataSourceId);

	void deleteDataSourceById(long dataSourceId);

	List<DataSource> allDataSourceConfig();

	Map<String, Object> allActiveDataSource(int page, int size, List<String> sort);

	DataSource updateDataSourceById(@Valid DataSourceDTO dataSourceRequest, Long datasourceId);
}