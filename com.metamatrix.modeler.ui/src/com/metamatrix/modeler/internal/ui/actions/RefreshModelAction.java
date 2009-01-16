/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ImportResourcesAction;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.jdbc.relational.JdbcImporter;
import com.metamatrix.modeler.internal.jdbc.relational.ModelerJdbcRelationalConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.IRefreshContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.dialog.AbstractPasswordDialog;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 4.0
 */
public class RefreshModelAction
    implements IRefreshContributor, ModelerJdbcRelationalConstants.Messages, ModelUtil.Constants, UiConstants {

    private IWorkbenchWindow wdw;
    ModelResource model;
    IStatus status;
    private boolean enable = false;

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     * @since 4.0
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     * @since 4.0
     */
    public void init( final IWorkbenchWindow window ) {
        this.wdw = window;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.0
     */
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
     * @see com.metamatrix.modeler.ui.actions.IRefreshContributor#canRefresh()
     */
    public boolean canRefresh() {
        return this.enable;
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        determineEnablement(selection);
    }
}
