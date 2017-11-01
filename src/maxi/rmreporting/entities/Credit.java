package maxi.rmreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import static maxi.rmreporting.api.DBTableNames.*;

@Entity
@Table(name = CREDITS_TABLE)
public class Credit {

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
	private String transactionSubType;

	@Column
	private String businessGroup;

	@Column
	private double result;

	public Credit(double transactionID, double userID, RetManager retManager, double date, double amount,
			String account, String transactionSubType, String businessGroup) {
		super();
		this.transactionID = transactionID;
		this.userID = userID;
		this.retManager = retManager;
		this.date = date;
		this.amount = amount;
		this.account = account;
		this.transactionSubType = transactionSubType;
		this.businessGroup = businessGroup;
	}

	public Credit() {
	
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

	public String getTransactionSubType() {
		return transactionSubType;
	}

	public String getBusinessGroup() {
		return businessGroup;
	}

	public double getResult() {
		return result;
	}

}
