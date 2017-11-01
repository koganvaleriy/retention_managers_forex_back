package maxi.rmreporting.api;

public class DealOpenedByDealerData {

	private String dealID;
	private String retManager;
	private String account;

	public DealOpenedByDealerData() {

	}

	public DealOpenedByDealerData(String dealID, String retManager, String account) {
		super();
		this.dealID = dealID.toString();
		this.retManager = retManager.toString();
		this.account = account.toString();
	}

	public String getDealID() {
		return dealID;
	}

	public void setDealID(String dealID) {
		this.dealID = dealID;
	}

	public String getRetManager() {
		return retManager;
	}

	public void setRetManager(String retManager) {
		this.retManager = retManager;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
