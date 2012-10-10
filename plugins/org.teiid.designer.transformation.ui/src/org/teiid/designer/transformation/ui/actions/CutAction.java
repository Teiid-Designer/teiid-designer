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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * DeleteAction
 *
 * @since 8.0
 */
public class CutAction 
     extends TransformationAction
  implements UiConstants  
 {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "CutAction.problem"; //$NON-NLS-1$
    private static final String UNDO_TEXT = "CutAction.undoText"; //$NON-NLS-1$
    private static final String PLURAL_UNDO_TEXT = "CutAction.pluralUndoText"; //$NON-NLS-1$
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of DeleteAction.
     * 
     */
    public CutAction(EObject transformationEObject, Diagram diagram) {
        super(transformationEObject, diagram);
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
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
        determineEnablement();
    }

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {

        String objectString = null;
        List lstObjs = SelectionUtilities.getSelectedEObjects(getSelection());
        String description = null;
        if ( lstObjs.size() == 1 ) {
            EObject obj = (EObject) lstObjs.get(0);
            ILabelProvider ilp = ModelUtilities.getEMFLabelProvider();                 
            objectString = ilp.getText(obj);
            description = getPluginUtils().getString(UNDO_TEXT, objectString);
        } else {
            objectString = "" + lstObjs.size(); //$NON-NLS-1$
            description = getPluginUtils().getString(PLURAL_UNDO_TEXT,objectString);
        }

        boolean requiredStart = ModelerCore.startTxn(true, true, description, this);
        boolean succeeded = false;
        try {
            ModelerCore.getModelEditor().cutAllToClipboard(lstObjs); 
            succeeded = true;
        } catch (ModelerCoreException theException) {
            String msg = getPluginUtils().getString(PROBLEM,  objectString); 
            getPluginUtils().log(IStatus.ERROR, theException, msg);
            setEnabled(false);
        }finally {
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        setEnabled(false);
    }
    
    /*
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;
        if( !isDependencyDiagram() && areEObjectsSelected() && !isReadOnly() ) {
            List sourceEObjects = Collections.EMPTY_LIST;
            if (isEObjectSelected()) {
                sourceEObjects = new ArrayList(1);
                Object o = SelectionUtilities.getSelectedEObject(getSelection());
                sourceEObjects.add(o);
            } else if (areEObjectsSelected()) {
                sourceEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            }
            if( !sourceEObjects.isEmpty() )
                enable = TransformationGlobalActionsManager.canCut(getTransformation(), sourceEObjects );
            else
                enable = false;
        }
        setEnabled(enable);
    }
}
