package org.teiid.designer.dataservices.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.dataservices.ui.UiConstants;
import org.teiid.designer.dataservices.ui.UiPlugin;
import org.teiid.designer.ui.actions.ISelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.refactor.SaveModifiedResourcesDialog;
import org.teiid.designer.ui.wizards.CloneProjectWizard;

public class OpenDSProjectAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    private static final String TITLE = "Open Data Service Project Editor"; //UiConstants.Util.getString("CloneProjectAction2.title"); //$NON-NLS-1$

    private IProject selectedProject;

    /**
     * Construct an instance of ImportMetadata.
     */
    public OpenDSProjectAction() {
        super();
        this.setText(TITLE);
        this.setToolTipText(TITLE);
        //setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CLONE_PROJECT_ICON));
        setEnabled(true);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     */
    @Override
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

    @Override
	public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    @Override
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
    	IEditorPart result;
    	IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
    	IEditorInput input = new DSProjectEditorInput(selectedProject);
        try {
            result = page.openEditor(input, UiConstants.DS_EDITOR_ID); //IDE.openEditor(UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage(), (IFile)selectedProject);
        } catch (PartInitException e) {
            UiConstants.UTIL.log(e);
        }

        setEnabled(false);
        setEnabled(true);
    }

    //
    // Utility methods:
    //
    protected Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
}