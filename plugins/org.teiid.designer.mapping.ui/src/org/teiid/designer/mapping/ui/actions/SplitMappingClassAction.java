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
import org.teiid.designer.mapping.factory.MappingClassFactory;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.editors.ModelEditor;


/**
 * SplitMappingClassAction
 *
 * @since 8.0
 */
public class SplitMappingClassAction extends MappingAction {
    private static final String ERROR_MESSAGE = "Cannot Split Mapping Class.\n\n" + //$NON-NLS-1$
                                                "Current mapping class multiplicity values are satisfied.\n" + //$NON-NLS-1$
                                                "Cannot automatically split.\n" + //$NON-NLS-1$
                                                "Split manually by selecting a node (i.e. sequence) in the mapping tree \n" + //$NON-NLS-1$
                                                "and selecting the \"New Mapping Class\" action."; //$NON-NLS-1$
    private static final String ACTION_DESCRIPTION = "Split Mapping Class"; //$NON-NLS-1$

    public SplitMappingClassAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.SPLIT_MAPPING_CLASS));
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
            EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());

            boolean canSplit = getMappingClassFactory().canSplitMappingClass((MappingClass)eObject,
                                                                             MappingClassFactory.getDefaultStrategy());

            if (canSplit) {
                boolean canUndo = IMappingDiagramActionConstants.DiagramActions.UNDO_SPLIT_MAPPING_CLASS;
                // start txn
                boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
                boolean succeeded = false;
                try {
                    getMappingClassFactory().splitMappingClass((MappingClass)eObject,
                                                               MappingClassFactory.getDefaultStrategy(),
                                                               false);
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
                // Throw up a dialog here to warn the user that split could not happen.
                WidgetUtil.showWarning(ERROR_MESSAGE);
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
            EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
            if (eObj != null && isWritable() && isMappingClass(eObj) && !isStagingTable(eObj) && getMappingClassFactory() != null) {
                enable = true;
            }
        }

        setEnabled(enable);
    }
}
