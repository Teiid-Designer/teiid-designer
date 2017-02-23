/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.builder.view;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class CreateViewInModelAction extends SortableSelectionAction implements RelationalConstants, ISQLConstants {
    
    public CreateViewInModelAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.VIRTUAL_RELATIONAL_TABLE));
    }
    
    /**
     * 
     */
    @Override
    public boolean isApplicable( final ISelection selection ) {
        return isValidSelection(selection);
    }
    
    /**
     * Valid selections include only Virtual Relational Tables.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( final ISelection selection ) {
        return viewModelSelected(selection);
    }
    
    private boolean viewModelSelected( ISelection theSelection ) {
        boolean result = false;
        List<?> allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator<?> iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelIdentifier.isRelationalViewModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }
    
    @Override
    public void run() {
        final Object iFile = SelectionUtilities.getSelectedObject(getSelection());
        ModelResource viewModel =  ModelUtilities.getModelResource(iFile);
        ViewBuilderManager builder = new ViewBuilderManager(viewModel);
       
        builder.run();
    }
}