package com.mashreq.paymentTracker.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.DataSource;

@Repository
public interface DataSourceCustomRepository {

	@Query("select ds from DataSource ds where ds.active=:activeStatus")
	List<DataSource> findByActive(String activeStatus);

}
