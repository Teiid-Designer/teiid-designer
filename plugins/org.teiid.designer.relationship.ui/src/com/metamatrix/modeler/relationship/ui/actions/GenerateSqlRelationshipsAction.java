/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.relationship.ui.wizards.GenerateSqlRelationshipsWizard;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class GenerateSqlRelationshipsAction extends SortableSelectionAction implements UiConstants {
    
    
	public GenerateSqlRelationshipsAction() {
		super();
	}
	
    @Override
    public boolean isValidSelection(ISelection selection) {
    	// Enable for single/multiple Virtual Tables
    	if( allVirtualTablesAndModelsSelected(selection ) ) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean allEObjects(ISelection selection) {
    	boolean result = false;
    	List allObjs = SelectionUtilities.getSelectedObjects(selection);
    	if( !allObjs.isEmpty() ) {
    		Iterator iter = allObjs.iterator();
    		result = true;
    		while( iter.hasNext() && result ) {
    			if( !(iter.next() instanceof EObject) ) {
    				result = false;
    			}
    		}
    	}
    	
    	return result;
    }
    
    @Override
    public void run() {
    	ISelection cachedSelection = getSelection();
    	if( cachedSelection != null && !cachedSelection.isEmpty() ) {
    		ISelection initWizardSelection = cachedSelection;
    		if( !allEObjects(cachedSelection) ) {
    			initWizardSelection = getAllVirtualTablesSelection(cachedSelection);
    		}
            final GenerateSqlRelationshipsWizard wizard = new GenerateSqlRelationshipsWizard();
            wizard.init(UiPlugin.getDefault().getCurrentWorkbenchWindow().getWorkbench(), (IStructuredSelection)initWizardSelection );
            final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
            dialog.open();
    	}
	    selectionChanged(null, new StructuredSelection());
    }
    
	@Override
    public boolean isApplicable(ISelection selection) {
		return allVirtualTablesAndModelsSelected(selection);
	}
	
	private ISelection getAllVirtualTablesSelection(ISelection cachedSelection) {
		Collection tableList = new ArrayList();
		try {
			// First check selected Resources
			List resourceObjects = SelectionUtilities.getSelectedIResourceObjects(cachedSelection);
			for( Iterator iter = resourceObjects.iterator(); iter.hasNext(); ) {
				Collection vTables = getVirtualTablesForResource(ModelUtilities.getModelResource(((IFile) iter.next()), false));
				if( !vTables.isEmpty() ) {
					tableList.addAll(vTables);
				}
			}
			
			// Now we get All EObjects and assume they are all VTables
			Collection eObjs = SelectionUtilities.getSelectedEObjects(cachedSelection);
			if( !eObjs.isEmpty() ) {
				tableList.addAll(eObjs);
			}
			
		} catch (ModelWorkspaceException e) {
			UiConstants.Util.log(e);
		}
		if( tableList.isEmpty() )
			return new StructuredSelection();
		
		return new StructuredSelection(tableList);
	}
	
	private boolean allVirtualTablesAndModelsSelected(ISelection theSelection) {
		boolean result = false;
    	List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
    	if( !allObjs.isEmpty() ) {
    		Iterator iter = allObjs.iterator();
    		result = true;
    		Object nextObj = null;
    		while( iter.hasNext() && result ) {
    			nextObj = iter.next();
    			if( nextObj instanceof EObject ) {
    				if( !TransformationHelper.isVirtualSqlTable(nextObj) || TransformationHelper.isXmlDocument(nextObj)) {
    					result = false;
    				}
    			} else if( nextObj instanceof IFile && ModelUtilities.isModelFile((IResource) nextObj) ) {
    				if (  ModelUtilities.isModelFile((IResource) nextObj) ) {
    		            try {
    		                ModelResource modelResource = ModelUtilities.getModelResource(((IFile) nextObj), false);
    		                if ( !ModelUtilities.isVirtual(modelResource) ) {
    		                	result = false;
    		                }
    		            } catch (ModelWorkspaceException e) {
    		                UiConstants.Util.log(e);
    		            }
    		        }
    			} else {
    				result = false;
    			}
    		}
    	}
		
		return result;
	}
	
	private Collection getVirtualTablesForResource(ModelResource modelResource) throws ModelWorkspaceException {
		Collection vTables = new ArrayList();
		
		List transformations = modelResource.getModelTransformations().getTransformations();
		
		for( Iterator iter = transformations.iterator(); iter.hasNext(); ) {
            Object obj = iter.next();
            if( obj instanceof SqlTransformationMappingRoot ) {
                vTables.add( ((SqlTransformationMappingRoot)obj).getTarget());
            }
		}
		
		return vTables;
	}
}
