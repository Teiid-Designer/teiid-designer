/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.move;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.refactor.AbstractResourcesRefactoring;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;
import org.teiid.designer.ui.refactor.SingleProjectModelContentProvider;

/**
 *
 */
public class MoveResourcesWizard extends RefactoringWizard {

    /**
     * @param refactoring
     * @param pageTitle
     */
    public MoveResourcesWizard(MoveResourcesRefactoring refactoring, String pageTitle) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(pageTitle);
    }

    @Override
    protected void addUserInputPages() {
        List<IResource> resources = ((AbstractResourcesRefactoring) getRefactoring()).getResources();
        addPage(new MoveResourceDestinationPage(resources));
    }

    private static class MoveResourceDestinationPage extends UserInputWizardPage {

        private final SingleProjectModelContentProvider contentProvider;

        private final ILabelProvider labelProvider = new ModelExplorerLabelProvider();

        private TreeViewer treeViewer;

        public MoveResourceDestinationPage(List<IResource> resources) {
            super(RefactorResourcesUtils.getString("MoveRefactoring.moveResourceDestinationPage")); //$NON-NLS-1$
            
            CoreArgCheck.isNotEmpty(resources, ""); //$NON-NLS-1$
            
            IProject project = resources.get(0).getProject();
            contentProvider = new SingleProjectModelContentProvider(project);
        }

        @Override
        public MoveResourcesRefactoring getRefactoring() {
            return (MoveResourcesRefactoring) super.getRefactoring();
        }

        private RefactoringStatus createErrorStatus(String key, Object... args) {
            return RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString(key, args));
        }

        private boolean verifyResourcesProject(Collection<IResource> resources) {
            if(resources == null || resources.isEmpty())
                return false;
            
            IProject project = null;
            for (IResource resource : resources) {
                if (project == null) {
                    project = resource.getProject();
                } else if (! project.equals(resource.getProject())) {
                    // 2 resources are not part of the same project
                    return false;
                }
            }
            
            return true;
        }

        private void verifyDestination(Object selection) {
            if (! (selection instanceof IContainer)) {
                setPageComplete(createErrorStatus("MoveRefactoring.destinationNotFolder")); //$NON-NLS-1$
                return;
            }
                
            IContainer destination = (IContainer) selection;
            String destinationPath = destination.getFullPath().toString();
            if (destination instanceof IProject) {
                IProject project = (IProject) destination;
                
                // Not open or a non-model project
                try {
                    if (! project.isOpen() || project.getNature(ModelerCore.NATURE_ID) == null) {
                        setPageComplete(createErrorStatus("MoveRefactoring.destinationProjectNotOpen")); //$NON-NLS-1$
                        return;
                    }
                } catch (CoreException ex) {
                    ModelerCore.Util.log(ex);
                    setPageComplete(createErrorStatus(ex.getMessage()));
                    return;
                }
            }

            List<IResource> resources = getRefactoring().getResources();
            if (! verifyResourcesProject(resources)) {
                setPageComplete(createErrorStatus("MoveRefactoring.resourcesNotInSameProject")); //$NON-NLS-1$
                return;
            }
            
            for (IResource resource : resources) {
                if (! resource.getProject().equals(destination.getProject())) {
                    setPageComplete(createErrorStatus("MoveRefactoring.destinationNotSameProject")); //$NON-NLS-1$
                    return;
                }
                
                if (resource.getParent().equals(destination)) {
                    setPageComplete(createErrorStatus("MoveRefactoring.destinationSame")); //$NON-NLS-1$
                    return;
                }

                if (resource instanceof IFolder) {
                    IFolder folderResource = (IFolder)resource;

                    // destination cannot be beneath target
                    final String resourcePath = folderResource.getFullPath().toString() + '/';
                    if (destinationPath.startsWith(resourcePath)) {
                        setPageComplete(createErrorStatus("MoveRefactoring.destinationSubFolder")); //$NON-NLS-1$
                        return;
                    }
                }

                /*
                 * Determine if the target resource were moved to the proposed destination, is there 
                 * another resource in the same container with the same name.
                 */
                final String proposedPath = destinationPath + '/' + resource.getName();
                final IWorkspaceRoot workspaceRoot = resource.getWorkspace().getRoot();
                if (workspaceRoot.findMember(proposedPath) != null) {
                    setPageComplete(createErrorStatus("MoveRefactoring.nameClash", resource.getName())); //$NON-NLS-1$
                    return;
                }
            }

            getRefactoring().setDestination(destination);
            setPageComplete(RefactoringStatus.create(Status.OK_STATUS));
        }

        private void addSelectionChangeListener() {
            treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ISelection selection = event.getSelection();
                    if (!(selection instanceof IStructuredSelection))
                        return;
                    
                    IStructuredSelection ss = (IStructuredSelection) selection;
                    verifyDestination(ss.getFirstElement());
                }
            });
        }

        private void addLabel(Composite parent) {
            Label label = new Label(parent, SWT.WRAP);
            String text = new String(RefactorResourcesUtils.getString("MoveRefactoring.destinationLabelDefaultText")); //$NON-NLS-1$

            label.setText(text);
            GridData data = new GridData(SWT.FILL, SWT.END, true, false);
            data.widthHint = convertWidthInCharsToPixels(50);
            label.setLayoutData(data);
        }

        private void createViewer(Composite parent) {
            treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.widthHint = convertWidthInCharsToPixels(40);
            gd.heightHint = convertHeightInCharsToPixels(15);
            treeViewer.getTree().setLayoutData(gd);

            treeViewer.setLabelProvider(labelProvider);
            treeViewer.setContentProvider(contentProvider);
            
            IWorkspaceRoot root = ModelerCore.getWorkspace().getRoot();
            treeViewer.setInput(root);
            
            IProject project = getRefactoring().getResources().get(0).getProject();
            
            treeViewer.setExpandedElements(new Object[] { project });
            treeViewer.setSelection(new StructuredSelection(project));
            verifyDestination(project);
        }
        
        @Override
        public void createControl(Composite parent) {
            initializeDialogUnits(parent);
            
            Composite result = new Composite(parent, SWT.NONE);

            result.setLayout(new GridLayout());

            addLabel(result);
            createViewer(result);
            Dialog.applyDialogFont(result);
            
            addSelectionChangeListener();
            setControl(result);
        }
    }

}
