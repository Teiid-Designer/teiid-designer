/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.actions;

import org.eclipse.jface.action.Action;

import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationListener;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationView;

/**
 * RefreshAction refreshes the content of the NavigationView
 */
public class RefreshAction extends Action implements NavigationListener, UiConstants.Images {

    private static final String LABEL = UiConstants.Util.getString("RefreshAction.label"); //$NON-NLS-1$
    private static final String TOOLTIP = UiConstants.Util.getString("RefreshAction.tooltip"); //$NON-NLS-1$

    private NavigationView viewer;

    /**
     * Construct an instance of RefreshAction.
     * 
     */
    public RefreshAction(NavigationView viewer) {
        this.viewer= viewer;
        setText(LABEL);
        setToolTipText(TOOLTIP);

        setDisabledImageDescriptor(UiPlugin.getDefault().getImageDescriptor(REFRESH_D));  
        setHoverImageDescriptor(UiPlugin.getDefault().getImageDescriptor(REFRESH_C));
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(REFRESH_E));
        
        setEnabled(this.viewer.getCurrentNavigationContext() != null);
        viewer.addNavigationListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        this.viewer.refresh();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.relationship.ui.navigation.NavigationListener#navigationChanged(com.metamatrix.modeler.relationship.NavigationContext)
     */
    public void navigationChanged(NavigationContext newContext) {
        setEnabled(this.viewer.getCurrentNavigationContext() != null);
    }

}
