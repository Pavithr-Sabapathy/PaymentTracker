package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ComponentDetails;

@Repository
public interface ComponentsDetailsRepository extends JpaRepository<ComponentDetails, Long>,ComponentsDetailsCustomizedRepository{
	

}
