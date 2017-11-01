package maxi.rmreporting.api.russian;

public interface FileNamesAndColumnTitles {
	
	String OUTPUT_FILENAME = "result-dir//Retention Managers Forex Russian.xlsx";
	String LAST_MONTH_REPORT = "upload-dir//Retention Managers Forex Russian Last Month.xlsx";
	
	String DEPOSITSFILE = "upload-dir//NB Deposits.xlsx";
	String TRANSACTION_ID = "Transaction Id";
	String CLIENT_ID = "Client Id";
	String RETENTION_MANAGER = "Retention Manager (Created)";
	String DATE = "Transaction Date";
	String AMOUNT = "Amount ABC";
	String ACCOUNT = "Account";
	String BUSINESS_GROUP = "Business Group";
	
	String RETMANAGERSFILE = "upload-dir//Ret Managers List.xlsx";
	String NAME = "Name";
	String DESK = "Desk";
	String TEAM = "Team";
	String TARGET = "Target";
	
	String ACTIVATIONS_FILE = "upload-dir//Activations.xlsx";
	String USER_ID_ACTIVATIONS = "User ID";
	
	String CREDITS_FILE = "upload-dir//NB Credits.xlsx";
	/* All titles like in Deposits */
	String TRANSACTION_SUB_TYPE = "Transaction Sub Type";
	String REDEPOSIT_BONUS = "Redeposit Bonus"; 
	String PENDING_BONUS = "Pending Bonus";
	
	String WITHDRAWALS_FILE = "upload-dir//NB Withdrawals.xlsx";
	/* All titles like in Deposits */
	
	String CLOSED_DEALS_FILE = "upload-dir//NB ClosedDeals.xlsx";
	String DEAL_ID = "Deal Id";
	String USER_ID_DEALS = "Client Id";
	String RETENTION_MANAGER_DEALS = "Retention Account Manager";
	String CLOSED_DATE = "Close Time";
	String VOLUME = "Volume ABC";
	String ACCOUNT_DEALS = "Account";
	String PNL = "Pnl";
	String OPEN_DATE = "Open Time";
	
	String BALANCES_FILE = "upload-dir//NB Balances.xlsx";
	String BALANCE = "Balance";
	String CLIENT_ID_BALANCES = "Client Id";
	String ACCOUNT_BALANCES = "Account";
	String RETENTION_MANAGER_BALANCES = "Retention Account Manager";
	
	String BALANCES_LAST_MONTH_FILE = "upload-dir//NB Balances Last Month.xlsx";
	/* All titles like in Balances */
	
	String MT4_OPENED_BY_DEALER_FILE = "upload-dir//MT4 opened by dealer.xlsx";
	String DEALID_MT4_OPENED_BY_DEALER = "Deal";
	String ACCOUNT_MT4_OPENED_BY_DEALER = "Login";
		
	String NB_OPENED_BY_DEALER_FILE = "upload-dir//NB Opened by Dealer.xlsx";
	/* All titles like in ClosedDeals */
	
	String LOCKS_FILE = "upload-dir//Locks.xlsx";
	String DATE_LOCKS = "Date";
	String RET_MANAGER_LOCKS = "Retention manager";
	String CREDIT_LOCKS = "Credit";
	String DEPOSIT_LOCKS = "Deposit";
	String DATE_OF_DEPOSIT_LOCKS = "Date of deposit";
	String DATE_OF_DEBIT_LOCKS = "Date of debit in the case of no deposit only";
	
	String REOPENINGS_FILE = "upload-dir//Reopenings.xlsx";
	String DATE_REOPENINGS = "Date";
	String RET_MANAGER_REOPENINGS = "Retention manager";
	String REFUND_FOR_REOPENING = "Refund for reopening";
	String DEPOSIT_REOPENING = "Deposit";
	String DATE_OF_DEPOSIT_REOPENING = "Date of deposit";
	String DATE_OF_DEBIT_REOPENING = "Date of debit in the case of no deposit only";
	
	

}
