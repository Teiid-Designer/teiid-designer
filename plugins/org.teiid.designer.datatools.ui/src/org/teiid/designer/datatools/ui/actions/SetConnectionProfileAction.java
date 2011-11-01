/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.ui.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.datatools.ui.dialogs.SelectConnectionProfileDialog;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class SetConnectionProfileAction extends SortableSelectionAction {
    private static final String label = DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.title"); //$NON-NLS-1$

    private static final String NO_PROFILE_PROVIDER_FOUND_KEY = "NoProfileProviderFound"; //$NON-NLS-1$

    /**
     * @since 5.0
     */
    public SetConnectionProfileAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DatatoolsUiPlugin.getDefault().getImageDescriptor(DatatoolsUiConstants.Images.SET_CONNECTION_ICON));
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
        // select a ConnectionProfile (or create new one)

        // C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
        // via the ConnectionProfileInfoHandler
        IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);

        boolean requiredStart = ModelerCore.startTxn(true, true, "Set Connection Profile", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile(modelFile, true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                SetConnectionProfileAction.setConnectionProfile(modelFile);

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
        	String msg = e.getMessage();
        	if( msg !=  null && msg.equalsIgnoreCase(NO_PROFILE_PROVIDER_FOUND_KEY) ) {
        		MessageDialog.openWarning(getShell(),
	                                    DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.noProvileProviderTitle"), //$NON-NLS-1$
	                                    DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.noProvileProviderMessage")); //$NON-NLS-1$
        	} else {
	            MessageDialog.openError(getShell(),
	                                    DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
	            IStatus status = new Status(IStatus.ERROR, DatatoolsUiConstants.PLUGIN_ID,
	                                        DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.exceptionMessage"), e); //$NON-NLS-1$
	            DatatoolsUiConstants.UTIL.log(status);
        	}

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

    public static boolean setConnectionProfile( IFile modelFile ) throws Exception {

        SelectConnectionProfileDialog dialog = new SelectConnectionProfileDialog(Display.getCurrent().getActiveShell());

        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result != null && result.length == 1) {
                IConnectionProfile profile = (IConnectionProfile)result[0];
                // // TODO: Not sure if we keep this dialog or NOT???
                // boolean doIt = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                //						DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.applyQuestionTitle"), //$NON-NLS-1$
                //						DatatoolsUiConstants.UTIL.getString("SetConnectionProfileAction.applyQuestionText", profile.getName())); //$NON-NLS-1$
                //
                // if( doIt ) {
                SetConnectionProfileAction.setConnectionInfo(modelFile, profile);

                return true;
                // }

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

    public static void setConnectionInfo( IFile model,
                                          IConnectionProfile connectionProfile ) throws Exception {

        ModelResource mr = ModelUtil.getModelResource(model, true);

        ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
        IConnectionInfoProvider provider = manager.getProvider(connectionProfile);

        if (null == provider) {
            throw new Exception(NO_PROFILE_PROVIDER_FOUND_KEY);
        }

        provider.setConnectionInfo(mr, connectionProfile);
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }

}
