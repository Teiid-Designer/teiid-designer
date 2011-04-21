/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.choice.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.choice.ChoicePanel;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * The <code>HideExcludedOptions</code> class hides the excluded option rows in the table
 * @since 4.0
 */
public class HideExcludedOptions extends AbstractAction {

    // =================================================================
    // FIELDS
    // =================================================================
    private ChoicePanel pnlChoicePanel;
    private static final String HIDE_CHECKBOX_TOOLTIP = UiConstants.Util.getString("ChoicePanel.hideCheckbox.toolTip"); //$NON-NLS-1$
    private static final String SHOW_CHECKBOX_TOOLTIP = UiConstants.Util.getString("ChoicePanel.showCheckbox.toolTip"); //$NON-NLS-1$

    private boolean bOptionsShowing = true;
    
    // =================================================================
    // CONSTRUCTORS
    // =================================================================
    
    public HideExcludedOptions( ChoicePanel pnlChoicePanel ) {
        super( UiPlugin.getDefault() );
        setImageDescriptor( UiPlugin.getDefault().getImageDescriptor( PluginConstants.Images.FILTER_ICON ) );
        
        this.pnlChoicePanel = pnlChoicePanel;
    }
    
    // =================================================================
    // METHODS
    // =================================================================
    
    @Override
    protected void doRun() {
        // Tell ChoicePanel to show the Criteria builder
        
        if ( bOptionsShowing ) {        
            pnlChoicePanel.hideExcludedOptions();
            bOptionsShowing = false;
            this.setToolTipText( SHOW_CHECKBOX_TOOLTIP );   
        }  else {
            pnlChoicePanel.showExcludedOptions();
            bOptionsShowing = true;               
            this.setToolTipText( HIDE_CHECKBOX_TOOLTIP );   
        }
    }
    

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
         
        super.selectionChanged(part, selection);
        if ( pnlChoicePanel.canHideExcludedOptions() ) {         
            setEnabled( true );            
        } else {
            setEnabled( false );
        }
    } 
}
