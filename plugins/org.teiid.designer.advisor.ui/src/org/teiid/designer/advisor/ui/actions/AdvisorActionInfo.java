/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.core.commands.AbstractHandler;

public class AdvisorActionInfo {
	private final String id;
	private final String displayName;
	private final String shortDisplayName;
	private boolean isSubMenu;
	
	AbstractHandler actionHandler;
	
	public AdvisorActionInfo(String id, String displayName, String shortDisplayName) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.shortDisplayName = shortDisplayName;
	}
	
	public AdvisorActionInfo(String id, String displayName) {
		this(id, displayName, displayName);
	}
	
	public AdvisorActionInfo(String id, String displayName, AbstractHandler actionHandler) {
		this(id, displayName, displayName);
		this.actionHandler = actionHandler;
	}
	
	public AdvisorActionInfo(String id, String displayName, String shortDisplayName, AbstractHandler actionHandler) {
		this(id, displayName, shortDisplayName);
		this.actionHandler = actionHandler;
	}

	public String getId() {
		return this.id;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getShortDisplayName() {
		return this.shortDisplayName;
	}

	public AbstractHandler getActionHandler() {
		return this.actionHandler;
	}

	public void setActionHandler(AbstractHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	public boolean isSubMenu() {
		return this.isSubMenu;
	}

	public void setIsSubMenu(boolean isSubMenu) {
		this.isSubMenu = isSubMenu;
	}
	
}
