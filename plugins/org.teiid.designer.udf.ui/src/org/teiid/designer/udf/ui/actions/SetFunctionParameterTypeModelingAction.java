/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf.ui.actions;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.metamodels.function.FunctionParameter;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeSelectionDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * Action to query the user to select an existing datatype to apply the selected function parameters.
 * This was cloned and tweaked from the SetDatatypeModelingAction which dealt with SQL Aspects of which the "Function
 * Model" knows nothing about.
 * 
 * @since 5.0
 */
public class SetFunctionParameterTypeModelingAction extends SortableSelectionAction  {
    
    /** 
     * 
     * @since 5.0
     */
    public SetFunctionParameterTypeModelingAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.BUILTIN_DATATYPE));
    }
    /**
     *  
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection(ISelection selection) {
        boolean isValid = false;
        
        if (SelectionUtilities.isAllEObjects(selection) ) {
            if( !isValid ) {
                isValid = allSelectedAreFunctionParameters(selection);
            }
            
            if( isValid ) {
            	isValid = allSelectedHaveEditorsOpen(selection);
            }
        }
        
        return isValid;
    }
    
    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        Collection selectedEObjects = new ArrayList(SelectionUtilities.getSelectedEObjects(getSelection()));
        
        if (!selectedEObjects.isEmpty()) {
            showDialog(selectedEObjects);
        }
    }
    
    /** 
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable(ISelection selection) {
        return isValidSelection(selection);
    }
    
    private boolean allSelectedAreFunctionParameters(ISelection selection) {
        boolean result = true;
        Iterator iter = SelectionUtilities.getSelectedEObjects(selection).iterator();
        EObject nextEObj = null;

        while( iter.hasNext()  && result) {
            nextEObj = (EObject)iter.next();
            if( ModelObjectUtilities.isReadOnly(nextEObj))
                result = false;
            if( result ) {
                if( !( nextEObj instanceof FunctionParameter ) ) {
                	result = false;
                }
            }
        }
        
        return result;
    }
    
    private boolean allSelectedHaveEditorsOpen(ISelection selection) {
    	boolean result = true;
        Iterator iter = SelectionUtilities.getSelectedEObjects(selection).iterator();
        EObject nextEObj = null;
        while( iter.hasNext()  && result) {
        	nextEObj = (EObject)iter.next();
        	
        	result = ModelEditorManager.isOpen(nextEObj);
        }
        
        return result;
    }
    /**
     *  
     * @param theEObjects collection of <code>EObject</code>s; cannot be null or empty.
     * @return
     * @since 4.2
     */
    protected void showDialog(Collection theEObjects) {
        int length = 0;
        EObject eObj = (EObject)theEObjects.iterator().next();
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

        // configure dialog
        DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(shell, eObj);

        // If multiple objects selected, then tell the dialog
        if( theEObjects.size() > 1 ) {
            dialog.setMultipleObjects(true);
        } else {
            dialog.setMultipleObjects(false);
        }
        
        
        FunctionParameter param = (FunctionParameter)eObj;
        Object originalValue = param.getType();
        Object[] selection = new Object[] { originalValue };
        dialog.setInitialSelections(selection);
        dialog.setEditLength(false);
        
        // show dialog
        int status = dialog.open();

        // process dialog
        if (status == Window.OK) {
            Object newType = null;
            Object[] result = dialog.getResult();

            // return the selected value
            if (result.length > 0) {
                newType = result[0];
            }
            setDatatypesForFunctionParameters(theEObjects, (EObject)newType, length, dialog.overrideAllLengths() );
        }

    }
    
    /**
     * Set the datatype for the supplied function parameters. 
     * @param eObject the supplied EObject
     * @param datatype the Datatype
     */
    private void setDatatypesForFunctionParameters(Collection params, EObject datatype, int length, boolean overrideAllLengths) {
        FunctionParameter nextParam = null;
        Iterator iter = params.iterator();

        
        boolean requiredStart = ModelerCore.startTxn(true,true,"Set Datatype For Function Parameters",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            while( iter.hasNext() ) {
            	nextParam = (FunctionParameter)iter.next();

            	nextParam.setType(ModelerCore.getModelEditor().getName(datatype));
            }
            succeeded = true;
        } finally {
            //if we started the txn, commit it.
            if(requiredStart){
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
}
