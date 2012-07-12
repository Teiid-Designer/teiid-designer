/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.custom.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.actions.DiagramAction;
import org.teiid.designer.diagram.ui.custom.CustomDiagramModelFactory;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.editor.DiagramEditorUtil;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * RemoveTransformationSource
 */
public class RemoveFromDiagramAction extends DiagramAction {
    private static final String THIS_CLASS = "RemoveFromDiagramAction"; //$NON-NLS-1$
    private DiagramEditor editor;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public RemoveFromDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.REMOVE_FROM_DIAGRAM));
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        super.selectionChanged(thePart, theSelection);

        setEnabled(shouldEnable());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        if (editor != null) {

            // Need to get the current diagram
            DiagramModelNode diagramNode = editor.getCurrentModel();
            Diagram diagram = (Diagram)diagramNode.getModelObject();
            // Need to get ahold of the CustomDiagramModelFactory
            CustomDiagramModelFactory modelFactory = (CustomDiagramModelFactory)editor.getModelFactory();
            // And call add(SelectionUtilities.getSelectedEObjects(getSelection())

            if (modelFactory != null) {
                boolean handleConstruction = !DiagramEditorUtil.isDiagramUnderConstruction(diagram);
                boolean requiredStart = false;
                boolean succeeded = false;
                try {
                    if (handleConstruction) {
                        DiagramEditorUtil.setDiagramUnderConstruction(diagram);
                    }
                    // -------------------------------------------------
                    // Let's wrap this in a transaction!!!
                    // -------------------------------------------------

                    requiredStart = ModelerCore.startTxn(true, false, "Add To Custom Diagram", this); //$NON-NLS-1$$

                    modelFactory.remove(SelectionUtilities.getSelectedEObjects(getSelection()), diagramNode);

                    succeeded = true;
                } catch (Exception ex) {
                    DiagramUiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".doRun()"); //$NON-NLS-1$  //$NON-NLS-2$
                } finally {
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                    if (handleConstruction) {
                        DiagramEditorUtil.setDiagramConstructionComplete(diagram, true);
                    }
                }

            }
        }
    }

    private boolean shouldEnable() {
        if (!(this.getPart() instanceof ModelEditor) || getSelection() == null
            || SelectionUtilities.getSelectedEObjects(getSelection()).size() < 0) return false;

        return allSelectedInDiagram() && isWritable();
    }

    private boolean allSelectedInDiagram() {
        // check the diagram to see if all selected objects are in diagram??
        if (editor != null && editor.getDiagramViewer() != null && editor.getDiagramViewer().getSelectionHandler() != null) {
            List selectedEObjects = editor.getDiagramViewer().getSelectionHandler().getSelectedEObjects();
            if (selectedEObjects == null || selectedEObjects.isEmpty()) return false;

            Iterator iter = SelectionUtilities.getSelectedEObjects(getSelection()).iterator();
            EObject eObj = null;
            while (iter.hasNext()) {
                eObj = (EObject)iter.next();
                if (!selectedEObjects.contains(eObj)) return false;
            }
        }

        return true;
    }

    public void setDiagramEditor( DiagramEditor editor ) {
        this.editor = editor;
    }

    private boolean isWritable() {
        if (editor != null) {
            DiagramModelNode currentDiagram = editor.getCurrentModel();
            if (currentDiagram != null) {
                EObject diagram = currentDiagram.getModelObject();
                if (!ModelObjectUtilities.isReadOnly(diagram)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling the run at the last
     * minute. This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        if (requiresEditorForRun()) {
            List allSelectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            if (allSelectedEObjects != null && !allSelectedEObjects.isEmpty()) {
                EObject eObject = (EObject)allSelectedEObjects.get(0);
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObject);
                if (mr != null) {
                    ModelEditorManager.activate(mr, false);
                }
            }
        }
        return true;
    }
}
