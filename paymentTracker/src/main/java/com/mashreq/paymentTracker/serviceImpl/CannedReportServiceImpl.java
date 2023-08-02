package com.mashreq.paymentTracker.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportInstanceComponentDTO;
import com.mashreq.paymentTracker.dto.SWIFTMessageDetailsReportOutput;
import com.mashreq.paymentTracker.dto.CannedReportMetric;
import com.mashreq.paymentTracker.dto.CannedReportPrompt;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.utility.CheckType;

@Component
public class CannedReportServiceImpl implements CannedReportService{
	
	public Set<ReportComponentDTO> populateCannedReportComponent(List<Components> componentList) {
		Set<ReportComponentDTO> cannedReportComponentSet = new HashSet<ReportComponentDTO>();
		if (!componentList.isEmpty()) {
			componentList.stream().forEach(component -> {
				ReportComponentDTO cannedReportComponent = new ReportComponentDTO();
				cannedReportComponent.setId(component.getId());
				cannedReportComponent.setComponentKey(component.getComponentKey());
				cannedReportComponent.setComponentName(component.getComponentName());
				cannedReportComponent.setActive(CheckType.getCheckType(component.getActive()));
				Set<ReportComponentDetailDTO> cannedReportComponentDetailsSet = populateCannedReportComponentDetails(
						component);
				cannedReportComponent.setReportComponentDetails(cannedReportComponentDetailsSet);
				cannedReportComponentSet.add(cannedReportComponent);
			});
		}
		return cannedReportComponentSet;
	}

	private Set<ReportComponentDetailDTO> populateCannedReportComponentDetails(Components component) {
		Set<ReportComponentDetailDTO> cannedReportComponentDetailsSet = new HashSet<ReportComponentDetailDTO>();
		if (!component.getComponentDetailsList().isEmpty()) {
			List<ComponentDetails> componentDetailsList = component.getComponentDetailsList();
			componentDetailsList.stream().forEach(componentDetails -> {
				ReportComponentDetailDTO cannedReportComponentDetail = new ReportComponentDetailDTO();
				cannedReportComponentDetail.setId(componentDetails.getId());
				cannedReportComponentDetail.setQuery(componentDetails.getQuery());
				cannedReportComponentDetail.setQueryKey(componentDetails.getQueryKey());
				cannedReportComponentDetailsSet.add(cannedReportComponentDetail);
			});
		}
		return cannedReportComponentDetailsSet;
	}

	public CannedReport populateCannedReportInstance(Report report) {
		CannedReport cannedReport = new CannedReport();
		if (null != report) {
			cannedReport.setId(report.getId());
			cannedReport.setName(report.getReportName());
			cannedReport.setDisplayName(report.getDisplayName());
			cannedReport.setValid(CheckType.getCheckType(report.getValid()));
			Set<CannedReportPrompt> cannedReportPromptSet = populateReportPrompts(report);
			Set<CannedReportMetric> cannedReportMetricSet = populateReportMetric(report);
			cannedReport.setCannedReportPrompts(cannedReportPromptSet);
			cannedReport.setCannedReportMetrics(cannedReportMetricSet);
		}
		return cannedReport;
	}

	private Set<CannedReportMetric> populateReportMetric(Report report) {
		Set<CannedReportMetric> cannedReportMetricSet = new HashSet<CannedReportMetric>();
		List<Metrics> metricList = report.getMetricsList();
		metricList.forEach(metric -> {
			CannedReportMetric cannedReportMetric = new CannedReportMetric();
			cannedReportMetric.setId(metric.getId());
			cannedReportMetric.setLabel(metric.getDisplayName());
			cannedReportMetric.setOrder(metric.getMetricsOrder().intValue());
			cannedReportMetric.setDisplayable(CheckType.getCheckType(metric.getDisplay()));
			cannedReportMetric.setMetricKey(metric.getDisplayName());
			cannedReportMetricSet.add(cannedReportMetric);
		});
		return cannedReportMetricSet;
	}

	private Set<CannedReportPrompt> populateReportPrompts(Report report) {
		Set<CannedReportPrompt> cannedReportPromptSet = new HashSet<CannedReportPrompt>();
		List<Prompts> promptsList = report.getPromptList();
		promptsList.forEach(prompt -> {
			CannedReportPrompt cannedReportPrompts = new CannedReportPrompt();
			cannedReportPrompts.setId(prompt.getId());
			cannedReportPrompts.setOrder(prompt.getPromptOrder().intValue());
			cannedReportPrompts.setPromptKey(prompt.getPromptKey());
			cannedReportPrompts.setPromptKeyDisplay(prompt.getDisplayName());
			cannedReportPrompts.setRequired(CheckType.getCheckType(prompt.getPromptRequired()));
			cannedReportPromptSet.add(cannedReportPrompts);
		});
		return cannedReportPromptSet;
	}

	@Override
	public List<SWIFTMessageDetailsReportOutput> processMessageDetailsReport(
			MessageDetailsFederatedReportInput reportInputContext,
			List<ReportInstanceComponentDTO> reportInstanceComponentDTO, ReportContext reportContext) {
		// TODO Auto-generated method stub
		return null;
	}
}
