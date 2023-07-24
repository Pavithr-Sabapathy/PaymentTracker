package com.mashreq.paymentTracker.daoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashreq.paymentTracker.dao.DataSourceDAO;
import com.mashreq.paymentTracker.model.DataSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Transactional
public class DataSourceDAOImpl extends BaseDAOImpl implements DataSourceDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(PromptsDAOImpl.class);

	@Override
	public DataSource save(DataSource dataSource) {
		logger.info("DataSourceDAOImpl.save dataSource:" + dataSource.toString());
		return super.create(dataSource);
	}

	@Override
	public DataSource getDataSourceById(long dataSourceId) {
		logger.info("DataSourceDAOImpl.findById id:" + dataSourceId);
		return super.findById(DataSource.class, dataSourceId);
	}

	@Override
	public void deleteById(long dataSourceId) {
		DataSource dataSource = super.findById(DataSource.class, dataSourceId);
		entityManager.remove(dataSource);
		logger.info("DataSourceDAOImpl.deleteById id:" + dataSourceId);

	}


	@Override
	public DataSource update(DataSource dataSource) {
		logger.info("DataSourceDAOImpl.save dataSource:" + dataSource.toString());
		return super.update(dataSource);
	}

	
}