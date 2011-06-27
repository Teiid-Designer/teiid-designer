/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierContainerNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.RelationalUmlEObjectHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.util.TransformationUmlEObjectHelper;

/**
 * @since 4.2
 */
public class TransformationDiagramNotificationHelper {
    private Notification primaryNotification = null;
    private DiagramModelNode diagramNode = null;
    private boolean reconcileTargetAttributes = false;
    private boolean reconcileSources = false;
    private boolean refreshDiagram;
    private List reconcileTables = null;
    private boolean handleNotification = false;
    private List changedNodes = null;
    private List movedNodes = null;
    private EObject targetEObject = null;

    /**
     * @since 4.2
     */
    public TransformationDiagramNotificationHelper( Notification notification,
                                                    DiagramModelNode transformationDiagramModelNode ) {
        super();
        this.primaryNotification = notification;
        this.diagramNode = transformationDiagramModelNode;
        init();
    }

    private void init() {
        reconcileTables = new ArrayList();
        changedNodes = new ArrayList();
        movedNodes = new ArrayList();
        setTransformation();
        setHandleNotification();
        setReconcileSources();
        setReconcileAttributes();
        setRefreshDiagram();
    }

    private void setTransformation() {
        targetEObject = ((Diagram)diagramNode.getModelObject()).getTarget();
    }

    private void setHandleNotification() {
        boolean shouldHandle = false;

        Diagram currentDiagram = (Diagram)diagramNode.getModelObject();

        ModelResource diagramMR = ModelUtilities.getModelResourceForModelObject(currentDiagram);
        if (diagramMR != null) {
            if (primaryNotification instanceof SourcedNotification) {
                Object source = ((SourcedNotification)primaryNotification).getSource();
                if (source == null || !source.equals(this)) {
                    Collection notifications = ((SourcedNotification)primaryNotification).getNotifications();
                    Iterator iter = notifications.iterator();
                    Notification nextNotification = null;

                    while (iter.hasNext() && !shouldHandle) {
                        nextNotification = (Notification)iter.next();
                        Object targetObject = ModelerCore.getModelEditor().getChangedObject(nextNotification);
                        if (targetObject != null && targetObject instanceof EObject
                            && !DiagramUiUtilities.isNonDrawingDiagramObject((EObject)targetObject)) {
                            shouldHandle = true;
                        } else if (targetObject instanceof Diagram && NotificationUtilities.isRemoved(nextNotification)) {
                            ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
                            if (mr != null && mr.equals(diagramMR)) {
                                shouldHandle = true;
                            }
                        }
                    }
                }
            } else { // SINGLE NOTIFICATION
                Object targetObject = ModelerCore.getModelEditor().getChangedObject(primaryNotification);
                if (targetObject != null && targetObject instanceof EObject
                    && !DiagramUiUtilities.isNonDrawingDiagramObject((EObject)targetObject)) {
                    shouldHandle = true;
                } else if (targetObject instanceof Diagram && NotificationUtilities.isRemoved(primaryNotification)) {
                    ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
                    if (mr != null && mr.equals(diagramMR)) {
                        shouldHandle = true;
                    }
                }
            }
        }

        handleNotification = shouldHandle;
    }

    /**
     * @return Returns the handleNotification.
     * @since 4.2
     */
    public boolean shouldHandleNotification() {
        return this.handleNotification;
    }

    /**
     * @return Returns the reconcileSources.
     * @since 4.2
     */
    public boolean shouldReconcileSources() {
        return this.reconcileSources;
    }

    private void setReconcileSources() {
        reconcileSources = false;
        String diagramType = ((Diagram)diagramNode.getModelObject()).getType();

        if (PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID.equals(diagramType)) {
            reconcileSources = true;
        } else if (PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID.equals(diagramType)) {
            // defect 16803 - be smarter about when reconcile sources needed for
            // dependency diagrams
            Collection diagramEObjects = DiagramUiUtilities.getEObjects(diagramNode);
            Collection notifications = ((SourcedNotification)primaryNotification).getNotifications();
            Iterator iter = notifications.iterator();

            while (iter.hasNext()) {
                Notification nextNotification = (Notification)iter.next();
                Object targetObject = ModelerCore.getModelEditor().getChangedObject(nextNotification);

                // check that we care about the object, and that the changed stuff
                // was more than just a column:
                if (diagramEObjects.contains(targetObject)
                    && (isAnyContainerType(NotificationUtilities.getAddedChildren(nextNotification)) || isAnyContainerType(NotificationUtilities.getRemovedChildren(nextNotification)))) {
                    reconcileSources = true;
                    break;
                }
            }
        }
    }

    /**
     * Scan the EObject array to see what kind of things changed
     * 
     * @param objs the array to scan
     * @return true if there are any BaseTables in the array.
     */
    private static boolean isAnyContainerType( EObject[] objs ) {
        for (int i = 0; i < objs.length; i++) {
            EObject object = objs[i];
            int umlType = TransformationUmlEObjectHelper.getEObjectType(object);
            switch (umlType) {
                case RelationalUmlEObjectHelper.UML_PACKAGE:
                case RelationalUmlEObjectHelper.UML_CLASSIFIER:
                case TransformationUmlEObjectHelper.MAPPING:
                case TransformationUmlEObjectHelper.MAPPING_CLASS:
                case TransformationUmlEObjectHelper.SQL_COLUMN_SET:
                case TransformationUmlEObjectHelper.SQL_INPUT_SET:
                case TransformationUmlEObjectHelper.SQL_PROCEDURE:
                case TransformationUmlEObjectHelper.SQL_PROCEDURE_RESULT_SET:
                case TransformationUmlEObjectHelper.SQL_TABLE:
                case TransformationUmlEObjectHelper.SQL_TRANSFORMATION:
                case TransformationUmlEObjectHelper.SQL_TRANSFORMATION_MAPPING_ROOT:
                case TransformationUmlEObjectHelper.SQL_VIRTUAL_PROCEDURE:
                case TransformationUmlEObjectHelper.STAGING_TABLE:
                case TransformationUmlEObjectHelper.TRANSFORMATION_MAPPING:
                case TransformationUmlEObjectHelper.TRANSFORMATION_MAPPING_ROOT:
                case TransformationUmlEObjectHelper.TRANSFORMATION_OBJECT:
                case TransformationUmlEObjectHelper.XML_DOCUMENT:
                    // any of these types, we want to go ahead and refresh the dep. diagram
                    return true;

                default:
                    // keep scanning if not one of the above.
                    break;
            } // endswitch
        } // endfor

        return false;
    }

    /**
     * @return Returns the reconcileSources.
     * @since 4.2
     */
    public boolean shouldRefreshDiagram() {
        return refreshDiagram;
    }

    private void setRefreshDiagram() {
        refreshDiagram = false;
        String diagramType = ((Diagram)diagramNode.getModelObject()).getType();

        if (PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID.equals(diagramType)) {
            // get the eobjects in the diagram:
            Collection diagramEObjects = DiagramUiUtilities.getEObjects(diagramNode);

            // loop through all notifications:
            Collection notifications = ((SourcedNotification)primaryNotification).getNotifications();
            Iterator iter = notifications.iterator();

            while (iter.hasNext()) {
                Notification nextNotification = (Notification)iter.next();
                // see if we care about the object affected:
                Object targetObject = ModelerCore.getModelEditor().getChangedObject(nextNotification);
                if (diagramEObjects.contains(targetObject)) {
                    refreshDiagram = true;
                    break;
                }
                
                // Check Removed
                if( NotificationUtilities.isRemoved(nextNotification) ) {
                	EObject[] remChildren = NotificationUtilities.getRemovedChildren(nextNotification);
                	for( EObject eobj : remChildren ) {
                		if( diagramEObjects.contains(eobj) ) {
                			refreshDiagram = true;
                		}
                	}
                }
                if( refreshDiagram ) {
                	break;
                }
            } // endwhile -- all notifications
        }
    }

    private void setReconcileAttributes() {
        reconcileTargetAttributes = false;
        reconcileTables.clear();
        // Walk through the notifications.
        // and determine we need to reconcile target attributes (bail on check if already set)
        // determine if any "source" tables need to be reconciled (don't bail on check if already set)
        // Add all tables to reconcileTables list
        Collection notifications = ((SourcedNotification)primaryNotification).getNotifications();
        Iterator iter = notifications.iterator();
        Notification notification = null;
        while (iter.hasNext()) {
            notification = (Notification)iter.next();
            EObject targetObject = getEObjectTarget(notification);
            if (NotificationUtilities.isAdded(notification) || NotificationUtilities.isRemoved(notification)) {

                if (!reconcileTargetAttributes && targetObject == targetEObject) {
                    reconcileTargetAttributes = true;
                    DiagramModelNode targetNode = getPrimaryDiagramModelNode(diagramNode, targetObject);
                    if (targetNode != null && !reconcileTables.contains(targetNode)) reconcileTables.add(targetNode);
                } else {
                    // get the digram node.
                    DiagramModelNode targetNode = getPrimaryDiagramModelNode(diagramNode, targetObject);
                    // Check it
                    if (canAddOrRemove(targetNode)) {
                        if (!isNestedClassifier(targetNode)) {
                            // We have a match, get the added children and hand them off to the generator to construct
                            // and add to this the corresponding targetNode
                            if (targetNode != null && !reconcileTables.contains(targetNode)) {
                                reconcileTables.add(targetNode);
                            }
                        } else {
                            // Check for nested here
                            targetNode = getModelNode(diagramNode, targetObject);
                            if (targetNode != null && isNestedClassifier(targetNode) && !reconcileTables.contains(targetNode)) {
                                // We have a match, get the added children and hand them off to the generator to construct
                                // and add to this the corresponding targetNode
                                reconcileTables.add(targetNode);
                            }
                        }
                    }

                }
                // Now we check to see if the target object is already in diagram

            } else if (NotificationUtilities.isChanged(notification)) {
                DiagramModelNode targetNode = getModelNode(diagramNode, targetObject);
                if (targetNode != null) {
                    if (notification.getEventType() == Notification.MOVE) {
                        if (!movedNodes.contains(targetNode)) movedNodes.add(targetNode);
                    } else {
                        if (!changedNodes.contains(targetNode)) changedNodes.add(targetNode);
                        DiagramModelNode vgDiagramNode = getTargetDiagramNode();
                        if (vgDiagramNode != null && vgDiagramNode == targetNode) changedNodes.add(getTransformationDiagramNode());

                    }
                }
            }
        }

    }

    /**
     * @return Returns the changedNodes.
     * @since 4.2
     */
    public List getChangedNodes() {
        return this.changedNodes;
    }

    /**
     * @return Returns the changedNodes.
     * @since 4.2
     */
    public List getMovedNodes() {
        return this.movedNodes;
    }

    private EObject getEObjectTarget( Notification notification ) {
        Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
        if (targetObject instanceof EObject) return (EObject)targetObject;

        return null;
    }

    /**
     * Had to create a method to perform something similar to super.getModelNode() method but insure that any selected node in a
     * transformation is either a classifier or a transformation. (not an extent, in other words). This was mucking up the Staging
     * table on detailed diagrams because it referenced the Classifier also.
     */
    public DiagramModelNode getPrimaryDiagramModelNode( DiagramModelNode diagramModelNode,
                                                        EObject someModelObject ) {
        if (diagramModelNode instanceof UmlClassifierNode || diagramModelNode instanceof TransformationNode) {
            if (diagramModelNode.getModelObject() != null && diagramModelNode.getModelObject().equals(someModelObject)) {
                return diagramModelNode;
            }
        }

        DiagramModelNode matchedNode = null;

        // Check the children
        List contents = diagramModelNode.getChildren();
        if (contents != null && !contents.isEmpty()) {
            Iterator iter = contents.iterator();
            Object nextObj = null;
            DiagramModelNode nextNode = null;

            while (iter.hasNext() && matchedNode == null) {
                nextObj = iter.next();
                if (nextObj instanceof DiagramModelNode) {
                    nextNode = (DiagramModelNode)nextObj;
                    matchedNode = getPrimaryDiagramModelNode(nextNode, someModelObject);
                }
            }
        }

        return matchedNode;
    }

    public DiagramModelNode getModelNode( DiagramModelNode diagramModelNode,
                                          EObject someModelObject ) {
        if (diagramModelNode.getModelObject() != null && diagramModelNode.getModelObject().equals(someModelObject)) {
            return diagramModelNode;
        }
        DiagramModelNode matchedNode = null;
        // Check the children
        List contents = diagramModelNode.getChildren();
        if (contents != null && !contents.isEmpty()) {
            Iterator iter = contents.iterator();
            Object nextObj = null;
            DiagramModelNode nextNode = null;

            while (iter.hasNext() && matchedNode == null) {
                nextObj = iter.next();
                if (nextObj instanceof DiagramModelNode) {
                    nextNode = (DiagramModelNode)nextObj;
                    matchedNode = getModelNode(nextNode, someModelObject);
                }
            }
        }

        return matchedNode;
    }

    private boolean canAddOrRemove( DiagramModelNode targetNode ) {
        boolean canDo = false;

        if (targetNode != null) {
            if (targetNode instanceof UmlClassifierNode) {
                canDo = true;
            }
        }

        return canDo;
    }

    private boolean isNestedClassifier( DiagramModelNode targetNode ) {
        if (targetNode.getParent() instanceof UmlClassifierContainerNode) return true;

        return false;
    }

    /**
     * @return Returns the reconcilable table diagram nodes (UmlClassifierNodes).
     * @since 4.2
     */
    public List getReconcileTableNodes() {
        return this.reconcileTables;
    }

    /**
     * @return Returns the diagramNode.
     * @since 4.2
     */
    public DiagramModelNode getDiagramNode() {
        return this.diagramNode;
    }

    public DiagramModelNode getTargetDiagramNode() {
        return getModelNode(diagramNode, targetEObject);
    }

    public DiagramModelNode getTransformationDiagramNode() {
        // walk children and look for TransformationNode type
        Iterator iter = diagramNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while (iter.hasNext()) {
            nextNode = (DiagramModelNode)iter.next();
            if (nextNode instanceof TransformationNode) return nextNode;
        }

        return null;
    }
}
