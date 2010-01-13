/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingModelFactory;
import com.metamatrix.modeler.diagram.ui.drawing.model.DrawingModelNode;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelFactoryImpl;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.NotationModelGenerator;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierContainerNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlPackageNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.RelationalUmlEObjectHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSourceManager;
import com.metamatrix.modeler.transformation.ui.connection.TransformationLink;
import com.metamatrix.modeler.transformation.ui.util.TransformationDiagramUtil;
import com.metamatrix.modeler.transformation.ui.util.TransformationUmlEObjectHelper;

/**
 * TransformModelFactory
 */
public class TransformationDiagramModelFactory extends DiagramModelFactoryImpl implements UiConstants {
    private static final String KEY_TRANSFORMATION_DIAGRAM_NAME = "DiagramNames.transformationDiagram"; //$NON-NLS-1$
    private static final String THIS_CLASS = "TransformationDiagramModelFactory"; //$NON-NLS-1$

    private String sNotationId;
    private NotationModelGenerator generator;

    private boolean hideLinksAlways = false;

    /**
     * Construct an instance of TransformModelFactory.
     */
    public TransformationDiagramModelFactory() {
        super();
    }

    protected void setNotationId( String sNotationId ) {
        this.sNotationId = sNotationId;
    }

    protected String getNotationId() {
        return sNotationId;
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

        setNotationId(sNotationId);

        Diagram diagram = (Diagram)baseObject;

        DiagramModelNode diagramModelNode = null;

        // Create the DiagramNode
        diagramModelNode = new TransformationDiagramNode(diagram, UiConstants.Util.getString(KEY_TRANSFORMATION_DIAGRAM_NAME));

        // Get Drawing Nodes
        List drawingNodes = DrawingModelFactory.getDrawingNodes(diagram, diagramModelNode);

        diagramModelNode.addChildren(getTransformationDiagramContents(diagramModelNode, diagram));

        // Need to clear
        cleanDiagramEntities(diagram);

        if (diagram.getType() != null && diagram.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID)) addDependencies(diagramModelNode,
                                                                                                                               diagram);

        addLockedImages(diagramModelNode);

        if (!drawingNodes.isEmpty()) diagramModelNode.addChildren(drawingNodes);

        return diagramModelNode;
    }

    private boolean isDependencyDiagram( Diagram diagram ) {
        if (diagram.getType() != null && diagram.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID)) {
            return true;
        }
        return false;
    }

    private boolean isDependencyDiagram( DiagramModelNode diagramNode ) {
        if (diagramNode.getModelObject() instanceof Diagram) {
            return isDependencyDiagram((Diagram)diagramNode.getModelObject());
        }
        return false;
    }

    protected List getTransformationDiagramContents( DiagramModelNode diagramModelNode,
                                                     Diagram transformationDiagram ) {
        List diagramContents = new ArrayList();

        EObject virtualGroupEObject = transformationDiagram.getTarget();
        DiagramModelNode targetModelNode = null;

        if (getGenerator() != null) {
            targetModelNode = getGenerator().createModel(virtualGroupEObject, transformationDiagram);
            if (targetModelNode != null) {

                diagramContents.add(targetModelNode);
            }
        } else {
            UiConstants.Util.log(IStatus.WARNING, UiConstants.Util.getString(DiagramUiConstants.Errors.MODEL_GENERATOR_FAILURE));
        }

        EObject transformationEObject = getTransformationObject(virtualGroupEObject);
        // Create Tranform Node here and add
        DiagramModelNode transformNode = new TransformationNode(transformationDiagram, transformationEObject);

        // Add Transform Node to diagram model
        diagramContents.add(transformNode);

        List labelNodes = null;

        if (showLinksInTransformation(diagramModelNode)) {
            TransformationLink targetLink = getTargetConnectionModel(transformNode, targetModelNode);
            ((DiagramModelNode)targetLink.getSourceNode()).addSourceConnection(targetLink);
            ((DiagramModelNode)targetLink.getTargetNode()).addTargetConnection(targetLink);

            labelNodes = targetLink.getLabelNodes();
            if (labelNodes != null && !labelNodes.isEmpty()) {
                diagramContents.addAll(labelNodes);
            }
        }
        // Now we need to get a list of "Sources"

        // Walk the list and add their links
        Iterator sourceIter = getSourceEObjects(transformationEObject).iterator();
        EObject nextSourceEObject = null;
        DiagramModelNode nextSourceNode = null;
        TransformationLink sourceLink = null;

        while (sourceIter.hasNext()) {
            nextSourceEObject = (EObject)sourceIter.next();
            if (getGenerator() != null) {
                nextSourceNode = getGenerator().createModel(nextSourceEObject, transformationDiagram);
                if (nextSourceNode != null) {
                    diagramContents.add(nextSourceNode);
                }
            } else {
                UiConstants.Util.log(IStatus.WARNING,
                                     UiConstants.Util.getString(DiagramUiConstants.Errors.MODEL_GENERATOR_FAILURE));
            }

            if (showLinksInTransformation(diagramModelNode)) {
                if (nextSourceNode != null) {
                    sourceLink = getSourceConnectionModel(nextSourceNode, transformNode);
                    ((DiagramModelNode)sourceLink.getSourceNode()).addSourceConnection(sourceLink);
                    ((DiagramModelNode)sourceLink.getTargetNode()).addTargetConnection(sourceLink);
                    labelNodes = sourceLink.getLabelNodes();
                    if (labelNodes != null && !labelNodes.isEmpty()) {
                        diagramContents.addAll(labelNodes);
                    }
                }
            }

        }

        return diagramContents;
    }

    /**
     * This method interrogates the diagram entities, compares them with the model import's resources and then removes stale
     * entities?
     * 
     * @param diagram
     * @since 4.2
     */
    protected void cleanDiagramEntities( Diagram diagram ) {
        ModelResource mr = ModelUtilities.getModelResourceForModelObject(diagram);
        if (mr != null && !mr.isReadOnly() && ModelUtilities.allDependenciesOpenInWorkspace(mr)) {
            DiagramEntityManager.cleanDiagramEntities(diagram);
        }

    }

    protected NotationModelGenerator getGenerator() {
        if (generator == null) generator = DiagramUiPlugin.getDiagramNotationManager().getDiagramModelGenerator(getNotationId());

        return generator;
    }

    protected List getSourceEObjects( EObject transformationEObject ) {
        List sourceEObjects = TransformationSourceManager.getSourceEObjects(transformationEObject);
        List realEObjects = new ArrayList(sourceEObjects.size());
        Iterator iter = sourceEObjects.iterator();
        EObject pseudoEObj = null;
        EObject realEObj = null;
        while (iter.hasNext()) {
            pseudoEObj = (EObject)iter.next();
            realEObj = ModelObjectUtilities.getRealEObject(pseudoEObj);
            if (realEObj != null) realEObjects.add(realEObj);
        }

        return realEObjects;
    }

    protected List getDiagramEObjects( DiagramModelNode transformationDiagramModelNode ) {
        Iterator iter = transformationDiagramModelNode.getChildren().iterator();
        List diagramEObjects = new ArrayList();

        DiagramModelNode nextDMN = null;
        while (iter.hasNext()) {
            nextDMN = (DiagramModelNode)iter.next();
            if (nextDMN.getModelObject() != null) {
                diagramEObjects.add(nextDMN.getModelObject());
            }
        }
        return diagramEObjects;
    }

    private boolean isValidDiagram( DiagramModelNode diagramModelNode ) {
        boolean result = false;
        Diagram diagram = (Diagram)diagramModelNode.getModelObject();
        if (diagram != null && diagram.getTarget() != null) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(diagram);
            if (mr != null) {
                String type = diagram.getType();
                if (type != null) {
                    if (type.equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID)
                        || type.equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID)) result = true;
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

        if (currentDiagramOK && isValidDiagram(diagramModelNode) && sourceIsNotThis(notification)) {
            TransformationDiagramNotificationHelper notificationHelper = new TransformationDiagramNotificationHelper(
                                                                                                                     notification,
                                                                                                                     diagramModelNode);

            if (notificationHelper.shouldHandleNotification()) {
                // -------------------------------------------------
                // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
                // will result in only one transaction?
                // -------------------------------------------------
                boolean requiredStart = ModelerCore.startTxn(false, false, "Update Transforamation Diagram", this); //$NON-NLS-1$$
                boolean succeeded = false;

                boolean handleConstruction = !DiagramEditorUtil.isDiagramUnderConstruction(diagram);

                try {
                    if (handleConstruction) {
                        DiagramEditorUtil.setDiagramUnderConstruction(diagram);
                    }

                    handleNotification(notificationHelper);

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
                    if (handleConstruction) {
                        DiagramEditorUtil.setDiagramConstructionComplete(diagram, true);
                    }
                    diagramModelNode.update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
                }
            }
        }

        return currentDiagramOK;
    }

    private boolean sourceIsNotThis( Notification notification ) {
        if (notification instanceof SourcedNotification) {

            Object source = ((SourcedNotification)notification).getSource();

            if (source == null) return true;

            return !(source.equals(this));
        }

        return true;
    }

    protected TransformationLink getTargetConnectionModel( DiagramModelNode transformationNode,
                                                           DiagramModelNode targetClassifierNode ) {
        TransformationLink association = new TransformationLink(transformationNode, targetClassifierNode);
        return association;
    }

    protected TransformationLink getSourceConnectionModel( DiagramModelNode sourceClassifierNode,
                                                           DiagramModelNode transformationNode ) {
        TransformationLink association = new TransformationLink(sourceClassifierNode, transformationNode);
        return association;
    }

    public EObject getTransformationObject( EObject targetVirtualGroupEObject ) {

        return TransformationHelper.getTransformationMappingRoot(targetVirtualGroupEObject);

    }

    private void handleNotification( TransformationDiagramNotificationHelper notificationHelper ) {
        boolean associationsChanged = false;
        boolean updateLayout = false;

        if (notificationHelper.shouldReconcileSources()) {
            reconcileSourceTables(notificationHelper.getDiagramNode());
            associationsChanged = true;
        }

        Iterator iter = notificationHelper.getReconcileTableNodes().iterator();
        DiagramModelNode diagramNode = null;
        while (iter.hasNext()) {
            diagramNode = (DiagramModelNode)iter.next();
            if (diagramNode instanceof UmlClassifierNode) {
                ((UmlClassifierNode)diagramNode).reconcile();
                diagramNode.update(DiagramUiConstants.DiagramNodeProperties.CONNECTION);
                updateLayout = true;
            }
            if (isNestedClassifier(diagramNode)) {
                DiagramModelNode parentClassifierNode = DiagramUiUtilities.getClassifierParentNode(diagramNode);
                parentClassifierNode.updateForChild(false);
                updateLayout = true;
            } else {
                diagramNode.updateForChild(false);
                updateLayout = true;
            }

        }

        iter = notificationHelper.getChangedNodes().iterator();
        while (iter.hasNext()) {
            diagramNode = (DiagramModelNode)iter.next();

            getGenerator().performUpdate(diagramNode, null);

            // Check to see if eObject is of type UmlAssociation object
            if (!associationsChanged && getGenerator().isAssociation(diagramNode.getModelObject())) {
                associationsChanged = true;
            } else {
                // Check for SUID updates here.
                // Automatically update SUID on TargetLink by calling targetDiagramNode.
                diagramNode.updateAssociations();
                // diagramNode.update(DiagramUiConstants.DiagramNodeProperties.NAME);
                // associationsChanged = true;
                updateLayout = true;
            }
        }

        iter = notificationHelper.getMovedNodes().iterator();
        while (iter.hasNext()) {
            diagramNode = (DiagramModelNode)iter.next();
            diagramNode.updateForChild(true);
        }
        if (associationsChanged) {
            updateLayout = true;
            updateAssociations(notificationHelper.getDiagramNode());
            DiagramModelNode tNode = getTransformationNode(notificationHelper.getDiagramNode());
            // Need to make the tNode update all alias labels.
            if (tNode != null) tNode.update(DiagramUiConstants.DiagramNodeProperties.NAME);
        }

        if (updateLayout) {
            notificationHelper.getDiagramNode().update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
        }
        DiagramEntityManager.cleanUpDiagram((Diagram)notificationHelper.getDiagramNode().getModelObject());
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

    // private void handleSingleNotification(Notification notification, DiagramModelNode transformationDiagramModelNode) {
    // if( UiConstants.Util.isDebugEnabled(DebugConstants.NOTIFICATIONS)) {
    // UiConstants.Util.print(DebugConstants.NOTIFICATIONS, THIS_CLASS
    //                    + ".handleNotification(): NOTIFICATION = " //$NON-NLS-1$
    // + NotificationUtilities.paramString(notification));
    // }
    //        
    // if( NotificationUtilities.isAdded(notification) ) {
    // performAdd(notification, transformationDiagramModelNode);
    // } else if( NotificationUtilities.isRemoved(notification) ) {
    // performRemove(notification, transformationDiagramModelNode);
    // } else if( NotificationUtilities.isChanged(notification) ) {
    // performChange(notification, transformationDiagramModelNode);
    // }
    // }

    protected void performAdd( Notification notification,
                               DiagramModelNode transformationDiagramModelNode ) {
        EObject targetObject = getEObjectTarget(notification);

        // we know that the object is not a child of a model resource !!!!!

        // Now we check to see if the target object is already in diagram

        DiagramModelNode targetNode = getPrimaryDiagramModelNode(transformationDiagramModelNode, targetObject);

        if (canAddOrRemove(targetNode)) {
            if (!isNestedClassifier(targetNode)) {
                // We have a match, get the added children and hand them off to the generator to construct
                // and add to this the corresponding targetNode
                EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                for (int iChild = 0; iChild < newChildren.length; iChild++) {
                    DiagramModelNode dNode = getGenerator().createChildModel(targetNode, newChildren[iChild]);
                    dNode.update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
                    dNode.update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
                }
            } else {
                // Check for nested here
                targetNode = getModelNode(transformationDiagramModelNode, targetObject);
                if (targetNode != null && isNestedClassifier(targetNode)) {
                    // We have a match, get the added children and hand them off to the generator to construct
                    // and add to this the corresponding targetNode
                    EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                    for (int iChild = 0; iChild < newChildren.length; iChild++) {
                        getGenerator().createChildModel(targetNode, newChildren[iChild]);
                    }
                    targetNode.getParent().getParent().updateForChild(false);
                }
            }
        }
    }

    private boolean canAddOrRemove( DiagramModelNode targetNode ) {
        boolean canDo = false;

        if (targetNode != null) {
            if (targetNode instanceof UmlClassifierNode) {
                canDo = true;
            } else if (targetNode instanceof DrawingModelNode) {
                canDo = true;
            }
        }

        return canDo;
    }

    private boolean isNestedClassifier( DiagramModelNode targetNode ) {
        if (targetNode.getParent() instanceof UmlClassifierContainerNode) return true;

        return false;
    }

    protected void performRemove( Notification notification,
                                  DiagramModelNode transformationDiagramModelNode ) {
        EObject targetObject = getEObjectTarget(notification);

        // we know that the object is not a child of a model resource !!!!!

        // ReconcileSource method should take care of adding/removing tables
        // All we need to do here is check to see if the target object for a remove is on the diagram
        // else we ignore.
        DiagramModelNode parentNode = getPrimaryDiagramModelNode(transformationDiagramModelNode, targetObject);

        if (parentNode != null && canAddOrRemove(parentNode)) {
            DiagramModelNode removedNode = null;

            EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
            for (int iChild = 0; iChild < removedChildren.length; iChild++) {
                removedNode = getModelNode(transformationDiagramModelNode, removedChildren[iChild]);
                if (removedNode != null) parentNode.removeChild(removedNode, false);
            }
            if (isNestedClassifier(parentNode)) parentNode.getParent().getParent().updateForChild(false);
            parentNode.update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
            parentNode.update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
        }

    }

    protected void performChange( Notification notification,
                                  DiagramModelNode transformationDiagramModelNode ) {
        EObject targetObject = getEObjectTarget(notification);

        DiagramModelNode targetNode = getModelNode(transformationDiagramModelNode, targetObject);
        if (targetNode != null) {
            if (notification.getEventType() == Notification.MOVE) {
                targetNode.updateForChild(true);
            } else {

                // If object's parent is in our current model
                // Assume this is a rename for now.
                getGenerator().performUpdate(targetNode, notification);

                // Check to see if eObject is of type UmlAssociation object
                if (getGenerator().isAssociation(targetObject)) {
                    updateAssociations(transformationDiagramModelNode);
                } else {
                    // Check for SUID updates here.
                    // Automatically update SUID on TargetLink by calling targetDiagramNode.
                    targetNode.updateAssociations();
                }

                // addLockedImages(transformationDiagramModelNode);
            }
        }

    }

    /**
     * Method for determining if all removed objects are Sql Objects. In other words, SqlTable, or SqlColumns
     * 
     * @param notification
     * @return
     */
    protected boolean isRemovingSqlObjects( Notification notification ) {
        boolean result = true;
        EObject[] removedEObjects = NotificationUtilities.getRemovedChildren(notification);
        for (int i = 0; i < removedEObjects.length; i++) {
            if (!isSqlTableOrColumn(removedEObjects[i])) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean isSqlTableOrColumn( EObject eObj ) {
        boolean result = false;
        if (TransformationHelper.isSqlTable(eObj) || TransformationHelper.isSqlColumn(eObj)) {
            result = true;
        }
        return result;
    }

    protected void clearAllSourceNodes( DiagramModelNode transformationDiagramModelNode ) {
        EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram((Diagram)transformationDiagramModelNode.getModelObject());

        Iterator iter = getCurrentSourceNodes(transformationDiagramModelNode).iterator();
        DiagramModelNode nextSourceNode = null;
        while (iter.hasNext()) {
            nextSourceNode = (DiagramModelNode)iter.next();
            removeSourceTable(transformationEObject, transformationDiagramModelNode, nextSourceNode.getModelObject());
        }
    }

    protected boolean associationExists( DiagramModelNode diagramModelNode,
                                         TransformationLink targetAssociation ) {
        if (showLinksInTransformation(diagramModelNode)) {
            // get all connections from model
            List currentChildren = diagramModelNode.getChildren();
            Iterator iter = currentChildren.iterator();
            while (iter.hasNext()) {
                DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
                List sourceConnections = childModelNode.getSourceConnections();
                // Walk through the source connections and check if the same info.
                TransformationLink nextAssociation = null;
                Iterator sIter = sourceConnections.iterator();
                while (sIter.hasNext()) {
                    nextAssociation = (TransformationLink)sIter.next();
                    if (associationsMatch(targetAssociation, nextAssociation)) return true;
                }
                // Walk through the target connections and check if the same info.
                List targetConnections = childModelNode.getTargetConnections();
                sIter = targetConnections.iterator();
                while (sIter.hasNext()) {
                    nextAssociation = (TransformationLink)sIter.next();
                    if (associationsMatch(targetAssociation, nextAssociation)) return true;
                }
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
        List staleAssociations = new ArrayList();

        if (showLinksInTransformation(diagramModelNode)) {
            // get all connections from model
            // walk through expected associations.
            Iterator iter = getCurrentAssociations(diagramModelNode).iterator();
            Iterator expectedIter = null;
            TransformationLink nextCurrentAssociation = null;
            TransformationLink nextExpectedAssociation = null;
            boolean foundMatch = false;
            while (iter.hasNext()) {
                foundMatch = false;
                nextCurrentAssociation = (TransformationLink)iter.next();
                expectedIter = expectedAssociations.iterator();
                while (expectedIter.hasNext() && !foundMatch) {
                    nextExpectedAssociation = (TransformationLink)expectedIter.next();
                    if (associationsMatch(nextExpectedAssociation, nextCurrentAssociation)) {
                        foundMatch = true;
                    }
                }

                if (!foundMatch) {
                    staleAssociations.add(nextCurrentAssociation);
                }
            }
        }

        return staleAssociations;
    }

    protected List getCurrentAssociations( DiagramModelNode diagramModelNode ) {
        List currentAssociations = new ArrayList();

        if (showLinksInTransformation(diagramModelNode)) {
            Iterator iter = diagramModelNode.getChildren().iterator();
            while (iter.hasNext()) {
                DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
                List sourceConnections = childModelNode.getSourceConnections();
                // Walk through the source connections and check if the same info.
                TransformationLink nextAssociation = null;
                Iterator sIter = sourceConnections.iterator();
                while (sIter.hasNext()) {
                    nextAssociation = (TransformationLink)sIter.next();
                    if (!currentAssociations.contains(nextAssociation)) currentAssociations.add(nextAssociation);
                }

                // Walk through the target connections and check if the same info.
                List targetConnections = childModelNode.getTargetConnections();
                sIter = targetConnections.iterator();
                while (sIter.hasNext()) {
                    nextAssociation = (TransformationLink)sIter.next();
                    if (!currentAssociations.contains(nextAssociation)) currentAssociations.add(nextAssociation);
                }
            }
        }

        return currentAssociations;
    }

    protected DiagramModelNode getTargetNode( DiagramModelNode diagramModelNode ) {
        DiagramModelNode root = null;
        DiagramModelNode transformationModelNode = getTransformationNode(diagramModelNode);
        // Get it's target
        Diagram diagram = transformationModelNode.getDiagram();
        if (diagram != null) {
            EObject targetObject = diagram.getTarget();
            if (targetObject != null) {
                root = DiagramUiUtilities.getDiagramModelNode(targetObject, transformationModelNode.getParent());
            }
        }
        return root;
    }

    protected List getCurrentSourceNodes( DiagramModelNode diagramModelNode ) {
        List currentSourceNodes = new ArrayList();

        DiagramModelNode rootNode = getTargetNode(diagramModelNode);

        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while (iter.hasNext()) {
            nextNode = (DiagramModelNode)iter.next();
            if ((nextNode instanceof UmlClassifierNode || nextNode instanceof UmlPackageNode) && nextNode != rootNode) currentSourceNodes.add(nextNode);
        }

        return currentSourceNodes;
    }

    protected List getVisibleSourceEObjects( DiagramModelNode tranformationDiagramModelNode ) {
        List visibleSourceEObjects = new ArrayList();
        List visibleSourceNodes = getCurrentSourceNodes(tranformationDiagramModelNode);
        Iterator iter = visibleSourceNodes.iterator();
        DiagramModelNode nextSourceNode = null;
        while (iter.hasNext()) {
            nextSourceNode = (DiagramModelNode)iter.next();
            if (nextSourceNode != null) visibleSourceEObjects.add(nextSourceNode.getModelObject());
        }

        return visibleSourceEObjects;
    }

    protected List cleanUpStaleAssociations( List staleAssociations,
                                             DiagramModelNode diagramModelNode ) {
        List updatedNodes = new ArrayList();

        if (showLinksInTransformation(diagramModelNode)) {
            Iterator iter = staleAssociations.iterator();
            TransformationLink nextAssociation = null;

            while (iter.hasNext()) {
                nextAssociation = (TransformationLink)iter.next();
                ((DiagramModelNode)nextAssociation.getSourceNode()).removeSourceConnection(nextAssociation);
                ((DiagramModelNode)nextAssociation.getTargetNode()).removeTargetConnection(nextAssociation);

                if (!updatedNodes.contains(nextAssociation.getSourceNode())) updatedNodes.add(nextAssociation.getSourceNode());
                if (!updatedNodes.contains(nextAssociation.getTargetNode())) updatedNodes.add(nextAssociation.getTargetNode());

                removeLinkLabels(diagramModelNode, nextAssociation);
            }
        }

        return updatedNodes;
    }

    /**
     * This method updated for the sake of defect 16803 to be smart enough to gather information for dependencies if this is a
     * dependency diagram. This method helps add/remove column events to come through without disrupting the dep. diagram.
     */
    protected List getRealAssociations( final boolean isDependency,
                                        EObject transformationEObject,
                                        DiagramModelNode transformationDiagramModelNode ) {
        List realAssociations = new ArrayList();

        if (showLinksInTransformation(transformationDiagramModelNode)) {
            EObject targetEObject = ((Diagram)transformationDiagramModelNode.getModelObject()).getTarget();

            Iterator sourceIter = getSourceEObjects(transformationEObject).iterator();
            DiagramModelNode transformationNode = getTransformationNode(transformationDiagramModelNode);

            while (sourceIter.hasNext()) {
                EObject nextSourceEObject = (EObject)sourceIter.next();
                DiagramModelNode nextSourceNode = DiagramUiUtilities.getDiagramModelNode(nextSourceEObject,
                                                                                         transformationDiagramModelNode);
                if (nextSourceNode != null) {
                    realAssociations.add(new TransformationLink(nextSourceNode, transformationNode));
                    // defect 16803 - check for dependencies and keep them in the diagram
                    if (isDependency && ModelObjectUtilities.isVirtual(nextSourceEObject)) {
                        // gather up information for the dependency object:
                        EObject myTransformationEObject = getTransformationObject(nextSourceEObject);
                        DiagramModelNode myTransformationNode = getNodeInDiagram(transformationDiagramModelNode,
                                                                                 myTransformationEObject);
                        if (myTransformationNode != null) {
                            List depAssocs = getRealDependencyAssociations(myTransformationEObject,
                                                                           transformationDiagramModelNode,
                                                                           nextSourceEObject,
                                                                           myTransformationNode);
                            realAssociations.addAll(depAssocs);
                        } // endif -- dep transform not null
                    } // endif -- is dependency and virtual
                } // endif -- nSN not null
            } // endwhile -- primary sources

            // Add the target one
            DiagramModelNode targetNode = DiagramUiUtilities.getDiagramModelNode(targetEObject, transformationDiagramModelNode);
            if (targetNode != null) {
                realAssociations.add(new TransformationLink(transformationNode, targetNode));
            }
        }

        return realAssociations;
    }

    /**
     * This method will recurse dependencies as needed.
     * 
     * @param transformationEObject
     * @param rootDiagramModelNode
     * @param targetEObject
     * @param transformationNode
     * @return
     */
    protected List getRealDependencyAssociations( EObject transformationEObject,
                                                  DiagramModelNode rootDiagramModelNode,
                                                  EObject targetEObject,
                                                  DiagramModelNode transformationNode ) {
        List realAssociations = new ArrayList();

        if (showLinksInTransformation(rootDiagramModelNode)) {
            Iterator sourceIter = getSourceEObjects(transformationEObject).iterator();

            while (sourceIter.hasNext()) {
                EObject nextSourceEObject = (EObject)sourceIter.next();
                DiagramModelNode nextSourceNode = DiagramUiUtilities.getDiagramModelNode(nextSourceEObject, rootDiagramModelNode);
                if (nextSourceNode != null) {
                    realAssociations.add(new TransformationLink(nextSourceNode, transformationNode));
                    // check for further dependencies:
                    if (ModelObjectUtilities.isVirtual(nextSourceEObject)
                        && TransformationUmlEObjectHelper.getEObjectType(nextSourceEObject) == RelationalUmlEObjectHelper.UML_CLASSIFIER) {
                        EObject myTransformationEObject = getTransformationObject(nextSourceEObject);
                        DiagramModelNode myTransformationNode = getNodeInDiagram(rootDiagramModelNode, myTransformationEObject);
                        if (myTransformationNode != null) {
                            List depAssocs = getRealDependencyAssociations(myTransformationEObject,
                                                                           rootDiagramModelNode,
                                                                           nextSourceEObject,
                                                                           myTransformationNode);
                            realAssociations.addAll(depAssocs);
                        } // endif -- dep transf not null
                    } // endif
                }
            }

            // Add the target one
            DiagramModelNode targetNode = DiagramUiUtilities.getDiagramModelNode(targetEObject, rootDiagramModelNode);
            if (targetNode != null) {
                realAssociations.add(new TransformationLink(transformationNode, targetNode));
            }
        }

        return realAssociations;
    }

    protected void updateAssociations( DiagramModelNode transformationDiagramModelNode ) {
        // get transformation object for diagram

        if (showLinksInTransformation(transformationDiagramModelNode)) {
            Diagram diagram = (Diagram)transformationDiagramModelNode.getModelObject();

            EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram(diagram);

            if (transformationEObject == null) return;

            // defect 16803 - be sure to check dependencies, too.
            boolean isDep = PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID.equals(diagram.getType());
            List realAssociations = getRealAssociations(isDep, transformationEObject, transformationDiagramModelNode);

            List staleAssociations = getStaleAssociations(realAssociations, transformationDiagramModelNode);

            List updatedNodes = new ArrayList(cleanUpStaleAssociations(staleAssociations, transformationDiagramModelNode));

            Iterator iter = realAssociations.iterator();

            while (iter.hasNext()) {
                TransformationLink nextAssociation = (TransformationLink)iter.next();
                if (!associationExists(transformationDiagramModelNode, nextAssociation)) {
                    ((DiagramModelNode)nextAssociation.getSourceNode()).addSourceConnection(nextAssociation);
                    ((DiagramModelNode)nextAssociation.getTargetNode()).addTargetConnection(nextAssociation);

                    // Keep a list of new end nodes so we can tell them to fire
                    // an updateAssociations() call...
                    if (!updatedNodes.contains(nextAssociation.getSourceNode())) updatedNodes.add(nextAssociation.getSourceNode());
                    if (!updatedNodes.contains(nextAssociation.getTargetNode())) updatedNodes.add(nextAssociation.getTargetNode());

                    List labelNodes = nextAssociation.getLabelNodes();
                    if (labelNodes != null && !labelNodes.isEmpty()) {
                        transformationDiagramModelNode.addChildren(labelNodes);
                    }
                }
            }

            if (!updatedNodes.isEmpty()) {
                iter = updatedNodes.iterator();
                DiagramModelNode nextNode = null;
                while (iter.hasNext()) {
                    nextNode = (DiagramModelNode)iter.next();
                    nextNode.updateAssociations();
                }
            }
        }
    }

    public DiagramModelNode getTransformationNode( DiagramModelNode transformationDiagramModelNode ) {
        // walk children and look for TransformationNode type
        Iterator iter = transformationDiagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while (iter.hasNext()) {
            nextNode = (DiagramModelNode)iter.next();
            if (nextNode instanceof TransformationNode) return nextNode;
        }

        return null;
    }

    protected void reconcileSourceTables( DiagramModelNode transformationDiagramModelNode ) {
        boolean childrenChanged = false;

        List removeSourceList = new ArrayList();
        Diagram diagram = (Diagram)transformationDiagramModelNode.getModelObject();
        EObject virtualGroupEObject = diagram.getTarget();
        EObject transformationEObject = getTransformationObject(virtualGroupEObject);

        // Make sure tNode has a model resource to be safe.

        ModelResource txResource = ModelUtilities.getModelResourceForModelObject(transformationEObject);

        if (txResource != null) {
            List currentSourceNodes = getCurrentSourceNodes(transformationDiagramModelNode);
            List currentSourceTableEObjects = new ArrayList(getSourceEObjects(transformationEObject));

            if (currentSourceTableEObjects.isEmpty()) {
                clearAllSourceNodes(transformationDiagramModelNode);
                childrenChanged = true;
            } else {
                DiagramModelNode nextNode = null;
                Iterator iter = currentSourceNodes.iterator();
                while (iter.hasNext()) {
                    nextNode = (DiagramModelNode)iter.next();
                    if (!currentSourceTableEObjects.contains(nextNode.getModelObject())) {
                        removeSourceList.add(nextNode.getModelObject());
                        childrenChanged = true;
                    }
                }
                iter = removeSourceList.iterator();
                EObject nextSourceEObject = null;
                while (iter.hasNext()) {
                    nextSourceEObject = (EObject)iter.next();
                    if (!TransformationHelper.isSqlInputSet(nextSourceEObject)) removeSourceTable(transformationEObject,
                                                                                                  transformationDiagramModelNode,
                                                                                                  nextSourceEObject);
                }

                // Clean up stale diagram entities (i.e. modelObject = null)
                DiagramEntityManager.cleanUpDiagram(diagram);

                List visibleSourceTableEObjects = new ArrayList(getVisibleSourceEObjects(transformationDiagramModelNode));
                iter = currentSourceTableEObjects.iterator();
                while (iter.hasNext()) {
                    Object nextObj = iter.next();
                    if (!visibleSourceTableEObjects.contains(nextObj)) {
                        addSourceTable(transformationEObject, transformationDiagramModelNode, (EObject)nextObj);
                        childrenChanged = true;
                    }
                }

                getTransformationNode(transformationDiagramModelNode).update(DiagramUiConstants.DiagramNodeProperties.SUBSCRIPT);

                // Removed this line. We don't think the locked image is clear on it's intent right now, so we won't
                // call this TBD todo.
                addLockedImages(transformationDiagramModelNode);
            }

            DiagramEntityManager.cleanDiagramEntities(diagram);
            if (childrenChanged) {
                transformationDiagramModelNode.update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
            }
        }
    }

    public void addSourceTable( EObject transformationEObject,
                                DiagramModelNode transformationDiagramModelNode,
                                EObject sourceEObject ) {
        // from transformation

        DiagramModelNode newSourceNode = null;
        DiagramModelNode transformNode = getTransformationNode(transformationDiagramModelNode);

        if (getGenerator() != null && transformNode != null) {
            newSourceNode = getGenerator().createModel(sourceEObject, (Diagram)transformationDiagramModelNode.getModelObject());
            if (newSourceNode != null) {
                newSourceNode.setParent(transformationDiagramModelNode);
                transformationDiagramModelNode.addChild(newSourceNode);
            }
        } else {
            UiConstants.Util.log(IStatus.WARNING, UiConstants.Util.getString(DiagramUiConstants.Errors.MODEL_GENERATOR_FAILURE));
        }

        if (newSourceNode != null) {
            setLockedImage(newSourceNode);

            if (showLinksInTransformation(transformationDiagramModelNode)) {
                TransformationLink sourceLink = getSourceConnectionModel(newSourceNode, transformNode);
                ((DiagramModelNode)sourceLink.getSourceNode()).addSourceConnection(sourceLink);
                ((DiagramModelNode)sourceLink.getTargetNode()).addTargetConnection(sourceLink);

                List labelNodes = sourceLink.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    transformationDiagramModelNode.addChildren(labelNodes);
                }

                newSourceNode.updateAssociations();
                transformNode.updateAssociations();
            }
        }
    }

    public void removeSourceTable( EObject transformationEObject,
                                   DiagramModelNode transformationDiagramModelNode,
                                   EObject sourceEObject ) {

        // /Let's get the diagramModelNode from diagram for the removed sourceEObject

        DiagramModelNode removedNode = DiagramUiUtilities.getDiagramModelNode(sourceEObject, transformationDiagramModelNode);
        if (removedNode != null) {
            removeConnectionsForNode(transformationDiagramModelNode, removedNode);
            transformationDiagramModelNode.removeChild(removedNode, true);
            DiagramEntity de = removedNode.getDiagramModelObject();
            if (de != null) {
                DiagramEntityAdapter.setModelObject(de, null);
            }
        }
    }

    protected void removeLinkLabels( DiagramModelNode transformationDiagramModelNode,
                                     TransformationLink theLink ) {
        List labelNodes = theLink.getLabelNodes();
        theLink.clearLabelNodes();
        if (labelNodes != null && !labelNodes.isEmpty()) {
            transformationDiagramModelNode.removeChildren(labelNodes, false);
        }

    }

    protected void removeConnectionsForNode( DiagramModelNode transformationDiagramModelNode,
                                             DiagramModelNode removedNode ) {
        // We need transformation model node object
        DiagramModelNode transformNode = getTransformationNode(transformationDiagramModelNode);

        TransformationLink tempAssociation = new TransformationLink(removedNode, transformNode);

        // Walk the diagram's children, get all source and target connections and remove any that match!

        List sourceConnections = removedNode.getSourceConnections();
        // Walk through the source connections and check if the same info.
        TransformationLink nextAssociation = null;
        Iterator sIter = sourceConnections.iterator();
        while (sIter.hasNext()) {
            nextAssociation = (TransformationLink)sIter.next();
            if (associationsMatch(nextAssociation, tempAssociation)) {
                sIter.remove();
                removeLinkLabels(transformationDiagramModelNode, nextAssociation);
            }
        }

        // Walk through the target connections and check if the same info.
        List targetConnections = removedNode.getTargetConnections();
        sIter = targetConnections.iterator();
        while (sIter.hasNext()) {
            nextAssociation = (TransformationLink)sIter.next();
            if (associationsMatch(nextAssociation, tempAssociation)) {
                sIter.remove();
                removeLinkLabels(transformationDiagramModelNode, nextAssociation);
            }
        }

        // Walk through the target connections if transformation node and check if the same info.
        targetConnections = transformNode.getTargetConnections();
        sIter = targetConnections.iterator();
        while (sIter.hasNext()) {
            nextAssociation = (TransformationLink)sIter.next();
            if (associationsMatch(nextAssociation, tempAssociation)) {
                sIter.remove();
                removeLinkLabels(transformationDiagramModelNode, nextAssociation);
            }
        }

        transformNode.updateAssociations();
        removedNode.updateAssociations();
    }

    @Override
    public String toString() {
        return "TransformationDiagamModelFactory()"; //$NON-NLS-1$
    }

    private void addDependencies( DiagramModelNode diagramModelNode,
                                  Diagram baseTransformationDiagram ) {
        // Get base virtual group
        EObject virtualGroupEObject = baseTransformationDiagram.getTarget();
        // get transformation object
        EObject transformationEObject = getTransformationObject(virtualGroupEObject);
        // get sources
        Iterator sourceIter = getSourceEObjects(transformationEObject).iterator();
        EObject nextSourceEObject = null;
        // walk through sources and add dependencies if "virtual"
        while (sourceIter.hasNext()) {
            nextSourceEObject = (EObject)sourceIter.next();
            // defect 16803 - don't go into non-classifiers (like XmlDocument)
            if (ModelObjectUtilities.isVirtual(nextSourceEObject)
                && TransformationUmlEObjectHelper.getEObjectType(nextSourceEObject) == RelationalUmlEObjectHelper.UML_CLASSIFIER) {
                addDependencies(diagramModelNode, baseTransformationDiagram, nextSourceEObject);
            } // endif
        }

    }

    private void addDependencies( DiagramModelNode diagramModelNode,
                                  Diagram baseTransformationDiagram,
                                  EObject virtualSource ) {
        List diagramContents = new ArrayList();
        List virtualSources = new ArrayList();

        DiagramModelNode targetModelNode = null;

        EObject transformationEObject = null;
        // Create Tranform Node here and add
        DiagramModelNode transformNode = null;

        // // Check to see if diagram already has this group or not, if so, then don't add another one.
        // if( !DiagramUiUtilities.diagramContainsEObject(virtualSource, diagramModelNode)) {
        targetModelNode = getModelNode(diagramModelNode, virtualSource);
        transformationEObject = getTransformationObject(virtualSource);
        // Add Transform Node to diagram model
        transformNode = new TransformationNode(baseTransformationDiagram, transformationEObject);
        diagramContents.add(transformNode);
        // }

        if (targetModelNode != null) {
            TransformationLink targetLink = getTargetConnectionModel(transformNode, targetModelNode);
            ((DiagramModelNode)targetLink.getSourceNode()).addSourceConnection(targetLink);
            ((DiagramModelNode)targetLink.getTargetNode()).addTargetConnection(targetLink);

            List labelNodes = targetLink.getLabelNodes();
            if (labelNodes != null && !labelNodes.isEmpty()) {
                diagramContents.addAll(labelNodes);
            }

            // Now we need to get a list of "Sources"

            // Walk the list and add their links
            Iterator sourceIter = getSourceEObjects(transformationEObject).iterator();
            EObject nextSourceEObject = null;
            DiagramModelNode nextSourceNode = null;
            TransformationLink sourceLink = null;

            while (sourceIter.hasNext()) {
                nextSourceEObject = (EObject)sourceIter.next();
                if (getGenerator() != null) {
                    // Check to see if diagram already has this group or not, if so, then don't add another one.
                    nextSourceNode = DiagramUiUtilities.getDiagramModelNode(nextSourceEObject, diagramModelNode);
                    if (nextSourceNode == null) {
                        nextSourceNode = getGenerator().createModel(nextSourceEObject, baseTransformationDiagram);
                        // We just created a new node, hopefully
                        if (nextSourceNode != null) {
                            diagramContents.add(nextSourceNode);
                            // defect 16803 - don't go into non-classifiers (like XmlDocument)
                            if (ModelObjectUtilities.isVirtual(nextSourceEObject)
                                && TransformationUmlEObjectHelper.getEObjectType(nextSourceEObject) == RelationalUmlEObjectHelper.UML_CLASSIFIER) {
                                virtualSources.add(nextSourceEObject);
                            } // endif
                        }
                    }
                } else {
                    UiConstants.Util.log(IStatus.WARNING,
                                         UiConstants.Util.getString(DiagramUiConstants.Errors.MODEL_GENERATOR_FAILURE));
                }

                if (nextSourceNode != null) {
                    sourceLink = getSourceConnectionModel(nextSourceNode, transformNode);
                    ((DiagramModelNode)sourceLink.getSourceNode()).addSourceConnection(sourceLink);
                    ((DiagramModelNode)sourceLink.getTargetNode()).addTargetConnection(sourceLink);

                    labelNodes = sourceLink.getLabelNodes();
                    if (labelNodes != null && !labelNodes.isEmpty()) {
                        diagramContents.addAll(labelNodes);
                    }
                }

            }
        }

        if (!diagramContents.isEmpty()) diagramModelNode.addChildren(diagramContents);

        if (!virtualSources.isEmpty()) {
            Iterator vIter = virtualSources.iterator();
            while (vIter.hasNext()) {
                addDependencies(diagramModelNode, baseTransformationDiagram, (EObject)vIter.next());
            }

        }
    }

    protected boolean isSecondaryMappingGroup( EObject eObject ) {
        if (eObject instanceof InputSet || eObject instanceof StagingTable) return true;

        return false;
    }

    protected void addLockedImages( DiagramModelNode transformationDiagramNode ) {
        // Walk the children of the diagram
        // when we find a UmlClassifier, check for isVirtual() and use
        // TransformationHelper to ask for the locked value;
        List allDiagramNodes = transformationDiagramNode.getChildren();
        if (allDiagramNodes != null && !allDiagramNodes.isEmpty()) {
            Image image = UiPlugin.getDefault().getImage(UiConstants.Images.LOCK_VIRTUAL_GROUP);

            Iterator iter = allDiagramNodes.iterator();
            DiagramModelNode diagramNode = null;
            EObject classifierEObject = null;

            while (iter.hasNext()) {
                diagramNode = (DiagramModelNode)iter.next();
                if (diagramNode instanceof UmlClassifierNode) {
                    classifierEObject = diagramNode.getModelObject();
                    if (ModelUtilities.getModelResourceForModelObject(classifierEObject) != null) {
                        if (!isSecondaryMappingGroup(classifierEObject)
                            && TransformationHelper.isTargetGroupLocked(classifierEObject)) {
                            if (image != null) {
                                diagramNode.setSecondOverlayImage(image, DiagramUiConstants.Position.UPPER_RIGHT);
                            }
                        } else {
                            diagramNode.setSecondOverlayImage(null, DiagramUiConstants.Position.UPPER_RIGHT);
                        }

                        diagramNode.update(DiagramUiConstants.DiagramNodeProperties.IMAGES);
                    }
                }
            }
        }
    }

    public void setLockedImage( DiagramModelNode diagramModelNode ) {
        // when we find a UmlClassifier, check for isVirtual() and use
        // TransformationHelper to ask for the locked value;

        if (diagramModelNode instanceof UmlClassifierNode) {
            EObject classifierEObject = diagramModelNode.getModelObject();
            if (ModelUtilities.getModelResourceForModelObject(classifierEObject) != null) {
                if (!isSecondaryMappingGroup(classifierEObject) && TransformationHelper.isTargetGroupLocked(classifierEObject)) {
                    Image image = UiPlugin.getDefault().getImage(UiConstants.Images.LOCK_VIRTUAL_GROUP);
                    if (image != null) diagramModelNode.setSecondOverlayImage(image, DiagramUiConstants.Position.UPPER_RIGHT);
                } else {
                    diagramModelNode.setSecondOverlayImage(null, DiagramUiConstants.Position.UPPER_RIGHT);
                }
            }
        } else {
            diagramModelNode.setSecondOverlayImage(null, DiagramUiConstants.Position.UPPER_RIGHT);
        }
        diagramModelNode.update(DiagramUiConstants.DiagramNodeProperties.IMAGES);
    }

    /* (non-Javadoc) defect 16803 - find out if diagram refresh needed
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelFactoryImpl#shouldRefreshDiagram(org.eclipse.emf.common.notify.Notification, com.metamatrix.modeler.diagram.ui.model.DiagramModelNode, java.lang.String)
     */
    @Override
    public boolean shouldRefreshDiagram( Notification notification,
                                         DiagramModelNode diagramModelNode,
                                         String sDiagramTypeId ) {

        // only check this if dependency diagram:
        if (PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID.equals(sDiagramTypeId)) {
            TransformationDiagramNotificationHelper notificationHelper = new TransformationDiagramNotificationHelper(
                                                                                                                     notification,
                                                                                                                     diagramModelNode);
            return notificationHelper.shouldRefreshDiagram();
        } // endif

        return false;
    }

    private boolean showLinksInTransformation( DiagramModelNode diagramModelNode ) {
        return (isDependencyDiagram(diagramModelNode) || TransformationDiagramUtil.isTreeLayout()) && !(hideLinksAlways);
    }

    /**
     * @param theHideLinksAlways The hideLinksAlways to set.
     * @since 5.0
     */
    protected void setHideLinksAlways( boolean theHideLinksAlways ) {
        this.hideLinksAlways = theHideLinksAlways;
    }
}
