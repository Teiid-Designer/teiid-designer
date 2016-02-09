/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.functions.ConvertFunctionModelDialog;
import org.teiid.designer.transformation.ui.wizards.functions.ConvertFunctionModelHelper;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;

public class ConvertFunctionsToVirtualProceduresAction extends SortableSelectionAction {
	
    public static final String OPEN_EDITOR_TITLE = UiConstants.Util.getString("ConvertFunctionsToVirtualProceduresAction.openModelEditorTitle"); //$NON-NLS-1$
    public static final String OPEN_EDITOR_MESSAGE = UiConstants.Util.getString("ConvertFunctionsToVirtualProceduresAction.openModelEditorMessage"); //$NON-NLS-1$
    public static final String ALWAY_FORCE_OPEN_MESSAGE = UiConstants.Util.getString("ConvertFunctionsToVirtualProceduresAction.alwaysForceOpenMessage"); //$NON-NLS-1$
	
    public ConvertFunctionsToVirtualProceduresAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
        		org.teiid.designer.transformation.ui.PluginConstants.Images.CREATE_MATERIALIZED_VIEWS_ICON));
    }
    
    @Override
    public boolean isValidSelection(ISelection selection) {
        // Enable for single/multiple Virtual Tables
        return functionModelSelected(selection);
    }
    
    @Override
    public void run() {
        ISelection cachedSelection = getSelection();
        if( cachedSelection != null && !cachedSelection.isEmpty() ) {
            Object selectedObj = SelectionUtilities.getSelectedObject(cachedSelection);
            if( selectedObj != null && selectedObj instanceof IFile) {
                ModelResource functionModel = null;
                try {
                	functionModel = ModelUtil.getModelResource(((IFile) selectedObj), false);
                    if( functionModel != null ) {
                    	// FUNCTION MODEL IS SELECTED SO LET's PROCESS
                    	
                        ConvertFunctionModelHelper helper = new ConvertFunctionModelHelper(functionModel);
                        
                        System.out.println("ConvertFunctionsToVirtualProceduresAction.run() called.  Model = " + functionModel.getItemName());
                        
                        ConvertFunctionModelDialog dialog = new ConvertFunctionModelDialog(Display.getCurrent().getActiveShell(), helper);
                        
                        if( dialog.open() == Dialog.OK ) {
                        	helper.generateProcedures();
                        }
                    }
                } catch (ModelWorkspaceException e) {
                    UiConstants.Util.log(e);
                }
            }
            
        }
        selectionChanged(null, new StructuredSelection());
    }
    
    @Override
    public boolean isApplicable(ISelection selection) {
        return functionModelSelected(selection);
    }
    
    private boolean functionModelSelected(ISelection theSelection) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if( !allObjs.isEmpty() && allObjs.size() == 1 ) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while( iter.hasNext() && result ) {
                nextObj = iter.next();
                
                if( nextObj instanceof IFile ) {
                    result = ModelIdentifier.isFunctionModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }
        
        return result;
    }
}
