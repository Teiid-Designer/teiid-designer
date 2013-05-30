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


public class NewConnectionProfileAction extends Action implements AdvisorUiConstants {
	public final static String CATEGORY_JDBC = "org.eclipse.datatools.connectivity.db.category"; //$NON-NLS-1$
	public static final String CATEGORY_ODA_FLAT_FILE_ID = "org.eclipse.datatools.connectivity.oda.flatfile"; //$NON-NLS-1$
	public static final String CATEGORY_MODESHAPE = "org.teiid.designer.datatools.profiles.modeshape.ModeShapeConnectionProfile"; //$NON-NLS-1$
	public static final String CATEGORY_XML_FILE_LOCAL = "org.teiid.designer.datatools.profiles.xml.localfile"; //$NON-NLS-1$
	public static final String CATEGORY_XML_FILE_URL = "org.teiid.designer.datatools.profiles.xml.fileurl"; //$NON-NLS-1$
	public static final String CATEGORY_WS_CONNECTION = "org.teiid.designer.datatools.profiles.ws.WSConnectionProfile"; //$NON-NLS-1$
	public static final String CATEGORY_ODATA_CONNECTION = "org.teiid.designer.datatools.profiles.ws.ODataConnectionProfile"; //$NON-NLS-1$
	public static final String CATEGORY_LDAP_CONNECTION = "org.teiid.designer.datatools.profiles.ldap.LDAPConnectionProfile"; //$NON-NLS-1$
	public static final String CATEGORY_SALESFORCE_CONNECTION = "org.teiid.designer.datatools.salesforce.connectionProfile"; //$NON-NLS-1$
	
	String profileCategoryId = CATEGORY_JDBC;

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
