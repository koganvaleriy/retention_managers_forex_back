package maxi.rmreporting.api;

public class LockData {

	private int id;
	private String date;
	private String retManager;
	private double credit;
	private double deposit;
	private String dateOfDeposit;
	private String dateOfDebit;

	public LockData() {

	}

	public LockData(String date, String retManager, double credit, double deposit, String dateOfDeposit,
			String dateOfDebit) {
		super();
		this.date = date.toString();
		this.retManager = retManager.toString();
		this.credit = credit;
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

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
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

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "LockData [id=" + id + ", date=" + date + ", retManager=" + retManager + ", credit=" + credit
				+ ", deposit=" + deposit + ", dateOfDeposit=" + dateOfDeposit + ", dateOfDebit=" + dateOfDebit + "]";
	}

}
