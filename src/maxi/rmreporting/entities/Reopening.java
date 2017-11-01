package maxi.rmreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import maxi.rmreporting.entities.RetManager;

import static maxi.rmreporting.api.DBTableNames.*;

import java.util.Date;

@Entity
@Table(name = REOPENINGS)
public class Reopening {

	@Id
	@Column
	private int id;

	@Column
	private Date date;

	@ManyToOne
	private RetManager retManager;

	@Column
	private double refund;
	
	@Column
	private double deposit;

	@Column
	private Date dateOfDeposit;

	@Column
	private Date dateOfDebit;

	@Column
	private double result;

	public Reopening() {

	}

	public Reopening(int id, Date date, RetManager retManager, double refund, double deposit, Date dateOfDeposit, Date dateOfDebit) {
		super();
		this.id = id;
		this.date = date;
		this.retManager = retManager;
		this.refund = refund;
		this.deposit = deposit;
		this.dateOfDeposit = dateOfDeposit;
		this.dateOfDebit = dateOfDebit;
	}

	public Date getDate() {
		return date;
	}

	public RetManager getRetManager() {
		return retManager;
	}

	public double getRefund() {
		return refund;
	}

	public Date getDateOfDeposit() {
		return dateOfDeposit;
	}

	public Date getDateOfDebit() {
		return dateOfDebit;
	}

	public double getResult() {
		return result;
	}

}
