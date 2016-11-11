package org.teiid.designer.transformation.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.transformation.reverseeng.ReverseEngConstants.Mode;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.jdg.MaterializationWizard;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class MaterializationAction extends SortableSelectionAction {
	
    public MaterializationAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
        		org.teiid.designer.transformation.ui.PluginConstants.Images.CREATE_MATERIALIZED_VIEWS_ICON));
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
            
            if ( isRelationalVirtualTable(eObject) && TransformationHelper.isVirtualSqlTable(eObject) ) {
            	isValid = !((Table)eObject).isMaterialized();
            } else isValid = false;

        } else isValid = false;

        return isValid;
    }
    
    private boolean isRelationalVirtualTable( EObject eObject ) {
    	// Do a quick object check
    	if( TransformationHelper.isVirtualSqlTable(eObject)) {
    		// make sure it's a virtual relational model
	        final Resource resource = eObject.eResource();
	        if (resource != null ) {
	        	ModelResource mr = ModelUtilities.getModelResource(resource, true);
	        	return ModelIdentifier.isRelationalViewModel(mr);
	        }
    	}
        return false;
    }
    
    @Override
    public void run() {
        final IWorkbenchWindow iww = UiPlugin.getDefault().getCurrentWorkbenchWindow();

        final MaterializationWizard wizard = new MaterializationWizard(Mode.MATERIALIZE);
        wizard.init(iww.getWorkbench(), new StructuredSelection(SelectionUtilities.getSelectedObjects(getSelection())));
        final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
        final int rc = dialog.open();
        
        if( rc == Window.OK ) {
        	
        }
    	
    }
}