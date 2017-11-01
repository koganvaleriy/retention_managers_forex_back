package maxi.rmreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import static maxi.rmreporting.api.DBTableNames.*;

@Entity
@Table(name = WITHDRAWALS_TABLE)
public class Withdrawal {

	@Id
	@Column
	private double transactionID;

	@Column
	private double userID;

	@ManyToOne
	private RetManager retManager;
	
	@Column
	private double date;

	@Column
	private double amount;

	@Column
	private String account;

	@Column
	private String newUser;
	
	@Column
	private String BusinessGroup;
	
	@Column
	private double result;

	public Withdrawal() {

	}

	public Withdrawal(double transactionID, double userID, RetManager retManager, double date, double amount, String account,
			String businessGroup, String newUser) {
		super();
		this.transactionID = transactionID;
		this.userID = userID;
		this.retManager = retManager;
		this.date = date;
		this.amount = amount;
		this.account = account;
		BusinessGroup = businessGroup;
		this.newUser = newUser;
	}

	public double getTransactionID() {
		return transactionID;
	}

	public double getUserID() {
		return userID;
	}

	public RetManager getRetManager() {
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
	
	
	public double getResult() {
		return result;
	}
	
	public String isNewUser() {
		return newUser;
	}

	@Override
	public String toString() {
		return "Withdrawal [transactionID=" + transactionID + ", UserID=" + userID + ", RetManager=" + retManager
				+ ", date=" + date + ", amount=" + amount + ", account=" + account + ", BusinessGroup=" + BusinessGroup
				+ ", newUser=" + newUser + "]";
	}

}
