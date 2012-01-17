/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class TeiidDesignerActionHandler extends AbstractHandler {
	String id;
	String displayName;
	
	public TeiidDesignerActionHandler(String id, String displayName) {
		assert id != null;
		assert displayName != null;
		this.id = id;
		this.displayName = displayName;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AdvisorActionFactory.executeAction(this);
		return null;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
