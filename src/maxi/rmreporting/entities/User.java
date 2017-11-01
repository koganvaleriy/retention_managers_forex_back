package maxi.rmreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import static maxi.rmreporting.api.DBTableNames.*;

@Entity
@Table(name = BALANCES)
public class User {

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
	private String newUser;

	@Column
	private int dealsNumber;

	@Column
	private double result;

	public User() {

	}

	public User(String account, double userID, RetManager retManager, double balance, String newUser, int dealsNumber) {
		super();
		this.account = account;
		this.userID = userID;
		this.retManager = retManager;
		this.balance = balance;
		this.newUser = newUser;
		this.dealsNumber = dealsNumber;
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

	public String getNewUser() {
		return newUser;
	}

	public int getDealsNumber() {
		return dealsNumber;
	}

	public double getResult() {
		return result;
	}

}
