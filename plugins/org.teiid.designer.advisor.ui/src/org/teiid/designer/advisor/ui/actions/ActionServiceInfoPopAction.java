/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.core.InfoPopAction;

import com.metamatrix.ui.actions.GlobalActionsMap;

/**
 * 
 */
public class ActionServiceInfoPopAction extends InfoPopAction {
    //
    // fields
    //

    private String actionClass;

    //
    // constructors
    //

    public ActionServiceInfoPopAction( String theActionClassName,
                                       int theType,
                                       String theDescription,
                                       Image theImage ) {
        super(null, theType, theDescription, AdvisorUiPlugin.getImageHelper().BINDING_IMAGE);
        this.actionClass = theActionClassName;
    }

    //
    // methods
    //

    /**
     * @see org.eclipse.ui.advisor.core.vdbview.ui.webservices.InfoPopAction#getAction()
     * @since 5.0
     */
    @Override
    public IAction getAction() {
        if (super.getAction() == null) {
            IAction action = null;

            try {
                action = AdvisorUiPlugin.getDefault().getActionService().getAction(this.actionClass);
            } catch (CoreException theException) {
                action = GlobalActionsMap.UNSUPPORTED_ACTION;
            }

            setAction(action);
        }
        IAction theAction = super.getAction();

        return theAction;
    }
}
