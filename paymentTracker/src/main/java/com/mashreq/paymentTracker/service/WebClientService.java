package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.model.DataSource;

import jakarta.validation.Valid;

public interface WebClientService {

	DataSource getDataSourceConfigById(Long dataSourceId);

	String saveDataSourceConfig(@Valid DataSource dataSourceConfigurationRequest);


}