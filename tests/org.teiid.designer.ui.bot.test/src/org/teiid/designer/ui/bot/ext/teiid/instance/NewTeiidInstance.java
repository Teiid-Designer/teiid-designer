package org.teiid.designer.ui.bot.ext.teiid.instance;

import org.jboss.tools.ui.bot.ext.logging.WidgetsLogger;
import org.teiid.designer.ui.bot.ext.teiid.perspective.TeiidPerspective;
import org.teiid.designer.ui.bot.ext.teiid.view.TeiidInstanceView;

/**
 * Creates a new Teiid instance. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class NewTeiidInstance {
	
	private String name;

	private String host;

	private String adminPort;

	private String adminUser;

	private String adminPassword;

	private String userPort;

	private String userName;

	private String userPassword;

	public void execute(){
		TeiidInstanceView view = TeiidPerspective.getInstance().getTeiidInstanceView();
		
		TeiidInstanceDialog dialog = view.newTeiidInstance();
		WidgetsLogger.log();
		dialog.setName(name);
		dialog.setHost(host);
		dialog.setAdminPort(adminPort);
		dialog.setAdminUser(adminUser);
		dialog.setAdminPassword(adminPassword);
		dialog.setUserPort(userPort);
		dialog.setUserName(userName);
		dialog.setUserPassword(userPassword);
		dialog.checkSave();
		dialog.finish();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setAdminPort(String adminPort) {
		this.adminPort = adminPort;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public void setUserPort(String userPort) {
		this.userPort = userPort;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
}
