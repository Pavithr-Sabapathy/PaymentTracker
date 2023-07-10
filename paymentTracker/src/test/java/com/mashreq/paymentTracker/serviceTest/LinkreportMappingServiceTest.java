package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;
import com.mashreq.paymentTracker.model.LinkedReportDetails;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.LinkMappingRepository;
import com.mashreq.paymentTracker.repository.MetricsRepository;
import com.mashreq.paymentTracker.repository.PromptsRepository;
import com.mashreq.paymentTracker.serviceImpl.LinkMappingServiceImpl;

@ExtendWith(MockitoExtension.class)
public class LinkreportMappingServiceTest {
	@Mock
	ModelMapper modelMapper;
	
	@InjectMocks
	LinkMappingServiceImpl linkMappingServiceImpl;
	
	@Mock
	MetricsRepository mockMetricsRepository;

	@Mock
	PromptsRepository mockpromptsRepository;

	@Mock
	LinkMappingRepository mocklinkMappingRepo;

	@Test
	public void testFetchLinkMappingByIdForMetric() throws JsonMappingException, JsonProcessingException {
		long linkedReportId = 1L;
		List<LinkedReportDetails> linkMappingdeatilsListMock = new ArrayList<LinkedReportDetails>();
		ObjectMapper mapper = new ObjectMapper();
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

		Metrics metricsMockObject = new Metrics();
		metricsMockObject.setDisplay("y");
		metricsMockObject.setDisplayName("sampleMetrics");
		metricsMockObject.setEntityId(BigInteger.ZERO);
		metricsMockObject.setId(1L);
		metricsMockObject.setMetricsOrder(BigInteger.ONE);
		LinkedReportDetails linkedReportDetailsMock = new LinkedReportDetails(1, 2, 5, 4, "M");

		Mockito.when(mocklinkMappingRepo.findByLinkReportPromptId(linkedReportId)).thenReturn(linkedReportDetailsMock);
		Mockito.when(mockMetricsRepository.findById(linkedReportDetailsMock.getMappedId()))
				.thenReturn(Optional.of(metricsMockObject));
		when(mockpromptsRepository.findById(linkedReportDetailsMock.getLinkReportPromptId()))
				.thenReturn(Optional.of(mockPromptResponse));

		LinkMappingResponseDTO linkMappingResponseDTOList = linkMappingServiceImpl
				.fetchLinkMappingById(linkedReportId);
		//assertEquals(linkMappingResponseDTOList.size(), 1);
		verify(mocklinkMappingRepo, times(1)).findByLinkReportPromptId(linkedReportId);
		verify(mockMetricsRepository, times(1)).findById(linkedReportDetailsMock.getMappedId());
		verify(mockpromptsRepository, times(1)).findById(linkedReportDetailsMock.getLinkReportPromptId());
	}

	@Test
	public void testFetchLinkMappingByIdForPrompt() throws JsonMappingException, JsonProcessingException {
		long linkedReportId = 1L;
		List<LinkedReportDetails> linkMappingdeatilsListMock = new ArrayList<LinkedReportDetails>();
		ObjectMapper mapper = new ObjectMapper();
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(5L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		String promptResponseString = "{\"displayName\": \"Advanced Search\",\"entityId\": 0,\"promptKey\": \"5\",\"promptOrder\": 1,\"promptRequired\": \"y\"}";

		Prompts mockPromptResponse = mapper.readValue(promptResponseString, Prompts.class);

		LinkedReportDetails linkedReportDetailsMock = new LinkedReportDetails(1, 2, 5, 4, "P");

		Mockito.when(mocklinkMappingRepo.findByLinkReportPromptId(linkedReportId)).thenReturn(linkedReportDetailsMock);
		when(mockpromptsRepository.findById(linkedReportDetailsMock.getMappedId()))
				.thenReturn(Optional.of(mockPromptResponse));
		when(mockpromptsRepository.findById(linkedReportDetailsMock.getLinkReportPromptId()))
				.thenReturn(Optional.of(mockPromptResponse));

		 LinkMappingResponseDTO linkMappingResponseDTOList = linkMappingServiceImpl
				.fetchLinkMappingById(linkedReportId);
		//assertEquals(linkMappingResponseDTOList.size(), 1);
		verify(mocklinkMappingRepo, times(1)).findByLinkReportPromptId(linkedReportId);
		verify(mockpromptsRepository, times(1)).findById(linkedReportDetailsMock.getLinkReportPromptId());
	}
	
	@Test
	public void testSaveorUpdate() {
		LinkedReportMappingRequestDTO linkedReportMappingRequestDTO = new LinkedReportMappingRequestDTO(1,3, 4, 5,"M");
		LinkedReportDetails linkedReportDetailsMock = modelMapper.map(linkedReportMappingRequestDTO, LinkedReportDetails.class);
		when(mocklinkMappingRepo.save(linkedReportDetailsMock)).thenReturn(linkedReportDetailsMock);
		linkMappingServiceImpl.saveOrUpdateLinkMapping(linkedReportMappingRequestDTO);
	
	}
}