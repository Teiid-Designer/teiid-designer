/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.scope;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * 
 */
public class SetModelProjectScopeAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    private IProject selectedProject;

    public SetModelProjectScopeAction() {
        super();
        setText("Change Modeling Scope");
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.MODEL));
    }

    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IProject && ModelerCore.hasModelNature((IProject)obj)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public void run() {
        if (selectedProject != null) {
            Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
            SelectModelingScopeDialog dialog = new SelectModelingScopeDialog(shell, selectedProject);
            dialog.open();
        }
    }

    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        boolean enable = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IProject && ModelerCore.hasModelNature((IProject)obj)) {
                this.selectedProject = (IProject)obj;
                enable = true;
            }
        }
        setEnabled(enable);
    }
}
