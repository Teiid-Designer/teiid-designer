/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.wizards.CreateMaterializedViewWizard;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.Dialog;

public class CreateMaterializedViewsAction extends SortableSelectionAction {
	
    public static final String OPEN_EDITOR_TITLE = UiConstants.Util.getString("CreateMaterializedViewsAction.openModelEditorTitle"); //$NON-NLS-1$
    public static final String OPEN_EDITOR_MESSAGE = UiConstants.Util.getString("CreateMaterializedViewsAction.openModelEditorMessage"); //$NON-NLS-1$
    public static final String ALWAY_FORCE_OPEN_MESSAGE = UiConstants.Util.getString("CreateMaterializedViewsAction.alwaysForceOpenMessage"); //$NON-NLS-1$
	
    public CreateMaterializedViewsAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
        		com.metamatrix.modeler.transformation.ui.PluginConstants.Images.CREATE_MATERIALIZED_VIEWS_ICON));
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
        if (SelectionUtilities.isEmptySelection(selection) || !SelectionUtilities.isAllEObjects(selection)) isValid = false;

        if (isValid ) {
            final Collection objs = SelectionUtilities.getSelectedEObjects(selection);
            final Iterator selections = objs.iterator();
            while (selections.hasNext() && isValid) {
                final EObject next = (EObject)selections.next();
                
                if ( isRelationalVirtualTable(next) && TransformationHelper.isVirtualSqlTable(next) ) {
                	isValid = true;
                } else isValid = false;

                // stop processing if no longer valid:
                if (!isValid) break;
            } // endwhile -- all selected
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
        
        // Present the user a dialog to select or create a new physical relational model to store the materialized views in
        
        EObject firstEObj = (EObject)SelectionUtilities.getSelectedEObjects(getSelection()).get(0);
        
        boolean userCancelled = false;
        
        if (!ModelEditorManager.isOpen(firstEObj)) {
            // Let's get the preferenced value for auto-open-editor
            String autoOpen = UiPlugin.getDefault().getPreferenceStore().getString(PluginConstants.Prefs.General.AUTO_OPEN_EDITOR_IF_NEEDED);
            // if the preference is to auto-open, then set forceOpen so we don't prompt the user
            boolean forceOpen = false;
            if (autoOpen.equals(MessageDialogWithToggle.ALWAYS)) {
                forceOpen = true;
            } else if (autoOpen.equals(MessageDialogWithToggle.NEVER)) {
                forceOpen = false;
            }

            if (!forceOpen) {
                // can't modify a property value on an EObject if it's ModelEditor is not open.
                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                MessageDialogWithToggle tDialog = MessageDialogWithToggle.openYesNoCancelQuestion(shell,
                                                                                                  OPEN_EDITOR_TITLE,
                                                                                                  OPEN_EDITOR_MESSAGE,
                                                                                                  ALWAY_FORCE_OPEN_MESSAGE,
                                                                                                  false,
                                                                                                  UiPlugin.getDefault().getPreferenceStore(),
                                                                                                  PluginConstants.Prefs.General.AUTO_OPEN_EDITOR_IF_NEEDED);
                int result = tDialog.getReturnCode();
                switch (result) {
                    // yes, ok
                    case IDialogConstants.YES_ID:
                    case IDialogConstants.OK_ID:
                        forceOpen = true;
                        break;
                    // no
                    case IDialogConstants.NO_ID:
                        forceOpen = false;
                        userCancelled = true;
                        break;
                }
            }

            if (forceOpen) {
                ModelEditorManager.open((EObject)firstEObj, true);
            }
        }
        if( userCancelled ) {
        	return;
        	
        }

        final CreateMaterializedViewWizard wizard = new CreateMaterializedViewWizard();
        wizard.init(iww.getWorkbench(), new StructuredSelection(SelectionUtilities.getSelectedObjects(getSelection())));
        final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
        final int rc = dialog.open();
        
        if( rc == Dialog.OK ) {
        	
        }
    	
    	//runAsJob(selectedTables);
    }
}
