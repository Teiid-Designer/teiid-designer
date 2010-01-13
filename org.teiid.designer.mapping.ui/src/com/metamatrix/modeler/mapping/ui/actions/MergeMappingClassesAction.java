/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class MergeMappingClassesAction extends MappingAction {
    private static final String ACTION_DESCRIPTION = "Merge Mapping Classes"; //$NON-NLS-1$
    private static final String MERGE_MAPPING_CLASSES_ERROR = "MergeMappingClassesAction: Error merging mapping classes. Object = "; //$NON-NLS-1$

    public MergeMappingClassesAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.MERGE_MAPPING_CLASSES));
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        super.selectionChanged(thePart, theSelection);
        determineEnablement();
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        if (getMappingClassFactory() != null) {
            mergeWithProgress();
        }

        setEnabled(false);
    }

    private boolean mergeWithProgress() {
        boolean success = false;
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) {
                Object[] mcArray = getOrderedSelectedMappingClasses();
                printMCArray(mcArray);
                int nWork = 10 * mcArray.length;
                monitor.beginTask("Merging Mapping Classes", nWork); //$NON-NLS-1$
                mergeMultiple(mcArray, monitor);
            }
        };

        try {
            final ProgressMonitorDialog dlg = new ProgressMonitorDialog(getPlugin().getCurrentWorkbenchWindow().getShell());
            dlg.setCancelable(false);
            dlg.run(false, true, op);
            if (dlg.getProgressMonitor().isCanceled()) {
                return true;
            }

            success = true;
        } catch (final InterruptedException ignored) {
            success = true;
        } catch (final Exception err) {
            success = false;
        }

        return success;
    }

    void mergeMultiple( final Object[] mcArray,
                        IProgressMonitor monitor ) {
        boolean canUndo = IMappingDiagramActionConstants.DiagramActions.UNDO_MERGE_MAPPING_CLASSES;
        boolean removeDuplicateAttributes = ModelerCore.getTransformationPreferences().getRemoveDuplicateAttibutes();
        // start txn
        boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
        boolean succeeded = false;
        MappingClass firstMC = (MappingClass)mcArray[0];
        MappingClass secondMC = null;
        try {
            String progressMessage = null;
            int nMCs = mcArray.length;
            for (int i = 1; i < nMCs; i++) {
                secondMC = (MappingClass)mcArray[i];
                progressMessage = "Merging " + (i + 1) + " of " + nMCs + ":  Name = " + secondMC.getName(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                monitor.subTask(progressMessage);
                monitor.worked(10);
                // Do the work
                // System.out.println("\n  ACTION ------- Message = " + progressMessage);
                getMappingClassFactory().mergeMappingClasses(firstMC, secondMC, removeDuplicateAttributes);
            }
            succeeded = true;
        } catch (ModelerCoreException e) {
            String message = MERGE_MAPPING_CLASSES_ERROR + firstMC.toString();
            UiConstants.Util.log(IStatus.ERROR, e, message);
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

    private boolean enableForMultiple() {
        if (this.getPart() instanceof ModelEditor && multipleMappingClassesSelected()) {

            Object[] mcArray = getOrderedSelectedMappingClasses();
            // Now we need to walk through all the mapping classes two by two and check "can Merge"
            // For each mapping class 1....n, check if can merge from j, through 0. If we find ONE, then it can
            // merge, if we find NONE, then we can't.

            MappingClass firstMC = null;
            MappingClass secondMC = null;
            boolean canMerge = true;

            for (int i = 1; i < mcArray.length; i++) {
                for (int j = i - 1; j >= 0; j--) {
                    firstMC = (MappingClass)mcArray[j];
                    secondMC = (MappingClass)mcArray[i];
                    canMerge = getMappingClassFactory().canMergeMappingClasses(firstMC, secondMC);
                    if (canMerge) return true;
                }
            }
        }

        return false;
    }

    Object[] getOrderedSelectedMappingClasses() {
        // Need to order the list of selected mapping classes
        List allOrderedMCs = MappingDiagramUtil.getOrderedCoarseMappingClasses();
        List selectedMCs = SelectionUtilities.getSelectedEObjects(getSelection());
        List orderedSelectedMCs = new ArrayList(selectedMCs.size());

        // Now we need to order the selected mapping classes
        EObject nextMC = null;
        // Walk through the allOrderedMCs, and if an MC exists in selectedMCs, add it to the final list
        for (Iterator iter = allOrderedMCs.iterator(); iter.hasNext();) {
            nextMC = (EObject)iter.next();
            if (selectedMCs.contains(nextMC)) {
                orderedSelectedMCs.add(nextMC);
            }
        }

        return orderedSelectedMCs.toArray();

    }

    private void determineEnablement() {
        setEnabled(enableForMultiple());
    }

    private boolean multipleMappingClassesSelected() {
        if (SelectionUtilities.isMultiSelection(getSelection()) && isWritable()) {

            List selectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            if (selectedEObjects.size() > 1) {
                EObject eObj = null;
                for (Iterator iter = selectedEObjects.iterator(); iter.hasNext();) {
                    eObj = (EObject)iter.next();
                    if (isStagingTable(eObj) || !isMappingClass(eObj)) return false;
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "MergeMappingClassesAction[]"; //$NON-NLS-1$
    }

    void printMCArray( Object[] mcArray ) {
    }

}
