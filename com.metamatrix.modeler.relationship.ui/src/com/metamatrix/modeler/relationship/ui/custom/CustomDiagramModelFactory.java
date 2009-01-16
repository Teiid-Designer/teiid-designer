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

package com.metamatrix.modeler.relationship.ui.custom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.AbstractDiagramEntity;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingModelFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.model.FocusModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipDiagramModelFactory;
import com.metamatrix.modeler.relationship.ui.model.RelationshipFolderModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipTypeModelNode;

/**
 * PackageDiagramModelFactory
 */
public class CustomDiagramModelFactory extends RelationshipDiagramModelFactory {
    // private String sNotationId;

    private static final String KEY_CUSTOM_DIAGRAM_NAME = "DiagramNames.customRelationshipDiagram"; //$NON-NLS-1$
    private static final String THIS_CLASS = "CustomDiagramModelFactory"; //$NON-NLS-1$

    // private String errorMessage;

    /**
     *
     */
    public CustomDiagramModelFactory() {
    }

    /**
     * Create a DiagramModelNode.
     */
    @Override
    public DiagramModelNode createModel( Object baseObject ) {
        return null;
    }

    /**
     * Create a DiagramModelNode.
     */
    @Override
    public DiagramModelNode createModel( Object baseObject,
                                         String sNotationId,
                                         IProgressMonitor monitor ) {
        // Return null if the baseObject is not a com.metamatrix.metamodels.diagram.Diagram
        if (!(baseObject instanceof Diagram)) {
            return null;
        }

        // this.sNotationId = sNotationId;

        Diagram diagram = (Diagram)baseObject;

        DiagramModelNode diagramModelNode = null;

        List contents = null;

        // Create base diagram node.
        diagramModelNode = new CustomDiagramNode(diagram, Util.getString(KEY_CUSTOM_DIAGRAM_NAME));

        // Get Drawing Nodes
        List drawingNodes = DrawingModelFactory.getDrawingNodes(diagram, diagramModelNode);

        // go get the contents based on the diagram only. DiagramEntity's hold the reference to their objects
        // and nothing else.
        contents = getDiagramContents(diagram);

        if (!contents.isEmpty()) {
            Iterator iter = contents.iterator();
            // Now let's create a model Object for each "DiagramEntity"

            // We start with adding relationship types

            List relationshipContents = new ArrayList();

            EObject nextEObject = null;
            while (iter.hasNext()) {
                nextEObject = (EObject)iter.next();
                if (nextEObject instanceof Relationship || nextEObject instanceof RelationshipType
                    || nextEObject instanceof RelationshipFolder) {
                    relationshipContents.add(nextEObject);
                }
            }

            super.createDiagramContents(diagramModelNode, relationshipContents, false);
        }

        if (!drawingNodes.isEmpty()) diagramModelNode.addChildren(drawingNodes);
        updateTypeAssociations(diagramModelNode);
        setRelationshipCompleteness(diagramModelNode);

        return diagramModelNode;
    }

    private List getDiagramContents( Diagram customDiagram ) {
        List contents = new ArrayList();

        if (customDiagram != null) {
            List diagramChildren = customDiagram.eContents();
            if (diagramChildren != null && !diagramChildren.isEmpty()) {
                AbstractDiagramEntity nextDE = null;
                Iterator iter = diagramChildren.iterator();
                while (iter.hasNext()) {
                    nextDE = (AbstractDiagramEntity)iter.next();
                    if (nextDE.getModelObject() != null) {
                        contents.add(nextDE.getModelObject());
                    }
                }
            } else contents = Collections.EMPTY_LIST;
        }
        return contents;
    }

    private boolean isValidDiagram( DiagramModelNode diagramModelNode ) {
        boolean result = false;
        Diagram diagram = (Diagram)diagramModelNode.getModelObject();
        if (diagram != null && diagram.getTarget() != null) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(diagram);
            if (mr != null) {
                String type = diagram.getType();
                if (type != null) {
                    if (type.equals(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID)) result = true;
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory#notifyModel(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public boolean notifyModel( Notification notification,
                                DiagramModelNode diagramModelNode,
                                String sDiagramTypeId ) {
        boolean currentDiagramOK = true;

        if (currentDiagramRemoved((Diagram)diagramModelNode.getModelObject())) currentDiagramOK = false;

        if (currentDiagramOK && isValidDiagram(diagramModelNode) && shouldHandleNotification(notification, diagramModelNode)) {
            boolean requiredStart = false;
            boolean succeeded = false;
            try {
                // -------------------------------------------------
                // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
                // will result in only one transaction?
                // -------------------------------------------------

                requiredStart = ModelerCore.startTxn(false, false, "Update Custom Diagram", this); //$NON-NLS-1$$

                handleNotification(notification, diagramModelNode);
                succeeded = true;
            } catch (Exception ex) {
                DiagramUiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".notifyModel()"); //$NON-NLS-1$  //$NON-NLS-2$
            } finally {
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }

        }

        return currentDiagramOK;
    }

    @Override
    protected boolean shouldHandleNotification( Notification notification,
                                                DiagramModelNode diagramModelNode ) {
        boolean shouldHandle = false;
        Diagram currentDiagram = (Diagram)diagramModelNode.getModelObject();

        ModelResource diagramMR = ModelUtilities.getModelResourceForModelObject(currentDiagram);
        if (diagramMR != null) {
            if (notification instanceof SourcedNotification) {
                Object source = ((SourcedNotification)notification).getSource();
                if (source == null || !source.equals(this)) {
                    Collection notifications = ((SourcedNotification)notification).getNotifications();
                    Iterator iter = notifications.iterator();
                    Notification nextNotification = null;

                    while (iter.hasNext() && !shouldHandle) {
                        nextNotification = (Notification)iter.next();
                        Object targetObject = ModelerCore.getModelEditor().getChangedObject(nextNotification);
                        if (targetObject != null && targetObject instanceof EObject
                            && !DiagramUiUtilities.isDiagramObject((EObject)targetObject)) {
                            // Custom Diagrams don't care what model resources are... can have other model objects
                            // in other model custom diagrams.
                            shouldHandle = true;
                        }
                    }
                }
            } else { // SINGLE NOTIFICATION
                Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
                if (targetObject != null && targetObject instanceof EObject
                    && !DiagramUiUtilities.isDiagramObject((EObject)targetObject)) {
                    // Custom Diagrams don't care what model resources are... can have other model objects
                    // in other model custom diagrams.
                    shouldHandle = true;
                }
            }
        }

        return shouldHandle;
    }

    public Diagram getDiagram( EObject someTarget ) {
        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(someTarget);
        try {
            List returnedDiagrams = modelResource.getModelDiagrams().getDiagrams(someTarget);
            if (returnedDiagrams.size() == 1) {
                return (Diagram)returnedDiagrams.get(0);
            }
            // Find the one for custom diagram
            Iterator iter = returnedDiagrams.iterator();
            while (iter.hasNext()) {
                Diagram nextDiagram = (Diagram)iter.next();
                if (nextDiagram.getType() != null
                    && nextDiagram.getType().equals(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID)) return nextDiagram;
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void add( EObject someTarget,
                     DiagramModelNode customDiagramModelNode,
                     boolean updateAssociations ) {
        boolean updateTypes = false;
        if (someTarget instanceof Relationship) {
            DiagramModelNode newNode = createModelNode((Diagram)customDiagramModelNode.getModelObject(), someTarget);
            if (newNode != null) {
                addButtonDiagramImage(newNode);
                customDiagramModelNode.addChild(newNode);
                if (newNode instanceof RelationshipModelNode) {
                    addSourcesAndTargets(customDiagramModelNode, newNode, true, true);
                }
            }
        } else {
            DiagramModelNode newNode = createModelNode((Diagram)customDiagramModelNode.getModelObject(), someTarget);
            customDiagramModelNode.addChild(newNode);
            newNode.setParent(customDiagramModelNode);
            if (newNode instanceof RelationshipTypeModelNode) updateTypes = true;
        }
        if (updateTypes) updateTypeAssociations(customDiagramModelNode);
    }

    public void add( List targets,
                     DiagramModelNode customDiagramModelNode ) {
        Iterator iter = targets.iterator();
        EObject nextTarget = null;

        while (iter.hasNext()) {
            nextTarget = (EObject)iter.next();

            add(nextTarget, customDiagramModelNode, false);
        }
    }

    public void remove( List targets,
                        DiagramModelNode customDiagramModelNode ) {
        Iterator iter = targets.iterator();
        EObject nextTarget = null;
        boolean removedNodes = false;
        DiagramModelNode removedNode = null;

        while (iter.hasNext()) {
            nextTarget = (EObject)iter.next();
            removedNode = getModelNode(customDiagramModelNode, nextTarget);
            if (removedNode != null) {
                if (removedNode instanceof RelationshipModelNode || removedNode instanceof RelationshipTypeModelNode
                    || removedNode instanceof RelationshipFolderModelNode) {
                    if (removedNode instanceof RelationshipModelNode || removedNode instanceof RelationshipTypeModelNode) removeAssociationsForNode(customDiagramModelNode,
                                                                                                                                                    removedNode);
                    customDiagramModelNode.removeChild(removedNode, true);
                } else if (removedNode instanceof FocusModelNode) {
                    removeAssociationsForNode(customDiagramModelNode, removedNode);
                    removedNodes = true;
                }
            }
        }
        if (removedNodes) setRelationshipCompleteness(customDiagramModelNode);
    }

    public void clear( DiagramModelNode customDiagramModelNode ) {
        List currentChildren = new ArrayList(customDiagramModelNode.getChildren());
        if (!currentChildren.isEmpty()) {
            customDiagramModelNode.removeChildren(currentChildren, true);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.ModelNotificationHandler#performUpdate(org.eclipse.emf.common.notify.Notification, int)
     */
    @Override
    public void handleNotification( Notification notification,
                                    DiagramModelNode packageDiagramModelNode ) {
        if (notification instanceof SourcedNotification) {
            Collection notifications = ((SourcedNotification)notification).getNotifications();
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                handleSingleNotification((Notification)iter.next(), packageDiagramModelNode);
            }
        } else {
            handleSingleNotification(notification, packageDiagramModelNode);
        }
    }

    @Override
    protected void performAdd( Notification notification,
                               DiagramModelNode customDiagramModelNode ) {

        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);

            if (!(targetObject instanceof EObject) || targetObject instanceof Diagram
                || targetObject instanceof AbstractDiagramEntity) return;

            if (targetObject instanceof Relationship && notification.getEventType() == Notification.ADD) {
                super.performAdd(notification, customDiagramModelNode);
            }

        }
    }

    @Override
    protected void performRemove( Notification notification,
                                  DiagramModelNode customDiagramModelNode ) {
        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);

            if (!(targetObject instanceof EObject) || targetObject instanceof Diagram
                || targetObject instanceof AbstractDiagramEntity) return;

            if (targetObject instanceof Relationship && notification.getEventType() == Notification.REMOVE) {
                super.performRemove(notification, customDiagramModelNode);
            }

        }
    }

    public void restoreRelationship( EObject relationshipEObject,
                                     DiagramModelNode diagramNode ) {
        if (relationshipEObject instanceof Relationship) {
            // We need to reconcile the source and targetAttributes....

            // get relationship node
            DiagramModelNode relationshipNode = getModelNode(diagramNode, relationshipEObject);

            if (relationshipNode instanceof RelationshipModelNode) {
                addSourcesAndTargets(diagramNode, relationshipNode, true, false);
                relationshipNode.updateAssociations();
                ((RelationshipModelNode)relationshipNode).setIsComplete(true);
                relationshipNode.setSecondOverlayImage(null, 0);
                ((RelationshipModelNode)relationshipNode).updateForButtons();
            }

        }
    }

    public boolean relationshipIsComplete( DiagramModelNode diagramNode,
                                           DiagramModelNode relationshipNode ) {
        Relationship rEObject = (Relationship)relationshipNode.getModelObject();
        // Add Sources
        List sources = new ArrayList(rEObject.getSources());
        // Walk through list of sources, get each targetConnections, and sourceNode.
        // If the source node exists on diagram and nodesAreConnected(), then move on
        // if any case does not exist, then we bail with a FALSE
        Iterator iter = null;
        EObject nextEObject = null;
        DiagramModelNode existingNode = null;
        if (!sources.isEmpty()) {
            iter = sources.iterator();
            while (iter.hasNext()) {
                nextEObject = (EObject)iter.next();
                existingNode = getModelNode(diagramNode, nextEObject);

                if (existingNode == null) {
                    return false;
                }
                if (!DiagramUiUtilities.nodesAreConnected(existingNode, relationshipNode)) {
                    return false;
                }
            }
        }

        List targets = new ArrayList(rEObject.getTargets());
        if (!targets.isEmpty()) {
            iter = targets.iterator();
            while (iter.hasNext()) {
                nextEObject = (EObject)iter.next();
                existingNode = getModelNode(diagramNode, nextEObject);

                if (existingNode == null) {
                    return false;
                }
                if (!DiagramUiUtilities.nodesAreConnected(existingNode, relationshipNode)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setRelationshipCompleteness( DiagramModelNode diagramNode ) {
        Iterator iter = diagramNode.getChildren().iterator();
        Object nextObject = null;
        RelationshipModelNode nextRMN = null;
        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof RelationshipModelNode) {
                nextRMN = (RelationshipModelNode)nextObject;
                nextRMN.setIsComplete(relationshipIsComplete(diagramNode, nextRMN));
                addButtonRestoreImage(nextRMN);
                nextRMN.updateForButtons();
            }
        }
    }

}
