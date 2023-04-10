package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.PromptResponseDTO;

public interface promptService {

	List<PromptResponseDTO> fetchAllPrompts();

	void savePrompt(PromptDTO promptRequest);

	void deletePromptById(long promptId);

	void updatePromptById(PromptDTO promptRequest, long promptId);

	List<PromptDTO> fetchPromptsByReportId(long reportId);

}
