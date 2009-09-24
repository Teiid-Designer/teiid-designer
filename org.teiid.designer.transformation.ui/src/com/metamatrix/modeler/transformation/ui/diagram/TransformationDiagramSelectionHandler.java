/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlAttributeEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.model.TransformationNode;

/**
 * TransformationDiagramSelectionHandler
 */
public class TransformationDiagramSelectionHandler extends DiagramSelectionHandler {

    /**
     * Construct an instance of MappingDiagramSelectionHandler.
     * 
     * @param viewer
     */
    public TransformationDiagramSelectionHandler( DiagramViewer viewer ) {
        super(viewer);
    }

    @Override
    public void select( EObject selectedObject ) {
        super.select(selectedObject);

        hiliteDependencies(selectedObject);
    }

    @Override
    public boolean shouldRename( EObject dClickedEObject ) {
        // Need to check if the double-clicked EObject is either the "Target" table, or a child/grandchild of it
        // 1) Get the target table of the transform
        boolean renameOK = false;
        EObject target = getTargetEObject();
        if (target.equals(dClickedEObject)) {
            renameOK = true;
        } else {
            // Get the top classifier for the selection
            EditPart selectedPart = findEditPart(dClickedEObject, false);
            if (selectedPart != null && selectedPart instanceof DiagramEditPart) {
                DiagramEditPart dep = DiagramUiUtilities.getTopClassifierParent((DiagramEditPart)selectedPart);
                if (dep != null) {
                    EObject parentTableEObject = dep.getModelObject();
                    if (target.equals(parentTableEObject)) {
                        renameOK = true;
                    }
                }
            }
        }

        return renameOK;
    }

    // This method overides the base class method so we can hilite the extents for selecting a Mapping Class
    @Override
    public void hiliteDependencies( Object selectedObject ) {
        clearDependencyHilites();

        if (selectedObject != null && selectedObject instanceof EObject) {
            EObject selectedEObject = (EObject)selectedObject;

            EditPart selectedPart = findEditPart(selectedEObject, false);
            if (selectedPart != null && selectedPart instanceof UmlAttributeEditPart) {
                super.hiliteDependencies(selectedObject);

                List allDependencies = new ArrayList(findAllDependencies((EObject)selectedObject));

                Iterator iter = allDependencies.iterator();
                EditPart nextEP = null;
                EObject nextEObject = null;
                while (iter.hasNext()) {
                    nextEObject = (EObject)iter.next();
                    nextEP = findEditPart(nextEObject, false);
                    if (nextEP != null && nextEP instanceof DiagramEditPart) {
                        ((DiagramEditPart)nextEP).hiliteBackground(UiConstants.Colors.DEPENDENCY);
                        if (UiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.diagram.ui.DebugConstants.DIAGRAM_SELECTION)) {
                            String debugMessage = "hiliteDependencies():  Dependent object = " + nextEObject; //$NON-NLS-1$
                            UiConstants.Util.print(this.getClass(), debugMessage);
                        }
                    }
                }
            }
        }
    }

    private List findAllDependencies( EObject selectedAttribute ) {

        List allDependencies = new ArrayList();

        Iterator iter = null;
        Object nextObject = null;
        // Let's check to see if the selected attribute is "virtual" or not.
        if (ModelObjectUtilities.isVirtual(selectedAttribute)) {
            // if it's virtual, then we need to find all Source dependencies
            List allSourceDependencies = new ArrayList();

            getSourceDependencies(selectedAttribute, allSourceDependencies);

            iter = allSourceDependencies.iterator();
            while (iter.hasNext()) {
                nextObject = iter.next();
                if (!allDependencies.contains(nextObject)) allDependencies.add(nextObject);
            }
        }

        // Now we work on target dependencies (could be either physical or virtual

        List allTargetDependencies = new ArrayList();

        getTargetDependencies(selectedAttribute, allTargetDependencies);

        iter = allTargetDependencies.iterator();
        while (iter.hasNext()) {
            nextObject = iter.next();
            if (!allDependencies.contains(nextObject)) allDependencies.add(nextObject);
        }

        if (allDependencies.isEmpty()) return Collections.EMPTY_LIST;

        return allDependencies;
    }

    /*
     * This method initiates the search for source dependencies
     */
    private List getSourceDependencies( EObject selectedAttribute,
                                        List sourceDependencyList ) {

        // As long as it's virtual attribute we recursively search.
        if (ModelObjectUtilities.isVirtual(selectedAttribute)) {

            // Get the attribute's parent
            EObject virtualTarget = selectedAttribute.eContainer();
            // if virtualTarget is a Procedure ResultSet, get the parent Procedure
            if (!TransformationHelper.isValidSqlTransformationTarget(virtualTarget)
                && TransformationHelper.isSqlColumnSet(virtualTarget)) {
                virtualTarget = virtualTarget.eContainer();
            }

            if (virtualTarget != null) {
                TransformationMappingRoot tmr = (TransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(virtualTarget);

                List attrDepList = new ArrayList(TransformationHelper.getSourceAttributesForTargetAttr(selectedAttribute, tmr));

                EObject nextEObject = null;
                Iterator iter = attrDepList.iterator();
                while (iter.hasNext()) {
                    nextEObject = (EObject)iter.next();
                    if (!nextEObject.equals(selectedAttribute)) {
                        EditPart visibleEditPart = findEditPart(nextEObject, false);
                        // As long as we find the edit part, we should keep looking
                        if (visibleEditPart != null && visibleEditPart instanceof DiagramEditPart) {
                            if (!sourceDependencyList.contains(nextEObject)) {
                                sourceDependencyList.add(nextEObject);

                                if (ModelObjectUtilities.isVirtual(nextEObject)) {
                                    // Now get it's dependencies and add them. (recursive);
                                    getSourceDependencies(nextEObject, sourceDependencyList);
                                }
                            }
                        }
                    }
                }
            }
        }

        return sourceDependencyList;
    }

    /*
     * Recursively walks target dependencies, on any visible transformations.
     */
    private List getTargetDependencies( EObject selectedAttribute,
                                        List targetDependencyList ) {

        EObject targetClassifier = selectedAttribute.eContainer();

        List allTargetTransformations = getTargetTransformationsForSource(targetClassifier);

        // Now we walk through these transformations and get a list of "target" attributes...

        Iterator iter = allTargetTransformations.iterator();
        TransformationMappingRoot nextTMR = null;

        while (iter.hasNext()) {
            nextTMR = (TransformationMappingRoot)iter.next();

            List attrDepList = new ArrayList(TransformationHelper.getTargetAttributesForSourceAttr(selectedAttribute, nextTMR));

            Iterator iter2 = attrDepList.iterator();
            EObject nextEObject = null;
            while (iter2.hasNext()) {
                nextEObject = (EObject)iter2.next();
                if (!nextEObject.equals(selectedAttribute)) {
                    EditPart visibleEditPart = findEditPart(nextEObject, false);
                    // As long as we find the edit part, we should keep looking
                    if (visibleEditPart != null && visibleEditPart instanceof DiagramEditPart) {
                        if (!targetDependencyList.contains(nextEObject)) {
                            targetDependencyList.add(nextEObject);
                            // Now get it's dependencies and add them. (recursive);
                            getTargetDependencies(nextEObject, targetDependencyList);
                            // targetDependencyList.addAll( getTargetDependencies(nextEObject) );
                        }
                    }
                }
            }

        }

        return targetDependencyList;
    }

    private List getVisibleTransformations() {
        List visibleTransformations = Collections.EMPTY_LIST;

        if (getViewer().getEditor().getCurrentModel() != null) {
            visibleTransformations = new ArrayList();
            List allDiagramModelNodes = getViewer().getEditor().getCurrentModel().getChildren();
            Object nextObject = null;
            Iterator iter = allDiagramModelNodes.iterator();
            EObject nextTransformation = null;
            while (iter.hasNext()) {
                nextObject = iter.next();
                if (nextObject instanceof TransformationNode) {
                    nextTransformation = ((DiagramModelNode)nextObject).getModelObject();
                    if (!visibleTransformations.contains(nextTransformation)) {
                        visibleTransformations.add(nextTransformation);
                    }
                }
            }
        }

        return visibleTransformations;
    }

    /**
     * This method will collect up a set of target groups/tables that are referenced as Inputs (sources) to transformation targets
     * (virtual groups)
     * 
     * @param eObject
     * @return List of target transformations for the input Source table
     */
    private List getTargetTransformationsForSource( EObject sourceTableObject ) {
        List visibleTransformations = getVisibleTransformations();
        // Result List of target transformations.
        List targetTransformations = Collections.EMPTY_LIST;

        if (!visibleTransformations.isEmpty()) {

            targetTransformations = new ArrayList(visibleTransformations.size());

            TransformationMappingRoot nextMR = null;
            Iterator iter = visibleTransformations.iterator();
            while (iter.hasNext()) {
                nextMR = (TransformationMappingRoot)iter.next();
                // get the "inputs"
                if (nextMR.getInputs() != null && !nextMR.getInputs().isEmpty() && nextMR.getInputs().contains(sourceTableObject)
                    && !targetTransformations.contains(nextMR)) {
                    targetTransformations.add(nextMR);
                }
            }
        }

        return targetTransformations;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler#findEditPart(org.eclipse.emf.ecore.EObject, boolean)
     * @since 4.2
     */
    @Override
    public EditPart findEditPart( EObject selectedObject,
                                  boolean linksAllowed ) {
        // EditPart matchingPart = null;
        // if selectedObject is same as t-diagram's target, than let's get the edit part for
        // the virtual group
        EObject vGroupEObject = getTargetEObject();
        if (vGroupEObject != null && vGroupEObject.equals(selectedObject)) {
            DiagramModelNode dmn = DiagramUiUtilities.getDiagramModelNode(vGroupEObject, getDiagramNode());
            return DiagramUiUtilities.getDiagramEditPart((DiagramEditPart)getDiagramEditPart(), dmn);
        }

        return super.findEditPart(selectedObject, linksAllowed);
    }

    private EObject getTargetEObject() {
        if (getViewer().getEditor().getCurrentModel() != null) {

            EObject diagram = getViewer().getEditor().getCurrentModel().getModelObject();

            if (diagram instanceof Diagram) {
                EObject targetEObject = ((Diagram)diagram).getTarget();
                return targetEObject;
            }
        }

        return null;
    }

    private DiagramModelNode getDiagramNode() {
        return getViewer().getEditor().getCurrentModel();
    }

    private EditPart getDiagramEditPart() {
        List contents = getViewer().getRootEditPart().getChildren();
        Iterator iter = contents.iterator();
        if (iter.hasNext()) {
            return (EditPart)iter.next();
        }
        return null;
    }
}
