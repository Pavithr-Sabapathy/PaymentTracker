package com.mashreq.paymentTracker.daoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.ComponentDetailsDAO;
import com.mashreq.paymentTracker.model.ComponentDetails;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ComponentDetailsDAOImpl extends BaseDAOImpl implements ComponentDetailsDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(ComponentDetailsDAOImpl.class);

	@Override
	public ComponentDetails save(ComponentDetails componentDetailsObject) {
	//	logger.info("ComponentDetailsDAOImpl.save ComponentDetails:" + componentDetailsObject.toString());
		return super.create(componentDetailsObject);
	}
	
	@Override
	public ComponentDetails update(ComponentDetails componentDetailsObject) {
		//logger.info("ComponentDetailsDAOImpl.update ComponentDetails:" + componentDetailsObject.toString());
		return super.update(componentDetailsObject);
	}

	@Override
	public void deleteById(long componentDetailId) {
		ComponentDetails componentDetail = super.findById(ComponentDetails.class, componentDetailId);
		entityManager.remove(componentDetail);
		logger.info("ComponentDetailsDAOImpl.deleteById id:" + componentDetailId);

	}

	@Override
	public ComponentDetails findById(long componentDetailId) {
		logger.info("ComponentDetailsDAOImpl.findById id:" + componentDetailId);
		return super.findById(ComponentDetails.class, componentDetailId);
	}

}