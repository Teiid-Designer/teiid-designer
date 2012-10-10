/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.actions;

import java.util.ArrayList;
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
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * PasteAction
 *
 * @since 8.0
 */
public class PasteAction 
     extends MappingAction
  implements UiConstants  
 {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM   = "PasteAction.problem"; //$NON-NLS-1$
    private static final String UNDO_TEXT = "PasteAction.undoText"; //$NON-NLS-1$
    
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of PasteAction.
     * 
     */
    public PasteAction(EObject transformationEObject) {
        super(transformationEObject);
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
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
//        System.out.println("[transformation.ui.actions.PasteAction.doRun] TOP"); //$NON-NLS-1$
        final EObject obj = SelectionUtilities.getSelectedEObject(getSelection());
        
        String objectString = null;

        String description = null;
        if (obj != null) {
            ILabelProvider ilp = ModelUtilities.getEMFLabelProvider();
            objectString = ilp.getText(obj);
            description = getPluginUtils().getString(UNDO_TEXT, objectString);
        }

        boolean requiredStart = ModelerCore.startTxn(true, true, description, this);
        boolean succeeded = false;
        try {
            ModelerCore.getModelEditor().pasteFromClipboard(obj);
            succeeded = true;
        } catch (ModelerCoreException theException) {
            String msg = getPluginUtils().getString(PROBLEM, objectString); 
            getPluginUtils().log(IStatus.ERROR, theException, msg);
            setEnabled(false);
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
        determineEnablement();
    }
        
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;
        
        if( !isReadOnly() ) {
            List sourceEObjects = null;
            EObject targetEObject = null;
            if (SelectionUtilities.isSingleSelection(getSelection())) {
                sourceEObjects = new ArrayList(1);
                targetEObject = SelectionUtilities.getSelectedEObject(getSelection());
                sourceEObjects.add(targetEObject);
            }
            if( targetEObject != null ) {
                if( getTransformation() != null )
                    enable = (MappingGlobalActionsManager.canPaste(getTransformation(), sourceEObjects) &&
                              ModelerCore.getModelEditor().isValidPasteParent(targetEObject));
                else
                    enable = ModelerCore.getModelEditor().isValidPasteParent(targetEObject);
            }
        }
        setEnabled(enable);
    }
}
