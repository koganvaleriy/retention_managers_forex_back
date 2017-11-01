package maxi.rmreporting.api;

public class CreditData {

	public double transactionID;
	public double userID;
	public String retManager;
	public double date;
	public double amount;
	public String account;
	public String transactionSubType;
	public String businessGroup;
	
	public CreditData() {
		
	}

	public CreditData(double transactionID, double userID, String retManager, double date, double amount,
			String account, String transactionSubType, String businessGroup) {
		super();
		this.transactionID = transactionID;
		this.userID = userID;
		this.retManager = retManager.toString();
		this.date = date;
		this.amount = amount;
		this.account = account.toString();
		this.transactionSubType = transactionSubType.toString();
		this.businessGroup = businessGroup.toString();
	}

	public double getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(double transactionID) {
		this.transactionID = transactionID;
	}

	public double getUserID() {
		return userID;
	}

	public void setUserID(double userID) {
		this.userID = userID;
	}

	public String getRetManager() {
		return retManager;
	}

	public void setRetManager(String retManager) {
		this.retManager = retManager;
	}

	public double getDate() {
		return date;
	}

	public void setDate(double date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTransactionSubType() {
		return transactionSubType;
	}

	public void setTransactionSubType(String transactionSubType) {
		this.transactionSubType = transactionSubType;
	}

	public String getBusinessGroup() {
		return businessGroup;
	}

	public void setBusinessGroup(String businessGroup) {
		this.businessGroup = businessGroup;
	}

}
