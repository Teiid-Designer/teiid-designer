/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.AbstractDiagramEntity;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;

/**
 * DiagramModelFactoryImpl
 */
abstract public class DiagramModelFactoryImpl implements DiagramModelFactory {
//    private static final String THIS_CLASS = "DiagramModelFactoryImpl"; //$NON-NLS-1$
    
    public DiagramModelNode createModel(Object oBaseObject, IProgressMonitor monitor) {
        return null;
    }
    
//    public DiagramModelNode createModel(Object oBaseObject, String sNotationId, IProgressMonitor monitor) {
//        return null;
//    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory#notifyModel(org.eclipse.emf.common.notify.Notification)
     */
    public boolean notifyModel(Notification notification, DiagramModelNode diagramModelNode, String sDiagramTypeId) {
        // Default implementation does nothing;
        return true;
    }
    
    protected boolean currentDiagramRemoved(Diagram diagram) {
        boolean isRemoved = false;
    
        if( diagram == null )
            isRemoved = true;
        else if( diagram.eResource() == null ) {
            isRemoved = true;
            if( diagram.getTarget() != null && diagram.getTarget().eResource() != null)
                isRemoved = false;
            // check the eContainer.
            if( diagram.eContainer() == null ) {
                if(diagramIsTransient(diagram) )
                    isRemoved = false;
            }
        }       
        return isRemoved;
    }
    
    protected boolean diagramIsTransient(Diagram diagram) {
        // Get DiagramTypeManager and ask it the same question
        return DiagramUiPlugin.getDiagramTypeManager().isTransientDiagram(diagram);
    }
    
    public DiagramModelNode getParentInDiagram(DiagramModelNode diagramModelNode, EObject parentEObject) {
        DiagramModelNode parentModelNode = null;
        if( diagramModelNode.getModelObject() != null && diagramModelNode.getModelObject() == parentEObject)
            parentModelNode = diagramModelNode;
        else
            parentModelNode = findParent(diagramModelNode, parentEObject);
        
        return parentModelNode;
    }
    
    private DiagramModelNode findParent(DiagramModelNode someModelNode, EObject parentEObject ) {
        
        List modelChildren = someModelNode.getChildren();
        Iterator iter = modelChildren.iterator();
        DiagramModelNode nextChildNode = null;
        while( iter.hasNext() ) {
            nextChildNode = (DiagramModelNode)iter.next();
            if( nextChildNode.getModelObject() != null && nextChildNode.getModelObject() == parentEObject )
                return nextChildNode;
        }
        return null;
    }
    
    /**
     * Utility method to find a specific diagram model node for an eObject and it is a direct child of the
     * diagram. (i.e not a nested classifier, or an attribute)
     * @param diagramModelNode
     * @param someEObject
     * @return diagramModelNode.
     */
    public DiagramModelNode getNodeInDiagram(DiagramModelNode diagramModelNode, EObject someEObject) {
        if( diagramModelNode.getModelObject() != null && diagramModelNode.getModelObject() == someEObject)
            return diagramModelNode;
        List modelChildren = diagramModelNode.getChildren();
        Iterator iter = modelChildren.iterator();
        DiagramModelNode nextChildNode = null;
        while( iter.hasNext() ) {
            nextChildNode = (DiagramModelNode)iter.next();
            if( nextChildNode.getModelObject() != null && nextChildNode.getModelObject() == someEObject)
                return nextChildNode;
        }
        
        return null;
    }
    
    public DiagramModelNode getModelNode(DiagramModelNode diagramModelNode, EObject someModelObject) {
        if ( diagramModelNode.getModelObject() != null && diagramModelNode.getModelObject() == someModelObject) {
            return diagramModelNode;
        }
        DiagramModelNode matchedNode = null;
        // Check the children
        List contents = diagramModelNode.getChildren();
        if( contents != null && !contents.isEmpty() ) {
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
    
    protected boolean notifierIsDiagramEntity(Notification notification) {
        Object notifier = ModelerCore.getModelEditor().getChangedObject(notification);
        
        return notifier instanceof AbstractDiagramEntity;
    }
    
    
    protected boolean notifierIsDiagram(Notification notification) {
        Object notifier = ModelerCore.getModelEditor().getChangedObject(notification);
        
        return notifier instanceof Diagram;
    }
    
    
    protected boolean isDiagramNotifier(Notification notification) {
        Object notifier = ModelerCore.getModelEditor().getChangedObject(notification);
        
        return (notifier instanceof Diagram || notifier instanceof AbstractDiagramEntity);
    }
    
    protected EObject getEObjectTarget(Notification notification) {
        Object targetObject = ModelerCore.getModelEditor().getChangedObject(notification);
        if( targetObject instanceof EObject )
            return (EObject)targetObject;
            
        return null;
    }
    
    protected boolean isValidTarget(EObject targetEObject) {
        if( targetEObject == null || targetEObject instanceof Diagram || targetEObject instanceof AbstractDiagramEntity )
            return false;
            
        return true;
    }
    
    public void handleLabelProviderChanged(DiagramModelNode diagramModelNode, List eObjects) {
        HashMap updateNodes = new HashMap(eObjects.size());
        
        Iterator iter = eObjects.iterator();
        while( iter.hasNext() ) {
            EObject nextEObject = (EObject)iter.next();
            DiagramModelNode nextNode = getModelNode(diagramModelNode, nextEObject);
            if( nextNode != null ) {
                updateNodes.put(nextNode, "x");  //$NON-NLS-1$
//                nextNode.updateForErrorsAndWarnings();
                DiagramModelNode parentClassNode = DiagramUiUtilities.getClassifierParentNode(nextNode);
                if( parentClassNode != null ) {
                    updateNodes.put(parentClassNode, "x");  //$NON-NLS-1$
                }
            }
        }
        
        iter = updateNodes.keySet().iterator();
        while( iter.hasNext() ) {
            DiagramModelNode nextNode = (DiagramModelNode)iter.next();
            if( nextNode != null ) {
                nextNode.updateForErrorsAndWarnings();
            }
        }
    }
    
    /**
     * Method which determines whether this EObject can be represented in a diagram or not.
     * @return boolean
     */
    public boolean isDrawable( EObject object ) {
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory#shouldRefreshDiagram(org.eclipse.emf.common.notify.Notification, com.metamatrix.modeler.diagram.ui.model.DiagramModelNode, java.lang.String)
     */
    public boolean shouldRefreshDiagram(Notification notification, DiagramModelNode diagramModelNode, String sDiagramTypeId) {
        // return false by default.
        return false;
    }
}
