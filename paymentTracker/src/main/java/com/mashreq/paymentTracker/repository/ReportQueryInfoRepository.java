package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ReportQueryInfo;

@Repository
public interface ReportQueryInfoRepository extends JpaRepository<ReportQueryInfo, Long>{

}

