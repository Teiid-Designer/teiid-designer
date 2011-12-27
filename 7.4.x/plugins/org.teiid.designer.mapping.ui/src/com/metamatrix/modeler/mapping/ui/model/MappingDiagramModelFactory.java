/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlAttributeNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierContainerNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.actions.MergeMappingClassesAction;
import com.metamatrix.modeler.mapping.ui.actions.SplitMappingClassAction;
import com.metamatrix.modeler.mapping.ui.connection.EnumeratedTypeLink;
import com.metamatrix.modeler.mapping.ui.connection.MappingLink;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.modeler.mapping.ui.editor.MappingAdapterFilter;
import com.metamatrix.modeler.mapping.ui.editor.MappingExtent;
import com.metamatrix.modeler.mapping.ui.editor.SummaryExtent;
import com.metamatrix.modeler.mapping.ui.util.MappingUiUtil;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSourceManager;
import com.metamatrix.modeler.transformation.ui.model.TransformationDiagramModelFactory;
import com.metamatrix.modeler.transformation.ui.model.TransformationNode;
import com.metamatrix.modeler.xsd.util.ModelerXsdUtils;

/**
 * MappingDiagramModelFactory
 */
public class MappingDiagramModelFactory extends TransformationDiagramModelFactory {

    private static final String KEY_MAPPING_DIAGRAM_NAME = "DiagramNames.mappingDiagram"; //$NON-NLS-1$
    private static final String THIS_CLASS = "MappingDiagramModelFactory"; //$NON-NLS-1$

    // private String errorMessage;

    // private static DiagramFactory diagramFactory;

    static {
        // diagramFactory = DiagramFactory.eINSTANCE;
    }

    /**
     * Construct an instance of TransformModelFactory.
     */
    public MappingDiagramModelFactory() {
        super();
        super.setHideLinksAlways(true);
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

        setNotationId(sNotationId);

        // Return null if the baseObject is not a com.metamatrix.metamodels.diagram.Diagram
        if (!(baseObject instanceof Diagram)) {
            return null;
        }

        Diagram diagram = (Diagram)baseObject;

        DiagramModelNode diagramModelNode = null;

        // Create the DiagramNode
        diagramModelNode = new MappingDiagramNode(diagram, UiConstants.Util.getString(KEY_MAPPING_DIAGRAM_NAME));

        if (isDetailedDiagram(diagramModelNode)) {
            DiagramModelNode transDiagramNode = super.createModel(baseObject, sNotationId, monitor);
            diagramModelNode.addChildren(transDiagramNode.getChildren());
            // We need to get the "Virtual" table and tell it to add Recursion here.

            DiagramModelNode baseVirtualTableNode = getBaseNode(diagramModelNode);
            if (baseVirtualTableNode != null) addRecursionImage(baseVirtualTableNode);

            addInputSet(baseVirtualTableNode, diagramModelNode);
        }
        // if it's not DETAILED, then we wait for the refresh() method from the MappingDiagramController to
        // call the refresh() method in this factory to do all the extent dirty work.

        return diagramModelNode;
    }

    private void addInputSet( DiagramModelNode baseVirtualTableNode,
                              DiagramModelNode diagramNode ) {
        // Let's add the Input Set if it exists...
        DiagramModelNode tNode = super.getTransformationNode(diagramNode);
        MappingClass mappingClass = (MappingClass)baseVirtualTableNode.getModelObject();
        if (tNode != null && mappingClass != null) {
            // 
            EObject inputSet = mappingClass.getInputSet();
            if (inputSet != null && getGenerator() != null) {
                DiagramModelNode inputSetModelNode = getGenerator().createModel(inputSet, (Diagram)diagramNode.getModelObject());
                if (inputSetModelNode != null) {
                    inputSetModelNode.setHideLocation(true);

                    addEditInputSetImage(inputSetModelNode);

                    diagramNode.addChild(inputSetModelNode);
                    // TransformationLink sourceLink = getSourceConnectionModel(inputSetModelNode, tNode);
                    // ((DiagramModelNode)sourceLink.getSourceNode()).addSourceConnection(sourceLink);
                    // ((DiagramModelNode)sourceLink.getTargetNode()).addTargetConnection(sourceLink);
                    //                  
                    // if( UiConstants.Util.isDebugEnabled(DebugConstants.TX_DIAGRAM_MODEL_NODE)) {
                    //                      String message = "Adding Connection \n" + sourceLink.toString(); //$NON-NLS-1$
                    // UiConstants.Util.print(DebugConstants.TX_DIAGRAM_MODEL_NODE, message);
                    // }
                    //                  
                    // inputSetModelNode.updateAssociations();
                    // inputSetModelNode.updateAssociations();
                }
            }
        }
    }

    private MappingLink getMappingLinkConnectionModel( DiagramModelNode sourceMappingClassNode,
                                                       DiagramModelNode targetExtentNode ) {
        MappingLink association = new MappingLink(sourceMappingClassNode, targetExtentNode);
        return association;
    }

    private boolean isDetailedDiagram( DiagramModelNode diagramNode ) {
        if (diagramNode.getModelObject() instanceof Diagram) {
            if (((Diagram)diagramNode.getModelObject()).getType() != null
                && ((Diagram)diagramNode.getModelObject()).getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) return true;
        }

        return false;
    }

    private boolean isDetailedDiagram( Diagram diagram ) {
        if (diagram.getType() != null && diagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) return true;

        return false;
    }

    private DiagramModelNode getBaseNode( DiagramModelNode diagramModelNode ) {
        // find the base virtual group object in this detailed diagram.
        EObject virtualGroupEObject = ((Diagram)diagramModelNode.getModelObject()).getTarget();
        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while (iter.hasNext()) {
            nextNode = (DiagramModelNode)iter.next();
            if (nextNode.getModelObject().equals(virtualGroupEObject)) return nextNode;
        }

        return null;
    }

    private boolean isValidDiagram( DiagramModelNode diagramModelNode ) {
        boolean result = false;
        Diagram diagram = (Diagram)diagramModelNode.getModelObject();
        if (diagram != null && diagram.getTarget() != null) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(diagram);
            if (mr != null) {
                String type = diagram.getType();
                if (type != null) {
                    if (type.equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)
                        || type.equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) result = true;
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

                requiredStart = ModelerCore.startTxn(false, false, "Update Mapping Diagram", this); //$NON-NLS-1$$

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

        boolean isDetailed = isDetailedDiagram(currentDiagram);

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
                            // If detailed, we can cross model resources, so we go ahead here.
                            if (isDetailed) {
                                shouldHandle = true;
                            } else {
                                // If notification is from another "model resource" we don't care for Coarse
                                // Mapping diagram. All objects are in same model.
                                // Check here if the targetObject and document have the same resource, then set to TRUE;
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
                if (targetObject != null && targetObject instanceof EObject
                    && !DiagramUiUtilities.isDiagramObject((EObject)targetObject)) {
                    // If detailed, we can cross model resources, so we go ahead here.
                    if (isDetailed) {
                        shouldHandle = true;
                    } else {
                        // If notification is from another "model resource" we don't care for Coarse
                        // Mapping diagram. All objects are in same model.
                        // Check here if the targetObject and document have the same resource, then set to TRUE;
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

    @Override
    protected boolean currentDiagramRemoved( Diagram diagram ) {
        boolean isRemoved = false;

        if (diagram == null) isRemoved = true;
        else if (diagram.eResource() == null) {
            isRemoved = true;
            if (diagram.getTarget() != null && diagram.getTarget().eResource() != null) isRemoved = false;
            // check the eContainer.
            // if( diagram.eContainer() == null ) {
            // if( diagram.getType() != null && diagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID) )
            // isRemoved = false;
            // else
            // isRemoved = true;
            // }

        }
        return isRemoved;
    }

    private boolean sourceIsNotThis( Notification notification ) {
        if (notification instanceof SourcedNotification) {

            Object source = ((SourcedNotification)notification).getSource();

            if (source == null) return true;

            return !(source.equals(this));
        }

        return true;
    }

    private void handleSingleNotification( Notification notification,
                                           DiagramModelNode transformationDiagramModelNode,
                                           Object source ) {

        if (!isDiagramNotifier(notification)) {
            if (NotificationUtilities.isAdded(notification)) {
                performAdd(notification, transformationDiagramModelNode, source);
            } else if (NotificationUtilities.isRemoved(notification)) {
                performRemove(notification, transformationDiagramModelNode);
            } else if (NotificationUtilities.isChanged(notification)) {
                performChange(notification, transformationDiagramModelNode);
            }
        }
    }

    /**
     * @param notification
     * @param transformationDiagramModelNode
     */
    private void handleNotification( Notification notification,
                                     DiagramModelNode mappingDiagramModelNode ) {
        boolean isTransformation = isDetailedDiagram(mappingDiagramModelNode);

        if (notification instanceof SourcedNotification) {
            Object source = ((SourcedNotification)notification).getSource();
            if (isTransformation) reconcileSourceTables(mappingDiagramModelNode);

            Collection notifications = ((SourcedNotification)notification).getNotifications();
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                handleSingleNotification((Notification)iter.next(), mappingDiagramModelNode, source);
            }
            // let's just update the T Node associations

            if (isTransformation) {
                DiagramModelNode transNode = getTransformationNode(mappingDiagramModelNode);
                if (transNode != null) transNode.updateAssociations();
            } else {
                addLockedImages(mappingDiagramModelNode);
            }

        } else {
            if (isTransformation) reconcileSourceTables(mappingDiagramModelNode);

            handleSingleNotification(notification, mappingDiagramModelNode, null);
            // let's just update the T Node associations

            if (isTransformation) {
                DiagramModelNode transNode = getTransformationNode(mappingDiagramModelNode);
                if (transNode != null) transNode.updateAssociations();
            }
        }

    }

    protected void performAdd( Notification notification,
                               DiagramModelNode mappingDiagramModelNode,
                               Object source ) {
        if (isDetailedDiagram(mappingDiagramModelNode)) super.performAdd(notification, mappingDiagramModelNode);

        EObject targetObject = getEObjectTarget(notification);

        // Need to tell the editor to tell the controller to refresh()!

        // we know that the object is not a child of a model resource !!!!!
        // Now we check to see if the target object is already in diagram
        DiagramModelNode targetNode = getModelNode(mappingDiagramModelNode, targetObject);

        if (targetNode != null && !(targetNode instanceof TransformationNode) && !isDetailedDiagram(mappingDiagramModelNode)) {
            if (isNestedClassifier(targetNode)) {
                // We have a match, get the added children and hand them off to the generator to construct
                // and add to this the corresponding targetNode
                EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                for (int iChild = 0; iChild < newChildren.length; iChild++) {
                    if (isDrawable(newChildren[iChild])) {
                        DiagramModelNode newNode = getGenerator().createChildModel(targetNode, newChildren[iChild]);
                        if (newChildren.length == 1 && newNode instanceof UmlAttributeNode && isValidRenameSource(source)) {
                            // Need to force an Edit in place??
                            // Let's get it's edit part
                            ((UmlAttributeNode)newNode).rename();
                        }
                    }
                }
                targetNode.getParent().getParent().updateForChild(false);
            } else if (!isDetailedDiagram(mappingDiagramModelNode)) {
                EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                for (int iChild = 0; iChild < newChildren.length; iChild++) {
                    if (isDrawable(newChildren[iChild])) {
                        DiagramModelNode newNode = getGenerator().createChildModel(targetNode, newChildren[iChild]);
                        if (newChildren.length == 1 && newNode instanceof UmlAttributeNode && isValidRenameSource(source)) {
                            // Need to force an Edit in place??
                            // Let's get it's edit part
                            targetNode.updateForChild(false);
                            ((UmlAttributeNode)newNode).rename();
                        }
                    }
                }

                MappingDiagramUtil.layoutDiagram(mappingDiagramModelNode);
            }
        } else if (isDetailedDiagram(mappingDiagramModelNode) && targetObject instanceof MappingClassSet) {
            // Defect 22008: New InputSet Parameter Does Not Show In Diagram Until Diagram Is Refreshed
            // If this IS DETAILED, we have to cover the case where a New Input Set Parameter & Binding is created
            // Target Object is the MappingClassSet
            // New Value is an InputBinding
            // Get the Binding, get the InputSetParameter
            // Get it's parent, treat as target, then add a new child for it? (use code from TransformationDiagramModelFactory)
            EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
            if (newChildren.length > 0) {
                for (int i = 0; i < newChildren.length; i++) {
                    if (newChildren[i] instanceof InputBinding) {
                        InputParameter newInputParameter = ((InputBinding)newChildren[i]).getInputParameter();
                        if (newInputParameter != null) {
                            InputSet inputSet = newInputParameter.getInputSet();
                            // InputSet available, get it's diagram node and reconcile!
                            if (inputSet != null) {
                                DiagramModelNode parentNode = getModelNode(mappingDiagramModelNode, inputSet);
                                if (parentNode != null) {
                                    // ((UmlClassifierNode)parentNode).reconcile();
                                    DiagramModelNode dNode = getGenerator().createChildModel(parentNode, newInputParameter);
                                    dNode.update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
                                    dNode.update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
                                }
                            }
                        }
                    }
                }
            }
            MappingDiagramUtil.layoutDiagram(mappingDiagramModelNode);
        }

    }

    private boolean isNestedClassifier( DiagramModelNode targetNode ) {
        if (targetNode.getParent() instanceof UmlClassifierContainerNode) return true;

        return false;
    }

    @Override
    protected void performRemove( Notification notification,
                                  DiagramModelNode mappingDiagramModelNode ) {
        if (isDetailedDiagram(mappingDiagramModelNode) && isRemovingSqlObjects(notification)) super.performRemove(notification,
                                                                                                                  mappingDiagramModelNode);

        EObject targetObject = getEObjectTarget(notification);

        // Need to tell the editor to tell the controller to refresh()!
        if (isValidTarget(targetObject)) {
            // Adding/removing Mapping Classes for Coarse mode, is performed by the refresh() method
            // This really only updates existing mapping classes that have changed children.
            List diagramSourceEObjects = getDiagramEObjects(mappingDiagramModelNode);
            DiagramModelNode parentNode = null;
            DiagramModelNode removedNode = null;
            boolean childrenRemoved = false;
            EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
            for (int iChild = 0; iChild < removedChildren.length; iChild++) {
                if (diagramSourceEObjects.contains(removedChildren[iChild])) {
                    // Do nothing here. Should be taken care of by refresh() method so the contents can be
                    // reconciled with the document model.
                } else {
                    removedNode = getModelNode(mappingDiagramModelNode, removedChildren[iChild]);
                    if (removedNode != null) {
                        parentNode = removedNode.getParent();
                        if (parentNode != null) {
                            parentNode.removeChild(removedNode, false);
                            childrenRemoved = true;
                            DiagramModelNode topClassifierNode = DiagramUiUtilities.getTopClassifierParentNode(parentNode);
                            if (topClassifierNode != null) {
                                topClassifierNode.updateForChild(false);
                                topClassifierNode.update(DiagramUiConstants.DiagramNodeProperties.SIZE);
                                topClassifierNode.update(DiagramUiConstants.DiagramNodeProperties.CHILDREN);
                                topClassifierNode.update(DiagramUiConstants.DiagramNodeProperties.LAYOUT);
                            }
                        }
                    }
                }
            }
            if (childrenRemoved) MappingDiagramUtil.layoutDiagram(mappingDiagramModelNode, childrenRemoved);
            else MappingDiagramUtil.layoutDiagram(mappingDiagramModelNode);
        }

    }

    @Override
    protected void performChange( Notification notification,
                                  DiagramModelNode mappingDiagramModelNode ) {
        if (isDetailedDiagram(mappingDiagramModelNode)) super.performChange(notification, mappingDiagramModelNode);

        EObject targetObject = getEObjectTarget(notification);

        if (isValidTarget(targetObject)) {
            DiagramModelNode targetNode = getModelNode(mappingDiagramModelNode, targetObject);
            if (targetNode != null) {
                // If object's parent is in our current model
                // Assume this is a rename for now.
                getGenerator().performUpdate(targetNode, notification);
                addRecursionImage(targetNode);
                targetNode.update(DiagramUiConstants.DiagramNodeProperties.IMAGES);
            }
        }
    }

    public void updateNodes( Collection inputExtentList, // NO_UCD
                             DiagramModelNode mappingDiagramModelNode ) {
        // Not implemented
    }

    public void removeNodes( Collection inputExtentList, // NO_UCD
                             DiagramModelNode mappingDiagramModelNode ) {
        MappingExtent nextExtent = null;
        List removeList = new ArrayList(1);
        List extentList = new ArrayList(inputExtentList);
        Iterator iter = extentList.iterator();
        while (iter.hasNext()) {
            nextExtent = (MappingExtent)iter.next();
            DiagramModelNode existingMappingNode = getExtentNode(mappingDiagramModelNode,
                                                                 nextExtent.getMappingReference(),
                                                                 nextExtent.getMappingReference());
            if (existingMappingNode != null) removeList.add(existingMappingNode);
        }

        if (!removeList.isEmpty()) mappingDiagramModelNode.removeChildren(removeList, false);
    }

    public void updateMappingLinks( Collection inputExtentList ) { // NO_UCD

    }

    private List getExistingMappingExtentNodes( DiagramModelNode mappingDiagramModelNode ) {
        if (mappingDiagramModelNode == null) return Collections.EMPTY_LIST;

        Iterator iter = mappingDiagramModelNode.getChildren().iterator();
        List existingExtentNodes = new ArrayList(10);
        DiagramModelNode nextDMN = null;
        while (iter.hasNext()) {
            nextDMN = (DiagramModelNode)iter.next();
            if (nextDMN instanceof MappingExtentNode) existingExtentNodes.add(nextDMN);
        }

        if (existingExtentNodes.isEmpty()) return Collections.EMPTY_LIST;
        return existingExtentNodes;
    }

    private List getExistingMappingExtents( DiagramModelNode mappingDiagramModelNode ) {
        if (mappingDiagramModelNode == null) return Collections.EMPTY_LIST;

        Iterator iter = mappingDiagramModelNode.getChildren().iterator();
        List existingExtents = new ArrayList(10);
        DiagramModelNode nextDMN = null;
        while (iter.hasNext()) {
            nextDMN = (DiagramModelNode)iter.next();
            if (nextDMN instanceof MappingExtentNode) existingExtents.add(((MappingExtentNode)nextDMN).getExtent());
        }

        if (existingExtents.isEmpty()) return Collections.EMPTY_LIST;
        return existingExtents;
    }

    public int getNumberOfMappingExtents( Object mappingDiagramModelNode ) {
        int nExt = 0;

        Iterator iter = ((DiagramModelNode)mappingDiagramModelNode).getChildren().iterator();
        DiagramModelNode nextDMN = null;
        while (iter.hasNext()) {
            nextDMN = (DiagramModelNode)iter.next();
            if (nextDMN instanceof MappingExtentNode) nExt++;
        }

        return nExt;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory#refresh(com.metamatrix.metamodels.diagram.Diagram)
     */
    public void refresh( DiagramModelNode diagramModelNode,
                         Diagram diagram,
                         MappingAdapterFilter xmlFilter,
                         boolean reconcileMappingClasses,
                         IProgressMonitor monitor ) {
        // use the filter to get the Visible Mapping classes and extents.
        boolean showProgress = (monitor != null);

        if (diagram.getType() != null) {
            DiagramEditor editor = DiagramEditorUtil.getDiagramEditor(diagram);
            DiagramEditPart diagramEP = null;
            if (editor != null && editor.getDiagramViewer() != null) {
                ((MappingDiagramNode)diagramModelNode).setViewer(editor.getDiagramViewer());
                diagramEP = (DiagramEditPart)editor.getDiagramViewer().getContents();
            }
            // get DiagramEditPart and set it's construction state
            if (diagramEP != null) {
                diagramEP.setUnderConstruction(true);
            }
            if (diagram.getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) {
                // we have a coarse Mapping here

                if (reconcileMappingClasses) {
                    if (showProgress) {
                        monitor.subTask("Reconciling Coarse Mapping Classes"); //$NON-NLS-1$
                        monitor.worked(20);
                    }

                    reconcileCoarseMappingClasses(diagramModelNode, diagram, xmlFilter);
                }
                if (showProgress) {
                    monitor.subTask("Reconciling Coarse Extents"); //$NON-NLS-1$
                    monitor.worked(20);
                }
                List coarseExents = reconcileCoarseExtents(diagramModelNode, diagram, xmlFilter, monitor);
                if (showProgress) {
                    monitor.subTask("Update Coarse Mapping Links"); //$NON-NLS-1$
                    monitor.worked(30);
                }

                updateCoarseMappingLinks(diagramModelNode, coarseExents);

                // only create XSD enumerated type diagram objects if we have a logical model type
                if (MappingUiUtil.isLogicalModelType(diagram)) {
                    showEnumeratedTypes(diagramModelNode, diagram);
                }
            } else {
                removeStaleSourceNodeEntities(diagramModelNode);
                // DETAILED MAPPING HERE!!
                if (showProgress) {
                    monitor.subTask("Reconciling Detailed Extents"); //$NON-NLS-1$
                    monitor.worked(35);
                }
                List detailedExents = reconcileDetailedExtents(diagramModelNode, diagram, xmlFilter);
                if (showProgress) {
                    monitor.subTask("Update Detailed Mapping LInks"); //$NON-NLS-1$
                    monitor.worked(35);
                }
                updateDetailedMappingLinks(diagramModelNode, detailedExents);
            }

            // get DiagramEditPart and set it's construction state
            if (diagramEP != null) {
                diagramEP.constructionCompleted(false);
            }
        }

    }

    /**
     * Deletes all {@link EnumeratedTypeLink}s on the specified diagram.
     * 
     * @param theDiagramModelNode the diagram on which all enumerated type links are being deleted
     * @since 5.0.2
     */
    private void deleteAllEnumeratedTypeLinks( DiagramModelNode theDiagramModelNode ) {
        List links = getCurrentEnumeratedTypeLinks(theDiagramModelNode);

        if (!links.isEmpty()) {
            List processedNodes = new ArrayList();

            for (int numLinks = links.size(), i = 0; i < numLinks; ++i) {
                NodeConnectionModel link = (NodeConnectionModel)links.get(i);

                // delete source
                DiagramModelNode source = (DiagramModelNode)link.getSourceNode();
                source.removeSourceConnection(link);

                if (!processedNodes.contains(source)) {
                    processedNodes.add(source);
                    source.updateAssociations();
                }

                // delete target
                DiagramModelNode target = (DiagramModelNode)link.getTargetNode();
                target.removeTargetConnection(link);

                if (!processedNodes.contains(target)) {
                    processedNodes.add(target);
                    target.updateAssociations();
                }
            }
        }
    }

    /**
     * Creates diagram nodes for the enumerated types.
     * 
     * @param theDiagramNode the diagram node where the enumerated type nodes will be contained
     * @param theDiagram the diagram
     * @since 5.0.2
     */
    private void showEnumeratedTypes( DiagramModelNode theDiagramNode,
                                      Diagram theDiagram ) {
        List mappingClassNodes = getCurrentMappingClassNodes(theDiagramNode);

        if (!mappingClassNodes.isEmpty()) {
            deleteAllEnumeratedTypeLinks(theDiagramNode);

            // key=XSDSimpleTypeDefinition, value=diagram node for the enumerated type
            Map typeNodeMap = new HashMap();

            // if a mapping class a column that has a type that is an enumerated type create the
            // enumerated type diagram model node if necessary
            for (int size = mappingClassNodes.size(), i = 0; i < size; ++i) {
                DiagramModelNode mcModelNode = (DiagramModelNode)mappingClassNodes.get(i);
                MappingClass mappingClass = (MappingClass)mcModelNode.getModelObject();
                List columns = mappingClass.getColumns();

                // find mapping class columns that have a type that is an enumerated type
                if (!columns.isEmpty()) {
                    // keep track of the types already processed for the current mapping class in order
                    // to not create duplicate links
                    List processedTypes = new ArrayList();

                    for (int numCols = columns.size(), j = 0; j < numCols; ++j) {
                        MappingClassColumn col = (MappingClassColumn)columns.get(j);
                        EObject type = col.getType();

                        if ((type instanceof XSDSimpleTypeDefinition) && ModelerXsdUtils.isEnumeratedType(type)) {
                            DiagramModelNode node = null;

                            // ensure only process type once for each mapping class
                            if (!processedTypes.contains(type)) {
                                processedTypes.add(type);

                                // create model node for type if needed
                                if (typeNodeMap.containsKey(type)) {
                                    node = (DiagramModelNode)typeNodeMap.get(type);
                                } else {
                                    node = getGenerator().createModel(type, theDiagram);

                                    if (node != null) {
                                        typeNodeMap.put(type, node);
                                    }
                                }

                                // created diagram link between mapping class node and new enumerated type node
                                if (node != null) {
                                    createEnumeratedTypeLink(node, mcModelNode);
                                    node.updateAssociations();
                                    mcModelNode.updateAssociations();
                                } else {
                                    // should not happen if model factory is working
                                    String msgKey = I18nUtil.getPropertyPrefix(MappingDiagramModelFactory.class)
                                                    + "enumeratedTypeModelNodeNotFound"; //$NON-NLS-1$
                                    String msg = UiConstants.Util.getString(msgKey, ((XSDSimpleTypeDefinition)type).getName());
                                    UiConstants.Util.log(IStatus.ERROR, msg);
                                }
                            }
                        }
                    }
                }
            }

            // add all new model nodes to the diagram
            if (!typeNodeMap.isEmpty()) {
                theDiagramNode.addChildren(new ArrayList(typeNodeMap.values()));
            }

        }
    }

    /**
     * Creates the diagram link object from the mapping class to the enumerated type.
     * 
     * @param theEnumeratedType the enumerated type diagram model node
     * @param theMappingClass the mapping class diagram model node
     * @since 5.0.2
     */
    private void createEnumeratedTypeLink( DiagramModelNode theEnumeratedType,
                                           DiagramModelNode theMappingClass ) {
        EnumeratedTypeLink link = new EnumeratedTypeLink(theEnumeratedType, theMappingClass);
        ((DiagramModelNode)link.getSourceNode()).addSourceConnection(link);
        ((DiagramModelNode)link.getTargetNode()).addTargetConnection(link);
    }

    private void removeStaleSourceNodeEntities( DiagramModelNode diagramModelNode ) {
        DiagramEntityManager.cleanUpDiagram((Diagram)diagramModelNode.getModelObject());
    }

    private MappingExtent getMatchingMappingExtent( List extentList,
                                                    MappingExtent extent ) {
        Iterator iter = extentList.iterator();
        MappingExtent nextExtent = null;

        while (iter.hasNext()) {
            nextExtent = (MappingExtent)iter.next();

            if (extentsAreEqual(nextExtent, extent)) {
                return nextExtent;
            }
        }

        return null;
    }

    private List reconcileDetailedExtents( DiagramModelNode diagramModelNode,
                                           Diagram diagram,
                                           MappingAdapterFilter xmlFilter ) {
        List expectedExtents = new ArrayList(getAllDetailedExtents(diagram, xmlFilter));
        List existingExtents = new ArrayList(getExistingMappingExtents(diagramModelNode));

        List currentMappingExtentNodes = getExistingMappingExtentNodes(diagramModelNode);
        // create add and remove lists (try to do this all at once);
        List removeExtentList = new ArrayList();
        List addExtentList = new ArrayList();
        List updateExtentList = new ArrayList();

        MappingExtent nextExtent = null;

        // Walk through existing extents and check to see if any don't belong anymore.
        Iterator iter = existingExtents.iterator();
        while (iter.hasNext()) {
            nextExtent = (MappingExtent)iter.next();
            // Do we have an extent already showing?
            if (!extentListContains(expectedExtents, nextExtent)) {
                removeExtentList.add(nextExtent);
            } else {
                // Need to add the "updated" or recent extent here, so we get the geometry right.
                MappingExtent updatedExtent = getMatchingMappingExtent(expectedExtents, nextExtent);

                if (updatedExtent != null) {
                    updateExtentList.add(updatedExtent);
                }
            }
        }

        // remove old extents from existing list.
        iter = removeExtentList.iterator();
        while (iter.hasNext())
            existingExtents.remove(iter.next());

        // Walk through expected extents and check to see if any don't exist yet.
        iter = expectedExtents.iterator();
        while (iter.hasNext()) {
            nextExtent = (MappingExtent)iter.next();
            // Do we have an extent already showing?
            if (!extentListContains(existingExtents, nextExtent)) addExtentList.add(nextExtent);
        }

        // Now we need to do the work here, remove old, and create new.

        if (!removeExtentList.isEmpty()) {
            List removeNodeList = new ArrayList(removeExtentList.size());
            iter = removeExtentList.iterator();
            MappingExtentNode nextNode = null;
            while (iter.hasNext()) {
                nextExtent = (MappingExtent)iter.next();
                nextNode = getExtentNode(diagramModelNode, nextExtent, currentMappingExtentNodes);
                removeNodeList.add(nextNode);
            }
            if (!removeNodeList.isEmpty()) diagramModelNode.removeChildren(removeNodeList, false);
        }

        // Now we need to call "update" to update list
        if (!updateExtentList.isEmpty()) {
            iter = updateExtentList.iterator();
            MappingExtentNode nextNode = null;
            while (iter.hasNext()) {
                nextExtent = (MappingExtent)iter.next();
                nextNode = getExtentNode(diagramModelNode, nextExtent, currentMappingExtentNodes);
                updateExtentNode(nextNode, nextExtent);
            }
        }

        // Last we need to create new extents for the missing ones.
        if (!addExtentList.isEmpty()) {
            addExentNodes(diagramModelNode, addExtentList, null);
        }

        return expectedExtents;
    }

    private List reconcileCoarseExtents( DiagramModelNode diagramModelNode,
                                         Diagram diagram,
                                         MappingAdapterFilter xmlFilter,
                                         IProgressMonitor monitor ) {
        boolean showProgress = (monitor != null);

        if (showProgress) monitor.subTask("Getting all coarse extents from XmlFilter."); //$NON-NLS-1$

        List expectedExtents = new ArrayList(getAllCoarseExtents(xmlFilter, monitor));

        // Check if there are NO mapping classes, then set the expected list to EMPTY
        if (getCurrentMappingClasses(diagramModelNode).isEmpty()) {
            expectedExtents = Collections.EMPTY_LIST;
        }

        if (showProgress) monitor.subTask("Getting existing extents in diagram."); //$NON-NLS-1$
        List existingExtents = new ArrayList(getExistingMappingExtents(diagramModelNode));

        List currentMappingExtentNodes = getExistingMappingExtentNodes(diagramModelNode);
        // create add and remove lists (try to do this all at once);
        List removeExtentList = new ArrayList();
        List addExtentList = new ArrayList();
        List updateExtentList = new ArrayList();

        MappingExtent nextExtent = null;

        if (showProgress) monitor.subTask("Find old extents"); //$NON-NLS-1$
        // Walk through existing extents and check to see if any don't belong anymore.
        Iterator iter = existingExtents.iterator();
        while (iter.hasNext()) {
            nextExtent = (MappingExtent)iter.next();
            // Do we have an extent already showing?
            if (!extentListContains(expectedExtents, nextExtent)) {
                removeExtentList.add(nextExtent);
            } else {
                // Need to add the "updated" or recent extent here, so we get the geometry right.
                MappingExtent updatedExtent = getMatchingMappingExtent(expectedExtents, nextExtent);

                if (updatedExtent != null) {
                    updateExtentList.add(updatedExtent);
                }
            }
        }

        if (showProgress) monitor.subTask("Find new extents"); //$NON-NLS-1$
        // remove old extents from existing list.
        iter = removeExtentList.iterator();
        while (iter.hasNext())
            existingExtents.remove(iter.next());

        // Walk through expected extents and check to see if any don't exist yet.
        /*
         * jh Lyran enh:  I sense an issue here.  If the basic logic does not recreate an Extent
         *                when it already exists, will we fail to refresh the count on a SummaryExtent
         *                when more expanding is done after it is first displayed?
         */
        iter = expectedExtents.iterator();
        while (iter.hasNext()) {
            nextExtent = (MappingExtent)iter.next();
            // Do we have an extent already showing?
            if (!extentListContains(existingExtents, nextExtent)) addExtentList.add(nextExtent);
        }

        // Now we need to do the work here, remove old, and create new.
        if (showProgress) monitor.subTask("Remove old extents from diagram"); //$NON-NLS-1$
        if (!removeExtentList.isEmpty()) {
            List removeNodeList = new ArrayList(removeExtentList.size());
            iter = removeExtentList.iterator();
            MappingExtentNode nextNode = null;
            while (iter.hasNext()) {
                nextExtent = (MappingExtent)iter.next();
                nextNode = getExtentNode(diagramModelNode, nextExtent, currentMappingExtentNodes);
                removeNodeList.add(nextNode);
            }
            if (!removeNodeList.isEmpty()) diagramModelNode.removeChildren(removeNodeList, false);
        }

        if (showProgress) monitor.subTask("Update existing extents"); //$NON-NLS-1$
        // Now we need to call "update" to update list
        if (!updateExtentList.isEmpty()) {
            iter = updateExtentList.iterator();
            MappingExtentNode nextNode = null;
            while (iter.hasNext()) {
                nextExtent = (MappingExtent)iter.next();
                nextNode = getExtentNode(diagramModelNode, nextExtent, currentMappingExtentNodes);
                updateExtentNode(nextNode, nextExtent);
            }
        }

        // Last we need to create new extents for the missing ones.
        if (!addExtentList.isEmpty()) {
            addExentNodes(diagramModelNode, addExtentList, monitor);
        }
        return expectedExtents;
    }

    private void reconcileCoarseMappingClasses( DiagramModelNode diagramModelNode,
                                                Diagram diagram,
                                                MappingAdapterFilter xmlFilter ) {
        /*
         *  jh Lyra enh: Issue: do we need Lyra-specific behavior in this method????? 
         */
        // System.out.println("[MappingDiagramModelFactory.reconcileCoarseMappingClasses] TOP " );
        List removeNodeList = new ArrayList();

        List currentMappingClassNodes = getCurrentMappingClassNodes(diagramModelNode);
        List visibleMappingClasses = xmlFilter.getMappedClassifiers();

        DiagramModelNode nextNode = null;
        Iterator iter = currentMappingClassNodes.iterator();
        while (iter.hasNext()) {
            nextNode = (DiagramModelNode)iter.next();
            if (!visibleMappingClasses.contains(nextNode.getModelObject())) {
                removeNodeList.add(nextNode);
            }
        }
        diagramModelNode.removeChildren(removeNodeList, false);

        boolean bDefaultFoldedState = MappingDiagramUtil.getCurrentMappingDiagramBehavior().getDefaultMappingClassFoldedState();
        // System.out.println("[MappingDiagramModelFactory.reconcileCoarseMappingClasses] Retrieved DefaultFoldedState: " +
        // bDefaultFoldedState );

        List addedNodes = new ArrayList();

        List currentMappingClasses = getCurrentMappingClasses(diagramModelNode);
        iter = visibleMappingClasses.iterator();
        while (iter.hasNext()) {
            Object nextObj = iter.next();
            if (!currentMappingClasses.contains(nextObj)) {
                DiagramModelNode childModelNode = getGenerator().createModel(nextObj, diagram);
                if (childModelNode != null) {
                    addedNodes.add(childModelNode);
                    addRecursionImage(childModelNode);
                    if (childModelNode instanceof UmlClassifierNode) {
                        // System.out.println("[MappingDiagramModelFactory.reconcileCoarseMappingClasses] About to set a classifier's expanded state to: "
                        // + bDefaultFoldedState );
                        // jhTODO Is this an issue? When can we turn this off so that we leave
                        // MCs expanded as the user made them, rather than make them all
                        // expand or collapse per the global derault?
                        ((UmlClassifierNode)childModelNode).setExpandedState(!bDefaultFoldedState);
                    }

                }
            }
        }

        diagramModelNode.addChildren(addedNodes);

        addLockedImages(diagramModelNode);
    }

    /**
     * Sets the column properties of this tree viewer. The properties must correspond with the columns of the tree control. They
     * are used to identify the column in a cell modifier.
     * 
     * @param columnProperties the list of column properties
     * @since 3.1
     */

    private MappingExtentNode getDetailedExtentNode( DiagramModelNode diagramModelNode,
                                                     EObject mappingReferenceEObject,
                                                     EObject locationEObject ) {

        Iterator iter = getExistingMappingExtentNodes(diagramModelNode).iterator();
        MappingExtentNode nextNode = null;

        while (iter.hasNext()) {
            nextNode = (MappingExtentNode)iter.next();
            if (nextNode.getModelObject().equals(locationEObject) && nextNode.getExtent().getMappingReference() != null
                && nextNode.getExtent().getMappingReference().equals(mappingReferenceEObject)) {
                return nextNode;
            }
        }

        return null;
    }

    private MappingExtentNode getExtentNode( DiagramModelNode diagramModelNode,
                                             EObject mappingClassEObject,
                                             EObject locationEObject ) {
        Iterator iter = getExistingMappingExtentNodes(diagramModelNode).iterator();
        MappingExtentNode nextNode = null;

        while (iter.hasNext()) {
            nextNode = (MappingExtentNode)iter.next();

            // Defect 21744: adding null checks to avoid NPEs
            if (nextNode == null) {
                continue;
            }

            if (nextNode.getModelObject() == null) {
                continue;
            }

            if (nextNode.getMappingClass() == null) {
                continue;
            }

            // jh Lyra enh:
            if (nextNode instanceof SummaryExtentNode) {
                continue;
            }

            if (nextNode.getModelObject().equals(locationEObject) && nextNode.getMappingClass().equals(mappingClassEObject)) {
                return nextNode;
            }
        }

        return null;
    }

    private MappingExtentNode getSummaryExtentNode( DiagramModelNode diagramModelNode,
                                                    EObject mappingClassEObject,
                                                    EObject locationEObject ) {
        Iterator iter = getExistingMappingExtentNodes(diagramModelNode).iterator();
        SummaryExtentNode seNode = null;

        while (iter.hasNext()) {
            Object oNextNode = iter.next();

            if (oNextNode instanceof SummaryExtentNode) {
                seNode = (SummaryExtentNode)oNextNode;

                if (seNode.getModelObject().equals(locationEObject)
                    && ((SummaryExtent)seNode.getExtent()).getMappingClasses().keySet().contains(mappingClassEObject)) {
                    return seNode;
                }
            }
        }

        return null;
    }

    private MappingExtentNode getExtentNode( DiagramModelNode diagramModelNode,
                                             MappingExtent mappingExtent,
                                             List mappingExtentNodes ) {
        Iterator iter = mappingExtentNodes.iterator();
        MappingExtentNode nextNode = null;

        while (iter.hasNext()) {
            nextNode = (MappingExtentNode)iter.next();
            if (extentsAreEqual(nextNode.getExtent(), mappingExtent)) return nextNode;
        }

        return null;
    }

    private void updateExtentNode( MappingExtentNode extentNode,
                                   MappingExtent currentExtent ) {
        double zoomFactor = DiagramEditorUtil.getCurrentZoomFactor();
        extentNode.setExtent(currentExtent);
        extentNode.setExtentPosition(0);
        int newH = (int)(currentExtent.getHeight() / zoomFactor);
        // defect 17002 - mapping extents would grow or shrink, depending on the zoom, as
        // the document tree is expanded/collapsed. The current node's width is already
        // set correctly from when the zoom factor was set.
        int oldW = extentNode.getWidth();
        extentNode.setSize(new Dimension(oldW, newH));
    }

    /**
     * Returns newly created and linked MappingExtentNode Note that the object still has to be added to a parent and the node's
     * updateAssociations() method has to be called to get the links to show up.
     * 
     * @param diagramModelNode
     * @param currentExtent
     * @return
     */
    private MappingExtentNode createExtentNode( DiagramModelNode diagramModelNode,
                                                MappingExtent currentExtent ) {
        MappingExtentNode mappingExtentNode = null;

        if (((Diagram)diagramModelNode.getModelObject()).getType() != null
            && ((Diagram)diagramModelNode.getModelObject()).getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) {
            EObject mappingClassEObject = currentExtent.getMappingReference();
            EObject locationEObject = currentExtent.getDocumentNodeReference();

            DiagramModelNode mappingClassNode = getModelNode(diagramModelNode, mappingClassEObject);

            if (mappingClassNode != null) {
                mappingExtentNode = new MappingExtentNode(diagramModelNode, locationEObject, currentExtent, true);

                mappingExtentNode.setMappingClass(mappingClassEObject);
                mappingExtentNode.setParent(diagramModelNode);
                // Add link from MC to Extent
                MappingLink targetLink = getMappingLinkConnectionModel(mappingClassNode, mappingExtentNode);
                ((DiagramModelNode)targetLink.getSourceNode()).addSourceConnection(targetLink);
                ((DiagramModelNode)targetLink.getTargetNode()).addTargetConnection(targetLink);

                mappingClassNode.updateAssociations();
                mappingExtentNode.updateAssociations();
            } else {
                mappingExtentNode = new MappingExtentNode(diagramModelNode, locationEObject, currentExtent, true);

                // mappingExtentNode.setMappingClass(mappingClassEObject);
                mappingExtentNode.setParent(diagramModelNode);
            }
        } else {
            // DETAILED
            EObject mappingReference = currentExtent.getMappingReference();
            EObject locationEObject = currentExtent.getDocumentNodeReference();
            EObject mappingClassReference = null;
            if (mappingReference != null) mappingClassReference = mappingReference.eContainer();

            // DiagramModelNode attributeNode = getModelNode(diagramModelNode, mappingReference);
            DiagramModelNode mappingClassNode = getModelNode(diagramModelNode, mappingClassReference);

            mappingExtentNode = new MappingExtentNode(diagramModelNode, locationEObject, currentExtent, false);

            mappingExtentNode.setParent(diagramModelNode);

            if (mappingClassNode != null) {
                // Add link from MC to Extent
                MappingLink targetLink = getMappingLinkConnectionModel(mappingClassNode, mappingExtentNode);
                ((DiagramModelNode)targetLink.getSourceNode()).addSourceConnection(targetLink);
                ((DiagramModelNode)targetLink.getTargetNode()).addTargetConnection(targetLink);
                mappingClassNode.updateAssociations();
                mappingExtentNode.updateAssociations();
            }
        }

        return mappingExtentNode;
    }

    /**
     * Returns newly created and linked SummaryExtentNode Note that the object still has to be added to a parent and the node's
     * updateAssociations() method has to be called to get the links to show up.
     * 
     * @param diagramModelNode
     * @param currentExtent
     * @return
     */
    private SummaryExtentNode createSummaryExtentNode( DiagramModelNode diagramModelNode,
                                                       SummaryExtent currentExtent ) {
        SummaryExtentNode mappingExtentNode = null;

        if (((Diagram)diagramModelNode.getModelObject()).getType() != null
            && ((Diagram)diagramModelNode.getModelObject()).getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) {

            EObject locationEObject = currentExtent.getDocumentNodeReference();

            /*
             *  jh Lyra ISSUE: fix this: For a Summary, there might be multiple MCs
             *                 BUT, we only wish to create a single SummaryExtentNode.  Hmmmm....
             *                 -- Create the single SummaryExtentNode
             *                 -- Leave 'setMappingClass( null )
             *                 -- Create a MappingLink for each MC
             */
            // jh: revision of this next line should be to test all mappingClassObjects in array
            // to see if one exists? No, I don't think so. By definition, shouldn't the
            // Summary extent appear whether or not its MCs are visible? So we must revise
            // with the goal of creating links WHEN the MC is visible.
            HashMap hmapMappingClasses = currentExtent.getMappingClasses();

            if (hmapMappingClasses == null) {
                return null;
            }

            // create the SummaryExtentNode whether or not any of its MCs are in the diagram
            mappingExtentNode = new SummaryExtentNode(diagramModelNode, locationEObject, currentExtent, true);

            mappingExtentNode.setParent(diagramModelNode);
            mappingExtentNode.setMappingClasses(hmapMappingClasses);

            Iterator itMCs = hmapMappingClasses.keySet().iterator();

            // walk this SummaryExtent's MCs, and create links to any that are visible:
            while (itMCs.hasNext()) {

                EObject nextMappingClassEObject = (EObject)itMCs.next();
                DiagramModelNode nextMappingClassNode = getModelNode(diagramModelNode, nextMappingClassEObject);

                if (nextMappingClassNode != null) {

                    // Create a link from each MC to the Extent
                    MappingLink targetLink = getMappingLinkConnectionModel(nextMappingClassNode, mappingExtentNode);
                    ((DiagramModelNode)targetLink.getSourceNode()).addSourceConnection(targetLink);
                    ((DiagramModelNode)targetLink.getTargetNode()).addTargetConnection(targetLink);

                    nextMappingClassNode.updateAssociations();
                    mappingExtentNode.updateAssociations();
                }
            }
        }

        return mappingExtentNode;
    }

    private void addExentNodes( DiagramModelNode diagramModelNode,
                                List newExtents,
                                IProgressMonitor monitor ) {
        boolean showProgress = (monitor != null);

        List newChildren = new ArrayList(newExtents.size());
        String message = null;
        int nExtents = newExtents.size();

        Iterator iter = newExtents.iterator();
        MappingExtent nextExtent = null;
        int iExtent = 0;
        while (iter.hasNext()) {

            if (showProgress) {
                if (iExtent % 10 == 0) {
                    message = "Create node for missing extent " + iExtent + " of " + nExtents; //$NON-NLS-1$ //$NON-NLS-2$
                    monitor.subTask(message);
                }
            }

            nextExtent = (MappingExtent)iter.next();

            // jh Lyra ISSUE: we could revise this to call the right create method:
            MappingExtentNode meNode = null;

            if (nextExtent instanceof SummaryExtent) {
                meNode = createSummaryExtentNode(diagramModelNode, (SummaryExtent)nextExtent);
            } else {
                meNode = createExtentNode(diagramModelNode, nextExtent);
            }

            if (meNode != null) {
                newChildren.add(meNode);
            }
            iExtent++;
        }

        if (showProgress) {
            message = "Adding " + newChildren.size() + " extent nodes to diagram."; //$NON-NLS-1$ //$NON-NLS-2$
            monitor.subTask(message);
        }
        diagramModelNode.addChildren(newChildren);

        // call updateExtentNode and updateAssociations on each node.
        iter = newChildren.iterator();
        MappingExtentNode nextNode = null;
        iExtent = 0;
        while (iter.hasNext()) {

            if (showProgress) {
                if (iExtent % 10 == 0) {
                    message = "Updating extent " + iExtent + " of " + nExtents; //$NON-NLS-1$ //$NON-NLS-2$
                    monitor.subTask(message);
                }
            }
            nextNode = (MappingExtentNode)iter.next();
            nextNode.updateAssociations();
            updateExtentNode(nextNode, nextNode.getExtent());
            iExtent++;
        }

    }

    private List getCurrentMappingClasses( DiagramModelNode diagramModelNode ) {
        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        List returnList = new ArrayList();
        while (iter.hasNext()) {
            nextNode = (DiagramModelNode)iter.next();
            if (nextNode instanceof UmlClassifierNode) {
                if (!MappingDiagramUtil.isInputSet(nextNode.getModelObject())) returnList.add(nextNode.getModelObject());
            }
        }

        return returnList;
    }

    @Override
    protected void clearAllSourceNodes( DiagramModelNode transformationDiagramModelNode ) {
        EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram((Diagram)transformationDiagramModelNode.getModelObject());

        Iterator iter = getCurrentSourceNodes(transformationDiagramModelNode).iterator();
        DiagramModelNode nextSourceNode = null;
        while (iter.hasNext()) {
            nextSourceNode = (DiagramModelNode)iter.next();
            if (!TransformationHelper.isSqlInputSet(nextSourceNode.getModelObject())) removeSourceTable(transformationEObject,
                                                                                                        transformationDiagramModelNode,
                                                                                                        nextSourceNode.getModelObject());
        }
    }

    private List getCurrentMappingClassNodes( DiagramModelNode diagramModelNode ) {
        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        List returnList = new ArrayList();
        while (iter.hasNext()) {
            nextNode = (DiagramModelNode)iter.next();
            if (nextNode instanceof UmlClassifierNode) {
                if (!MappingDiagramUtil.isInputSet(nextNode.getModelObject())) returnList.add(nextNode);
            }
        }

        return returnList;
    }

    private List getAllCoarseExtents( MappingAdapterFilter xmlFilter,
                                      IProgressMonitor monitor ) {
        boolean showProgress = (monitor != null);

        List totalExtentList = new ArrayList();
        if (showProgress) {
            monitor.subTask("Getting mapping classes from XmlFilter"); //$NON-NLS-1$
        }

        // get the extents from the MappingAdapterFilter
        totalExtentList.addAll(xmlFilter.getCoarseMappingExtents(monitor));

        return totalExtentList;
    }

    private List getAllDetailedExtents( Diagram diagram,
                                        MappingAdapterFilter xmlFilter ) {
        // THis diagram's target is assumed to be the mapping class
        MappingClass targetMappingClass = (MappingClass)diagram.getTarget();
        List detailedExtentList = new ArrayList(xmlFilter.getDetailedMappingExtents(targetMappingClass));
        return detailedExtentList;
    }

    // MAPPING LINK METHODS

    public void updateCoarseMappingLinks( DiagramModelNode mappingDiagramModelNode,
                                          List currentExtents ) {

        List realMappingLinks = getRealCoarseMappingLinks(mappingDiagramModelNode, currentExtents);

        /*
         * jh Lyra enh:  I like this, pass the common list of Extents into both the original get...Links
         *               method and the new getRealCoarseMappingLinksForSummaryExtents() method,
         *               and the Summary method is the only one that pays attention to the Summary Extents.
         */
        List realSummaryLinks = getRealCoarseMappingLinksForSummaryExtents(mappingDiagramModelNode, currentExtents);

        // now combind these two Lists
        ArrayList arylRealLinks = new ArrayList();
        arylRealLinks.addAll(realMappingLinks);
        arylRealLinks.addAll(realSummaryLinks);

        List staleMappingLinks = getStaleMappingLinks(arylRealLinks, mappingDiagramModelNode);

        List updatedNodes = new ArrayList(cleanUpStaleMappingLinks(staleMappingLinks, mappingDiagramModelNode));

        Iterator iter = arylRealLinks.iterator();

        while (iter.hasNext()) {
            NodeConnectionModel nextAssociation = (NodeConnectionModel)iter.next();
            if (!mappingLinkExists(mappingDiagramModelNode, nextAssociation)) {
                ((DiagramModelNode)nextAssociation.getSourceNode()).addSourceConnection(nextAssociation);
                ((DiagramModelNode)nextAssociation.getTargetNode()).addTargetConnection(nextAssociation);

                // Keep a list of new end nodes so we can tell them to fire
                // an updateAssociations() call...
                if (!updatedNodes.contains(nextAssociation.getSourceNode())) updatedNodes.add(nextAssociation.getSourceNode());
                if (!updatedNodes.contains(nextAssociation.getTargetNode())) updatedNodes.add(nextAssociation.getTargetNode());

                List labelNodes = nextAssociation.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    mappingDiagramModelNode.addChildren(labelNodes);
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

    public void updateDetailedMappingLinks( DiagramModelNode mappingDiagramModelNode,
                                            List currentExtents ) {

        List realMappingLinks = getRealDetailedMappingLinks(mappingDiagramModelNode, currentExtents);

        List staleMappingLinks = getStaleMappingLinks(realMappingLinks, mappingDiagramModelNode);

        List updatedNodes = new ArrayList(cleanUpStaleMappingLinks(staleMappingLinks, mappingDiagramModelNode));

        Iterator iter = realMappingLinks.iterator();

        while (iter.hasNext()) {
            NodeConnectionModel nextAssociation = (NodeConnectionModel)iter.next();
            if (!mappingLinkExists(mappingDiagramModelNode, nextAssociation)) {
                ((DiagramModelNode)nextAssociation.getSourceNode()).addSourceConnection(nextAssociation);
                ((DiagramModelNode)nextAssociation.getTargetNode()).addTargetConnection(nextAssociation);

                // Keep a list of new end nodes so we can tell them to fire
                // an updateAssociations() call...
                if (!updatedNodes.contains(nextAssociation.getSourceNode())) updatedNodes.add(nextAssociation.getSourceNode());
                if (!updatedNodes.contains(nextAssociation.getTargetNode())) updatedNodes.add(nextAssociation.getTargetNode());

                List labelNodes = nextAssociation.getLabelNodes();
                if (labelNodes != null && !labelNodes.isEmpty()) {
                    mappingDiagramModelNode.addChildren(labelNodes);
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

    protected List getRealDetailedMappingLinks( DiagramModelNode mappingDiagramModelNode,
                                                List extentList ) {
        // THis method takes the coarse extent list, and creates a set of temporary MappingLinks
        // to compare to the existing ones on the diagram...

        List realMappingLinks = new ArrayList();

        Iterator extentIter = extentList.iterator();
        // Each coarse extent should have a MappingReference (Mapping Class) and a
        // Document reference (location)
        EObject nextMappingReference = null;
        EObject nextExtentLocation = null;
        MappingExtent nextExtent = null;
        DiagramModelNode mappingClassNode = null;
        DiagramModelNode extentNode = null;

        while (extentIter.hasNext()) {
            nextExtent = (MappingExtent)extentIter.next();
            nextMappingReference = nextExtent.getMappingReference();
            nextExtentLocation = nextExtent.getDocumentNodeReference();
            if (nextMappingReference != null && nextExtentLocation != null) {
                mappingClassNode = DiagramUiUtilities.getDiagramModelNode(nextMappingReference.eContainer(),
                                                                          mappingDiagramModelNode);
                extentNode = getDetailedExtentNode(mappingDiagramModelNode,
                                                   nextExtent.getMappingReference(),
                                                   nextExtent.getDocumentNodeReference());
                if (extentNode != null && mappingClassNode != null) {
                    realMappingLinks.add(new MappingLink(mappingClassNode, extentNode));
                }
            }

        }
        return realMappingLinks;
    }

    protected List getRealCoarseMappingLinks( DiagramModelNode mappingDiagramModelNode,
                                              List extentList ) {
        // THis method takes the coarse extent list, and creates a set of temporary MappingLinks
        // to compare to the existing ones on the diagram...

        List realMappingLinks = new ArrayList();

        Iterator extentIter = extentList.iterator();
        // Each coarse extent should have a MappingReference (Mapping Class) and a
        // Document reference (location)
        EObject nextMappingClass = null;
        EObject nextExtentLocation = null;
        MappingExtent nextExtent = null;
        DiagramModelNode mappingClassNode = null;
        DiagramModelNode extentNode = null;

        while (extentIter.hasNext()) {
            nextExtent = (MappingExtent)extentIter.next();

            // this method should NOT process SummaryExtents
            if (nextExtent instanceof SummaryExtent) {
                continue;
            }

            nextMappingClass = nextExtent.getMappingReference();
            nextExtentLocation = nextExtent.getDocumentNodeReference();
            if (nextMappingClass != null && nextExtentLocation != null) {
                mappingClassNode = DiagramUiUtilities.getDiagramModelNode(nextMappingClass, mappingDiagramModelNode);
                extentNode = getExtentNode(mappingDiagramModelNode,
                                           nextExtent.getMappingReference(),
                                           nextExtent.getDocumentNodeReference());
                if (extentNode != null && mappingClassNode != null) {
                    realMappingLinks.add(new MappingLink(mappingClassNode, extentNode));
                }
            }
        }
        return realMappingLinks;
    }

    protected List getRealCoarseMappingLinksForSummaryExtents( DiagramModelNode mappingDiagramModelNode,
                                                               List extentList ) {
        /*
         * new code for jh Lyra enh: 
         *  Should we use this new method to handle Links for Summary Extents,
         *  Or should we just go ahead and create them in the getAllCoarseExtents() method? 
         */

        // THis method takes the coarse extent list, and creates a set of temporary MappingLinks
        // to compare to the existing ones on the diagram...
        List realMappingLinks = new ArrayList();

        Iterator extentIter = extentList.iterator();
        // Each coarse extent should have a MappingReference (Mapping Class) and a
        // Document reference (location)
        EObject nextMappingClass = null;
        EObject nextExtentLocation = null;
        MappingExtent nextExtent = null;
        SummaryExtent nextSummaryExtent = null;
        DiagramModelNode mappingClassNode = null;
        DiagramModelNode extentNode = null;

        while (extentIter.hasNext()) {
            nextExtent = (MappingExtent)extentIter.next();

            // this method only processes SummaryExtents
            if (nextExtent instanceof SummaryExtent) {
                nextSummaryExtent = (SummaryExtent)nextExtent;

                nextExtentLocation = nextExtent.getDocumentNodeReference();

                HashMap hmapMappingClasses = nextSummaryExtent.getMappingClasses();
                Iterator itMCs = hmapMappingClasses.keySet().iterator();

                while (itMCs.hasNext()) {
                    nextMappingClass = (EObject)itMCs.next();

                    if (nextMappingClass != null && nextExtentLocation != null) {

                        // get any existing node from the diagram
                        mappingClassNode = DiagramUiUtilities.getDiagramModelNode(nextMappingClass, mappingDiagramModelNode);

                        extentNode = getSummaryExtentNode(mappingDiagramModelNode, nextMappingClass, nextExtentLocation);

                        // if both of those nodes exist, add a new MappingLink node to our collection of links
                        if (extentNode != null && mappingClassNode != null) {
                            realMappingLinks.add(new MappingLink(mappingClassNode, extentNode));
                        }
                    }
                }
            }
        }

        return realMappingLinks;
    }

    protected List getStaleMappingLinks( List expectedMappingLinks,
                                         DiagramModelNode diagramModelNode ) {
        List staleAssociations = new ArrayList();

        // get all connections from model
        // walk through expected associations.
        Iterator iter = getCurrentMappingLinks(diagramModelNode).iterator();
        Iterator expectedIter = null;
        MappingLink nextCurrentAssociation = null;
        MappingLink nextExpectedAssociation = null;
        boolean foundMatch = false;
        while (iter.hasNext()) {
            foundMatch = false;
            nextCurrentAssociation = (MappingLink)iter.next();
            expectedIter = expectedMappingLinks.iterator();
            while (expectedIter.hasNext() && !foundMatch) {
                nextExpectedAssociation = (MappingLink)expectedIter.next();
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

    protected boolean mappingLinkExists( DiagramModelNode diagramModelNode,
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

    protected List cleanUpStaleMappingLinks( List staleLinks,
                                             DiagramModelNode diagramModelNode ) {
        List updatedNodes = new ArrayList();
        Iterator iter = staleLinks.iterator();
        NodeConnectionModel nextLink = null;

        while (iter.hasNext()) {
            nextLink = (NodeConnectionModel)iter.next();
            if (nextLink instanceof MappingLink) {
                ((DiagramModelNode)nextLink.getSourceNode()).removeSourceConnection(nextLink);
                ((DiagramModelNode)nextLink.getTargetNode()).removeTargetConnection(nextLink);

                if (!updatedNodes.contains(nextLink.getSourceNode())) updatedNodes.add(nextLink.getSourceNode());
                if (!updatedNodes.contains(nextLink.getTargetNode())) updatedNodes.add(nextLink.getTargetNode());
            }

        }

        return updatedNodes;
    }

    protected List getCurrentMappingLinks( DiagramModelNode diagramModelNode ) {
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
                if (nextAssociation instanceof MappingLink && !currentAssociations.contains(nextAssociation)) currentAssociations.add(nextAssociation);
            }

            // Walk through the target connections and check if the same info.
            List targetConnections = childModelNode.getTargetConnections();
            sIter = targetConnections.iterator();
            while (sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if (nextAssociation instanceof MappingLink && !currentAssociations.contains(nextAssociation)) currentAssociations.add(nextAssociation);
            }
        }

        return currentAssociations;
    }

    /**
     * Obtain all the {@link EnumeratedTypeLink}s currently on the diagram.
     * 
     * @param theDiagramModelNode the diagram whose enumerated type links are being requested
     * @return the links
     * @since 5.0.2
     */
    private List getCurrentEnumeratedTypeLinks( DiagramModelNode theDiagramModelNode ) {
        List associations = new ArrayList();
        Iterator itr = theDiagramModelNode.getChildren().iterator();

        while (itr.hasNext()) {
            DiagramModelNode child = (DiagramModelNode)itr.next();

            // Walk through the source connections
            NodeConnectionModel nextAssociation = null;
            Iterator connectionItr = child.getSourceConnections().iterator();

            while (connectionItr.hasNext()) {
                nextAssociation = (NodeConnectionModel)connectionItr.next();

                if ((nextAssociation instanceof EnumeratedTypeLink) && !associations.contains(nextAssociation)) {
                    associations.add(nextAssociation);
                }
            }

            // Walk through the target connections
            connectionItr = child.getTargetConnections().iterator();

            while (connectionItr.hasNext()) {
                nextAssociation = (NodeConnectionModel)connectionItr.next();

                if ((nextAssociation instanceof EnumeratedTypeLink) && !associations.contains(nextAssociation)) {
                    associations.add(nextAssociation);
                }
            }
        }

        return associations;
    }

    public void resetExtentLocations( DiagramModelNode mappingDiagramModelNode,
                                      Diagram diagram,
                                      MappingAdapterFilter xmlFilter,
                                      int newY ) {
        if (diagram.getType() != null) {
            DiagramEditPart diagramEP = null;
            DiagramViewer viewer = ((MappingDiagramNode)mappingDiagramModelNode).getViewer();
            if (viewer != null) {
                diagramEP = (DiagramEditPart)viewer.getContents();
            }
            // get DiagramEditPart and set it's construction state
            if (diagramEP != null) {
                diagramEP.setUnderConstruction(true);
            }
            // use the filter to get the Visible Mapping classes and extents.
            if (diagram.getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) {
                // we have a coarse Mapping here
                ((MappingDiagramNode)mappingDiagramModelNode).setCurrentYOrigin(newY);
                resetExtents(mappingDiagramModelNode, newY);
            } else {
                // DETAILED MAPPING HERE!!
                ((MappingDiagramNode)mappingDiagramModelNode).setCurrentYOrigin(newY);
                resetExtents(mappingDiagramModelNode, newY);
            }
            // get DiagramEditPart and set it's construction state
            if (diagramEP != null) {
                diagramEP.constructionCompleted(false);
            }
        }
    }

    private boolean extentsAreEqual( MappingExtent extent1,
                                     MappingExtent extent2 ) {
        boolean result = false;
        if ((extent1.getDocumentNodeReference() != null && extent2.getDocumentNodeReference() != null && extent1.getDocumentNodeReference().equals(extent2.getDocumentNodeReference()))) {
            if ((extent1.getMappingReference() != null && extent2.getMappingReference() != null && extent1.getMappingReference().equals(extent2.getMappingReference()))) result = true;
            else if (extent1.getMappingReference() == null) result = true;
        } else if (extent1.getDocumentNodeReference() == null && extent2.getDocumentNodeReference() == null) {
            if ((extent1.getMappingReference() != null && extent2.getMappingReference() != null && extent1.getMappingReference().equals(extent2.getMappingReference()))) result = true;
        }

        return result;
    }

    private boolean extentListContains( List extentList,
                                        MappingExtent extent ) {
        Iterator iter = extentList.iterator();
        MappingExtent nextExtent = null;
        while (iter.hasNext()) {
            nextExtent = (MappingExtent)iter.next();
            if (extentsAreEqual(extent, nextExtent)) return true;
        }

        return false;
    }

    private void resetExtents( DiagramModelNode mappingDiagramModelNode,
                               int newY ) {
        Iterator iter = getExistingMappingExtentNodes(mappingDiagramModelNode).iterator();

        MappingExtentNode nextExtentNode = null;
        while (iter.hasNext()) {
            nextExtentNode = (MappingExtentNode)iter.next();
            if (nextExtentNode != null) {
                nextExtentNode.setExtentPosition(newY);
            }
        }

    }

    private void addRecursionImage( DiagramModelNode diagramNode ) {
        if (diagramNode instanceof UmlClassifierNode && diagramNode.getModelObject() instanceof MappingClass) {
            MappingClass mc = (MappingClass)diagramNode.getModelObject();
            if (mc.isRecursionAllowed()) {
                Image image = UiPlugin.getDefault().getImage(UiConstants.Images.RECURSION_IMAGE);
                if (image != null) {
                    diagramNode.setFirstOverlayImage(image, PluginConstants.RECURSION_EDITOR_ID);
                }
            } else {
                diagramNode.setFirstOverlayImage(null, null);
            }
        }
    }

    private void addEditInputSetImage( DiagramModelNode diagramNode ) {
        Image image = UiPlugin.getDefault().getImage(UiConstants.Images.EDIT_OBJECT_ICON);
        if (image != null) diagramNode.setFirstOverlayImage(image, PluginConstants.INPUT_SET_EDITOR_ID);
    }

    /**
     * Method which determines whether this EObject can be represented in a diagram or not.
     * 
     * @return boolean
     */
    @Override
    public boolean isDrawable( EObject object ) {
        boolean bResult = true;

        if (ModelMapperFactory.isXmlTreeNode(object)) {
            bResult = false;
        }

        return bResult;
    }

    /**
     * @see java.lang.Object#toString()
     * @since 4.2
     */
    @Override
    public String toString() {
        return "MappingDiagamModelFactory()"; //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.transformation.ui.model.TransformationDiagramModelFactory#addSourceTable(org.eclipse.emf.ecore.EObject,
     *      com.metamatrix.modeler.diagram.ui.model.DiagramModelNode, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public void addSourceTable( EObject transformationEObject,
                                DiagramModelNode transformationDiagramModelNode,
                                EObject sourceEObject ) {
        super.addSourceTable(transformationEObject, transformationDiagramModelNode, sourceEObject);

        // This override method allows the factory to check if the added table is a Staging table and locate it's initial position
        // better.
        if (sourceEObject instanceof StagingTable) {
            DiagramModelNode dmn = getStagingTableNode(transformationDiagramModelNode, sourceEObject);
            DiagramModelNode tNode = getTransformationNode(transformationDiagramModelNode);
            Point tNodePos = tNode.getPosition();
            if (dmn != null) {
                Point newPosition = new Point(tNodePos.x + tNode.getWidth() + 40, tNodePos.y + tNode.getHeight() + 40);
                dmn.setPosition(newPosition);
                // dmn.update(DiagramUiConstants.DiagramNodePropertis.LOCATION);
            }
        }
    }

    private DiagramModelNode getStagingTableNode( DiagramModelNode transformationDiagramModelNode,
                                                  EObject sourceEObject ) {

        Collection contents = transformationDiagramModelNode.getChildren();
        Iterator iter = contents.iterator();
        Object nextChild = null;

        while (iter.hasNext()) {
            nextChild = iter.next();
            if (nextChild instanceof DiagramModelNode && nextChild instanceof UmlClassifierNode) {
                EObject nodeEObject = ((DiagramModelNode)nextChild).getModelObject();

                if (nodeEObject == sourceEObject) {
                    return (DiagramModelNode)nextChild;
                }
            }
        }

        return null;
    }

    private boolean isValidRenameSource( Object source ) {
        if (source != null && (source instanceof MergeMappingClassesAction || source instanceof SplitMappingClassAction)) {
            return false;
        }

        return true;
    }

}
