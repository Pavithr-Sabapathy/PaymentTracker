package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ComponentDetails;

@Repository
@RepositoryRestResource(exported = false)
public interface ComponentsDetailsRepository extends JpaRepository<ComponentDetails, Long>,ComponentsDetailsCustomizedRepository{
	

}
