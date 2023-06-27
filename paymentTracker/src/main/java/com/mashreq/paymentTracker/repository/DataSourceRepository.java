package com.mashreq.paymentTracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.DataSourceCustomRepository;
import com.mashreq.paymentTracker.model.DataSource;

@Repository
@RepositoryRestResource(exported = false)
public interface DataSourceRepository extends JpaRepository<DataSource, Long>, DataSourceCustomRepository {
	
	@Query("select ds from DataSource ds where ds.active=:activeStatus")
	Page<DataSource> findByActiveContaining(String activeStatus, Pageable pagingSort);

}
