/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.model;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

/**
 * DiagramModelFactory
 */
public interface DiagramModelFactory {
    /**
     * Generic method for creating model object.
     * Model can be as deep (tree-wise) as desired. This model will be given
     * to the DiagramEditor's graphical viewer using setContents(model). The viewer
     * will walk this model and create edit parts via the current plugin's edit part 
     * factory.
     * @param oBaseObject
     * @return
     */
    public DiagramModelNode createModel(Object oBaseObject, IProgressMonitor monitor);
    
    /**
     * More specific method for creating model object based on a desired notation.
     * Default notation is UML.
     * Model can be as deep (tree-wise) as desired. This model will be given
     * to the DiagramEditor's graphical viewer using setContents(model). The viewer
     * will walk this model and create edit parts via the current plugin's edit part 
     * factory.
     * @param oBaseObject
     * @param sNotationId
     * @return
     */
    public DiagramModelNode createModel(Object oBaseObject, String sNotationId, IProgressMonitor monitor);
    
    
    /**
     * This method provides a hook back to this factory for any updates to the model
     * coming from a notification object from the workspace.
     * This method also provides the DiagramEditor a way to assess whether or not the diagram
     * is still valid or not. Deleting the "Target" of the diagram, or some ancestor of the target
     * can be assessed.  The default behavior should be to return true and to set to false when the 
     * model factory detects this situation. The DiagramEditor will then replace the visible diagram
     * with an empty package diagram.
     * 
     * @param notification
     * @param diagramModelNode
     * @param sDiagramTypeId
     * @return diagramRemainsValid
     */
    public boolean notifyModel(
        Notification notification, 
        DiagramModelNode diagramModelNode,
        String sDiagramTypeId );
    /**
     * This method provides a hook to allow the factory to update it's model components based on a list of
     * eObjects that were provided via a DiagramEditor's lable provider listener.
     * @param eObjects - the eObjects that were affected.
     */
    void handleLabelProviderChanged(DiagramModelNode diagramModelNode, List eObjects);
    
    /**
     * Method which determines whether this EObject can be represented in a diagram or not.
     * @return boolean
     */
    boolean isDrawable( EObject object );

    /**
     * This method provides the DiagramEditor a way to assess whether or not the diagram
     * needs to be refreshed.
     * 
     * @param notification
     * @param diagramModelNode
     * @param sDiagramTypeId
     * @return true if the DiagramEditor should update the diagram based on the notifications
     */
    boolean shouldRefreshDiagram( 
        Notification notification, 
        DiagramModelNode diagramModelNode,
        String sDiagramTypeId);
}
