/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.EditTransformationHelper;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * Provides means to edit a specific transformation based on a specific transformation target obtained by querying the user
 * to select from a list of available transformation targets within a specified ModelResource
 * @since 5.0
 */
public class EditTransformationAction extends SortableSelectionAction implements UiConstants {
    private static final String label = Util.getString("TransformationObjectEditorPage.editTransformationsId", SWT.DEFAULT); //$NON-NLS-1$
    private static final String tooltip = Util.getString("TransformationObjectEditorPage.editTransformationsTooltip", SWT.DEFAULT); //$NON-NLS-1$
    /** 
     * 
     * @since 5.0
     */
    public EditTransformationAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.EDIT_TRANSFORMATION_ICON));
        setToolTipText(tooltip);
    }
    @Override
    public boolean isValidSelection(ISelection selection) {
        // Enable for single/multiple Virtual Tables
        return virtualModelSelected(selection);
    }
    
    @Override
    public void run() {
        ISelection cachedSelection = getSelection();
        if( cachedSelection != null && !cachedSelection.isEmpty() ) {
            Object selectedObj = SelectionUtilities.getSelectedObject(cachedSelection);
            if( selectedObj != null && selectedObj instanceof IFile) {
                ModelResource modelResource = null;
                try {
                    modelResource = ModelUtilities.getModelResource(((IFile) selectedObj), false);
                    if( modelResource != null ) {
                        EditTransformationHelper helper = new EditTransformationHelper(modelResource);
                        
                        EObject target = helper.queryUserToSelectTarget();
                        if( target != null ) {
                            helper.openAndEdit(target);
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
        return virtualModelSelected(selection);
    }
    
    private boolean virtualModelSelected(ISelection theSelection) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if( !allObjs.isEmpty() && allObjs.size() == 1 ) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while( iter.hasNext() && result ) {
                nextObj = iter.next();
                
                if( nextObj instanceof IFile ) {
                    result = ModelIdentifier.isRelationalViewModel((IFile)nextObj) ||
                             ModelIdentifier.isXmlViewModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }
        
        return result;
    }
}
