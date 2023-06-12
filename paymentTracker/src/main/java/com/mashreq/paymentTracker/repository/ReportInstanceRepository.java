package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ReportInstance;

@Repository
public interface ReportInstanceRepository extends JpaRepository<ReportInstance, Long>{

}
