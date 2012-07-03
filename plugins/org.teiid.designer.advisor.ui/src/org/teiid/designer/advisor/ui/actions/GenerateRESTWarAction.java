/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.dialogs.GenerateRestWarDialog;
import org.teiid.designer.runtime.ui.actions.GenerateRestWarAction;

public class GenerateRESTWarAction extends Action implements AdvisorUiConstants {

    private Properties designerProperties;

    /**
     * Construct an instance of NewModelAction.
     */
    public GenerateRESTWarAction() {
        super();
        setText("Generate REST War"); //$NON-NLS-1$
        setToolTipText("Generate REST War"); //$NON-NLS-1$
        setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(Images.GENERATE_WAR));

    }
    
    public GenerateRESTWarAction( Properties properties ) {
        this();
        this.designerProperties = properties;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
    	final IWorkbenchWindow iww = AdvisorUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
    	
		GenerateRestWarDialog sdDialog = new GenerateRestWarDialog(iww.getShell(), this.designerProperties);

		sdDialog.open();

		if (sdDialog.getReturnCode() == Window.OK) {
			IFile theVdbFile = (IFile)sdDialog.getVdb();
			if( theVdbFile != null ) {
				GenerateRestWarAction genAction = new GenerateRestWarAction();
				genAction.setDesingerProperties(this.designerProperties);
				genAction.setSelection(new StructuredSelection(theVdbFile));
				genAction.run();
			}
		}
    }
}