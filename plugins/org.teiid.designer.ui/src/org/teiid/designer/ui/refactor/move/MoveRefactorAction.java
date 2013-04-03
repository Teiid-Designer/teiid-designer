/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.move;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;

/**
 *
 */
public class MoveRefactorAction implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

    private IWorkbenchWindow window;
    private List<IResource> selectedResources;

    @Override
    public void init(IViewPart view) {
        this.window = view.getSite().getWorkbenchWindow();
    }

    @Override
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    @Override
    public void run(IAction action) {
        if (selectedResources == null || selectedResources.isEmpty() || window == null) {
            return;
        }

        MoveResourcesRefactoring refactoring = new MoveResourcesRefactoring(selectedResources);
        refactoring.setWorkbenchWindow(window);
        run(new MoveResourcesWizard(refactoring, "Move Resource"), window.getShell(), "Move Resource");
    }

    private void run(RefactoringWizard wizard, Shell parent, String dialogTitle) {
        try {
            RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(wizard);
            operation.run(parent, dialogTitle);
        } catch (InterruptedException exception) {
            // Do nothing
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection == null || selection.isEmpty()) {
            action.setEnabled(false);
            return;
        }

        if (! SelectionUtilities.isAllIResourceObjects(selection)) {
            action.setEnabled(false);
            return;
        }

        List<IResource> resources = SelectionUtilities.getSelectedIResourceObjects(selection);

        for (IResource resource : resources) {
            if (ModelUtil.isIResourceReadOnly(resource)) {
                action.setEnabled(false);
                return;
            }

            if(resource instanceof IProject) {
                action.setEnabled(false);
                return;
            }
        }

        action.setEnabled(true);
        selectedResources = resources;
    }

    @Override
    public void dispose() {
        // Do nothing
    }
}
