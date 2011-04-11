/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.extension.ExtensionPropertiesManager;
import org.teiid.designer.extension.manager.ExtendedModelObject;
import org.teiid.designer.extension.manager.IExtensionPropertiesHandler;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.actions.dialogs.EditExtensionPropertiesDialog;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * Action designed to allow editing of the new Model Extension properties based on the {@link IExtensionPropertiesHandler} framework
 * 
 * The action is intended to be available whenever the selected object contains a model extension type AND the particular object
 * allows extended properties. (See isApplicable() method)
 * 
 */
public class EditExtensionPropertiesAction  extends SortableSelectionAction  {

    /**
     * @since 5.0
     */
    public EditExtensionPropertiesAction() {
        super(NLS.bind(Messages.EditExtensionPropertiesAction_title, null), SWT.DEFAULT);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.EDIT_EXTENSION_PROPERTIES_ICON));
    }

    public EditExtensionPropertiesAction(String text, int style) {
		super(text, style);
	}

	/**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return selectionHasExtendedProperties(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        EObject targetEObject = SelectionUtilities.getSelectedEObject(getSelection());
        editInTransaction(targetEObject);
    }
    
    private void editInTransaction(EObject targetEObject) {
    	String targetName = targetEObject.toString();
        boolean requiredStart = ModelerCore.startTxn(true, true, "Edit Extension Properties", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	ModelResource mr = ModelUtilities.getModelResource(targetEObject);
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)mr.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

            	ExtendedModelObject extendedMO = ExtensionPropertiesManager.getExtendedModelObject(targetEObject);
            	
            	if( extendedMO != null ) {
    	        	EditExtensionPropertiesDialog dialog = new EditExtensionPropertiesDialog(getShell(), extendedMO);
    	        	
    	        	if( dialog.open() == Window.OK )  {
    	        		if( dialog.isChanged() ) {
    							extendedMO.saveChanges();
    	        		}
    	        	}
            	}

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
                                        NLS.bind(Messages.EditExtensionPropertiesAction_exceptionMessage, targetName), e); 
            UiConstants.Util.log(status);

            return;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
    	boolean result = selectionHasExtendedProperties(selection);
    	if( result ) {
    		String name = getExtendedPropertiesName(selection);
    		if( name != null ) {
    			this.setText(NLS.bind(Messages.EditExtensionPropertiesAction_customTitle,  name ));
    		} else {
    			this.setText(NLS.bind(Messages.EditExtensionPropertiesAction_title, null));
    		}
    		return true;
    	}
    	
    	return false;
    }

    private boolean selectionHasExtendedProperties( ISelection theSelection ) {
        EObject targetEObject = SelectionUtilities.getSelectedEObject(theSelection);
        if(  targetEObject != null ) {
        	return ExtensionPropertiesManager.isApplicable(targetEObject);
        }
        
        return false;
    }
    
    private String getExtendedPropertiesName(ISelection theSelection) {
        EObject targetEObject = SelectionUtilities.getSelectedEObject(theSelection);
        if(  targetEObject != null ) {
        	return ExtensionPropertiesManager.getDisplayName(targetEObject);
        }
        
        return null;
    }

    protected Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
