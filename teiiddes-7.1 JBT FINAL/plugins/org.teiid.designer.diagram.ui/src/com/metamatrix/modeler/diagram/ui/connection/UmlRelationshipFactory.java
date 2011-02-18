/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;


/**
 * @author BLaFond
 *
 * This factory class provides static methods to create one or more UML BinaryAssociation objects
 * given an eObject that contains a UmlRelationship aspect
 */
public class UmlRelationshipFactory {
	
	//
	public static List getBinaryAssociations(EObject relationshipEObject, EObject sourceEObject) {
		List assList = new ArrayList();
		int type = UmlRelationshipHelper.getType(relationshipEObject);
		
		switch( type ) {
			case BinaryAssociation.TYPE_UML_ASSOCIATION: {
				BinaryAssociation newAss = createUmlAssociation(relationshipEObject);
				if( newAss != null ) {
					assList.add(newAss);
				}
			} break;
			
			case BinaryAssociation.TYPE_UML_DEPENDENCY: {
				assList.addAll(createUmlDependencies(relationshipEObject));
			} break;
			
			case BinaryAssociation.TYPE_UML_GENERALIZATION: {
				BinaryAssociation newAss =  createUmlGeneralization(relationshipEObject);
				if( newAss != null ) {
					assList.add(newAss);
				}
			} break;
			
            // Case 3617 - problems with UML2 Diagrams - discussion with Dennis and Barry.  If
            // we cannot determine an assocn type, we should not assume one.  Just dont draw it...
            // Commented out default block below.
            //---------------

//            default: {
//				// Assume that if we get here, we need to create a generalization
//				BinaryAssociation newAss = createUmlGeneralization(sourceEObject, relationshipEObject);
//				if( newAss != null ) {
//					assList.add(newAss);
//				}
//			} break;
		}
		
		return assList;
	}
	
	public static BinaryAssociation createUmlAssociation(EObject relationshipEObject) {
		return new UmlAssociationBass(relationshipEObject);
	}
	
	public static List createUmlDependencies(EObject relationshipEObject) {
		// We need to get the source and targets for this
		List assList = new ArrayList(1);
		
		UmlRelationship theAspect = UmlRelationshipHelper.getRelationshipAspect(relationshipEObject);
		if( theAspect != null ) {
			UmlDependency depAspect = (UmlDependency)theAspect;
			
			Iterator sourceIter = depAspect.getSource(relationshipEObject).iterator();
			Iterator targetIter = null;
			
			// We need to walk through the sources and add a new association for
			
			EObject sourceEObject = null;
			EObject targetEObject = null;
			
			while( sourceIter.hasNext() ) {
				sourceEObject = (EObject)sourceIter.next();
				targetIter = depAspect.getTarget(relationshipEObject).iterator();
				while( targetIter.hasNext()) {
					targetEObject = (EObject)targetIter.next();
					assList.add(new UmlDependencyBass(relationshipEObject, sourceEObject, targetEObject));
				}
			}
		
		}

		return assList;
	}

	public static BinaryAssociation createUmlGeneralization(EObject relationshipEObject) {
		return new UmlGeneralizationBass(relationshipEObject);
	}
	
	public static BinaryAssociation createUmlGeneralization(EObject sourceEObject, EObject targetEObject) {
		return new UmlGeneralizationBass(sourceEObject, sourceEObject, targetEObject);
	}
	
	public static List getBinaryAssociations(List relationshipList, EObject sourceEObject) {
		List finalList = new ArrayList();
		
		Iterator iter = relationshipList.iterator();
		Object nextObject = null;
		EObject nextEObject = null;
		BinaryAssociation nextBass = null;
		
		while( iter.hasNext() ) {
			nextObject = iter.next();
			if( nextObject instanceof EObject ) {
				nextEObject = (EObject)nextObject;
				List tempList = getBinaryAssociations(nextEObject, sourceEObject);
				if( tempList != null && !tempList.isEmpty() ) {
					Iterator innerIter = tempList.iterator();
					while( innerIter.hasNext() ) {
						nextBass = (BinaryAssociation)innerIter.next();
						if( !relationshipExists(finalList, nextBass) )
							finalList.add(nextBass);
					}
				}
			}			
		}
		return finalList;
	}
	
	public static NodeConnectionModel getConnectionModel(
		BinaryAssociation bAss, 
		DiagramModelNode diagramRootModelObject, 
		HashMap nodeMap) {
		
		NodeConnectionModel newConnectionModel = null;
		
		switch( bAss.getRelationshipType() ) {
			case BinaryAssociation.TYPE_UML_ASSOCIATION: {
				
				EObject sourceObject = bAss.getEndTarget(BinaryAssociation.SOURCE_END);
				EObject targetObject = bAss.getEndTarget(BinaryAssociation.TARGET_END);
				if( sourceObject != null &&
					targetObject != null &&
					!sourceObject.equals(targetObject)) {
                                    
					DiagramModelNode sourceNode = null;
					DiagramModelNode targetNode = null;
					if( nodeMap != null && !nodeMap.isEmpty()) {
						sourceNode = (DiagramModelNode)nodeMap.get(sourceObject);
						targetNode = (DiagramModelNode)nodeMap.get(targetObject);
					} else {
						sourceNode = DiagramUiUtilities.getModelNode(sourceObject, diagramRootModelObject);
						targetNode = DiagramUiUtilities.getModelNode(targetObject, diagramRootModelObject);
					}

                                  
					if( sourceNode != null && targetNode != null ) {
						// Make sure these aren't nested in any way. i.e. get the top level classifier node.
						DiagramModelNode sourceClassifierNode = DiagramUiUtilities.getTopClassifierParentNode(sourceNode);
						DiagramModelNode targetClassifierNode = DiagramUiUtilities.getTopClassifierParentNode(targetNode);
						if( sourceClassifierNode != null && targetClassifierNode != null ) {
							newConnectionModel = new DiagramUmlAssociation(sourceClassifierNode, targetClassifierNode, bAss);
						}
					}
				}		
			} break;
						
			case BinaryAssociation.TYPE_UML_DEPENDENCY: {
				
				EObject sourceObject = bAss.getEnd(BinaryAssociation.SOURCE_END);
				EObject targetObject = bAss.getEnd(BinaryAssociation.TARGET_END);
				if( sourceObject != null &&
					targetObject != null &&
					!sourceObject.equals(targetObject)) {
                                    
					DiagramModelNode sourceNode = DiagramUiUtilities.getModelNode(sourceObject, diagramRootModelObject);
					DiagramModelNode targetNode = DiagramUiUtilities.getModelNode(targetObject, diagramRootModelObject);
                                  
					if( sourceNode != null && targetNode != null ) {
						// Make sure these aren't nested in any way. i.e. get the top level classifier node.
						DiagramModelNode sourceClassifierNode = DiagramUiUtilities.getTopClassifierParentNode(sourceNode);
						DiagramModelNode targetClassifierNode = DiagramUiUtilities.getTopClassifierParentNode(targetNode);
						if( sourceClassifierNode != null && targetClassifierNode != null ) {
							newConnectionModel = new DiagramUmlDependency(sourceClassifierNode, targetClassifierNode, bAss);
						}
					}
				}				
			} break;
						
			case BinaryAssociation.TYPE_UML_GENERALIZATION: {
				
				EObject sourceObject = bAss.getEnd(BinaryAssociation.SOURCE_END);
				EObject targetObject = bAss.getEnd(BinaryAssociation.TARGET_END);
				if( sourceObject != null &&
					targetObject != null &&
					!sourceObject.equals(targetObject)) {
                                    
					DiagramModelNode sourceNode = DiagramUiUtilities.getModelNode(sourceObject, diagramRootModelObject);
					DiagramModelNode targetNode = DiagramUiUtilities.getModelNode(targetObject, diagramRootModelObject);
                                  
					if( sourceNode != null && targetNode != null ) {
						// Make sure these aren't nested in any way. i.e. get the top level classifier node.
						DiagramModelNode sourceClassifierNode = DiagramUiUtilities.getTopClassifierParentNode(sourceNode);
						DiagramModelNode targetClassifierNode = DiagramUiUtilities.getTopClassifierParentNode(targetNode);
						if( sourceClassifierNode != null && targetClassifierNode != null ) {
							newConnectionModel = new DiagramUmlGeneralization(sourceClassifierNode, targetClassifierNode, bAss);
						}
					}
				}		
			} break;
						
			default:
			break;
		}
		
		return newConnectionModel;
	}
	
	private static boolean relationshipExists(List currentAssList, BinaryAssociation newBass) {
		Iterator iter = currentAssList.iterator();
		BinaryAssociation nextBass = null;
		
		while( iter.hasNext() ) {
			nextBass = (BinaryAssociation)iter.next();
			if( newBass.equals(nextBass))
				return true;
		}
		
		return false;
	}
	
	public static boolean relationshipExistsInDiagram(DiagramModelNode diagramNode, BinaryAssociation someBass ) {
		
		List allConnections = new ArrayList(DiagramUiUtilities.getAllSourceConnections(diagramNode));
		List bassList = new ArrayList(allConnections.size());
		
		Iterator iter = allConnections.iterator();
		Object nextObject = null;
		DiagramUmlAssociation nextConnectionNode = null;
		BinaryAssociation nextBass = null;
		while( iter.hasNext() ) {
			nextObject = iter.next();
			if( nextObject instanceof DiagramUmlAssociation ) {
				nextConnectionNode = (DiagramUmlAssociation)nextObject;
				nextBass = nextConnectionNode.getBAssociation();
				
				if( nextBass != null && 
					!bassList.contains(nextBass) ) {
					bassList.add(nextBass);	
				}
			}
		}
		
		return relationshipExists(bassList, someBass);
	}
}
