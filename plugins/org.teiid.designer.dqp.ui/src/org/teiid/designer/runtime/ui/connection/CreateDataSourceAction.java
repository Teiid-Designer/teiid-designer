package org.teiid.designer.runtime.ui.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;
import com.metamatrix.ui.internal.dialog.AbstractPasswordDialog;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class CreateDataSourceAction extends SortableSelectionAction implements DqpUiConstants {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(CreateDataSourceAction.class);
    private static final String label = DqpUiConstants.UTIL.getString("label"); //$NON-NLS-1$

    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object value ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value);
    }

    private String pwd;
    private ConnectionInfoProviderFactory providerFactory;

    private ExecutionAdmin cachedAdmin;

    /**
     * @since 5.0
     */
    public CreateDataSourceAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
        providerFactory = new ConnectionInfoProviderFactory();
    }

    public void setAdmin( ExecutionAdmin admin ) {
        this.cachedAdmin = admin;
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
        final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
        // A) get the selected model and extract a "ConnectionProfileInfo" from it using the ConnectionProfileInfoHandler

        // B) Use ConnectionProfileHandler.getConnectionProfile(connectionProfileInfo) to query the user to
        // select a ConnectionProfile (or create new one)

        // C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
        // via the ConnectionProfileInfoHandler
        ModelResource modelResource = null;
        if (!getSelection().isEmpty()) {
            IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
            modelResource = ModelUtilities.getModelResource(modelFile);
        }
        try {

            ExecutionAdmin executionAdmin = cachedAdmin;
            if (executionAdmin == null) {
                if (DqpPlugin.getInstance().getServerManager().getDefaultServer() == null) {
                    MessageDialog.openConfirm(iww.getShell(), getString("noServer.title"), //$NON-NLS-1$
                                              getString("noServer.message")); //$NON-NLS-1$
                    return;
                } else if (DqpPlugin.getInstance().getServerManager().getDefaultServer().isConnected()) {
                    executionAdmin = DqpPlugin.getInstance().getServerManager().getDefaultServer().getAdmin();
                } else {
                    MessageDialog.openConfirm(iww.getShell(), getString("noServerConnection.title"), //$NON-NLS-1$
                                              getString("noServerConnection.message")); //$NON-NLS-1$
                    return;
                }

            }

            Collection<ModelResource> relationalModels = getRelationalModelsWithConnections();
            final CreateDataSourceWizard wizard = new CreateDataSourceWizard(executionAdmin, relationalModels, modelResource);

            wizard.init(iww.getWorkbench(), new StructuredSelection());
            final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
            final int rc = dialog.open();
            if (rc == Window.OK) {
                // Need to check if the connection needs a password

                TeiidDataSourceInfo info = wizard.getTeiidDataSourceInfo();
                Properties props = info.getProperties();
                IConnectionInfoProvider provider = info.getConnectionInfoProvider();

                if (null != provider.getPasswordPropertyKey() && props.get(provider.getPasswordPropertyKey()) == null) {

                    int result = new AbstractPasswordDialog(iww.getShell()) {
                        @SuppressWarnings( "synthetic-access" )
                        @Override
                        protected boolean isPasswordValid( final String password ) {
                            pwd = password;
                            return true;
                        }
                    }.open();
                    if (result == Window.OK) {
                        props.put(provider.getPasswordPropertyKey(), this.pwd);
                    }
                }

                executionAdmin.getOrCreateDataSource(info.getDisplayName(),
                                                     info.getJndiName(),
                                                     provider.getDataSourceType(),
                                                     props);

            }
            // createDataSource(modelFile);
        } catch (Exception e) {
            if (modelResource != null) {
                MessageDialog.openError(getShell(),
                                        getString("errorCreatingDataSource", modelResource.getItemName()), e.getMessage()); //$NON-NLS-1$
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("errorCreatingDataSource", modelResource.getItemName())); //$NON-NLS-1$
            } else {
                MessageDialog.openError(getShell(), getString("errorCreatingDataSource"), e.getMessage()); //$NON-NLS-1$
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("errorCreatingDataSource")); //$NON-NLS-1$

            }
        }
    }

    private Collection<ModelResource> getRelationalModelsWithConnections() {
        Collection<ModelResource> result = new ArrayList<ModelResource>();

        try {
            ModelResource[] mrs = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().getModelResources();
            for (ModelResource mr : mrs) {
                if (ModelIdentifier.isRelationalSourceModel(mr)) {
                    IConnectionInfoProvider provider = null;

                    try {
                        provider = getProvider(mr);
                    } catch (Exception e) {
                        // If provider throws exception its OK because some models may not have connection info.
                    }

                    if (provider != null) {
                        Properties properties = provider.getConnectionProperties(mr);
                        if (properties != null && !properties.isEmpty()) {
                            result.add(mr);
                        }
                    }
                }
            }

        } catch (CoreException e) {
            DqpUiConstants.UTIL.log(e);
        }

        return result;
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

    public IConnectionInfoProvider getProvider( ModelResource modelResource ) throws Exception {
        IConnectionInfoProvider provider = null;
        provider = providerFactory.getProvider(modelResource);
        if (null == provider) {
            throw new Exception(getString("noConnectionInfoProvider.message")); //$NON-NLS-1$
        }
        return provider;

    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
