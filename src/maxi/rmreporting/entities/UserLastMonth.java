package maxi.rmreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import static maxi.rmreporting.api.DBTableNames.*;

@Entity
@Table(name = BALANCES_LAST_MONTH)
public class UserLastMonth {

	@Id
	@Column
	private String account;

	@Column
	private double userID;

	@ManyToOne
	private RetManager retManager;

	@Column
	private double balance;

	@Column
	private double result;

	public UserLastMonth() {

	}

	public UserLastMonth(String account, double userID, RetManager retManager, double balance) {
		super();
		this.account = account;
		this.userID = userID;
		this.retManager = retManager;
		this.balance = balance;
	}

	public String getAccount() {
		return account;
	}

	public double getUserID() {
		return userID;
	}

	public RetManager getRetManager() {
		return retManager;
	}

	public double getBalance() {
		return balance;
	}

	public double getResult() {
		return result;
	}

}
