package com.mashreq.paymentTracker.daoImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ComponentsDAOImpl extends BaseDAOImpl implements ComponentsDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(ComponentsDAOImpl.class);

	public Components save(Components componentsObject) {
		return super.create(componentsObject);
	}

	@Override
	public void deleteById(long componentId) {
		Components component = super.findById(Components.class, componentId);
		entityManager.remove(component);
		logger.info("ComponentsDAOImpl.deleteById id:" + componentId);
	}

	@Override
	public Components findById(long compReportId) {
		logger.info("ComponentsDAOImpl.findById id:" + compReportId);
		return super.findById(Components.class, compReportId);
	}

	@Override
	public List<Components> findAllByreportId(long reportId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Components> criteriaQuery = criteriaBuilder.createQuery(Components.class);
		Root<Components> root = criteriaQuery.from(Components.class);

		Join<Components, Report> report = root.join("report");
		criteriaQuery.where(criteriaBuilder.equal(report.get("id"), reportId));

		TypedQuery<Components> query = entityManager.createQuery(criteriaQuery);
		List<Components> componentList = query.getResultList();
		/*
		 * componentList.forEach(component -> { List<ComponentDetails> componentDetails
		 * = component.getComponentDetailsList(); ComponentsCountry componentCountry =
		 * component.getComponentsCountry(); });
		 */
		return componentList;
	}

	@Override
	public Components update(Components componentsObject) {
		// logger.info("ComponentsDAOImpl.save Components:" +
		// componentsObject.toString());
		return super.update(componentsObject);
	}

}