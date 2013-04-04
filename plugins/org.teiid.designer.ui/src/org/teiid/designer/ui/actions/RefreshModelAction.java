/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ImportResourcesAction;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.relational.JdbcImporter;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.dialog.AbstractPasswordDialog;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class RefreshModelAction
    implements IRefreshContributor, org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Messages, ModelUtil.Constants, UiConstants {

    private IWorkbenchWindow wdw;
    ModelResource model;
    IStatus status;
    private boolean enable = false;

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     * @since 4.0
     */
    @Override
	public void dispose() {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     * @since 4.0
     */
    @Override
	public void init( final IWorkbenchWindow window ) {
        this.wdw = window;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.0
     */
    @Override
	public void run( final IAction action ) {
        if (this.model != null) {
            try {
                // Make sure model is a physical, relational model
                if (model.getModelType().getValue() == ModelType.VIRTUAL) {
                    WidgetUtil.showError(MODEL_NOT_PHYSICAL_MESSAGE);
                    return;
                }
                if (!RelationalPackage.eNS_URI.equals(model.getPrimaryMetamodelDescriptor().getNamespaceURI())) {
                    WidgetUtil.showError(MODEL_NOT_RELATIONAL_MESSAGE);
                    return;
                }
                // If model has no source settings, call importer w/ update option forced on
                for (final Iterator iter = this.model.getAllRootEObjects().iterator(); iter.hasNext();) {
                    if (iter.next() instanceof JdbcSource) {
                        Shell sh = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();

                        // JdbcSource found - check if autoUpdate is allowed
                        String allowsUpdate = ModelUtil.getModelAnnotationPropertyValue(model, IConnectionInfoHelper.JDBCCONNECTION_NAMESPACE+IConnectionInfoHelper.JDBCCONNECTION_ALLOW_AUTOUPDATE_KEY);
                        if(allowsUpdate!=null && !allowsUpdate.isEmpty() && !Boolean.getBoolean(allowsUpdate)) {
                            final String title = UiConstants.Util.getString("RefreshModelAction.updateNotAllowed.title"); //$NON-NLS-1$
                            final String message = UiConstants.Util.getString("RefreshModelAction.updateNotAllowed.msg"); //$NON-NLS-1$
                            MessageDialog.openInformation(sh, title, message);
                            return;
                        }
                       
                        new AbstractPasswordDialog(sh) {
                            @Override
                            protected boolean isPasswordValid( final String password ) {
                                return refresh(password);
                            }
                        }.open();
                        return;
                    }
                }
                new ImportResourcesAction(this.wdw).run();
            } catch (final ModelWorkspaceException err) {
                Util.log(err);
                WidgetUtil.showError(err.getLocalizedMessage());
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     * @since 4.0
     */
    @Override
	public void selectionChanged( final IAction action,
                                  final ISelection selection ) {
        determineEnablement(selection);
    }

    /**
     * @since 4.0
     */
    boolean refresh( final String password ) {
        try {
            new ProgressMonitorDialog(this.wdw.getShell()).run(true, true, new IRunnableWithProgress() {
                @Override
				public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                    final JdbcImporter importer = new JdbcImporter();
                    try {
                        importer.setUpdatedModel(RefreshModelAction.this.model);
                        importer.connect(password, monitor);
                        importer.setUpdatedModelSettings();

                        // Open the editor in the GUI thread ...
                        final IFile modelFile = (IFile)RefreshModelAction.this.model.getResource();
                        ModelEditorManager.activate(modelFile, true);
                        // Import/refresh the model ...
                        RefreshModelAction.this.status = importer.importModel(monitor);
                    } catch (final Throwable err) {
                        throw new InvocationTargetException(err);
                    } finally {
                        try {
                            importer.disconnect();
                        } catch (final SQLException err) {
                            throw new InvocationTargetException(err);
                        }
                        monitor.done();
                    }
                }
            });
            if (!this.status.isOK()) {
                Util.log(this.status);
                WidgetUtil.showError(this.status.getMessage());
            }
            return (this.status.getSeverity() != IStatus.ERROR);
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                err = ((InvocationTargetException)err).getTargetException();
            }
            Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
            return false;
        }
    }

    private void determineEnablement( ISelection selection ) {
        this.model = null;
        this.enable = false;

        if (selection instanceof IStructuredSelection) {
            final Object obj = ((IStructuredSelection)selection).getFirstElement();
            if (obj != null) {
                try {
                    this.model = ModelUtil.getModifiableModel(obj);
                    // Can refresh a physical relational model if it contains a JdbcSource node from
                    // which the refresh operation can extract connection information. (Fix for defect 14401)
                    if (this.model != null && !ModelUtilities.isVirtual(this.model)) {
                        // defect 19183 - don't cause the models to be opened when checking input sources:
                        if (ModelUtilities.isRelationalModel(model) && ModelUtilities.hasJdbcSource(model)) {
                            enable = true;
                            return;
                        }
                    }
                } catch (final ModelWorkspaceException err) {
                    Util.log(err);
                    WidgetUtil.showError(err.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * @see org.teiid.designer.ui.actions.IRefreshContributor#canRefresh()
     */
    @Override
	public boolean canRefresh() {
        return this.enable;
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
	public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        determineEnablement(selection);
    }
}
