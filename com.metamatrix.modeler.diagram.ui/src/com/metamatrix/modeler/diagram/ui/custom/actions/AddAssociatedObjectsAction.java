/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.custom.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.DiagramAction;
import com.metamatrix.modeler.diagram.ui.connection.UmlRelationshipHelper;
import com.metamatrix.modeler.diagram.ui.custom.CustomDiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * AddAssociatedObjectsAction This class provides custom diagrams the ability to allow the user to add any objects associated with
 * objects selected in the diagram. This action should only be offered on diagram node selection.
 */
public class AddAssociatedObjectsAction extends DiagramAction {
    private static final String THIS_CLASS = "AddAssociatedObjectsAction"; //$NON-NLS-1$
    private DiagramEditor editor;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public AddAssociatedObjectsAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.ADD_ASSOCIATED_OBJECTS));
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

        // initialize abstract base class info
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

            List objectsToAdd = getAssociatedObjectsNotInDiagram();

            if (!objectsToAdd.isEmpty() && modelFactory != null) {

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

                    requiredStart = ModelerCore.startTxn(true, false, "Add Related Objects", this); //$NON-NLS-1$$

                    modelFactory.add(objectsToAdd, diagramNode);

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

        return allSelectedInDiagram() && isWritable() && allObjectsAddable();
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
                return !isReadOnly();
            }
        }
        return false;
    }

    private boolean allObjectsAddable() {
        boolean allOK = true;

        List objectsToAdd = getAssociatedObjectsNotInDiagram();
        if (objectsToAdd == null || objectsToAdd.isEmpty()) allOK = false;

        return allOK;
    }

    private List getAssociatedObjectsNotInDiagram() {
        List newObjects = new ArrayList();
        if (editor != null && editor.getCurrentModel() != null) {
            DiagramModelNode diagramNode = editor.getCurrentModel();
            List allAssociatedToAdd = UmlRelationshipHelper.getRelatedObjects(SelectionUtilities.getSelectedEObjects(getSelection()));
            Iterator iter = allAssociatedToAdd.iterator();
            EObject nextEObj = null;
            while (iter.hasNext()) {
                nextEObj = (EObject)iter.next();
                if (editor != null && editor.getCurrentModel() != null) {
                    if (!(DiagramUiUtilities.diagramContainsEObject(nextEObj, diagramNode))) newObjects.add(nextEObj);
                }
            }
        }

        if (newObjects.isEmpty()) return Collections.EMPTY_LIST;

        return newObjects;
    }

    @Override
    protected boolean requiresEditorForRun() {
        return false;
    }
}
