package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.runtime.connection.ConnectionInfoHelper;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class SetConnectionProfileAction  extends SortableSelectionAction implements DqpUiConstants {
    private static final String label = DqpUiConstants.UTIL.getString("SetConnectionProfileAction.title"); //$NON-NLS-1$
    //UTIL.getString("BindToConnectorAction.label", SWT.DEFAULT); //$NON-NLS-1$
    
    /**
     * @since 5.0
     */
    public SetConnectionProfileAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return sourceModelSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
    	// A) get the selected model and extract a "ConnectionProfileInfo" from it using the ConnectionProfileInfoHandler
    	
    	// B) Use ConnectionProfileHandler.getConnectionProfile(connectionProfileInfo) to query the user to
    	//    select a ConnectionProfile (or create new one)
    	
    	// C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
    	//    via the ConnectionProfileInfoHandler
    	IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
    	
        boolean requiredStart = ModelerCore.startTxn(true,true,"Set Connection Profile",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	ModelEditor editor = ModelEditorManager.getModelEditorForFile(modelFile, true);
        	boolean isDirty = editor.isDirty();
        	
        	SetConnectionProfileAction.setConnectionProfile(modelFile);
        	
        	if( !isDirty && editor.isDirty()) {
        		editor.doSave(new NullProgressMonitor());
        	}
        	succeeded = true;
        } finally {
            //if we started the txn, commit it.
            if(requiredStart){
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
    
    public static boolean setConnectionProfile(IFile modelFile ) {
    	
		SelectConnectionProfileDialog dialog = new SelectConnectionProfileDialog(Display.getCurrent().getActiveShell());

		dialog.open();

		if( dialog.getReturnCode() == Dialog.OK ) {
			Object[] result = dialog.getResult();
			if( result != null && result.length == 1 ) {
				IConnectionProfile profile = (IConnectionProfile)result[0];
				// TODO: Not sure if we keep this dialog or NOT???
				boolean doIt = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), 
						DqpUiConstants.UTIL.getString("SetConnectionProfileAction.applyQuestionTitle"), //$NON-NLS-1$
						DqpUiConstants.UTIL.getString("SetConnectionProfileAction.applyQuestionText", profile.getName())); //$NON-NLS-1$
				
				if( doIt ) {
					SetConnectionProfileAction.setConnectionInfo(modelFile, profile);
					return true;
				}
				
			}
		}
		
		return false;
    }
    /**
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    private boolean sourceModelSelected( ISelection theSelection ) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelIdentifier.isRelationalSourceModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }
    
    public static void setConnectionInfo(IFile model, IConnectionProfile connectionProfile) {
    	ConnectionInfoHelper helper = new ConnectionInfoHelper();
    	//IFile model = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
    	
    	ModelResource mr = null;
    	
    	try {
			mr = ModelUtilities.getModelResource(model, true);
		} catch (ModelWorkspaceException e) {
			// TODO LOG THIS EXCEPTION
			e.printStackTrace();
		}
    	
		if( mr != null ) {
			helper.setConnectionInfo(mr, connectionProfile);
		} else {
			// TODO: THROW EXCEPTION OR LOG ERROR HERE!!!
		}
    }
    
}
