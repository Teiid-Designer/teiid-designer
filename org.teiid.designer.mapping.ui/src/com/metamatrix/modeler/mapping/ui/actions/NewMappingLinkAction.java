/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * NewMappingLinkAction
 */
public class NewMappingLinkAction extends MappingAction {
    private static final String ACTION_DESCRIPTION = "New Mapping Link"; //$NON-NLS-1$

    public NewMappingLinkAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.NEW_MAPPING_LINK));
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
            List selectedObjects = SelectionUtilities.getSelectedObjects(getSelection());
            if (selectedObjects.size() == 2 && getMappingClassFactory() != null) {
                EObject eObject1 = (EObject)selectedObjects.get(0);
                EObject eObject2 = (EObject)selectedObjects.get(1);
                MappingClassColumn mcc = null;
                EObject locationEObject = null;
                if (isMappingClassColumn(eObject1)) {
                    mcc = (MappingClassColumn)eObject1;
                    locationEObject = eObject2;
                }
                if (mcc == null && isMappingClassColumn(eObject2)) {
                    mcc = (MappingClassColumn)eObject2;
                    locationEObject = eObject1;
                }

                if (mcc != null && locationEObject != null) {
                    boolean canUndo = IMappingDiagramActionConstants.DiagramActions.UNDO_NEW_MAPPING_LINK;
                    // start txn
                    boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
                    boolean succeeded = false;
                    try {
                        getMappingClassFactory().addLocation(mcc, locationEObject);
                        succeeded = true;
                    } finally {
                        if (requiredStart) {
                            if (succeeded) {
                                ModelerCore.commitTxn();
                                if (!canUndo) ModelerUndoManager.getInstance().clearAllEdits();
                            } else {
                                ModelerCore.rollbackTxn();
                            }
                        }
                    }

                } else {
                    // LOG AN ERROR HERE!!
                }
            }
        }

        setEnabled(false);
    }

    private void determineEnablement() {
        // This is an action that requires two things...
        // 1) Double Selection
        // 2) Selected objects can be linked
        boolean enable = false;

        if (this.getPart() instanceof ModelEditor && SelectionUtilities.isMultiSelection(getSelection())) {
            List selectedObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            if (selectedObjects.size() == 2 && getMappingClassFactory() != null) {
                EObject eObject1 = (EObject)selectedObjects.get(0);
                EObject eObject2 = (EObject)selectedObjects.get(1);
                MappingClassColumn mcc = null;
                EObject locationEObject = null;
                if (isMappingClassColumn(eObject1)) {
                    mcc = (MappingClassColumn)eObject1;
                    locationEObject = eObject2;
                }
                if (mcc == null && isMappingClassColumn(eObject2)) {
                    mcc = (MappingClassColumn)eObject2;
                    locationEObject = eObject1;
                }
                if (mcc != null && locationEObject != null && isWritable()) enable = getMappingClassFactory().canAddLocation(mcc,
                                                                                                                             locationEObject);
            }
        }

        setEnabled(enable);
    }
}
