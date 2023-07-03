package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ReportExecution;

@Repository
@RepositoryRestResource(exported = false)
public interface ReportExecutionRepoistory extends JpaRepository<ReportExecution, Long>{

	@Query("update ReportExecution set executionTime=:executionTime  where id=:executionId")
	void updateExecutionTimeByExecutionId(Long executionTime, Long executionId);
}
