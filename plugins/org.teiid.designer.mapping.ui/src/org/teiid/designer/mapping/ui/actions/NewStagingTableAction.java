/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditor;


/**
 * NewTempTableAction
 *
 * @since 8.0
 */
public class NewStagingTableAction extends MappingAction {
    private static final String ACTION_DESCRIPTION = "New Staging Table"; //$NON-NLS-1$

    public NewStagingTableAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.NEW_TEMP_TABLE));
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        super.selectionChanged(thePart, theSelection);
        determineEnablement();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        if (getMappingClassFactory() != null) {
            Object o = SelectionUtilities.getSelectedObject(getSelection());
            if (o instanceof EObject) {
                EObject eObject = (EObject)o;
                boolean canUndo = IMappingDiagramActionConstants.DiagramActions.UNDO_NEW_STAGING_TABLE;
                // start txn
                boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
                boolean succeeded = false;
                try {
                    getMappingClassFactory().createStagingTable(eObject);
                    succeeded = true;
                } finally {
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            } else {
                // LOG AN ERROR HERE!!
            }
        }

        setEnabled(false);
    }

    private void determineEnablement() {
        // This is an action that requires two things...
        // 1) Single Selection
        // 2) Selected object can allow mapping class
        boolean enable = false;

        if (this.getPart() instanceof ModelEditor && SelectionUtilities.isSingleSelection(getSelection())) {
            EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
            if (eObject != null && isWritable() && getMappingClassFactory() != null) {
                if (getMappingClassFactory().canCreateStagingTable(eObject)) enable = true;
            }
        }

        setEnabled(enable);
    }
}
