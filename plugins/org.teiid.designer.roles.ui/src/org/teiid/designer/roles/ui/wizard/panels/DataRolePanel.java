/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard.panels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.roles.ui.wizard.DataRolesModelTreeProvider;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 *
 */
public abstract class DataRolePanel {
	private DataRoleWizard wizard;
	private Composite primaryPanel;

	/**
     * @param parent
     * @param wizard
     */
    public DataRolePanel(Composite parent, DataRoleWizard wizard) {
    	super();
    	this.wizard = wizard;
    	
    	createPanel(parent);
    	
    	createControl();
    }
    
	private void createPanel(Composite parent) {
    	primaryPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, 2);
    	primaryPanel.setLayout(new GridLayout(2, false));
	}
	
	public Composite getPrimaryPanel() {
		return this.primaryPanel;
	}
	
	abstract void createControl();

	protected DataRoleWizard getWizard() {
		return this.wizard;
	}
	
	protected DataRolesModelTreeProvider getTreeProvider() {
		return this.wizard.getTreeProvider();
	}
	
	protected void validateInputs() {
		this.wizard.validateInputs();
	}
	
	protected Shell getShell() {
		return getPrimaryPanel().getShell();
	}
	
	public void refresh() {
		
	}
	
	protected String getSpaces(int numSpaces) {
		StringBuilder sb = new StringBuilder();
		for( int i=0; i<numSpaces; i++ ) {
			sb.append(StringConstants.SPACE);
		}
		
		return sb.toString();
	}
}
