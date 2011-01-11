/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.choice.actions;

import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.choice.ChoicePanel;
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
