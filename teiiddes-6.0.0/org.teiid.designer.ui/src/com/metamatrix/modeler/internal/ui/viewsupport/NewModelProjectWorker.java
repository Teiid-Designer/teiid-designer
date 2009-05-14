/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import com.metamatrix.common.util.OSPlatformUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.internal.core.workspace.ModelProjectImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.dialogs.FileUiUtils;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;

/**
 * @since 4.3
 */
public class NewModelProjectWorker {
    private static final String[] MODEL_NATURES = new String[] {ModelerCore.NATURE_ID};

    public NewModelProjectWorker() {

    }

    public IProject createNewProject( IPath newPath,
                                      String name,
                                      IProgressMonitor monitor ) {
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        final IProjectDescription description = workspace.newProjectDescription(newProject.getName());
        description.setLocation(newPath);

        // run the new project creation operation
        try {
            boolean doit = false;

            if (!newProject.exists()) {
                // Defect 24558 - Text Importer may result in New Project being created with NO PATH, so check if NULL
                // before
                if (newPath != null && OSPlatformUtil.isWindows()) {
                    // check to see if path exists but case is different
                    IPath path = new Path(FileUiUtils.INSTANCE.getExistingCaseVariantFileName(newPath));

                    newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(path.lastSegment());
                    description.setLocation(path);
                    doit = !newProject.exists();
                } else {
                    doit = true;
                }

                if (doit) {
                    createProject(description, newProject, monitor);
                }
            }
        } catch (CoreException e) {
            if (e.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
                MessageDialog.openError(getShell(),
                                        ResourceMessages.NewProject_errorMessage,
                                        NLS.bind(ResourceMessages.NewProject_caseVariantExistsError, newProject.getName()));
            } else {
                ErrorDialog.openError(getShell(), ResourceMessages.NewProject_errorMessage, null, // no special message
                                      e.getStatus());
            }

            return null;
        } catch (Exception theException) {
            return null;
        }

        configureProject(newProject);

        return newProject;
    }

    private void configureProject( IProject newProject ) {
        try {
            final IProjectDescription desc = newProject.getDescription();
            desc.setNatureIds(new String[0]);
            newProject.setDescription(desc, null);

            desc.setNatureIds(MODEL_NATURES);
            if (ProductCustomizerMgr.getInstance() != null) {
                String productName = ProductCustomizerMgr.getInstance().getProductName();
                if (!StringUtil.isEmpty(productName)) {
                    desc.setComment(productName + ", version " + ModelerCore.ILicense.VERSION); //$NON-NLS-1$
                }
            }
            newProject.setDescription(desc, null);

            if (!ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
                // Defect 11480 - closing and opening the project sets the overlay icon properly
                newProject.close(null);
            }

            newProject.open(null);
        } catch (final CoreException err) {
            UiConstants.Util.log(IStatus.ERROR, err, err.getMessage());
        }
    }

    /**
     * Creates a project resource given the project handle and description.
     * 
     * @param description the project description to create a project resource for
     * @param projectHandle the project handle to create a project resource for
     * @param monitor the progress monitor to show visual progress with
     * @exception CoreException if the operation fails
     * @exception OperationCanceledException if the operation is canceled
     */
    private void createProject( IProjectDescription description,
                                IProject projectHandle,
                                IProgressMonitor monitor ) throws CoreException, OperationCanceledException {

        projectHandle.create(description, new SubProgressMonitor(monitor, 1000));

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }

        // projectHandle.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));
        projectHandle.open(IResource.NONE, new SubProgressMonitor(monitor, 1000));

        // Create the corresponding ModelProject
        ModelWorkspace mWorkspace = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace();
        ModelProject mProject = new ModelProjectImpl(projectHandle, mWorkspace);

        // Create the corresponding ModelFolders and ModelResources
        List iResources = new ArrayList();
        addMembersToList(projectHandle, iResources);
        for (Iterator iter = iResources.iterator(); iter.hasNext();) {
            IResource iResource = (IResource)iter.next();
            ModelWorkspaceManager.create(iResource, mProject);
        }
    }

    private void addMembersToList( IContainer iContainer,
                                   List iResources ) throws CoreException {
        if (iContainer != null) {
            IResource[] members = iContainer.members();
            for (int i = 0; i != members.length; ++i) {
                IResource member = members[i];
                if (!iResources.contains(member)) {
                    iResources.add(member);
                }
                if (member instanceof IContainer) {
                    addMembersToList((IContainer)member, iResources);
                }
            }
        }
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
