package maxi.rmreporting.formatting;

import static maxi.rmreporting.api.international.FileNamesAndColumnTitlesEn.*;
import static maxi.rmreporting.api.international.RetentionManagersForexTableRowsInt.*;
import static maxi.rmreporting.utilities.XSSFReadWriteInternational.*;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import maxi.rmreporting.utilities.XSSFReadWriteInternational;

public class StylesEn extends Styles {

	private XSSFCellStyle cs;
	private XSSFFont f;

	public StylesEn() {

	}

	@Override
	public void setFirstColumnStyle(XSSFCell cell, int rownum, XSSFWorkbook wb) throws IOException {

		if (cell != null) {

			// Formula Not Main
			if (rownum == NET_DEPOSITS_ROW_NUMBER_INT || rownum == CREDITS_DIVIDED_BY_DEPOSITS_INT
					|| rownum == AVERAGE_DEPOSIT_ROWNUM_INT || rownum == ACTIVE_USERS_DIV_TOTAL_CLIENTS_ROWNUM_INT
					|| rownum == ACTIVE_NEW_USERS_ROWNUM_INT || rownum == NEW_USERS_VOL_DIVIDED_VOL_ROWNUM_INT) {
				cell.setCellStyle(getStyleFirstColumnFormula(wb));

				// Simple Not Main
			} else if (rownum == CREDITS_REDEPOSITS_INT || rownum == CREDITS_PRIOR_INT || rownum == TOTAL_WITHDRAWALS_ROWNUM_INT
					|| rownum == NUMBER_OF_DEPOSITS_ROWNUM_INT || rownum == DEALS_PNL_ROWNUM_INT
					|| rownum == TRADES_OPENED_VIA_DEALING_ROWNUM_INT || rownum == ACTIVE_USERS_ROWNUM_INT
					|| rownum == TOTAL_CLIENTS_EOP_ROWNUM_INT || rownum == ACCOUNT_VALUE_BOP_ROWNUM_INT
					|| rownum == ACCOUNT_VALUE_EOP_ROWNUM_INT || rownum == NEW_USERS60_ROWNUM_INT
					|| rownum == NEW_USERS_MORE5_ROWNUM_INT || rownum == NEW_USERS_REDEPOSITED_MORE450_ROWNUM_INT
					|| rownum == NEW_USERS_REDEPOSITS_NUMBER_MORE450_ROWNUM_INT
					|| rownum == NEW_USERS_REDEPOSITS_SUM_ROWNUM_INT) {
				cell.setCellStyle(getStyleFirstColumnSimple(wb));

				// Simple Main
			} else if (rownum == TOTAL_DEPOSIT_ROWNUM_INT || rownum == CREDITS_FOR_LOCK_ROWNUM_INT
					|| rownum == DEPOSITS_AFTER_LOCK_ROWNUM_INT || rownum == REOPENING_POSITIONS_ROWNUM_INT
					|| rownum == DEPOSITS_AFTER_REOPEN_INT || rownum == NUMBER_OF_DEPOSITED_USERS_ROWNUM_INT
					|| rownum == VOLUME_ROWNUM_INT || rownum == TRADES_NUMBER_ROWNUM_INT || rownum == NEW_USERS_VOLUME_ROWNUM_INT) {
				cell.setCellStyle(getStyleFirstColumnSimpleMain(wb));

				// Formula Main
			} else {
				cell.setCellStyle(getStyleFirstColumnFormulaMain(wb));
			}
		}

	}

	@Override
	public void setReportSyle(String outputFilename) throws IOException {

		XSSFReadWriteInternational file = new XSSFReadWriteInternational();
		XSSFWorkbook wb = file.readFile(outputFilename);
		XSSFSheet s = wb.getSheetAt(0);
		XSSFRow retManListRow = s.getRow(RM_LIST_ROW_NUMBER_INT);

		XSSFWorkbook wbLastMonth = file.readFile(LAST_MONTH_REPORT_EN);
		XSSFSheet sLastMonth = wbLastMonth.getSheetAt(0);

		try {

			int lastRowNum = s.getLastRowNum();

			for (int rownum = 0; rownum <= lastRowNum; rownum++) {

				XSSFRow row = s.getRow(rownum);

				if (row == null) {
					continue;
				}

				int lastCellNum = row.getLastCellNum();

				for (int cellnum = 1; cellnum < lastCellNum; cellnum++) {

					XSSFCell cell = row.getCell(cellnum);
					XSSFCell cellRetManagersList = retManListRow.getCell(cellnum);

					// tupaya zatychka!! ispravit'!!
					String cellRetManagersListText = "";
					if (cellRetManagersList != null) {
						cellRetManagersListText = cellRetManagersList.getStringCellValue();
					} else {
						cellRetManagersListText = "1";
					}

					if (cell == null) {
						continue;
					}

					if (rownum == TEAMS_LIST_ROW_NUMBER_INT) {
						cell.setCellStyle(getStyle0(wb));
					} else if (rownum == RM_LIST_ROW_NUMBER_INT) {
						cell.setCellStyle(getStyle1(wb));
					} else if (rownum == NET_DEPOSITS_ROW_NUMBER_INT || rownum == AVERAGE_DEPOSIT_ROWNUM_INT) {
						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgFormulaCur(wb));
						else {
							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainFormulaCur(wb));
							} else
								cell.setCellStyle(getStyleFormulaCur(wb));

						}
					}

					else if (rownum == CREDITS_REDEPOSITS_INT || rownum == CREDITS_PRIOR_INT
							|| rownum == CREDITS_FOR_LOCK_ROWNUM_INT || rownum == DEPOSITS_AFTER_LOCK_ROWNUM_INT
							|| rownum == REOPENING_POSITIONS_ROWNUM_INT || rownum == DEPOSITS_AFTER_REOPEN_INT
							|| rownum == TOTAL_WITHDRAWALS_ROWNUM_INT || rownum == ACCOUNT_VALUE_BOP_ROWNUM_INT
							|| rownum == ACCOUNT_VALUE_EOP_ROWNUM_INT || rownum == DEALS_PNL_ROWNUM_INT) {
						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgCur(wb));
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainCur(wb));
							} else
								cell.setCellStyle(getStyleCur(wb));
						}
					}

					else if (rownum == CREDITS_DIVIDED_BY_DEPOSITS_INT || rownum == DEPOSITS_DIV_CREDITS_FOR_LOCK_ROWNUM_INT
							|| rownum == DEPOSITS_DIV_CREDITS_AFTER_REOP_ROWNUM_INT
							|| rownum == ACTIVE_USERS_DIV_TOTAL_CLIENTS_ROWNUM_INT || rownum == ACTIVE_NEW_USERS_ROWNUM_INT
							|| rownum == NEW_USERS_VOL_DIVIDED_VOL_ROWNUM_INT
							|| rownum == NEW_USERS_VOL_DIVIDED_VOL_ROWNUM_INT) {
						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgFormulaPers(wb));
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainFormulaPers(wb));
							} else
								cell.setCellStyle(getStyleFormulaPers(wb));
						}
					}

					else if (rownum == NUMBER_OF_DEPOSITS_ROWNUM_INT || rownum == TRADES_OPENED_VIA_DEALING_ROWNUM_INT
							|| rownum == ACTIVE_USERS_ROWNUM_INT || rownum == TOTAL_CLIENTS_EOP_ROWNUM_INT
							|| rownum == NEW_USERS60_ROWNUM_INT || rownum == NEW_USERS_MORE5_ROWNUM_INT
							|| rownum == NEW_USERS_REDEPOSITED_MORE450_ROWNUM_INT
							|| rownum == NEW_USERS_REDEPOSITS_NUMBER_MORE450_ROWNUM_INT) {
						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgNum(wb));
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainNum(wb));
							} else
								cell.setCellStyle(getStyleNum(wb));
						}

					}

					else if (rownum == TOTAL_DEPOSIT_ROWNUM_INT || rownum == VOLUME_ROWNUM_INT
							|| rownum == NEW_USERS_VOLUME_ROWNUM_INT || rownum == NEW_USERS_REDEPOSITS_SUM_ROWNUM_INT) {

						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgCur(wb));
							
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainCur(wb));
							} else
								cell.setCellStyle(
										getStyleColoredCur(wb, rownum, sLastMonth, cell, cellRetManagersListText));
						}

					} else if (rownum == NUMBER_OF_DEPOSITED_USERS_ROWNUM_INT || rownum == TRADES_NUMBER_ROWNUM_INT) {

						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgNum(wb));
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainNum(wb));
							} else
								cell.setCellStyle(
										getStyleColoredNum(wb, rownum, sLastMonth, cell, cellRetManagersListText));
						}

					}

					else {
						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgFormulaNum2(wb));
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainFormulaNum2(wb));
							} else
								cell.setCellStyle(getStyleFormulaNum2(wb));

						}
					}

				}

			}

			FileOutputStream out = new FileOutputStream(outputFilename);

			try {
				wb.write(out);
			} finally {
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wb.close();
		}
	}

	@Override
	protected CellStyle getStyleColoredNum(XSSFWorkbook wb, int rownum, XSSFSheet sLastMonth, XSSFCell cell,
			String cellRetManagersListText) {
		double cellValue = 0;

		switch (cell.getCellTypeEnum()) {

		case FORMULA:
			cellValue = Double.parseDouble(cell.getRawValue());
			break;

		case NUMERIC:
			cellValue = cell.getNumericCellValue();
			break;

		default:
			cellValue = cell.getNumericCellValue();
			break;
		}

		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());

		cs.setDataFormat(1);

		XSSFRow retManListRowLastMonth = sLastMonth.getRow(RM_LIST_ROW_NUMBER_INT);
		XSSFRow rowLastMonth = sLastMonth.getRow(rownum);

		int retManListRowLastMonthNumber = retManListRowLastMonth.getLastCellNum();

		for (int cellnum = 1; cellnum < retManListRowLastMonthNumber; cellnum++) {

			XSSFCell cellRetManLastMonth = retManListRowLastMonth.getCell(cellnum);
			String cellRetManagersListTextLastMonth = cellRetManLastMonth.getStringCellValue();

			if (cellRetManagersListText.equals(cellRetManagersListTextLastMonth)) {
				XSSFCell cellLastMonth = rowLastMonth.getCell(cellnum);
				double cellLastMonthValue = cellLastMonth.getNumericCellValue();

				if (cellValue > cellLastMonthValue) {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.GREEN.getIndex());
					// cell.setCellValue(String.valueOf(cellValue) + " " +
					// String.valueOf(cellLastMonthValue));
				} else if (cellValue == cellLastMonthValue) {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
					// cell.setCellValue(String.valueOf(cellValue) + " " +
					// String.valueOf(cellLastMonthValue));
				} else {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.RED.getIndex());
					// cell.setCellValue(String.valueOf(cellValue) + " " +
					// String.valueOf(cellLastMonthValue));

				}
			}
		}

		f.setBold(true);
		f.setFontHeight(11);

		cs.setFont(f);
		return cs;
	}

	@Override
	protected CellStyle getStyleColoredCur(XSSFWorkbook wb, int rownum, XSSFSheet sLastMonth, XSSFCell cell,
			String cellRetManagersListText) {

		double cellValue = 0;

		switch (cell.getCellTypeEnum()) {

		case FORMULA:
			cellValue = Double.parseDouble(cell.getRawValue());
			break;

		case NUMERIC:
			cellValue = cell.getNumericCellValue();
			break;

		default:
			cellValue = cell.getNumericCellValue();
			break;
		}

		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		cs.setDataFormat(3);

		XSSFRow retManListRowLastMonth = sLastMonth.getRow(RM_LIST_ROW_NUMBER_INT);
		XSSFRow rowLastMonth = sLastMonth.getRow(rownum);

		int retManListRowLastMonthNumber = retManListRowLastMonth.getLastCellNum();

		for (int cellnum = 1; cellnum < retManListRowLastMonthNumber; cellnum++) {

			XSSFCell cellRetManLastMonth = retManListRowLastMonth.getCell(cellnum);
			String cellRetManagersListTextLastMonth = cellRetManLastMonth.getStringCellValue();

			if (cellRetManagersListText.equals(cellRetManagersListTextLastMonth)) {
				XSSFCell cellLastMonth = rowLastMonth.getCell(cellnum);
				double cellLastMonthValue = cellLastMonth.getNumericCellValue();

				if (cellValue > cellLastMonthValue) {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.GREEN.getIndex());
					// cell.setCellValue(String.valueOf(cellValue) + " " +
					// String.valueOf(cellLastMonthValue));
				} else if (cellValue == cellLastMonthValue) {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
					// cell.setCellValue(String.valueOf(cellValue) + " " +
					// String.valueOf(cellLastMonthValue));
				} else {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.RED.getIndex());
					// cell.setCellValue(String.valueOf(cellValue) + " " +
					// String.valueOf(cellLastMonthValue));

				}
			}
		}

		f.setBold(true);
		f.setFontHeight(11);

		cs.setFont(f);
		return cs;
	}

}
