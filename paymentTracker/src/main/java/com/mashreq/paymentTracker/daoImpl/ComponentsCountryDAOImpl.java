package com.mashreq.paymentTracker.daoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.ComponentsCountryDAO;
import com.mashreq.paymentTracker.model.ComponentsCountry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ComponentsCountryDAOImpl extends BaseDAOImpl implements ComponentsCountryDAO {
	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(ComponentsCountryDAOImpl.class);

	@Override
	public ComponentsCountry save(ComponentsCountry componentsCountryObject) {
	//	logger.info("ComponentsCountryDAOImpl.save ComponentsCountry:" + componentsCountryObject.toString());
		return super.create(componentsCountryObject);
	}

	@Override
	public ComponentsCountry findBycomponentsId(Long reportComponentId) {
		TypedQuery<ComponentsCountry> query = entityManager.createNamedQuery("componentsCountry.findBycomponentsId", ComponentsCountry.class);
		query.setParameter("reportComponentId", reportComponentId);
		ComponentsCountry componentCountryObj = query.getSingleResult();
		logger.info("ComponentsCountryDAOImpl.findBycomponentsId reportComponentId:" + reportComponentId);
		return componentCountryObj;
	}

}