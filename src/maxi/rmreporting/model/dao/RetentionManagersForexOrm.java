package maxi.rmreporting.model.dao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.transaction.Transactional;

import maxi.rmreporting.api.ActivationsData;
import maxi.rmreporting.api.ClosedDealData;
import maxi.rmreporting.api.CreditData;
import maxi.rmreporting.api.DealOpenedByDealerData;
import maxi.rmreporting.api.DepositData;
import maxi.rmreporting.api.LockData;
import maxi.rmreporting.api.ReopeningData;
import maxi.rmreporting.api.RetentionManagerData;
import maxi.rmreporting.api.UserData;
import maxi.rmreporting.api.WithdrawalData;
import maxi.rmreporting.entities.Activations;
import maxi.rmreporting.entities.ClosedDeal;
import maxi.rmreporting.entities.Credit;
import maxi.rmreporting.entities.DealOpenedByDealer;
import maxi.rmreporting.entities.Deposit;
import maxi.rmreporting.entities.Lock;
import maxi.rmreporting.entities.Reopening;
import maxi.rmreporting.entities.RetManager;
import maxi.rmreporting.entities.User;
import maxi.rmreporting.entities.UserLastMonth;
import maxi.rmreporting.entities.Withdrawal;
import maxi.rmreporting.utilities.XSSFReadWrite;

import static maxi.rmreporting.api.DBTableNames.*;
import static maxi.rmreporting.api.russian.FileNamesAndColumnTitles.*;

public class RetentionManagersForexOrm {

	private final String UNKNOWN_RM = "UNKNOWN_RM";
	private final String UNKNOWN_RM_FOR_DB = "UNKNOWN_RM_FOR_DB";

	@PersistenceContext(unitName = "springHibernate", type = PersistenceContextType.EXTENDED)
	EntityManager em;

	public RetManager getRetManager(String name) {

		RetManager retManager = em.find(RetManager.class, name);

		if (retManager == null) {
			addRetManager(new RetentionManagerData(name, UNKNOWN_RM, UNKNOWN_RM, -1));
			retManager = em.find(RetManager.class, name);
			return retManager;
		} else {
			return retManager;
		}
	}
	
	public RetManager getRetManagerForDeposits(String name) {

		RetManager retManager = em.find(RetManager.class, name);

		if (retManager == null) {
			addRetManager(new RetentionManagerData(name, UNKNOWN_RM_FOR_DB, UNKNOWN_RM_FOR_DB, -1));
			retManager = em.find(RetManager.class, name);
			return retManager;
		} else {
			return retManager;
		}
	}

	@Transactional
	public boolean addClosedDeal(ClosedDealData closedDealData) {

		double closedDealDate = closedDealData.getClosedDate();
		double openDealDate = closedDealData.getOpendDate();

		ClosedDeal closedDeal = new ClosedDeal(closedDealData.getDealID(), closedDealData.getUserID(),
				getRetManager(closedDealData.getRetManager()), closedDealDate, openDealDate, closedDealData.getVolume(),
				closedDealData.getAccount(), isNewUser(closedDealData), closedDealData.getPnl(),
				getDealDuration(closedDealDate, openDealDate));

		if (em.find(ClosedDeal.class, closedDealData.getDealID()) != null) {
			em.merge(closedDeal);
			return true;
		} else {
			em.persist(closedDeal);
			return true;
		}
	}

	@Transactional
	public boolean addDealOpenedByDealer(DealOpenedByDealerData closedDealData) {

		DealOpenedByDealer closedDeal = new DealOpenedByDealer(
				closedDealData.getDealID(), getRetManager(closedDealData.getRetManager() == null
						? getRetManagerByDealID(closedDealData.getDealID()) : closedDealData.getRetManager()),
				closedDealData.getAccount());

		if (em.find(DealOpenedByDealer.class, closedDealData.getDealID()) != null) {
			em.merge(closedDeal);
			return true;
		} else {
			em.persist(closedDeal);
			return true;
		}
	}
	
	@Transactional
	public boolean addReopening(ReopeningData reopeningData) {

		Reopening reopening = new Reopening(reopeningData.getId(), getDate(reopeningData.getDate()), getRetManager(reopeningData.getRetManager()),
				reopeningData.getRefund(), reopeningData.getDeposit(), getDate(reopeningData.getDateOfDeposit()),
				getDate(reopeningData.getDateOfDebit()));

		if (em.find(Reopening.class, reopeningData.getId()) != null) {
			em.merge(reopening);
			return true;
		} else {
			em.persist(reopening);
			return true;
		}
	}

	@Transactional
	public boolean addLock(LockData lockData) {

		Lock lock = new Lock(lockData.getId(), getDate(lockData.getDate()), getRetManager(lockData.getRetManager()),
				lockData.getCredit(), lockData.getDeposit(), getDate(lockData.getDateOfDeposit()),
				getDate(lockData.getDateOfDebit()));

		if (em.find(Lock.class, lockData.getId()) != null) {
			em.merge(lock);
			return true;
		} else {
			em.persist(lock);
			return true;
		}
	}
	@Transactional
	private Date getDate(String date) {

		try {
			long dateInMillisec = (Long.parseLong(date) - 25569) * 86400000;
			Date res = new Date(dateInMillisec);
			return res;
		} catch (Exception e) {

			SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
			String[] dateSplitted = date.split("(-|/|\\.)| ");
			Date res;
			try {
				if (dateSplitted[0].length() < 3 && Integer.parseInt(dateSplitted[0]) <= 12 && dateSplitted[1].length() < 3 && dateSplitted[2].length() < 5) {
					res = format.parse(dateSplitted[0] + "-" + dateSplitted[1] + "-" + dateSplitted[2]);
					return res;
				} else {
					return new Date(0);
				}
			} catch (Exception e1) {
				return new Date(0);
			}

		}

	}

	private String getRetManagerByDealID(String dealID) {
		ClosedDeal closedDeal = em.find(ClosedDeal.class, dealID);

		if (closedDeal == null) {
			return "unknown";
		} else {
			return closedDeal.getRetManager().getName();
		}
	}

	@Transactional
	protected boolean addUser(UserData userData, Map<Double, Integer> dealsNumberMap) {
		User user = new User(userData.getAccount(), userData.getUserID(), getRetManager(userData.getRetManager()),
				userData.getBalance(), isNewUser(userData),
				dealsNumberMap.containsKey(userData.getUserID()) ? dealsNumberMap.get(userData.getUserID()) : 0);

		if (em.find(User.class, userData.getAccount()) != null) {
			em.merge(user);
			return true;
		} else {
			em.persist(user);
			return true;
		}

	}

	@Transactional
	protected boolean addUserLastMonth(UserData userData) {
		UserLastMonth user = new UserLastMonth(userData.getAccount(), userData.getUserID(),
				getRetManager(userData.getRetManager()), userData.getBalance());

		if (em.find(User.class, userData.getAccount()) != null) {
			em.merge(user);
			return true;
		} else {
			em.persist(user);
			return true;
		}

	}

	@SuppressWarnings("unchecked")
	public Map<Double, Integer> getDealsNumber() {

		em.clear();
		Map<Double, Integer> mapRes = new HashMap<>();
		String queryText = "select userID, " + "count(dealID) as result, "
				+ "dealID, account, closedDate, dealDuration, newUser, opendDate, pnl, volume, retManager_name "
				+ "from closed_deals " + "group by userID";

		Query query = em.createNativeQuery(queryText, ClosedDeal.class);
		List<ClosedDeal> userList = (List<ClosedDeal>) query.getResultList();

		for (ClosedDeal user : userList) {
			mapRes.put(user.getUserID(), (int) user.getResult());
		}

		// em.clear();
		return mapRes;
	}

	private double getDealDuration(double closedDealDate, double openDealDate) {
		return (closedDealDate - openDealDate) * 86400000;
	}

	@Transactional
	public boolean addDeposit(DepositData depositData) {

		if (em.find(Deposit.class, depositData.getTransactionID()) != null) {
			em.merge(new Deposit(depositData.getTransactionID(), depositData.getUserID(),
					getRetManagerForDeposits(depositData.getRetManager()), depositData.getDate(), depositData.getAmount(),
					depositData.getAccount(), depositData.getBusinessGroup(), isNewUser(depositData)));
			return true;
		} else {
			em.persist(new Deposit(depositData.getTransactionID(), depositData.getUserID(),
					getRetManagerForDeposits(depositData.getRetManager()), depositData.getDate(), depositData.getAmount(),
					depositData.getAccount(), depositData.getBusinessGroup(), isNewUser(depositData)));
			return true;
		}
	}

	@Transactional
	public boolean addCredit(CreditData creditData) {

		if (em.find(Credit.class, creditData.getTransactionID()) != null) {
			em.merge(new Credit(creditData.getTransactionID(), creditData.getUserID(),
					getRetManager(creditData.getRetManager()), creditData.getDate(), creditData.getAmount(),
					creditData.getAccount(), creditData.getTransactionSubType(), creditData.getBusinessGroup()));
			return true;
		} else {
			em.persist(new Credit(creditData.getTransactionID(), creditData.getUserID(),
					getRetManager(creditData.getRetManager()), creditData.getDate(), creditData.getAmount(),
					creditData.getAccount(), creditData.getTransactionSubType(), creditData.getBusinessGroup()));
			return true;
		}
	}

	@Transactional
	public boolean addWithdrawal(WithdrawalData withdrawalData) {

		if (em.find(Withdrawal.class, withdrawalData.getTransactionID()) != null) {
			em.merge(new Withdrawal(withdrawalData.getTransactionID(), withdrawalData.getUserID(),
					getRetManager(withdrawalData.getRetManager()), withdrawalData.getDate(), withdrawalData.getAmount(),
					withdrawalData.getAccount(), withdrawalData.getBusinessGroup(), isNewUser(withdrawalData)));
			return true;
		} else {
			em.persist(new Withdrawal(withdrawalData.getTransactionID(), withdrawalData.getUserID(),
					getRetManager(withdrawalData.getRetManager()), withdrawalData.getDate(), withdrawalData.getAmount(),
					withdrawalData.getAccount(), withdrawalData.getBusinessGroup(), isNewUser(withdrawalData)));
			return true;
		}
	}

	private String isNewUser(UserData userData) {

		Activations activationsRussian = em.find(Activations.class, userData.getUserID());

		if (activationsRussian != null) {
			return "true";

		} else {
			return "false";
		}

	}

	@Transactional
	private String isNewUser(DepositData depositData) {

		Activations activationsRussian = em.find(Activations.class, depositData.getUserID());

		if (activationsRussian != null) {
			return "true";
		} else {
			return "false";
		}
	}

	@Transactional
	private String isNewUser(WithdrawalData withdrawalData) {

		Activations activationsRussian = em.find(Activations.class, withdrawalData.getUserID());

		if (activationsRussian != null) {
			return "true";
		} else {
			return "false";
		}
	}

	@Transactional
	private String isNewUser(ClosedDealData closedDealData) {

		Activations activationsRussian = em.find(Activations.class, closedDealData.getUserID());

		if (activationsRussian != null) {
			return "true";
		} else {
			return "false";
		}
	}

	@Transactional
	public boolean addRetManager(RetentionManagerData retManagerData) {

		if (em.find(RetManager.class, retManagerData.getName()) != null) {
			em.merge(new RetManager(retManagerData.getName(), retManagerData.getDesk(), retManagerData.getTeam(),
					retManagerData.getTarget()));
			return true;
		} else {
			em.persist(new RetManager(retManagerData.getName(), retManagerData.getDesk(), retManagerData.getTeam(),
					retManagerData.getTarget()));
			return true;
		}
	}

	@Transactional
	public boolean fillActivationsDBFromExsel(String filename) {

		if (truncateTable(ACTIVATIONS_TABLE) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<ActivationsData> activationsRussianDataList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						activationsRussianDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						ActivationsData activationsRussianData = new ActivationsData();
						activationsRussianDataList.add(i, activationsRussianData);
					}

					String cell = (String) column.get(i);

					if (cell.equals("BLANK")) {
						cell = "0";
					}

					ActivationsData activationsRussianData = activationsRussianDataList.get(i);

					if (title.equals(USER_ID_ACTIVATIONS)) {
						activationsRussianData.setUserID(Double.parseDouble(cell));
					}
				}
			}

			for (ActivationsData activationsRussianData : activationsRussianDataList) {
				addActivationRussian(activationsRussianData);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional
	protected boolean addActivationRussian(ActivationsData activationsRussianData) {

		if (em.find(Activations.class, activationsRussianData.getUserID()) != null) {
			em.merge(new Activations(activationsRussianData.getUserID()));
			return true;
		} else {
			em.persist(new Activations(activationsRussianData.getUserID()));
			return true;
		}

	}

	@Transactional
	public boolean fillDepositsDBFromExsel(String filename) {

		if (truncateTable(DEPOSITS_NAME_TABLE) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<DepositData> depositDataList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						depositDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						DepositData depositData = new DepositData();
						depositDataList.add(i, depositData);
					}

					String cell = (String) column.get(i);

					if (cell.equals("BLANK")) {
						cell = "0";
					}
					DepositData depositData = depositDataList.get(i);

					if (title.equals(TRANSACTION_ID)) {
						depositData.setTransactionID(Double.parseDouble(cell));
					} else if (title.equals(CLIENT_ID)) {
						depositData.setUserID(Double.parseDouble(cell));
					} else if (title.equals(RETENTION_MANAGER)) {
						depositData.setRetManager(cell);
					} else if (title.equals(DATE)) {
						depositData.setDate(Double.parseDouble(cell));
					} else if (title.equals(AMOUNT)) {
						depositData.setAmount(Double.parseDouble(cell));
					} else if (title.equals(ACCOUNT)) {
						depositData.setAccount(cell);
					} else if (title.equals(BUSINESS_GROUP)) {
						depositData.setBusinessGroup(cell);
					}
				}
			}

			for (DepositData depositData : depositDataList) {
				addDeposit(depositData);

			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional
	public boolean fillClosedDealsDBFromExsel(String filename) {

		if (truncateTable(CLOSED_DEALS_TABLE) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<ClosedDealData> closedDealsDataList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);
				int columnSize = column.size();

				for (int i = 0; i < columnSize; i++) {

					try {
						closedDealsDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						ClosedDealData closedDealsData = new ClosedDealData();
						closedDealsDataList.add(i, closedDealsData);
					}

					String cell = (String) column.get(i);
					if (cell.equals("BLANK")) {
						cell = "0";
					}

					ClosedDealData closedDealsData = closedDealsDataList.get(i);

					if (title.equals(DEAL_ID)) {
						closedDealsData.setDealID(cell);
					} else if (title.equals(USER_ID_DEALS)) {
						closedDealsData.setUserID(Double.parseDouble(cell));
					} else if (title.equals(RETENTION_MANAGER_DEALS)) {
						closedDealsData.setRetManager(cell);
					} else if (title.equals(CLOSED_DATE)) {
						closedDealsData.setClosedDate(Double.parseDouble(cell));
					} else if (title.equals(VOLUME)) {
						closedDealsData.setVolume(Double.parseDouble(cell));
					} else if (title.equals(ACCOUNT_DEALS)) {
						closedDealsData.setAccount(cell);
					} else if (title.equals(OPEN_DATE)) {
						closedDealsData.setOpendDate(Double.parseDouble(cell));
					} else if (title.equals(PNL)) {
						closedDealsData.setPnl(Double.parseDouble(cell));
					}

				}
			}

			for (ClosedDealData closedDealsData : closedDealsDataList) {
				addClosedDeal(closedDealsData);

			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	protected boolean truncateTable(String dbTableName) {

		Query query = em.createNativeQuery("TRUNCATE TABLE " + dbTableName);
		query.executeUpdate();
		return true;
	}

	@Transactional
	public boolean fillCreditsDBFromExsel(String filename) {

		if (truncateTable(CREDITS_TABLE) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<CreditData> creditsDataList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						creditsDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						CreditData creditData = new CreditData();
						creditsDataList.add(i, creditData);
					}

					String cell = (String) column.get(i);
					if (cell.equals("BLANK")) {
						cell = "0";
					}
					CreditData creditData = creditsDataList.get(i);

					if (title.equals(TRANSACTION_ID)) {
						creditData.setTransactionID(Double.parseDouble(cell));
					} else if (title.equals(CLIENT_ID)) {
						creditData.setUserID(Double.parseDouble(cell));
					} else if (title.equals(RETENTION_MANAGER)) {
						creditData.setRetManager(cell);
					} else if (title.equals(DATE)) {
						creditData.setDate(Double.parseDouble(cell));
					} else if (title.equals(AMOUNT)) {
						creditData.setAmount(Double.parseDouble(cell));
					} else if (title.equals(ACCOUNT)) {
						creditData.setAccount(cell);
					} else if (title.equals(BUSINESS_GROUP)) {
						creditData.setBusinessGroup(cell);
					} else if (title.equals(TRANSACTION_SUB_TYPE)) {
						creditData.setTransactionSubType(cell);
					}
				}
			}

			for (CreditData creditData : creditsDataList) {
				addCredit(creditData);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional
	public boolean fillWithdrawalsDBFromExsel(String filename) {

		if (truncateTable(WITHDRAWALS_TABLE) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<WithdrawalData> withdrawalDataList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						withdrawalDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						WithdrawalData withdrawalData = new WithdrawalData();
						withdrawalDataList.add(i, withdrawalData);
					}

					String cell = (String) column.get(i);
					if (cell.equals("BLANK")) {
						cell = "0";
					}
					WithdrawalData withdrawalData = withdrawalDataList.get(i);

					if (title.equals(TRANSACTION_ID)) {
						withdrawalData.setTransactionID(Double.parseDouble(cell));
					} else if (title.equals(CLIENT_ID)) {
						withdrawalData.setUserID(Double.parseDouble(cell));
					} else if (title.equals(RETENTION_MANAGER)) {
						withdrawalData.setRetManager(cell);
					} else if (title.equals(DATE)) {
						withdrawalData.setDate(Double.parseDouble(cell));
					} else if (title.equals(AMOUNT)) {
						withdrawalData.setAmount(Double.parseDouble(cell));
					} else if (title.equals(ACCOUNT)) {
						withdrawalData.setAccount(cell);
					} else if (title.equals(BUSINESS_GROUP)) {
						withdrawalData.setBusinessGroup(cell);
					}
				}
			}

			for (WithdrawalData withdrawalData : withdrawalDataList) {
				addWithdrawal(withdrawalData);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional
	public boolean fillBalancesDBFromExsel(String filename) {

		if (truncateTable(BALANCES) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<UserData> userDataList = new ArrayList<>();
		Map<Double, Integer> dealsNumberMap = getDealsNumber();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						userDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						UserData userData = new UserData();
						userDataList.add(i, userData);
					}

					String cell = (String) column.get(i);
					if (cell.equals("BLANK")) {
						cell = "0";
					}
					UserData userData = userDataList.get(i);

					if (title.equals(BALANCE)) {
						userData.setBalance(Double.parseDouble(cell));
					} else if (title.equals(CLIENT_ID_BALANCES)) {
						userData.setUserID(Double.parseDouble(cell));
					} else if (title.equals(RETENTION_MANAGER_BALANCES)) {
						userData.setRetManager(cell);
					} else if (title.equals(ACCOUNT_BALANCES)) {
						userData.setAccount(cell);
					}
				}
			}

			for (UserData userData : userDataList) {
				addUser(userData, dealsNumberMap);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional
	public boolean fillBalancesLastMonthDBFromExsel(String filename) {

		if (truncateTable(BALANCES_LAST_MONTH) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<UserData> userDataList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						userDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						UserData userData = new UserData();
						userDataList.add(i, userData);
					}

					String cell = (String) column.get(i);
					if (cell.equals("BLANK")) {
						cell = "0";
					}
					UserData userData = userDataList.get(i);

					if (title.equals(BALANCE)) {
						userData.setBalance(Double.parseDouble(cell));
					} else if (title.equals(CLIENT_ID_BALANCES)) {
						userData.setUserID(Double.parseDouble(cell));
					} else if (title.equals(RETENTION_MANAGER_BALANCES)) {
						userData.setRetManager(cell);
					} else if (title.equals(ACCOUNT_BALANCES)) {
						userData.setAccount(cell);
					}
				}
			}

			for (UserData userData : userDataList) {
				addUserLastMonth(userData);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional
	public boolean fillRetManagersDBFromExsel(String filename) {
		/*
		 * if (truncateRetManagersTable() == false) { System.out.println(
		 * "Unsuccesfull table truncating"); }
		 */
		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<RetentionManagerData> retentionManagerDataList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						retentionManagerDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						RetentionManagerData retManData = new RetentionManagerData();
						retentionManagerDataList.add(i, retManData);
					}

					String cell = (String) column.get(i);

					if (cell.equals("BLANK") || cell.equals("null")) {
						cell = "0";
					}
					RetentionManagerData retManData = retentionManagerDataList.get(i);

					if (title.equalsIgnoreCase(NAME)) {
						retManData.setName(cell);
					} else if (title.equalsIgnoreCase(DESK)) {
						retManData.setDesk(cell);
					} else if (title.equalsIgnoreCase(TEAM)) {
						retManData.setTeam(cell);
					} else if (title.equalsIgnoreCase(TARGET)) {
						retManData.setTarget(Double.parseDouble(cell));
					}
				}
			}

			for (RetentionManagerData retManData : retentionManagerDataList) {
				addRetManager(retManData);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional
	public boolean fillDealsOpenedByDealerDBFromExsel(String filename) {

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<DealOpenedByDealerData> dealsOpenedByDealerDataList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						dealsOpenedByDealerDataList.get(i);
					} catch (IndexOutOfBoundsException e) {
						DealOpenedByDealerData dealOpenedByDealerData = new DealOpenedByDealerData();
						dealsOpenedByDealerDataList.add(i, dealOpenedByDealerData);
					}

					String cell = (String) column.get(i);

					if (cell.equals("BLANK")) {
						cell = "0";
					}
					DealOpenedByDealerData dealOpenedByDealerData = dealsOpenedByDealerDataList.get(i);

					if (title.equalsIgnoreCase(RETENTION_MANAGER_DEALS)) {
						dealOpenedByDealerData.setRetManager(cell);
					} else if (title.equalsIgnoreCase(ACCOUNT_DEALS)) {
						dealOpenedByDealerData.setAccount(cell);
					} else if (title.equalsIgnoreCase(ACCOUNT_MT4_OPENED_BY_DEALER)) {
						dealOpenedByDealerData.setAccount(cell);
					} else if (title.equalsIgnoreCase(DEALID_MT4_OPENED_BY_DEALER)) {
						dealOpenedByDealerData.setDealID(cell);
					} else if (title.equalsIgnoreCase(DEAL_ID)) {
						dealOpenedByDealerData.setDealID(cell);
					}
				}
			}

			for (DealOpenedByDealerData dealOpenedByDealer : dealsOpenedByDealerDataList) {
				addDealOpenedByDealer(dealOpenedByDealer);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional
	public boolean fillLocksDBFromExsel(String filename) throws IOException {

		if (truncateTable(LOCKS) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<LockData> locksList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						locksList.get(i);
					} catch (IndexOutOfBoundsException e) {
						LockData lockData = new LockData();
						lockData.setId(i);
						locksList.add(i, lockData);
					}

					String cell = (String) column.get(i);

					if (cell.equals("BLANK")) {
						cell = "0";
					}
					LockData lockData = locksList.get(i);

					if (title.equalsIgnoreCase(DATE_LOCKS)) {
						lockData.setDate(cell);
					} else if (title.equalsIgnoreCase(RET_MANAGER_LOCKS)) {
						lockData.setRetManager(cell);
					} else if (title.equalsIgnoreCase(CREDIT_LOCKS)) {
						lockData.setCredit(Double.parseDouble(cell));
					} else if (title.equalsIgnoreCase(DEPOSIT_LOCKS)) {
						lockData.setDeposit(Double.parseDouble(cell));
					} else if (title.equalsIgnoreCase(DATE_OF_DEPOSIT_LOCKS)) {
						lockData.setDateOfDeposit(cell);
					} else if (title.equalsIgnoreCase(DATE_OF_DEBIT_LOCKS)) {
						lockData.setDateOfDebit(cell);
					}
				}
			}

			for (LockData lockData : locksList) {
				addLock(lockData);
			}
			
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}
		
	}

	@Transactional
	public boolean fillReopeningsDBFromExsel(String filename) throws IOException {

		if (truncateTable(REOPENINGS) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		XSSFReadWrite file1 = new XSSFReadWrite();
		Map<String, List<Object>> excelFileMap = file1.parseFile(filename);
		List<ReopeningData> reopeningsList = new ArrayList<>();

		try {
			for (String title : excelFileMap.keySet()) {

				List<Object> column = excelFileMap.get(title);

				for (int i = 0; i < column.size(); i++) {

					try {
						reopeningsList.get(i);
					} catch (IndexOutOfBoundsException e) {
						ReopeningData reopeningData = new ReopeningData();
						reopeningData.setId(i);
						reopeningsList.add(i, reopeningData);
					}

					String cell = (String) column.get(i);

					if (cell.equals("BLANK")) {
						cell = "0";
					}
					ReopeningData reopeningData = reopeningsList.get(i);

					if (title.equalsIgnoreCase(DATE_REOPENINGS)) {
						reopeningData.setDate(cell);
					} else if (title.equalsIgnoreCase(RET_MANAGER_REOPENINGS)) {
						reopeningData.setRetManager(cell);
					} else if (title.equalsIgnoreCase(REFUND_FOR_REOPENING)) {
						reopeningData.setRefund(Double.parseDouble(cell));
					} else if (title.equalsIgnoreCase(DEPOSIT_REOPENING)) {
						reopeningData.setDeposit(Double.parseDouble(cell));
					} else if (title.equalsIgnoreCase(DATE_OF_DEPOSIT_REOPENING)) {
						reopeningData.setDateOfDeposit(cell);
					} else if (title.equalsIgnoreCase(DATE_OF_DEBIT_REOPENING)) {
						reopeningData.setDateOfDebit(cell);
					}
				}
			}

			for (ReopeningData reopeningData : reopeningsList) {
				addReopening(reopeningData);
			}
			
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}
		
	}
	
	@Transactional
	public boolean fillDealsOpenedByDealerDBFromExsel(String filename1, String filename2) {

		if (truncateTable(DEALS_OPENED_BY_DEALER) == false) {
			System.out.println("Unsuccesfull table truncating");
		}

		fillDealsOpenedByDealerDBFromExsel(filename1);
		fillDealsOpenedByDealerDBFromExsel(filename2);
		return true;
	}

	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyDataByRetManagers(String queryText) {

		Map<RetManager, Object> depositsByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, Deposit.class);

		List<Deposit> deposits = (List<Deposit>) query.getResultList();

		for (Deposit deposit : deposits) {
			RetManager retManager = deposit.getRetManager();
			Double result = deposit.getResult();
			depositsByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return depositsByRetManagersMap;
	}

	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyCreditsDataByRetManagers(String queryText) {

		Map<RetManager, Object> creditsByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, Credit.class);

		List<Credit> credits = (List<Credit>) query.getResultList();

		for (Credit credit : credits) {
			RetManager retManager = credit.getRetManager();
			Double result = credit.getResult();
			creditsByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return creditsByRetManagersMap;
	}

	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyDataWithdrawalByRetManagers(String queryText) {

		Map<RetManager, Object> withdrawsByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, Withdrawal.class);

		List<Withdrawal> withdraws = (List<Withdrawal>) query.getResultList();

		for (Withdrawal deposit : withdraws) {
			RetManager retManager = deposit.getRetManager();
			Double result = deposit.getResult();
			withdrawsByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return withdrawsByRetManagersMap;
	}

	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyDealsDataByRetManagers(String queryText) {

		Map<RetManager, Object> closedDealsByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, ClosedDeal.class);

		List<ClosedDeal> closedDeals = (List<ClosedDeal>) query.getResultList();

		for (ClosedDeal closedDeal : closedDeals) {
			RetManager retManager = closedDeal.getRetManager();
			Double result = closedDeal.getResult();
			closedDealsByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return closedDealsByRetManagersMap;
	}

	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyBalancesDataByRetManagers(String queryText) {

		Map<RetManager, Object> closedDealsByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, User.class);

		List<User> users = (List<User>) query.getResultList();

		for (User user : users) {
			RetManager retManager = user.getRetManager();
			Double result = user.getResult();
			closedDealsByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return closedDealsByRetManagersMap;
	}

	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyBalancesLastMonthDataByRetManagers(String queryText) {

		Map<RetManager, Object> closedDealsByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, UserLastMonth.class);

		List<UserLastMonth> users = (List<UserLastMonth>) query.getResultList();

		for (UserLastMonth user : users) {
			RetManager retManager = user.getRetManager();
			Double result = user.getResult();
			closedDealsByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return closedDealsByRetManagersMap;
	}

	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyDealsByDealerDataByRetManagers(String queryText) {

		Map<RetManager, Object> closedDealsByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, DealOpenedByDealer.class);

		List<DealOpenedByDealer> closedDeals = (List<DealOpenedByDealer>) query.getResultList();

		for (DealOpenedByDealer closedDeal : closedDeals) {
			RetManager retManager = closedDeal.getRetManager();
			Double result = closedDeal.getResult();
			closedDealsByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return closedDealsByRetManagersMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyLockByRetManagers(String queryText) {
		em.clear();
		Map<RetManager, Object> locksByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, Lock.class);

		List<Lock> locks = (List<Lock>) query.getResultList();

		for (Lock lock : locks) {
			RetManager retManager = lock.getRetManager();
			Double result = lock.getResult();
			locksByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return locksByRetManagersMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<RetManager, Object> getAnyReopeningByRetManagers(String queryText) {
		em.clear();
		Map<RetManager, Object> reopeningdByRetManagersMap = new HashMap<>();
		Query query = em.createNativeQuery(queryText, Reopening.class);

		List<Reopening> reopenings = (List<Reopening>) query.getResultList();

		for (Reopening reopening : reopenings) {
			RetManager retManager = reopening.getRetManager();
			Double result = reopening.getResult();
			reopeningdByRetManagersMap.put(retManager, result);

		}
		em.clear();
		return reopeningdByRetManagersMap;
	}
	
	protected boolean dropTable(String dbTableName) {

		Query query = em.createNativeQuery("DROP TABLE " + dbTableName);
		query.executeUpdate();
		return true;
	}
	
	@Transactional
	public boolean truncateAllTables() {
		
		try {
			dropTable(ACTIVATIONS_TABLE);
			dropTable(BALANCES);
			dropTable(BALANCES_LAST_MONTH);
			dropTable(CLOSED_DEALS_TABLE);
			dropTable(CREDITS_TABLE);
			dropTable(DEALS_OPENED_BY_DEALER);
			dropTable(DEPOSITS_NAME_TABLE);
			dropTable(LOCKS);
			dropTable(REOPENINGS);
			dropTable(WITHDRAWALS_TABLE);
			dropTable(RETENTION_MANAGERS_TABLE);
			createAllTables();
			em.clear();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			em.clear();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String findUnknownRetentionManager () {
		
		StringBuilder res = new StringBuilder();
		String queryText = "select * from retention_managers "
				+ "where desk = '"+ UNKNOWN_RM_FOR_DB +"';";
		
		Query query = em.createNativeQuery(queryText, RetManager.class);

		List<RetManager> retManagers = (List<RetManager>) query.getResultList();

		for (RetManager retManager : retManagers) {
			res.append("\n");
			res.append(retManager.getName());
		}
		em.clear();
		return res.toString();
	}
	
	@Transactional
	public boolean createAllTables() {
		
		String queryText = "CREATE TABLE "+ ACTIVATIONS_TABLE + 
		      " (userID double not null PRIMARY KEY)";
		Query query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ RETENTION_MANAGERS_TABLE + 
			      " (name varchar(255) not null PRIMARY KEY, " +    
			      "desk varchar(255), " +
			      "target double, " +
			      "team varchar(255) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ BALANCES + 
			      " (account varchar(255) not null PRIMARY KEY, " +
			      "balance double, " +
			      "dealsNumber int(11), " +
			      "newUser varchar(255), " +
			      "result double, " +
			      "userID double, " +
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ BALANCES_LAST_MONTH + 
			      " (account varchar(255) not null PRIMARY KEY, " +
			      "balance double, " +
			      "dealsNumber int(11), " +
			      "newUser varchar(255), " +
			      "result double, " +
			      "userID double, " +
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ CREDITS_TABLE + 
			      " (transactionID double not null PRIMARY KEY, " +
			      "account varchar(255), " +
			      "amount double, " +
			      "businessGroup varchar(255), " +
			      "date double, " +
			      "result double, " +
			      "transactionSubType varchar(255), " +
			      "userID double, " +
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ DEPOSITS_NAME_TABLE + 
			      " (transactionID double not null PRIMARY KEY, " +
			      "account varchar(255), " +
			      "amount double, " +
			      "businessGroup varchar(255), " +
			      "date double, " +
			      "result double, " +
			      "userID double, " +
			      "newUser varchar(255), " +
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ WITHDRAWALS_TABLE + 
			      " (transactionID double not null PRIMARY KEY, " +
			      "account varchar(255), " +
			      "amount double, " +
			      "businessGroup varchar(255), " +
			      "date double, " +
			      "result double, " +
			      "userID double, " +
			      "newUser varchar(255), " +
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ DEALS_OPENED_BY_DEALER + 
			      " (dealID varchar(255) not null PRIMARY KEY, " +
			      "account varchar(255), " +
			      "result double, " +
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ LOCKS + 
			      " (id int(11) not null PRIMARY KEY, " +
			      "credit double, " +			
			      "date datetime, " +	
			      "dateOfDebit datetime, " +			
			      "dateOfDeposit datetime, " +	
			      "deposit double, " +			      
			      "result double, " +
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ REOPENINGS + 
			      " (id int(11) not null PRIMARY KEY, " +
			      "refund double, " +			
			      "date datetime, " +	
			      "dateOfDebit datetime, " +			
			      "dateOfDeposit datetime, " +	
			      "deposit double, " +			      
			      "result double, " +
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		queryText = "CREATE TABLE "+ CLOSED_DEALS_TABLE + 
			      " (dealID varchar(255) not null PRIMARY KEY, " +    
			      "account varchar(255), " +
			      "closedDate double, " +
			      "dealDuration double, " +
			      "newUser varchar(255), " +
			      "opendDate double, " +
			      "pnl double, " +
			      "result double, " +			      
			      "userID double, " +		
			      "volume double, " +	
			      "retManager_name varchar(255), " +
			      "FOREIGN KEY(retManager_name) REFERENCES retention_managers(name) " +
			      ")";
		query = em.createNativeQuery(queryText);
		query.executeUpdate();
		
		return true;
		
	}

}
