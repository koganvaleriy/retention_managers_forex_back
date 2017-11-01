package maxi.rmreporting.formatting;

import static maxi.rmreporting.api.russian.FileNamesAndColumnTitles.*;
import static maxi.rmreporting.api.russian.RetentionManagersForexTableRows.*;
import static maxi.rmreporting.utilities.XSSFReadWrite.*;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import maxi.rmreporting.utilities.XSSFReadWrite;

public class Styles {

	private XSSFCellStyle cs;
	private XSSFFont f;

	public Styles() {

	}

	public void setFirstColumnStyle(XSSFCell cell, int rownum, XSSFWorkbook wb) throws IOException {

		if (cell != null) {

			// Formula Not Main
			if (rownum == NET_DEPOSITS_ROW_NUMBER || rownum == CREDITS_DIVIDED_BY_DEPOSITS
					|| rownum == AVERAGE_DEPOSIT_ROWNUM || rownum == ACTIVE_USERS_DIV_TOTAL_CLIENTS_ROWNUM
					|| rownum == AVG_BAL_PER_CLIENT_ROWNUM || rownum == AVG_DEAL_SIZE_ROWNUM
					|| rownum == AVG_VOLUME_PER_TRADER_ROWNUM || rownum == AVG_PNL_FROM_CLIENT_ROWNUM
					|| rownum == AVG_PNL_FROM_TRADE_ROWNUM || rownum == VOL_PNL_RATIO_ROWNUM
					|| rownum == AVG_DEALS_NUMBER_PER_CLIENT_ROWNUM || rownum == CHANGE_IN_ACCOUNT_VALUE_ROWNUM
					|| rownum == BAL_MORE100_DIV_CLIENTS_ROWNUM || rownum == BAL_MORE1000_DIV_CLIENTS_ROWNUM
					|| rownum == ACTIVE_BAL_MORE1000_ROWNUM || rownum == DT_MORE5_DIV_TOT_CLIENTS_ROWNUM
					|| rownum == ACTIVE_NEW_USERS_ROWNUM || rownum == NEW_USERS_VOL_DIVIDED_VOL_ROWNUM) {
				cell.setCellStyle(getStyleFirstColumnFormula(wb));

				// Simple Not Main
			} else if (rownum == CREDITS_REDEPOSITS || rownum == CREDITS_PRIOR || rownum == TOTAL_WITHDRAWALS_ROWNUM
					|| rownum == NUMBER_OF_DEPOSITS_ROWNUM || rownum == NUMBER_OF_DEPOSITS_MORE100_ROWNUM
					|| rownum == NUMBER_OF_DEPOSITS_MORE450_ROWNUM || rownum == NUMBER_OF_DEPOSITS_MORE1000_ROWNUM
					|| rownum == NUMBER_OF_DEPOSITED_USERS_MORE100_ROWNUM
					|| rownum == NUMBER_OF_DEPOSITED_USERS_MORE450_ROWNUM
					|| rownum == NUMBER_OF_DEPOSITED_USERS_MORE1000_ROWNUM || rownum == DEALS_PNL_ROWNUM
					|| rownum == TRADES_OPENED_VIA_DEALING_ROWNUM || rownum == ACTIVE_USERS_ROWNUM
					|| rownum == TOTAL_CLIENTS_EOP_ROWNUM || rownum == ACCOUNT_VALUE_BOP_ROWNUM
					|| rownum == ACCOUNT_VALUE_EOP_ROWNUM || rownum == BALANCE_MORE100_ROWNUM
					|| rownum == BALANCE_MORE1000_ROWNUM || rownum == ACTIVE_MORE1000_ROWNUM
					|| rownum == NEW_USERS60_ROWNUM || rownum == DT_MORE5_ROWNUM || rownum == NEW_USERS_MORE5_ROWNUM
					|| rownum == NEW_USERS_REDEPOSITED_MORE450_ROWNUM
					|| rownum == NEW_USERS_REDEPOSITS_NUMBER_MORE450_ROWNUM
					|| rownum == NEW_USERS_REDEPOSITS_SUM_ROWNUM) {
				cell.setCellStyle(getStyleFirstColumnSimple(wb));

				// Simple Main
			} else if (rownum == TOTAL_DEPOSIT_ROWNUM || rownum == CREDITS_FOR_LOCK_ROWNUM
					|| rownum == DEPOSITS_AFTER_LOCK_ROWNUM || rownum == REOPENING_POSITIONS_ROWNUM
					|| rownum == DEPOSITS_AFTER_REOPEN || rownum == NUMBER_OF_DEPOSITED_USERS_ROWNUM
					|| rownum == VOLUME_ROWNUM || rownum == TRADES_NUMBER_ROWNUM || rownum == NEW_USERS_VOLUME_ROWNUM) {
				cell.setCellStyle(getStyleFirstColumnSimpleMain(wb));

				// Formula Main
			} else {
				cell.setCellStyle(getStyleFirstColumnFormulaMain(wb));
			}
		}

	}

	public void setReportSyle(String outputFilename) throws IOException {

		XSSFReadWrite file = new XSSFReadWrite();
		XSSFWorkbook wb = file.readFile(outputFilename);
		XSSFSheet s = wb.getSheetAt(0);
		XSSFRow retManListRow = s.getRow(RM_LIST_ROW_NUMBER);

		XSSFWorkbook wbLastMonth = file.readFile(LAST_MONTH_REPORT);
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

					if (rownum == TEAMS_LIST_ROW_NUMBER) {
						cell.setCellStyle(getStyle0(wb));
					} else if (rownum == RM_LIST_ROW_NUMBER) {
						cell.setCellStyle(getStyle1(wb));
					} else if (rownum == NET_DEPOSITS_ROW_NUMBER || rownum == AVERAGE_DEPOSIT_ROWNUM
							|| rownum == AVG_BAL_PER_CLIENT_ROWNUM || rownum == AVG_DEAL_SIZE_ROWNUM
							|| rownum == AVG_VOLUME_PER_TRADER_ROWNUM || rownum == AVG_PNL_FROM_CLIENT_ROWNUM
							|| rownum == AVG_PNL_FROM_TRADE_ROWNUM || rownum == CHANGE_IN_ACCOUNT_VALUE_ROWNUM) {
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

					else if (rownum == CREDITS_REDEPOSITS || rownum == CREDITS_PRIOR
							|| rownum == CREDITS_FOR_LOCK_ROWNUM || rownum == DEPOSITS_AFTER_LOCK_ROWNUM
							|| rownum == REOPENING_POSITIONS_ROWNUM || rownum == DEPOSITS_AFTER_REOPEN
							|| rownum == TOTAL_WITHDRAWALS_ROWNUM || rownum == ACCOUNT_VALUE_BOP_ROWNUM
							|| rownum == ACCOUNT_VALUE_EOP_ROWNUM || rownum == DEALS_PNL_ROWNUM) {
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

					else if (rownum == CREDITS_DIVIDED_BY_DEPOSITS || rownum == DEPOSITS_DIV_CREDITS_FOR_LOCK_ROWNUM
							|| rownum == DEPOSITS_DIV_CREDITS_AFTER_REOP_ROWNUM
							|| rownum == ACTIVE_USERS_DIV_TOTAL_CLIENTS_ROWNUM
							|| rownum == BAL_MORE100_DIV_CLIENTS_ROWNUM || rownum == BAL_MORE1000_DIV_CLIENTS_ROWNUM
							|| rownum == ACTIVE_BAL_MORE1000_ROWNUM || rownum == ACTIVE_MORE100_DIV_CLIENTS_ROWNUM
							|| rownum == DT_MORE5_DIV_TOT_CLIENTS_ROWNUM || rownum == ACTIVE_NEW_USERS_ROWNUM
							|| rownum == NEW_USERS_VOL_DIVIDED_VOL_ROWNUM
							|| rownum == NEW_USERS_VOL_DIVIDED_VOL_ROWNUM) {
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

					else if (rownum == NUMBER_OF_DEPOSITS_ROWNUM || rownum == NUMBER_OF_DEPOSITS_MORE100_ROWNUM
							|| rownum == NUMBER_OF_DEPOSITS_MORE450_ROWNUM
							|| rownum == NUMBER_OF_DEPOSITS_MORE1000_ROWNUM
							|| rownum == NUMBER_OF_DEPOSITED_USERS_MORE100_ROWNUM
							|| rownum == NUMBER_OF_DEPOSITED_USERS_MORE450_ROWNUM
							|| rownum == NUMBER_OF_DEPOSITED_USERS_MORE1000_ROWNUM
							|| rownum == TRADES_OPENED_VIA_DEALING_ROWNUM || rownum == ACTIVE_USERS_ROWNUM
							|| rownum == TOTAL_CLIENTS_EOP_ROWNUM || rownum == BALANCE_MORE100_ROWNUM
							|| rownum == BALANCE_MORE1000_ROWNUM || rownum == ACTIVE_MORE1000_ROWNUM
							|| rownum == ACTIVE_MORE100_ROWNUM || rownum == DT_MORE5_ROWNUM
							|| rownum == NEW_USERS60_ROWNUM || rownum == NEW_USERS_MORE5_ROWNUM
							|| rownum == NEW_USERS_REDEPOSITED_MORE450_ROWNUM
							|| rownum == NEW_USERS_REDEPOSITS_NUMBER_MORE450_ROWNUM) {
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

					else if (rownum == VOL_PNL_RATIO_ROWNUM || rownum == VOL_DIV200_DIV_EOM_ROWNUM) {
						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgFormulaNum(wb));
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainFormulaNum(wb));
							} else
								cell.setCellStyle(getStyleFormulaNum(wb));
						}
					} else if (rownum == TOTAL_DEPOSIT_ROWNUM || rownum == VOLUME_ROWNUM
							|| rownum == NEW_USERS_VOLUME_ROWNUM || rownum == NEW_USERS_REDEPOSITS_SUM_ROWNUM) {
						
						
						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgCur(wb));
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainCur(wb));
							} else
								cell.setCellStyle(getStyleColoredCur(wb, rownum, sLastMonth, cell, cellRetManagersListText));
						}

					} else if (rownum == NUMBER_OF_DEPOSITED_USERS_ROWNUM  || rownum == TRADES_NUMBER_ROWNUM ) {
												
						if (cellRetManagersListText.equalsIgnoreCase(AVERAGE)
								|| cellRetManagersListText.equalsIgnoreCase(TOTAL))
							cell.setCellStyle(getStyleTotAvgNum(wb));
						else {

							if (cellRetManagersListText.equalsIgnoreCase(AVERAGE_ALL)
									|| cellRetManagersListText.equalsIgnoreCase(TOTAL_ALL)) {
								cell.setCellStyle(getStyleMainNum(wb));
							} else
								cell.setCellStyle(getStyleColoredNum(wb, rownum, sLastMonth, cell, cellRetManagersListText));
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

		XSSFRow retManListRowLastMonth = sLastMonth.getRow(RM_LIST_ROW_NUMBER);
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
					//cell.setCellValue(String.valueOf(cellValue) + " " + String.valueOf(cellLastMonthValue));
				} else if (cellValue == cellLastMonthValue) {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
					//cell.setCellValue(String.valueOf(cellValue) + " " + String.valueOf(cellLastMonthValue));
				} else {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.RED.getIndex());
					//cell.setCellValue(String.valueOf(cellValue) + " " + String.valueOf(cellLastMonthValue));
					
				}
			}
		}

		f.setBold(true);
		f.setFontHeight(11);

		cs.setFont(f);
		return cs;
	}

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

		XSSFRow retManListRowLastMonth = sLastMonth.getRow(RM_LIST_ROW_NUMBER);
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
					//cell.setCellValue(String.valueOf(cellValue) + " " + String.valueOf(cellLastMonthValue));
				} else if (cellValue == cellLastMonthValue) {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
					//cell.setCellValue(String.valueOf(cellValue) + " " + String.valueOf(cellLastMonthValue));
				} else {
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cs.setFillForegroundColor(IndexedColors.RED.getIndex());
					//cell.setCellValue(String.valueOf(cellValue) + " " + String.valueOf(cellLastMonthValue));
					
				}
			}
		}

		f.setBold(true);
		f.setFontHeight(11);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleMainFormulaCur(XSSFWorkbook wb) {
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
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(153, 0, 255)));
		cs.setDataFormat(3);

		f.setFontHeight(11);
		f.setItalic(true);
		f.setBold(true);
		f.setColor(IndexedColors.WHITE.getIndex());

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleMainCur(XSSFWorkbook wb) {
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
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(153, 0, 255)));
		cs.setDataFormat(3);

		f.setFontHeight(11);
		f.setBold(true);
		f.setColor(IndexedColors.WHITE.getIndex());

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleMainFormulaPers(XSSFWorkbook wb) {
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
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(153, 0, 255)));
		cs.setDataFormat(9);

		f.setFontHeight(11);
		f.setItalic(true);
		f.setBold(true);
		f.setColor(IndexedColors.WHITE.getIndex());

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleMainNum(XSSFWorkbook wb) {
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
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(153, 0, 255)));
		cs.setDataFormat(1);

		f.setFontHeight(11);
		f.setBold(true);
		f.setColor(IndexedColors.WHITE.getIndex());

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleMainFormulaNum(XSSFWorkbook wb) {
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
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(153, 0, 255)));
		cs.setDataFormat(2);

		f.setFontHeight(11);
		f.setItalic(true);
		f.setBold(true);
		f.setColor(IndexedColors.WHITE.getIndex());

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleMainFormulaNum2(XSSFWorkbook wb) {
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
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(153, 0, 255)));
		cs.setDataFormat(1);

		f.setFontHeight(11);
		f.setItalic(true);
		f.setBold(true);
		f.setColor(IndexedColors.WHITE.getIndex());

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleFormulaNum2(XSSFWorkbook wb) {
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

		f.setFontHeight(11);
		f.setItalic(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleTotAvgFormulaNum2(XSSFWorkbook wb) {
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
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(164, 194, 244)));

		f.setFontHeight(11);
		f.setItalic(true);
		f.setBold(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleNum(XSSFWorkbook wb) {
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

		f.setFontHeight(11);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleTotAvgNum(XSSFWorkbook wb) {
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
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(164, 194, 244)));
		cs.setDataFormat(1);

		f.setFontHeight(11);
		f.setBold(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleFormulaNum(XSSFWorkbook wb) {
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
		cs.setDataFormat(2);

		f.setFontHeight(11);
		f.setItalic(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleTotAvgFormulaNum(XSSFWorkbook wb) {
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
		cs.setDataFormat(2);
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(164, 194, 244)));

		f.setFontHeight(11);
		f.setItalic(true);
		f.setBold(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleTotAvgCur(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(164, 194, 244)));
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		cs.setDataFormat(3);

		f.setBold(true);
		f.setFontHeight(11);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleCur(XSSFWorkbook wb) {
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

		f.setFontHeight(11);

		cs.setFont(f);
		return cs;
	}

	protected XSSFCellStyle getStyle0(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(28, 69, 135)));
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());

		f.setBold(true);
		f.setFontHeight(14);
		f.setColor((IndexedColors.WHITE.getIndex()));

		cs.setFont(f);
		return cs;
	}

	protected XSSFCellStyle getStyle1(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(28, 69, 135)));
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());

		f.setBold(true);
		f.setFontHeight(11);
		f.setColor((IndexedColors.WHITE.getIndex()));

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleFormulaCur(XSSFWorkbook wb) {
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

		f.setFontHeight(11);
		f.setItalic(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleTotAvgFormulaCur(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(164, 194, 244)));
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		cs.setDataFormat(3);

		f.setBold(true);
		f.setFontHeight(11);
		f.setItalic(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleTotAvgFormulaPers(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.CENTER);
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(164, 194, 244)));
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		cs.setDataFormat(9);

		f.setBold(true);
		f.setFontHeight(11);
		f.setItalic(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleFormulaPers(XSSFWorkbook wb) {
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
		cs.setDataFormat(9);

		f.setFontHeight(11);
		f.setItalic(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleFirstColumnSimple(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.LEFT);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(164, 194, 244)));

		f.setFontHeight(11);
		f.setBold(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleFirstColumnFormula(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.LEFT);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(164, 194, 244)));

		f.setFontHeight(11);
		f.setItalic(true);
		f.setBold(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleFirstColumnFormulaMain(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.LEFT);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(17, 85, 204)));

		f.setFontHeight(11);
		f.setColor(IndexedColors.WHITE.getIndex());
		f.setItalic(true);
		f.setBold(true);

		cs.setFont(f);
		return cs;
	}

	protected CellStyle getStyleFirstColumnSimpleMain(XSSFWorkbook wb) {
		cs = wb.createCellStyle();
		f = wb.createFont();

		cs.setAlignment(HorizontalAlignment.LEFT);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setTopBorderColor(IndexedColors.BLUE.getIndex());
		cs.setLeftBorderColor(IndexedColors.BLUE.getIndex());
		cs.setRightBorderColor(IndexedColors.BLUE.getIndex());
		cs.setBottomBorderColor(IndexedColors.BLUE.getIndex());
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(new XSSFColor(new Color(17, 85, 204)));

		f.setFontHeight(11);
		f.setColor(IndexedColors.WHITE.getIndex());
		f.setBold(true);

		cs.setFont(f);
		return cs;
	}

}
