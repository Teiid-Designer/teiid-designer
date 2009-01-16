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

package com.metamatrix.modeler.internal.ui.explorer;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.views.navigator.NavigatorDropAdapter;

import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * @author SDelap
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ModelExplorerDropAdapter extends NavigatorDropAdapter {
    /**
     * @param viewer
     */
    public ModelExplorerDropAdapter(StructuredViewer viewer) {
        super(viewer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
     */

    @Override
    public void drop(DropTargetEvent event) {
        Object data = event.data;
        try {
            if (data instanceof IResource) {
                if (DotProjectUtils.getDotProjectCount((IResource) data, true,
                        false) > 0) {
                    ErrorDialog.openError(getViewer().getControl().getShell(), UiConstants.Util.getString("ModelExplorerDropAdapter.dotProjectResourceDropErrorTitle"), UiConstants.Util.getString("ModelExplorerDropAdapter.dotProjectResourceDropErrorMessage"), null);  //$NON-NLS-1$//$NON-NLS-2$
                    event.detail = DND.DROP_NONE;
                } else {
                    performDrop(data);
                }
            } else {
                String[] files = (String[]) data;
                ArrayList projects = new ArrayList();
                ArrayList nonProjectFiles = new ArrayList();
                ArrayList invalidProjects = new ArrayList();
                for (int i = 0; i < files.length; i++) {
                    //Check For Top Level .project
                    int projectCount = DotProjectUtils.getDotProjectCount(
                            files[i], true, false);

                    if (projectCount == 1) {
                        if (DotProjectUtils.getDotProjectCount(files[i], false,
                                false) == 1) {
                            if (files[i].indexOf(DotProjectUtils.DOT_PROJECT) > 0) {
                                invalidProjects.add(files[i]);
                            } else {
                                projects.add(files[i]);
                            }
                        }
                    } else if (projectCount == 0) {
                        nonProjectFiles.add(files[i]);
                    } else {
                        invalidProjects.add(files[i]);
                    }
                }
                
                if (nonProjectFiles.size() > 0 && (projects.size() > 0 || invalidProjects.size() > 0)) {
                    MessageBox messageBox = new MessageBox(getViewer().getControl().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    messageBox.setMessage(UiConstants.Util.getString("ModelExplorerDropAdapter.mixedDropQuestion")); //$NON-NLS-1$
                    int rc = messageBox.open();
                    if (rc == SWT.YES) {
                        handleProjectImport(projects, invalidProjects);
                    } else {
                        event.detail = DND.DROP_NONE;
                    }
                } else {
                    if (nonProjectFiles.size() > 0) {
                        performDrop(nonProjectFiles.toArray(new String[0]));
                    } else {
                        handleProjectImport(projects, invalidProjects);
                    }
                }
            }
        } catch (Exception ce) {
            event.detail = DND.DROP_NONE;
        }
    }
    
    private void handleProjectImport(ArrayList projects, ArrayList invalidProjects) {
        for (int i = 0; i < projects.size(); i++) {
            createExistingProject((String) projects.get(i));
        } 
        
        if (invalidProjects.size() > 0) {
            ListMessageDialog.openError(getViewer().getControl().getShell(), UiConstants.Util.getString("ModelExplorerDropAdapter.invalidProjectErrorTitle"), null, UiConstants.Util.getString("ModelExplorerDropAdapter.invalidProjectErrorMessage"), invalidProjects, null); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private void createExistingProject(String projectFolder) {
        try {
            final IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(projectFolder + "//.project")); //$NON-NLS-1$
            final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());

            // create the new project operation
            WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
                @Override
                protected void execute(IProgressMonitor monitor)
                        throws CoreException {
                    monitor.beginTask("", 2000); //$NON-NLS-1$
                    project.create(description, new SubProgressMonitor(monitor,
                            1000));
                    if (monitor.isCanceled())
                        throw new OperationCanceledException();
                    project.open(new SubProgressMonitor(monitor, 1000));

                }
            };

            // run the new project creation operation
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(
                    getViewer().getControl().getShell());
            dialog.run(true, true, op);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
