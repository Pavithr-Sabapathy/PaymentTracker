package com.mashreq.paymentTracker.dao;

import java.io.Serializable;

public interface BaseDAO {

	public <T extends Serializable> T create(T entity);

	public <T extends Serializable> T update(T entity);

	public <T extends Serializable> T findById(Class<T> clazz, Long id);

}