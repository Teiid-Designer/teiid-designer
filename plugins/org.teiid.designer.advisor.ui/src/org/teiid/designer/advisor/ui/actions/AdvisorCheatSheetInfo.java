/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;

public class AdvisorCheatSheetInfo extends AdvisorActionInfo {
	
	
	
	protected AdvisorCheatSheetInfo(String id, String displayName) {
		super(id, displayName);
	}

	public void launch() {
        OpenCheatSheetAction action = new OpenCheatSheetAction(getId());
        action.run();
	}
	
}