package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.DataSourceCustomRepository;
import com.mashreq.paymentTracker.model.DataSource;

@Repository
@RepositoryRestResource(exported = false)
public interface DataSourceRepository extends JpaRepository<DataSource, Long> ,DataSourceCustomRepository{

}
