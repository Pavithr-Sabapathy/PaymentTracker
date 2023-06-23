package com.mashreq.paymentTracker.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.MetricsDTO;
import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.service.ModuleService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
public class ReportConfigurationServiceImpl implements ReportConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ReportConfigurationServiceImpl.class);
	private static final String FILENAME = "ReportConfigurationServiceImpl";

	@Autowired
	ReportConfigurationRepository reportConfigurationRepo;

	@Autowired
	UtilityClass utility;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	ModuleService moduleService;

	@Override
	public ReportDTO saveReport(ReportDTORequest reportDTORequest) throws Exception {
		ReportDTO reportDTO = new ReportDTO();
		Report reportResponse = new Report();
		try {
			Report reportRequest = modelMapper.map(reportDTORequest, Report.class);
			log.info(FILENAME + "[saveReport]-->" + reportRequest.toString());
			reportResponse = reportConfigurationRepo.save(reportRequest);
			if (null != reportResponse.getId()) {
				reportDTO = modelMapper.map(reportResponse, ReportDTO.class);
			}

		} catch (Exception exception) {
			throw new Exception(exception.getMessage());
		}

		return reportDTO;

	}

	public Report fetchReportByName(String reportName) {
		Report report = reportConfigurationRepo.findByReportName(reportName);
		return report;
	}

	public ReportDTO updateReportById(ReportDTORequest reportUpdateRequest, long reportId) {
		ReportDTO reportDTO = new ReportDTO();
		Optional<Report> reportReponseOptional = reportConfigurationRepo.findById(reportId);
		if (reportReponseOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
		}
		Report reportConfigurationRequest = modelMapper.map(reportUpdateRequest, Report.class);
		reportConfigurationRequest.setId(reportId);
		Report reportResponse = reportConfigurationRepo.save(reportConfigurationRequest);
		if (null != reportResponse.getId()) {
			reportDTO = modelMapper.map(reportResponse, ReportDTO.class);
		}
		return reportDTO;
	}

	public void deleteReportById(long reportId) {
		if (reportConfigurationRepo.existsById(reportId)) {
			reportConfigurationRepo.deleteById(reportId);
		} else {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
		}

	}

	public List<Report> fetchReportsAsExcel() {
		List<Report> reportsList = reportConfigurationRepo.findAll();

		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		List<PromptDTO> promptDTOList = new ArrayList<PromptDTO>();
		List<MetricsDTO> metricDTOList = new ArrayList<MetricsDTO>();

		for (Report report : reportsList) {

			ReportDTO reportDTO = new ReportDTO();

			reportDTO.setReportName(report.getReportName());
			reportDTO.setReportDescription(report.getReportDescription());
			reportDTO.setReportCategory(report.getReportCategory());
			reportDTO.setDisplayName(report.getDisplayName());
			reportDTO.setActive(report.getActive());
			reportDTO.setValid(report.getValid());
			reportDTO.setModuleId(report.getModuleId());
			reportDTOList.add(reportDTO);
			/** prompts **/
			List<Prompts> promptList = report.getPromptList();
			List<PromptDTO> promptsDTO = promptList.stream().map(Prompt -> modelMapper.map(Prompt, PromptDTO.class))
					.collect(Collectors.toList());
			promptDTOList.addAll(promptsDTO);

			List<Metrics> metricsList = report.getMetricsList();
			List<MetricsDTO> metricsDTO = metricsList.stream().map(metric -> modelMapper.map(metric, MetricsDTO.class))
					.collect(Collectors.toList());
			metricDTOList.addAll(metricsDTO);
		}

		String excelFileName = "ReportsExcel";

		Map<String, List<?>> sheetRowDataList = new HashMap<String, List<?>>();
		sheetRowDataList.put("Report", reportDTOList);
		sheetRowDataList.put("prompts", promptDTOList);
		sheetRowDataList.put("Metrics", metricDTOList);
		try {
			UtilityClass.writeDataSheetWise(excelFileName, sheetRowDataList);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return reportsList;

	}

	@Override
	public ByteArrayOutputStream generateReportPDF() {

		List<Report> reportsList = reportConfigurationRepo.findAll();

		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		List<PromptDTO> promptDTOList = new ArrayList<PromptDTO>();
		List<MetricsDTO> metricDTOList = new ArrayList<MetricsDTO>();
		for (Report report : reportsList) {

			ReportDTO reportDTO = new ReportDTO();

			reportDTO.setReportName(report.getReportName());
			reportDTO.setReportDescription(report.getReportDescription());
			reportDTO.setReportCategory(report.getReportCategory());
			reportDTO.setDisplayName(report.getDisplayName());
			reportDTO.setActive(report.getActive());
			reportDTO.setValid(report.getValid());
			reportDTO.setModuleId(report.getModuleId());
			reportDTOList.add(reportDTO);
			/** prompts **/
			List<Prompts> promptList = report.getPromptList();
			List<PromptDTO> promptsDTO = promptList.stream().map(Prompt -> modelMapper.map(Prompt, PromptDTO.class))
					.collect(Collectors.toList());
			promptDTOList.addAll(promptsDTO);
			/** metrics **/
			List<Metrics> metricsList = report.getMetricsList();
			List<MetricsDTO> metricsDTO = metricsList.stream().map(metric -> modelMapper.map(metric, MetricsDTO.class))
					.collect(Collectors.toList());
			metricDTOList.addAll(metricsDTO);
		}

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			PdfWriter.getInstance(document, out);
			document.open();

			// add heading for Report table as text
			Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
			Paragraph reportTableName = new Paragraph("Report Table", font);
			reportTableName.setAlignment(Element.ALIGN_CENTER);
			document.add(reportTableName);
			document.add(Chunk.NEWLINE);

			PdfPTable reportTable = new PdfPTable(7);
			reportTable.setWidthPercentage(100f);
			// adding PDF table header for report
			Stream.of("Report_Name", "Display_Name", "Description", "Category", "Active", "Valid", "Module_ID")
					.forEach(headerTitle -> {
						addTableHeaderCell(reportTable, headerTitle);
					});
			for (ReportDTO reportDto : reportDTOList) {
				addTableDataCell(reportTable, reportDto.getReportName());
				addTableDataCell(reportTable, reportDto.getDisplayName());
				addTableDataCell(reportTable,
						reportDto.getReportDescription() != null ? reportDto.getReportDescription() : "n/a");
				addTableDataCell(reportTable, reportDto.getReportCategory());
				addTableDataCell(reportTable, reportDto.getActive());
				addTableDataCell(reportTable, reportDto.getValid());
				addTableDataCell(reportTable, String.valueOf(reportDto.getModuleId()));
			}

			document.add(reportTable);
			document.add(Chunk.NEWLINE);

			if (promptDTOList != null) {
				Paragraph promptTableName = new Paragraph("Prompt Table", font);
				promptTableName.setAlignment(Element.ALIGN_CENTER);
				document.add(promptTableName);
				document.add(Chunk.NEWLINE);

				PdfPTable promptTable = new PdfPTable(6);
				promptTable.setWidthPercentage(100f);
				// adding PDF table header for prompt
				Stream.of("Prompt_Key", "Display_Name", "Prompt_Order", "Prompt_Required", "Report_ID", "Entity_ID")
						.forEach(headerTitle -> {
							addTableHeaderCell(promptTable, headerTitle);
						});
				for (PromptDTO promptDto : promptDTOList) {
					addTableDataCell(promptTable, promptDto.getPromptKey());
					addTableDataCell(promptTable, promptDto.getDisplayName());
					addTableDataCell(promptTable,
							String.valueOf(promptDto.getPromptOrder() != null ? promptDto.getPromptOrder() : "n/a"));
					addTableDataCell(promptTable, promptDto.getPromptRequired());
					addTableDataCell(promptTable, String.valueOf(promptDto.getReportId()));
					addTableDataCell(promptTable,
							String.valueOf(promptDto.getEntityId() != null ? promptDto.getEntityId() : "n/a"));
				}

				document.add(promptTable);
				document.add(Chunk.NEWLINE);
			}

			if (metricDTOList != null) {
				Paragraph metricsTableName = new Paragraph("Prompt Table", font);
				metricsTableName.setAlignment(Element.ALIGN_CENTER);
				document.add(metricsTableName);
				document.add(Chunk.NEWLINE);

				PdfPTable metricsTable = new PdfPTable(5);
				metricsTable.setWidthPercentage(100f);
				// adding PDF table header for metrics
				Stream.of("Display_Name", "Metrics_Order", "Display", "Report_ID", "Entity_ID").forEach(headerTitle -> {
					addTableHeaderCell(metricsTable, headerTitle);
				});
				for (MetricsDTO metricDto : metricDTOList) {
					addTableDataCell(metricsTable, metricDto.getDisplayName());
					// addTableDataCell(metricsTable, String.valueOf(metricDto.getMetricsOrder()));
					addTableDataCell(metricsTable,
							String.valueOf(metricDto.getMetricsOrder() != null ? metricDto.getMetricsOrder() : ""));
					addTableDataCell(metricsTable, metricDto.getDisplay());
					addTableDataCell(metricsTable, String.valueOf(metricDto.getReportId()));
					addTableDataCell(metricsTable,
							String.valueOf(metricDto.getEntityId() != null ? metricDto.getEntityId() : "n/a"));
				}

				document.add(metricsTable);
			}

			document.close();

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out;
	}

	private void addTableDataCell(PdfPTable table, String text) {
		PdfPCell cell = new PdfPCell(new Phrase(text));
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(4);
		table.addCell(cell);
	}

	private void addTableHeaderCell(PdfPTable table, String headerTitle) {
		PdfPCell header = new PdfPCell();
		Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		// header.setBackgroundColor(BaseColor.LIGHT_GRAY);
		header.setHorizontalAlignment(Element.ALIGN_CENTER);
		// header.setBorderWidth(1);
		header.setPhrase(new Phrase(headerTitle, headerFont));
		table.addCell(header);
	}

	@Override
	public List<ReportDTO> fetchReportsByModule(String moduleName) {
		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		List<Report> reportList = reportConfigurationRepo.findReportByModule(moduleName);
		if (!reportList.isEmpty()) {
			reportDTOList = reportList.stream().map(report -> modelMapper.map(report, ReportDTO.class))
					.collect(Collectors.toList());

			log.info(FILENAME + "[fetchReportsByModule] " + reportDTOList.toString());
		}
		return reportDTOList;
	}

	@Override
	public List<ReportDTO> fetchReportsByModuleId(Long moduleId) {
		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		List<Report> reportList = reportConfigurationRepo.findByModuleId(moduleId);
		if (!reportList.isEmpty()) {
			reportDTOList = reportList.stream().map(report -> modelMapper.map(report, ReportDTO.class))
					.collect(Collectors.toList());

			log.info(FILENAME + "[fetchReportsByModuleId] " + reportDTOList.toString());
		}
		return reportDTOList;
	}

	@Override
	public ReportDTO fetchReportById(Long reportId) {
		ReportDTO reportDTO = new ReportDTO();
		Optional<Report> reportDataOptional = reportConfigurationRepo.findById(reportId);
		if (reportDataOptional.isPresent()) {
			Report reportObject = reportDataOptional.get();
			reportDTO = modelMapper.map(reportObject, ReportDTO.class);
			log.info(FILENAME + "[fetchReportById] " + reportId + "--->" + reportDTO.toString());
		} else {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
		}
		return reportDTO;
	}

}