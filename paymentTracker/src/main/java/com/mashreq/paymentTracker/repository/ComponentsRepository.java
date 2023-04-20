package com.mashreq.paymentTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.LinkedReportDetails;

@Repository
public interface ComponentsRepository extends JpaRepository<Components, Long>{

}
