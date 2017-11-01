package maxi.rmreporting.utilities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import maxi.rmreporting.entities.RetManager;
import maxi.rmreporting.formatting.StylesEn;

import static maxi.rmreporting.api.international.LeftColumnTitlesListInt.*;
import static maxi.rmreporting.api.international.RetentionManagersForexTableRowsInt.*;
import static maxi.rmreporting.api.MonthAndYear.*;

public class XSSFReadWriteInternational extends XSSFReadWrite{

	public static final String AVERAGE = "Average";
	public static final String TOTAL = "Total";
	public static final String GRAND_TOTAL = "TOTAL";
	private static final int ROW_AMOUNT_IN_TEMPLATE = 2;
	public static final String AVERAGE_ALL = "AVERAGE ALL";
	public static final String TOTAL_ALL = "TOTAL ALL";


	@Override
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

				if (rownum == TEAMS_LIST_ROW_NUMBER_INT) {

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
									if (mergedCellStart < mergedCellEnd) {
										mergedCellEnd = mergedCellEnd + 2;
										s.addMergedRegion(
												new CellRangeAddress(rownum, rownum, mergedCellStart, mergedCellEnd));
									}
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

				} else if (rownum == RM_LIST_ROW_NUMBER_INT) {

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
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, AVERAGE_DEPOSIT_ROWNUM_INT, formula,
					TOTAL_DEPOSIT_ROWNUM_INT, NUMBER_OF_DEPOSITS_ROWNUM_INT);

			formula = "%s%d+%s%d";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, NET_DEPOSITS_ROW_NUMBER_INT, formula,
					TOTAL_DEPOSIT_ROWNUM_INT, TOTAL_WITHDRAWALS_ROWNUM_INT);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, CREDITS_DIVIDED_BY_DEPOSITS_INT, formula,
					CREDITS_REDEPOSITS_INT, TOTAL_DEPOSIT_ROWNUM_INT);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, NEW_USERS_VOL_DIVIDED_VOL_ROWNUM_INT, formula,
					NEW_USERS_VOLUME_ROWNUM_INT, VOLUME_ROWNUM_INT);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, DEPOSITS_DIV_CREDITS_FOR_LOCK_ROWNUM_INT,
					formula, DEPOSITS_AFTER_LOCK_ROWNUM_INT, CREDITS_FOR_LOCK_ROWNUM_INT);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, DEPOSITS_DIV_CREDITS_AFTER_REOP_ROWNUM_INT,
					formula, DEPOSITS_AFTER_REOPEN_INT, REOPENING_POSITIONS_ROWNUM_INT);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, ACTIVE_USERS_DIV_TOTAL_CLIENTS_ROWNUM_INT,
					formula, ACTIVE_USERS_ROWNUM_INT, TOTAL_CLIENTS_EOP_ROWNUM_INT);

			formula = "IFERROR(%s%d/%s%d,0)";
			writeFormulaRowWithTwoParameters(wb, s, depositsByRetManagersMap, ACTIVE_NEW_USERS_ROWNUM_INT, formula,
					NEW_USERS_MORE5_ROWNUM_INT, NEW_USERS60_ROWNUM_INT);

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

	@Override
	public void fillLeftColumn(String outputFilename) throws IOException {

		XSSFWorkbook wb = this.readFile(outputFilename);
		XSSFSheet s = wb.getSheetAt(0);
		int lastRowNum = s.getLastRowNum();
		StylesEn style = new StylesEn();

		for (int rownum = 2; rownum <= lastRowNum; rownum++) {

			XSSFRow row = s.getRow(rownum);

			if (row == null) {
				continue;
			}

			XSSFCell cell = row.createCell(0);
			cell.setCellValue(LEFT_COLUMN_TITLES_LIST_INT[rownum - 2]);
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

	@Override
	public void writeDataByRetManagersToExsel(String outputFilename, Map<RetManager, Object> depositsByRetManagersMap,
			int rowNumber) throws IOException {

		XSSFWorkbook wb = readFile(outputFilename);
		Set<RetManager> retManagers = (Set<RetManager>) depositsByRetManagersMap.keySet();

		try {
			XSSFSheet s = wb.getSheetAt(0);
			XSSFRow outputRow = s.createRow(rowNumber);
			XSSFRow retManListRow = s.getRow(RM_LIST_ROW_NUMBER_INT);
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


}
