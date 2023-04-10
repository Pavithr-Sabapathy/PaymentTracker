package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.PromptResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.PromptsRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.serviceImpl.PromptServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PromptServiceTest {

	@InjectMocks
	PromptServiceImpl promptService;

	@Mock
	PromptsRepository mockpromptsRepository;

	@Mock
	ReportConfigurationRepository mockreportConfigurationRepo;

	@Test
	public void testSavePrompt() throws JsonMappingException, JsonProcessingException {
		/*
		 * ObjectMapper mapper = new ObjectMapper(); PromptDTO mockpromptDTORequest =
		 * new PromptDTO("Refernce_Number", "Reference Number", BigInteger.ONE, "y", 1,
		 * BigInteger.ZERO); Reports mockReportsOptionalResponse = new Reports(1L,
		 * "Advanced_Search", "Advanced Search", "Search", "Searching Category", "y",
		 * "y"); String mockPromptRequestString =
		 * "{\"displayName\": \"Advanced Search\",\"entityId\": 0,\"promptKey\": \"1\",\"promptOrder\": 1,\"promptRequired\": \"y\",\"report\": {\"active\": \"y\",\"displayName\": \"sample\",\"id\": 5,\"reportCategory\": \"string\",\"reportDescription\": \"string\", \"reportName\": \"string\",\"valid\": \"y\"}}"
		 * ; Prompts mockpromptRequest = mapper.readValue(mockPromptRequestString,
		 * Prompts.class);
		 * 
		 * 
		 * when(mockreportConfigurationRepo.findById(1L)).thenReturn(Optional.of(
		 * mockReportsOptionalResponse));
		 * 
		 * when(mockpromptsRepository.save(mockpromptRequest)).thenReturn(
		 * mockpromptRequest);
		 * 
		 * Prompts prompts = promptService.savePrompt(mockpromptDTORequest);
		 * //assertEquals(prompts.getDisplayName(), "Refernce_Number");
		 * verify(mockpromptsRepository, times(1)).save(mockpromptRequest);
		 */}

	@Test
	public void testfetchAllReports() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		String promptResponseString = "{\"displayName\": \"Advanced Search\",\"entityId\": 0,\"promptKey\": \"1\",\"promptOrder\": 1,\"promptRequired\": \"y\",\"report\": {\"active\": \"y\",\"displayName\": \"sample\",\"id\": 5,\"reportCategory\": \"string\",\"reportDescription\": \"string\", \"reportName\": \"string\",\"valid\": \"y\"}}\r\n"
				+ "";
		Prompts mockPromptResponse = mapper.readValue(promptResponseString, Prompts.class);

		List<Prompts> promptsList = Arrays.asList(mockPromptResponse);

		when(mockpromptsRepository.findAll()).thenReturn(promptsList);

		// test
		List<PromptResponseDTO> promptRespnse = promptService.fetchAllPrompts();

		assertEquals(1, promptRespnse.size());
		verify(mockpromptsRepository, times(1)).findAll();
	}

	@Test
	public void testDeletePromptById() {
		long promptId = 1L;

		when(mockpromptsRepository.existsById(promptId)).thenReturn(true);
		doNothing().when(mockpromptsRepository).deleteById(promptId);

		promptService.deletePromptById(promptId);

		verify(mockpromptsRepository).existsById(1L);
		verify(mockpromptsRepository).deleteById(1L);
	}

	@Test
	void testdeleteReportByIdNotExists() throws ResourceNotFoundException {
		long promptId = 1L;

		when(mockpromptsRepository.existsById(promptId)).thenReturn(false);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> promptService.deletePromptById(promptId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Prompts not exist with this id :1"));
	}

	@Test
	public void testupdatePromptById() throws JsonMappingException, JsonProcessingException {
		long promptId = 1L;
		ObjectMapper mapper = new ObjectMapper();
		PromptDTO mockPromptDTO = new PromptDTO("Reference_Sample", "Reference Sample", BigInteger.ONE, "N", 1,
				BigInteger.ZERO);
		Reports mockReportsResponse = new Reports();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		String promptResponseString = "{\"displayName\": \"Advanced Search\",\"entityId\": 0,\"promptKey\": \"1\",\"promptOrder\": 1,\"promptRequired\": \"y\"}";

		Prompts mockPromptResponse = mapper.readValue(promptResponseString, Prompts.class);

		when(mockreportConfigurationRepo.findById(1L)).thenReturn(Optional.of(mockReportsResponse));
		when(mockpromptsRepository.findById(1L)).thenReturn(Optional.of(mockPromptResponse));
		promptService.updatePromptById(mockPromptDTO, promptId);
		verify(mockpromptsRepository, times(1)).findById(promptId);
	}

	@Test
	public void testupdateReportByIdNotExists() throws ResourceNotFoundException {
		long reportId = 1L;
		Reports mockReportsResponse = null;
		PromptDTO mockPromptDTO = new PromptDTO("Reference_Sample", "Reference Sample", BigInteger.ONE, "N", 1,
				BigInteger.ZERO);
		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.ofNullable(mockReportsResponse));
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> promptService.updatePromptById(mockPromptDTO, reportId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Report Configuration not exist with this id :1"));
	}

}