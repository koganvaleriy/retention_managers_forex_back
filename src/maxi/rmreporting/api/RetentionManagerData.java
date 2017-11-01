package maxi.rmreporting.api;

public class RetentionManagerData {

	public String name;
	public String desk;
	public String team;
	public double target;

	public RetentionManagerData() {

	}

	public RetentionManagerData(String name, String desk, String team, double target) {
		super();
		this.name = name.toString();
		this.desk = desk.toString();
		this.team = team.toString();
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

	public void setName(String name) {
		this.name = name;
	}

	public void setDesk(String desk) {
		this.desk = desk;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "RetentionManagerData [retManager=" + name + ", desk=" + desk + ", team=" + team + ", target=" + target
				+ "]";
	}

}
