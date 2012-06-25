package org.teiid.designer.ui.bot.ext.teiid.instance;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.wizards.SWTBotWizard;

public class TeiidInstanceDialog {

	private static final String PASSWORD = "Password:";

	private static final String USER_NAME = "User name:";

	private static final String PORT_NUMBER = "Port number:";

	private static final String TEIID_JDBC_CONNECTION_INFO = "Teiid JDBC Connection Info";

	private static final String TEIID_ADMIN_CONNECTION_INFO = "Teiid Admin Connection Info";
	
	// the fields in admin group need to be identified by their index due to the fact that both JDBC and admin connection 
	// info contain the same labels and SWT bot does just check if there is label ANYWHERE before the text field
	// not just in the right group. This results in the fact that all text fields in admin fullfil 
	// all label conditions (user name, port, pasword)
	// NOTE: there is a read only text at the index 0
	private static final int ADMIN_PORT_FIELD_INDEX = 1;
	
	private static final int ADMIN_USER_FIELD_INDEX = 2;
	
	private static final int ADMIN_PASSWORD_FIELD_INDEX = 3;
	
	private SWTBotShell shell;
	
	public TeiidInstanceDialog(SWTBotShell shell) {
		this.shell = shell;
	}
	
	public void finish(){
//		new SWTBotWizard().finishWithWait();
		shell.bot().button("Finish").setFocus();
		shell.bot().button("Finish").click();
	}
	
	public String getName() {
		return shell.bot().textInGroup("Name").getText();
	}

	public void setName(String name) {
		shell.bot().textInGroup("Name").setFocus();
		shell.bot().textInGroup("Name").setText(name);
	}
	
	public String getHost() {
		return shell.bot().textWithLabel("Host:").getText();
	}

	public void setHost(String host) {
		shell.bot().textWithLabel("Host: ").setFocus();
		shell.bot().textWithLabel("Host: ").setText(host);
	}

	public String getAdminPort() {
		return shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_ADMIN_CONNECTION_INFO, ADMIN_PORT_FIELD_INDEX).getText();
	}

	public void setAdminPort(String adminPort) {
		shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_ADMIN_CONNECTION_INFO, ADMIN_PORT_FIELD_INDEX).setFocus();
		shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_ADMIN_CONNECTION_INFO, ADMIN_PORT_FIELD_INDEX).setText(adminPort);
	}

	public String getAdminUser() {
		return shell.bot().textWithLabelInGroup(USER_NAME, TEIID_ADMIN_CONNECTION_INFO, ADMIN_USER_FIELD_INDEX).getText();
	}

	public void setAdminUser(String adminUser) {
		shell.bot().textWithLabelInGroup(USER_NAME, TEIID_ADMIN_CONNECTION_INFO, ADMIN_USER_FIELD_INDEX).setFocus();
		shell.bot().textWithLabelInGroup(USER_NAME, TEIID_ADMIN_CONNECTION_INFO, ADMIN_USER_FIELD_INDEX).setText(adminUser);
	}

	public String getAdminPassword() {
		return shell.bot().textWithLabelInGroup(PASSWORD, TEIID_ADMIN_CONNECTION_INFO, ADMIN_PASSWORD_FIELD_INDEX).getText();
	}

	public void setAdminPassword(String adminPassword) {
		shell.bot().textWithLabelInGroup(PASSWORD, TEIID_ADMIN_CONNECTION_INFO, ADMIN_PASSWORD_FIELD_INDEX).setFocus();
		shell.bot().textWithLabelInGroup(PASSWORD, TEIID_ADMIN_CONNECTION_INFO, ADMIN_PASSWORD_FIELD_INDEX).setText(adminPassword);
	}

	public String getUserPort() {
		return shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_JDBC_CONNECTION_INFO).getText();
	}

	public void setUserPort(String userPort) {
		shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_JDBC_CONNECTION_INFO).setFocus();
		shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_JDBC_CONNECTION_INFO).setText(userPort);
	}

	public String getUserName() {
		return shell.bot().textWithLabelInGroup(USER_NAME, TEIID_JDBC_CONNECTION_INFO).getText();
	}

	public void setUserName(String userName) {
		shell.bot().textWithLabelInGroup(USER_NAME, TEIID_JDBC_CONNECTION_INFO).setFocus();
		shell.bot().textWithLabelInGroup(USER_NAME, TEIID_JDBC_CONNECTION_INFO).setText(userName);
	}

	public String getUserPassword() {
		return shell.bot().textWithLabelInGroup(PASSWORD, TEIID_JDBC_CONNECTION_INFO).getText();
	}

	public void setUserPassword(String userPassword) {
		shell.bot().textWithLabelInGroup(PASSWORD, TEIID_JDBC_CONNECTION_INFO).setFocus();
		shell.bot().textWithLabelInGroup(PASSWORD, TEIID_JDBC_CONNECTION_INFO).setText(userPassword);
	}
	
	public void checkSave() {
		shell.bot().checkBox("Save").select();
	}
}
