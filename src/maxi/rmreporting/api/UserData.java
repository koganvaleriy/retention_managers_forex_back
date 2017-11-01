package maxi.rmreporting.api;

public class UserData {

	public String account;
	public double userID;
	public String retManager;
	public double balance;
	public int dealsNumber;

	public UserData() {

	}

	public UserData(String account, double userID, String retManager, double balance) {
		super();
		this.account = account.toString();
		this.userID = userID;
		this.retManager = retManager.toString();
		this.balance = balance;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public void setRetManager(String retMAnager) {
		this.retManager = retMAnager;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getDealsNumber() {
		return dealsNumber;
	}

	public void setDealsNumber(int dealsNumber) {
		this.dealsNumber = dealsNumber;
	}

}
