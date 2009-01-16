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

package com.metamatrix.modeler.diagram.ui.notation.uml;

//import java.util.ArrayList;
import java.util.Collections;
//import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.NotationModelGenerator;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlAssociationNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlAttributeNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlOperationNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlPackageNode;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;

/**
 * @author mdrilling
 *
 * Factory for creating UML Model Nodes
 * Responsibility for creating and managing the diagram is at the DiagramModelFactory level.
 */
public class UmlModelGenerator implements NotationModelGenerator {
//    private final static int SOURCE = 0;
//    private final static int TARGET = 1;
    
    /**
     * Create a DiagramModelNode.
     */
    public DiagramModelNode createModel(Object inputEObject, Diagram diagramContainerObject) {
        
        // Return null if the baseObject is not a EObject
        if( ! (inputEObject instanceof EObject) ) {
            return null;
        }
        EObject eObj = (EObject)inputEObject;
        
        DiagramModelNode newDiagramModelNode = null;
        
//        DiagramEntity diagramEntity = getDiagramEntity(eObj, diagramContainerObject);
        
        // Get UML Aspect for this EObject
        MetamodelAspect someAspect = getUmlAspect(eObj);
        
        if( someAspect != null ) {
	        // Create Model Node for the aspect type
	        if(someAspect instanceof UmlPackage) {
	            newDiagramModelNode = new UmlPackageNode(diagramContainerObject, eObj, (UmlPackage)someAspect);
	        } else if(someAspect instanceof UmlClassifier) {
	            newDiagramModelNode = new UmlClassifierNode(diagramContainerObject, eObj, (UmlClassifier)someAspect);
	        }
		}
        return newDiagramModelNode;
    }
    
    /**
     * Create a DiagramModelNode.
     */
    public DiagramModelNode createChildModel(DiagramModelNode parentDiagramModelNode, Object inputEObject ) {
        if( DiagramUiConstants.Util.isDebugEnabled(DebugConstants.MODEL_FACTORY )) {
            String message = "UmlModelGenerator.createChildModel() for parent = " //$NON-NLS-1$
                    + parentDiagramModelNode + "  child EObject = " //$NON-NLS-1$
                    + inputEObject;
            DiagramUiConstants.Util.log( IStatus.INFO, message);
        }
        // Return null if the baseObject is not a EObject
        if( ! (inputEObject instanceof EObject) ) {
            return null;
        }
        
        DiagramModelNode newDiagramModelNode = null;
        
        
        if( parentDiagramModelNode instanceof UmlPackageNode ) {
            // Do Nothing
        } else if(parentDiagramModelNode instanceof UmlClassifierNode) {
			newDiagramModelNode = ((UmlClassifierNode)parentDiagramModelNode).addChild((EObject)inputEObject);
            if( DiagramUiConstants.Util.isDebugEnabled(DebugConstants.MODEL_FACTORY )) {
                String message = "UmlModelGenerator.createChildModel() added child to Node = " + parentDiagramModelNode; //$NON-NLS-1$ 
                DiagramUiConstants.Util.log( IStatus.INFO, message);
            }
        } else if( parentDiagramModelNode.getModelObject() instanceof Diagram ) {
            newDiagramModelNode = createModel(inputEObject, (Diagram)parentDiagramModelNode.getModelObject());
            if( newDiagramModelNode != null )
                newDiagramModelNode.setParent(parentDiagramModelNode);
        } else {
            String message = "UmlModelGenerator.createChildModel() Couldn't Add child to parent Node = " + parentDiagramModelNode; //$NON-NLS-1$
            DiagramUiConstants.Util.log( IStatus.ERROR, message);
        }
        
        if( newDiagramModelNode != null && DiagramUiConstants.Util.isDebugEnabled(DebugConstants.MODEL_FACTORY )) {
            String message = "UmlModelGenerator.createChildModel() Created node = " + newDiagramModelNode; //$NON-NLS-1$ 
            DiagramUiConstants.Util.log( IStatus.INFO, message);
        }
        
        return newDiagramModelNode;
    }
    
    /**
     * Helper method to get the UmlAspect given an EObject
     */
    public MetamodelAspect getUmlAspect(EObject eObject) {
        
        return DiagramUiPlugin.getDiagramAspectManager().getUmlAspect( eObject );   
    }
    
    public boolean isAssociation( EObject eObj ) {
        MetamodelAspect aspect = getUmlAspect(eObj);
        return aspect instanceof UmlAssociation;
    }
    
    public List getAssociations(List candidateAssociationNodes, List diagramModelNodes) {
            return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.notation.NotationModelGenerator#performUpdate(com.metamatrix.modeler.diagram.ui.model.DiagramModelNode, org.eclipse.emf.common.notify.Notification)
     */
    public void performUpdate(DiagramModelNode targetNode, Notification setNotification) {
        if( targetNode instanceof UmlClassifierNode ) {
            ((UmlClassifierNode)targetNode).refreshForNameChange();
            ((UmlClassifierNode)targetNode).refreshForPathChange();
        } else if( targetNode instanceof UmlPackageNode ) {
            ((UmlPackageNode)targetNode).refreshForNameChange();
            ((UmlPackageNode)targetNode).refreshForPathChange();
        } else if( targetNode instanceof UmlAttributeNode ) {
            ((UmlAttributeNode)targetNode).refreshForNameChange();
        } else if( targetNode instanceof UmlAssociationNode ) {
            ((UmlAssociationNode)targetNode).refreshForNameChange();
        } else if( targetNode instanceof UmlOperationNode ) {
            ((UmlOperationNode)targetNode).refreshForNameChange();
        }

    }

}
