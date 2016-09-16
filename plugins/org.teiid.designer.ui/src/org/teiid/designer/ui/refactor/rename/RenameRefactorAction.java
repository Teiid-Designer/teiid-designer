/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.rename;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.refactor.AbstractRefactorAction;

/**
 *
 */
public class RenameRefactorAction extends AbstractRefactorAction {

    @Override
    protected RefactoringWizard getRefactoringWizard(List<IResource> resources, IWorkbenchWindow window) {
        CoreArgCheck.isNotNull(resources);
        CoreArgCheck.isEqual(resources.size(), 1);
        
        RenameResourceRefactoring refactoring = new RenameResourceRefactoring(resources.get(0));
        
        String wizardTitle = "Rename Resource " + resources.get(0).getName(); //$NON-NLS-1$
        
        return new RenameResourceWizard(refactoring, wizardTitle);
    }
    
    @Override
    public void run(IAction action) {
        super.run(action);
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // Rename only logically works on 1 item at a time
        if (SelectionUtilities.isMultiSelection(selection)) {
            action.setEnabled(false);
            return;
        }
        
        List<IResource> resources = SelectionUtilities.getSelectedIResourceObjects(selection);
        for( IResource resource : resources ) {
        	if( resource instanceof IProject ) {
        		action.setEnabled(false);
        		return;
        	}
        }
        
        super.selectionChanged(action, selection);
    }
    
    
}
