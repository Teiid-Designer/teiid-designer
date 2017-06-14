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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.EditTransformationHelper;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;



/** 
 * Provides means to edit a specific transformation based on a specific transformation target obtained by querying the user
 * to select from a list of available transformation targets within a specified ModelResource
 * @since 8.0
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
        setId(ModelActionConstants.Resource.EDIT_TRANSFORMATION);
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
                    modelResource = ModelUtil.getModelResource(((IFile) selectedObj), false);
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
