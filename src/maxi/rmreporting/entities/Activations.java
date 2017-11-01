package maxi.rmreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import static maxi.rmreporting.api.DBTableNames.*;

@Entity
@Table(name = ACTIVATIONS_TABLE)
public class Activations {

	@Id
	@Column
	private double userID;

	//private Set<Deposit> deposits;

	public Activations() {

	}

	public Activations(double userID) {
		super();
		this.userID = userID;
	}

	public double getUserID() {
		return userID;
	}
/*
	public Set<Deposit> getDeposits() {
		return deposits;
	}
*/
}
