package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.dao.PromptsDAO;
import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.ModuleResponseDTO;
import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.PromptRequestDTO;
import com.mashreq.paymentTracker.dto.PromptResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.serviceImpl.PromptServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class PromptServiceTest {

	@InjectMocks
	PromptServiceImpl promptService;

	@Mock
	PromptsDAO mockpromptsRepository;

	@Mock
	ReportDAO mockreportConfigurationRepo;

	@Mock
	private ModelMapper modelMapper;
	
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

		when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(report);
		
		PromptRequestDTO mockPromptDto = new PromptRequestDTO("promptKey", "displaynm", BigInteger.ONE, "y", reportId,
				BigInteger.ONE);
		when(mockpromptsRepository.findPromptOrderByReportId(reportId)).thenReturn(null);

		promptService.savePrompt(mockPromptDto);

		ArgumentCaptor<Prompts> promptCaptor = ArgumentCaptor.forClass(Prompts.class);
		verify(mockpromptsRepository).save(promptCaptor.capture());
		Prompts result = promptCaptor.getValue();
		assertEquals("displaynm", result.getDisplayName());
		verify(mockreportConfigurationRepo, times(1)).getReportById(reportId);

	}
	
	@Test
	public void testSavePromptIfReportNotExist() {

		PromptRequestDTO mockPromptDto = new PromptRequestDTO("promptKey", "displaynm", BigInteger.ONE, "y", 1L,
				BigInteger.ONE);
		when(mockreportConfigurationRepo.getReportById(1L)).thenReturn(null);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> promptService.savePrompt(mockPromptDto), "Report Configuration not exist with this id :1");
		assertNotNull(thrown);

	}

	@Test
    public void testFetchAllPrompts() {
		Prompts prompt1 = new Prompts();
		prompt1.setId(1L);
		prompt1.setDisplayName("promptKey-2");
		prompt1.setPromptKey("display-2");
		prompt1.setPromptRequired("y");
		prompt1.setPromptOrder(BigInteger.ONE);
		
        Report report1 = new Report();
        report1.setId(1L);

        prompt1.setReport(report1);

        List<Prompts> promptsList = new ArrayList<>();
        promptsList.add(prompt1);

        when(mockpromptsRepository.findAll()).thenReturn(promptsList);

        List<PromptResponseDTO> result = promptService.fetchAllPrompts();

        verify(mockpromptsRepository).findAll();

        assertNotNull(result);
        assertEquals(1, result.size());

        PromptResponseDTO promptResponseDTO = result.get(0);
        assertNotNull(promptResponseDTO.getReports());
        assertNotNull(promptResponseDTO.getPromptsList());
        assertEquals(1, promptResponseDTO.getPromptsList().size());
        
    }

	@Test
	public void testDeletePromptById() {
		long promptId = 1L;

		doNothing().when(mockpromptsRepository).deleteById(promptId);

		promptService.deletePromptById(promptId);

		verify(mockpromptsRepository).deleteById(1L);
	}

	@Test
    public void testUpdatePromptById() {
        long promptId = 1L;

        PromptRequestDTO promptRequest = new PromptRequestDTO();
        promptRequest.setReportId(1L);
        promptRequest.setDisplayName("Updated Prompt");
        promptRequest.setPromptKey("UpdatedKey");        

        Report report = new Report();
        report.setId(1L);

        Prompts existingPrompt = new Prompts();
        existingPrompt.setId(promptId);
        existingPrompt.setReport(report);

        when(mockreportConfigurationRepo.getReportById(1L)).thenReturn(report);
        when(mockpromptsRepository.getPromptById(promptId)).thenReturn(existingPrompt);
        when(mockpromptsRepository.updatePrompt(any())).thenReturn(existingPrompt);

        PromptDTO result = promptService.updatePromptById(promptRequest, promptId);

        verify(mockreportConfigurationRepo).getReportById(1L);
        verify(mockpromptsRepository).getPromptById(promptId);
        verify(mockpromptsRepository).updatePrompt(any());

        assertNotNull(result);
        assertEquals(promptId, result.getPromptId());
    }


	@Test
	public void testupdateReportByIdNotExists() throws ResourceNotFoundException {
		long reportId = 1L;
		PromptRequestDTO mockPromptDTO = new PromptRequestDTO("Reference_Sample", "Reference Sample", BigInteger.ONE,
				"N", 1, BigInteger.ZERO);
		when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(null);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> promptService.updatePromptById(mockPromptDTO, reportId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertNotNull(thrown.getMessage().contains("Report Configuration not exist with this id :1"));
	}

	@Test
	public void testFetchPromptByReportId() {
		long reportId = 1L;
		Report report = new Report();
		report.setId(reportId);
		when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(report);

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

		when(mockpromptsRepository.getPromptsByReportId(reportId)).thenReturn(mockPromptList);

		List<PromptDTO> result = promptService.fetchPromptsByReportId(reportId);
		assertEquals(2, result.size());
		
		verify(mockpromptsRepository, times(1)).getPromptsByReportId(reportId);
	}

}