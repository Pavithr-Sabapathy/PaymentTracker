package com.mashreq.paymentTracker.dao;

import com.mashreq.paymentTracker.model.DataSource;

public interface DataSourceDAO{

	DataSource getDataSourceById(long dataSourceId);

	void deleteById(long dataSourceId);

	DataSource update(DataSource dataSource);

}