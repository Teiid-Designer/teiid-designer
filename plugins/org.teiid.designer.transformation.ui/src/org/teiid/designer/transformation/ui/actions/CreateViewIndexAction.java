/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.actions;

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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalViewIndex;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.editor.EditRelationalObjectDialog;
import org.teiid.designer.transformation.model.RelationalViewModelFactory;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.editors.TransformationDialogModel;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.actions.INewChildAction;
import org.teiid.designer.ui.actions.INewSiblingAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *
 */
public class CreateViewIndexAction  extends Action implements INewChildAction, INewSiblingAction {
	private IFile selectedModel;
    /**
     * 
     */
    public static final String TITLE = Messages.createRelationalViewIndexActionText;
	 
	private Collection<String> datatypes;
    private Properties designerProperties;
    
    private EObject newViewIndex;
    private RelationalViewIndex relationalViewIndex;
	 
	/**
	 * 
	 */
	public CreateViewIndexAction() {
		super(TITLE);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor( UiConstants.Images.NEW_INDEX_ICON));
		
		IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
		Set<String> unsortedDatatypes = service.getAllDataTypeNames();
		datatypes = new ArrayList<String>();
		
		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
		Arrays.sort(sortedStrings);
		for( String dType : sortedStrings ) {
			datatypes.add(dType);
		}
	}
	
    /**
     * @param properties the initial properties
     */
    public CreateViewIndexAction( Properties properties ) {
        this();
        this.designerProperties = properties;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewChildAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean canCreateChild(EObject parent) {
    	return false;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewChildAc
				tion#canCreateChild(org.eclipse.core.resources.IFile)
     */
    @Override
	public boolean canCreateChild(IFile modelFile) {
    	return isApplicable(new StructuredSelection(modelFile));
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewSiblingAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    @Override
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
    
	/**
	 * @param selection the selection
	 * @return if action is applicable to selection
	 */
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
	        run(mr);
		}
	}
	
	public void run(ModelResource mr) {
        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        
        relationalViewIndex = new RelationalViewIndex();
        
        // Hand the table off to the generic edit dialog
        TransformationDialogModel dialogModel = new TransformationDialogModel(relationalViewIndex, (IFile)ModelUtilities.getIResource(mr));
        EditRelationalObjectDialog dialog = new EditRelationalObjectDialog(shell, dialogModel);

        dialog.open();
        
        if (dialog.getReturnCode() == Window.OK) {
        	this.newViewIndex = createViewIndexInTxn(mr, relationalViewIndex);
        } else {
        	this.newViewIndex = null;
        	this.relationalViewIndex = null;
        }
	}

    private EObject createViewIndexInTxn( ModelResource modelResource, RelationalViewIndex viewIndex ) {
    	EObject newTable = null;
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, Messages.createRelationalViewIndexTitle, this);
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalViewModelFactory factory = new RelationalViewModelFactory();
                
                RelationalModel relModel = new RelationalModel("dummy"); //$NON-NLS-1$
                relModel.addChild(viewIndex);
                
                factory.build(modelResource, relModel, new NullProgressMonitor());

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
                
                for( Object child : modelResource.getEObjects() ) {
                	EObject eObj = (EObject)child;
                	if( ModelerCore.getModelEditor().getName(eObj).equalsIgnoreCase(this.relationalViewIndex.getName()) ) {
                		newTable = eObj;
                		break;
                	}
                }
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                    Messages.createRelationalViewIndexExceptionMessage,
                                    e.getMessage());
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, Messages.createRelationalViewIndexExceptionMessage, e);
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
    
    /**
     * @return the newly created Index EObject
     */
    public EObject getNewViewIndex() {
    	return this.newViewIndex;
    }
}
