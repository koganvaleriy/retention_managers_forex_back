package maxi.rmreporting.controllers;

import org.apache.poi.util.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import maxi.rmreporting.formatting.Styles;
import maxi.rmreporting.formatting.StylesAr;
import maxi.rmreporting.formatting.StylesEn;
import maxi.rmreporting.formatting.StylesSp;
import maxi.rmreporting.model.dao.RetentionManagersForexOrm;
import maxi.rmreporting.storage.StorageFileNotFoundException;
import maxi.rmreporting.storage.StorageProperties;
import maxi.rmreporting.storage.StorageService;
import maxi.rmreporting.utilities.XSSFReadWrite;
import maxi.rmreporting.utilities.XSSFReadWriteInternational;

import static maxi.rmreporting.api.Desks.*;

import static maxi.rmreporting.api.MonthAndYear.*;
import static maxi.rmreporting.api.RESTCommands.*;
import static maxi.rmreporting.api.russian.FileNamesAndColumnTitles.*;
import static maxi.rmreporting.api.russian.RetentionManagersForexTableRows.*;
import static maxi.rmreporting.api.international.FileNamesAndColumnTitlesAr.*;
import static maxi.rmreporting.api.international.FileNamesAndColumnTitlesEn.*;
import static maxi.rmreporting.api.international.FileNamesAndColumnTitlesSp.*;
import static maxi.rmreporting.api.international.RetentionManagersForexTableRowsInt.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;

@RestController
@SpringBootApplication
@ImportResource("classpath:beans.xml")
@EnableConfigurationProperties(StorageProperties.class)
public class RestContoller {

	private static final String NOTIFICATION_ABOUT_RM = "Attention! Following managers have received deposits but they are not existing in Retention Managers list - ";
	private static final String FILE_NOT_FOUND = "File not found";
	private final String SUCCESS_MESSAGE = "Congrats! Success! Duration - ";
	private final String ERROR_MESSAGE_P1 = "Oh, what a pity! You received error - ";
	private final String ERROR_MESSAGE_P2 = "! For more information please contact talanted senior developer kogan.valeriy@gmail.com (50$ for the request)";
	
	private final StorageService storageService;
	
    @Autowired
    public RestContoller(StorageService storageService) {
        this.storageService = storageService;
    }
	
	XSSFReadWrite outputfile = new XSSFReadWrite();
	XSSFReadWriteInternational outputfileInt = new XSSFReadWriteInternational();

	String query;

	@Autowired
	RetentionManagersForexOrm repository;

	@RequestMapping(value = "CHECK")
	public String check() {
		return "OK";
	}
	
	//Month and year setting
	@CrossOrigin
	@PostMapping(SET_MONTH)
	public String setMonth(@RequestBody String month) {
		try{
			setCurrentMonth(month);
			return SUCCESS_MESSAGE;
		} catch (Exception e) {
			return ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2;
		}
		
	}
	
	@CrossOrigin
	@PostMapping(SET_YEAR)
	public String setYear(@RequestBody String year) {
		try{
			setCurrentYear(Integer.parseInt(year));
			return SUCCESS_MESSAGE;
		} catch (Exception e) {
			return ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2;
		}
	}
	

	// Totals
	@CrossOrigin
	@RequestMapping(value = CREATE_REP_ALL)
	public String createReportsForexAll() {

		double start = System.currentTimeMillis();

		fillDatabases();
		performAllQueriesRu();
		performAllQueriesAr();
		performAllQueriesSp();
		performAllQueriesEn();
		String unknownRM = findUnknownRM();
		setStyleRu();
		setStyleAr();
		setStyleSp();
		setStyleEn();
		//clearDB();
		deleteFiles();

		double end = System.currentTimeMillis();

		String result = "\nAll Reports: " + SUCCESS_MESSAGE + Math.round((end - start) / 1000 / 60) + "min " + unknownRM;
		System.out.println(result);

		return result;
	}
	
	@CrossOrigin
	@RequestMapping(value = FILL_DATABASES)
	public String fillDatabases() {
		double start = System.currentTimeMillis();
		System.out.println("DB filling started " + start);

		fillRetManDB();
		fillActivationsDB();
		fillDepositsDB();
		fillCreditsDB();
		fillWithdrawalsDB();
		fillClosedDealsDB();
		fillBalancesLastMonthDB();
		fillBalancesDB();
		fillDealsOpenedByDealerDB();
		fillLocksDB();
		fillReopeningsDB();

		double end = System.currentTimeMillis();

		String result = "\nAll Databases Filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min";
		System.out.println(result);

		return result;
	}

	// Reports Creation per Desk

	@CrossOrigin
	@RequestMapping(value = CREATE_REP_RU)
	public String createReportForexRu() {

		double start = System.currentTimeMillis();

		fillDatabases();
		performAllQueriesRu();
		String unknownRM = findUnknownRM();
		setStyleRu();
		clearDB();
		deleteFiles();

		double end = System.currentTimeMillis();

		String result = "\nReport RU Creation: " + SUCCESS_MESSAGE + Math.round((end - start)) / 1000 / 60 + " min " + unknownRM;
		System.out.println(result);

		return result;
	}
	
	@CrossOrigin
	@RequestMapping(value = CREATE_REP_AR)
	public String createReportForexAr() {

		double start = System.currentTimeMillis();

		fillDatabases();
		performAllQueriesAr();
		String unknownRM = findUnknownRM();
		setStyleAr();
		clearDB();
		deleteFiles();

		double end = System.currentTimeMillis();

		String result = "\nReport AR Creation: " + SUCCESS_MESSAGE + Math.round((end - start)) / 1000 / 60 + " min " + unknownRM;
		System.out.println(result);

		return result;
	}
	
	@CrossOrigin
	@RequestMapping(value = CREATE_REP_EN)
	public String createReportForexEn() {

		double start = System.currentTimeMillis();

		fillDatabases();
		performAllQueriesEn();
		String unknownRM = findUnknownRM();
		setStyleEn();
		clearDB();
		deleteFiles();

		double end = System.currentTimeMillis();

		String result = "\nReport EN Creation: " + SUCCESS_MESSAGE + Math.round((end - start)) / 1000 / 60 + " min " + unknownRM;
		System.out.println(result);

		return result;
	}
	
	@CrossOrigin
	@RequestMapping(value = CREATE_REP_SP)
	public String createReportForexSp() {

		double start = System.currentTimeMillis();

		fillDatabases();
		performAllQueriesSp();
		String unknownRM = findUnknownRM();
		setStyleSp();
		clearDB();
		deleteFiles();

		double end = System.currentTimeMillis();

		String result = "\nReport SP Creation: " + SUCCESS_MESSAGE + Math.round((end - start)) / 1000 / 60 + " min " + unknownRM;
		System.out.println(result);

		return result;
	}

	// All Queries Creation per Desk

	@RequestMapping(value = PERFORM_ALL_QUERIES_RU)
	public String performAllQueriesRu() {

		double start = System.currentTimeMillis();
		System.out.println("Queries RU performing started");

		createFile();
		getDepositsNumRu();
		getDepositsSumRu();
		getDepositsMore100Ru();
		getDepositsMore450Ru();
		getDepositsMore1000Ru();
		getDepositors();
		getDepositorsMore100();
		getDepositorsMore450();
		getDepositorsMore1000();
		getNewDepositsNum();
		getNewDepositorsNum();
		getNewDepositsSum();
		getCreditsRedeposits();
		getCreditsPriors();
		getWithdraws();
		getVolume();
		getPNL();
		getTradesNum();
		getNewVolume();
		getActiveUser();
		getClientsEOPRu();
		getAccountsValueEOPRu();
		getAccountsValueBOPRu();
		getBalancesMore100();
		getBalancesMore1000();
		getActiveBalancesMore1000();
		getActiveBalancesMore100();
		getActiveMore5();
		getNewUsers();
		getNewUsersMore5();
		getTradesDealing();
		getCreditsLock();
		getDepositsLock();
		getCreditsReopening();
		getDepositsReopening();

		double end = System.currentTimeMillis();

		String result = "\nAll Queries: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min";
		System.out.println(result);

		return result;
	}

	@RequestMapping(value = PERFORM_ALL_QUERIES_AR)
	public String performAllQueriesAr() {

		double start = System.currentTimeMillis();
		System.out.println("Queries AR performing started ");

		createFileAr();
		getDepositsNumAr();
		getDepositsSumAr();
		getDepositorsAr();
		getNewDepositsNumAr();
		getNewDepositorsNumAr();
		getNewDepositsSumAr();
		getCreditsRedepositsAr();
		getCreditsPriorsAr();
		getWithdrawsAr();
		getVolumeAr();
		getPNLAr();
		getTradesNumAr();
		getNewVolumeAr();
		getActiveUserAr();
		getClientsEOPAr();
		getAccountsValueEOPAr();
		getAccountsValueBOPAr();
		getNewUsersAr();
		getNewUsersMore5Ar();
		getTradesDealingAr();
		getCreditsLockAr();
		getDepositsLockAr();
		getCreditsReopeningAr();
		getDepositsReopeningAr();

		double end = System.currentTimeMillis();

		String result = "\nAll Queries Arabic: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min";
		System.out.println(result);

		return result;
	}

	@RequestMapping(value = PERFORM_ALL_QUERIES_EN)
	public String performAllQueriesEn() {

		double start = System.currentTimeMillis();
		System.out.println("Queries EN performing started ");

		createFileEn();
		getDepositsNumEn();
		getDepositsSumEn();
		getDepositorsEn();
		getNewDepositsNumEn();
		getNewDepositorsNumEn();
		getNewDepositsSumEn();
		getCreditsRedepositsEn();
		getCreditsPriorsEn();
		getWithdrawsEn();
		getVolumeEn();
		getPNLEn();
		getTradesNumEn();
		getNewVolumeEn();
		getActiveUserEn();
		getClientsEOPEn();
		getAccountsValueEOPEn();
		getAccountsValueBOPEn();
		getNewUsersEn();
		getNewUsersMore5En();
		getTradesDealingEn();
		getCreditsLockEn();
		getDepositsLockEn();
		getCreditsReopeningEn();
		getDepositsReopeningEn();

		double end = System.currentTimeMillis();

		String result = "\nAll Queries English: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min";
		System.out.println(result);

		return result;
	}

	@RequestMapping(value = PERFORM_ALL_QUERIES_SP)
	public String performAllQueriesSp() {

		double start = System.currentTimeMillis();
		System.out.println("Queries Sp performing started ");

		createFileSp();
		getDepositsNumSp();
		getDepositsSumSp();
		getDepositorsSp();
		getNewDepositsNumSp();
		getNewDepositorsNumSp();
		getNewDepositsSumSp();
		getCreditsRedepositsSp();
		getCreditsPriorsSp();
		getWithdrawsSp();
		getVolumeSp();
		getPNLSp();
		getTradesNumSp();
		getNewVolumeSp();
		getActiveUserSp();
		getClientsEOPSp();
		getAccountsValueEOPSp();
		getAccountsValueBOPSp();
		getNewUsersSp();
		getNewUsersMore5Sp();
		getTradesDealingSp();
		getCreditsLockSp();
		getDepositsLockSp();
		getCreditsReopeningSp();
		getDepositsReopeningSp();

		double end = System.currentTimeMillis();

		String result = "\nAll Queries Spanish: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min";
		System.out.println(result);

		return result;
	}

	// Set Styles

	@RequestMapping(value = SET_STYLE_RU)
	public String setStyleRu() {

		double start = System.currentTimeMillis();

		try {
			Styles styles = new Styles();
			styles.setReportSyle(OUTPUT_FILENAME);
			outputfile.fillLeftColumn(OUTPUT_FILENAME);
			double end = System.currentTimeMillis();

			return ("\nStyles setting: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			e.printStackTrace();
			return ("\nnStyles setting: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	@RequestMapping(value = SET_STYLE_AR)
	public String setStyleAr() {

		double start = System.currentTimeMillis();

		try {
			StylesAr styles = new StylesAr();
			styles.setReportSyle(OUTPUT_FILENAME_AR);
			outputfileInt.fillLeftColumn(OUTPUT_FILENAME_AR);
			double end = System.currentTimeMillis();

			return ("\nStyles setting: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			e.printStackTrace();
			return ("\nnStyles setting: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	@RequestMapping(value = SET_STYLE_EN)
	public String setStyleEn() {

		double start = System.currentTimeMillis();

		try {
			StylesEn styles = new StylesEn();
			styles.setReportSyle(OUTPUT_FILENAME_EN);
			outputfileInt.fillLeftColumn(OUTPUT_FILENAME_EN);
			double end = System.currentTimeMillis();

			return ("\nStyles setting: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			e.printStackTrace();
			return ("\nnStyles setting: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	@RequestMapping(value = SET_STYLE_SP)
	public String setStyleSp() {

		double start = System.currentTimeMillis();

		try {
			StylesSp styles = new StylesSp();
			styles.setReportSyle(OUTPUT_FILENAME_SP);
			outputfileInt.fillLeftColumn(OUTPUT_FILENAME_SP);
			double end = System.currentTimeMillis();

			return ("\nStyles setting: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			e.printStackTrace();
			return ("\nnStyles setting: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	// Database Filling

	@RequestMapping(value = FILL_RM_DB)
	public String fillRetManDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillRetManDB started");

		try {
			repository.fillRetManagersDBFromExsel(RETMANAGERSFILE);
			double end = System.currentTimeMillis();
			return ("\nRetention Managers DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (Exception e) {
			return ("\nRetention Managers DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = FILL_ACTIVATIONS_DB)
	public String fillActivationsDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillActivationsDB started");

		try {
			repository.fillActivationsDBFromExsel(ACTIVATIONS_FILE);
			double end = System.currentTimeMillis();
			return ("\nActivations DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (Exception e) {
			return ("\nActivations DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = FILL_DEPOSITS_DB)
	public String fillDepositsDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillDepositsDB started");

		try {
			repository.fillDepositsDBFromExsel(DEPOSITSFILE);
			double end = System.currentTimeMillis();
			return ("\nDeposits DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (Exception e) {
			return ("\nDeposits DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = FILL_CREDITS_DB)
	public String fillCreditsDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillCreditsDB started");

		try {
			repository.fillCreditsDBFromExsel(CREDITS_FILE);
			double end = System.currentTimeMillis();
			return ("\nCredits DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (Exception e) {
			return ("\nCredits DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = FILL_WITHDRAWALS_DB)
	public String fillWithdrawalsDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillWithdrawalsDB started");

		try {
			repository.fillWithdrawalsDBFromExsel(WITHDRAWALS_FILE);
			double end = System.currentTimeMillis();
			return ("\nWithdrawals DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (Exception e) {
			return ("\nWithdrawals DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	@RequestMapping(value = FILL_CLOSED_DEALS_DB)
	public String fillClosedDealsDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillClosedDealsDB started");

		try {
			repository.fillClosedDealsDBFromExsel(CLOSED_DEALS_FILE);
			double end = System.currentTimeMillis();
			return ("\nClosedDeals DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (Exception e) {
			return ("\nClosedDeals DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = FILL_BALANCES_LAST_MONTH_DB)
	public String fillBalancesLastMonthDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillBalancesLastMonthDB started");

		try {
			repository.fillBalancesLastMonthDBFromExsel(BALANCES_LAST_MONTH_FILE);
			double end = System.currentTimeMillis();
			return ("\nBalancesLastMonth DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (Exception e) {
			return ("\nBalancesLastMonth DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	@RequestMapping(value = FILL_BALANCES_DB)
	public String fillBalancesDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillBalancesDB started");

		try {
			repository.fillBalancesDBFromExsel(BALANCES_FILE);
			double end = System.currentTimeMillis();
			return ("\nBalances DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (Exception e) {
			return ("\nBalances DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = FILL_DEALS_DEALING_DB)
	public String fillDealsOpenedByDealerDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillDealsOpenedByDealerDB started");

		try {
			repository.fillDealsOpenedByDealerDBFromExsel(MT4_OPENED_BY_DEALER_FILE, NB_OPENED_BY_DEALER_FILE);
			double end = System.currentTimeMillis();
			return ("\nDeals Opened Via Dealing DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60
					+ " min");
		} catch (Exception e) {
			return ("\nDeals Opened Via Dealing DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage()
					+ ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = FILL_LOCKS_DB)
	public String fillLocksDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillLocksDB started");

		try {
			repository.fillLocksDBFromExsel(LOCKS_FILE);
			double end = System.currentTimeMillis();
			return ("\nLocks DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (IOException e) {
			return ("\nLocks DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	@RequestMapping(value = FILL_REOPENINGS_DB)
	public String fillReopeningsDB() {

		double start = System.currentTimeMillis();
		System.out.println("\nfillReopeningsDB started");

		try {
			repository.fillReopeningsDBFromExsel(REOPENINGS_FILE);
			double end = System.currentTimeMillis();
			return ("\nReopenings DataBase filling: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} catch (IOException e) {
			return ("\nReopenings DataBase filling: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	// Queries RU

	@RequestMapping(value = CREATE_FILE_RU)
	public String createFile() {

		double start = System.currentTimeMillis();

		query = "select " + 
				"retManager_name," 
				+ "sum(amount) as result, "
				+ "amount, transactionID, BusinessGroup, account, date, userID, newUser " 
				+ "from deposits "
				+ "where retManager_name <> '0' "
				+ "group by retManager_name";
		try {
			outputfile.writeRetManagersForexTemplateFile(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					RUSSIAN_DESK);

			double end = System.currentTimeMillis();
			return ("\nExsel File Creating: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nExsel File Creating: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_NUM_RU)
	public String getDepositsNumRu() {

		double start = System.currentTimeMillis();

		query = "select " + "concat(floor(date), ' ',userID) as k, " + "retManager_name,"
				+ "count(distinct(concat(floor(date), ' ',userID))) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITS_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nNumber of deposits: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNumber of deposits: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_TOTAL_RU)
	public String getDepositsSumRu() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					TOTAL_DEPOSIT_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nTotal Deposit: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal Deposit: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_MORE100_RU)
	public String getDepositsMore100Ru() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=100) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITS_MORE100_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nDeposits > 100: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeposits > 100: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_MORE450_RU)
	public String getDepositsMore450Ru() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers Deposits > 450 */

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITS_MORE450_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nDeposits > 450: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeposits > 450: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_MORE1000_RU)
	public String getDepositsMore1000Ru() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers Deposits > 1000 */

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=1000) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITS_MORE1000_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nDeposits > 1000: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeposits > 1000: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITORS_NUM_RU)
	public String getDepositors() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers # Deposited Users */

		query = "select " + "retManager_name," + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITED_USERS_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\n# Deposited Users: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Deposited Users: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITORS_MORE100_RU)
	public String getDepositorsMore100() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers # Deposited Users >100 */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=100) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITED_USERS_MORE100_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\n# Deposited Users >100: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Deposited Users >100: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITORS_MORE450_RU)
	public String getDepositorsMore450() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers # Deposited Users >450 */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITED_USERS_MORE450_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\n# Deposited Users >450: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Deposited Users >450: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITORS_MORE1000_RU)
	public String getDepositorsMore1000() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers # Deposited Users >1000 */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=1000) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITED_USERS_MORE1000_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\n# Deposited Users >1000: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Deposited Users >1000: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITS_MORE450_RU)
	public String getNewDepositsNum() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New user's redeposits (>450 USD) */

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp " + "where newUser='true' "
				+ "group by retManager_name; ";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITS_NUMBER_MORE450_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nNew user's redeposits (>450 USD): " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew user's redeposits (>450 USD): " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITORS_MORE450_RU)
	public String getNewDepositorsNum() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New users redeposited (>450USD) */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp " + "where newUser='true' "
				+ "group by retManager_name; ";

		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITED_MORE450_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nNew users redeposited (>450USD): " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users redeposited (>450USD): " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITS_RU)
	public String getNewDepositsSum() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New users redeposits sum */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "where newUser='true' " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITS_SUM_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nNew users redeposits sum: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users redeposits sum: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_REDEPOSITS_RU)
	public String getCreditsRedeposits() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers Credits redeposits */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, transactionSubType " + "from credits "
				+ "where transactionSubType='" + REDEPOSIT_BONUS + "' " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyCreditsDataByRetManagers(query),
					CREDITS_REDEPOSITS);

			double end = System.currentTimeMillis();
			return ("\nCredits redeposits: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits redeposits: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_PRIOR_RU)
	public String getCreditsPriors() {

		double start = System.currentTimeMillis();

		/* Test getAnyCreditsDataByRetManagers Credits Prior */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, transactionSubType " + "from credits "
				+ "where transactionSubType='" + PENDING_BONUS + "' " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyCreditsDataByRetManagers(query),
					CREDITS_PRIOR);

			double end = System.currentTimeMillis();
			return ("\nCredits Prior: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits Prior: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = WITHDRAWS_RU)
	public String getWithdraws() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataWithdrawalByRetManagers Total Withdraw */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from withdrawals "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME,
					repository.getAnyDataWithdrawalByRetManagers(query), TOTAL_WITHDRAWALS_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nTotal Withdraw: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal Withdraw: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = VOLUME_RU)
	public String getVolume() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers Volume */

		query = "select " + "retManager_name,sum(volume) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDealsDataByRetManagers(query),
					VOLUME_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nVolume: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nVolume: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = PNL_RU)
	public String getPNL() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers Deals PnL */

		query = "select " + "retManager_name,-sum(pnl) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDealsDataByRetManagers(query),
					DEALS_PNL_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nDeals Pnl: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeals Pnl: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TRADES_RU)
	public String getTradesNum() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers # Trades */

		query = "select " + "retManager_name,count(dealID) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDealsDataByRetManagers(query),
					TRADES_NUMBER_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nDeals Pnl: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeals Pnl: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_VOLUME_RU)
	public String getNewVolume() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers New users Volume */

		query = "select " + "retManager_name,sum(volume) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "and newUser='true' "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyDealsDataByRetManagers(query),
					NEW_USERS_VOLUME_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nNew users Volume: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users Volume: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACTIVE_RU)
	public String getActiveUser() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Active Users */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where dealsNumber >= 1 "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					ACTIVE_USERS_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\n# Active Users: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Active Users: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TOTAL_CLIENTS_EOP_RU)
	public String getClientsEOPRu() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Total clients EoP */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					TOTAL_CLIENTS_EOP_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nTotal clients EoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal clients EoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACCOUNT_VALUE_EOP_RU)
	public String getAccountsValueEOPRu() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Account Value EoP */

		query = "select " + "retManager_name,sum(balance) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					ACCOUNT_VALUE_EOP_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nAccount value EoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nAccount value EoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACCOUNT_VALUE_BOP_RU)
	public String getAccountsValueBOPRu() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Account Value BoP */

		query = "select " + "retManager_name,sum(balance) as result," + "account, balance, userID "
				+ "from balances_last_month " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME,
					repository.getAnyBalancesLastMonthDataByRetManagers(query), ACCOUNT_VALUE_BOP_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nAccount value BoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nAccount value BoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TOTAL_CLIENTS_MORE100_RU)
	public String getBalancesMore100() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Balance >= 100$ */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where balance >= 100 "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					BALANCE_MORE100_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nBalance >= 100$: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nBalance >= 100$: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TOTAL_CLIENTS_MORE1000_RU)
	public String getBalancesMore1000() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Balance >= 1000$ */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where balance >= 1000 "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					BALANCE_MORE1000_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nBalance >= 1000$: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nBalance >= 1000$: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TOTAL_CLIENTS_ACTIVE_MORE1000_RU)
	public String getActiveBalancesMore1000() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Active Balance >= 1000$ */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where balance >= 1000 "
				+ "and dealsNumber >= 1 " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					ACTIVE_MORE1000_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nActive Users with Balance>=1000: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nActive Users with Balance>=1000: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TOTAL_CLIENTS_ACTIVE_MORE100_RU)
	public String getActiveBalancesMore100() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Active Balance >= 100$ */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where balance >= 100 "
				+ "and dealsNumber >= 1 " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					ACTIVE_MORE100_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nActive Users with Balance>=100: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nActive Users with Balance>=100: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACTIVE_MORE5_RU)
	public String getActiveMore5() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers DT=>5 */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where dealsNumber >= 5 "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					DT_MORE5_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nDT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_USERS_RU)
	public String getNewUsers() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers New users (<60 days) */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where newUser = 'true' "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					NEW_USERS60_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nNew users (<60 days) : " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users (<60 days) : " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_USERS_MORE5_RU)
	public String getNewUsersMore5() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers New users DT=>5 */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where newUser = 'true' and "
				+ "dealsNumber >= 5 " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyBalancesDataByRetManagers(query),
					NEW_USERS_MORE5_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nNew users DT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users DT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TRADES_DEALING_RU)
	public String getTradesDealing() {

		double start = System.currentTimeMillis();

		/*
		 * Test getAnyDealsByDealerDataByRetManagers # Trades opened via dealing
		 */

		query = "select " + "retManager_name,count(distinct(dealID)) as result," + "dealID, account "
				+ "from deals_opened_by_dealer " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME,
					repository.getAnyDealsByDealerDataByRetManagers(query), TRADES_OPENED_VIA_DEALING_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nNew users DT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users DT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_LOCK_RU)
	public String getCreditsLock() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Credits for locking positions */

		query = "select " + "retManager_name,sum(credit) as result,"
				+ "id, credit, date, dateOfDebit, dateOfDeposit, deposit " + "from locks " + "where monthname(date)='"
				+ currentMonth + "' and year(date)=" + currentYear + " " + "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyLockByRetManagers(query),
					CREDITS_FOR_LOCK_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nCredits for locking positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits for locking positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_LOCK_RU)
	public String getDepositsLock() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Deposits after locking positions */

		query = "select " + "retManager_name,sum(deposit) as result,"
				+ "id, credit, date, dateOfDebit, dateOfDeposit, deposit " + "from locks "
				+ "where monthname(dateOfDeposit)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyLockByRetManagers(query),
					DEPOSITS_AFTER_LOCK_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nDeposits after locking positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeposits after locking positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_REOPENING_RU)
	public String getCreditsReopening() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Reopening positions */

		query = "select " + "retManager_name,sum(refund) as result,"
				+ "id, refund, date, dateOfDebit, dateOfDeposit, deposit " + "from reopenings "
				+ "where monthname(date)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyReopeningByRetManagers(query),
					REOPENING_POSITIONS_ROWNUM);

			double end = System.currentTimeMillis();
			return ("\nReopening positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nReopening positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_REOPENING_RU)
	public String getDepositsReopening() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Deposits after reopening positions */

		query = "select " + "retManager_name,sum(deposit) as result,"
				+ "id, refund, date, dateOfDebit, dateOfDeposit, deposit " + "from reopenings "
				+ "where monthname(dateOfDeposit)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfile.writeDataByRetManagersToExsel(OUTPUT_FILENAME, repository.getAnyReopeningByRetManagers(query),
					DEPOSITS_AFTER_REOPEN);

			double end = System.currentTimeMillis();
			return ("\nReopening positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nReopening positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	// Queries Arabic

	@RequestMapping(value = CREATE_FILE_AR)
	public String createFileAr() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name," + "sum(amount) as result, "
				+ "amount, transactionID, BusinessGroup, account, date, userID, newUser " + "from deposits "
				+ "group by retManager_name";
		try {
			outputfileInt.writeRetManagersForexTemplateFile(OUTPUT_FILENAME_AR,
					repository.getAnyDataByRetManagers(query), ARABIC_DESK);

			double end = System.currentTimeMillis();
			return ("\nExsel File Creating: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nExsel File Creating: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_NUM_AR)
	public String getDepositsNumAr() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=50) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNumber of deposits: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNumber of deposits: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_TOTAL_AR)
	public String getDepositsSumAr() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR, repository.getAnyDataByRetManagers(query),
					TOTAL_DEPOSIT_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal Deposit: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal Deposit: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITORS_MORE50_AR)
	public String getDepositorsAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers # Deposited Users */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=50) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITED_USERS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\n# Deposited Users: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Deposited Users: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITS_MORE450_AR)
	public String getNewDepositsNumAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New user's redeposits (>450 USD) */

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp " + "where newUser='true' "
				+ "group by retManager_name; ";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITS_NUMBER_MORE450_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew user's redeposits (>450 USD): " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew user's redeposits (>450 USD): " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITORS_MORE450_AR)
	public String getNewDepositorsNumAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New users redeposited (>450USD) */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp " + "where newUser='true' "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITED_MORE450_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users redeposited (>450USD): " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users redeposited (>450USD): " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITS_AR)
	public String getNewDepositsSumAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New users redeposits sum */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "where newUser='true' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITS_SUM_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users redeposits sum: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users redeposits sum: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_REDEPOSITS_AR)
	public String getCreditsRedepositsAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers Credits redeposits */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, transactionSubType " + "from credits "
				+ "where transactionSubType='" + REDEPOSIT_BONUS + "' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyCreditsDataByRetManagers(query), CREDITS_REDEPOSITS_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits redeposits: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits redeposits: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_PRIOR_AR)
	public String getCreditsPriorsAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyCreditsDataByRetManagers Credits Prior */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, transactionSubType " + "from credits "
				+ "where transactionSubType='" + PENDING_BONUS + "' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyCreditsDataByRetManagers(query), CREDITS_PRIOR_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits Prior: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits Prior: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = WITHDRAWS_AR)
	public String getWithdrawsAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataWithdrawalByRetManagers Total Withdraw */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from withdrawals "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyDataWithdrawalByRetManagers(query), TOTAL_WITHDRAWALS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal Withdraw: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal Withdraw: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = VOLUME_AR)
	public String getVolumeAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers Volume */

		query = "select " + "retManager_name,sum(volume) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyDealsDataByRetManagers(query), VOLUME_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nVolume: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nVolume: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = PNL_AR)
	public String getPNLAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers Deals PnL */

		query = "select " + "retManager_name,-sum(pnl) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyDealsDataByRetManagers(query), DEALS_PNL_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeals Pnl: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeals Pnl: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TRADES_AR)
	public String getTradesNumAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers # Trades */

		query = "select " + "retManager_name,count(dealID) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyDealsDataByRetManagers(query), TRADES_NUMBER_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeals Pnl: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeals Pnl: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_VOLUME_AR)
	public String getNewVolumeAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers New users Volume */

		query = "select " + "retManager_name,sum(volume) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "and newUser='true' "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyDealsDataByRetManagers(query), NEW_USERS_VOLUME_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users Volume: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users Volume: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACTIVE_AR)
	public String getActiveUserAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Active Users */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where dealsNumber >= 5 "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyBalancesDataByRetManagers(query), ACTIVE_USERS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\n# Active Users: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Active Users: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TOTAL_CLIENTS_EOP_AR)
	public String getClientsEOPAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Total clients EoP */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyBalancesDataByRetManagers(query), TOTAL_CLIENTS_EOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal clients EoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal clients EoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACCOUNT_VALUE_EOP_AR)
	public String getAccountsValueEOPAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Account Value EoP */

		query = "select " + "retManager_name,sum(balance) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyBalancesDataByRetManagers(query), ACCOUNT_VALUE_EOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nAccount value EoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nAccount value EoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACCOUNT_VALUE_BOP_AR)
	public String getAccountsValueBOPAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Account Value BoP */

		query = "select " + "retManager_name,sum(balance) as result," + "account, balance, userID "
				+ "from balances_last_month " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyBalancesLastMonthDataByRetManagers(query), ACCOUNT_VALUE_BOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nAccount value BoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nAccount value BoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_USERS_AR)
	public String getNewUsersAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers New users (<60 days) */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where newUser = 'true' "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyBalancesDataByRetManagers(query), NEW_USERS60_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users (<60 days) : " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users (<60 days) : " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_USERS_MORE5_AR)
	public String getNewUsersMore5Ar() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers New users DT=>5 */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where newUser = 'true' and "
				+ "dealsNumber >= 5 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyBalancesDataByRetManagers(query), NEW_USERS_MORE5_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users DT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users DT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TRADES_DEALING_AR)
	public String getTradesDealingAr() {

		double start = System.currentTimeMillis();

		/*
		 * Test getAnyDealsByDealerDataByRetManagers # Trades opened via dealing
		 */

		query = "select " + "retManager_name,count(distinct(dealID)) as result," + "dealID, account "
				+ "from deals_opened_by_dealer " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyDealsByDealerDataByRetManagers(query), TRADES_OPENED_VIA_DEALING_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users DT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users DT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_LOCK_AR)
	public String getCreditsLockAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Credits for locking positions */

		query = "select " + "retManager_name,sum(credit) as result,"
				+ "id, credit, date, dateOfDebit, dateOfDeposit, deposit " + "from locks " + "where monthname(date)='"
				+ currentMonth + "' and year(date)=" + currentYear + " " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR, repository.getAnyLockByRetManagers(query),
					CREDITS_FOR_LOCK_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits for locking positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits for locking positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_LOCK_AR)
	public String getDepositsLockAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Deposits after locking positions */

		query = "select " + "retManager_name,sum(deposit) as result,"
				+ "id, credit, date, dateOfDebit, dateOfDeposit, deposit " + "from locks "
				+ "where monthname(dateOfDeposit)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR, repository.getAnyLockByRetManagers(query),
					DEPOSITS_AFTER_LOCK_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeposits after locking positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeposits after locking positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_REOPENING_AR)
	public String getCreditsReopeningAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Reopening positions */

		query = "select " + "retManager_name,sum(refund) as result,"
				+ "id, refund, date, dateOfDebit, dateOfDeposit, deposit " + "from reopenings "
				+ "where monthname(date)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyReopeningByRetManagers(query), REOPENING_POSITIONS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nReopening positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nReopening positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_REOPENING_AR)
	public String getDepositsReopeningAr() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Deposits after reopening positions */

		query = "select " + "retManager_name,sum(deposit) as result,"
				+ "id, refund, date, dateOfDebit, dateOfDeposit, deposit " + "from reopenings "
				+ "where monthname(dateOfDeposit)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_AR,
					repository.getAnyReopeningByRetManagers(query), DEPOSITS_AFTER_REOPEN_INT);

			double end = System.currentTimeMillis();
			return ("\nReopening positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nReopening positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	// Queries English

	@RequestMapping(value = CREATE_FILE_EN)
	public String createFileEn() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name," + "sum(amount) as result, "
				+ "amount, transactionID, BusinessGroup, account, date, userID, newUser " + "from deposits "
				+ "group by retManager_name";
		try {
			outputfileInt.writeRetManagersForexTemplateFile(OUTPUT_FILENAME_EN,
					repository.getAnyDataByRetManagers(query), ENGLISH_DESK);

			double end = System.currentTimeMillis();
			return ("\nExsel File Creating: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nExsel File Creating: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_NUM_EN)
	public String getDepositsNumEn() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=50) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNumber of deposits: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNumber of deposits: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_TOTAL_EN)
	public String getDepositsSumEn() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN, repository.getAnyDataByRetManagers(query),
					TOTAL_DEPOSIT_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal Deposit: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal Deposit: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITORS_MORE50_EN)
	public String getDepositorsEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers # Deposited Users */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=50) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITED_USERS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\n# Deposited Users: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Deposited Users: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITS_MORE450_EN)
	public String getNewDepositsNumEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New user's redeposits (>450 USD) */

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp " + "where newUser='true' "
				+ "group by retManager_name; ";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITS_NUMBER_MORE450_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew user's redeposits (>450 USD): " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew user's redeposits (>450 USD): " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITORS_MORE450_EN)
	public String getNewDepositorsNumEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New users redeposited (>450USD) */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp " + "where newUser='true' "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITED_MORE450_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users redeposited (>450USD): " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users redeposited (>450USD): " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITS_EN)
	public String getNewDepositsSumEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New users redeposits sum */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "where newUser='true' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITS_SUM_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users redeposits sum: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users redeposits sum: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_REDEPOSITS_EN)
	public String getCreditsRedepositsEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers Credits redeposits */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, transactionSubType " + "from credits "
				+ "where transactionSubType='" + REDEPOSIT_BONUS + "' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyCreditsDataByRetManagers(query), CREDITS_REDEPOSITS_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits redeposits: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits redeposits: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_PRIOR_EN)
	public String getCreditsPriorsEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyCreditsDataByRetManagers Credits Prior */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, transactionSubType " + "from credits "
				+ "where transactionSubType='" + PENDING_BONUS + "' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyCreditsDataByRetManagers(query), CREDITS_PRIOR_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits Prior: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits Prior: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = WITHDRAWS_EN)
	public String getWithdrawsEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataWithdrawalByRetManagers Total Withdraw */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from withdrawals "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyDataWithdrawalByRetManagers(query), TOTAL_WITHDRAWALS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal Withdraw: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal Withdraw: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = VOLUME_EN)
	public String getVolumeEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers Volume */

		query = "select " + "retManager_name,sum(volume) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyDealsDataByRetManagers(query), VOLUME_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nVolume: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nVolume: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = PNL_EN)
	public String getPNLEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers Deals PnL */

		query = "select " + "retManager_name,-sum(pnl) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyDealsDataByRetManagers(query), DEALS_PNL_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeals Pnl: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeals Pnl: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TRADES_EN)
	public String getTradesNumEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers # Trades */

		query = "select " + "retManager_name,count(dealID) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyDealsDataByRetManagers(query), TRADES_NUMBER_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeals Pnl: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeals Pnl: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_VOLUME_EN)
	public String getNewVolumeEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers New users Volume */

		query = "select " + "retManager_name,sum(volume) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "and newUser='true' "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyDealsDataByRetManagers(query), NEW_USERS_VOLUME_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users Volume: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users Volume: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACTIVE_EN)
	public String getActiveUserEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Active Users */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where dealsNumber >= 5 "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyBalancesDataByRetManagers(query), ACTIVE_USERS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\n# Active Users: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Active Users: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TOTAL_CLIENTS_EOP_EN)
	public String getClientsEOPEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Total clients EoP */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyBalancesDataByRetManagers(query), TOTAL_CLIENTS_EOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal clients EoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal clients EoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACCOUNT_VALUE_EOP_EN)
	public String getAccountsValueEOPEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Account Value EoP */

		query = "select " + "retManager_name,sum(balance) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyBalancesDataByRetManagers(query), ACCOUNT_VALUE_EOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nAccount value EoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nAccount value EoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACCOUNT_VALUE_BOP_EN)
	public String getAccountsValueBOPEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Account Value BoP */

		query = "select " + "retManager_name,sum(balance) as result," + "account, balance, userID "
				+ "from balances_last_month " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyBalancesLastMonthDataByRetManagers(query), ACCOUNT_VALUE_BOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nAccount value BoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nAccount value BoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_USERS_EN)
	public String getNewUsersEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers New users (<60 days) */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where newUser = 'true' "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyBalancesDataByRetManagers(query), NEW_USERS60_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users (<60 days) : " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users (<60 days) : " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_USERS_MORE5_EN)
	public String getNewUsersMore5En() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers New users DT=>5 */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where newUser = 'true' and "
				+ "dealsNumber >= 5 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyBalancesDataByRetManagers(query), NEW_USERS_MORE5_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users DT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users DT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TRADES_DEALING_EN)
	public String getTradesDealingEn() {

		double start = System.currentTimeMillis();

		/*
		 * Test getAnyDealsByDealerDataByRetManagers # Trades opened via dealing
		 */

		query = "select " + "retManager_name,count(distinct(dealID)) as result," + "dealID, account "
				+ "from deals_opened_by_dealer " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyDealsByDealerDataByRetManagers(query), TRADES_OPENED_VIA_DEALING_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users DT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users DT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_LOCK_EN)
	public String getCreditsLockEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Credits for locking positions */

		query = "select " + "retManager_name,sum(credit) as result,"
				+ "id, credit, date, dateOfDebit, dateOfDeposit, deposit " + "from locks " + "where monthname(date)='"
				+ currentMonth + "' and year(date)=" + currentYear + " " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN, repository.getAnyLockByRetManagers(query),
					CREDITS_FOR_LOCK_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits for locking positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits for locking positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_LOCK_EN)
	public String getDepositsLockEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Deposits after locking positions */

		query = "select " + "retManager_name,sum(deposit) as result,"
				+ "id, credit, date, dateOfDebit, dateOfDeposit, deposit " + "from locks "
				+ "where monthname(dateOfDeposit)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN, repository.getAnyLockByRetManagers(query),
					DEPOSITS_AFTER_LOCK_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeposits after locking positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeposits after locking positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_REOPENING_EN)
	public String getCreditsReopeningEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Reopening positions */

		query = "select " + "retManager_name,sum(refund) as result,"
				+ "id, refund, date, dateOfDebit, dateOfDeposit, deposit " + "from reopenings "
				+ "where monthname(date)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyReopeningByRetManagers(query), REOPENING_POSITIONS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nReopening positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nReopening positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_REOPENING_EN)
	public String getDepositsReopeningEn() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Deposits after reopening positions */

		query = "select " + "retManager_name,sum(deposit) as result,"
				+ "id, refund, date, dateOfDebit, dateOfDeposit, deposit " + "from reopenings "
				+ "where monthname(dateOfDeposit)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_EN,
					repository.getAnyReopeningByRetManagers(query), DEPOSITS_AFTER_REOPEN_INT);

			double end = System.currentTimeMillis();
			return ("\nReopening positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nReopening positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	// Queries Spanish

	@RequestMapping(value = CREATE_FILE_SP)
	public String createFileSp() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name," + "sum(amount) as result, "
				+ "amount, transactionID, BusinessGroup, account, date, userID, newUser " + "from deposits "
				+ "group by retManager_name";
		try {
			outputfileInt.writeRetManagersForexTemplateFile(OUTPUT_FILENAME_SP,
					repository.getAnyDataByRetManagers(query), SPANISH_DESK);

			double end = System.currentTimeMillis();
			return ("\nExsel File Creating: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nExsel File Creating: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_NUM_SP)
	public String getDepositsNumSp() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=50) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNumber of deposits: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNumber of deposits: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_TOTAL_SP)
	public String getDepositsSumSp() {

		double start = System.currentTimeMillis();

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP, repository.getAnyDataByRetManagers(query),
					TOTAL_DEPOSIT_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal Deposit: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal Deposit: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITORS_MORE50_SP)
	public String getDepositorsSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers # Deposited Users */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=50) as dtemp "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP, repository.getAnyDataByRetManagers(query),
					NUMBER_OF_DEPOSITED_USERS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\n# Deposited Users: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Deposited Users: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITS_MORE450_SP)
	public String getNewDepositsNumSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New user's redeposits (>450 USD) */

		query = "select " + "retManager_name, " + "count(distinct(concat(floor(date), ' ',userID))) as result, "
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp " + "where newUser='true' "
				+ "group by retManager_name; ";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITS_NUMBER_MORE450_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew user's redeposits (>450 USD): " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew user's redeposits (>450 USD): " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITORS_MORE450_SP)
	public String getNewDepositorsNumSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New users redeposited (>450USD) */

		query = "select " + "retManager_name, " + "count(distinct(userID)) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from ("
				+ "select concat(floor(date), ' ',userID) as ktemp, " + "sum(amount) as day_amount, "
				+ "retManager_name,transactionID, BusinessGroup, account, date, userID, amount, newUser "
				+ "from deposits " + "group by ktemp " + "having sum(amount)>=450) as dtemp " + "where newUser='true' "
				+ "group by retManager_name; ";

		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITED_MORE450_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users redeposited (>450USD): " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users redeposited (>450USD): " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_DEPOSITS_SP)
	public String getNewDepositsSumSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers New users redeposits sum */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from deposits "
				+ "where newUser='true' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP, repository.getAnyDataByRetManagers(query),
					NEW_USERS_REDEPOSITS_SUM_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users redeposits sum: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users redeposits sum: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_REDEPOSITS_SP)
	public String getCreditsRedepositsSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataByRetManagers Credits redeposits */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, transactionSubType " + "from credits "
				+ "where transactionSubType='" + REDEPOSIT_BONUS + "' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyCreditsDataByRetManagers(query), CREDITS_REDEPOSITS_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits redeposits: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits redeposits: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_PRIOR_SP)
	public String getCreditsPriorsSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyCreditsDataByRetManagers Credits Prior */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, transactionSubType " + "from credits "
				+ "where transactionSubType='" + PENDING_BONUS + "' " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyCreditsDataByRetManagers(query), CREDITS_PRIOR_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits Prior: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits Prior: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = WITHDRAWS_SP)
	public String getWithdrawsSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDataWithdrawalByRetManagers Total Withdraw */

		query = "select " + "retManager_name,sum(amount) as result,"
				+ "transactionID, BusinessGroup, account, date, userID, amount, newUser " + "from withdrawals "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyDataWithdrawalByRetManagers(query), TOTAL_WITHDRAWALS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal Withdraw: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal Withdraw: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = VOLUME_SP)
	public String getVolumeSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers Volume */

		query = "select " + "retManager_name,sum(volume) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyDealsDataByRetManagers(query), VOLUME_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nVolume: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nVolume: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = PNL_SP)
	public String getPNLSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers Deals PnL */

		query = "select " + "retManager_name,-sum(pnl) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyDealsDataByRetManagers(query), DEALS_PNL_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeals Pnl: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeals Pnl: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TRADES_SP)
	public String getTradesNumSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers # Trades */

		query = "select " + "retManager_name,count(dealID) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyDealsDataByRetManagers(query), TRADES_NUMBER_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeals Pnl: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeals Pnl: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_VOLUME_SP)
	public String getNewVolumeSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyDealsDataByRetManagers New users Volume */

		query = "select " + "retManager_name,sum(volume) as result,"
				+ "dealID, account, closedDate, opendDate, dealDuration, newUser, pnl, userID, volume "
				+ "from closed_deals " + "where dealDuration >= 180000 " + "and newUser='true' "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyDealsDataByRetManagers(query), NEW_USERS_VOLUME_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users Volume: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users Volume: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACTIVE_SP)
	public String getActiveUserSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Active Users */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where dealsNumber >= 5 "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyBalancesDataByRetManagers(query), ACTIVE_USERS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\n# Active Users: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\n# Active Users: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TOTAL_CLIENTS_EOP_SP)
	public String getClientsEOPSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Total clients EoP */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyBalancesDataByRetManagers(query), TOTAL_CLIENTS_EOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nTotal clients EoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nTotal clients EoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACCOUNT_VALUE_EOP_SP)
	public String getAccountsValueEOPSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Account Value EoP */

		query = "select " + "retManager_name,sum(balance) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyBalancesDataByRetManagers(query), ACCOUNT_VALUE_EOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nAccount value EoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nAccount value EoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = ACCOUNT_VALUE_BOP_SP)
	public String getAccountsValueBOPSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers Account Value BoP */

		query = "select " + "retManager_name,sum(balance) as result," + "account, balance, userID "
				+ "from balances_last_month " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyBalancesLastMonthDataByRetManagers(query), ACCOUNT_VALUE_BOP_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nAccount value BoP: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nAccount value BoP: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_USERS_SP)
	public String getNewUsersSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers New users (<60 days) */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where newUser = 'true' "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyBalancesDataByRetManagers(query), NEW_USERS60_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users (<60 days) : " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users (<60 days) : " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = NEW_USERS_MORE5_SP)
	public String getNewUsersMore5Sp() {

		double start = System.currentTimeMillis();

		/* Test getAnyBalancesDataByRetManagers New users DT=>5 */

		query = "select " + "retManager_name,count(distinct(userID)) as result,"
				+ "account, balance, dealsNumber, newUser, userID " + "from balances " + "where newUser = 'true' and "
				+ "dealsNumber >= 5 " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyBalancesDataByRetManagers(query), NEW_USERS_MORE5_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users DT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users DT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = TRADES_DEALING_SP)
	public String getTradesDealingSp() {

		double start = System.currentTimeMillis();

		/*
		 * Test getAnyDealsByDealerDataByRetManagers # Trades opened via dealing
		 */

		query = "select " + "retManager_name,count(distinct(dealID)) as result," + "dealID, account "
				+ "from deals_opened_by_dealer " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyDealsByDealerDataByRetManagers(query), TRADES_OPENED_VIA_DEALING_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nNew users DT=>5: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nNew users DT=>5: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_LOCK_SP)
	public String getCreditsLockSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Credits for locking positions */

		query = "select " + "retManager_name,sum(credit) as result,"
				+ "id, credit, date, dateOfDebit, dateOfDeposit, deposit " + "from locks " + "where monthname(date)='"
				+ currentMonth + "' and year(date)=" + currentYear + " " + "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP, repository.getAnyLockByRetManagers(query),
					CREDITS_FOR_LOCK_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nCredits for locking positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nCredits for locking positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_LOCK_SP)
	public String getDepositsLockSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Deposits after locking positions */

		query = "select " + "retManager_name,sum(deposit) as result,"
				+ "id, credit, date, dateOfDebit, dateOfDeposit, deposit " + "from locks "
				+ "where monthname(dateOfDeposit)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP, repository.getAnyLockByRetManagers(query),
					DEPOSITS_AFTER_LOCK_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nDeposits after locking positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nDeposits after locking positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = CREDITS_REOPENING_SP)
	public String getCreditsReopeningSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Reopening positions */

		query = "select " + "retManager_name,sum(refund) as result,"
				+ "id, refund, date, dateOfDebit, dateOfDeposit, deposit " + "from reopenings "
				+ "where monthname(date)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyReopeningByRetManagers(query), REOPENING_POSITIONS_ROWNUM_INT);

			double end = System.currentTimeMillis();
			return ("\nReopening positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nReopening positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	@RequestMapping(value = DEPOSITS_REOPENING_SP)
	public String getDepositsReopeningSp() {

		double start = System.currentTimeMillis();

		/* Test getAnyLockByRetManagers Deposits after reopening positions */

		query = "select " + "retManager_name,sum(deposit) as result,"
				+ "id, refund, date, dateOfDebit, dateOfDeposit, deposit " + "from reopenings "
				+ "where monthname(dateOfDeposit)='" + currentMonth + "' and year(date)=" + currentYear + " "
				+ "group by retManager_name";
		try {
			outputfileInt.writeDataByRetManagersToExsel(OUTPUT_FILENAME_SP,
					repository.getAnyReopeningByRetManagers(query), DEPOSITS_AFTER_REOPEN_INT);

			double end = System.currentTimeMillis();
			return ("\nReopening positions: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");

		} catch (IOException e) {
			return ("\nReopening positions: " + ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}

	//Clear DB
	@CrossOrigin
	@RequestMapping(value = CLEAR_DATABASES)
	public String clearDB() {
		
		double start = System.currentTimeMillis();
		System.out.println("DB Clearing started");
		
		if (repository.truncateAllTables()) {
			double end = System.currentTimeMillis();
			System.out.println("\nDB Clearing: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
			return ("\nDB Clearing: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
		} else {
			return ("\nDB Clearing: " + ERROR_MESSAGE_P1 + "Can't clear DB" + ERROR_MESSAGE_P2);
		}
		
		
	}
	
	
	//Find Unknown Ret Managers
	@CrossOrigin
	@RequestMapping(value = FIND_UNKNOWN_RM)
	public String findUnknownRM() {
		return NOTIFICATION_ABOUT_RM + repository.findUnknownRetentionManager();
	}
	
	
	
	// File uploading
/*
	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files",
				storageService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(RestController.class, "serveFile", path.getFileName().toString())
								.build().toString())
						.collect(Collectors.toList()));

		return "uploadForm";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
*/
	@CrossOrigin
	@PostMapping(UPLOAD_FILE)
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		storageService.store(file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect/";
	}
	
	@CrossOrigin
	@PostMapping(UPLOAD_FILES)
	public String handleFilesUpload(@RequestParam("file") MultipartFile[] files, RedirectAttributes redirectAttributes) throws FileSizeLimitExceededException{

		try {
			System.out.println("Outside cycle: " + files[0].getOriginalFilename());
			for (int i = 0; i < files.length; i++) {
				storageService.store(files[i]);
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + files[i].getOriginalFilename() + "!");
				System.out.println("Inside cycle: " + files[i].getOriginalFilename());
			}
			
			return checkFiles();
		} catch (Exception e) {
			System.out.println(ERROR_MESSAGE_P1 + ERROR_MESSAGE_P2);
			return (ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}

	}
	
	@CrossOrigin
	@RequestMapping(value = DELETE_FILES)
	public String deleteFiles() {
		double start = System.currentTimeMillis();
		storageService.deleteAll();
		double end = System.currentTimeMillis();
		
		return ("\nFiles Deleting: " + SUCCESS_MESSAGE + (end - start) / 1000 / 60 + " min");
	}
	
	@CrossOrigin
	@RequestMapping(value = CHECK_FILES)
	public String checkFiles() {
		System.currentTimeMillis();
		String res = storageService.checkFilesExisting();
		System.currentTimeMillis();
		
		if (res.equalsIgnoreCase("OK")) {
			System.out.println("\nAll files have been uploaded! ");
			return ("\nAll files have been uploaded! ");
		} else {
			System.out.println("\nSeems that you don't have such files: " + res);
			return ("\nSeems that you don't have such files: " + res);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = SEND_FILE_RU)
	public void sendFilesRu(HttpServletResponse response) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        

        try {
            File file = new File(OUTPUT_FILENAME);

            if(file.exists()){
                response.setHeader("Content-Disposition", "attachment; filename="
                        + file.getName());
                InputStream inputStream = new FileInputStream(file);
                IOUtils.copy(inputStream, response.getOutputStream());
            } else {
            	response.setHeader("Content-Disposition", "attachment; filename="
                        + FILE_NOT_FOUND);
            	IOUtils.copy(null, response.getOutputStream());
            }

		} catch (Exception e) {
			System.out.println(ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = SEND_FILE_EN)
	public void sendFilesEn(HttpServletResponse response) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        try {
            	File file = new File(OUTPUT_FILENAME_EN);
            	if(file.exists()){
                    response.setHeader("Content-Disposition", "attachment; filename="
                            + file.getName());
	                InputStream inputStream = new FileInputStream(file);
	                IOUtils.copy(inputStream, response.getOutputStream());
            	} else {
                	response.setHeader("Content-Disposition", "attachment; filename="
                            + FILE_NOT_FOUND);
                	IOUtils.copy(null, response.getOutputStream());
                }

		} catch (Exception e) {
			System.out.println(ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = SEND_FILE_AR)
	public void sendFilesAr(HttpServletResponse response) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        try {
        	
            File file = new File(OUTPUT_FILENAME_AR);
            if(file.exists()){
                InputStream inputStream = new FileInputStream(file);
                IOUtils.copy(inputStream, response.getOutputStream());
        	}

		} catch (Exception e) {
			System.out.println(ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = SEND_FILE_SP)
	public void sendFilesSp(HttpServletResponse response) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        try {
            File file = new File(OUTPUT_FILENAME_SP);
            if(file.exists()){
                InputStream inputStream = new FileInputStream(file);
                IOUtils.copy(inputStream, response.getOutputStream());
            }

		} catch (Exception e) {
			System.out.println(ERROR_MESSAGE_P1 + e.getMessage() + ERROR_MESSAGE_P2);
		}
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

	
	
	public static void main(String[] args) {
		SpringApplication.run(RestContoller.class, args);
		System.out.println("Server is ON");

	}
}
