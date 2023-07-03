package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.PromptRequestDTO;
import com.mashreq.paymentTracker.dto.PromptResponseDTO;

public interface promptService {

	List<PromptResponseDTO> fetchAllPrompts();

	PromptDTO savePrompt(PromptRequestDTO promptRequest);

	void deletePromptById(long promptId);

	PromptDTO updatePromptById(PromptRequestDTO promptRequest, long promptId);

	List<PromptDTO> fetchPromptsByReportId(long reportId);

}
