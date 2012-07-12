/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;


/**
 * DeleteAction
 */
public class DeleteAction 
     extends TransformationAction
  implements UiConstants  
 {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "org.teiid.designer.transformation.ui.actions.DeleteAction.problem"; //$NON-NLS-1$
    private static final String ACTION_DESCRIPTION = "Delete From Transformation"; //$NON-NLS-1$
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of DeleteAction.
     * 
     */
    public DeleteAction(EObject transformationEObject, Diagram diagram) {
        super(transformationEObject, diagram);
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
        
        if( !isDependencyDiagram() && areEObjectsSelected()) {
            List sourceEObjects = Collections.EMPTY_LIST;
            
            if (isEObjectSelected()) {
                sourceEObjects = new ArrayList(1);
                Object o = SelectionUtilities.getSelectedObject(selection);
                sourceEObjects.add(o);
            } else if (areEObjectsSelected()) {
                sourceEObjects = SelectionUtilities.getSelectedEObjects(selection);
            }
            if( !sourceEObjects.isEmpty() )
                enable = TransformationGlobalActionsManager.canDelete( getTransformation(), sourceEObjects );
            else
                enable = false;        

        }
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

            //start txn
            boolean requiredStart = ModelerCore.startTxn(true, true, ACTION_DESCRIPTION, this);
            boolean succeeded = false;
            try {
                // Walk through each object and call delete
                TransformationGlobalActionsManager.delete( selectedEObjects );
                succeeded = true;
            } catch (ModelerCoreException theException) {
                final String msg = Util.getString(PROBLEM); 
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
