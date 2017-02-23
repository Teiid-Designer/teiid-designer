/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.builder.view;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class CreateViewFromTableAction extends SortableSelectionAction implements RelationalConstants, ISQLConstants {
    
    public CreateViewFromTableAction() {
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
        boolean isValid = true;
        
        if (SelectionUtilities.isEmptySelection(selection) || 
        		!SelectionUtilities.isAllEObjects(selection) ||
        		SelectionUtilities.getSelectedEObjects(selection).size() > 1) isValid = false;
        
        if (isValid ) {
        	final EObject eObject = SelectionUtilities.getSelectedEObject(selection);
            
            if ( isRelationalTable(eObject)) {
            	isValid = true;
            } else isValid = false;

        } else isValid = false;

        return isValid;
    }
    
    private boolean isRelationalTable( EObject eObject ) {
    	// Do a quick object check
    	if( eObject instanceof Table) {
    		// make sure it's a virtual relational model
	        final Resource resource = eObject.eResource();
	        if (resource != null ) {
	        	ModelResource mr = ModelUtilities.getModelResource(resource, true);
	        	return ModelIdentifier.isRelationalViewModel(mr) || ModelIdentifier.isRelationalSourceModel(mr);
	        }
    	}
        return false;
    }
    
    @Override
    public void run() {
        final EObject table = SelectionUtilities.getSelectedEObject(getSelection());
        ViewBuilderManager builder = new ViewBuilderManager((BaseTable)table);
       
        builder.run();
    }
}