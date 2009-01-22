/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.actions;

import org.eclipse.jface.action.Action;

import com.metamatrix.modeler.relationship.NavigationContextInfo;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationView;

/**
 * ShowHistoryAction is the Action wrapper to display a NavigationContext in the NavigationView.
 */
public class ShowHistoryAction extends Action {

    private NavigationView view;
    private NavigationContextInfo info;

    /**
     * Construct an instance of ShowHistoryAction.
     * 
     */
    public ShowHistoryAction(NavigationView view, NavigationContextInfo info) {
        super(info.getLabel());
        this.view = view;
        this.info = info;
    }




    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        this.view.setCurrentContext(this.info);
    }

}
