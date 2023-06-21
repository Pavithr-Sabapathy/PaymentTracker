package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.model.DataSource;

public interface DataSourceConfigService {

	DataSource saveDataSourceConfiguration(DataSource dataSourceConfigurationRequest) throws Exception;

	DataSource getDataSourceConfigById(long dataSourceId);

	void deleteDataSourceConfigById(long dataSourceId);

	List<DataSource> allDataSourceConfig();

	void updateDataSourceConfigById(DataSource dataSourceupdateRequest);
}