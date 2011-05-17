/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.extended.properties.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.relational.ui.extended.properties.ExtendedPropertiesDialog;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>SetModelObjectExtendedPropertyAction</code> runs a UI that allows the user to create new {@link ExtendedProperty}s
 * for certain {@link EObject} s.
 */
public class SetModelResourceExtendedPropertyAction extends SortableSelectionAction {

    private EObject theEObject;
    private IFile theIFile;
    private Image theImage;

    public SetModelResourceExtendedPropertyAction() {
        super();
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

    /**
     * @param selection
     * @return
     * @throws ModelWorkspaceException
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {

        boolean isValid = false;
        this.theEObject = null;
        this.theIFile = null;
        this.theImage = null;
        Object obj = SelectionUtilities.getSelectedObject(selection);
        if (obj instanceof IFile) {
            this.theIFile = (IFile)obj;
            ModelResource modelResource = ModelUtilities.getModelResourceForIFile(this.theIFile, true);
            try {
                if (modelResource != null && ModelUtilities.isRelationalModel(modelResource)) {
                    this.theEObject = modelResource.getModelAnnotation();
                    isValid = true;
                    theImage = new ModelExplorerLabelProvider().getImage(this.theIFile);
                }
            } catch (ModelWorkspaceException e) {
                throw new RuntimeException(e);
            }
        }

        return isValid;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {

        boolean requiredStart = ModelerCore.startTxn(true, true, "Set Model Resource Extended Properties", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile(this.theIFile, true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                boolean cont = true;
                if (editor.getModelResource().isReadOnly()) {
                    cont = MessageDialog.openQuestion(getShell(),
                                                      UiConstants.Util.getString("SetModelResourceExtendedPropertiesAction.readonlyTitle"), //$NON-NLS-1$$
                                                      UiConstants.Util.getString("SetModelResourceExtendedPropertiesAction.readonlyMessage")); //$NON-NLS-1$$
                }

                if (!cont) {
                    return;
                }

                ExtendedPropertiesDialog dialog = null;
                dialog = new ExtendedPropertiesDialog(window.getShell(), this.theEObject, editor.getModelResource().isReadOnly(),
                                                      this.theImage);

                dialog.open();

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
            MessageDialog.openError(getShell(),
                                    UiConstants.Util.getString("SetModelResourceExtendedProeprtiesAction.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(
                                        IStatus.ERROR,
                                        UiConstants.PLUGIN_ID,
                                        UiConstants.Util.getString("SetModelResourceExtendedProeprtiesAction.exceptionMessage"), e); //$NON-NLS-1$
            UiConstants.Util.log(status);

            return;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

    }

    /**
     * 
     */
    @Override
    public boolean isApplicable( final ISelection selection ) {
        return isValidSelection(selection);
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }

}
