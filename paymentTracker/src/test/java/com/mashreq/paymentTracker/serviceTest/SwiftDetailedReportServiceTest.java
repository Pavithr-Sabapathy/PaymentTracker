package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsCountryRepository;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.serviceImpl.SwiftDetailedReportServiceImpl;
import com.mashreq.paymentTracker.type.CountryType;
import com.mashreq.paymentTracker.utility.CheckType;

@ContextConfiguration(classes = {SwiftDetailedReportServiceImpl.class})
@ExtendWith(SpringExtension.class)
class SwiftDetailedReportServiceTest {
    @MockBean
    private CannedReportService cannedReportService;

    @MockBean
    private ComponentsCountryRepository componentsCountryRepository;

    @MockBean
    private ComponentsRepository componentsRepository;

    @MockBean
    private LinkReportService linkReportService;

    @MockBean
    private QueryExecutorService queryExecutorService;

    @MockBean
    private ReportConfigurationService reportConfigurationService;

    @Autowired
    private SwiftDetailedReportServiceImpl swiftDetailedReportServiceImpl;

    @Test
    void testProcessSwiftDetailReport() {
        CannedReport cannedReport = new CannedReport();
        cannedReport.setActive(CheckType.NO);
        cannedReport.setAppId(1L);
        cannedReport.setCannedReportComponents(new HashSet<>());
        cannedReport.setCannedReportMetrics(new HashSet<>());
        cannedReport.setCannedReportPrompts(new HashSet<>());
        cannedReport.setDeleted(CheckType.NO);
        cannedReport.setDisplayName("Display Name");
        cannedReport.setId(1L);
        cannedReport.setName("Name");
        cannedReport.setValid(CheckType.NO);
        when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);
        when(componentsRepository.findAllByreportId(anyLong())).thenReturn(Optional.of(new ArrayList<>()));
        when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(new Report());

        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
        reportInstanceDTO
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportInstanceDTO.setId(1L);
        reportInstanceDTO.setModuleId(1L);
        reportInstanceDTO.setPromptsList(new ArrayList<>());
        reportInstanceDTO.setReportId(1L);
        reportInstanceDTO.setReportInstanceComponents(new HashSet<>());
        reportInstanceDTO.setReportInstanceMetrics(new HashSet<>());
        reportInstanceDTO.setReportInstancePrompts(new HashSet<>());
        reportInstanceDTO.setReportName("Report Name");
        reportInstanceDTO.setRoleId(1L);
        reportInstanceDTO.setRoleName("Role Name");
        reportInstanceDTO.setUserId(1L);
        reportInstanceDTO.setUserName("janedoe");

        ReportInstanceDTO reportInstance = new ReportInstanceDTO();
        reportInstance
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportInstance.setId(1L);
        reportInstance.setModuleId(1L);
        reportInstance.setPromptsList(new ArrayList<>());
        reportInstance.setReportId(1L);
        reportInstance.setReportInstanceComponents(new HashSet<>());
        reportInstance.setReportInstanceMetrics(new HashSet<>());
        reportInstance.setReportInstancePrompts(new HashSet<>());
        reportInstance.setReportName("Report Name");
        reportInstance.setRoleId(1L);
        reportInstance.setRoleName("Role Name");
        reportInstance.setUserId(1L);
        reportInstance.setUserName("janedoe");

        ReportContext reportContext = new ReportContext();
        reportContext.setCountry(CountryType.UAE);
        reportContext.setExecutionId(1L);
        reportContext.setLinkInstanceId(1L);
        reportContext.setLinkReference("Link Reference");
        reportContext.setLinkedReport(true);
        reportContext.setModuleId(1L);
        reportContext.setReportId(1L);
        reportContext.setReportInstance(reportInstance);
        reportContext.setReportName("Report Name");
        reportContext.setRoleId(1L);
        reportContext.setRoleName("Role Name");
        reportContext.setUserId(1L);
        reportContext.setUserName("janedoe");
        ReportExecuteResponseData actualProcessSwiftDetailReportResult = swiftDetailedReportServiceImpl
                .processReport(reportInstanceDTO, reportContext);
        assertNull(actualProcessSwiftDetailReportResult.getColumnDefs());
        assertNull(actualProcessSwiftDetailReportResult.getData());
        verify(cannedReportService).populateCannedReportInstance(Mockito.<Report>any());
        verify(componentsRepository).findAllByreportId(anyLong());
        verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
    }

    @Test
    void testProcessSwiftDetailReport2() {
        CannedReport cannedReport = new CannedReport();
        cannedReport.setActive(CheckType.NO);
        cannedReport.setAppId(1L);
        cannedReport.setCannedReportComponents(new HashSet<>());
        cannedReport.setCannedReportMetrics(new HashSet<>());
        cannedReport.setCannedReportPrompts(new HashSet<>());
        cannedReport.setDeleted(CheckType.NO);
        cannedReport.setDisplayName("Display Name");
        cannedReport.setId(1L);
        cannedReport.setName("Name");
        cannedReport.setValid(CheckType.NO);
        when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);
        when(componentsRepository.findAllByreportId(anyLong())).thenReturn(Optional.of(new ArrayList<>()));
        when(reportConfigurationService.fetchReportByName(Mockito.<String>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
        reportInstanceDTO
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportInstanceDTO.setId(1L);
        reportInstanceDTO.setModuleId(1L);
        reportInstanceDTO.setPromptsList(new ArrayList<>());
        reportInstanceDTO.setReportId(1L);
        reportInstanceDTO.setReportInstanceComponents(new HashSet<>());
        reportInstanceDTO.setReportInstanceMetrics(new HashSet<>());
        reportInstanceDTO.setReportInstancePrompts(new HashSet<>());
        reportInstanceDTO.setReportName("Report Name");
        reportInstanceDTO.setRoleId(1L);
        reportInstanceDTO.setRoleName("Role Name");
        reportInstanceDTO.setUserId(1L);
        reportInstanceDTO.setUserName("janedoe");

        ReportInstanceDTO reportInstance = new ReportInstanceDTO();
        reportInstance
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportInstance.setId(1L);
        reportInstance.setModuleId(1L);
        reportInstance.setPromptsList(new ArrayList<>());
        reportInstance.setReportId(1L);
        reportInstance.setReportInstanceComponents(new HashSet<>());
        reportInstance.setReportInstanceMetrics(new HashSet<>());
        reportInstance.setReportInstancePrompts(new HashSet<>());
        reportInstance.setReportName("Report Name");
        reportInstance.setRoleId(1L);
        reportInstance.setRoleName("Role Name");
        reportInstance.setUserId(1L);
        reportInstance.setUserName("janedoe");

        ReportContext reportContext = new ReportContext();
        reportContext.setCountry(CountryType.UAE);
        reportContext.setExecutionId(1L);
        reportContext.setLinkInstanceId(1L);
        reportContext.setLinkReference("Link Reference");
        reportContext.setLinkedReport(true);
        reportContext.setModuleId(1L);
        reportContext.setReportId(1L);
        reportContext.setReportInstance(reportInstance);
        reportContext.setReportName("Report Name");
        reportContext.setRoleId(1L);
        reportContext.setRoleName("Role Name");
        reportContext.setUserId(1L);
        reportContext.setUserName("janedoe");
        assertThrows(ResourceNotFoundException.class,
                () -> swiftDetailedReportServiceImpl.processReport(reportInstanceDTO, reportContext));
        verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
    }
 @Test
    void testProcessSwiftDetailReport3() {
        CannedReport cannedReport = new CannedReport();
        cannedReport.setActive(CheckType.NO);
        cannedReport.setAppId(1L);
        cannedReport.setCannedReportComponents(new HashSet<>());
        cannedReport.setCannedReportMetrics(new HashSet<>());
        cannedReport.setCannedReportPrompts(new HashSet<>());
        cannedReport.setDeleted(CheckType.NO);
        cannedReport.setDisplayName("Display Name");
        cannedReport.setId(1L);
        cannedReport.setName("Name");
        cannedReport.setValid(CheckType.NO);
        when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);

        ArrayList<Components> componentsList = new ArrayList<>();
        componentsList.add(new Components());
        Optional<List<Components>> ofResult = Optional.of(componentsList);
        when(componentsRepository.findAllByreportId(anyLong())).thenReturn(ofResult);
        when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(new Report());

        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
        reportInstanceDTO
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportInstanceDTO.setId(1L);
        reportInstanceDTO.setModuleId(1L);
        reportInstanceDTO.setPromptsList(new ArrayList<>());
        reportInstanceDTO.setReportId(1L);
        reportInstanceDTO.setReportInstanceComponents(new HashSet<>());
        reportInstanceDTO.setReportInstanceMetrics(new HashSet<>());
        reportInstanceDTO.setReportInstancePrompts(new HashSet<>());
        reportInstanceDTO.setReportName("Report Name");
        reportInstanceDTO.setRoleId(1L);
        reportInstanceDTO.setRoleName("Role Name");
        reportInstanceDTO.setUserId(1L);
        reportInstanceDTO.setUserName("janedoe");

        ReportInstanceDTO reportInstance = new ReportInstanceDTO();
        reportInstance
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportInstance.setId(1L);
        reportInstance.setModuleId(1L);
        reportInstance.setPromptsList(new ArrayList<>());
        reportInstance.setReportId(1L);
        reportInstance.setReportInstanceComponents(new HashSet<>());
        reportInstance.setReportInstanceMetrics(new HashSet<>());
        reportInstance.setReportInstancePrompts(new HashSet<>());
        reportInstance.setReportName("Report Name");
        reportInstance.setRoleId(1L);
        reportInstance.setRoleName("Role Name");
        reportInstance.setUserId(1L);
        reportInstance.setUserName("janedoe");

        ReportContext reportContext = new ReportContext();
        reportContext.setCountry(CountryType.UAE);
        reportContext.setExecutionId(1L);
        reportContext.setLinkInstanceId(1L);
        reportContext.setLinkReference("Link Reference");
        reportContext.setLinkedReport(true);
        reportContext.setModuleId(1L);
        reportContext.setReportId(1L);
        reportContext.setReportInstance(reportInstance);
        reportContext.setReportName("Report Name");
        reportContext.setRoleId(1L);
        reportContext.setRoleName("Role Name");
        reportContext.setUserId(1L);
        reportContext.setUserName("janedoe");
        ReportExecuteResponseData actualProcessSwiftDetailReportResult = swiftDetailedReportServiceImpl
                .processReport(reportInstanceDTO, reportContext);
        assertNull(actualProcessSwiftDetailReportResult.getColumnDefs());
        assertNull(actualProcessSwiftDetailReportResult.getData());
        verify(cannedReportService).populateCannedReportInstance(Mockito.<Report>any());
        verify(componentsRepository).findAllByreportId(anyLong());
        verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
    }

   
    @Test
    void testProcessSwiftDetailReport4() {
        CannedReport cannedReport = new CannedReport();
        cannedReport.setActive(CheckType.NO);
        cannedReport.setAppId(1L);
        cannedReport.setCannedReportComponents(new HashSet<>());
        cannedReport.setCannedReportMetrics(new HashSet<>());
        cannedReport.setCannedReportPrompts(new HashSet<>());
        cannedReport.setDeleted(CheckType.NO);
        cannedReport.setDisplayName("Display Name");
        cannedReport.setId(1L);
        cannedReport.setName("Name");
        cannedReport.setValid(CheckType.NO);
        when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);
        when(componentsRepository.findAllByreportId(anyLong())).thenReturn(Optional.empty());
        when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(new Report());

        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
        reportInstanceDTO
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportInstanceDTO.setId(1L);
        reportInstanceDTO.setModuleId(1L);
        reportInstanceDTO.setPromptsList(new ArrayList<>());
        reportInstanceDTO.setReportId(1L);
        reportInstanceDTO.setReportInstanceComponents(new HashSet<>());
        reportInstanceDTO.setReportInstanceMetrics(new HashSet<>());
        reportInstanceDTO.setReportInstancePrompts(new HashSet<>());
        reportInstanceDTO.setReportName("Report Name");
        reportInstanceDTO.setRoleId(1L);
        reportInstanceDTO.setRoleName("Role Name");
        reportInstanceDTO.setUserId(1L);
        reportInstanceDTO.setUserName("janedoe");

        ReportInstanceDTO reportInstance = new ReportInstanceDTO();
        reportInstance
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reportInstance.setId(1L);
        reportInstance.setModuleId(1L);
        reportInstance.setPromptsList(new ArrayList<>());
        reportInstance.setReportId(1L);
        reportInstance.setReportInstanceComponents(new HashSet<>());
        reportInstance.setReportInstanceMetrics(new HashSet<>());
        reportInstance.setReportInstancePrompts(new HashSet<>());
        reportInstance.setReportName("Report Name");
        reportInstance.setRoleId(1L);
        reportInstance.setRoleName("Role Name");
        reportInstance.setUserId(1L);
        reportInstance.setUserName("janedoe");

        ReportContext reportContext = new ReportContext();
        reportContext.setCountry(CountryType.UAE);
        reportContext.setExecutionId(1L);
        reportContext.setLinkInstanceId(1L);
        reportContext.setLinkReference("Link Reference");
        reportContext.setLinkedReport(true);
        reportContext.setModuleId(1L);
        reportContext.setReportId(1L);
        reportContext.setReportInstance(reportInstance);
        reportContext.setReportName("Report Name");
        reportContext.setRoleId(1L);
        reportContext.setRoleName("Role Name");
        reportContext.setUserId(1L);
        reportContext.setUserName("janedoe");
        assertThrows(ResourceNotFoundException.class,
                () -> swiftDetailedReportServiceImpl.processReport(reportInstanceDTO, reportContext));
        verify(cannedReportService).populateCannedReportInstance(Mockito.<Report>any());
        verify(componentsRepository).findAllByreportId(anyLong());
        verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
    }
}

