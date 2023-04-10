package com.mashreq.paymentTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Prompts;

@Repository
public interface PromptsCustomizedRepository {

	@Query("select max(prompt.promptOrder)  from Prompts prompt join Reports report on prompt.report = report.id where report.id =?1")
	Long findPromptOrderByReportId(Long reportId);
	
	@Query("select prompt from Prompts prompt join Reports report on prompt.report = report.id where report.id =?1")
	List<Prompts> findPromptByReportId(long reportId);

}