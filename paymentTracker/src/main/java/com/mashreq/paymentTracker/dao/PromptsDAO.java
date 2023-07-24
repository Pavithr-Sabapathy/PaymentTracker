package com.mashreq.paymentTracker.dao;

import java.math.BigInteger;
import java.util.List;

import com.mashreq.paymentTracker.model.Prompts;

public interface PromptsDAO {
	
	List<Prompts> findAll();

	BigInteger findPromptOrderByReportId(long reportId);

	Prompts save(Prompts promptsObject);

	void deleteById(long promptId);

	List<Prompts> getPromptsByReportId(Long reportId);

	Prompts getPromptById(Long promptId);

	Prompts updatePrompt(Prompts prompts);
}
