/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation;

import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * NotationModelGenerator
 */
public interface NotationModelGenerator {
    /**
     * Creates DiagramModelNode object based on eObject input. If diagramContainerObject is
     * provided, a diagram entity object will be created to be stored as a child of the
     * diagramContainerObject. Hence, persistence.
     * @param eObject
     * @param diagramContainerObject
     * @return
     */
    DiagramModelNode createModel(Object eObject, Diagram diagramContainerObject);

    /**
     * Creates DiagramModelNode object based on oChildObject input and a parent node.
     * This method can be used to create a new "attribute" node under a classifier, as 
     * well as a classifier or package node in a diagram. This method was created to be used
     * as part of the notification process.
     * @param parentNode
     * @param oChildObject
     * @return
     */
    DiagramModelNode createChildModel(DiagramModelNode parentNode, Object oChildObject);
    
    /**
     * Returns list of DiagramAssociationObjects for a list of diagram model nodes.
     * @param candidateAssociationNodes
     * @param diagramModelNodes
     * @return
     */
    List getAssociations(List candidateAssociationNodes, List diagramModelNodes);
    
    /**
     * Method designed for use by the notification process to allow a model generator to
     * handle the update to it's specific diagram node types, and to delegate as necessary.
     * @param targetNode
     * @param setNotification
     */
    void performUpdate(DiagramModelNode targetNode, Notification setNotification);
    
    /**
     * Method designed for use by external model factories to find out if specific eObject's
     * are associationed with association type objects i.e. UmlAssociations.
     * This allows the factory to perform updates on links without know specific aspect info.
     * @param targetNode
     * @param setNotification
     */
    boolean isAssociation(EObject someEObject);
}

