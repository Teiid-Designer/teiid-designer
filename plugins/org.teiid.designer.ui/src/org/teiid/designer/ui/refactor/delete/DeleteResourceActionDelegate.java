/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.delete;

import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.refactor.AbstractRefactorAction;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;

/**
 *
 */
public class DeleteResourceActionDelegate extends AbstractRefactorAction {

    @Override
    protected RefactoringWizard getRefactoringWizard(List<IResource> resources, IWorkbenchWindow window) {
        CoreArgCheck.isNotNull(resources);
        
        DeleteResourcesRefactoring refactoring = new DeleteResourcesRefactoring(resources);
        refactoring.setWorkbenchWindow(window);
        
        String wizardTitle = "Delete Resources"; //$NON-NLS-1$
        return new DeleteResourcesWizard(refactoring, wizardTitle);
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        List<IResource> resources = SelectionUtilities.getSelectedIResourceObjects(selection);

        if (! RefactorResourcesUtils.containsOnlyProjects(resources) 
            && ! RefactorResourcesUtils.containsOnlyNonProjects(resources)) {
            // Cannot delete projects and files/folders at the same time
            action.setEnabled(false);
            return;
        }

        super.selectionChanged(action, selection);
    }
}
