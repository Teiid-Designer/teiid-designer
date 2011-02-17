package org.teiid.designer.datatools.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * This action allows users to clear/remove all connection profile and translator info from a source model.
 * 
 * This should be and can be run prior to final VDB creation if users wish to keep the Source DB meta-data out of the VDB.
 * 
 * I can also be used to remove this same info prior to exporting a model project set which will be imported/used by
 * other users.
 * 
 *
 */
public class RemoveConnectionInfoAction extends SortableSelectionAction {
    private static final String label = DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.title"); //$NON-NLS-1$

    private ConnectionInfoHelper helper;

    /**
     * @since 5.0
     */
    public RemoveConnectionInfoAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DatatoolsUiPlugin.getDefault().getImageDescriptor(DatatoolsUiConstants.Images.REMOVE_CONNECTION_ICON));
        helper = new ConnectionInfoHelper();
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return sourceModelSelected(selection) && hasConnectionInfo(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);

        boolean requiredStart = ModelerCore.startTxn(true, true, "Remove Connection Info", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile(modelFile, true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                ModelResource modelResource = ModelUtil.getModelResource(modelFile, true);
            	helper.clearConnectionInfo(modelResource);

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
            MessageDialog.openError(getShell(),
                                    DatatoolsUiConstants.UTIL.getString("RemoveConnectionInfoAction.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, DatatoolsUiConstants.PLUGIN_ID,
                                        DatatoolsUiConstants.UTIL.getString("RemoveConnectionInfoAction.exceptionMessage"), e); //$NON-NLS-1$
            DatatoolsUiConstants.UTIL.log(status);

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
        return sourceModelSelected(selection) && hasConnectionInfo(selection);
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

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
    
    private boolean hasConnectionInfo(ISelection selection) {
    	
    	try {
			ModelResource mr = getSelectedModel(selection);
			
			return helper.hasConnectionInfo(mr);
		} catch (ModelWorkspaceException e) {
			IStatus status = new Status(IStatus.ERROR, DatatoolsUiConstants.PLUGIN_ID,
                    DatatoolsUiConstants.UTIL.getString("RemoveConnectionInfoAction.exceptionMessage"), e); //$NON-NLS-1$
			DatatoolsUiConstants.UTIL.log(status);
		}

		return false;
    }
    
    private ModelResource getSelectedModel(ISelection selection) throws ModelWorkspaceException {
    	IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(selection).get(0);
    	return ModelUtil.getModelResource(modelFile, true);
    }
}
