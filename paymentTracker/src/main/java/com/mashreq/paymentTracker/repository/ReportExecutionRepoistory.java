package com.mashreq.paymentTracker.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ReportExecution;
import com.mashreq.paymentTracker.type.ExecutionStatusType;

@Repository
@RepositoryRestResource(exported = false)
public interface ReportExecutionRepoistory extends JpaRepository<ReportExecution, Long>{

	@Query("update ReportExecution set executionTime=:executionTime  where id=:executionId")
	void updateExecutionTimeByExecutionId(Long executionTime, Long executionId);

	@Query("update ReportExecution set executionStatus=:status , endDate=:endDate  where id=:executionId")
	void updateStatusById(Long executionId, ExecutionStatusType status, Date endDate);
}
