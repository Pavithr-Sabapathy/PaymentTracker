package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.model.DataSourceConfig;

public interface DataSourceConfigService {

	DataSourceConfig saveDataSourceConfiguration(DataSourceConfig dataSourceConfigurationRequest) throws Exception;

	DataSourceConfig getDataSourceConfigById(long dataSourceId);

	void deleteDataSourceConfigById(long dataSourceId);

	List<DataSourceConfig> allDataSourceConfig();

	void updateDataSourceConfigById(DataSourceConfig dataSourceupdateRequest);
}