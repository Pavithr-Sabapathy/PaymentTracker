package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ReportExecution;

@Repository
public interface ReportExecutionRepoistory extends JpaRepository<ReportExecution, Long>{

}
