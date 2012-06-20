/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.recursion.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.mapping.ui.PluginConstants;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.mapping.ui.recursion.RecursionPanel;
import org.teiid.designer.ui.common.actions.AbstractAction;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * The <code>ClearCriteria</code> class launches the Criteria Builder
 * @since 4.0
 */
public class ClearCriteria extends AbstractAction {

    // =================================================================
    // FIELDS
    // =================================================================
    private RecursionPanel pnlRecursionPanel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ClearCriteria( RecursionPanel pnlRecursionPanel ) {
        super( UiPlugin.getDefault() );
   
        setImageDescriptor( UiPlugin.getDefault().getImageDescriptor( PluginConstants.Images.CLEAR_ICON ) );
                
        this.pnlRecursionPanel = pnlRecursionPanel;

        // default to disabled:
        setEnabled( false );
    }
    
    // =================================================================
    // METHODS
    // =================================================================
    
    @Override
    protected void doRun() {
        // Tell ChoicePanel to show the Criteria builder
//        System.out.println("[ClearCriteria.doRun]"); //$NON-NLS-1$
        pnlRecursionPanel.clearCriteria();
    }
    
    
    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    public void selectionChanged() {
//        System.out.println("[ClearCriteria.selectionChanged]"); //$NON-NLS-1$
        if ( ModelObjectUtilities.isReadOnly( pnlRecursionPanel.getRecursionObject().getMappingClass() ) ) {
            setEnabled( false );
            return;
        }

        if ( pnlRecursionPanel.canClearCriteria() ) {         
//            System.out.println("[ClearCriteria.selectionChanged] About to setEnabled( true )"); //$NON-NLS-1$
            setEnabled( true );            
        } else {
//            System.out.println("[ClearCriteria.selectionChanged] About to setEnabled( false )"); //$NON-NLS-1$
            setEnabled( false );
        }
    }    
    
}
