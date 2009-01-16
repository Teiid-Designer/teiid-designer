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

package com.metamatrix.modeler.mapping.ui.choice.actions;

import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.mapping.ui.choice.ChoicePanel;
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
    private ChoicePanel pnlChoicePanel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public LaunchCriteriaBuilder( ChoicePanel pnlChoicePanel ) {
        super( UiPlugin.getDefault() );
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.CRITERIA_BUILDER));
        this.pnlChoicePanel = pnlChoicePanel;
        setEnabled( false );            
    }
    
    // =================================================================
    // METHODS
    // =================================================================
    
    @Override
    protected void doRun() {
        // Tell ChoicePanel to show the Criteria builder
//        System.out.println("[LaunchCriteriaBuilder.doRun]"); //$NON-NLS-1$
        pnlChoicePanel.launchCriteriaBuilder();
    }
    

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    public void selectionChanged() {
        if ( ModelObjectUtilities.isReadOnly( pnlChoicePanel.getChoiceObject().getChoice() ) ) {
            setEnabled( false );
            return;
        }

        if ( pnlChoicePanel.canLaunchCriteriaBuilder() ) {         
            setEnabled( true );            
        } else {
            setEnabled( false );
        }
    }
}
