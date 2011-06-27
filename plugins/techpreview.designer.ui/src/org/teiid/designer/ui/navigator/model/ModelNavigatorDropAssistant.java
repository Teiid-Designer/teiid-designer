/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.dotProjectResourceDropErrorMessage;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.dotProjectResourceDropErrorTitle;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.invalidProjectErrorTitle;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.mixedDropQuestion;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

public class ModelNavigatorDropAssistant extends CommonDropAdapterAssistant {

    private void createExistingProject( String projectFolder ) {
        try {
            final IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(projectFolder
                                                                                                                  + "//.project")); //$NON-NLS-1$
            final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());

            // create the new project operation
            WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
                 */
                @Override
                protected void execute( IProgressMonitor monitor ) throws CoreException {
                    monitor.beginTask("", 2000); //$NON-NLS-1$
                    project.create(description, new SubProgressMonitor(monitor, 1000));

                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    project.open(new SubProgressMonitor(monitor, 1000));

                }
            };

            // run the new project creation operation
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
            dialog.run(true, true, op);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonDropAdapterAssistant#handleDrop(org.eclipse.ui.navigator.CommonDropAdapter,
     *      org.eclipse.swt.dnd.DropTargetEvent, java.lang.Object)
     */
    @Override
    public IStatus handleDrop( CommonDropAdapter aDropAdapter,
                               DropTargetEvent aDropTargetEvent,
                               Object aTarget ) {
        Object data = aDropTargetEvent.data;

        try {
            if (data instanceof IResource) {
                if (DotProjectUtils.getDotProjectCount((IResource)data, true, false) > 0) {
                    ErrorDialog.openError(getShell(), dotProjectResourceDropErrorTitle, dotProjectResourceDropErrorMessage, null);
                    aDropTargetEvent.detail = DND.DROP_NONE;
                } else {
                    // TODO fix this
                    // performDrop(data);
                }
            } else {
                String[] files = (String[])data;
                ArrayList projects = new ArrayList();
                ArrayList nonProjectFiles = new ArrayList();
                ArrayList invalidProjects = new ArrayList();

                for (String file : files) {
                    // Check For Top Level .project
                    int projectCount = DotProjectUtils.getDotProjectCount(file, true, false);

                    if (projectCount == 1) {
                        if (DotProjectUtils.getDotProjectCount(file, false, false) == 1) {
                            if (file.indexOf(DotProjectUtils.DOT_PROJECT) > 0) {
                                invalidProjects.add(file);
                            } else {
                                projects.add(file);
                            }
                        }
                    } else if (projectCount == 0) {
                        nonProjectFiles.add(file);
                    } else {
                        invalidProjects.add(file);
                    }
                }

                if ((nonProjectFiles.size() > 0) && ((projects.size() > 0) || (invalidProjects.size() > 0))) {
                    MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    messageBox.setMessage(mixedDropQuestion);
                    int rc = messageBox.open();

                    if (rc == SWT.YES) {
                        handleProjectImport(projects, invalidProjects);
                    } else {
                        aDropTargetEvent.detail = DND.DROP_NONE;
                    }
                } else {
                    if (nonProjectFiles.size() > 0) {
                        // TODO fix this
                        // performDrop(nonProjectFiles.toArray(new String[0]));
                    } else {
                        handleProjectImport(projects, invalidProjects);
                    }
                }
            }
        } catch (Exception ce) {
            aDropTargetEvent.detail = DND.DROP_NONE;
        }

        return null;
    }

    private void handleProjectImport( ArrayList projects,
                                      ArrayList invalidProjects ) {
        for (Object project : projects) {
            createExistingProject((String)project);
        }

        if (invalidProjects.size() > 0) {
            ListMessageDialog.openError(getShell(), invalidProjectErrorTitle, null, invalidProjectErrorTitle, invalidProjects, null);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonDropAdapterAssistant#validateDrop(java.lang.Object, int,
     *      org.eclipse.swt.dnd.TransferData)
     */
    @Override
    public IStatus validateDrop( Object target,
                                 int operation,
                                 TransferData transferType ) {
        return null;
    }
}
