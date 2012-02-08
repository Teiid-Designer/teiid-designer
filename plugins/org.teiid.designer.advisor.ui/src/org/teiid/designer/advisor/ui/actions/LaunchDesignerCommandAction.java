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
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import com.metamatrix.modeler.ui.viewsupport.PropertiesContextManager;


/** This action can be used to launch an action defined in the product
  *  characteristics generically.
  *  <br/>param1 is the name of the action to get.
  *  <br/>param2, if set to requiresProject, will call ProductCharacteristics.getHiddenProject
  *  to try to force a project to exist.  Otherwise, the action itself needs to make sure it can
  *  run.
  * <br/>Note: run() method is not implemented.  use run(String[], ICheatSheetManager) instead.
  * See com.metamatrix.modeler.ui.product.IModelingProductCharacteristics for the
  * constants to use.
  * @since 5.0
  */
public class LaunchDesignerCommandAction extends Action implements ICheatSheetAction {
    //
    // Class constants:
    //
    //private static final String REQUIRES_PROJECT = "requiresProject"; //$NON-NLS-1$
	
	private static final PropertiesContextManager propertiesManager = new PropertiesContextManager();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public LaunchDesignerCommandAction() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void run() {
        throw new IllegalStateException();
    }

    public void run(String[] params, ICheatSheetManager manager) {
        final boolean[] result = {true};
        if (params != null && params.length > 0) {
            String pActionID = params[0];
            Properties props = propertiesManager.getProperties(manager.getCheatSheetID());
            AdvisorActionFactory.executeAction(pActionID, props);
        }
        
        notifyResult(result[0]);
    }
}