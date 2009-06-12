/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.product.IProductCharacteristics;


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
public class LaunchProductAction extends Action implements ICheatSheetAction {
    //
    // Class constants:
    //
    private static final String REQUIRES_PROJECT = "requiresProject"; //$NON-NLS-1$

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public LaunchProductAction() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void run() {
        throw new IllegalStateException();
    }

    public void run(String[] params, ICheatSheetManager manager) {
        final boolean[] result = {false};
        if (params != null
                && params.length > 0) {
            String pActionID = params[0];
            IProductCharacteristics productCharacteristics = ProductCustomizerMgr.getInstance()
                    .getProductCharacteristics();
            IAction productAction = productCharacteristics.getProductAction(pActionID);
            if (productAction != null) {
                if (params.length > 1 && REQUIRES_PROJECT.equals(params[1])) {
                    productCharacteristics.getHiddenProject();
                } // endif

                IPropertyChangeListener pcl = new IPropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if (RESULT.equals(event.getProperty())) {
                            result[0] = ((Boolean) event.getNewValue()).booleanValue();
                        } // endif
                    }
                };
                productAction.addPropertyChangeListener(pcl);
                productAction.run();
                productAction.removePropertyChangeListener(pcl);
            } // endif
        } // endif
        notifyResult(result[0]);
    }
}
