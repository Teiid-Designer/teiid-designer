/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

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
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * CopyAction
 */
public class CopyAction 
     extends MappingAction
  implements UiConstants  
 {
    //============================================================================================================================
    // Constants
    
    private static final String PROBLEM = "CopyAction.problem"; //$NON-NLS-1$
    
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of CopyAction.
     * 
     */
    public CopyAction(EObject transformationEObject) {
        super(transformationEObject);
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
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
        if (lstObjs.size() == 1) {
            EObject obj = (EObject)lstObjs.get(0);
            ILabelProvider ilp = ModelUtilities.getEMFLabelProvider();
            objectString = ilp.getText(obj);
        } else {
            objectString = "" + lstObjs.size(); //$NON-NLS-1$
        }
        
        try {
            if( lstObjs.size() == 1 ) {
                EObject obj = (EObject)lstObjs.get(0);
                ModelerCore.getModelEditor().copyToClipboard(obj); 
            } else {
                ModelerCore.getModelEditor().copyAllToClipboard(lstObjs);
            }
        } catch (final ModelerCoreException err) {
            final String msg = Util.getString(PROBLEM, objectString); 
            getPluginUtils().log(IStatus.ERROR, err, msg);
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
            if (SelectionUtilities.isSingleSelection(getSelection())) {
                sourceEObjects = new ArrayList(1);
                Object o = SelectionUtilities.getSelectedObject(getSelection());
                sourceEObjects.add(o);
            } else if (SelectionUtilities.isMultiSelection(getSelection())) {
                sourceEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            }
                     
            enable = MappingGlobalActionsManager.canCopy(sourceEObjects );
        }
        setEnabled(enable);
    }
    
    

}
