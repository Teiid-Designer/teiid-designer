package org.teiid.designer.ui.bot.ext.teiid.instance;


public class NewDefaultTeiidInstance extends NewTeiidInstance {

	public static final String TEIID_URL = "mms://localhost:31443";

	public static final String TEIID_HOST = "localhost";
	
	public static final String TEIID_ADMIN_PORT = "31443";
	
	public static final String TEIID_ADMIN_USER = "admin";
	
	public static final String TEIID_ADMIN_PASSWORD = "admin";
	
	public static final String TEIID_JDBC_PORT = "31000";
	
	public static final String TEIID_USER = "user";
	
	public static final String TEIID_USER_PASSWORD = "user";
	
	public NewDefaultTeiidInstance() {
		super();
		setName(TEIID_URL);
		setHost(TEIID_HOST);
		setAdminPort(TEIID_ADMIN_PORT);
		setAdminUser(TEIID_ADMIN_USER);
		setAdminPassword(TEIID_ADMIN_PASSWORD);

		setUserPort(TEIID_JDBC_PORT);
		setUserName(TEIID_USER);
		setUserPassword(TEIID_USER_PASSWORD);
	}
}
