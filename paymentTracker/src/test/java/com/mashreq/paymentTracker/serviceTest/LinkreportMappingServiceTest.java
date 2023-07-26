package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.LinkMappingDAO;
import com.mashreq.paymentTracker.dao.MetricsDAO;
import com.mashreq.paymentTracker.dao.PromptsDAO;
import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.LinkedReportDetails;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.serviceImpl.LinkMappingServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class LinkreportMappingServiceTest {
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	LinkMappingServiceImpl linkMappingServiceImpl;

	@Mock
	MetricsDAO mockMetricsRepository;

	@Mock
	PromptsDAO mockpromptsRepository;

	@Mock
	LinkMappingDAO mocklinkMappingRepo;

	@Test
	public void testFetchLinkMappingByIdForMetric() throws JsonMappingException, JsonProcessingException {
		long linkedReportId = 1L;
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		Metrics metricsMockObject = new Metrics();
		metricsMockObject.setDisplay("y");
		metricsMockObject.setDisplayName("sampleMetrics");
		metricsMockObject.setEntityId(BigInteger.ZERO);
		metricsMockObject.setId(1L);
		metricsMockObject.setMetricsOrder(BigInteger.ONE);
		LinkedReportDetails linkedReportDetailsMock = new LinkedReportDetails(1, 2, 5, 4, "M");

		Mockito.when(mocklinkMappingRepo.findByLinkReportPromptId(linkedReportId)).thenReturn(linkedReportDetailsMock);
		Mockito.when(mockMetricsRepository.getMetricsById(linkedReportDetailsMock.getMappedId()))
				.thenReturn((metricsMockObject));
		LinkMappingResponseDTO mocklinkMappingResponseDTO = linkMappingServiceImpl.fetchLinkMappingById(linkedReportId);
		assertEquals(mocklinkMappingResponseDTO.getMappingType(), "M");
		verify(mocklinkMappingRepo, times(1)).findByLinkReportPromptId(linkedReportId);
		verify(mockMetricsRepository, times(1)).getMetricsById(linkedReportDetailsMock.getMappedId());
	}

	@Test
	public void testFetchLinkMappingByIdForPrompt() throws JsonMappingException, JsonProcessingException {
		long linkedReportId = 1L;
		ObjectMapper mapper = new ObjectMapper();
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(5L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		String promptResponseString = "{\"id\":\"1\",\"displayName\": \"Advanced Search\",\"promptKey\": \"4\",\"promptOrder\": 1,\"promptRequired\": \"y\"}";

		Prompts mockPromptResponse = mapper.readValue(promptResponseString, Prompts.class);

		LinkedReportDetails linkedReportDetailsMock = new LinkedReportDetails(1, 2, 4, 4, "P");

		Mockito.when(mocklinkMappingRepo.findByLinkReportPromptId(linkedReportId)).thenReturn(linkedReportDetailsMock);
		when(mockpromptsRepository.getPromptById(linkedReportDetailsMock.getMappedId())).thenReturn(mockPromptResponse);
		when(mockpromptsRepository.getPromptById(linkedReportDetailsMock.getLinkReportPromptId()))
				.thenReturn(mockPromptResponse);

		LinkMappingResponseDTO mocklinkMappingResponseDTO = linkMappingServiceImpl.fetchLinkMappingById(linkedReportId);
		assertEquals(mocklinkMappingResponseDTO.getMappingType(), "P");
		verify(mocklinkMappingRepo, times(1)).findByLinkReportPromptId(linkedReportId);
		verify(mockpromptsRepository, times(1)).getPromptById(linkedReportDetailsMock.getLinkReportPromptId());
	}

	@Test
	void testMetricsThrowsException() {
		long linkedReportId = 1L;
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		LinkedReportDetails linkedReportDetailsMock = new LinkedReportDetails(1, 2, 4, 4, "M");
		Mockito.when(mocklinkMappingRepo.findByLinkReportPromptId(linkedReportId)).thenReturn(linkedReportDetailsMock);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> linkMappingServiceImpl.fetchLinkMappingById(linkedReportId),
				"Expected linkMappingServiceImpl.findByLinkReportPromptId to throw");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains(ApplicationConstants.METRICS_DOES_NOT_EXISTS));
	}

	@Test
	void testPromptThrowsException() {
		long linkedReportId = 1L;
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		LinkedReportDetails linkedReportDetailsMock = new LinkedReportDetails(1, 2, 4, 4, "P");
		Mockito.when(mocklinkMappingRepo.findByLinkReportPromptId(linkedReportId)).thenReturn(linkedReportDetailsMock);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> linkMappingServiceImpl.fetchLinkMappingById(linkedReportId),
				"Expected linkMappingServiceImpl.findByLinkReportPromptId to throw");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains(ApplicationConstants.PROMPTS_DOES_NOT_EXISTS));
	}

	@Test
	public void testSaveorUpdate() {
		LinkedReportMappingRequestDTO linkedReportMappingRequestDTO = new LinkedReportMappingRequestDTO(1, 3, 4, 5,
				"M");
		LinkedReportDetails linkedReportDetailsMock = new LinkedReportDetails(1, 2, 4, 4, "M");
		when(modelMapper.map(linkedReportMappingRequestDTO, LinkedReportDetails.class))
				.thenReturn(linkedReportDetailsMock);
		when(mocklinkMappingRepo.save(any(LinkedReportDetails.class))).thenReturn(linkedReportDetailsMock);
		linkMappingServiceImpl.saveOrUpdateLinkMapping(linkedReportMappingRequestDTO);
		ArgumentCaptor<LinkedReportDetails> argumentCaptor = ArgumentCaptor.forClass(LinkedReportDetails.class);
		verify(mocklinkMappingRepo).update(argumentCaptor.capture());
		LinkedReportDetails saveLinkedReportDetails = argumentCaptor.getValue();
		assertEquals(linkedReportMappingRequestDTO.getMappingType(), saveLinkedReportDetails.getMappingType());
	}
}