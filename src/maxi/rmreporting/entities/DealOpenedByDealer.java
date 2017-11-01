package maxi.rmreporting.entities;

import static maxi.rmreporting.api.DBTableNames.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = DEALS_OPENED_BY_DEALER)
public class DealOpenedByDealer {

	@Id
	@Column
	private String dealID;

	@ManyToOne
	private RetManager retManager;

	@Column
	private String account;
	
	@Column
	private double result;

	public DealOpenedByDealer() {

	}

	public DealOpenedByDealer(String dealID, RetManager retManager, String account) {
		super();
		this.dealID = dealID;
		this.retManager = retManager;
		this.account = account;
	}

	public String getDealID() {
		return dealID;
	}

	public RetManager getRetManager() {
		return retManager;
	}

	public String getAccount() {
		return account;
	}

	public double getResult() {
		return result;
	}
	
	

}
