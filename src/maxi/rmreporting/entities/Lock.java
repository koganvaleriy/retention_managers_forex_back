package maxi.rmreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import static maxi.rmreporting.api.DBTableNames.*;

import java.util.Date;

@Entity
@Table(name = LOCKS)
public class Lock {

	@Id
	@Column
	private int id;

	@Column
	private Date date;

	@ManyToOne
	private RetManager retManager;

	@Column
	private double credit;

	@Column
	private double deposit;

	@Column
	private Date dateOfDeposit;

	@Column
	private Date dateOfDebit;

	@Column
	private double result;

	public Lock() {

	}

	public Lock(int id, Date date, RetManager retManager, double credit, double deposit, Date dateOfDeposit,
			Date dateOfDebit) {
		super();
		this.id = id;
		this.date = date;
		this.retManager = retManager;
		this.credit = credit;
		this.dateOfDeposit = dateOfDeposit;
		this.dateOfDebit = dateOfDebit;
		this.deposit = deposit;
	}

	public double getDeposit() {
		return deposit;
	}

	public Date getDate() {
		return date;
	}

	public RetManager getRetManager() {
		return retManager;
	}

	public double getCredit() {
		return credit;
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

	public int getId() {
		return id;
	}

}
