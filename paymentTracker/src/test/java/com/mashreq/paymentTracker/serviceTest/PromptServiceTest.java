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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.PromptRequestDTO;
import com.mashreq.paymentTracker.dto.PromptResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
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
	public void testSavePrompt() {
		long reportId = 1L;
		Report report = new Report();
		report.setId(reportId);
		report.setActive("y");
		report.setDisplayName("Reference Number");
		report.setModuleId(1);
		report.setReportCategory("Reference");
		report.setReportDescription("Search");
		report.setReportName("Refernce_No");
		report.setValid("y");

		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.of(report));
		PromptRequestDTO mockPromptDto = new PromptRequestDTO("promptKey", "displaynm", BigInteger.ONE, "y", reportId,
				BigInteger.ONE);
		when(mockpromptsRepository.findPromptOrderByReportId(reportId)).thenReturn(null);
//		Prompts mockPrompt = new Prompts(2, "promptKey", "displaynm", "y", BigInteger.ONE, BigInteger.TWO, report);
//		doNothing().when(mockpromptsRepository.save(mockPrompt));

		promptService.savePrompt(mockPromptDto);

		ArgumentCaptor<Prompts> promptCaptor = ArgumentCaptor.forClass(Prompts.class);
		verify(mockpromptsRepository).save(promptCaptor.capture());
		Prompts result = promptCaptor.getValue();
		assertEquals("displaynm", result.getDisplayName());
		verify(mockreportConfigurationRepo, times(1)).findById(reportId);

	}

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
		PromptRequestDTO mockPromptDTO = new PromptRequestDTO("Reference_Sample", "Reference Sample", BigInteger.ONE,
				"N", 1, BigInteger.ZERO);
		Report mockReportsResponse = new Report();
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
		Report mockReportsResponse = null;
		PromptRequestDTO mockPromptDTO = new PromptRequestDTO("Reference_Sample", "Reference Sample", BigInteger.ONE,
				"N", 1, BigInteger.ZERO);
		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.ofNullable(mockReportsResponse));
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> promptService.updatePromptById(mockPromptDTO, reportId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Report Configuration not exist with this id :1"));
	}

	@Test
	public void testFetchPromptByReportIdExist() {
		long reportId = 1L;
		Report report = new Report();
		report.setId(reportId);
		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.of(report));

		List<Prompts> mockPromptList = new ArrayList<Prompts>();
		Prompts prompt1 = new Prompts();
		prompt1.setId(1L);
		prompt1.setDisplayName("promptKey-1");
		prompt1.setPromptKey("display-1");
		prompt1.setPromptRequired("y");
		prompt1.setPromptOrder(BigInteger.ONE);
		prompt1.setReport(report);

		Prompts prompt2 = new Prompts();
		prompt2.setId(1L);
		prompt2.setDisplayName("promptKey-2");
		prompt2.setPromptKey("display-2");
		prompt2.setPromptRequired("y");
		prompt2.setPromptOrder(BigInteger.ONE);
		prompt2.setReport(report);

		mockPromptList.add(prompt1);
		mockPromptList.add(prompt2);

		when(mockpromptsRepository.findPromptByReportId(reportId)).thenReturn(mockPromptList);

		List<PromptDTO> result = promptService.fetchPromptsByReportId(reportId);
		assertEquals(2, result.size());
		assertEquals(result.get(0).getDisplayName(), "display-1");
		verify(mockreportConfigurationRepo, times(1)).findById(reportId);
		verify(mockpromptsRepository, times(1)).findPromptByReportId(reportId);
	}

	@Test
	public void testFetchPromptByReportIdNotExist() {
		long reportId = 1L;
		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.empty());
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> promptService.fetchPromptsByReportId(reportId),
				"Expected ResourceNotFoundException But It was not");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Report Configuration not exist with this id :1"));
		verify(mockreportConfigurationRepo, times(1)).findById(reportId);
	}

	@Test
	public void testSavePromptIfReportNotExist() {

		PromptRequestDTO mockPromptDto = new PromptRequestDTO("promptKey", "displaynm", BigInteger.ONE, "y", 1L,
				BigInteger.ONE);
		when(mockreportConfigurationRepo.findById(1L)).thenReturn(Optional.empty());
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> promptService.savePrompt(mockPromptDto), "Report Configuration not exist with this id :1");
		assertNotNull(thrown);

	}

}