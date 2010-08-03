package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.TeiidDataSource;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.dialog.AbstractPasswordDialog;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class CreateDataSourceAction extends SortableSelectionAction implements DqpUiConstants {
    private static final String label = DqpUiConstants.UTIL.getString("CreateDataSourceAction.label"); //$NON-NLS-1$

    private String pwd;
    private ConnectionInfoProviderFactory providerFactory;

    /**
     * @since 5.0
     */
    public CreateDataSourceAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
        providerFactory = new ConnectionInfoProviderFactory();
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

        try {
            createDataSource(modelFile);
        } catch (Exception e) {
            MessageDialog.openError(getShell(),
                                    DqpUiConstants.UTIL.getString("CreateDataSourceAction.errorCreatingDataSource", modelFile.getName()), e.getMessage()); //$NON-NLS-1$
            DqpUiConstants.UTIL.log(IStatus.ERROR,
                                    e,
                                    DqpUiConstants.UTIL.getString("CreateDataSourceAction.errorCreatingDataSource", modelFile.getName())); //$NON-NLS-1$
        }
    }

    public boolean createDataSource( IFile modelFile ) throws Exception {

        ModelResource modelResource = null;

        modelResource = ModelUtilities.getModelResource(modelFile);
        IConnectionInfoProvider provider = getProvider(modelResource);
        Properties properties = provider.getConnectionProperties(modelResource);
        Shell sh = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        if (properties != null && !properties.isEmpty()) {
            ExecutionAdmin executionAdmin = DqpPlugin.getInstance().getServerManager().getDefaultServer().getAdmin();
            String name = modelFile.getFullPath().removeFileExtension().lastSegment();
            String jndiName = provider.generateUniqueConnectionJndiName(name,
                                                                        modelFile.getFullPath(),
                                                                        ModelerCore.workspaceUuid().toString());

            if (null != provider.getPasswordPropertyKey()) {

                int result = new AbstractPasswordDialog(sh) {
                    @SuppressWarnings( "synthetic-access" )
                    @Override
                    protected boolean isPasswordValid( final String password ) {
                        pwd = password;
                        return true;
                    }
                }.open();
                if (result == Window.OK) {
                    properties.put(provider.getPasswordPropertyKey(), this.pwd);
                }
            }

            String dsTypeName = provider.getDataSourceType();
            TeiidDataSource tds = executionAdmin.getOrCreateDataSource(modelFile.getProjectRelativePath().lastSegment(),
                                                                       jndiName,
                                                                       dsTypeName,
                                                                       properties);

            if (tds != null) {
                DqpPlugin.getInstance().getServerManager().notifyListeners(ExecutionConfigurationEvent.createAddDataSourceEvent(tds));
                return true;
            }

        } else {
            MessageDialog.openWarning(sh, DqpUiConstants.UTIL.getString("CreateDataSourceAction.noConnectionProperties.title"), //$NON-NLS-1$
                                      DqpUiConstants.UTIL.getString("CreateDataSourceAction.noConnectionProperties.message", modelFile.getName())); //$NON-NLS-1$
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

    public IConnectionInfoProvider getProvider( ModelResource modelResource ) throws Exception {
        IConnectionInfoProvider provider = null;
        provider = providerFactory.getProvider(modelResource);
        if (null == provider) {
            throw new Exception(DqpUiConstants.UTIL.getString("CreateDataSourceAction.noConnectionInfoProvider.message")); //$NON-NLS-1$
        }
        return provider;

    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
