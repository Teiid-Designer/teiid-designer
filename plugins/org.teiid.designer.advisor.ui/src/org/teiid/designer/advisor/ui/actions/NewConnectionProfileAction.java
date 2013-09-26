/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;
import org.teiid.designer.ui.common.UiConstants.ConnectionProfileIds;

public class NewConnectionProfileAction extends Action implements AdvisorUiConstants {
	String profileCategoryId = ConnectionProfileIds.CATEGORY_JDBC;

    public NewConnectionProfileAction() {
        super();
        setText("New Connection Profile"); //$NON-NLS-1$
        setToolTipText("New Connection Profile Tooltip"); //$NON-NLS-1$

    }
    
    public NewConnectionProfileAction(String categoryId, String text, String tooltip) {
        this();
        this.profileCategoryId = categoryId;
        setText(text);
        setToolTipText(tooltip);
    }
	
	public void execute(String profileCategoryId) {
			INewWizard wiz = new NewTeiidFilteredCPWizard(profileCategoryId);

			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
			//wizardDialog.setBlockOnOpen(true);
			wizardDialog.open();
	}

	@Override
	public void run() {
		execute(this.profileCategoryId);
	}
}
