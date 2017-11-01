package maxi.rmreporting.api;

public class ClosedDealData {

	private String dealID;
	private double userID;
	private String retManager;
	private double closedDate;
	private double volume;
	private String account;
	private double pnl;
	private double result;
	private double opendDate;

	public ClosedDealData() {

	}

	public ClosedDealData(String dealID, double userID, String retManager, double closedDate, double volume,
			String account, double pnl, double result, double opendDate) {
		super();
		this.dealID = dealID.toString();
		this.userID = userID;
		this.retManager = retManager.toString();
		this.closedDate = closedDate;
		this.volume = volume;
		this.account = account.toString();
		this.pnl = pnl;
		this.result = result;
		this.opendDate = opendDate;

	}

	public String getDealID() {
		return dealID;
	}

	public void setDealID(String dealID) {
		this.dealID = dealID;
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

	public double getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(double closedDate) {
		this.closedDate = closedDate;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public double getPnl() {
		return pnl;
	}

	public void setPnl(double pnl) {
		this.pnl = pnl;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public double getOpendDate() {
		return opendDate;
	}

	public void setOpendDate(double opendDate) {
		this.opendDate = opendDate;
	}

}
