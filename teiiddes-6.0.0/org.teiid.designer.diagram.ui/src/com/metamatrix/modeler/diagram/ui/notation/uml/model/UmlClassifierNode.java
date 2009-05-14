/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlAssociation;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.UmlRelationshipFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.ExpandableNode;
import com.metamatrix.modeler.diagram.ui.part.ExpandableDiagram;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @author mdrilling Model Node for UML Classifier.
 */
public class UmlClassifierNode extends UmlModelNode implements ExpandableNode {

    boolean expandedState = false;

    private UmlClassifierContainerNode attributesContainer;
    private UmlClassifierContainerNode associationsContainer;
    private UmlClassifierContainerNode operationsContainer;
    private UmlClassifierContainerNode classifiersContainer;

    public UmlClassifierNode( Diagram diagram,
                              EObject modelObject,
                              UmlClassifier aspect ) {
        super(diagram, modelObject, aspect);

        reconcile();

    }

    public UmlClassifierNode( Diagram diagram,
                              EObject modelObject,
                              UmlClassifier aspect,
                              boolean isNested ) {
        super(diagram, modelObject, aspect, isNested);

        reconcile();

    }

    public boolean showInnerClasses( EObject eObj,
                                     Diagram diagram ) {
        boolean show = false;

        IClassifierContentAdapter classAdapter = DiagramUiPlugin.getDiagramTypeManager().getDiagram(diagram.getType()).getClassifierContentAdapter();
        if (classAdapter != null) show = classAdapter.showInnerClasses(eObj, diagram);

        return show;
    }

    /**
     * Helper method to get the UmlAspect given an EObject
     */
    public MetamodelAspect getUmlAspect( EObject eObject ) {

        return DiagramUiPlugin.getDiagramAspectManager().getUmlAspect(eObject);
    }

    @Override
    public String getName() {
        return aspect.getSignature(getRealModelObject(), UmlClassifier.SIGNATURE_NAME);
    }

    @Override
    public String getPath() {
        String somePath = null;

        if (getDiagramModelObject() != null) {
            Diagram theDiagram = getDiagramModelObject().getDiagram();
            somePath = DiagramUiPlugin.getDiagramTypeManager().getDisplayedPath(theDiagram, getRealModelObject());
        }

        return somePath;
    }

    public void refreshForNameChange() {
        String oldName = "xxxXxxx"; //$NON-NLS-1$
        if (getDiagramModelObject() != null && !isReadOnly()) {
            DiagramEntityAdapter.setName(getDiagramModelObject(), getName());
        }

        firePropertyChange(DiagramNodeProperties.NAME, oldName, getName());
    }

    public void refreshForPathChange() {
        update(DiagramNodeProperties.PATH);
    }

    @Override
    public void setName( String name ) {
        // aspect.setSignature(getModelObject(),name);
        ModelObjectUtilities.rename(getRealModelObject(), name, this);
        // super.setName(name);
    }

    public void setSignature( String signature ) {
        aspect.setSignature(getRealModelObject(), signature);
    }

    // public String getPath() {
    // return aspect.getSignature(getModelObject(),UmlClassifier.SIGNATURE_NAME);
    // }

    @Override
    public String toString() {
        return "UmlClassifierNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public List getAttributes() {
        if (attributesContainer != null) return attributesContainer.getChildren();

        return Collections.EMPTY_LIST;
    }

    public DiagramModelNode getAttributesContainer() {
        return attributesContainer;
    }

    @Override
    public List getAssociations( HashMap nodeMap ) {
        IPreferenceStore store = DiagramUiPlugin.getDefault().getPreferenceStore();
        boolean showRoles = store.getBoolean(PluginConstants.Prefs.SHOW_FK_NAME);
        boolean showMulti = store.getBoolean(PluginConstants.Prefs.SHOW_FK_MULTIPLICITY);

        MetamodelAspect classifierAspect = getUmlAspect(getRealModelObject());

        if (classifierAspect instanceof UmlClassifier) {
            List returnAssociations = new ArrayList();
            DiagramModelNode diagramRootModelObject = this.getParent();

            Collection relationships = ((UmlClassifier)classifierAspect).getRelationships(getRealModelObject());
            Collection superTypes = ((UmlClassifier)classifierAspect).getSupertypes(getRealModelObject());

            List allAssociations = new ArrayList();
            if (relationships != null && !relationships.isEmpty()) {
                Iterator iter = relationships.iterator();
                Object nextObject = null;
                while (iter.hasNext()) {
                    nextObject = iter.next();
                    if (!allAssociations.contains(nextObject)) allAssociations.add(nextObject);
                }
            }

            if (superTypes != null && !superTypes.isEmpty()) {
                Iterator iter = superTypes.iterator();
                Object nextObject = null;
                while (iter.hasNext()) {
                    nextObject = iter.next();
                    if (!allAssociations.contains(nextObject)) allAssociations.add(nextObject);
                }
            }

            List allBasses = UmlRelationshipFactory.getBinaryAssociations(allAssociations, getRealModelObject());

            // allAssociations will be a list of BinaryAssociations.....
            Iterator iter = allBasses.iterator();
            BinaryAssociation nextBass = null;
            NodeConnectionModel newConnectionModel = null;

            while (iter.hasNext()) {
                nextBass = (BinaryAssociation)iter.next();
                newConnectionModel = UmlRelationshipFactory.getConnectionModel(nextBass, diagramRootModelObject, nodeMap);
                if (newConnectionModel instanceof DiagramUmlAssociation) {
                    ((DiagramUmlAssociation)newConnectionModel).setShowRoles(showRoles);
                    ((DiagramUmlAssociation)newConnectionModel).setShowMultiplicity(showMulti);
                }
                if (newConnectionModel != null) {
                    returnAssociations.add(newConnectionModel);
                }

            }
            if (returnAssociations.isEmpty()) return Collections.EMPTY_LIST;
            return returnAssociations;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * This method provides a consistent way for the classifier node to keep in synch with it's eObject. The reconcile() method
     * will insure that the attributes of the classifier (all containers) are consistent with the model provider and in the right
     * order.
     * 
     * @param eObj
     * @return
     */
    public DiagramModelNode addChild( EObject eObj ) {

        reconcile();

        DiagramModelNode newNode = getChild(eObj);

        if (newNode != null) updateForChild(true);

        return newNode;
    }

    public void removeChild( DiagramModelNode childNode ) {
        // Check containers and if any child's modelObject == eObj, then then
        // tell the container's modelObject to removeChild(node);

        if (attributesContainer != null && attributesContainer.getChildren().contains(childNode)) {
            attributesContainer.removeChild(childNode, false);
            if (attributesContainer.getChildren() == null || attributesContainer.getChildren().isEmpty()) {
                super.removeChild(attributesContainer, false);
                attributesContainer = null;
            }
        } else if (associationsContainer != null && associationsContainer.getChildren().contains(childNode)) {
            associationsContainer.removeChild(childNode, false);
            if (associationsContainer.getChildren() == null || associationsContainer.getChildren().isEmpty()) {
                super.removeChild(associationsContainer, false);
                associationsContainer = null;
            }
        } else if (operationsContainer != null && operationsContainer.getChildren().contains(childNode)) {
            operationsContainer.removeChild(childNode, false);
            if (operationsContainer.getChildren() == null || operationsContainer.getChildren().isEmpty()) {
                super.removeChild(operationsContainer, false);
                operationsContainer = null;
            }
        } else if (classifiersContainer != null && classifiersContainer.getChildren().contains(childNode)) {
            classifiersContainer.removeChild(childNode, false);
            if (classifiersContainer.getChildren() == null || classifiersContainer.getChildren().isEmpty()) {
                super.removeChild(classifiersContainer, false);
                classifiersContainer = null;
            }
        }
        updateForChild(false);
    }

    @Override
    public void updateForChild( boolean isMove ) {
        if (isMove) {
            reorderChildren();
            if (attributesContainer != null) attributesContainer.updateForChild(false);
            if (associationsContainer != null) associationsContainer.updateForChild(false);
            if (operationsContainer != null) operationsContainer.updateForChild(false);
            if (classifiersContainer != null) classifiersContainer.updateForChild(false);

            super.updateForChild(false);
        } else super.updateForChild(isMove);
    }

    public void reorderChildren() {
        // Find the diagram.
        Diagram diagram = null;

        if (getDiagramModelObject() == null) {
            DiagramModelNode diagramNode = DiagramUiUtilities.getTopClassifierParentNode(this);

            Object mo = diagramNode.getDiagram();

            if (mo instanceof Diagram) {
                diagram = (Diagram)mo;
            } else {
                // We shouldn't get here...
                String message = " UmlClassifierNode.reorderChildren():  modelObject NOT Diagram. Object = " + mo + " for DiagramModelNode = " + diagramNode; //$NON-NLS-1$  //$NON-NLS-2$
                DiagramUiConstants.Util.log(IStatus.ERROR, message);
            }
        } else {
            diagram = (Diagram)getDiagramModelObject().eContainer();
        }

        if (diagram != null) {
            EObject modelObject = getRealModelObject();
            boolean showInnerClasses = showInnerClasses(modelObject, diagram);
            // This method reconciles the children of each container with the children of the Classifier.
            List list = getChildren(getRealModelObject());
            Iterator iter = list.iterator();
            // Gather up each type separately
            List newAttributeList = new ArrayList();
            List newAssociationList = new ArrayList();
            List newOperationList = new ArrayList();
            List newClassifiersList = new ArrayList();
            // Iterate
            while (iter.hasNext()) {
                EObject eObj = (EObject)iter.next();
                MetamodelAspect mmAspect = getUmlAspect(eObj);

                if (mmAspect instanceof UmlProperty) {
                    newAttributeList.add(eObj);
                } else if (mmAspect instanceof UmlAssociation) {
                    newAssociationList.add(eObj);
                } else if (mmAspect instanceof UmlOperation) {
                    newOperationList.add(eObj);
                } else if (mmAspect instanceof UmlClassifier && showInnerClasses) {
                    newClassifiersList.add(eObj);
                }

            }

            // Now add the appropriate container children to this Classifier
            if (!newAttributeList.isEmpty() && attributesContainer != null) {
                attributesContainer.reorderChildren(newAttributeList);
            }
            if (!newAssociationList.isEmpty() && associationsContainer != null) {
                associationsContainer.reorderChildren(newAssociationList);
            }
            if (!newOperationList.isEmpty() && operationsContainer != null) {
                operationsContainer.reorderChildren(newOperationList);
            }
            if (!newClassifiersList.isEmpty() && classifiersContainer != null) {
                classifiersContainer.reorderChildren(newClassifiersList);
            }
        }
    }

    protected List getChildren( EObject parent ) {
        return DiagramUiPlugin.getDiagramAspectManager().getChildren(parent);
    }

    private DiagramModelNode getChild( EObject eObj ) {
        DiagramModelNode childNode = null;
        // Walk through the containers and ask for a child
        // Now add the appropriate container children to this Classifier
        if (attributesContainer != null) {
            childNode = attributesContainer.getChild(eObj);
        }
        if (childNode == null && associationsContainer != null) {
            childNode = associationsContainer.getChild(eObj);
        }
        if (childNode == null && operationsContainer != null) {
            childNode = operationsContainer.getChild(eObj);
        }
        if (childNode == null && classifiersContainer != null) {
            childNode = classifiersContainer.getChild(eObj);
        }

        return childNode;
    }

    public void reconcile() {
        if (getRealModelObject() != null) {
            boolean showInnerClasses = showInnerClasses(getRealModelObject(), getDiagram());
            // Get the EObject Contents (children) - create appropriate ModelNodes
            List list = getChildren(getRealModelObject());
            Iterator iter = list.iterator();

            // Gather up each type separately
            List attributeList = new ArrayList();
            List associationList = new ArrayList();
            List operationList = new ArrayList();
            List classifiersList = new ArrayList();

            // Iterate
            while (iter.hasNext()) {
                EObject eObj = (EObject)iter.next();
                MetamodelAspect mmAspect = getUmlAspect(eObj);

                if (mmAspect instanceof UmlProperty) {
                    attributeList.add(eObj);
                } else if (mmAspect instanceof UmlAssociation) {
                    associationList.add(eObj);
                } else if (mmAspect instanceof UmlOperation) {
                    operationList.add(eObj);
                } else if (mmAspect instanceof UmlClassifier && showInnerClasses) {
                    classifiersList.add(eObj);
                }
            }

            // Now add the appropriate container children to this Classifier
            if (!attributeList.isEmpty()) {
                if (attributesContainer == null) {
                    attributesContainer = new UmlClassifierContainerNode(UmlClassifierContainerNode.ATTRIBUTES, attributeList,
                                                                         getDiagram());
                    attributesContainer.setParent(this);
                    super.addChild(attributesContainer);
                } else {
                    attributesContainer.reconcile(attributeList, getDiagram());
                }
            } else {
                if (attributesContainer != null) {
                    super.removeChild(attributesContainer, false);
                    attributesContainer = null;
                }
            }
            if (!associationList.isEmpty()) {
                if (associationsContainer == null) {
                    associationsContainer = new UmlClassifierContainerNode(UmlClassifierContainerNode.ASSOCIATIONS,
                                                                           associationList, getDiagram());
                    associationsContainer.setParent(this);
                    super.addChild(associationsContainer);
                } else {
                    associationsContainer.reconcile(associationList, getDiagram());
                }
            } else {
                if (associationsContainer != null) {
                    super.removeChild(associationsContainer, false);
                    associationsContainer = null;
                }
            }
            if (!operationList.isEmpty()) {
                if (operationsContainer == null) {
                    operationsContainer = new UmlClassifierContainerNode(UmlClassifierContainerNode.ASSOCIATIONS, operationList,
                                                                         getDiagram());
                    operationsContainer.setParent(this);
                    super.addChild(operationsContainer);
                } else {
                    operationsContainer.reconcile(operationList, getDiagram());
                }
            } else {
                if (operationsContainer != null) {
                    super.removeChild(operationsContainer, false);
                    operationsContainer = null;
                }
            }
            if (!classifiersList.isEmpty()) {
                if (classifiersContainer == null) {
                    classifiersContainer = new UmlClassifierContainerNode(UmlClassifierContainerNode.CLASSIFIERS,
                                                                          classifiersList, getDiagram());
                    classifiersContainer.setParent(this);
                    super.addChild(classifiersContainer);
                } else {
                    classifiersContainer.reconcile(classifiersList, getDiagram());
                }
            } else {
                if (classifiersContainer != null) {
                    super.removeChild(classifiersContainer, false);
                    classifiersContainer = null;
                }
            }
            setErrorState();
            reorderChildren();
        }
    }

    public boolean isAbstract() {
        return ((UmlClassifier)getUmlAspect()).isAbstract(getRealModelObject());
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.model.AbstractDiagramModelNode#setErrorState()
     * @since 4.2
     */
    @Override
    public void setErrorState() {
        super.setErrorState();
        if (!hasWarnings() && !hasErrors()) {
            // Need to check children for errors.
            int errorValue = childErrorState();
            if (errorValue == DiagramUiConstants.HAS_ERROR) {
                errorState = true;
            } else if (errorValue == DiagramUiConstants.HAS_WARNING) {
                warningState = true;
            }
        } else if (hasWarnings()) {
            // Need to check children for errors.
            int errorValue = childErrorState();
            if (errorValue == DiagramUiConstants.HAS_ERROR) {
                errorState = true;
                warningState = false;
            }
        }
    }

    private int childErrorState() {
        int state = DiagramUiConstants.NO_ERRORS; // NO ERRORS
        Iterator iter = null;
        DiagramModelNode nextNode = null;
        if (attributesContainer != null && attributesContainer.getChildren() != null
            && !attributesContainer.getChildren().isEmpty()) {
            iter = attributesContainer.getChildren().iterator();
            while (iter.hasNext() && state < 2) {
                nextNode = (DiagramModelNode)iter.next();
                if (nextNode.hasWarnings()) state = DiagramUiConstants.HAS_WARNING;
                if (nextNode.hasErrors()) state = DiagramUiConstants.HAS_ERROR;
            }
        }
        if (state < 2 && associationsContainer != null && associationsContainer.getChildren() != null
            && !associationsContainer.getChildren().isEmpty()) {
            iter = associationsContainer.getChildren().iterator();
            while (iter.hasNext() && state < 2) {
                nextNode = (DiagramModelNode)iter.next();
                if (nextNode.hasWarnings()) state = DiagramUiConstants.HAS_WARNING;
                if (nextNode.hasErrors()) state = DiagramUiConstants.HAS_ERROR;
            }
        }
        if (state < 2 && operationsContainer != null && operationsContainer.getChildren() != null
            && !operationsContainer.getChildren().isEmpty()) {
            iter = operationsContainer.getChildren().iterator();
            while (iter.hasNext() && state < 2) {
                nextNode = (DiagramModelNode)iter.next();
                if (nextNode.hasWarnings()) state = DiagramUiConstants.HAS_WARNING;
                if (nextNode.hasErrors()) state = DiagramUiConstants.HAS_ERROR;
            }
        }
        if (state < 2 && classifiersContainer != null && classifiersContainer.getChildren() != null
            && !classifiersContainer.getChildren().isEmpty()) {
            iter = classifiersContainer.getChildren().iterator();
            while (iter.hasNext() && state < 2) {
                nextNode = (DiagramModelNode)iter.next();
                if (nextNode.hasWarnings()) state = DiagramUiConstants.HAS_WARNING;
                if (nextNode.hasErrors()) state = DiagramUiConstants.HAS_ERROR;
            }
        }
        return state;
    }

    protected EObject getRealModelObject() {
        return getModelObject();
    }

    private boolean canExpand() {
        return (this.getParent() instanceof ExpandableDiagram && ((ExpandableDiagram)this.getParent()).canExpand());
    }

    public void setExpandedState( boolean b ) {
        this.expandedState = b;
    }

    public boolean isExpanded() {
        // If classifier is contained on an expandable diagram
        // return the expanded state.
        if (canExpand()) return expandedState;

        // Else return TRUE always
        return true;
    }

    public void expand() {
        if (canExpand()) {
            expandedState = true;
            firePropertyChange(DiagramNodeProperties.EXPAND, null, null);
            getParent().firePropertyChange(DiagramNodeProperties.EXPAND, null, null);
        }
    }

    public void collapse() {
        if (canExpand()) {
            expandedState = false;
            firePropertyChange(DiagramNodeProperties.COLLAPSE, null, null);
            getParent().firePropertyChange(DiagramNodeProperties.EXPAND, null, null);
        }
    }

    @Override
    public List getChildren() {
        if (isExpanded()) return super.getChildren();

        return Collections.EMPTY_LIST;
    }
}
