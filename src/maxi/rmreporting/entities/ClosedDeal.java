package maxi.rmreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import static maxi.rmreporting.api.DBTableNames.*;

@Entity
@Table(name = CLOSED_DEALS_TABLE)
public class ClosedDeal {

	@Id
	@Column
	private String dealID;

	@Column
	private double userID;

	@ManyToOne
	private RetManager retManager;

	@Column
	private double closedDate;
	
	@Column
	private double opendDate;

	@Column
	private double volume;

	@Column
	private String account;

	@Column
	private String newUser;

	@Column
	private double pnl;

	@Column
	private double result;

	@Column
	private double dealDuration;

	public ClosedDeal(String dealID, double userID, RetManager retManager, double closedDate, double opendDate,
			double volume, String account, String newUser, double pnl, double dealDuration) {
		super();
		this.dealID = dealID;
		this.userID = userID;
		this.retManager = retManager;
		this.closedDate = closedDate;
		this.opendDate = opendDate;
		this.volume = volume;
		this.account = account;
		this.newUser = newUser;
		this.pnl = pnl;
		this.dealDuration = dealDuration;
	}

	public ClosedDeal() {

	}

	public String getDealID() {
		return dealID;
	}

	public double getUserID() {
		return userID;
	}

	public RetManager getRetManager() {
		return retManager;
	}

	public double getClosedDate() {
		return closedDate;
	}

	public double getVolume() {
		return volume;
	}

	public String getAccount() {
		return account;
	}

	public String getNewUser() {
		return newUser;
	}

	public double getPnl() {
		return pnl;
	}

	public double getResult() {
		return result;
	}

	public double getDealDuration() {
		return dealDuration;
	}

	public double getOpendDate() {
		return opendDate;
	}
	
	

}
