package com.mashreq.paymentTracker.daoImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.LinkedReportDAO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.LinkedReportInfo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class LinkedReportDAOImpl extends BaseDAOImpl implements LinkedReportDAO {
	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(LinkedReportDAOImpl.class);

	@Override
	public void deleteById(long linkedReportId) {
		LinkedReportInfo linkedReportInfo = super.findById(LinkedReportInfo.class, linkedReportId);
		entityManager.remove(linkedReportInfo);
		logger.info("LinkedReportDAOImpl.deleteById id:" + linkedReportId);

	}

	@Override
	public List<LinkedReportInfo> findByAllModuleId(long moduleId) {
		TypedQuery<LinkedReportInfo> query = entityManager.createNamedQuery("linkedReport.findByModuleId",
				LinkedReportInfo.class);
		query.setParameter("moduleId", moduleId);
		List<LinkedReportInfo> linkedReportList = query.getResultList();
		return linkedReportList;
	}

	@Override
	public LinkedReportInfo findById(long linkedReportId) {
		LinkedReportInfo linkedReportObject = new LinkedReportInfo();
		try {
			TypedQuery<LinkedReportInfo> query = entityManager.createNamedQuery("linkedReport.findById",
					LinkedReportInfo.class);
			query.setParameter("linkedReportId", linkedReportId);
			linkedReportObject = query.getSingleResult();
		} catch (NoResultException nre) {
			throw new ResourceNotFoundException(ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS + linkedReportId);
		} 
		return linkedReportObject;
	}

	@Override
	public List<LinkedReportInfo> findAllByReportId(long reportId) {
		TypedQuery<LinkedReportInfo> query = entityManager.createNamedQuery("linkedReport.findAllByReportId",
				LinkedReportInfo.class);
		query.setParameter("reportId", reportId);
		List<LinkedReportInfo> linkedReportList = (List<LinkedReportInfo>) query.getResultList();
		return linkedReportList;
	}

	@Override
	public LinkedReportInfo save(LinkedReportInfo linkedReportModel) {
		return super.create(linkedReportModel);
	}

	@Override
	public LinkedReportInfo update(LinkedReportInfo linkedReportModel) {
		return super.update(linkedReportModel);
	}

}