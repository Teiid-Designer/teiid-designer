/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
