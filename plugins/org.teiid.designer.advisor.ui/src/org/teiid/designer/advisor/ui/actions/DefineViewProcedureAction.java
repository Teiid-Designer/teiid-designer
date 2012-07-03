/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import java.util.Properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.runtime.ui.extension.ApplyRestWarPropertiesAction;

import com.metamatrix.modeler.transformation.ui.editors.DefineViewProcedureDialog;

public class DefineViewProcedureAction extends Action implements AdvisorUiConstants {

    private Properties designerProperties;

    /**
     * Construct an instance of NewModelAction.
     */
    public DefineViewProcedureAction() {
        super();
        setText("Define View Table"); //$NON-NLS-1$
        setToolTipText("Define View Table Tooltip"); //$NON-NLS-1$
        setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(Images.NEW_VIRTUAL_PROCEDURE_ICON));

    }
    
    public DefineViewProcedureAction( Properties properties ) {
        this();
        this.designerProperties = properties;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
    	final IWorkbenchWindow iww = AdvisorUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
    	
    	DefineViewProcedureDialog sdDialog = new DefineViewProcedureDialog(iww.getShell(), this.designerProperties);

		sdDialog.open();

		if (sdDialog.getReturnCode() == Window.OK) {
			EObject proc = sdDialog.getViewProcedure();
			boolean doApply = sdDialog.doApplyRestWarProperties();
			String restMethod = sdDialog.getRestMethod();
			String restURI = sdDialog.getRestUri();
			if( proc != null && doApply ) {
				try {
					ApplyRestWarPropertiesAction.applyRestWarProperties(proc);
					if( restMethod != null ) {
						ApplyRestWarPropertiesAction.setRestMethod(proc, restMethod);
					}
					if( restURI != null ) {
						ApplyRestWarPropertiesAction.setRestUri(proc, restURI);
					}
				} catch (Exception ex) {
					AdvisorUiConstants.UTIL.log(ex);
				}
			}
		}
    }
}
