package com.mashreq.paymentTracker.daoImpl;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mashreq.paymentTracker.dao.PromptsDAO;
import com.mashreq.paymentTracker.model.Prompts;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Repository
@Transactional
public class PromptsDAOImpl extends BaseDAOImpl implements PromptsDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(PromptsDAOImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<Prompts> findAll() {
		Query query = entityManager.createQuery("SELECT e FROM Prompts e");
		return (List<Prompts>) query.getResultList();
	}

	@Override
	public List<Prompts> getPromptsByReportId(Long reportId) {
		TypedQuery<Prompts> query = entityManager.createNamedQuery("prompt.findPromptsByReportId", Prompts.class);
		query.setParameter("reportId", reportId);
		List<Prompts> prompts = query.getResultList();
		logger.info("PromptsDAOImpl.getPromptsByReportId reportId:" + reportId);
		return prompts;
	}

	@Override
	public BigInteger findPromptOrderByReportId(long reportId) {
		Query query = entityManager.createNamedQuery("prompt.findPromptOrderByReportId", Prompts.class);
		query.setParameter("reportId", reportId);
		return (BigInteger) query.getSingleResult();
	}

	@Override
	public Prompts save(Prompts prompts) {
		// logger.info("PromptsDAOImpl.save prompts:" + prompts.toString());
		return super.create(prompts);
	}

	@Override
	public Prompts updatePrompt(Prompts prompts) {
		// logger.info("PromptsDAOImpl.save prompts:" + prompts.toString());
		return super.update(prompts);
	}
	
	@Override
	public void deleteById(long promptId) {
		Prompts prompt = super.findById(Prompts.class, promptId);
		entityManager.remove(prompt);
		logger.info("PromptsDAOImpl.deleteById id:" + promptId);
	}

	@Override
	public Prompts getPromptById(Long promptId) {
		logger.info("PromptsDAOImpl.getPromptById id:" + promptId);
		return super.findById(Prompts.class, promptId);
	}

}