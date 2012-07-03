/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;

import com.metamatrix.modeler.internal.ui.viewsupport.DefineModelProjectDialog;

public class DefineProjectAction  extends Action implements AdvisorUiConstants {

    private Properties designerProperties;

    /**
     * Construct an instance of NewModelAction.
     */
    public DefineProjectAction() {
        super();
        setText("Define Model Project"); //$NON-NLS-1$
        setToolTipText("Define Model Action Tooltip"); //$NON-NLS-1$
        setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(Images.NEW_PROJECT_ACTION));

    }
    
    public DefineProjectAction( Properties properties ) {
        this();
        this.designerProperties = properties;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
    	final IWorkbenchWindow iww = AdvisorUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
    	
		DefineModelProjectDialog sdDialog = new DefineModelProjectDialog(iww.getShell(), this.designerProperties);

		sdDialog.open();

		if (sdDialog.getReturnCode() == Window.OK) {
			// Should do nothing since the user can select or create new project or do nothing
			// and the property for the Project will be set in the "designerProperties"
		}
    }
}
