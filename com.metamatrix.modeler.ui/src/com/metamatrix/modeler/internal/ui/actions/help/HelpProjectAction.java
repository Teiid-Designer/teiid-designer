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

package com.metamatrix.modeler.internal.ui.actions.help;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 4.3
 */
public class HelpProjectAction extends Action {
    private String title;
    // private String noProjectMessage;
    private String noOpenProjectMessage;

    /**
     * Construct an instance of ImportMetadata.
     */
    public HelpProjectAction( String title,
                              String noProjectMessage,
                              String noOpenProjectMessage ) {
        super();
        this.title = title;
        // this.noProjectMessage = noProjectMessage;
        this.noOpenProjectMessage = noOpenProjectMessage;
    }

    protected IStructuredSelection getFirstOpenProject() {
        // Check workspace for projects

        if (!workingProjectsExist()) {

            final NewProjectAction npAction = new NewProjectAction();
            // Insure action is run on SWT thread because it presents a dialog
            UiUtil.runInSwtThread(new Runnable() {
                public void run() {
                    npAction.run();
                }
            }, true);

            // Now we check again. If user has canceled, we'll know it and we need to return EMPTY selection
            // (i.e. can't do anything)
            if (!workingProjectsExist()) {
                // Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                // MessageDialog.openError(shell, title, noProjectMessage);
                return StructuredSelection.EMPTY;
            }
        }

        // Now, we've made it this far and there is at least one project
        boolean noOpenProject = true;
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < projects.length; ++i) {
            // DO NOT return a reserved project
            if (projects[i].isOpen() && !isReservedProject(projects[i])) {
                noOpenProject = false;
                return new StructuredSelection(projects[i]);
            }
        }

        if (noOpenProject) {
            notifyNoOpenProject();
            return StructuredSelection.EMPTY;
        }

        return StructuredSelection.EMPTY;
    }

    protected boolean workingProjectsExist() {
        // Check workspace for projects
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

        // If only one project exists, then check for hidden XML Extension project
        if (projects.length == 1) {
            // check if reserved project
            return !isReservedProject(projects[0]) && !isHiddenProject(projects[0]);
        } else if (projects.length == 0) {
            // NO projects in workspace, return false;
            return false;
        }

        return true;
    }

    /*
     * Simple check if project is reserved or not. Initially it'll be only the XML Extensions project.
     */
    private boolean isReservedProject( IProject project ) {
        // Check for XmlExtensions
        if (project != null && project.getName().equalsIgnoreCase(PluginConstants.XML_EXTENSIONS_PROJECT_NAME)) {
            return true;
        }
        return false;
    }

    private boolean isHiddenProject( IProject project ) {
        try {
            return project.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID);
        } catch (CoreException e) {
            UiConstants.Util.log(e);
        }

        // if an exception we don't want the project to be available
        return true;
    }

    protected void notifyNoOpenProject() {
        MessageDialog.openInformation(UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(), title, noOpenProjectMessage);
    }
}
