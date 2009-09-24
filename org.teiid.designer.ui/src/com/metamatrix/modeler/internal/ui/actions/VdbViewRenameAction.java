/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.DelegatableAction;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>renameResourceAction</code> serves as a wrapper to manage the Rename process in the VdbView 
 * environment.  It supplies the correct delegate Rename action for the selected object.
 */
public class VdbViewRenameAction extends AbstractAction
                                 implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Delegate action components to delete Resources. */
    IActionDelegate delegateRenameResourceAction;
    IAction renameResourceActionWrapper;
    
    /** Delegate action to delete EObjects. */
    private AbstractAction renameEObjectAction;
    
    /** The current delegate. */
    private Object delegateAction;

    private static final String RENAME_TITLE 
        = Util.getString("VdbViewRenameAction.renameTitle.text"); //$NON-NLS-1$

    private static final String RENAME_TOOLTIP 
        = Util.getString("VdbViewRenameAction.renameTooltip.text"); //$NON-NLS-1$
     

    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>renameResourceAction</code>.
     */
    public VdbViewRenameAction() {
        super( UiPlugin.getDefault() );
        
        // create Resource rename action
        delegateRenameResourceAction = getRenameResourceAction();
        
        // make it the default
        this.delegateAction = this.delegateRenameResourceAction;
        
        // set labels
        // create EObject rename action
        renameEObjectAction = new TreeViewerRenameAction();

        setText( RENAME_TITLE );            
        setToolTipText( RENAME_TOOLTIP );
    }
    
    private IActionDelegate getRenameResourceAction() {
        
        if ( delegateRenameResourceAction == null ) {
            IWorkbenchWindow window 
                = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
    
            // create Rename delegate
            delegateRenameResourceAction = new RenameRefactorAction();
            
            // create Rename wrapper
            renameResourceActionWrapper = new DelegatableAction( delegateRenameResourceAction, window );            
            renameResourceActionWrapper.setText( RENAME_TITLE );
            
            // set labels
            setText( RENAME_TITLE );            
            setToolTipText( RENAME_TOOLTIP );
        }
        
        return delegateRenameResourceAction;
    }
    
    public void setTreeViewer(TreeViewer treeViewer, ILabelProvider labelProvider) {
        ((TreeViewerRenameAction)renameEObjectAction).setTreeViewer( treeViewer, labelProvider );
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        
        if ( this.delegateAction == this.delegateRenameResourceAction ) {
            this.delegateRenameResourceAction.run( renameResourceActionWrapper );
        } 
        else
        if ( this.delegateAction == this.renameEObjectAction ) {
            ((TreeViewerRenameAction)this.renameEObjectAction).doRun();            
        }
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractAction#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        
        super.selectionChanged(thePart, theSelection);
        
        // make the delegate actions aware of the new selection and set enablement
        IStructuredSelection selection = null;
        
        if (theSelection instanceof IStructuredSelection) {
             selection = (IStructuredSelection)theSelection;
        } else {
            selection = StructuredSelection.EMPTY;
        }
        
        // set enablement
        boolean enable = false;
        boolean alreadySet = false;
        
        // this block adds some defensive code just in case the VDB tree root element is passed in
        if (SelectionUtilities.isSingleSelection(theSelection) && SelectionUtilities.isAllIResourceObjects(theSelection)) {
            IResource resource = (IResource)SelectionUtilities.getSelectedIResourceObjects(theSelection).get(0);
            
            if (resource.equals(resource.getWorkspace().getRoot())) {
                enable = false;
                alreadySet = true;
            }
        }
        
        if (!alreadySet) {
            delegateRenameResourceAction.selectionChanged( renameResourceActionWrapper, selection );
            
            if (this.renameEObjectAction != null) {
                this.renameEObjectAction.selectionChanged(thePart, selection);
            }

            if ( this.renameResourceActionWrapper.isEnabled() ) {
                this.delegateAction = this.delegateRenameResourceAction;
                enable = true;            
            } else if ((this.renameEObjectAction != null) && this.renameEObjectAction.isEnabled()) {
                this.delegateAction = this.renameEObjectAction;
                enable = true;
            }
        }

        setEnabled(enable);
    }
    
    protected Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
}
