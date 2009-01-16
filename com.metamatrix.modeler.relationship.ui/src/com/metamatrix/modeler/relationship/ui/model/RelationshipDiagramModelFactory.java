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

package com.metamatrix.modeler.relationship.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlDependency;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.UmlDependencyBass;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelFactoryImpl;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.connection.RelationshipLink;
import com.metamatrix.modeler.relationship.ui.diagram.RelationshipDiagramUtil;
import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * @author BLaFond To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class RelationshipDiagramModelFactory extends DiagramModelFactoryImpl implements UiConstants {
    private static final String KEY_RELATIONSHIP_DIAGRAM_NAME = "DiagramNames.relationshipDiagram"; //$NON-NLS-1$
    private static final String THIS_CLASS = "RelationshipDiagramModelFactory"; //$NON-NLS-1$

    // private String sAspectId;

    // private static DiagramFactory diagramFactory;

    // static {
    // diagramFactory = DiagramFactory.eINSTANCE;
    // }
    /**
     * Construct an instance of TransformModelFactory.
     */
    public RelationshipDiagramModelFactory() {
        super();
        // sAspectId = ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.ID;
    }

    /**
     * Create a DiagramModelNode.
     */
    public DiagramModelNode createModel( Object baseObject ) {
        return null;
    }

    /**
     * Create a DiagramModelNode.
     */

    public DiagramModelNode createModel( Object baseObject,
                                         String sNotationId,
                                         IProgressMonitor monitor ) {

        // Return null if the baseObject is not a com.metamatrix.metamodels.diagram.Diagram
        if (!(baseObject instanceof Diagram)) {
            return null;
        }

        Diagram diagram = (Diagram)baseObject;

        DiagramModelNode diagramModelNode = null;

        // Create the DiagramNode
        diagramModelNode = new RelationshipDiagramNode(diagram, UiConstants.Util.getString(KEY_RELATIONSHIP_DIAGRAM_NAME));

        addRelationshipDiagramContents(diagramModelNode, diagram);
        // diagramModelNode.addChildren(getRelationshipDiagramContents(diagramModelNode, diagram));

        return diagramModelNode;
    }

    protected void addRelationshipDiagramContents( DiagramModelNode diagramModelNode,
                                                   Diagram relationshipDiagram ) {

        // Let's get the aspect for each node
        // MetamodelAspect aspect = getUmlAspect(someEObject);
        // now create RelationshipModelNodes and add to contents.

        EObject targetEObject = relationshipDiagram.getTarget();

        if (targetEObject != null) {
            List contents = null;
            if (!(targetEObject instanceof ModelAnnotation)) {
                contents = targetEObject.eContents();
            } else if (targetEObject instanceof ModelAnnotation) {
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(relationshipDiagram);
                if (modelResource != null) {
                    try {
                        contents = modelResource.getEObjects();
                    } catch (ModelWorkspaceException e) {
                        contents = Collections.EMPTY_LIST;
                        String message = Util.getString("ModelErrors.getContentError", modelResource.getItemName()); //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, e, message);
                    }
                } else {
                    contents = Collections.EMPTY_LIST;
                }
            } else {
                contents = Collections.EMPTY_LIST;
            }

            if (!contents.isEmpty()) {
                createDiagramContents(diagramModelNode, contents, true);
                updateTypeAssociations(diagramModelNode);
            }

        }
    }

    protected void createDiagramContents( DiagramModelNode diagramModelNode,
                                          List relationshipObjects,
                                          boolean forceFocusNodes ) {

        if (!relationshipObjects.isEmpty()) {
            Iterator iter = relationshipObjects.iterator();
            EObject nextEO = null;
            Object nextO = null;
            DiagramModelNode newNode = null;
            while (iter.hasNext()) {
                nextO = iter.next();
                if (nextO instanceof EObject) {
                    nextEO = (EObject)nextO;
                    newNode = createModelNode((Diagram)diagramModelNode.getModelObject(), nextEO);
                    if (newNode != null) {
                        addButtonDiagramImage(newNode);
                        diagramModelNode.addChild(newNode);
                        if (newNode instanceof RelationshipModelNode) {
                            addSourcesAndTargets(diagramModelNode, newNode, forceFocusNodes, false);
                        }
                    }
                }
            }

        }
    }

    protected DiagramModelNode createModelNode( Diagram relationshipDiagram,
                                                EObject eObject ) {
        DiagramModelNode newNode = null;

        // MetamodelAspect someAspect = getRelationshipAspect(eObject);
        if (eObject != null) {
            if (eObject instanceof Relationship) {
                newNode = new RelationshipModelNode(relationshipDiagram, eObject, RelationshipModelNode.SOURCE_NODE);
            } else if (eObject instanceof RelationshipRole) {
            } else if (eObject instanceof RelationshipType) {
                newNode = new RelationshipTypeModelNode(relationshipDiagram, eObject);
            } else if (eObject instanceof RelationshipFolder) {
                newNode = new RelationshipFolderModelNode(relationshipDiagram, eObject);
            }
        }
        return newNode;
    }

    // public MetamodelAspect getRelationshipAspect(EObject eObject) {
    // EClass eClass = eObject.eClass();
    // MetamodelAspect someAspect =
    // ModelerCore.getMetamodels().getMetamodelAspect(eClass, getAspectId());
    // return someAspect;
    // }

    // private String getAspectId() {
    // return sAspectId;
    // }

    private boolean isValidDiagram( DiagramModelNode diagramModelNode ) {
        boolean result = false;
        Diagram diagram = (Diagram)diagramModelNode.getModelObject();
        if (diagram != null && diagram.getTarget() != null) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(diagram);
            if (mr != null) {
                String type = diagram.getType();
                if (type != null) {
                    if (type.equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID)) result = true;
                }
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------------------
    // EVENT HANDLING (NOTIFICATIONS)
    // -------------------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory#notifyModel(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public boolean notifyModel( Notification notification,
                                DiagramModelNode diagramModelNode,
                                String sDiagramTypeId ) {
        boolean currentDiagramOK = true;

        if (currentDiagramRemoved((Diagram)diagramModelNode.getModelObject())) currentDiagramOK = false;

        if (currentDiagramOK && isValidDiagram(diagramModelNode) && sourceIsNotThis(notification)
            && shouldHandleNotification(notification, diagramModelNode)) {
            UiConstants.Util.start("RelationshipDiagramModelFactory.notifyModel()", InternalUiConstants.Debug.Metrics.NOTIFICATIONS); //$NON-NLS-1$

            boolean requiredStart = false;
            boolean succeeded = false;
            try {
                // -------------------------------------------------
                // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
                // will result in only one transaction?
                // -------------------------------------------------

                requiredStart = ModelerCore.startTxn(false, false, "Update Relationship Diagram", this); //$NON-NLS-1$$

                handleNotification(notification, diagramModelNode);

                succeeded = true;
            } catch (Exception ex) {
                UiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".notifyModel()"); //$NON-NLS-1$  //$NON-NLS-2$
            } finally {
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
            UiConstants.Util.stop("RelationshipDiagramModelFactory.notifyModel()", InternalUiConstants.Debug.Metrics.NOTIFICATIONS); //$NON-NLS-1$
        }

        return currentDiagramOK;
    }

    protected boolean shouldHandleNotification( Notification notification,
                                                DiagramModelNode diagramModelNode ) {
        boolean shouldHandle = false;
        Diagram currentDiagram = (Diagram)diagramModelNode.getModelObject();
        Resource diagramModelResource = null;

        if (currentDiagram.getTarget() instanceof ModelAnnotation) {
            diagramModelResource = currentDiagram.getTarget().eResource();
        }

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
                        if (targetObject != null) {
                            if (targetObject instanceof EObject
                                && !DiagramUiUtilities.isNonDrawingDiagramObject((EObject)targetObject)) {
                                // If notification is from another "model resource" we don't care for Coarse
                                // Mapping diagram. All objects are in same model.
                                // Check here if the targetObject and document have the same resource, then set to TRUE;
                                ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
                                if (mr != null && mr.equals(diagramMR)) {
                                    shouldHandle = true;
                                }
                                if (!shouldHandle) {
                                    DiagramModelNode existingNode = getNodeInDiagram(diagramModelNode, (EObject)targetObject);
                                    if (existingNode != null) shouldHandle = true;
                                }
                            } else if (targetObject instanceof Resource && diagramModelResource != null) {
                                Resource targetResource = (Resource)targetObject;
                                if (targetResource.equals(diagramModelResource)) shouldHandle = true;
                            } else if (targetObject instanceof Diagram && NotificationUtilities.isRemoved(nextNotification)) {
                                ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
                                if (mr != null && mr.equals(diagramMR)) {
                                    shouldHandle = true;
                                }
                            }
                        }
                    }
                }
            } else { // SINGLE NOTIFICATION
                Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
                if (targetObject != null) {
                    if (targetObject instanceof EObject && !DiagramUiUtilities.isNonDrawingDiagramObject((EObject)targetObject)) {
                        // If notification is from another "model resource" we don't care for Coarse
                        // Mapping diagram. All objects are in same model.
                        // Check here if the targetObject and document have the same resource, then set to TRUE;
                        ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
                        if (mr != null && mr.equals(diagramMR)) {
                            shouldHandle = true;
                        }
                        if (!shouldHandle) {
                            DiagramModelNode existingNode = getNodeInDiagram(diagramModelNode, (EObject)targetObject);
                            if (existingNode != null) shouldHandle = true;
                        }
                    } else if (targetObject instanceof Resource && diagramModelResource != null) {
                        Resource targetResource = (Resource)targetObject;
                        if (targetResource.equals(diagramModelResource)) shouldHandle = true;
                    } else if (targetObject instanceof Diagram && NotificationUtilities.isRemoved(notification)) {
                        ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
                        if (mr != null && mr.equals(diagramMR)) {
                            shouldHandle = true;
                        }
                    }
                }
            }
        }

        return shouldHandle;
    }

    protected boolean sourceIsNotThis( Notification notification ) {
        if (notification instanceof SourcedNotification) {

            Object source = ((SourcedNotification)notification).getSource();

            if (source == null) return true;

            return !(source.equals(this));
        }

        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.ModelNotificationHandler#performUpdate(org.eclipse.emf.common.notify.Notification, int)
     */
    public void handleNotification( Notification notification,
                                    DiagramModelNode diagramModelNode ) {
        if (notification instanceof SourcedNotification) {
            Collection notifications = ((SourcedNotification)notification).getNotifications();
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                handleSingleNotification((Notification)iter.next(), diagramModelNode);
            }
        } else {
            handleSingleNotification(notification, diagramModelNode);
        }
    }

    protected void handleSingleNotification( Notification notification,
                                             DiagramModelNode diagramModelNode ) {

        if (isAddNotification(notification) || NotificationUtilities.isAdded(notification)) {
            performAdd(notification, diagramModelNode);
        } else if (isRemoveNotification(notification) || NotificationUtilities.isRemoved(notification)) {
            performRemove(notification, diagramModelNode);
        } else if (NotificationUtilities.isChanged(notification)) {
            performChange(notification, diagramModelNode);
        }

    }

    private boolean isAddNotification( Notification notification ) {
        boolean result = false;

        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            EObject targetObject = NotificationUtilities.getEObject(notification);
            if (targetObject instanceof Relationship) {
                if (notification.getEventType() == Notification.ADD || notification.getEventType() == Notification.ADD_MANY) {
                    result = true;
                }
            }
        }

        return result;
    }

    private boolean isRemoveNotification( Notification notification ) {
        boolean result = false;

        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            EObject targetObject = NotificationUtilities.getEObject(notification);
            if (targetObject instanceof Relationship) {
                if (notification.getEventType() == Notification.REMOVE || notification.getEventType() == Notification.REMOVE_MANY) {
                    result = true;
                }
            }
        }

        return result;
    }

    protected void performAdd( Notification notification,
                               DiagramModelNode diagramModelNode ) {
        //		System.out.println(THIS_CLASS + ".performAdd() called"); //$NON-NLS-1$

        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            EObject targetObject = NotificationUtilities.getEObject(notification);

            // We know that we have an object that is not a child of a model resource, therefore,
            // it is safe to assume that it would exist on a normal package diagram.
            // Let's check to see if the target of the current "Diagram", is the same as the
            // parent of the new target object.
            Diagram diagram = (Diagram)diagramModelNode.getModelObject();

            // boolean hasAssociations = false;
            EObject diagramTargetEObject = diagram.getTarget();
            if (diagramTargetEObject.equals(targetObject)) {
                // We have a match, get the added children and hand them off to the generator to construct
                // and add to this packageDiagramModelNode
                DiagramModelNode newNode = null;
                EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                for (int iChild = 0; iChild < newChildren.length; iChild++) {
                    newNode = createModelNode(diagram, newChildren[iChild]);
                    if (newNode != null) {
                        diagramModelNode.addChild(newNode);
                        if (newNode instanceof RelationshipModelNode) {
                            addSourcesAndTargets(diagramModelNode, newNode, true, true);
                            newNode.updateAssociations();
                            newNode.update(NavigationModelNodeProperties.LAYOUT);
                        }
                    }
                }
            } else {
                if (targetObject instanceof Relationship
                    && (notification.getEventType() == Notification.ADD || notification.getEventType() == Notification.ADD_MANY)) {
                    Object newValue = notification.getNewValue();
                    if (newValue instanceof EObject) {
                        DiagramModelNode existingNode = getNodeInDiagram(diagramModelNode, targetObject);
                        if (existingNode != null && newValue instanceof Relationship
                            && ((EObject)newValue).eContainer() == targetObject) {
                            // This is a new owned relationship, update the icon!!!
                            addButtonDiagramImage(existingNode);
                            existingNode.update(DiagramUiConstants.DiagramNodeProperties.BUTTONS);
                        } else if (existingNode != null) {
                            addSourcesAndTargets(diagramModelNode, existingNode, true, true);
                        }
                    } else if (newValue instanceof List) {
                        EObject[] addedValues = null;
                        Object added = notification.getNewValue();
                        if (added instanceof List) {
                            List newKids = (List)added;
                            int size = newKids.size();
                            addedValues = new EObject[size];

                            for (int i = 0; i < size; i++) {
                                addedValues[i] = (EObject)newKids.get(i);
                            }
                        }
                        // peak at first one
                        if (addedValues.length > 1) {
                            if (addedValues[0] instanceof Relationship) {
                                RelationshipModelNode relationshipNode = (RelationshipModelNode)getNodeInDiagram(diagramModelNode,
                                                                                                                 targetObject);
                                if (relationshipNode != null) {
                                    // This is a new owned relationship, update the icon!!!
                                    addButtonDiagramImage(relationshipNode);
                                    relationshipNode.update(DiagramUiConstants.DiagramNodeProperties.BUTTONS);
                                }
                            } else {
                                RelationshipModelNode relationshipNode = (RelationshipModelNode)getNodeInDiagram(diagramModelNode,
                                                                                                                 targetObject);
                                if (relationshipNode != null) {
                                    addSourcesAndTargets(diagramModelNode, relationshipNode, true, true);
                                    relationshipNode.updateAssociations();
                                    relationshipNode.update(NavigationModelNodeProperties.LAYOUT);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Diagram diagram = (Diagram)diagramModelNode.getModelObject();
            EObject diagramTargetEObject = diagram.getTarget();
            Resource diagramModelResource = null;
            // boolean hasAssociations = false;
            if (diagramTargetEObject instanceof ModelAnnotation) diagramModelResource = diagramTargetEObject.eResource();

            // target of notification (notifier) is ModelResource
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
            if (diagramModelResource != null && changedObj != null && changedObj instanceof Resource) {
                Resource targetResource = (Resource)changedObj;
                if (targetResource.equals(diagramModelResource)) {
                    // We have a match, get the added children and hand them off to the generator to construct
                    // and add to this packageDiagramModelNode
                    DiagramModelNode newNode = null;
                    EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                    for (int iChild = 0; iChild < newChildren.length; iChild++) {
                        newNode = createModelNode(diagram, newChildren[iChild]);
                        if (newNode != null) {
                            diagramModelNode.addChild(newNode);
                            if (newNode instanceof RelationshipModelNode) {
                                addSourcesAndTargets(diagramModelNode, newNode, true, true);
                                newNode.updateAssociations();
                                newNode.update(NavigationModelNodeProperties.LAYOUT);
                            }
                        }
                    }
                }
            }
        }

    }

    protected void performRemove( Notification notification,
                                  DiagramModelNode diagramModelNode ) {

        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            EObject targetObject = NotificationUtilities.getEObject(notification);

            if (targetObject instanceof Relationship
                && (notification.getEventType() == Notification.REMOVE || notification.getEventType() == Notification.REMOVE_MANY)) {
                Object oldValue = notification.getOldValue();
                if (oldValue instanceof EObject) {
                    Object dNode = getNodeInDiagram(diagramModelNode, (EObject)oldValue);
                    if (dNode instanceof FocusModelNode) {
                        FocusModelNode removedNode = (FocusModelNode)dNode;
                        RelationshipModelNode relationshipNode = (RelationshipModelNode)getNodeInDiagram(diagramModelNode,
                                                                                                         targetObject);
                        // need to check to see how many "connections are on model. if more than one, than don't remove
                        int nConnections = removedNode.getSourceConnections().size() + removedNode.getTargetConnections().size();
                        removeAssociationsForNode(diagramModelNode, removedNode, relationshipNode);
                        if (nConnections <= 1) {
                            removeFocusNode(diagramModelNode, removedNode);
                        }
                    } else if (dNode == null) {
                        RelationshipModelNode relationshipNode = (RelationshipModelNode)getNodeInDiagram(diagramModelNode,
                                                                                                         targetObject);
                        if (relationshipNode != null) {
                            // This is a new owned relationship, update the icon!!!
                            addButtonDiagramImage(relationshipNode);
                            relationshipNode.update(DiagramUiConstants.DiagramNodeProperties.BUTTONS);
                        }
                    }
                } else if (oldValue instanceof List) {
                    EObject[] removedValues = null;
                    Object removed = notification.getOldValue();
                    if (removed instanceof List) {
                        List oldKids = (List)removed;
                        int size = oldKids.size();
                        removedValues = new EObject[size];

                        for (int i = 0; i < size; i++) {
                            removedValues[i] = (EObject)oldKids.get(i);
                        }
                    }
                    // peak at first one
                    if (removedValues.length > 1) {
                        Object dNode = getNodeInDiagram(diagramModelNode, removedValues[0]);
                        if (dNode instanceof FocusModelNode) {
                            // Let's walk through all nodes
                            dNode = null;
                            for (int i = 0; i < removedValues.length; i++) {
                                dNode = getNodeInDiagram(diagramModelNode, removedValues[i]);
                                if (dNode != null && dNode instanceof FocusModelNode) {
                                    FocusModelNode removedNode = (FocusModelNode)dNode;
                                    RelationshipModelNode relationshipNode = (RelationshipModelNode)getNodeInDiagram(diagramModelNode,
                                                                                                                     targetObject);
                                    // need to check to see how many "connections are on model. if more than one, than don't
                                    // remove
                                    int nConnections = removedNode.getSourceConnections().size()
                                                       + removedNode.getTargetConnections().size();
                                    removeAssociationsForNode(diagramModelNode, removedNode, relationshipNode);
                                    if (nConnections <= 1) {
                                        removeFocusNode(diagramModelNode, removedNode);
                                    }
                                }
                            }
                        } else if (dNode == null) {
                            RelationshipModelNode relationshipNode = (RelationshipModelNode)getNodeInDiagram(diagramModelNode,
                                                                                                             targetObject);
                            if (relationshipNode != null) {
                                // This is a new owned relationship, update the icon!!!
                                addButtonDiagramImage(relationshipNode);
                                relationshipNode.update(DiagramUiConstants.DiagramNodeProperties.BUTTONS);
                            }
                        }
                    }
                }
            } else {
                DiagramModelNode removedNode = null;

                EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);

                for (int iChild = 0; iChild < removedChildren.length; iChild++) {
                    removedNode = getModelNode(diagramModelNode, removedChildren[iChild]);
                    if (removedNode != null) {
                        if (removedNode instanceof RelationshipModelNode || removedNode instanceof RelationshipTypeModelNode
                            || removedNode instanceof RelationshipFolderModelNode) {
                            if (removedNode instanceof RelationshipModelNode) removeAssociationsForNode(diagramModelNode,
                                                                                                        removedNode);
                            diagramModelNode.removeChild(removedNode, false);
                        } else if (removedNode instanceof FocusModelNode) {
                            // need to check to see how many "connections are on model. if more than one, than don't remove
                            int nConnections = removedNode.getSourceConnections().size()
                                               + removedNode.getTargetConnections().size();

                            if (nConnections <= 1) {
                                removeAssociationsForNode(diagramModelNode, removedNode);
                                removeFocusNode(diagramModelNode, removedNode);
                            }

                        }
                    }
                }
            }
        } else {
            Diagram diagram = (Diagram)diagramModelNode.getModelObject();
            EObject diagramTargetEObject = diagram.getTarget();
            Resource diagramModelResource = null;

            if (diagramTargetEObject instanceof ModelAnnotation) diagramModelResource = diagramTargetEObject.eResource();

            // target of notification (notifier) is ModelResource
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
            if (diagramModelResource != null && changedObj != null && changedObj instanceof Resource) {
                Resource targetResource = (Resource)changedObj;
                if (targetResource.equals(diagramModelResource)) {
                    // We have a match, get the added children and hand them off to the generator to construct
                    // and add to this packageDiagramModelNode
                    DiagramModelNode removedNode = null;
                    EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);

                    for (int iChild = 0; iChild < removedChildren.length; iChild++) {
                        removedNode = getModelNode(diagramModelNode, removedChildren[iChild]);
                        if (removedNode instanceof RelationshipModelNode || removedNode instanceof RelationshipTypeModelNode
                            || removedNode instanceof RelationshipFolderModelNode) {
                            if (removedNode instanceof RelationshipModelNode) removeAssociationsForNode(diagramModelNode,
                                                                                                        removedNode);
                            diagramModelNode.removeChild(removedNode, false);
                            diagramModelNode.updateAssociations();
                        } else if (removedNode instanceof FocusModelNode) {
                            RelationshipModelNode relationshipNode = (RelationshipModelNode)getNodeInDiagram(diagramModelNode,
                                                                                                             (EObject)changedObj);
                            // need to check to see how many "connections are on model. if more than one, than don't remove
                            int nConnections = removedNode.getSourceConnections().size()
                                               + removedNode.getTargetConnections().size();
                            removeAssociationsForNode(diagramModelNode, (FocusModelNode)removedNode, relationshipNode);
                            if (nConnections <= 1) {
                                removeFocusNode(diagramModelNode, removedNode);
                            }
                        }
                    }
                }
            }
        }
    }

    protected void performChange( Notification notification,
                                  DiagramModelNode diagramModelNode ) {

        EObject targetObject = NotificationUtilities.getEObject(notification);

        if (notification.getEventType() == Notification.MOVE) {
            DiagramModelNode targetNode = getModelNode(diagramModelNode, targetObject);
            if (targetNode != null) {
                targetNode.updateForChild(true);
            }
        } else {
            DiagramModelNode targetNode = getModelNode(diagramModelNode, targetObject);
            if (targetNode != null) {
                // If object's parent is in our current model
                // Assume this is a rename for now.
                performUpdate(diagramModelNode, targetNode, notification);
            } else {
                if (targetObject instanceof RelationshipRole) {
                    // get all RelationshipNode's and get their "types".
                    // if any types match, then call update
                    RelationshipType relType = ((RelationshipRole)targetObject).getRelationshipType();
                    updateRelationships(diagramModelNode, relType);
                } else if (targetObject instanceof RelationshipType) {
                    updateRelationships(diagramModelNode, (RelationshipType)targetObject);
                }
            }
        }

    }

    // -------------------------------------------------------------------------------------
    // Methods used to add nodes and links to relationships
    // -------------------------------------------------------------------------------------

    protected void addSourcesAndTargets( DiagramModelNode diagramNode,
                                         DiagramModelNode relationshipNode,
                                         boolean forceFocusNodes,
                                         boolean setInitialPositions ) {
        if (relationshipNode instanceof RelationshipModelNode) {
            // We have a relationship here and we need to add sources and targets for it.
            // Note: do a check of the current "diagram model nodes" model object to make sure
            // we don't put duplicate nodes on diagram.
            Relationship relationshipEObject = (Relationship)relationshipNode.getModelObject();

            // Add Sources
            List sources = new ArrayList(relationshipEObject.getSources());
            Iterator iter = null;
            EObject nextEObject = null;
            DiagramModelNode newNode = null;
            DiagramModelNode existingNode = null;
            if (!sources.isEmpty()) {
                iter = sources.iterator();
                while (iter.hasNext()) {
                    nextEObject = (EObject)iter.next();
                    existingNode = getModelNode(diagramNode, nextEObject);

                    if (existingNode == null) {
                        if (DiagramUiUtilities.hasDiagramEntity(diagramNode.getDiagram(), nextEObject) || forceFocusNodes) {
                            newNode = addFocusNode(diagramNode, relationshipNode, nextEObject, FocusModelNode.SOURCE_NODE);
                            addAssociation(diagramNode, newNode, relationshipNode, setInitialPositions);
                        }
                    } else {
                        if (!DiagramUiUtilities.nodesAreConnected(existingNode, relationshipNode)) { // &&
                            // !associationExists(existingNode,
                            // relationshipNode)) {
                            // addAssociation(diagramNode, existingNode, relationshipNode);
                            RelationshipType rType = ((Relationship)relationshipNode.getModelObject()).getType();
                            if (rType != null && existingNode instanceof FocusModelNode) {
                                ((FocusModelNode)existingNode).setRole(rType.getLabel());
                            }
                            RelationshipLink newLink = addAssociation(diagramNode, existingNode, relationshipNode, false);
                            if (existingNode instanceof RelationshipModelNode) newLink.setUseCenterAnchorForSource(true);
                        }
                    }

                }
            }

            // Add Targets
            List targets = new ArrayList(relationshipEObject.getTargets());
            if (!targets.isEmpty()) {
                iter = targets.iterator();
                while (iter.hasNext()) {
                    nextEObject = (EObject)iter.next();
                    existingNode = getModelNode(diagramNode, nextEObject);

                    if (existingNode == null) {
                        if (DiagramUiUtilities.hasDiagramEntity(diagramNode.getDiagram(), nextEObject) || forceFocusNodes) {
                            newNode = addFocusNode(diagramNode, relationshipNode, nextEObject, FocusModelNode.TARGET_NODE);
                            addAssociation(diagramNode, relationshipNode, newNode, setInitialPositions);
                        }
                    } else {
                        if (!DiagramUiUtilities.nodesAreConnected(existingNode, relationshipNode)) {// if
                            // (!associationExists(existingNode,
                            // relationshipNode)) {
                            RelationshipType rType = ((Relationship)relationshipNode.getModelObject()).getType();
                            if (rType != null && existingNode instanceof FocusModelNode) {
                                ((FocusModelNode)existingNode).setRole(rType.getOppositeLabel());
                            }
                            RelationshipLink newLink = addAssociation(diagramNode, relationshipNode, existingNode, false);
                            if (existingNode instanceof RelationshipModelNode) newLink.setUseCenterAnchorForTarget(true);
                        }
                    }

                }
            }
        }
    }

    protected DiagramModelNode addFocusNode( DiagramModelNode diagramNode,
                                             DiagramModelNode relationshipNode,
                                             EObject newFocusEObject,
                                             int nodeType ) {

        DiagramModelNode newDiagramNode = null;

        // if( newFocusEObject instanceof Relationship ) {
        // newDiagramNode =new RelationshipModelNode((Diagram)diagramNode.getModelObject(), newFocusEObject, nodeType);
        // } else {
        newDiagramNode = new FocusModelNode((Diagram)diagramNode.getModelObject(), newFocusEObject, nodeType);

        RelationshipType rType = null;
        if (nodeType == FocusModelNode.TARGET_NODE) {
            rType = ((Relationship)relationshipNode.getModelObject()).getType();
            if (rType != null) ((FocusModelNode)newDiagramNode).setRole(rType.getOppositeLabel());
        } else {
            rType = ((Relationship)relationshipNode.getModelObject()).getType();
            if (rType != null) ((FocusModelNode)newDiagramNode).setRole(rType.getLabel());
        }

        DiagramModelNode labelNode = null;
        diagramNode.addChild(newDiagramNode);
        labelNode = ((FocusModelNode)newDiagramNode).getLabelNode();
        if (labelNode != null) {
            diagramNode.addChild(labelNode);
            diagramNode.update(DiagramUiConstants.DiagramNodeProperties.LOCATION);
        }
        // }

        return newDiagramNode;
    }

    protected void addButtonDiagramImage( DiagramModelNode diagramNode ) {
        if (diagramNode instanceof RelationshipModelNode && diagramNode.getModelObject() instanceof Relationship) {
            Relationship rel = (Relationship)diagramNode.getModelObject();
            if (!rel.getOwnedRelationships().isEmpty()) {
                // List rels = new ArrayList(rel.getRelationshipContainer().getOwnedRelationships());
                Image image = UiPlugin.getDefault().getImage(PluginConstants.Images.RELATIONSHIP_DIAGRAM_ICON);
                if (image != null) diagramNode.setFirstOverlayImage(image, null);
            } else {
                diagramNode.setFirstOverlayImage(null, null);
            }
        }
    }

    protected void addButtonRestoreImage( DiagramModelNode diagramNode ) {
        if (diagramNode instanceof RelationshipModelNode && diagramNode.getModelObject() instanceof Relationship) {
            if (!((RelationshipModelNode)diagramNode).getIsComplete()) {
                Image image = UiPlugin.getDefault().getImage(UiConstants.Images.RESTORE_RELATIONSHIP);
                if (image != null) diagramNode.setSecondOverlayImage(image, 0);
            }
        }
    }

    protected RelationshipLink addAssociation( DiagramModelNode diagramNode,
                                               DiagramModelNode sourceNode,
                                               DiagramModelNode targetNode,
                                               boolean setInitialPosition ) {
        RelationshipLink newLink = null;

        if (sourceNode != null && targetNode != null) {
            String linkRole = StringUtil.Constants.EMPTY_STRING;
            if (sourceNode instanceof FocusModelNode) {
                linkRole = ((FocusModelNode)sourceNode).getRole();
            } else if (targetNode instanceof FocusModelNode) {
                linkRole = ((FocusModelNode)targetNode).getRole();
            }

            newLink = getSourceConnectionModel(sourceNode, targetNode, linkRole);
            ((DiagramModelNode)newLink.getSourceNode()).addSourceConnection(newLink);
            ((DiagramModelNode)newLink.getTargetNode()).addTargetConnection(newLink);

            List labelNodes = newLink.getLabelNodes();
            if (labelNodes != null && !labelNodes.isEmpty()) {
                diagramNode.addChildren(labelNodes);
            }
            sourceNode.updateAssociations();
            targetNode.updateAssociations();
            if (sourceNode instanceof FocusModelNode) {
                if (setInitialPosition) {
                    Point newPoint = new Point(targetNode.getPosition());
                    newPoint.x += -40;
                    newPoint.y += targetNode.getHeight();
                    sourceNode.setPosition(newPoint);
                } else {
                    sourceNode.update(DiagramUiConstants.DiagramNodeProperties.LOCATION);
                }
            }
            if (targetNode instanceof FocusModelNode) {
                if (setInitialPosition) {
                    Point newPoint = new Point(sourceNode.getPosition());
                    newPoint.x += sourceNode.getWidth() + 40;
                    newPoint.y += sourceNode.getHeight();
                    targetNode.setPosition(newPoint);
                } else {
                    targetNode.update(DiagramUiConstants.DiagramNodeProperties.LOCATION);
                }
            }

        }

        return newLink;
    }

    protected NodeConnectionModel addTypeAssociation( DiagramModelNode diagramNode,
                                                      DiagramModelNode sourceNode,
                                                      DiagramModelNode targetNode ) {
        NodeConnectionModel newLink = null;

        if (sourceNode != null && targetNode != null) {
            BinaryAssociation bAss = new UmlDependencyBass(sourceNode.getModelObject(), sourceNode.getModelObject(),
                                                           targetNode.getModelObject());
            newLink = new DiagramUmlDependency(sourceNode, targetNode, bAss);
            ((DiagramModelNode)newLink.getSourceNode()).addSourceConnection(newLink);
            ((DiagramModelNode)newLink.getTargetNode()).addTargetConnection(newLink);

            List labelNodes = newLink.getLabelNodes();
            if (labelNodes != null && !labelNodes.isEmpty()) {
                diagramNode.addChildren(labelNodes);
            }

            sourceNode.updateAssociations();
            targetNode.updateAssociations();
        }

        return newLink;
    }

    // private RelationshipLink getTargetConnectionModel(DiagramModelNode relationshipNode, DiagramModelNode newTargetNode) {
    // RelationshipLink association = new RelationshipLink(relationshipNode, newTargetNode);
    // return association;
    // }

    protected RelationshipLink getSourceConnectionModel( DiagramModelNode newSourceNode,
                                                         DiagramModelNode relationshipNode,
                                                         String roleString ) {
        RelationshipLink association = new RelationshipLink(newSourceNode, relationshipNode, null, roleString);
        return association;
    }

    protected boolean associationExists( DiagramModelNode diagramModelNode,
                                         RelationshipLink targetAssociation ) {
        // get all connections from model
        List currentChildren = diagramModelNode.getChildren();
        Iterator iter = currentChildren.iterator();
        while (iter.hasNext()) {
            DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
            List sourceConnections = childModelNode.getSourceConnections();
            // Walk through the source connections and check if the same info.
            RelationshipLink nextAssociation = null;
            Iterator sIter = sourceConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (RelationshipLink)sIter.next();
                if (associationsMatch(targetAssociation, nextAssociation)) return true;
            }
            // Walk through the target connections and check if the same info.
            List targetConnections = childModelNode.getTargetConnections();
            sIter = targetConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (RelationshipLink)sIter.next();
                if (associationsMatch(targetAssociation, nextAssociation)) return true;
            }
        }

        return false;
    }

    protected boolean associationExists( DiagramModelNode sourceNode,
                                         DiagramModelNode targetNode ) {

        List sourceConnections = sourceNode.getSourceConnections();
        // Walk through the source connections and check if the same info.
        RelationshipLink nextAssociation = null;
        Iterator sIter = sourceConnections.iterator();
        while (sIter.hasNext()) {
            nextAssociation = (RelationshipLink)sIter.next();
            DiagramModelNode targetEndNode = (DiagramModelNode)nextAssociation.getTargetNode();
            if (targetNode.getModelObject().equals(targetEndNode.getModelObject())) return true;
        }

        sourceConnections = targetNode.getSourceConnections();
        // Walk through the source connections and check if the same info.
        sIter = sourceConnections.iterator();
        while (sIter.hasNext()) {
            nextAssociation = (RelationshipLink)sIter.next();
            DiagramModelNode sourceEndNode = (DiagramModelNode)nextAssociation.getSourceNode();
            if (targetNode.getModelObject().equals(sourceEndNode.getModelObject())) return true;
        }

        // Walk through the target connections and check if the same info.
        List targetConnections = targetNode.getTargetConnections();
        sIter = targetConnections.iterator();
        while (sIter.hasNext()) {
            nextAssociation = (RelationshipLink)sIter.next();
            DiagramModelNode sourceEndNode = (DiagramModelNode)nextAssociation.getSourceNode();
            if (sourceNode.getModelObject().equals(sourceEndNode.getModelObject())) return true;
        }

        // Walk through the target connections and check if the same info.
        targetConnections = sourceNode.getTargetConnections();
        sIter = targetConnections.iterator();
        while (sIter.hasNext()) {
            nextAssociation = (RelationshipLink)sIter.next();
            DiagramModelNode targetEndNode = (DiagramModelNode)nextAssociation.getTargetNode();
            if (sourceNode.getModelObject().equals(targetEndNode.getModelObject())) return true;
        }

        return false;
    }

    protected boolean associationsMatch( NodeConnectionModel associationOne,
                                         NodeConnectionModel associationTwo ) {
        if (associationOne.getSourceNode().equals(associationTwo.getSourceNode())
            && associationOne.getTargetNode().equals(associationTwo.getTargetNode())) return true;

        if (associationTwo.getSourceNode().equals(associationOne.getSourceNode())
            && associationTwo.getTargetNode().equals(associationOne.getTargetNode())) return true;

        return false;
    }

    protected void removeAssociationsForNode( DiagramModelNode diagramNode,
                                              DiagramModelNode sourceNode ) {

        if (sourceNode != null && sourceNode instanceof RelationshipModelNode) {
            List sourceConnections = new ArrayList(sourceNode.getSourceConnections());
            Iterator iter = sourceConnections.iterator();
            NodeConnectionModel nextAssociation = null;
            DiagramModelNode otherNode = null;
            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                otherNode = ((DiagramModelNode)nextAssociation.getTargetNode());
                sourceNode.removeSourceConnection(nextAssociation);
                otherNode.removeTargetConnection(nextAssociation);
                sourceNode.updateAssociations();
                otherNode.updateAssociations();

                int nConnections = otherNode.getSourceConnections().size() + otherNode.getTargetConnections().size();
                if (nConnections < 1) {
                    if (otherNode instanceof FocusModelNode) removeFocusNode(diagramNode, otherNode);
                    else diagramNode.removeChild(otherNode, false);
                }

                List labelNodes = nextAssociation.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    Iterator labelIter = labelNodes.iterator();
                    LabelModelNode nextNode = null;
                    while (labelIter.hasNext()) {
                        nextNode = (LabelModelNode)labelIter.next();
                        diagramNode.removeChild(nextNode, false);
                    }
                }
            }
            List targetConnections = new ArrayList(sourceNode.getTargetConnections());
            iter = targetConnections.iterator();
            nextAssociation = null;

            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                otherNode = ((DiagramModelNode)nextAssociation.getSourceNode());
                sourceNode.removeTargetConnection(nextAssociation);
                otherNode.removeSourceConnection(nextAssociation);
                sourceNode.updateAssociations();

                otherNode.updateAssociations();
                int nConnections = otherNode.getSourceConnections().size() + otherNode.getTargetConnections().size();
                if (nConnections < 1) {
                    if (otherNode instanceof FocusModelNode) removeFocusNode(diagramNode, otherNode);
                    else diagramNode.removeChild(otherNode, false);
                }

                List labelNodes = nextAssociation.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    Iterator labelIter = labelNodes.iterator();
                    LabelModelNode nextNode = null;
                    while (labelIter.hasNext()) {
                        nextNode = (LabelModelNode)labelIter.next();
                        diagramNode.removeChild(nextNode, false);
                    }
                }
            }
        } else if (sourceNode != null && sourceNode instanceof FocusModelNode) {
            List sourceConnections = new ArrayList(sourceNode.getSourceConnections());
            Iterator iter = sourceConnections.iterator();
            NodeConnectionModel nextAssociation = null;
            DiagramModelNode otherNode = null;
            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                otherNode = ((DiagramModelNode)nextAssociation.getTargetNode());
                sourceNode.removeSourceConnection(nextAssociation);
                otherNode.removeTargetConnection(nextAssociation);
                sourceNode.updateAssociations();
                otherNode.updateAssociations();

                int nConnections = sourceNode.getSourceConnections().size() + sourceNode.getTargetConnections().size();
                if (nConnections < 1) removeFocusNode(diagramNode, sourceNode);

                List labelNodes = nextAssociation.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    Iterator labelIter = labelNodes.iterator();
                    LabelModelNode nextNode = null;
                    while (labelIter.hasNext()) {
                        nextNode = (LabelModelNode)labelIter.next();
                        diagramNode.removeChild(nextNode, false);
                    }
                }
            }
            List targetConnections = new ArrayList(sourceNode.getTargetConnections());
            iter = targetConnections.iterator();
            nextAssociation = null;

            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                otherNode = ((DiagramModelNode)nextAssociation.getSourceNode());
                sourceNode.removeTargetConnection(nextAssociation);
                otherNode.removeSourceConnection(nextAssociation);
                sourceNode.updateAssociations();

                otherNode.updateAssociations();
                int nConnections = sourceNode.getSourceConnections().size() + sourceNode.getTargetConnections().size();
                if (nConnections < 1) removeFocusNode(diagramNode, sourceNode);

                List labelNodes = nextAssociation.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    Iterator labelIter = labelNodes.iterator();
                    LabelModelNode nextNode = null;
                    while (labelIter.hasNext()) {
                        nextNode = (LabelModelNode)labelIter.next();
                        diagramNode.removeChild(nextNode, false);
                    }
                }
            }
        } else if (sourceNode != null && sourceNode instanceof RelationshipTypeModelNode) {
            List sourceConnections = new ArrayList(sourceNode.getSourceConnections());
            Iterator iter = sourceConnections.iterator();
            NodeConnectionModel nextAssociation = null;
            DiagramModelNode otherNode = null;
            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                otherNode = ((DiagramModelNode)nextAssociation.getTargetNode());
                sourceNode.removeSourceConnection(nextAssociation);
                otherNode.removeTargetConnection(nextAssociation);
                sourceNode.updateAssociations();
                otherNode.updateAssociations();
            }
            List targetConnections = new ArrayList(sourceNode.getTargetConnections());
            iter = targetConnections.iterator();
            nextAssociation = null;

            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                otherNode = ((DiagramModelNode)nextAssociation.getSourceNode());
                sourceNode.removeTargetConnection(nextAssociation);
                otherNode.removeSourceConnection(nextAssociation);
                sourceNode.updateAssociations();
                otherNode.updateAssociations();
            }
        }

    }

    protected void removeAssociationsForNode( DiagramModelNode diagramNode,
                                              FocusModelNode focusNode,
                                              RelationshipModelNode relationshipNode ) {

        if (focusNode != null) {
            List sourceConnections = new ArrayList(focusNode.getSourceConnections());
            Iterator iter = sourceConnections.iterator();
            NodeConnectionModel nextAssociation = null;

            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                if (nextAssociation.getTargetNode().equals(relationshipNode)) {
                    focusNode.removeSourceConnection(nextAssociation);

                    ((DiagramModelNode)nextAssociation.getTargetNode()).removeTargetConnection(nextAssociation);
                    focusNode.updateAssociations();
                    relationshipNode.updateAssociations();

                    List labelNodes = nextAssociation.getLabelNodes();
                    if (labelNodes != null && !labelNodes.isEmpty()) {
                        Iterator labelIter = labelNodes.iterator();
                        LabelModelNode nextNode = null;
                        while (labelIter.hasNext()) {
                            nextNode = (LabelModelNode)labelIter.next();
                            diagramNode.removeChild(nextNode, false);
                        }
                    }
                }

            }

            List targetConnections = new ArrayList(focusNode.getTargetConnections());
            iter = targetConnections.iterator();
            nextAssociation = null;

            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                if (nextAssociation.getSourceNode().equals(relationshipNode)) {
                    focusNode.removeTargetConnection(nextAssociation);
                    relationshipNode.removeSourceConnection(nextAssociation);

                    focusNode.updateAssociations();
                    relationshipNode.updateAssociations();

                    List labelNodes = nextAssociation.getLabelNodes();
                    if (labelNodes != null && !labelNodes.isEmpty()) {
                        Iterator labelIter = labelNodes.iterator();
                        LabelModelNode nextNode = null;
                        while (labelIter.hasNext()) {
                            nextNode = (LabelModelNode)labelIter.next();
                            diagramNode.removeChild(nextNode, false);
                        }
                    }

                }
            }
        }

    }

    private void removeFocusNode( DiagramModelNode diagramNode,
                                  DiagramModelNode focusNode ) {
        DiagramModelNode focusLabelNode = ((FocusModelNode)focusNode).getLabelNode();
        if (focusLabelNode != null) diagramNode.removeChild(focusLabelNode, false);
        diagramNode.removeChild(focusNode, false);
        DiagramEntity de = focusNode.getDiagramModelObject();
        if (de != null) {
            DiagramEntityAdapter.setModelObject(de, null);
            DiagramEntityManager.cleanDiagramEntities((Diagram)diagramNode.getModelObject());
            DiagramEntityManager.cleanUpDiagram((Diagram)diagramNode.getModelObject());
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.notation.NotationModelGenerator#performUpdate(com.metamatrix.modeler.diagram.ui.model.DiagramModelNode, org.eclipse.emf.common.notify.Notification)
     */
    public void performUpdate( DiagramModelNode diagramNode,
                               DiagramModelNode targetNode,
                               Notification setNotification ) {
        if (targetNode instanceof RelationshipModelNode) {
            ((RelationshipModelNode)targetNode).refreshForNameChange();
        } else if (targetNode instanceof RelationshipFolderModelNode) {
            ((RelationshipFolderModelNode)targetNode).refreshForNameChange();
        } else if (targetNode instanceof RelationshipTypeModelNode) {
            if (setNotification.getEventType() == Notification.REMOVE || setNotification.getEventType() == Notification.ADD) {
                // Assume that supertype/supertype is changed??? so
                updateTypeAssociations(diagramNode, (RelationshipTypeModelNode)targetNode);
                targetNode.updateAssociations();
            } else ((RelationshipTypeModelNode)targetNode).refreshForNameChange();
        } else if (targetNode instanceof FocusModelNode) {
            ((FocusModelNode)targetNode).refreshForNameChange();
        }

    }

    public void updateRelationships( DiagramModelNode diagramNode,
                                     RelationshipType someType ) {
        List relNodes = RelationshipDiagramUtil.getRelationshipsForType(diagramNode, someType);
        Iterator iter = relNodes.iterator();

        RelationshipModelNode nextNode = null;
        while (iter.hasNext()) {
            nextNode = (RelationshipModelNode)iter.next();
            nextNode.refreshForNameChange();
        }
        RelationshipTypeModelNode typeNode = (RelationshipTypeModelNode)getNodeInDiagram(diagramNode, someType);
        if (typeNode != null) typeNode.refreshForNameChange();
    }

    public void updateTypeAssociations( DiagramModelNode diagramNode ) {
        // These associations have to do with BinaryAssociations. In this diagram case.... it means
        // sub/super types for RelationshipTypes.

        // Walk through the model objects and gather up all relationship types.
        Iterator iter = diagramNode.getChildren().iterator();
        Object nextObject = null;
        RelationshipTypeModelNode nextRTMN = null;
        // RelationshipType thisType = null;
        // RelationshipType superType = null;
        while (iter.hasNext()) {
            nextObject = iter.next();
            if (nextObject instanceof RelationshipTypeModelNode) {
                nextRTMN = (RelationshipTypeModelNode)nextObject;
                updateTypeAssociations(diagramNode, nextRTMN);
                //					
                // thisType = (RelationshipType)nextRTMN.getModelObject();
                // List subTypes = thisType.getSubType();
                // superType = thisType.getSuperType();
                // if( subTypes != null && !subTypes.isEmpty()) {
                // EObject nextSubType = null;
                // DiagramModelNode subTypeNode = null;
                // Iterator innerIter = subTypes.iterator();
                // while( innerIter.hasNext() ) {
                // nextSubType = (EObject)innerIter.next();
                // subTypeNode = getModelNode(diagramNode, nextSubType);
                // if( subTypeNode != null && !DiagramUiUtilities.nodesAreConnected(nextRTMN, subTypeNode)) {
                // addTypeAssociation(diagramNode, subTypeNode, nextRTMN);
                // }
                // }
                // }
                // if( superType != null ) {
                // DiagramModelNode superTypeNode = getModelNode(diagramNode, superType);
                // if( superTypeNode != null && !DiagramUiUtilities.nodesAreConnected(nextRTMN, superTypeNode)) {
                // addTypeAssociation(diagramNode, nextRTMN, superTypeNode);
                // }
                //							
                // }

            }
        }

    }

    private void updateTypeAssociations( DiagramModelNode diagramNode,
                                         RelationshipTypeModelNode relTypeModelNode ) {
        removeTypeAssociationsForNode(diagramNode, relTypeModelNode);
        addTypeAssociationsForNode(diagramNode, relTypeModelNode);
    }

    private void addTypeAssociationsForNode( DiagramModelNode diagramNode,
                                             RelationshipTypeModelNode relTypeModelNode ) {
        RelationshipType thisType = null;
        RelationshipType superType = null;

        thisType = (RelationshipType)relTypeModelNode.getModelObject();
        List subTypes = thisType.getSubType();
        superType = thisType.getSuperType();
        if (subTypes != null && !subTypes.isEmpty()) {
            EObject nextSubType = null;
            DiagramModelNode subTypeNode = null;
            Iterator innerIter = subTypes.iterator();
            while (innerIter.hasNext()) {
                nextSubType = (EObject)innerIter.next();
                subTypeNode = getModelNode(diagramNode, nextSubType);
                if (subTypeNode != null && !DiagramUiUtilities.nodesAreConnected(relTypeModelNode, subTypeNode)) {
                    addTypeAssociation(diagramNode, subTypeNode, relTypeModelNode);
                }
            }
        }
        if (superType != null) {
            DiagramModelNode superTypeNode = getModelNode(diagramNode, superType);
            if (superTypeNode != null && !DiagramUiUtilities.nodesAreConnected(relTypeModelNode, superTypeNode)) {
                addTypeAssociation(diagramNode, relTypeModelNode, superTypeNode);
            }

        }
    }

    private void removeTypeAssociationsForNode( DiagramModelNode diagramNode,
                                                RelationshipTypeModelNode relTypeModelNode ) {
        List sourceConnections = new ArrayList(relTypeModelNode.getSourceConnections());
        Iterator iter = sourceConnections.iterator();
        NodeConnectionModel nextAssociation = null;
        DiagramModelNode otherNode = null;

        while (iter.hasNext()) {
            nextAssociation = (NodeConnectionModel)iter.next();
            otherNode = (DiagramModelNode)nextAssociation.getTargetNode();
            if (otherNode != null && otherNode instanceof RelationshipTypeModelNode) {
                relTypeModelNode.removeSourceConnection(nextAssociation);
                otherNode.removeTargetConnection(nextAssociation);
                relTypeModelNode.updateAssociations();
                otherNode.updateAssociations();

                List labelNodes = nextAssociation.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    Iterator labelIter = labelNodes.iterator();
                    LabelModelNode nextNode = null;
                    while (labelIter.hasNext()) {
                        nextNode = (LabelModelNode)labelIter.next();
                        diagramNode.removeChild(nextNode, false);
                    }
                }
            }

        }

        List targetConnections = new ArrayList(relTypeModelNode.getTargetConnections());
        iter = targetConnections.iterator();
        nextAssociation = null;

        while (iter.hasNext()) {
            nextAssociation = (NodeConnectionModel)iter.next();
            otherNode = (DiagramModelNode)nextAssociation.getSourceNode();
            if (otherNode != null && otherNode instanceof RelationshipTypeModelNode) {
                relTypeModelNode.removeTargetConnection(nextAssociation);
                otherNode.removeSourceConnection(nextAssociation);
                otherNode.updateAssociations();
                relTypeModelNode.updateAssociations();

                List labelNodes = nextAssociation.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    Iterator labelIter = labelNodes.iterator();
                    LabelModelNode nextNode = null;
                    while (labelIter.hasNext()) {
                        nextNode = (LabelModelNode)labelIter.next();
                        diagramNode.removeChild(nextNode, false);
                    }
                }

            }
        }
    }

}
