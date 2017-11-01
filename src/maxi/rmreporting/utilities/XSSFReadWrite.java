package maxi.rmreporting.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import maxi.rmreporting.entities.RetManager;
import maxi.rmreporting.formatting.Styles;

import static maxi.rmreporting.api.russian.LeftColumnTitlesList.*;
import static maxi.rmreporting.api.russian.RetentionManagersForexTableRows.*;
import static maxi.rmreporting.api.MonthAndYear.*;

public class XSSFReadWrite {

	public static final String AVERAGE = "Average";
	public static final String TOTAL = "Total";
	public static final String GRAND_TOTAL = "TOTAL";
	private static final int ROW_AMOUNT_IN_TEMPLATE = 2;
	public static final String AVERAGE_ALL = "AVERAGE ALL";
	public static final String TOTAL_ALL = "TOTAL ALL";


	/**
	 * creates an {@link HSSFWorkbook} with the specified OS filename.
	 */
	public XSSFWorkbook readFile(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		try {
			return new XSSFWorkbook(fis); // NOSONAR - should not be closed here
		} finally {
			fis.close();
		}
	}

	/**
	 * given a filename this outputs a sample sheet with just a set of
	 * rows/cells.
	 */

	public void writeRetManagersForexTemplateFile(String outputFilename,
			Map<RetManager, Object> depositsByRetManagersMap, String deskConfig) throws IOException {


		XSSFWorkbook wb = new XSSFWorkbook();

		Set<RetManager> retManagers = depositsByRetManagersMap.keySet();
		List<RetManager> retManagersArray = new ArrayList<>();

		for (RetManager retManager : retManagers) {
			String desk = retManager.getDesk();
			if (desk.equals(deskConfig)) {
				retManagersArray.add(retManager);
			}
		}

		Comparator<RetManager> comparator = new Comparator<RetManager>() {

			@Override
			public int compare(RetManager o1, RetManager o2) {
				return o1.getTeam().compareTo(o2.getTeam());
			}
		};

		retManagersArray.sort(comparator);

		int retManagersNumber = retManagersArray.size();

		try {
			XSSFSheet s = wb.createSheet();
			wb.setSheetName(0, currentMonth + " " + currentYear);
			int rownum;

			for (rownum = 0; rownum < ROW_AMOUNT_IN_TEMPLATE; rownum++) {
				XSSFRow r = s.createRow(rownum);

				if (rownum == TEAMS_LIST_ROW_NUMBER) {

					int mergedCellStart = 1;
					int mergedCellEnd = 1;

					for (int cellnum = 1, retManNum = 0; retManNum < retManagersNumber + 3; retManNum++, cellnum++) {

						// Merge of the last team
						if (retManNum == retManagersNumber) {

								s.addMergedRegion(
										new CellRangeAddress(rownum, rownum, mergedCellStart, mergedCellEnd + 2));
						

						}

						// Merge of all teams except last and build of all teams
						if (retManNum < retManagersNumber) {
							String team = retManagersArray.get(retManNum).getTeam();

							if (retManNum > 0) {
								String teamOfPreviousCell = retManagersArray.get(retManNum - 1).getTeam();
			
								if (team.equals(teamOfPreviousCell)) {
									mergedCellEnd++;
									XSSFCell c = r.createCell(cellnum);
									c.setCellValue(team);
								} else {
									
										mergedCellEnd = mergedCellEnd + 2;
										s.addMergedRegion(
												new CellRangeAddress(rownum, rownum, mergedCellStart, mergedCellEnd));
									
									mergedCellStart = mergedCellEnd + 1;
									mergedCellEnd = mergedCellStart;
									cellnum = mergedCellStart;
									XSSFCell c = r.createCell(cellnum);
									c.setCellValue(team);
								}

							} else {
								XSSFCell c = r.createCell(cellnum);
								c.setCellValue(team);		
							}

						}

						// Merge and Built Grand Total Columns
						if (retManNum == retManagersNumber + 2) {
							XSSFCell c = r.createCell(cellnum);
							c.setCellValue(GRAND_TOTAL);

							s.addMergedRegion(new CellRangeAddress(rownum, rownum, cellnum, cellnum + 1));
						}

					}

				} else if (rownum == RM_LIST_ROW_NUMBER) {

					int mergedCellStart = 1;
					int mergedCellEnd = 1;

					for (int cellnum = 1, retManNum = 0; retManNum < retManagersNumber + 3; retManNum++, cellnum++) {

						// Set Average and Total text for the last team
						if (retManNum == retManagersNumber) {

							XSSFCell average = r.createCell(mergedCellEnd + 1);
							average.setCellValue(AVERAGE);

							XSSFCell total = r.createCell(mergedCellEnd + 2);
							total.setCellValue(TOTAL);
						}

						// Set Averages and Totals texts for all teams except
						// the last team
						if (retManNum < retManagersNumber) {
							String team = retManagersArray.get(retManNum).getTeam();
							String name = retManagersArray.get(retManNum).getName();

							if (retManNum > 0) {
								String teamOfPreviousCell = retManagersArray.get(retManNum - 1).getTeam();

								if (team.equals(teamOfPreviousCell)) {
									mergedCellEnd++;
									XSSFCell c = r.createCell(cellnum);
									c.setCellValue(name);

								} else {
									XSSFCell average = r.createCell(cellnum);
									average.setCellValue(AVERAGE);

									XSSFCell total = r.createCell(cellnum + 1);
									total.setCellValue(TOTAL);

									mergedCellEnd = mergedCellEnd + 2;
									mergedCellStart = mergedCellEnd + 1;
									mergedCellEnd = mergedCellStart;
									cellnum = mergedCellStart;
									XSSFCell c = r.createCell(cellnum);
									c.setCellValue(name);
								}

							} else {
								XSSFCell c = r.createCell(cellnum);
								c.setCellValue(name);
							}

						}

						// Set Grand Total and Grand Average texts for the last
						// 2 columns
						if (retManNum == retManagersNumber + 2) {
							XSSFCell average = r.createCell(cellnum);
							average.setCellValue(AVERAGE_ALL);

							XSSFCell total = r.createCell(cellnum + 1);
							total.setCellValue(TOTAL_ALL);

						}

					}

				}

			}

			String formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, AVERAGE_DEPOSIT_ROWNUM, formula,
					TOTAL_DEPOSIT_ROWNUM, NUMBER_OF_DEPOSITS_ROWNUM);

			formula = "%s%d+%s%d";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, NET_DEPOSITS_ROW_NUMBER, formula,
					TOTAL_DEPOSIT_ROWNUM, TOTAL_WITHDRAWALS_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, CREDITS_DIVIDED_BY_DEPOSITS, formula,
					CREDITS_REDEPOSITS, TOTAL_DEPOSIT_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, NEW_USERS_VOL_DIVIDED_VOL_ROWNUM, formula,
					NEW_USERS_VOLUME_ROWNUM, VOLUME_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, DEPOSITS_DIV_CREDITS_FOR_LOCK_ROWNUM,
					formula, DEPOSITS_AFTER_LOCK_ROWNUM, CREDITS_FOR_LOCK_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, DEPOSITS_DIV_CREDITS_AFTER_REOP_ROWNUM,
					formula, DEPOSITS_AFTER_REOPEN, REOPENING_POSITIONS_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, ACTIVE_USERS_DIV_TOTAL_CLIENTS_ROWNUM,
					formula, ACTIVE_USERS_ROWNUM, TOTAL_CLIENTS_EOP_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, AVG_BAL_PER_CLIENT_ROWNUM, formula,
					ACCOUNT_VALUE_EOP_ROWNUM, TOTAL_CLIENTS_EOP_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, AVG_DEAL_SIZE_ROWNUM, formula,
					VOLUME_ROWNUM, TRADES_NUMBER_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, AVG_VOLUME_PER_TRADER_ROWNUM, formula,
					VOLUME_ROWNUM, ACTIVE_USERS_ROWNUM);

			formula = "IFERROR(ABS(%s%d)/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, AVG_PNL_FROM_CLIENT_ROWNUM, formula,
					DEALS_PNL_ROWNUM, ACTIVE_USERS_ROWNUM);

			formula = "IFERROR(ABS(%s%d)/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, AVG_PNL_FROM_TRADE_ROWNUM, formula,
					DEALS_PNL_ROWNUM, TRADES_NUMBER_ROWNUM);

			formula = "IFERROR(ABS(%s%d)/%s%d*10000,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, VOL_PNL_RATIO_ROWNUM, formula,
					DEALS_PNL_ROWNUM, VOLUME_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, AVG_DEALS_NUMBER_PER_CLIENT_ROWNUM,
					formula, TRADES_NUMBER_ROWNUM, TOTAL_CLIENTS_EOP_ROWNUM);

			formula = "IFERROR(%s%d-%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, CHANGE_IN_ACCOUNT_VALUE_ROWNUM, formula,
					ACCOUNT_VALUE_EOP_ROWNUM, ACCOUNT_VALUE_BOP_ROWNUM);

			formula = "IFERROR(%s%d/200/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, VOL_DIV200_DIV_EOM_ROWNUM, formula,
					VOLUME_ROWNUM, ACCOUNT_VALUE_EOP_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, BAL_MORE100_DIV_CLIENTS_ROWNUM, formula,
					BALANCE_MORE100_ROWNUM, TOTAL_CLIENTS_EOP_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, BAL_MORE1000_DIV_CLIENTS_ROWNUM, formula,
					BALANCE_MORE1000_ROWNUM, TOTAL_CLIENTS_EOP_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, ACTIVE_BAL_MORE1000_ROWNUM, formula,
					ACTIVE_MORE1000_ROWNUM, TOTAL_CLIENTS_EOP_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, ACTIVE_MORE100_DIV_CLIENTS_ROWNUM,
					formula, ACTIVE_MORE100_ROWNUM, TOTAL_CLIENTS_EOP_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, DT_MORE5_DIV_TOT_CLIENTS_ROWNUM, formula,
					DT_MORE5_ROWNUM, TOTAL_CLIENTS_EOP_ROWNUM);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, ACTIVE_NEW_USERS_ROWNUM, formula,
					NEW_USERS_MORE5_ROWNUM, NEW_USERS60_ROWNUM);

			FileOutputStream out = new FileOutputStream(outputFilename);
			try {
				wb.write(out);
			} finally {
				out.close();
			}

		} finally {
			wb.close();
		}
	}

	public void fillLeftColumn(String outputFilename) throws IOException {

		XSSFWorkbook wb = this.readFile(outputFilename);
		XSSFSheet s = wb.getSheetAt(0);
		int lastRowNum = s.getLastRowNum();
		Styles style = new Styles();

		for (int rownum = 2; rownum <= lastRowNum; rownum++) {

			XSSFRow row = s.getRow(rownum);

			if (row == null) {
				continue;
			}

			XSSFCell cell = row.createCell(0);
			cell.setCellValue(LEFT_COLUMN_TITLES_LIST[rownum - 2]);
			style.setFirstColumnStyle(cell, rownum, wb);

		}

		try {
			FileOutputStream out = new FileOutputStream(outputFilename);
			try {
				wb.write(out);
				wb.close();
			} finally {
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void writeFormulaRowWithTwoParameters(XSSFWorkbook wb, XSSFSheet s,
			Map<RetManager, Object> depositsByRetManagersMap, int rowNumber, String formula, int rowNumberOneInFormula,
			int rowNumberTwoInFormula) {

		Set<RetManager> retManagers = (Set<RetManager>) depositsByRetManagersMap.keySet();
		XSSFRow outputRow = s.createRow(rowNumber);
		XSSFRow retManListRow = s.getRow(RM_LIST_ROW_NUMBER);
		int retManListRowCellsAmount = retManListRow.getLastCellNum();

		ArrayList<String> averageRanges = new ArrayList<>();

		for (int cellnum = 0; cellnum < retManListRowCellsAmount; cellnum++) {
			XSSFCell outputCell = outputRow.createCell(cellnum);
			XSSFCell retManNameCell = retManListRow.getCell(cellnum);
			String retManNameCellText = null;

			if (retManNameCell != null) {
				retManNameCellText = retManNameCell.getStringCellValue();

				int cellnumFormulaEnd = cellnum - 1;

				if (retManNameCellText.equalsIgnoreCase(AVERAGE)) {

					int cellnumFormulaStart = findCellnumFormulaStart(cellnum, retManListRow, cellnumFormulaEnd);
					String cellnumFormulaStartPart2 = findCellnumFormulaStartPart2(cellnum, retManListRow,
							cellnumFormulaEnd);
					String cellnumFormulaEndStr = findCellnumFormulaEndAverage(cellnum, retManListRow,
							cellnumFormulaEnd);
					String averageRange = (char) (cellnumFormulaStart + 65) + cellnumFormulaStartPart2 + (rowNumber + 1)
							+ ":" + cellnumFormulaEndStr + (rowNumber + 1);
					outputCell.setCellFormula("AVERAGE(" + averageRange + ")");

					averageRanges.add(averageRange);

				} else if (retManNameCellText.equalsIgnoreCase(TOTAL)) {

					simpleFormula(cellnum, formula, outputCell, rowNumberOneInFormula, rowNumberTwoInFormula);

				} else if (retManNameCellText.equalsIgnoreCase(TOTAL_ALL)) {

					simpleFormula(cellnum, formula, outputCell, rowNumberOneInFormula, rowNumberTwoInFormula);

				}

				else if (retManNameCellText.equalsIgnoreCase(AVERAGE_ALL)) {

					StringBuilder averagesStrB = new StringBuilder("");

					for (String averageRange : averageRanges) {
						averagesStrB.append(averageRange + ",");
					}
					averagesStrB.deleteCharAt((averagesStrB.length() - 1));
					outputCell.setCellFormula("AVERAGE(" + averagesStrB.toString() + ")");

				}
			}

			for (RetManager retManager : retManagers) {

				if (retManager.getName().equals(retManNameCellText)) {
					simpleFormula(cellnum, formula, outputCell, rowNumberOneInFormula, rowNumberTwoInFormula);
				}

			}

		}

	}

	@SuppressWarnings("unused")
	private String cellnumToExsel(int cellnum) {
		String res = "";
		String part2 = "";

		if (cellnum >= 26 && cellnum < 52) {
			part2 = String.valueOf((char) (cellnum - 26 + 65));
			res = String.valueOf((char) 65) + part2;
		} else if (cellnum >= 52) {
			part2 = String.valueOf((char) (cellnum - 52 + 65));
			res = String.valueOf((char) 66) + part2;
		} else {
			res = String.valueOf((char) (cellnum + 65));
		}
		return res;

	}

	private void simpleFormula(int cellnum, String formula, XSSFCell outputCell, int rowNumberOneInFormula,
			int rowNumberTwoInFormula) {
		if (cellnum < 26) {
			outputCell.setCellFormula(String.format(formula, String.valueOf((char) (cellnum + 65)),
					rowNumberOneInFormula + 1, String.valueOf((char) (cellnum + 65)), rowNumberTwoInFormula + 1));
		} else if (cellnum >= 26 && cellnum < 52) {
			outputCell.setCellFormula(
					String.format(formula, "A" + String.valueOf((char) (cellnum - 26 + 65)), rowNumberOneInFormula + 1,
							"A" + String.valueOf((char) (cellnum - 26 + 65)), rowNumberTwoInFormula + 1));
		} else if (cellnum >= 52) {
			outputCell.setCellFormula(
					String.format(formula, "B" + String.valueOf((char) (cellnum - 52 + 65)), rowNumberOneInFormula + 1,
							"B" + String.valueOf((char) (cellnum - 52 + 65)), rowNumberTwoInFormula + 1));
		}
	}

	public void writeDataByRetManagersToExsel(String outputFilename, Map<RetManager, Object> depositsByRetManagersMap,
			int rowNumber) throws IOException {

		XSSFWorkbook wb = readFile(outputFilename);
		Set<RetManager> retManagers = (Set<RetManager>) depositsByRetManagersMap.keySet();

		try {
			XSSFSheet s = wb.getSheetAt(0);
			XSSFRow outputRow = s.createRow(rowNumber);
			XSSFRow retManListRow = s.getRow(RM_LIST_ROW_NUMBER);
			int retManListRowCellsAmount = retManListRow.getLastCellNum();
			
			ArrayList<String> averageRanges = new ArrayList<>();
			ArrayList<String> totalRanges = new ArrayList<>();

			for (int cellnum = 0; cellnum < retManListRowCellsAmount; cellnum++) {
				XSSFCell outputCell = outputRow.createCell(cellnum);
				XSSFCell retManNameCell = retManListRow.getCell(cellnum);
				String retManNameCellText = "empty";

				if (retManNameCell != null) {
					retManNameCellText = retManNameCell.getStringCellValue();

					int cellnumFormulaEnd = cellnum - 1;

					if (retManNameCellText.equalsIgnoreCase(AVERAGE)) {
						int cellnumFormulaStart = findCellnumFormulaStart(cellnum, retManListRow, cellnumFormulaEnd);
						String cellnumFormulaStartPart2 = findCellnumFormulaStartPart2(cellnum, retManListRow,
								cellnumFormulaEnd);
						String cellnumFormulaEndStr = findCellnumFormulaEndAverage(cellnum, retManListRow,
								cellnumFormulaEnd);

						String averageRange = (char) (cellnumFormulaStart + 65) + cellnumFormulaStartPart2 + (rowNumber + 1)
								+ ":" + cellnumFormulaEndStr + (rowNumber + 1);
						
						outputCell.setCellFormula(
								"AVERAGE(" + averageRange + ")");
						
						averageRanges.add(averageRange);

					} else if (retManNameCellText.equalsIgnoreCase(TOTAL)) {
						int cellnumFormulaStart = findCellnumFormulaStart(cellnum, retManListRow, cellnumFormulaEnd);
						String cellnumFormulaStartPart2 = findCellnumFormulaStartPart2(cellnum, retManListRow,
								cellnumFormulaEnd);
						String cellnumFormulaEndStr = findCellnumFormulaEnd(cellnum, retManListRow, cellnumFormulaEnd);
						
						String totalRange = (char) (cellnumFormulaStart + 65) + cellnumFormulaStartPart2
								+ (rowNumber + 1) + ":" + cellnumFormulaEndStr + (rowNumber + 1);
						outputCell.setCellFormula("SUM(" + totalRange + ")");
						
						totalRanges.add(totalRange);

					} else if (retManNameCellText.equalsIgnoreCase(TOTAL_ALL)) {

						StringBuilder totalsStrB = new StringBuilder("");

						for (String totalRange : totalRanges) {
							totalsStrB.append(totalRange + ",");
						}
						totalsStrB.deleteCharAt((totalsStrB.length() - 1));
						outputCell.setCellFormula("SUM(" + totalsStrB.toString() + ")");

					}

					else if (retManNameCellText.equalsIgnoreCase(AVERAGE_ALL)) {

						StringBuilder averagesStrB = new StringBuilder("");

						for (String averageRange : averageRanges) {
							averagesStrB.append(averageRange + ",");
						}
						averagesStrB.deleteCharAt((averagesStrB.length() - 1));
						outputCell.setCellFormula("AVERAGE(" + averagesStrB.toString() + ")");

					}
				}

				for (RetManager retManager : retManagers) {
					if (retManager.getName().equals(retManNameCellText)) {
						outputCell.setCellValue((Double) depositsByRetManagersMap.get(retManager));
					}
				}

				if (outputCell.getRawValue() == null && outputCell.getColumnIndex() != 0) {
					outputCell.setCellValue(0);
				}

			}

			FileOutputStream out = new FileOutputStream(outputFilename);
			try {
				wb.write(out);
			} finally {
				out.close();
			}

		} finally {
			wb.close();
		}
	}

	protected String findCellnumFormulaEndAverage(int cellnum, XSSFRow retManListRow, int cellnumFormulaEnd) {

		String res = "";
		String part2 = "";

		if (cellnumFormulaEnd >= 26 && cellnumFormulaEnd < 52) {
			part2 = String.valueOf((char) (cellnumFormulaEnd - 26 + 65));
			res = String.valueOf((char) 65) + part2;
		} else if (cellnumFormulaEnd >= 52) {
			part2 = String.valueOf((char) (cellnumFormulaEnd - 52 + 65));
			res = String.valueOf((char) 66) + part2;
		} else {
			res = String.valueOf((char) (cellnumFormulaEnd + 65));
		}
		return res;
	}

	protected String findCellnumFormulaEnd(int cellnum, XSSFRow retManListRow, int cellnumFormulaEnd) {

		String res = "";
		String part2 = "";

		if (cellnumFormulaEnd >= 26 && cellnumFormulaEnd < 52) {
			part2 = String.valueOf((char) (cellnumFormulaEnd - 26 + 64));
			res = String.valueOf((char) 65) + part2;
		} else if (cellnumFormulaEnd >= 52) {
			part2 = String.valueOf((char) (cellnumFormulaEnd - 52 + 64));
			res = String.valueOf((char) 66) + part2;
		} else {
			res = String.valueOf((char) (cellnumFormulaEnd + 64));
		}
		return res;
	}

	protected String findCellnumFormulaStartPart2(int cellnum, XSSFRow retManListRow, int cellnumFormulaEnd) {
		int cellnumFormulaStart = 1;
		for (int i = cellnumFormulaEnd; i > 0; i--) {
			String text = retManListRow.getCell(i).getStringCellValue();
			if (text.equalsIgnoreCase(TOTAL)) {
				cellnumFormulaStart = i + 1;
				break;
			}
		}

		if (cellnumFormulaStart >= 26 && cellnumFormulaStart < 52) {
			return String.valueOf((char) (cellnumFormulaStart - 26 + 65));
		} else if (cellnumFormulaStart >= 52) {
			return String.valueOf((char) (cellnumFormulaStart - 52 + 65));
			// return "K";
		} else {
			return "";
		}
	}

	protected int findCellnumFormulaStart(int cellnum, XSSFRow retManListRow, int cellnumFormulaEnd) {
		int cellnumFormulaStart = 1;
		for (int i = cellnumFormulaEnd; i > 0; i--) {
			String text = retManListRow.getCell(i).getStringCellValue();
			if (text.equalsIgnoreCase(TOTAL)) {
				cellnumFormulaStart = i + 1;
				break;
			}
		}

		if (cellnumFormulaStart >= 26 && cellnumFormulaStart < 52) {
			return 0;
		} else if (cellnumFormulaStart >= 52) {
			return 1;
		} else {
			return cellnumFormulaStart;
		}
	}

	public Map<String, List<Object>> parseFile(String filename) {

		Map<String, List<Object>> excelFileMap = new HashMap<>();
		Map<Integer, List<Object>> excelFileMapWithColumnsNumbers = new HashMap<>();

		try {

			XSSFWorkbook exselFile = readFile(filename);
			parseSheets(exselFile, excelFileMapWithColumnsNumbers);

			for (Integer columnNumber : excelFileMapWithColumnsNumbers.keySet()) {
				List<Object> column = new ArrayList<>();
				List<Object> columnWithoutTitle = new ArrayList<>();
				column = excelFileMapWithColumnsNumbers.get(columnNumber);
				columnWithoutTitle = column.subList(1, column.size());
				excelFileMap.put((String) column.get(0), columnWithoutTitle);
			}

			// printExcelFileMap(excelFileMap);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return excelFileMap;
	}

	private void parseSheets(XSSFWorkbook exselFile, Map<Integer, List<Object>> excelFileMapWithColumnsNumbers) {
		int numberOfSheets = exselFile.getNumberOfSheets();

		for (int s = 0; s < numberOfSheets; s++) {

			XSSFSheet sheet = exselFile.getSheetAt(s);
			parseSheet(sheet, s, excelFileMapWithColumnsNumbers, exselFile);

		}

	}

	private void parseSheet(XSSFSheet sheet, int sheetNumber, Map<Integer, List<Object>> excelFileMapWithColumnsNumbers,
			XSSFWorkbook exselFile) {
		int rows = sheet.getPhysicalNumberOfRows();
		// System.out.println("Sheet " + sheetNumber + " \"" +
		// exselFile.getSheetName(sheetNumber) + "\" has " + rows + " row(s).");

		for (int r = 0; r < rows; r++) {

			XSSFRow row = sheet.getRow(r);

			if (row == null) {
				// sheet.createRow(0);
				continue;
			}

			parseRow(row, r, excelFileMapWithColumnsNumbers);

		}

	}

	private void parseRow(XSSFRow row, int rowNumber, Map<Integer, List<Object>> excelFileMapWithColumnsNumbers) {

		// System.out.println("\nROW " + rowNumber + " has " +
		// row.getPhysicalNumberOfCells() + " cell(s).");
		int lastCellNum = row.getLastCellNum();

		for (int c = 0; c < lastCellNum; c++) {

			XSSFCell cell = row.getCell(c);
			if (cell == null) {
				cell = row.createCell(c);
				cell.setCellValue("0");

			}

			parseCell(cell, excelFileMapWithColumnsNumbers, rowNumber);

		}

	}

	private void parseCell(XSSFCell cell, Map<Integer, List<Object>> excelFileMapWithColumnsNumbers, int rowNumber) {
		String value = null;
		int columnIndex = -1;

		switch (cell.getCellTypeEnum()) {

		case FORMULA:
			value = String.valueOf(cell.getRawValue());
			break;

		case NUMERIC:
			// value = String.valueOf(cell.getNumericCellValue());
			value = String.valueOf(cell.getRawValue());
			break;

		case STRING:
			value = String.valueOf(cell.getStringCellValue());
			break;

		case BLANK:

			value = String.valueOf(cell.getCellTypeEnum());
			// value = "<BLANK>"; ////o4en portit!!!
			break;

		case BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;

		case ERROR:
			value = String.valueOf(cell.getErrorCellValue());
			break;

		default:
			value = String.valueOf(cell.getCellTypeEnum());
		}
		columnIndex = cell.getColumnIndex();
		fillMap(value, rowNumber, columnIndex, excelFileMapWithColumnsNumbers);
		// System.out.println("CELL col=" + columnIndex + " VALUE=" + value);
	}

	private void fillMap(String cellValue, int rowNumber, int columnIndex,
			Map<Integer, List<Object>> excelFileMapWithColumnsNumbers) {

		if (excelFileMapWithColumnsNumbers.containsKey(columnIndex)) {
			excelFileMapWithColumnsNumbers.get(columnIndex).add(cellValue);

		} else {
			List<Object> column = new ArrayList<>();
			column.add(cellValue);
			excelFileMapWithColumnsNumbers.putIfAbsent(columnIndex, column);
		}
	}

	@SuppressWarnings("unused")
	private void printExcelFileMap(Map<String, List<Object>> excelFileMap) {
		// System.out.println("\n");

		for (String key : excelFileMap.keySet()) {
			System.out.println(key + ":" + excelFileMap.get(key));
		}
	}

}
