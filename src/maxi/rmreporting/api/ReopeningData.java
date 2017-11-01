package maxi.rmreporting.api;

public class ReopeningData {

	private int id;
	private String date;
	private String retManager;
	private double refund;
	private double deposit;
	private String dateOfDeposit;
	private String dateOfDebit;

	public ReopeningData() {

	}

	public ReopeningData(String date, String retManager, double refund, double deposit, String dateOfDeposit, String dateOfDebit,
			double result) {
		super();
		this.date = date.toString();
		this.retManager = retManager.toString();
		this.refund = refund;
		this.deposit = deposit;
		this.dateOfDeposit = dateOfDeposit.toString();
		this.dateOfDebit = dateOfDebit.toString();

	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRetManager() {
		return retManager;
	}

	public void setRetManager(String retManager) {
		this.retManager = retManager;
	}

	public double getRefund() {
		return refund;
	}

	public void setRefund(double refund) {
		this.refund = refund;
	}

	public String getDateOfDeposit() {
		return dateOfDeposit;
	}

	public void setDateOfDeposit(String dateOfDeposit) {
		this.dateOfDeposit = dateOfDeposit;
	}

	public String getDateOfDebit() {
		return dateOfDebit;
	}

	public void setDateOfDebit(String dateOfDebit) {
		this.dateOfDebit = dateOfDebit;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

}
