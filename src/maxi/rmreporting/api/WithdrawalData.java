package maxi.rmreporting.api;

public class WithdrawalData {

	public double transactionID;
	public double userID;
	public String retManager;
	public double date;
	public double amount;
	public String account;
	public String BusinessGroup;

	public WithdrawalData(double transactionID, double userID, String retManager, double date, double amount, String account,
			String businessGroup) {
		super();
		this.transactionID = transactionID;
		this.userID = userID;
		this.retManager = retManager.toString();
		this.date = date;
		this.amount = amount;
		this.account = account.toString();
		this.BusinessGroup = businessGroup.toString();
	}

	public WithdrawalData() {

	}

	public double getTransactionID() {
		return transactionID;
	}

	public double getUserID() {
		return userID;
	}

	public String getRetManager() {
		return retManager;
	}

	public double getDate() {
		return date;
	}

	public double getAmount() {
		return amount;
	}

	public String getAccount() {
		return account;
	}

	public String getBusinessGroup() {
		return BusinessGroup;
	}

	public void setTransactionID(double transactionID) {
		this.transactionID = transactionID;
	}

	public void setUserID(double userID) {
		this.userID = userID;
	}

	public void setRetManager(String retManager) {
		this.retManager = retManager;
	}

	public void setDate(double date) {
		this.date = date;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setBusinessGroup(String businessGroup) {
		BusinessGroup = businessGroup;
	}

	@Override
	public String toString() {
		return "DepositData [transactionID=" + transactionID + ", UserID=" + userID + ", RetManager=" + retManager
				+ ", date=" + date + ", amount=" + amount + ", account=" + account + ", BusinessGroup=" + BusinessGroup
				+ "]";
	}
	

}
