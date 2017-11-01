package maxi.rmreporting.api;

public abstract class MonthAndYear {
	
	public static String currentMonth = "";
	public static int currentYear = 0;
	
	public static void setCurrentMonth(String currentMonth) {
		MonthAndYear.currentMonth = currentMonth;
		System.out.println(MonthAndYear.currentMonth);
	}
	public static void setCurrentYear(int currentYear) {
		MonthAndYear.currentYear = currentYear;
		System.out.println(MonthAndYear.currentYear);
	}
	

	
	
}
