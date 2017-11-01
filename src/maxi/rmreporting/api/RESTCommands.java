package maxi.rmreporting.api;

public interface RESTCommands {
	
	//Set Month and Year
	String SET_MONTH = "set/month";
	String SET_YEAR = "set/year";

	// Totals
	String CREATE_REP_ALL = "all/report";
	String FILL_DATABASES = "databases/fill";

	// Reports Creation per Desk
	String CREATE_REP_RU = "russian/report";
	String CREATE_REP_EN = "english/report";
	String CREATE_REP_SP = "spanish/report";
	String CREATE_REP_AR = "arabic/report";
	
	//Clear DB
	String CLEAR_DATABASES = "databases/clear";
	
	//Storage
	String UPLOAD_FILE = "upload/file";
	String UPLOAD_FILES = "upload/files";
	String DELETE_FILES = "delete/files";
	String CHECK_FILES = "check/files";
	String SEND_FILE_RU = "send/russian";
	String SEND_FILE_EN = "send/english";
	String SEND_FILE_AR = "send/arabic";
	String SEND_FILE_SP = "send/spanish";
	
	//Find Unknown Manager
	String FIND_UNKNOWN_RM = "manager/unknown";

	// All Queries Creation per Desk
	String PERFORM_ALL_QUERIES_RU = "russian/performqueries";
	String PERFORM_ALL_QUERIES_AR = "arabic/performqueries";
	String PERFORM_ALL_QUERIES_SP = "spanish/performqueries";
	String PERFORM_ALL_QUERIES_EN = "english/performqueries";

	// Styles
	String SET_STYLE_RU = "russian/style";
	String SET_STYLE_AR = "arabic/style";
	String SET_STYLE_SP = "spanish/style";
	String SET_STYLE_EN = "english/style";

	// DataBases
	String FILL_RM_DB = "databases/managers";
	String FILL_ACTIVATIONS_DB = "databases/activations";
	String FILL_DEPOSITS_DB = "databases/deposits";
	String FILL_CREDITS_DB = "databases/credits";
	String FILL_WITHDRAWALS_DB = "databases/withdraws";
	String FILL_CLOSED_DEALS_DB = "databases/deals";
	String FILL_BALANCES_LAST_MONTH_DB = "databases/balances/last";
	String FILL_BALANCES_DB = "databases/balances/";
	String FILL_DEALS_DEALING_DB = "databases/deals/dealing";
	String FILL_LOCKS_DB = "databases/locks";
	String FILL_REOPENINGS_DB = "databases/reipenings";

	// Queries RU
	String CREATE_FILE_RU = "russian/create/file";
	String DEPOSITS_NUM_RU = "russian/deposits/number";
	String DEPOSITS_TOTAL_RU = "russian/deposits/total";
	String DEPOSITS_MORE100_RU = "russian/deposits/more100";
	String DEPOSITS_MORE450_RU = "russian/deposits/more450";
	String DEPOSITS_MORE1000_RU = "russian/deposits/more1000";
	String DEPOSITORS_NUM_RU = "russian/depositors/number";
	String DEPOSITORS_MORE100_RU = "russian/depositors/more100";
	String DEPOSITORS_MORE450_RU = "russian/depositors/more450";
	String DEPOSITORS_MORE1000_RU = "russian/depositors/more1000";
	String NEW_DEPOSITS_MORE450_RU = "russian/new/deposits/more450";
	String NEW_DEPOSITORS_MORE450_RU = "russian/new/depositors/more450";
	String NEW_DEPOSITS_RU = "russian/new/deposits/total";
	String CREDITS_REDEPOSITS_RU = "russian/credits/redeposits";
	String CREDITS_PRIOR_RU = "russian/credits/prior";
	String WITHDRAWS_RU = "russian/withdraws";
	String VOLUME_RU = "russian/volume";
	String PNL_RU = "russian/pnl";
	String TRADES_RU = "russian/trades";
	String NEW_VOLUME_RU = "russian/new/volume";
	String ACTIVE_RU = "russian/active";
	String TOTAL_CLIENTS_EOP_RU = "russian/clients/balances/total/num/eop";
	String ACCOUNT_VALUE_EOP_RU = "russian/clients/balances/total/eop";
	String ACCOUNT_VALUE_BOP_RU = "russian/clients/balances/total/bop";
	String TOTAL_CLIENTS_MORE100_RU = "russian/clients/balances/more100";
	String TOTAL_CLIENTS_MORE1000_RU = "russian/clients/balances/more1000";
	String TOTAL_CLIENTS_ACTIVE_MORE1000_RU = "russian/clients/balances/active/more1000";
	String TOTAL_CLIENTS_ACTIVE_MORE100_RU = "russian/clients/balances/active/more100";
	String ACTIVE_MORE5_RU = "russian/clients/active/more5";
	String NEW_USERS_RU = "russian/new/clients";
	String NEW_USERS_MORE5_RU = "russian/new/active/more5";
	String TRADES_DEALING_RU = "russian/trades/dealing";
	String CREDITS_LOCK_RU = "russian/credits/lock";
	String DEPOSITS_LOCK_RU = "russian/deposits/lock";
	String CREDITS_REOPENING_RU = "russian/credits/reopening";
	String DEPOSITS_REOPENING_RU = "russian/deposits/reopening";

	// Queries AR
	String CREATE_FILE_AR = "arabic/create/file";
	String DEPOSITS_NUM_AR = "arabic/deposits/number";
	String DEPOSITS_TOTAL_AR = "arabic/deposits/total";
	String DEPOSITS_MORE50_AR = "arabic/deposits/more50";
	String DEPOSITORS_MORE50_AR = "arabic/depositors/more50";
	String NEW_DEPOSITS_MORE450_AR = "arabic/new/deposits/more450";
	String NEW_DEPOSITORS_MORE450_AR = "arabic/new/depositors/more450";
	String NEW_DEPOSITS_AR = "arabic/new/deposits/total";
	String CREDITS_REDEPOSITS_AR = "arabic/credits/redeposits";
	String CREDITS_PRIOR_AR = "arabic/credits/prior";
	String WITHDRAWS_AR = "arabic/withdraws";
	String VOLUME_AR = "arabic/volume";
	String PNL_AR = "arabic/pnl";
	String TRADES_AR = "arabic/trades";
	String NEW_VOLUME_AR = "arabic/new/volume";
	String ACTIVE_AR = "arabic/active";
	String TOTAL_CLIENTS_EOP_AR = "arabic/clients/balances/total/num/eop";
	String ACCOUNT_VALUE_EOP_AR = "arabic/clients/balances/total/eop";
	String NEW_USERS_AR = "arabic/new/clients";
	String NEW_USERS_MORE5_AR = "arabic/new/active/more5";
	String ACCOUNT_VALUE_BOP_AR = "arabic/clients/balances/total/bop";
	String TRADES_DEALING_AR = "arabic/trades/dealing";
	String CREDITS_LOCK_AR = "arabic/credits/lock";
	String DEPOSITS_LOCK_AR = "arabic/deposits/lock";
	String CREDITS_REOPENING_AR = "arabic/credits/reopening";
	String DEPOSITS_REOPENING_AR = "arabic/deposits/reopening";

	// Queries EN
	String CREATE_FILE_EN = "english/create/file";
	String DEPOSITS_NUM_EN = "english/deposits/number";
	String DEPOSITS_TOTAL_EN = "english/deposits/total";
	String DEPOSITS_MORE50_EN = "english/deposits/more50";
	String DEPOSITORS_MORE50_EN = "english/depositors/more50";
	String NEW_DEPOSITS_MORE450_EN = "english/new/deposits/more450";
	String NEW_DEPOSITORS_MORE450_EN = "english/new/depositors/more450";
	String NEW_DEPOSITS_EN = "english/new/deposits/total";
	String CREDITS_REDEPOSITS_EN = "english/credits/redeposits";
	String CREDITS_PRIOR_EN = "english/credits/prior";
	String WITHDRAWS_EN = "english/withdraws";
	String VOLUME_EN = "english/volume";
	String PNL_EN = "english/pnl";
	String TRADES_EN = "english/trades";
	String NEW_VOLUME_EN = "english/new/volume";
	String ACTIVE_EN = "english/active";
	String TOTAL_CLIENTS_EOP_EN = "english/clients/balances/total/num/eop";
	String ACCOUNT_VALUE_EOP_EN = "english/clients/balances/total/eop";
	String NEW_USERS_EN = "english/new/clients";
	String NEW_USERS_MORE5_EN = "english/new/active/more5";
	String ACCOUNT_VALUE_BOP_EN = "english/clients/balances/total/bop";
	String TRADES_DEALING_EN = "english/trades/dealing";
	String CREDITS_LOCK_EN = "english/credits/lock";
	String DEPOSITS_LOCK_EN = "english/deposits/lock";
	String CREDITS_REOPENING_EN = "english/credits/reopening";
	String DEPOSITS_REOPENING_EN = "english/deposits/reopening";

	// Queries SP
	String CREATE_FILE_SP = "spanish/create/file";
	String DEPOSITS_NUM_SP = "spanish/deposits/number";
	String DEPOSITS_TOTAL_SP = "spanish/deposits/total";
	String DEPOSITS_MORE50_SP = "spanish/deposits/more50";
	String DEPOSITORS_MORE50_SP = "spanish/depositors/more50";
	String NEW_DEPOSITS_MORE450_SP = "spanish/new/deposits/more450";
	String NEW_DEPOSITORS_MORE450_SP = "spanish/new/depositors/more450";
	String NEW_DEPOSITS_SP = "spanish/new/deposits/total";
	String CREDITS_REDEPOSITS_SP = "spanish/credits/redeposits";
	String CREDITS_PRIOR_SP = "spanish/credits/prior";
	String WITHDRAWS_SP = "spanish/withdraws";
	String VOLUME_SP = "spanish/volume";
	String PNL_SP = "spanish/pnl";
	String TRADES_SP = "spanish/trades";
	String NEW_VOLUME_SP = "spanish/new/volume";
	String ACTIVE_SP = "spanish/active";
	String TOTAL_CLIENTS_EOP_SP = "spanish/clients/balances/total/num/eop";
	String ACCOUNT_VALUE_EOP_SP = "spanish/clients/balances/total/eop";
	String NEW_USERS_SP = "spanish/new/clients";
	String NEW_USERS_MORE5_SP = "spanish/new/active/more5";
	String ACCOUNT_VALUE_BOP_SP = "spanish/clients/balances/total/bop";
	String TRADES_DEALING_SP = "spanish/trades/dealing";
	String CREDITS_LOCK_SP = "spanish/credits/lock";
	String DEPOSITS_LOCK_SP = "spanish/deposits/lock";
	String CREDITS_REOPENING_SP = "spanish/credits/reopening";
	String DEPOSITS_REOPENING_SP = "spanish/deposits/reopening";

}
