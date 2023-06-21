package com.mashreq.paymentTracker.daoImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashreq.paymentTracker.dao.DataSourceCustomRepository;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.serviceImpl.AdvanceSearchReportServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class DataSourceDAOImpl implements DataSourceCustomRepository {
	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger log = LoggerFactory.getLogger(AdvanceSearchReportServiceImpl.class);

	@Override
	public List<DataSource> findByActive() {
		Query query = entityManager.createNamedQuery("activeDataSource");
		query.setParameter("activeStatus", "Y");
		List<DataSource> dataSources = query.getResultList();
		log.info("Total Active DataSource :" + dataSources.size());
		return dataSources;
	}

}