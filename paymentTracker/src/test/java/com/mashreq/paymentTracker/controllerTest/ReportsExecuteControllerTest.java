package com.mashreq.paymentTracker.controllerTest;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.controller.ReportsExecuteController;
import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseMetaDTO;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.service.ReportHandlerService;

@ContextConfiguration(classes = {ReportsExecuteController.class})
@ExtendWith(SpringExtension.class)
class ReportsExecuteControllerTest {
    @MockBean
    private ReportHandlerService reportHandlerService;

    @Autowired
    private ReportsExecuteController reportsExecuteController;


    @Test
    void testExecuteReportTestCase1() throws Exception {

        ReportExecuteResponseMetaDTO meta = new ReportExecuteResponseMetaDTO();
        meta.setEndTime("End Time");
        meta.setExecutionTime(1L);
        meta.setReportId("swiftDetails");
        meta.setStartTime("Start Time");
        meta.setTotalExists(true);

        ReportExecuteResponseData reportExecuteResponseData = new ReportExecuteResponseData();
        reportExecuteResponseData.setColumnDefs(new ArrayList<>());
        reportExecuteResponseData.setData(new ArrayList<>());
        reportExecuteResponseData.setMeta(meta);

        APIResponse apiResponse = new APIResponse();
        apiResponse.setData("Data");
        apiResponse.setMessage("Not all who wander are lost");
        apiResponse.setStatus(true);

        when(reportHandlerService.executeReport(Mockito.<String>any(), Mockito.<ReportExecutionRequest>any()))
                .thenReturn(reportExecuteResponseData);
        when(reportHandlerService.populateSuccessAPIRespone(Mockito.<ReportExecuteResponseData>any()))
                .thenReturn(apiResponse);

        ReportExecutionRequest reportExecutionRequest = new ReportExecutionRequest();

        reportExecutionRequest
                .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportExecutionRequest.setIsMapLinked(true);
        reportExecutionRequest.setLinkExecution(true);
        reportExecutionRequest.setLinkInstanceId(1L);
        reportExecutionRequest.setLinkReference("Link Reference");
        reportExecutionRequest.setPrompts(new ArrayList<>());
        reportExecutionRequest.setRole("MASHREQ_REPORTING_ROLE");
        reportExecutionRequest.setUserId(1L);
        reportExecutionRequest.setUserName("Mashreq");

        String content = (new ObjectMapper()).writeValueAsString(reportExecutionRequest);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/report/{reportName}/execute", "Report Name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(reportsExecuteController)
                .build()
                .perform(requestBuilder);

        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("<APIResponse><data>Data</data><message>Not all who wander are lost</message><status>true</status><"
                                + "/APIResponse>"));
    }


    @Test
    void testExecuteReportTestCase2() throws Exception {
                
        ReportExecutionRequest reportExecutionRequest = new ReportExecutionRequest();
        reportExecutionRequest
                .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportExecutionRequest.setIsMapLinked(true);
        reportExecutionRequest.setLinkExecution(true);
        reportExecutionRequest.setLinkInstanceId(1L);
        reportExecutionRequest.setLinkReference("Link Reference");
        reportExecutionRequest.setPrompts(new ArrayList<>());
        reportExecutionRequest.setRole("MASHREQ_REPORTING_ROLE");
        reportExecutionRequest.setUserId(1L);
        reportExecutionRequest.setUserName("Mashreq");

        APIResponse apiResponse = new APIResponse();
        apiResponse.setData("Data");
        apiResponse.setMessage("Not all who wander are lost");
        apiResponse.setStatus(true);

        when(reportHandlerService.executeReport(Mockito.<String>any(), Mockito.<ReportExecutionRequest>any()))
        .thenThrow(new ReportException("An error occurred"));
        
        when(reportHandlerService.populateSuccessAPIRespone(Mockito.<ReportExecuteResponseData>any()))
                .thenReturn(apiResponse);

        
        String content = (new ObjectMapper()).writeValueAsString(reportExecutionRequest);
        
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/report/{reportName}/execute", "Report Name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(reportsExecuteController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(500));
    }
}

