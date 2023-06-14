package com.mashreq.paymentTracker.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.model.Report;

@Component
public class UtilityClass {
	/*** static code ****/
	public void writeToMasterExcel(List<Report> reportsList) throws IOException, InvalidFormatException {

		String[] columns = { "reportName", "displayName", "reportDescription", "reportCategory", "active", "valid" };
		// Create a Workbook
		HSSFWorkbook workbook = new HSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		// Create a Sheet
		HSSFSheet sheet = workbook.createSheet("Report Sheet");

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		Row headerRow = sheet.createRow(0);

		// Create cells

		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}
		// Create Other rows and cells with employees data
		int rowNum = 1;
		for (Report Report : reportsList) {

			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(Report.getReportName());
			row.createCell(1).setCellValue(Report.getDisplayName());
			row.createCell(2).setCellValue(Report.getReportDescription());
			row.createCell(3).setCellValue(Report.getReportCategory());
			row.createCell(4).setCellValue(Report.getActive());
			row.createCell(5).setCellValue(Report.getValid());
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream(new File("sample.xlsx"));
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();
	}

	/*** Dynamic content based on sheet ****/
	public static void writeDataSheetWise(final String excelFileName, Map<String, List<?>> sheetRowDataList)
			throws IOException, InvalidFormatException, IllegalArgumentException, IllegalAccessException {
		HSSFWorkbook workbook = new HSSFWorkbook();

		for (String sheetName : sheetRowDataList.keySet()) {
			createSheet(workbook, sheetName, sheetRowDataList.get(sheetName));
		}

		try {
			System.out.println("\nWritting data to excel file <" + excelFileName + ">");

			FileOutputStream outputStream = new FileOutputStream(new File("sample.xlsx"));
			workbook.write(outputStream);
			outputStream.flush();
			outputStream.close();

			System.out.println("\nData is successfully written to excel file <" + excelFileName + ">.");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean writeData(final String excelFileName, final String sheetName, List<?> rowDataList)
			throws IOException, InvalidFormatException, IllegalArgumentException, IllegalAccessException {

		boolean isWritten = false;
		HSSFWorkbook workbook = new HSSFWorkbook();
		createSheet(workbook, sheetName, rowDataList);
		try {
			System.out.println("\nWritting data to excel file <" + excelFileName + ">");

			FileOutputStream outputStream = new FileOutputStream(new File(excelFileName));
			workbook.write(outputStream);
			outputStream.flush();
			outputStream.close();
			isWritten = true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isWritten;

	}

	@SuppressWarnings("deprecation")
	private static void createSheet(final HSSFWorkbook workbook, final String sheetName, final List<?> rowDataList)
			throws IllegalArgumentException, IllegalAccessException {

		HSSFSheet sheet = workbook.createSheet(sheetName);
		List<String> headerList = new ArrayList<String>();
		int rowCount = 0;

		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont headersFont = workbook.createFont();
		headersFont.setFontName(HSSFFont.FONT_ARIAL);
		headersFont.setFontHeightInPoints((short) 16);
		headersFont.setColor(HSSFColor.GREEN.index);
		style.setFont(headersFont);

		// Creating header row
		Row headerRow = sheet.createRow(rowCount++);
		for (Field field : rowDataList.get(0).getClass().getDeclaredFields()) {
			if (Modifier.isPrivate(field.getModifiers())) {
				headerList.add(field.getName());
			}
		}

		for (int i = 0; i < headerList.size(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(headerList.get(i));
			sheet.autoSizeColumn(i);
		}

		for (Object value1 : rowDataList) {
			Row row = sheet.createRow(rowCount++);
			int cellnum = 0;
			Field[] fields = rowDataList.get(0).getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Cell cell = row.createCell(cellnum++);
				fields[i].setAccessible(true);
				Object value = fields[i].get(value1);
				if (value instanceof Date)
					cell.setCellValue((Date) value);
				else if (value instanceof Boolean)
					cell.setCellValue((Boolean) value);
				else if (value instanceof String)
					cell.setCellValue((String) value);
				else if (value instanceof Integer)
					cell.setCellValue((Integer) value);
				else if (value instanceof Long)
					cell.setCellValue((Long) value);
				else if (value instanceof Double)
					cell.setCellValue((Double) value);
			}
		}
	}

	public static String getStringRepresentation(Object colValue) {
		String colString = null;
		if (colValue != null) {
			colString = colValue.toString().trim();
		}
		return colString;
	}

	public static boolean isOutgoingPaymentMessage(String messageFormat, String messageType) {
		boolean outgoingPayment = false;
		if (messageFormat.equalsIgnoreCase(MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT)) {
			List<String> outgoingCodes = MashreqFederatedReportConstants.OUTGOING_PAYMENT_CODES_LIST;
			return outgoingCodes.stream().anyMatch(outgoingcode -> outgoingcode.equalsIgnoreCase(messageType));
		}
		return outgoingPayment;
	}

	public static Integer getNumberRepresentation(Object colValue) {
		Integer colString = null;
		if (colValue != null) {
			colString = Integer.parseInt(colValue.toString().trim());
		}
		return colString;
	}

	public static boolean isGpiTrchEnabledMessage(String receiver, String sender) {
		boolean isGpiTrchEnabledMessage = false;
		if ((null != receiver) && (receiver.toUpperCase().startsWith(MashreqFederatedReportConstants.GPI_ENABLED_TRCH_CODE))
				|| ((null != sender)
						&& (sender.toUpperCase().startsWith(MashreqFederatedReportConstants.GPI_ENABLED_TRCH_CODE)))) {
			isGpiTrchEnabledMessage = true;
		}
		return isGpiTrchEnabledMessage;
	}

	public static boolean isGpiIpalaEnabledMessage(String receiver, String sender) {
		boolean isGpiIpalaEnabledMessage = false;
		if (null != receiver && receiver.toUpperCase().startsWith(MashreqFederatedReportConstants.GPI_ENABLED_IPALA_CODE)
				|| ((null != sender) && sender.toUpperCase().startsWith(MashreqFederatedReportConstants.GPI_ENABLED_IPALA_CODE))) {
			isGpiIpalaEnabledMessage = true;
		}
		return isGpiIpalaEnabledMessage;
	}

	public static String combineValues(String... values) {
		String combinedValue = "";
		int index = 1;
		for (String val : values) {
			if (null == val) {
				continue;
			}
			if (index == 1) {
				combinedValue = val;
			} else {
				combinedValue += MashreqFederatedReportConstants.SEMI_COLON + val;
			}
			index++;
		}
		return combinedValue;
	}

	public static String combineValuesWithBreakTag(String... values) {
		String corrDetails = "";
		List<String> tokens = new ArrayList<String>();

		for (String value : values) {
			if (null != value) {
				tokens.add(value);
			}
		}

		if (!tokens.isEmpty()) {
			corrDetails = tokens.stream().collect(Collectors.joining(MashreqFederatedReportConstants.BREAK_TAG));
		}
		return corrDetails;
	}

	public static boolean ignoreCaseContains(List<String> theList, String searchStr) {
		for (String s : theList) {
			if (searchStr.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}
	
	 public static String populateSeriesOfQuestionSymbol (String promptKey, List<String> promptValueList) {
	      if (!promptValueList.isEmpty()) {
	         return promptValueList.stream().map(x -> ApplicationConstants.QUESTION_MARK).collect(Collectors.joining(ApplicationConstants.COMMA));
	      } else {
	         return null;
	      }
	   }
	 
	 public static String getCommaSeperatedStringRepresentation(List<String> valueList) {
	        StringBuilder result = new StringBuilder();
	        if (!valueList.isEmpty()) {
	            int counter = 1;
	            for (String value : valueList) {
	                result.append(ApplicationConstants.SINGLE_QUOTE).append(value).append(ApplicationConstants.SINGLE_QUOTE);
	                if (counter < valueList.size()) {
	                    result.append(ApplicationConstants.COMMA);
	                    counter++;
	                }
	            }
	        }
	        return result.toString();
	    }
}