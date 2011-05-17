/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.extended.properties.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
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
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>SetModelObjectExtendedPropertyAction</code> runs a UI that allows the user to create new {@link ExtendedProperty}s
 * for certain {@link EObject} s.
 */
public class SetModelObjectExtendedPropertyAction extends SortableSelectionAction {

    private EObject theEObject;
    private IFile modelFile;
    private Image theImage;

    public SetModelObjectExtendedPropertyAction() {
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
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {

        boolean isValid = false;
        Object obj = SelectionUtilities.getSelectedObject(selection);
        if (obj instanceof EObject) {
            this.theEObject = (EObject)obj;
            // Do a quick object check
            if (SqlAspectHelper.isTable(this.theEObject) || SqlAspectHelper.isColumn(this.theEObject)
                || SqlAspectHelper.isProcedure(this.theEObject) || SqlAspectHelper.isForeignKey(this.theEObject)
                || SqlAspectHelper.isColumnSet(this.theEObject) || SqlAspectHelper.isProcedureResultSet(this.theEObject)) {
                isValid = true;
                theImage = ModelUtilities.getModelObjectLabelProvider().getImage(this.theEObject);
            }
        }

        return isValid;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {

        boolean requiredStart = ModelerCore.startTxn(true, true, "Set Model Object Extended Properties", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            modelFile = ResourcesPlugin.getWorkspace().getRoot().getFile(ModelerCore.getModelEditor().findModelResource(theEObject).getPath());
            ModelEditor editor = ModelEditorManager.getModelEditorForFile(modelFile, true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                ExtendedPropertiesDialog dialog = null;

                boolean cont = true;
                if (editor.getModelResource().isReadOnly()) {
                    cont = MessageDialog.openQuestion(getShell(),
                                                      UiConstants.Util.getString("SetModelObjectExtendedPropertiesAction.readonlyTitle"), //$NON-NLS-1$$
                                                      UiConstants.Util.getString("SetModelObjectExtendedPropertiesAction.readonlyMessage")); //$NON-NLS-1$$
                }

                if (!cont) {
                    return;
                }
                dialog = new ExtendedPropertiesDialog(window.getShell(), this.theEObject, modelFile.isReadOnly(), theImage);

                dialog.open();

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
            MessageDialog.openError(getShell(),
                                    UiConstants.Util.getString("SetModelObjectExtendedPropertiesAction.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
                                        UiConstants.Util.getString("SetModelObjectExtendedPropertiesAction.exceptionMessage"), e); //$NON-NLS-1$
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
