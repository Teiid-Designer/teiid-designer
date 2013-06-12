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
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.refactor.AbstractRefactorAction;

/**
 * Action for moving models inside a project
 */
public class MoveRefactorAction extends AbstractRefactorAction {

    @Override
    protected RefactoringWizard getRefactoringWizard(List<IResource> resources, IWorkbenchWindow window) {
        MoveResourcesRefactoring refactoring = new MoveResourcesRefactoring(resources);
        
        String wizardTitle = "Move Resource"; //$NON-NLS-1$
        if (resources.size() > 1)
            wizardTitle = wizardTitle + "s"; //$NON-NLS-1$
        
        return new MoveResourcesWizard(refactoring, wizardTitle);
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        List<IResource> resources = SelectionUtilities.getSelectedIResourceObjects(selection);

        // Cannot move projects
        for (IResource resource : resources) {
            if(resource instanceof IProject) {
                action.setEnabled(false);
                return;
            }
        }

        /*
         * Check the resources being moved are in the same directory.
         *
         * This is a limitation of the move but avoids more difficult
         * problems with keeping track of location changes with the
         * resources being moved.
        */
        IPath parentDirectory = null;
        for (IResource resource : resources) {
            IPath path = resource.getFullPath();
            path = path.uptoSegment(path.segmentCount() - 1);
            if (parentDirectory == null) {
                parentDirectory = path;
            } else if (! parentDirectory.equals(path)) {
                action.setEnabled(false);
                return;
            }
        }
        
        super.selectionChanged(action, selection);
    }
}
