/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.types.DataTypeManager;
import org.teiid.designer.relational.model.RelationalModel;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.relational.ui.UiPlugin;
import com.metamatrix.modeler.transformation.model.RelationalViewModelFactory;
import com.metamatrix.modeler.transformation.model.RelationalViewProcedure;
import com.metamatrix.modeler.transformation.ui.Messages;
import com.metamatrix.modeler.transformation.ui.editors.EditViewProcedureDialog;
import com.metamatrix.modeler.ui.actions.INewChildAction;
import com.metamatrix.modeler.ui.actions.INewSiblingAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class CreateViewProcedureAction  extends Action implements INewChildAction, INewSiblingAction {
	private IFile selectedModel;
    public static final String TITLE = Messages.createRelationalViewProcedureActionText;
	 
	private Collection<String> datatypes;
    private Properties designerProperties;
    
    private EObject newViewProcedure;
    private RelationalViewProcedure relationalViewProcedure;
	 
	public CreateViewProcedureAction() {
		super(TITLE);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor( UiConstants.Images.NEW_VIRTUAL_PROCEDURE_ICON));
		
		Set<String> unsortedDatatypes = DataTypeManager.getAllDataTypeNames();
		datatypes = new ArrayList<String>();
		
		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
		Arrays.sort(sortedStrings);
		for( String dType : sortedStrings ) {
			datatypes.add(dType);
		}
	}
	
    public CreateViewProcedureAction( Properties properties ) {
        this();
        this.designerProperties = properties;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.INewChildAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    public boolean canCreateChild(EObject parent) {
    	return false;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.INewChildAc
				tion#canCreateChild(org.eclipse.core.resources.IFile)
     */
    public boolean canCreateChild(IFile modelFile) {
    	return isApplicable(new StructuredSelection(modelFile));
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.INewSiblingAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    public boolean canCreateSibling(EObject parent) {
    	//Convert eObject selection to IFile
    	ModelResource mr = ModelUtilities.getModelResourceForModelObject(parent);
    	if( mr != null ) {
    		IFile modelFile = null;
    		
    		try {
				modelFile = (IFile)mr.getCorrespondingResource();
			} catch (ModelWorkspaceException ex) {
				UiConstants.Util.log(ex);
			}
    		if( modelFile != null ) {
    			return isApplicable(new StructuredSelection(modelFile));
    		}
    	}
    	
    	return false;
    }
    
	public boolean isApplicable(ISelection selection) {
		boolean result = false;
		if (!SelectionUtilities.isMultiSelection(selection)) {
			Object obj = SelectionUtilities.getSelectedObject(selection);
			if (obj instanceof IResource) {
				IResource iRes = (IResource) obj;
                if (ModelIdentifier.isRelationalViewModel(iRes)) {
					this.selectedModel = (IFile) obj;
					result = true;
				}
			}
		}

		return result;
	}

	@Override
   public void run() {
        // If properties were passed in, use it's model as the selection - if available
        if (this.designerProperties != null) {
            IFile propsViewModel = DesignerPropertiesUtil.getViewModel(this.designerProperties);
            if (propsViewModel != null) this.selectedModel = propsViewModel;
        }
		if( selectedModel != null ) {
	        ModelResource mr = ModelUtilities.getModelResource(selectedModel);
	        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
	        
            relationalViewProcedure = new RelationalViewProcedure();
	        
	        // Hand the table off to the generic edit dialog
            EditViewProcedureDialog dialog = new EditViewProcedureDialog(shell, relationalViewProcedure, selectedModel);

	        dialog.open();
	        
	        if (dialog.getReturnCode() == Window.OK) {
	        	this.newViewProcedure = createViewProcedureInTxn(mr, relationalViewProcedure);
	        } else {
	        	this.relationalViewProcedure = null;
	        	this.newViewProcedure = null;
	        }
		}
		
	}

    private EObject createViewProcedureInTxn( ModelResource modelResource, RelationalViewProcedure viewProcedure ) {
    	EObject newTable = null;
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, Messages.createRelationalViewTitle, this);
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalViewModelFactory factory = new RelationalViewModelFactory();
                
                RelationalModel relModel = new RelationalModel("dummy"); //$NON-NLS-1$
                relModel.addChild(viewProcedure);
                
                factory.build(modelResource, relModel, new NullProgressMonitor());

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
                
                for( Object child : modelResource.getEObjects() ) {
                	EObject eObj = (EObject)child;
                	if( ModelerCore.getModelEditor().getName(eObj).equalsIgnoreCase(this.relationalViewProcedure.getName()) ) {
                		newTable = eObj;
                		break;
                	}
                }
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                    Messages.createRelationalViewExceptionMessage,
                                    e.getMessage());
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, Messages.createRelationalViewExceptionMessage, e);
            UiConstants.Util.log(status);

            return null;
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
        
        return newTable;
    }
    
    public EObject getNewViewProcedure() {
    	return this.newViewProcedure;
    }
}
