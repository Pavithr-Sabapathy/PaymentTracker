package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.DataSourceDTO;
import com.mashreq.paymentTracker.model.DataSource;

import jakarta.validation.Valid;

public interface DataSourceConfigService {

	DataSource saveDataSourceConfiguration(DataSourceDTO dataSourceConfigurationRequest);

	DataSourceDTO getDataSourceConfigById(long dataSourceId);

	void deleteDataSourceById(long dataSourceId);

	List<DataSource> allDataSourceConfig();

	List<DataSourceDTO> allActiveDataSource();

	DataSource updateDataSourceById(@Valid DataSourceDTO dataSourceRequest, Long datasourceId);
}