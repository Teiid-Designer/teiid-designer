/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import com.metamatrix.modeler.mapping.ui.model.MappingExtentNode;
import com.metamatrix.modeler.mapping.ui.part.MappingExtentEditPart;

/**
 * DeleteMappingLinksAction
 */
public class DeleteMappingLinksAction extends MappingAction {
    private static final String ACTION_DESCRIPTION = "Delete Mapping Links"; //$NON-NLS-1$

    public DeleteMappingLinksAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.DELETE_MAPPING_LINK));
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
            if (getMappingClassFactory() != null) {
                MappingClassColumn mcc = null;
                EObject locationEObject = null;
                EObject mappingRef = null;
                MappingExtentEditPart nextMEEP = null;

                List selectedEditParts = getSelectedMappedExtents();

                boolean canUndo = IMappingDiagramActionConstants.DiagramActions.UNDO_SPLIT_MAPPING_CLASS;
                // start txn
                boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
                boolean succeeded = false;
                try {
                    Iterator iter = selectedEditParts.iterator();
                    while (iter.hasNext()) {
                        nextMEEP = (MappingExtentEditPart)iter.next();

                        mappingRef = ((MappingExtentNode)nextMEEP.getModel()).getExtent().getMappingReference();
                        if (isMappingClassColumn(mappingRef)) mcc = (MappingClassColumn)mappingRef;

                        locationEObject = ((MappingExtentNode)nextMEEP.getModel()).getExtent().getDocumentNodeReference();

                        if (mcc != null && locationEObject != null) getMappingClassFactory().removeLocation(mcc, locationEObject);

                        succeeded = true;
                    }
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
            }
        }

        setEnabled(false);
    }

    private void determineEnablement() {
        boolean enable = false;
        if (this.getPart() instanceof ModelEditor) {

            boolean canWrite = isWritable();

            if (canWrite && getMappingClassFactory() != null) {
                MappingClassColumn mcc = null;
                EObject locationEObject = null;
                List selectedEditParts = getSelectedMappedExtents();
                if (selectedEditParts != null && !selectedEditParts.isEmpty()) {
                    Iterator iter = selectedEditParts.iterator();
                    boolean allOK = true;
                    MappingExtentEditPart nextMEEP = null;
                    while (iter.hasNext() && allOK) {
                        nextMEEP = (MappingExtentEditPart)iter.next();
                        EObject object = ((MappingExtentNode)nextMEEP.getModel()).getExtent().getMappingReference();
                        if (isMappingClassColumn(object)) mcc = (MappingClassColumn)object;

                        locationEObject = ((MappingExtentNode)nextMEEP.getModel()).getExtent().getDocumentNodeReference();

                        if (mcc != null && locationEObject != null) allOK = getMappingClassFactory().canRemoveLocation(mcc,
                                                                                                                       locationEObject);
                        else allOK = false;
                    }

                    enable = allOK;
                }
            }
        }

        setEnabled(enable);
    }

    private List getSelectedMappedExtents() {
        List selectedEditParts = new ArrayList(editor.getDiagramViewer().getSelectedEditParts());
        boolean allOK = true;
        Object nextObject = null;
        Iterator iter = selectedEditParts.iterator();
        while (iter.hasNext() && allOK) {
            nextObject = iter.next();
            if (!(nextObject instanceof MappingExtentEditPart)) allOK = false;
        }

        if (allOK) return selectedEditParts;
        return Collections.EMPTY_LIST;
    }
}
