/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * DeleteAction
 */
public class DeleteAction 
     extends RelationshipAction
  implements UiConstants  
 {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "DeleteAction.problem"; //$NON-NLS-1$
    private static final String ACTION_DESCRIPTION = "Delete"; //$NON-NLS-1$
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of DeleteAction.
     * 
     */
    public DeleteAction() {
        super();
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
    }

    //============================================================================================================================
    // ISelectionListener Methods
    
    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        // sample code:
        super.selectionChanged(part, selection);
        boolean enable = false;
        
        List sourceEObjects = null;
        if (SelectionUtilities.isSingleSelection(selection)) {
            sourceEObjects = new ArrayList(1);
            Object o = SelectionUtilities.getSelectedEObject(selection);
            sourceEObjects.add(o);
        } else if (SelectionUtilities.isMultiSelection(selection)) {
            sourceEObjects = SelectionUtilities.getSelectedEObjects(selection);
        }
        if( sourceEObjects != null && !sourceEObjects.isEmpty())      
        	enable = RelationshipGlobalActionsManager.canDelete(sourceEObjects);
        
        setEnabled(enable);
    } 

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        List selectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
        
        if (selectedEObjects != null) {
            String objectString = null;
            if (selectedEObjects.size() == 1) {
                EObject obj = (EObject)selectedEObjects.get(0);
                ILabelProvider ilp = ModelUtilities.getEMFLabelProvider();
                objectString = ilp.getText(obj);
            } else {
                objectString = "" + selectedEObjects.size(); //$NON-NLS-1$
            }

            //start txn
            boolean requiredStart = ModelerCore.startTxn(true, true, ACTION_DESCRIPTION, this);
            boolean succeeded = false;
            try {
                // Walk through each object and call delete
				RelationshipGlobalActionsManager.delete( selectedEObjects );
                succeeded = true;
            } catch (ModelerCoreException theException) {
                final String msg = Util.getString(PROBLEM,objectString); 
                getPluginUtils().log(IStatus.ERROR, theException, msg);
                setEnabled(false);
            } finally {
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
        setEnabled(false);
    }

}
