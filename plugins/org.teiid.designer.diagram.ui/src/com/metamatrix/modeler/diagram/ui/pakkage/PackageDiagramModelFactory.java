/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.pakkage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.DefaultIgnorableNotificationSource;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelFactoryImpl;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.notation.NotationModelGenerator;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * PackageDiagramModelFactory
 */
public class PackageDiagramModelFactory extends DiagramModelFactoryImpl implements DiagramUiConstants {
    private String sNotationId;

    private static final int I_BORDER = 20;
    private static final int X_INC = 20;
    private static final int Y_INC = 10;
    private static final int X_MAX = 600;
    private static Point startingPoint = new Point(I_BORDER, I_BORDER);
    private static int yInc = 20;

    private static final String KEY_PACKAGE_DIAGRAM_NAME = "DiagramNames.packageDiagram"; //$NON-NLS-1$
    private static final String THIS_CLASS = "PackageDiagramModelFactory"; //$NON-NLS-1$

    private NotationModelGenerator generator;

    private HashMap reconcileClassifierNodes;

    /**
     * 
     */
    public PackageDiagramModelFactory() {
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

        HashMap nodeMap = new HashMap();

        setSNotationId(sNotationId);

        Diagram diagram = (Diagram)baseObject;

        DiagramModelNode diagramModelNode = null;
        List contents = null;

        // Get package EObject from PackageDiagramResource

        EObject packageObject = null;

        Object targetObject = diagram.getTarget();

        // Get contents of the package, iterate EObjects

        if (targetObject != null && targetObject instanceof EObject && !(targetObject instanceof ModelAnnotation)) {
            packageObject = diagram.getTarget();
            // diagramModelNode = new PackageDiagramNode(packageObject, Util.getString( KEY_PACKAGE_DIAGRAM_NAME) );
            diagramModelNode = new PackageDiagramNode(diagram, Util.getString(KEY_PACKAGE_DIAGRAM_NAME));
            contents = packageObject.eContents();
        } else if (targetObject != null && targetObject instanceof EObject && targetObject instanceof ModelAnnotation) {
            diagramModelNode = new PackageDiagramNode(diagram, Util.getString(KEY_PACKAGE_DIAGRAM_NAME));
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(diagram);
            if (modelResource != null) {
                try {
                    contents = modelResource.getEObjects();
                } catch (ModelWorkspaceException e) {
                    contents = Collections.EMPTY_LIST;
                    String message = Util.getString("ModelErrors.getContentError", modelResource.getItemName()); //$NON-NLS-1$
                    DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
                }
            } else {
                contents = Collections.EMPTY_LIST;
            }
        } else {
            contents = Collections.EMPTY_LIST;
        }

        int interval;
        int iStage;
        int iCount;
        int nComponents = contents.size();
        String progressMessage;
        int nStages = 1;

        if (nComponents < 10) interval = 1;
        else if (nComponents < 50) interval = 2;
        else if (nComponents < 200) interval = 5;
        else if (nComponents < 400) interval = 10;
        else if (nComponents < 800) interval = 20;
        else interval = (int)((double)nComponents / (double)50);

        nStages = (int)((double)nComponents / (double)interval);
        int progressInc = (int)(50.0 / nStages);
        if (progressInc == 0) progressInc = 1;

        boolean showProgress = false;
        if (monitor != null) {
            monitor.subTask("Creating Model Components"); //$NON-NLS-1$
            showProgress = true;
        }

        if (!contents.isEmpty()) {
            iStage = 1;
            iCount = 0;
            int nComps = 0;

            Iterator iter = contents.iterator();
            // long startTime = 0;
            if (getGenerator() != null) {
                while (iter.hasNext()) {
                    // Get current EObject
                    EObject eObj = (EObject)iter.next();
                    // Check to see that it isn't a non-drawable object like "JdbcSource" (i.e. connection) object.

                    if (isDrawable(eObj)) {
                        // startTime = System.currentTimeMillis();
                        DiagramModelNode childModelNode = getGenerator().createModel(eObj, diagram);
                        // if( nComps % 50 == 0 )
                        // System.out.println(" PDMF.createModel() Object[" + nComps + "]  DeltaT = [" +
                        // (System.currentTimeMillis() - startTime) + "]");
                        if (childModelNode != null) {
                            nodeMap.put(eObj, childModelNode);

                            childModelNode.setParent(diagramModelNode);
                            diagramModelNode.addChild(childModelNode);
                            if (showProgress) {
                                nComps++;
                                iCount++;
                                if (iCount == interval && iStage < nStages) {
                                    progressMessage = "Created " + nComps + //$NON-NLS-1$
                                                      " of " + nComponents + //$NON-NLS-1$
                                                      " Diagram Components"; //$NON-NLS-1$
                                    monitor.subTask(progressMessage);
                                    monitor.worked(progressInc);
                                    iStage++;
                                    iCount = 0;
                                }
                            }
                        }
                    }
                }
            } else {
                Util.log(IStatus.WARNING, Util.getString(Errors.MODEL_GENERATOR_FAILURE));
            }
            if (showProgress) {
                monitor.subTask("Adding Association Links"); //$NON-NLS-1$
            }
            if (!diagramModelNode.getChildren().isEmpty()) {
                // Let's create a map containing binary associations keyed to the EObject reference
                HashMap connMap = new HashMap();
                nComps = 0;

                // Let's get contents, and get associations for each object
                List realAssociations = new ArrayList();
                List currentChildren = diagramModelNode.getChildren();
                iter = currentChildren.iterator();
                while (iter.hasNext()) {
                    DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
                    List childAssList = childModelNode.getAssociations(nodeMap);
                    if (showProgress) {
                        nComps++;
                        if (nComps % 20 == 0) {
                            progressMessage = "Constructed Associations for " + nComps + //$NON-NLS-1$
                                              " of " + nComponents + //$NON-NLS-1$
                                              " Diagram Components"; //$NON-NLS-1$
                            monitor.subTask(progressMessage);
                        }
                    }
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
                    // realAssociations.addAll(childModelNode.getAssociations());
                }

                // List realAssociations = getGenerator().getAssociations(allAssociations, currentChildren);
                if (showProgress) monitor.subTask("Adding Association Labels"); //$NON-NLS-1$
                iter = realAssociations.iterator();
                String currentRouterStyle = DiagramLinkType.get(DiagramEditorUtil.getCurrentDiagramRouterStyle()).getName();
                while (iter.hasNext()) {
                    NodeConnectionModel nextAssociation = (NodeConnectionModel)iter.next();
                    if (nextAssociation.getDiagramLink() == null) nextAssociation.setRouterStyle(currentRouterStyle);
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

        return diagramModelNode;
    }

    protected NotationModelGenerator getGenerator() {
        if (generator == null) generator = DiagramUiPlugin.getDiagramNotationManager().getDiagramModelGenerator(getSNotationId());

        return generator;
    }

    private boolean isValidDiagram( DiagramModelNode diagramModelNode ) {
        boolean result = false;
        Diagram diagram = (Diagram)diagramModelNode.getModelObject();
        if (diagram != null && diagram.getTarget() != null) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(diagram);
            if (mr != null) {
                String type = diagram.getType();
                if (type != null) {
                    if (type.equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID)) result = true;
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

        if (currentDiagramOK && isValidDiagram(diagramModelNode) && sourceIsNotThis(notification)
            && shouldHandleNotification(notification, diagramModelNode)) {

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

                // Defect 23268 - changing isUndoable to false. DiagramEntities can be created and will be deleted as needed
                requiredStart = ModelerCore.startTxn(false, false, "Update Package Diagram", this); //$NON-NLS-1$$

                handleNotification(notification, diagramModelNode);

                // We've cached up classifier nodes to reconcile so we only do it once...
                reconcileClassifiers(diagramModelNode);

                DiagramEntityManager.cleanDiagramEntities(diagram);

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
                if (handleConstruction) {
                    DiagramEditorUtil.setDiagramConstructionComplete(diagram, true);
                }
            }
        }

        return currentDiagramOK;
    }

    private boolean shouldHandleNotification( Notification notification,
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
                    
                    if( !notifications.isEmpty() ) {
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
	                            } else if (targetObject instanceof Resource && diagramModelResource != null) {
	                                Resource targetResource = (Resource)targetObject;
	                                if (targetResource.equals(diagramModelResource)) shouldHandle = true;
	                            } else if (targetObject instanceof Diagram && NotificationUtilities.isRemoved(nextNotification)) {
	                                ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
	                                if (mr != null && mr.equals(diagramMR)) {
	                                    shouldHandle = true;
	                                }
	                            } else if (targetObject instanceof Diagram) {
	                                if (notification.getNewValue() instanceof DiagramLinkType) shouldHandle = true;
	                            }
	                        }
	                    }
                    } else {
                    	// Single SourceNotificationImpl
                    	Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                    	if( changedObj != null && changedObj instanceof EmfResource) {
                        	ModelResource mr = ModelUtilities.getModelResource((Resource)changedObj, false);
                        	if( mr == diagramMR) {
                        		shouldHandle = true;
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

    private boolean sourceIsNotThis( Notification notification ) {
        if (notification instanceof SourcedNotification) {

            Object source = ((SourcedNotification)notification).getSource();

            if (source == null) return true;

            return !(source.equals(this));
        }

        return true;
    }

    public Diagram getDiagram( EObject someTarget ) {
        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(someTarget);
        try {
            List returnedDiagrams = modelResource.getModelDiagrams().getDiagrams(someTarget);
            if (returnedDiagrams.size() == 1) {
                return (Diagram)returnedDiagrams.get(0);
            }
            // Find the one for package diagram
            Iterator iter = returnedDiagrams.iterator();
            while (iter.hasNext()) {
                Diagram nextDiagram = (Diagram)iter.next();
                if (nextDiagram.getType() != null && nextDiagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID)) return nextDiagram;
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.ModelNotificationHandler#performUpdate(org.eclipse.emf.common.notify.Notification, int)
     */
    public void handleNotification( Notification notification,
                                    DiagramModelNode packageDiagramModelNode ) {
        if (notification instanceof SourcedNotification) {
            Collection notifications = ((SourcedNotification)notification).getNotifications();
            if( !notifications.isEmpty() ) {
	            Iterator iter = notifications.iterator();
	            while (iter.hasNext()) {
	                handleSingleNotification((Notification)iter.next(), packageDiagramModelNode);
	            }
            } else {
            	handleSingleNotification(notification, packageDiagramModelNode);
            }
        } else {
            handleSingleNotification(notification, packageDiagramModelNode);
        }
    }

    private void handleSingleNotification( Notification notification,
                                           DiagramModelNode packageDiagramModelNode ) {
        if (NotificationUtilities.isAdded(notification)) {
            performAdd(notification, packageDiagramModelNode);
        } else if (NotificationUtilities.isRemoved(notification)) {
            performRemove(notification, packageDiagramModelNode);
        } else if (NotificationUtilities.isChanged(notification)) {
            performChange(notification, packageDiagramModelNode);
        }
    }

    private void performAdd( Notification notification,
                             DiagramModelNode packageDiagramModelNode ) {
        boolean performedChange = false;

        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            EObject targetObject = NotificationUtilities.getEObject(notification);

            // We know that we have an object that is not a child of a model resource, therefore,
            // it is safe to assume that it would exist on a normal package diagram.
            // Let's check to see if the target of the current "Diagram", is the same as the
            // parent of the new target object.
            Diagram diagram = (Diagram)packageDiagramModelNode.getModelObject();

            boolean hasAssociations = false;
            EObject diagramTargetEObject = diagram.getTarget();
            if (diagramTargetEObject.equals(targetObject)) {
                // We have a match, get the added children and hand them off to the generator to construct
                // and add to this packageDiagramModelNode
                DiagramModelNode childModelNode = null;
                EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                for (int iChild = 0; iChild < newChildren.length; iChild++) {
                    childModelNode = getModelNode(packageDiagramModelNode, newChildren[iChild]);
                    if (childModelNode == null) {
                        childModelNode = getGenerator().createModel(newChildren[iChild],
                                                                    (Diagram)packageDiagramModelNode.getModelObject());
                        if (childModelNode != null) {
                            childModelNode.setParent(packageDiagramModelNode);
                            childModelNode.setPosition(getStartingPoint());
                            packageDiagramModelNode.addChild(childModelNode);
                            performedChange = true;
                            if (!hasAssociations && childModelNode.getAssociations(getNodeMap(packageDiagramModelNode)) != null) {
                                hasAssociations = true;
                            }
                        }
                    }
                }
                if (performedChange && hasAssociations) updateAssociations(packageDiagramModelNode, packageDiagramModelNode);

            } else if (NotificationUtilities.addedChildrenParentIsNotifier(notification)) {
                boolean isNested = false;
                // Now we check to see if the target object is already in diagram
                DiagramModelNode targetNode = getNodeInDiagram(packageDiagramModelNode, targetObject);
                // if still null, check if it's nested
                if (targetNode == null) {
                    targetNode = getModelNode(packageDiagramModelNode, targetObject);
                    if (targetNode != null) isNested = true;
                }

                if (targetNode != null) {
                    // This case where the node is in package diagram and we need to delegate to the object node to add a child
                    // We have a match, get the added children and hand them off to the generator to construct
                    // and add to this the corresponding targetNode
                    EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                    if (newChildren.length > 0) {
                        EObject childParent = newChildren[0].eContainer();
                        DiagramModelNode parentNode = getModelNode(packageDiagramModelNode, childParent);

                        if (parentNode != null) {
                            if (parentNode instanceof UmlClassifierNode) {
                                addReconcileClassifier(parentNode);
                                // ((UmlClassifierNode)parentNode).reconcile();
                                // ((UmlClassifierNode)parentNode).update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
                                // performedChange = true;
                            } else {
                                for (int iChild = 0; iChild < newChildren.length; iChild++) {
                                    DiagramModelNode newNode = getGenerator().createChildModel(parentNode, newChildren[iChild]);
                                    if (newNode != null) performedChange = true;
                                }
                            }
                        }
                        if (performedChange) updateAssociations(parentNode, packageDiagramModelNode);
                    }
                }

                if (isNested) {
                    EObject parentEObject = targetObject.eContainer();
                    DiagramModelNode parentNode = getModelNode(packageDiagramModelNode, parentEObject);
                    if (parentNode != null) {
                        parentNode.updateForChild(false);
                    }
                }
            } else {
                updateAssociations(packageDiagramModelNode, packageDiagramModelNode);
            }

        } else {
            Diagram diagram = (Diagram)packageDiagramModelNode.getModelObject();
            EObject diagramTargetEObject = diagram.getTarget();
            Resource diagramModelResource = null;
            boolean hasAssociations = false;
            if (diagramTargetEObject instanceof ModelAnnotation) diagramModelResource = diagramTargetEObject.eResource();

            // target of notification (notifier) is ModelResource
            Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
            if (diagramModelResource != null && changedObj != null && changedObj instanceof Resource) {
                Resource targetResource = (Resource)changedObj;
                if (targetResource.equals(diagramModelResource)) {
                    // We have a match, get the added children and hand them off to the generator to construct
                    // and add to this packageDiagramModelNode
                    DiagramModelNode childModelNode = null;
                    EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                    for (int iChild = 0; iChild < newChildren.length; iChild++) {
                        childModelNode = getModelNode(packageDiagramModelNode, newChildren[iChild]);
                        if (childModelNode == null) {
                            childModelNode = getGenerator().createModel(newChildren[iChild],
                                                                        (Diagram)packageDiagramModelNode.getModelObject());
                            if (childModelNode != null) {
                                childModelNode.setParent(packageDiagramModelNode);
                                childModelNode.setPosition(getStartingPoint());
                                packageDiagramModelNode.addChild(childModelNode);
                                performedChange = true;
                                if (childModelNode.getAssociations(getNodeMap(packageDiagramModelNode)) != null) {
                                    hasAssociations = true;
                                }
                            }
                        }
                    }
                }
                if (performedChange && hasAssociations) updateAssociations(packageDiagramModelNode, packageDiagramModelNode);
            }
        }
    }

    private void performRemove( Notification notification,
                                DiagramModelNode packageDiagramModelNode ) {
        if (NotificationUtilities.isEObjectNotifier(notification)) {
            // we know that the object is not a child of a model resource !!!!!
            EObject targetObject = NotificationUtilities.getEObject(notification);

            DiagramModelNode parentNode = getModelNode(packageDiagramModelNode, targetObject);

            DiagramModelNode removedNode = null;
            EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
            for (int iChild = 0; iChild < removedChildren.length; iChild++) {
                removedNode = getModelNode(packageDiagramModelNode, removedChildren[iChild]);
                if (removedNode != null) {
                    if (parentNode != null) {
                        if (!(parentNode instanceof UmlClassifierNode)) {
                            parentNode.removeChild(removedNode, false);
                        }

                        // updateAssociations(parentNode, packageDiagramModelNode);
                        // performedChange = true;
                    } else {
                        removeAllAssociationsFromNode(removedNode, packageDiagramModelNode);
                        packageDiagramModelNode.removeChild(removedNode, true);
                        // updateAssociations(removedNode, packageDiagramModelNode);
                    }
                }
            }
            if (parentNode instanceof UmlClassifierNode) {
                addReconcileClassifier(parentNode);
                // ((UmlClassifierNode)parentNode).reconcile();
            }
        } else {
            Diagram diagram = (Diagram)packageDiagramModelNode.getModelObject();
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
                        removedNode = getModelNode(packageDiagramModelNode, removedChildren[iChild]);
                        if (removedNode != null) {
                            removeAllAssociationsFromNode(removedNode, packageDiagramModelNode);
                            packageDiagramModelNode.removeChild(removedNode, true);
                            // updateAssociations(removedNode, packageDiagramModelNode);
                            // performedChange = true;
                        }
                    }
                }
            }
        }
    }

    protected void performChange( Notification notification,
                                  DiagramModelNode packageDiagramModelNode ) {
        EObject targetObject = NotificationUtilities.getEObject(notification);
        if (notification.getEventType() == Notification.MOVE) {
            DiagramModelNode targetNode = getModelNode(packageDiagramModelNode, targetObject);
            if (targetNode != null) {
                targetNode.updateForChild(true);
            }
        } else if (targetObject instanceof Diagram) {
            if (notification.getNewValue() instanceof DiagramLinkType) {
                String newValue = ((DiagramLinkType)notification.getNewValue()).getName();

                String oldValue = null;
                if (notification.getOldValue() instanceof DiagramLinkType) oldValue = ((DiagramLinkType)notification.getOldValue()).getName();

                packageDiagramModelNode.update(DiagramUiConstants.DiagramNodeProperties.ROUTER, oldValue, newValue);
            }
        } else {
            DiagramModelNode targetNode = getModelNode(packageDiagramModelNode, targetObject);
            if (targetNode != null) {
                // If object's parent is in our current model
                // Assume this is a rename for now.
                getGenerator().performUpdate(targetNode, notification);

                // Check to see if eObject is of type UmlAssociation object
                if (getGenerator().isAssociation(targetObject)) {
                    DiagramModelNode topClassNode = DiagramUiUtilities.getTopClassifierParentNode(targetNode);
                    if (topClassNode != null) updateAssociations(topClassNode, packageDiagramModelNode);
                    // updateAssociations(packageDiagramModelNode, packageDiagramModelNode);
                    DiagramUiUtilities.hiliteCurrentSelectionDependencies();
                } else {
                    if (notification.getEventType() == Notification.REMOVE || notification.getEventType() == Notification.ADD) {
                        // Assume that supertype is changed??? so
                        DiagramModelNode topClassNode = DiagramUiUtilities.getTopClassifierParentNode(targetNode);
                        if (topClassNode != null) updateAssociations(topClassNode, packageDiagramModelNode);
                    }
                }
            }
        }
    }

    protected boolean associationExists( DiagramModelNode diagramModelNode,
                                         NodeConnectionModel targetAssociation ) {
        // get all connections from model
        List currentChildren = diagramModelNode.getChildren();
        Iterator iter = currentChildren.iterator();
        while (iter.hasNext()) {
            DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
            List sourceConnections = childModelNode.getSourceConnections();
            // Walk through the source connections and check if the same info.
            NodeConnectionModel nextAssociation = null;
            Iterator sIter = sourceConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if (associationsMatch(targetAssociation, nextAssociation)) return true;
            }
            // Walk through the target connections and check if the same info.
            List targetConnections = childModelNode.getTargetConnections();
            sIter = targetConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if (associationsMatch(targetAssociation, nextAssociation)) return true;
            }
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

    protected List getStaleAssociations( List expectedAssociations,
                                         DiagramModelNode diagramModelNode ) {
        List currentAssociations = getCurrentAssociations(diagramModelNode);
        if (currentAssociations.isEmpty()) return Collections.EMPTY_LIST;
        return getStaleAssociations(expectedAssociations, currentAssociations);
    }

    protected List getStaleAssociations( List expectedAssociations,
                                         List currentAssociations ) {
        List staleAssociations = new ArrayList();

        // get all connections from model
        // walk through expected associations.
        Iterator iter = currentAssociations.iterator();
        Iterator expectedIter = null;
        NodeConnectionModel nextCurrentAssociation = null;
        NodeConnectionModel nextExpectedAssociation = null;
        boolean foundMatch = false;
        while (iter.hasNext()) {
            foundMatch = false;
            nextCurrentAssociation = (NodeConnectionModel)iter.next();
            expectedIter = expectedAssociations.iterator();
            while (expectedIter.hasNext() && !foundMatch) {
                nextExpectedAssociation = (NodeConnectionModel)expectedIter.next();
                if (associationsMatch(nextExpectedAssociation, nextCurrentAssociation)) {
                    foundMatch = true;
                }
            }

            if (!foundMatch) {
                staleAssociations.add(nextCurrentAssociation);
            }
        }

        return staleAssociations;
    }

    protected List getCurrentAssociations( DiagramModelNode diagramModelNode ) {
        List currentAssociations = new ArrayList();

        Iterator iter = diagramModelNode.getChildren().iterator();
        while (iter.hasNext()) {
            DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
            List sourceConnections = childModelNode.getSourceConnections();
            // Walk through the source connections and check if the same info.
            NodeConnectionModel nextAssociation = null;
            Iterator sIter = sourceConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if (!currentAssociations.contains(nextAssociation)) currentAssociations.add(nextAssociation);
            }

            // Walk through the target connections and check if the same info.
            List targetConnections = childModelNode.getTargetConnections();
            sIter = targetConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if (!currentAssociations.contains(nextAssociation)) currentAssociations.add(nextAssociation);
            }
        }

        return currentAssociations;
    }

    protected List cleanUpStaleAssociations( List staleAssociations,
                                             DiagramModelNode diagramModelNode ) {
        HashMap updatedNodes = new HashMap();
        Iterator iter = staleAssociations.iterator();
        NodeConnectionModel nextAssociation = null;

        while (iter.hasNext()) {
            nextAssociation = (NodeConnectionModel)iter.next();
            ((DiagramModelNode)nextAssociation.getSourceNode()).removeSourceConnection(nextAssociation);
            ((DiagramModelNode)nextAssociation.getTargetNode()).removeTargetConnection(nextAssociation);

            if (updatedNodes.get(nextAssociation.getSourceNode()) == null) updatedNodes.put(nextAssociation.getSourceNode(), "x"); //$NON-NLS-1$
            if (updatedNodes.get(nextAssociation.getTargetNode()) == null) updatedNodes.put(nextAssociation.getTargetNode(), "x"); //$NON-NLS-1$

            List labelNodes = nextAssociation.getLabelNodes();
            if (labelNodes != null && !labelNodes.isEmpty()) {
                Iterator labelIter = labelNodes.iterator();
                LabelModelNode nextNode = null;
                while (labelIter.hasNext()) {
                    nextNode = (LabelModelNode)labelIter.next();
                    diagramModelNode.removeChild(nextNode, false);
                }
            }
        }

        return new ArrayList(updatedNodes.keySet());
    }

    protected void removeAllAssociationsFromNode( DiagramModelNode diagramModelNode,
                                                  DiagramModelNode diagramNode ) {
        // We assume here that we are removing this object from the diagram
        List sourceConnections = new ArrayList(diagramModelNode.getSourceConnections());
        Iterator iter = sourceConnections.iterator();
        NodeConnectionModel nextAssociation = null;
        DiagramModelNode otherNode = null;
        while (iter.hasNext()) {
            nextAssociation = (NodeConnectionModel)iter.next();
            otherNode = ((DiagramModelNode)nextAssociation.getTargetNode());
            diagramModelNode.removeSourceConnection(nextAssociation);
            otherNode.removeTargetConnection(nextAssociation);
            diagramModelNode.updateAssociations();
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
        List targetConnections = new ArrayList(diagramModelNode.getTargetConnections());
        iter = targetConnections.iterator();
        nextAssociation = null;

        while (iter.hasNext()) {
            nextAssociation = (NodeConnectionModel)iter.next();
            otherNode = ((DiagramModelNode)nextAssociation.getSourceNode());
            diagramModelNode.removeTargetConnection(nextAssociation);
            otherNode.removeSourceConnection(nextAssociation);

            diagramModelNode.updateAssociations();
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

    protected void updateAssociations( DiagramModelNode diagramModelNode,
                                       DiagramModelNode diagramNode ) {
        HashMap nodeMap = getNodeMap(diagramNode);
        String currentRouterStyle = DiagramLinkType.get(DiagramEditorUtil.getCurrentDiagramRouterStyle()).getName();
        if (diagramModelNode instanceof PackageDiagramNode) {
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
                    if (nextAssociation.getDiagramLink() == null) nextAssociation.setRouterStyle(currentRouterStyle);
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
        } else if (diagramModelNode instanceof UmlClassifierNode) {
            NodeConnectionModel nextAssociation = null;
            List realAssociations = new ArrayList();

            List allAssociations = diagramModelNode.getAssociations(nodeMap);
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

            List currentAssociations = new ArrayList();

            List sourceConnections = diagramModelNode.getSourceConnections();
            // Walk through the source connections and check if the same info.
            Iterator sIter = sourceConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if (!currentAssociations.contains(nextAssociation)) currentAssociations.add(nextAssociation);
            }

            // Walk through the target connections and check if the same info.
            List targetConnections = diagramModelNode.getTargetConnections();
            sIter = targetConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if (!currentAssociations.contains(nextAssociation)) currentAssociations.add(nextAssociation);
            }

            List staleAssociations = getStaleAssociations(realAssociations, currentAssociations);

            List changedNodes = new ArrayList(cleanUpStaleAssociations(staleAssociations, diagramNode));

            HashMap updatedNodes = new HashMap();

            // Put the node in that started this whole process.
            updatedNodes.put(diagramModelNode, "x"); //$NON-NLS-1$

            Iterator iter = changedNodes.iterator();
            while (iter.hasNext()) {
                updatedNodes.put(iter.next(), "x"); //$NON-NLS-1$
            }

            updateNodeAssociations(updatedNodes.keySet());

            updatedNodes = new HashMap();
            // Put the node in that started this whole process.
            updatedNodes.put(diagramModelNode, "x"); //$NON-NLS-1$

            // Add new associations if they don't exist.
            iter = realAssociations.iterator();

            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                if (nextAssociation.getDiagramLink() == null) nextAssociation.setRouterStyle(currentRouterStyle);

                if (!associationExists(diagramNode, nextAssociation)) {
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
                            diagramNode.addChild(nextNode);
                        }
                    }
                }
            }

            // call updateLabels in case info changes. (i.e names, labels, etc...)
            iter = getCurrentAssociations(diagramNode).iterator();
            while (iter.hasNext()) {
                nextAssociation = (NodeConnectionModel)iter.next();
                nextAssociation.updateLabels();
                // ((DiagramModelNode)nextAssociation.getSourceNode()).updateAssociations();
                // ((DiagramModelNode)nextAssociation.getTargetNode()).updateAssociations();
            }

            updateNodeAssociations(updatedNodes.keySet());

        }
    }

    protected void updateNodeAssociations( Set diagramModelNodes ) {
        if (!diagramModelNodes.isEmpty()) {
            Iterator iter = diagramModelNodes.iterator();
            DiagramModelNode nextNode = null;
            while (iter.hasNext()) {
                nextNode = (DiagramModelNode)iter.next();
                // if( !nextNode.equals(diagramModelNode))
                nextNode.updateAssociations();
                nextNode.update(DiagramUiConstants.DiagramNodeProperties.BENDPOINT);
            }
        }
    }

    @Override
    public String toString() {
        return "PackageDiagamModelFactory()"; //$NON-NLS-1$
    }

    private Point getStartingPoint() {
        Point thisPoint = new Point(startingPoint);

        // Now we increment the point
        if (startingPoint.x < X_MAX) {
            startingPoint.x += X_INC;
            startingPoint.y += Y_INC;

        } else {
            yInc += 20;
            startingPoint.x = I_BORDER;
            startingPoint.y = yInc;
        }

        return thisPoint;
    }

    /**
     * Method which determines whether this EObject can be represented in a diagram or not.
     * 
     * @return boolean
     */
    @Override
    public boolean isDrawable( EObject eObject ) {
        boolean result = true;

        if (eObject instanceof JdbcSource) result = false;

        return result;
    }

    protected HashMap getNodeMap( DiagramModelNode diagramNode ) {
        HashMap nodeMap = new HashMap();
        Iterator iter = diagramNode.getChildren().iterator();
        DiagramModelNode dmn = null;
        while (iter.hasNext()) {
            dmn = (DiagramModelNode)iter.next();
            if (dmn.getModelObject() != null) nodeMap.put(dmn.getModelObject(), dmn);
        }
        return nodeMap;
    }

    /**
     * @return
     */
    public String getSNotationId() {
        return sNotationId;
    }

    /**
     * @param string
     */
    public void setSNotationId( String string ) {
        sNotationId = string;
    }

    private void addReconcileClassifier( DiagramModelNode classifierNode ) {
        if (reconcileClassifierNodes == null) reconcileClassifierNodes = new HashMap();

        if (reconcileClassifierNodes.get(classifierNode) == null) reconcileClassifierNodes.put(classifierNode, "x"); //$NON-NLS-1$
    }

    private void reconcileClassifiers( DiagramModelNode packageDiagramNode ) {
        if (reconcileClassifierNodes != null && !reconcileClassifierNodes.isEmpty()) {
            UmlClassifierNode nextNode = null;
            for (Iterator iter = reconcileClassifierNodes.keySet().iterator(); iter.hasNext();) {
                nextNode = (UmlClassifierNode)iter.next();
                nextNode.reconcile();
                // Call update on the classifier's children
                DiagramModelNode dNode = null;
                for (Iterator iter2 = nextNode.getChildren().iterator(); iter2.hasNext();) {
                    dNode = (DiagramModelNode)iter2.next();
                    dNode.update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
                    dNode.update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
                }
                nextNode.update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
                // nextNode.update(DiagramUiConstants.DiagramNodeProperties.SIZE);
                nextNode.update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
                updateAssociations(nextNode, packageDiagramNode);
            }
        }

        reconcileClassifierNodes = null;
    }

	@Override
	public boolean shouldRefreshDiagram(Notification notification,
			DiagramModelNode diagramModelNode, String sDiagramTypeId) {
		
		if( notification instanceof SourcedNotification ) {
			SourcedNotification sNot = (SourcedNotification)notification;
			if( sNot.getSource() instanceof DefaultIgnorableNotificationSource) {
				DefaultIgnorableNotificationSource isrc = (DefaultIgnorableNotificationSource)sNot.getSource();
				if( isrc.getActualSource().getClass().getName().indexOf("JdbcImportWizard") > -1 && //$NON-NLS-1$
						isrc.getSourceIdentifier() != null) {
					// Check same model name
					String mrName = ModelUtilities.getModelResource(diagramModelNode.getModelObject()).getItemName();
					if( mrName.equalsIgnoreCase(isrc.getSourceIdentifier())) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
    
}
