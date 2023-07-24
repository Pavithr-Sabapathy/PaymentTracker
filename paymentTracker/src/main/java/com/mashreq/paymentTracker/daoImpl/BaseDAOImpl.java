package com.mashreq.paymentTracker.daoImpl;

import java.io.Serializable;

import com.mashreq.paymentTracker.dao.BaseDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public abstract class BaseDAOImpl implements BaseDAO {

	   @PersistenceContext
	   private EntityManager entityManager;

		public <T extends Serializable> T create(final T entity){
	      try {
	         entityManager.persist(entity);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }

	      return entity;
	   }

	   @Override
	   public <T extends Serializable> T update (T entity){
	      // TODO Auto-generated method stub
	      return entityManager.merge(entity);
	   }

	   @Override
	   public <T extends Serializable> T findById (Class<T> clazz, Long id){
	      // TODO Auto-generated method stub
	      return entityManager.find(clazz, id);
	   }

}
