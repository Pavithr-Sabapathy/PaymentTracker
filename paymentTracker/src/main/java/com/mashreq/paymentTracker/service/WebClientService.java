package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.model.DataSourceConfig;

import jakarta.validation.Valid;

public interface WebClientService {

	DataSourceConfig getDataSourceConfigById(Long dataSourceId);

	String saveDataSourceConfig(@Valid DataSourceConfig dataSourceConfigurationRequest);


}