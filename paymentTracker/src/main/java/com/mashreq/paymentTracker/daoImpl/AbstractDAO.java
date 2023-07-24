package com.mashreq.paymentTracker.daoImpl;

import java.io.Serializable;

import org.springframework.dao.DataAccessException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


public abstract class AbstractDAO {
   
   @PersistenceContext  
   private EntityManager entityManager;


   public <T extends Serializable> T save( T entity ) throws DataAccessException{
       entityManager.persist(entity);
       return entity;
   }
   
   public <T extends Serializable> T update( T entity ) throws DataAccessException {
      return  entityManager.merge(entity);
  }
   
  public <T extends Serializable> T findById(Class<T> clazz, Long id) throws DataAccessException {
     return entityManager.find(clazz, id);
  }


}
