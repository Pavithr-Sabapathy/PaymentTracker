package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.DataSourceDTO;
import com.mashreq.paymentTracker.model.DataSource;

public interface DataSourceConfigService {

	DataSource saveDataSourceConfiguration(DataSource dataSourceConfigurationRequest) throws Exception;

	DataSourceDTO getDataSourceConfigById(long dataSourceId);

	void deleteDataSourceById(long dataSourceId);

	List<DataSource> allDataSourceConfig();

	void updateDataSourceById(DataSource dataSourceupdateRequest);

	List<DataSourceDTO> allActiveDataSource();
}