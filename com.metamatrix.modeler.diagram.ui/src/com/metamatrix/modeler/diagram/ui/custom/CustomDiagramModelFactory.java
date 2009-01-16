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

package com.metamatrix.modeler.diagram.ui.custom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.diagram.AbstractDiagramEntity;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingModelFactory;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.pakkage.PackageDiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * PackageDiagramModelFactory
 */
public class CustomDiagramModelFactory extends PackageDiagramModelFactory {

    private static final String KEY_CUSTOM_DIAGRAM_NAME = "DiagramNames.customDiagram"; //$NON-NLS-1$
    private static final String THIS_CLASS = "CustomDiagramModelFactory"; //$NON-NLS-1$
    private String errorMessage;

    /**
     *
     */
    public CustomDiagramModelFactory() {
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
        HashMap nodeMap = new HashMap();
        setSNotationId(sNotationId);

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

            if (getGenerator() != null) {
                while (iter.hasNext()) {
                    // Get current EObject
                    errorMessage = null;
                    EObject eObj = (EObject)iter.next();
                    // Get a DiagramEntity
                    DiagramModelNode childModelNode = getGenerator().createModel(eObj, diagram);
                    if (childModelNode != null) {
                        nodeMap.put(eObj, childModelNode);
                        childModelNode.setParent(diagramModelNode);
                        diagramModelNode.addChild(childModelNode);
                        diagramModelNode.update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
                        diagramModelNode.update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
                    } else {
                        String name = DiagramUiUtilities.getEObjectLabel(eObj);
                        errorMessage = Util.getString(Errors.MODEL_NODE_FAILURE) + " for object = " + name; //$NON-NLS-1$
                        Util.log(IStatus.WARNING, errorMessage);
                    }
                }
            } else {
                Util.log(IStatus.WARNING, Util.getString(Errors.MODEL_GENERATOR_FAILURE));
            }

            if (!diagramModelNode.getChildren().isEmpty()) {
                // ----------------
                // Let's create a map containing binary associations keyed to the EObject reference
                HashMap connMap = new HashMap();

                // Let's get contents, and get associations for each object
                List realAssociations = new ArrayList();
                List currentChildren = diagramModelNode.getChildren();
                iter = currentChildren.iterator();
                while (iter.hasNext()) {
                    DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
                    List childAssList = childModelNode.getAssociations(nodeMap);

                    NodeConnectionModel nextConnection = null;
                    NodeConnectionModel nextRealConnection = null;
                    Iterator innerIter = null;
                    Iterator subIter = childAssList.iterator();
                    while (subIter.hasNext()) {
                        boolean connExists = false;
                        nextConnection = (NodeConnectionModel)subIter.next();

                        // Check the map first if the reference object != null
                        if (nextConnection.getModelObject() != null) {
                            Object existingConn = connMap.get(nextConnection.getModelObject());
                            if (existingConn != null) connExists = true;
                        } else {
                            // no reference object, so we have to walk through all connections and check .equals()
                            innerIter = realAssociations.iterator();

                            while (innerIter.hasNext() && !connExists) {
                                nextRealConnection = (NodeConnectionModel)innerIter.next();
                                if (nextRealConnection != null && nextRealConnection.equals(nextConnection)) connExists = true;
                            }

                        }
                        // if the connection still doesn't exist, then add to list and to map.
                        if (!connExists) {
                            if (nextConnection.getModelObject() != null) {
                                connMap.put(nextConnection.getModelObject(), nextConnection);
                            }
                            realAssociations.add(nextConnection);
                        }
                    }
                }

                iter = realAssociations.iterator();
                while (iter.hasNext()) {
                    NodeConnectionModel nextAssociation = (NodeConnectionModel)iter.next();

                    ((DiagramModelNode)nextAssociation.getSourceNode()).addSourceConnection(nextAssociation);
                    ((DiagramModelNode)nextAssociation.getTargetNode()).addTargetConnection(nextAssociation);

                    List labelNodes = nextAssociation.getLabelNodes();
                    if (labelNodes != null && !labelNodes.isEmpty()) {
                        Iterator labelIter = labelNodes.iterator();
                        LabelModelNode nextNode = null;
                        while (labelIter.hasNext()) {
                            nextNode = (LabelModelNode)labelIter.next();
                            diagramModelNode.addChild(nextNode);
                        }
                    }
                }

            }
        }

        if (!drawingNodes.isEmpty()) diagramModelNode.addChildren(drawingNodes);

        return diagramModelNode;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelFactoryImpl#currentDiagramRemoved(com.metamatrix.metamodels.diagram.Diagram)
     * @since 4.2
     */
    @Override
    protected boolean currentDiagramRemoved( Diagram theDiagram ) {
        boolean isRemoved = false;

        if (theDiagram == null) {
            isRemoved = true;
        } else if (theDiagram.eResource() == null) {
            isRemoved = true;

            // check the eContainer.
            if ((theDiagram.eContainer() == null) && diagramIsTransient(theDiagram)) {
                isRemoved = false;
            }
        }
        return isRemoved;
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
                    if (!(nextDE instanceof DiagramLink) && nextDE.getModelObject() != null) {
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
                    if (type.equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID)) result = true;
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
        Diagram diagram = (Diagram)diagramModelNode.getModelObject();

        if (currentDiagramRemoved(diagram)) currentDiagramOK = false;

        if (currentDiagramOK && isValidDiagram(diagramModelNode) && shouldHandleNotification(notification, diagramModelNode)) {
            boolean requiredStart = false;
            boolean succeeded = false;
            boolean handleConstruction = !DiagramEditorUtil.isDiagramUnderConstruction(diagram);
            try {
                if (handleConstruction) {
                    DiagramEditorUtil.setDiagramUnderConstruction(diagram);
                }
                // -------------------------------------------------
                // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
                // will result in only one transaction?
                // -------------------------------------------------

                requiredStart = ModelerCore.startTxn(false, false, "Update Custom Diagram", this); //$NON-NLS-1$$

                CustomDiagramNotificationHelper helper = new CustomDiagramNotificationHelper(
                                                                                             notification,
                                                                                             (Diagram)diagramModelNode.getModelObject(),
                                                                                             this);

                handleMoves(helper.getMovedEObjects(), diagramModelNode);
                handleAdds(helper.getAddNotifications(), diagramModelNode);
                handleRemoves(helper.getRemoveNotifications(), diagramModelNode);
                handleChanges(helper.getChangedNotifications(), diagramModelNode);
                add(new ArrayList(helper.getUndoAddedEObjects()), diagramModelNode);

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
                    if (handleConstruction) {
                        DiagramEditorUtil.setDiagramConstructionComplete(diagram, true);
                    }
                }
            }

        }

        return currentDiagramOK;
    }

    private boolean shouldHandleNotification( Notification notification,
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
                        if (targetObject != null && targetObject instanceof EObject) {
                            // Custom Diagrams don't care what model resources are... can have other model objects
                            // in other model custom diagrams.
                            shouldHandle = true;
                        }
                    }
                }
            } else { // SINGLE NOTIFICATION
                Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
                if (targetObject != null && targetObject instanceof EObject) {
                    // Custom Diagrams don't care what model resources are... can have other model objects
                    // in other model custom diagrams.
                    shouldHandle = true;
                }
            }
        }

        return shouldHandle;
    }

    @Override
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
                if (nextDiagram.getType() != null && nextDiagram.getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID)) return nextDiagram;
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void add( EObject someTarget,
                     DiagramModelNode customDiagramModelNode,
                     boolean updateAssociations ) {
        DiagramModelNode newNode = getGenerator().createModel(someTarget, (Diagram)customDiagramModelNode.getModelObject());
        if (newNode != null) {
            customDiagramModelNode.addChild(newNode);
            if (updateAssociations) updateAssociations(customDiagramModelNode, customDiagramModelNode);
        }

    }

    public void add( List targets,
                     DiagramModelNode customDiagramModelNode ) {
        Iterator iter = targets.iterator();
        EObject nextTarget = null;
        List newChildren = new ArrayList(targets.size());

        while (iter.hasNext()) {
            nextTarget = (EObject)iter.next();
            DiagramModelNode newNode = getGenerator().createModel(nextTarget, (Diagram)customDiagramModelNode.getModelObject());
            if (newNode != null) {
                newNode.setParent(customDiagramModelNode);
                newChildren.add(newNode);
            }
        }
        if (!newChildren.isEmpty()) {
            customDiagramModelNode.addChildren(newChildren);
            updateAssociations(customDiagramModelNode, customDiagramModelNode);
        }
    }

    @Override
    protected void updateAssociations( DiagramModelNode diagramModelNode,
                                       DiagramModelNode diagramNode ) {
        super.updateAssociations(diagramModelNode, diagramNode);
        HashMap nodeMap = getNodeMap(diagramNode);

        if (diagramModelNode instanceof CustomDiagramNode) {
            // Need to get new list of associations that should exist between visible components.
            // Let's get contents, and get associations for each object
            NodeConnectionModel nextAssociation = null;
            List realAssociations = new ArrayList();
            List currentChildren = diagramModelNode.getChildren();
            Iterator iter = currentChildren.iterator();
            while (iter.hasNext()) {
                DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
                if (childModelNode != null) {
                    List allAssociations = childModelNode.getAssociations(nodeMap);
                    if (allAssociations != null && !allAssociations.isEmpty()) {
                        Iterator subIter = allAssociations.iterator();
                        Object nextAss = null;
                        while (subIter.hasNext()) {
                            nextAss = subIter.next();
                            if (!realAssociations.contains(nextAss)) {
                                realAssociations.add(nextAss);
                            }
                        }
                    }
                }
            }

            // Remove old associations.
            List staleAssociations = getStaleAssociations(realAssociations, diagramModelNode);

            List changedNodes = new ArrayList(cleanUpStaleAssociations(staleAssociations, diagramModelNode));
            HashMap updatedNodes = new HashMap();
            iter = changedNodes.iterator();
            while (iter.hasNext()) {
                updatedNodes.put(iter.next(), "x"); //$NON-NLS-1$
            }

            // Add new associations if they don't exist.
            iter = realAssociations.iterator();

            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();

                if (!associationExists(diagramModelNode, nextAssociation)) {
                    ((DiagramModelNode)nextAssociation.getSourceNode()).addSourceConnection(nextAssociation);
                    ((DiagramModelNode)nextAssociation.getTargetNode()).addTargetConnection(nextAssociation);

                    // Keep a list of new end nodes so we can tell them to fire
                    // an updateAssociations() call...
                    if (updatedNodes.get(nextAssociation.getSourceNode()) == null) updatedNodes.put(nextAssociation.getSourceNode(),
                                                                                                    "x"); //$NON-NLS-1$
                    if (updatedNodes.get(nextAssociation.getTargetNode()) == null) updatedNodes.put(nextAssociation.getTargetNode(),
                                                                                                    "x"); //$NON-NLS-1$

                    List labelNodes = nextAssociation.getLabelNodes();
                    if (labelNodes != null && !labelNodes.isEmpty()) {
                        Iterator labelIter = labelNodes.iterator();
                        LabelModelNode nextNode = null;
                        while (labelIter.hasNext()) {
                            nextNode = (LabelModelNode)labelIter.next();
                            diagramModelNode.addChild(nextNode);
                        }
                    }
                }
            }

            // call updateLabels in case info changes. (i.e names, labels, etc...)
            iter = getCurrentAssociations(diagramModelNode).iterator();
            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                nextAssociation.updateLabels();
                ((DiagramModelNode)nextAssociation.getSourceNode()).updateAssociations();
                ((DiagramModelNode)nextAssociation.getTargetNode()).updateAssociations();
            }
            if (!updatedNodes.isEmpty()) {
                iter = updatedNodes.keySet().iterator();
                DiagramModelNode nextNode = null;
                while (iter.hasNext()) {
                    nextNode = (DiagramModelNode)iter.next();
                    nextNode.updateAssociations();
                }
            }
        }
    }

    public void remove( List targets,
                        DiagramModelNode customDiagramModelNode ) {
        Iterator iter = targets.iterator();
        EObject nextTarget = null;
        // List staleDiagramEntities = new ArrayList(targets.size());
        List oldChildren = new ArrayList(targets.size());

        while (iter.hasNext()) {
            nextTarget = (EObject)iter.next();
            DiagramModelNode oldNode = getModelNode(customDiagramModelNode, nextTarget);
            if (oldNode != null) {
                // if( newNode.getDiagramModelObject() != null )
                // staleDiagramEntities.add(newNode.getDiagramModelObject());
                removeAllAssociationsFromNode(oldNode, customDiagramModelNode);
                oldChildren.add(oldNode);
            }
        }
        if (!oldChildren.isEmpty()) {
            customDiagramModelNode.removeChildren(oldChildren, true);
        }
    }

    public void clear( DiagramModelNode customDiagramModelNode ) {
        List currentChildren = new ArrayList(customDiagramModelNode.getChildren());
        if (!currentChildren.isEmpty()) {
            Iterator iter = currentChildren.iterator();
            DiagramModelNode nextNode = null;
            // List staleDiagramEntities = new ArrayList(currentChildren.size());

            while (iter.hasNext()) {
                nextNode = (DiagramModelNode)iter.next();
                if (nextNode != null) {
                    removeAllAssociationsFromNode(nextNode, customDiagramModelNode);
                }
            }

            if (!currentChildren.isEmpty()) {
                customDiagramModelNode.removeChildren(currentChildren, true);
            }
        }
    }

    private void performAdd( Notification notification,
                             DiagramModelNode customDiagramModelNode ) {
        boolean performedChange = false;

        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            Object target = ModelerCore.getModelEditor().getChangedObject(notification);

            if (Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS) && Util.isDebugEnabled(DebugConstants.DIAGRAM_MODEL_NODE)) {
                Util.print(DebugConstants.DIAGRAM_MODEL_NODE, "targetObject = " + target); //$NON-NLS-1$
            }

            if (!(target instanceof EObject) || target instanceof Diagram || target instanceof AbstractDiagramEntity) return;

            EObject targetObject = (EObject)target;
            if (NotificationUtilities.addedChildrenParentIsNotifier(notification)) {
                boolean isNested = false;
                // Now we check to see if the target object is already in diagram
                DiagramModelNode targetNode = getNodeInDiagram(customDiagramModelNode, targetObject);
                // if still null, check if it's nested
                if (targetNode == null) {
                    isNested = true;
                    targetNode = getModelNode(customDiagramModelNode, targetObject);
                }

                if (targetNode != null) {
                    // This case where the node is in package diagram and we need to delegate to the object node to add a child
                    // We have a match, get the added children and hand them off to the generator to construct
                    // and add to this the corresponding targetNode
                    EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                    if (newChildren.length > 0) {
                        EObject childParent = newChildren[0].eContainer();
                        DiagramModelNode parentNode = getModelNode(customDiagramModelNode, childParent);

                        if (parentNode != null) {
                            if (parentNode instanceof UmlClassifierNode) {
                                ((UmlClassifierNode)parentNode).reconcile();
                            } else {
                                for (int iChild = 0; iChild < newChildren.length; iChild++) {
                                    DiagramModelNode newNode = getGenerator().createChildModel(parentNode, newChildren[iChild]);
                                    if (newNode != null) performedChange = true;
                                }
                            }
                        }
                        if (performedChange) updateAssociations(parentNode, customDiagramModelNode);
                    }
                }

                if (isNested) {
                    EObject parentEObject = targetObject.eContainer();
                    DiagramModelNode parentNode = getModelNode(customDiagramModelNode, parentEObject);
                    if (parentNode != null) {
                        parentNode.updateForChild(false);
                    }
                }
            }

        }
    }

    private void performRemove( Notification notification,
                                DiagramModelNode customDiagramModelNode ) {
        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            Object target = ModelerCore.getModelEditor().getChangedObject(notification);

            if (Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS) && Util.isDebugEnabled(DebugConstants.DIAGRAM_MODEL_NODE)) {
                Util.debug(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS, THIS_CLASS + ".performRemove()  targetObject = " + target); //$NON-NLS-1$
            }

            if (!(target instanceof EObject) || target instanceof Diagram || target instanceof AbstractDiagramEntity) return;

            EObject targetObject = (EObject)target;

            DiagramModelNode parentNode = getModelNode(customDiagramModelNode, targetObject);

            DiagramModelNode removedNode = null;
            EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
            for (int iChild = 0; iChild < removedChildren.length; iChild++) {
                removedNode = getModelNode(customDiagramModelNode, removedChildren[iChild]);
                if (removedNode != null) {
                    if (parentNode != null) {
                        if (!(parentNode instanceof UmlClassifierNode)) {
                            parentNode.removeChild(removedNode, false);
                        }

                        updateAssociations(parentNode, customDiagramModelNode);
                    } else {
                        removeAllAssociationsFromNode(removedNode, customDiagramModelNode);
                        customDiagramModelNode.removeChild(removedNode, false);
                    }
                }
            }

            if (parentNode != null && parentNode instanceof UmlClassifierNode) {
                ((UmlClassifierNode)parentNode).reconcile();
            }
        } else {
            Object target = ModelerCore.getModelEditor().getChangedObject(notification);
            if (target instanceof Resource) {
                DiagramModelNode removedNode = null;
                EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
                for (int iChild = 0; iChild < removedChildren.length; iChild++) {
                    removedNode = getModelNode(customDiagramModelNode, removedChildren[iChild]);
                    if (removedNode != null) {
                        removeAllAssociationsFromNode(removedNode, customDiagramModelNode);
                        customDiagramModelNode.removeChild(removedNode, false);
                    }
                }
            }
        }

    }

    @Override
    protected void performChange( Notification notification,
                                  DiagramModelNode customDiagramModelNode ) {
        super.performChange(notification, customDiagramModelNode);

        // We need to handle the case where the location/path of any object in diagram is updated when
        // changed (i.e. rename Catagory/schema should update path for any classifier under that container)
        EObject targetObject = NotificationUtilities.getEObject(notification);
        if (targetObject != null) {
            Collection diagramChildren = getDiagramContents((Diagram)customDiagramModelNode.getModelObject());

            Iterator iter = diagramChildren.iterator();
            EObject modelEObject = null;
            Object nextObj = null;
            while (iter.hasNext()) {
                nextObj = iter.next();
                if (nextObj instanceof EObject) {
                    modelEObject = (EObject)nextObj;
                    if (ModelObjectUtilities.isDescendant(targetObject, modelEObject)) {
                        DiagramModelNode targetNode = getModelNode(customDiagramModelNode, modelEObject);
                        if (targetNode != null) {
                            getGenerator().performUpdate(targetNode, null);
                            targetNode.update(DiagramUiConstants.DiagramNodeProperties.SIZE);
                            targetNode.update(DiagramUiConstants.DiagramNodeProperties.NAME);
                        }
                    }
                }
            }

        }

    }

    private void handleMoves( Collection movedEObjects,
                              DiagramModelNode customDiagramModelNode ) {
        if (movedEObjects != null && !movedEObjects.isEmpty()) {
            DiagramModelNode targetNode = null;
            EObject nextEObj = null;
            Iterator iter = movedEObjects.iterator();
            while (iter.hasNext()) {
                nextEObj = (EObject)iter.next();
                targetNode = getModelNode(customDiagramModelNode, nextEObj);
                if (targetNode != null) {
                    getGenerator().performUpdate(targetNode, null);
                }
            }
        }
    }

    private void handleRemoves( Collection removeNotifications,
                                DiagramModelNode customDiagramModelNode ) {
        if (removeNotifications != null && !removeNotifications.isEmpty()) {
            Notification nextNotification = null;
            Iterator iter = removeNotifications.iterator();
            while (iter.hasNext()) {
                nextNotification = (Notification)iter.next();
                performRemove(nextNotification, customDiagramModelNode);
            }
        }
    }

    private void handleAdds( Collection addNotifications,
                             DiagramModelNode customDiagramModelNode ) {
        if (addNotifications != null && !addNotifications.isEmpty()) {
            Notification nextNotification = null;
            Iterator iter = addNotifications.iterator();
            while (iter.hasNext()) {
                nextNotification = (Notification)iter.next();
                performAdd(nextNotification, customDiagramModelNode);
            }
        }
    }

    private void handleChanges( Collection changeNotifications,
                                DiagramModelNode customDiagramModelNode ) {
        if (changeNotifications != null && !changeNotifications.isEmpty()) {
            Notification nextNotification = null;
            Iterator iter = changeNotifications.iterator();
            while (iter.hasNext()) {
                nextNotification = (Notification)iter.next();
                performChange(nextNotification, customDiagramModelNode);
            }
        }
    }

}
