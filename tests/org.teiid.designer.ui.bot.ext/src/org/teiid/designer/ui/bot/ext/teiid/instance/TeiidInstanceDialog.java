package org.teiid.designer.ui.bot.ext.teiid.instance;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.wizards.SWTBotWizard;

public class TeiidInstanceDialog {

	private static final String PASSWORD = "Password:";

	private static final String USER_NAME = "User name:";

	private static final String PORT_NUMBER = "Port number:";

	private static final String TEIID_JDBC_CONNECTION_INFO = "Teiid JDBC Connection Info";

	private static final String TEIID_ADMIN_CONNECTION_INFO = "Teiid Admin Connection Info";
	
	private SWTBotShell shell;
	
	public TeiidInstanceDialog(SWTBotShell shell) {
		this.shell = shell;
	}
	
	public void finish(){
		new SWTBotWizard().finishWithWait();
	}
	
	public String getHost() {
		return shell.bot().textWithLabel("Host:").getText();
	}

	public void setHost(String host) {
		shell.bot().textWithLabel("Host:").setText(host);
	}

	public String getAdminPort() {
		return shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_ADMIN_CONNECTION_INFO).getText();
	}

	public void setAdminPort(String adminPort) {
		shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_ADMIN_CONNECTION_INFO).setText(adminPort);
	}

	public String getAdminUser() {
		return shell.bot().textWithLabelInGroup(USER_NAME, TEIID_ADMIN_CONNECTION_INFO).getText();
	}

	public void setAdminUser(String adminUser) {
		shell.bot().textWithLabelInGroup(USER_NAME, TEIID_ADMIN_CONNECTION_INFO).setText(adminUser);
	}

	public String getAdminPassword() {
		return shell.bot().textWithLabelInGroup(PASSWORD, TEIID_ADMIN_CONNECTION_INFO).getText();
	}

	public void setAdminPassword(String adminPassword) {
		shell.bot().textWithLabelInGroup(PASSWORD, TEIID_ADMIN_CONNECTION_INFO).setText(adminPassword);
	}

	public String getUserPort() {
		return shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_JDBC_CONNECTION_INFO).getText();
	}

	public void setUserPort(String userPort) {
		shell.bot().textWithLabelInGroup(PORT_NUMBER, TEIID_JDBC_CONNECTION_INFO).setText(userPort);
	}

	public String getUserName() {
		return shell.bot().textWithLabelInGroup(USER_NAME, TEIID_JDBC_CONNECTION_INFO,1).getText();
	}

	public void setUserName(String userName) {
		shell.bot().textWithLabelInGroup(USER_NAME, TEIID_JDBC_CONNECTION_INFO,1).setText(userName);
	}

	public String getUserPassword() {
		return shell.bot().textWithLabelInGroup(PASSWORD, TEIID_JDBC_CONNECTION_INFO,2).getText();
	}

	public void setUserPassword(String userPassword) {
		shell.bot().textWithLabelInGroup(PASSWORD, TEIID_JDBC_CONNECTION_INFO,2).setText(userPassword);
	}
}
