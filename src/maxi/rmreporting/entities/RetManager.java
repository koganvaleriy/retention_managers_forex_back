package maxi.rmreporting.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import static maxi.rmreporting.api.DBTableNames.*;

@Entity
@Table(name = RETENTION_MANAGERS_TABLE)

public class RetManager {

	@Id
	@Column
	private String name;

	@Column
	private String desk;

	@Column
	private String team;
	
	@Column
	private double target;
	
	@OneToMany(mappedBy = "retManager")
	private Set<Deposit> deposits;
	
	public RetManager() {

	}

	public RetManager(String name, String desk, String team, double target) {
		super();
		this.name = name;
		this.desk = desk;
		this.team = team;
		this.target = target;
	}

	public String getName() {
		return name;
	}

	public String getDesk() {
		return desk;
	}

	public String getTeam() {
		return team;
	}

	public double getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "RetManagers [name=" + name + ", desk=" + desk + ", team=" + team + ", target=" + target + "]";
	}

}
