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

package com.metamatrix.modeler.mapping.ui.recursion.actions;

import com.metamatrix.modeler.mapping.ui.recursion.RecursionPanel;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * The <code>LaunchCriteriaBuilder</code> class launches the Criteria Builder
 * @since 4.0
 */
public class LaunchCriteriaBuilder extends AbstractAction {

    // =================================================================
    // FIELDS
    // =================================================================
    private RecursionPanel pnlRecursionPanel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public LaunchCriteriaBuilder( RecursionPanel pnlRecursionPanel ) {
        super( UiPlugin.getDefault() );
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.CRITERIA_BUILDER));
        this.pnlRecursionPanel = pnlRecursionPanel;
        setEnabled( false );            
    }
    
    // =================================================================
    // METHODS
    // =================================================================
    
    @Override
    protected void doRun() {
        // Tell ChoicePanel to show the Criteria builder
//        System.out.println("[LaunchCriteriaBuilder.doRun]"); //$NON-NLS-1$
        pnlRecursionPanel.launchCriteriaBuilder();
    }
    

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    public void selectionChanged() {
        if ( pnlRecursionPanel.canLaunchCriteriaBuilder() ) {         
            setEnabled( true );            
        } else {
            setEnabled( false );
        }
    }
}
