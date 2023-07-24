package com.mashreq.paymentTracker.daoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.LinkMappingDAO;
import com.mashreq.paymentTracker.model.LinkedReportDetails;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class LinkMappingDAOImpl extends BaseDAOImpl implements LinkMappingDAO {
	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(LinkMappingDAOImpl.class);

	@Override
	public LinkedReportDetails save(LinkedReportDetails linkReportDetails) {
		return super.create(linkReportDetails);
	}
	
	@Override
	public LinkedReportDetails update(LinkedReportDetails linkReportDetails) {
		return super.update(linkReportDetails);
	}

	@Override
	public LinkedReportDetails findByLinkReportPromptId(long linkPromptId) {
		TypedQuery<LinkedReportDetails> query = entityManager.createNamedQuery("LinkedReportDetails.findByLinkReportPromptId", LinkedReportDetails.class);
		query.setParameter("reportId", linkPromptId);
		LinkedReportDetails linkedReportList = query.getSingleResult();
		logger.info("LinkMappingDAOImpl.LinkedReportDetails linkPromptId:" + linkPromptId);
		return linkedReportList;
	}

}