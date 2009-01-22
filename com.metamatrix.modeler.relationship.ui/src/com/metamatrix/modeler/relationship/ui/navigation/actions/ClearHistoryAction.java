/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.actions;

import org.eclipse.jface.action.Action;

import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationView;

/**
 * ClearHistoryAction
 */
public class ClearHistoryAction extends Action implements UiConstants.Images {

    private static final String LABEL = UiConstants.Util.getString("ClearHistoryAction.label"); //$NON-NLS-1$
    private static final String TOOLTIP = UiConstants.Util.getString("ClearHistoryAction.tooltip"); //$NON-NLS-1$

    private NavigationView viewer;

    /**
     * Construct an instance of ClearHistoryAction.
     */
    public ClearHistoryAction(NavigationView viewer) {
        super();
        this.viewer = viewer;

        setText(LABEL);
        setToolTipText(TOOLTIP);
        setDisabledImageDescriptor(UiPlugin.getDefault().getImageDescriptor(CLEAR_D)); 
        setHoverImageDescriptor(UiPlugin.getDefault().getImageDescriptor(CLEAR_C));
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(CLEAR_E));
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        this.viewer.clearHistory();
    }

}
