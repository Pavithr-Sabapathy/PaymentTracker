package com.mashreq.paymentTracker.daoImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.ModuleDAO;
import com.mashreq.paymentTracker.model.ApplicationModule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ModuleDAOImpl extends BaseDAOImpl implements ModuleDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(ModuleDAOImpl.class);

	@Override
	public List<ApplicationModule> findAll() {
		TypedQuery<ApplicationModule> query = entityManager.createQuery("Select module FROM ApplicationModule module",ApplicationModule.class);
		List<ApplicationModule> moduleList = (List<ApplicationModule>) query.getResultList();
		logger.info("ModuleDAOImpl.findAll:" + moduleList.toString());
		return moduleList;
	}

	@Override
	public ApplicationModule save(ApplicationModule applicationModule) {
	//	logger.info("ModuleDAOImpl.save ApplicationModule:" + applicationModule.toString());
		return super.create(applicationModule);
	}
	
	@Override
	public ApplicationModule update(ApplicationModule applicationModule) {
	//	logger.info("ModuleDAOImpl.save ApplicationModule:" + applicationModule.toString());
		return super.update(applicationModule);
	}

	@Override
	public void deleteById(long moduleId) {
		ApplicationModule module = super.findById(ApplicationModule.class, moduleId);
		entityManager.remove(module);
		logger.info("ModuleDAOImpl.deleteById id:" + moduleId);
	}

	@Override
	public List<ApplicationModule> findByModuleName(String moduleName) {
		TypedQuery<ApplicationModule> query = entityManager.createNamedQuery("module.findByModuleName", ApplicationModule.class);
		query.setParameter("moduleName", moduleName);
		List<ApplicationModule> moduleList = query.getResultList();
		logger.info("ModuleDAOImpl.findByModuleName moduleName:" + moduleName);
		return moduleList;
	}

	@Override
	public ApplicationModule findById(Long moduleId) {
		logger.info("ModuleDAOImpl.findById id:" + moduleId);
		return super.findById(ApplicationModule.class, moduleId);
	}

}