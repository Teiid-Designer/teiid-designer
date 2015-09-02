package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ResourceAnnotationHelper;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;


/**
 * @since 8.0
 */
public class DeleteDataSourceAction extends SortableSelectionAction implements DqpUiConstants {
    private static final String label = DqpUiConstants.UTIL.getString("DeleteDataSourceAction.label"); //$NON-NLS-1$
    public static final String JDBC_DS_TYPE = "connector-jdbc"; //$NON-NLS-1$

    /**
     * @since 5.0
     */
    public DeleteDataSourceAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
    }

    /**
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
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
        IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);

        try {
            removeDataSource(modelFile);
        } catch (Exception e) {
            MessageDialog.openError(getShell(),
                                    DqpUiConstants.UTIL.getString("DeleteDataSourceAction.errorRemovingDataSource", modelFile.getName()), e.getMessage()); //$NON-NLS-1$
            DqpUiConstants.UTIL.log(IStatus.ERROR,
                                    e,
                                    DqpUiConstants.UTIL.getString("DeleteDataSourceAction.errorRemovingDataSource", modelFile.getName())); //$NON-NLS-1$
        }
    }

    public boolean removeDataSource( IFile modelFile ) throws Exception {

        Properties properties = getConnectionProperties(modelFile);

        if (properties != null && !properties.isEmpty()) {
            ITeiidServer teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
            String name = FileUtils.getNameWithoutExtension(modelFile);
            String jndiName = new ConnectionInfoHelper().generateUniqueConnectionJndiName(name,
                                                                                          modelFile.getFullPath(),
                                                                                          ModelerCore.workspaceUuid().toString());

            teiidServer.deleteDataSource(jndiName);
            return true;
        }
        return false;
    }

    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
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
            {

            }

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

    public Properties getConnectionProperties( IFile model ) {

        ModelResource modelResource = null;

        try {
            modelResource = ModelUtil.getModelResource(model, true);
            if (modelResource != null) {
                ResourceAnnotationHelper resourceHelper = new ResourceAnnotationHelper();
                return resourceHelper.getProperties(modelResource, IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE);
            }
            DqpUiConstants.UTIL.log(IStatus.ERROR,
                                    DqpUiConstants.UTIL.getString("DeleteDataSourceAction.errorCannotFindDataSourceProperties", model.getName())); //$NON-NLS-1$

        } catch (ModelWorkspaceException e) {
            DqpUiConstants.UTIL.log(IStatus.ERROR,
                                    DqpUiConstants.UTIL.getString("DeleteDataSourceAction.errorFindingModelResource", model.getName())); //$NON-NLS-1$
        }

        return null;
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }

}
