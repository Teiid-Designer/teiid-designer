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

package com.metamatrix.modeler.diagram.ui.notation.uml.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.model.AbstractDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.AbstractLocalDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * @author mdrilling
 *
 * Model Node for UML Classifier Container
 */
public class UmlClassifierContainerNode extends AbstractLocalDiagramModelNode {
    public static final int ATTRIBUTES = 0;
    public static final int ASSOCIATIONS = 1;
    public static final int OPERATIONS = 2;
    public static final int CLASSIFIERS = 3;
    
    public int type = -1;
    /**
     * Construct an instance of UmlClassifierContainerNode.
     */
    public UmlClassifierContainerNode( int containerType ) {
        super( null, "ClassifierContainer" ); //$NON-NLS-1$
        this.type = containerType;
    }
    
    /**
     * Construct an instance of UmlClassifierContainerNode.
     */
    public UmlClassifierContainerNode( int containerType , List containedItems ) {
        super( null, "ClassifierContainer" ); //$NON-NLS-1$
        
        this.type = containerType;
        setParentOfChildren(containedItems);
        setChildren(containedItems);
    }
    
    public UmlClassifierContainerNode( EObject modelObject, List containedItems ) {
        super( modelObject, "ClassifierContainer"); //$NON-NLS-1$
        
        setParentOfChildren(containedItems);
        setChildren(containedItems);
    }
    
    /**
     * This constructor is designed to take a list of EObjects and construct a list of
     * appropriate attributes whose type is based on "aspects"
     */
	public UmlClassifierContainerNode( int containerType, List eObjectList, Diagram diagram ) {
		super( null, "ClassifierContainer"); //$NON-NLS-1$
		this.type = containerType;
        // call createChildren() - private method (make sure you set the parent of the node
		setChildren(createChildren(eObjectList, diagram));
	} 
    
    public DiagramModelNode getChild(EObject eObject ) {
        DiagramModelNode childNode = null;
        
        List currentChildren = new ArrayList( getChildren() );
        Iterator iter = currentChildren.iterator();
        DiagramModelNode nextNode = null;
        while( iter.hasNext() && childNode == null ) {
            nextNode = (DiagramModelNode)iter.next();
            if( nextNode != null && nextNode.getModelObject() != null && 
                nextNode.getModelObject().equals(eObject)) {
                childNode = nextNode;
            }
        }
        
        return childNode;
        
    }
    
    public int getType() {
        return this.type;  
    }
    
    private void setParentOfChildren(List children) {
        if( !children.isEmpty() ) {
            DiagramModelNode nextNode = null;
            Iterator iter = children.iterator();
            while( iter.hasNext() ) {
                nextNode = (DiagramModelNode)iter.next();
                nextNode.setParent(this);
            }
        }
    }
    
    public void reorderChildren(List reorderedChildren) {
        List newChildList = new ArrayList(getChildren().size());
        
        Iterator iter = reorderedChildren.iterator();
        EObject nextChild = null;
        DiagramModelNode nextChildNode = null;
        while( iter.hasNext() ) {
            nextChild = (EObject)iter.next();
            nextChildNode = getChild(nextChild);
            if( nextChildNode != null )
                newChildList.add(nextChildNode);
        }
        if( !newChildList.isEmpty() ) {
            setChildren(newChildList);
        }
    }
    
	public List getEObjectChildren() {
		List eObjectList = Collections.EMPTY_LIST;
		
		if( getChildren() != null && !getChildren().isEmpty() ) {
			Iterator iter = null;
			DiagramModelNode nextNode = null;
			eObjectList = new ArrayList(getChildren().size());
			iter = getChildren().iterator();
			while( iter.hasNext() ) {
				nextNode = (DiagramModelNode)iter.next();
				eObjectList.add(nextNode.getModelObject());
			}
		}
		
		return eObjectList;
	}
	
	public boolean isEmpty() {
		if( getChildren() == null || getChildren().isEmpty() )
			return true;
			
		return false;
	}
    
    /**
     * This private method allows the constructor to create a set of attributes
     * for this container.  The container is in charge of knowing what type
     * of attributes to construct.
     * @param eObjectList
     * @param diagram
     * @return
     */
    private List createChildren(List eObjectList, Diagram diagram) {
    	if( eObjectList != null && !eObjectList.isEmpty() ) {
	    	List modelNodeList = new ArrayList(eObjectList.size());
	    	Iterator iter = eObjectList.iterator();
	    	
			while(iter.hasNext()) {
				EObject eObj = (EObject)iter.next();
				MetamodelAspect mmAspect = getUmlAspect(eObj);
	                        
				if(mmAspect instanceof UmlProperty) {
					DiagramModelNode attributeNode = new UmlAttributeNode(eObj, (UmlProperty)mmAspect);
					attributeNode.setParent(this);
					modelNodeList.add(attributeNode);
				} else if(mmAspect instanceof UmlAssociation) {
					DiagramModelNode associationNode = new UmlAssociationNode(eObj, (UmlAssociation)mmAspect);
					associationNode.setParent(this);
					modelNodeList.add(associationNode);
				} else if(mmAspect instanceof UmlOperation) {
					DiagramModelNode operationNode = new UmlOperationNode(eObj, (UmlOperation)mmAspect);
					operationNode.setParent(this);
					modelNodeList.add(operationNode);
				} else if(mmAspect instanceof UmlClassifier) {
				   DiagramModelNode classifierNode = new UmlClassifierNode(diagram, eObj, (UmlClassifier)mmAspect, true);
				   classifierNode.setParent(this);
				   modelNodeList.add(classifierNode);
				}
			}
			
			return modelNodeList;
		}
		return Collections.EMPTY_LIST;
    }
    
    /** this method is designed to add/remove any model objects that conflict with
    * the list of eObjects in the input list.
    */
     
    public void reconcile(List newEObjectList, Diagram diagram) {
		if( newEObjectList != null && !newEObjectList.isEmpty() ) {
			Iterator iter = newEObjectList.iterator();
	    	
            // BML 9/14/06 - Changed this method to use addChildren() & removeChildren() (rather than one at a time) to minimize 
            // the amount of property change events being fired.  Simple and safe fix.
            List newChildNodes = new ArrayList();
            
			while(iter.hasNext()) {
				EObject eObj = (EObject)iter.next();
				DiagramModelNode someModelNode = getChild(eObj);
				if( someModelNode == null ) {
					MetamodelAspect mmAspect = getUmlAspect(eObj);
		                        
					if(mmAspect instanceof UmlProperty) {
						DiagramModelNode attributeNode = new UmlAttributeNode(eObj, (UmlProperty)mmAspect);
						attributeNode.setParent(this);
                        newChildNodes.add(attributeNode);
					} else if(mmAspect instanceof UmlAssociation) {
						DiagramModelNode associationNode = new UmlAssociationNode(eObj, (UmlAssociation)mmAspect);
						associationNode.setParent(this);
                        newChildNodes.add(associationNode);
					} else if(mmAspect instanceof UmlOperation) {
						DiagramModelNode operationNode = new UmlOperationNode(eObj, (UmlOperation)mmAspect);
						operationNode.setParent(this);
                        newChildNodes.add(operationNode);
					} else if(mmAspect instanceof UmlClassifier) {
					   DiagramModelNode classifierNode = new UmlClassifierNode(diagram, eObj, (UmlClassifier)mmAspect);
					   classifierNode.setParent(this);
                       newChildNodes.add(classifierNode);
					}
				}
			}
			if( ! newChildNodes.isEmpty() ) {
                addChildren(newChildNodes);
            }
            
			// Now we need to walk through the current list and find any objects that don't belong
			List currentEObjectList = new ArrayList(getEObjectChildren());
			Iterator currentIter = currentEObjectList.iterator();
            
            List oldChildNodes = new ArrayList();
            
			EObject nextEObj = null;
			DiagramModelNode nextNode = null;
			while( currentIter.hasNext() ) {
				nextEObj = (EObject)currentIter.next();
				if( ! newEObjectList.contains(nextEObj) ) {
					nextNode = getChild(nextEObj);
					if( nextNode != null ) {
                        oldChildNodes.add(nextNode);
					}
				}
				
			}
			if( ! oldChildNodes.isEmpty() ) {
                removeChildren(oldChildNodes, false);
            }
			reorderChildren(newEObjectList);
			
		} else {
			// make sure that we remove all children here.
			List currentChildren = new ArrayList(getChildren());
            removeChildren(currentChildren, false);
		}
		

    }
    
	/**
	 * Helper method to get the UmlAspect given an EObject
	 */
	private MetamodelAspect getUmlAspect(EObject eObject) {
        
		return DiagramUiPlugin.getDiagramAspectManager().getUmlAspect( eObject );   
	}
	
	// Used to alert the Edit Part that the children have been modified
	// and a refreshChildren() is needed.
	@Override
    public void fireStructureChange(String prop, Object child) {
		super.fireStructureChange(prop, child);
		// Now we need to tell the parent classifier that it children has changed...
		((AbstractDiagramModelNode)getParent()).fireStructureChange(prop,null);
	}

    @Override
    public void setSize(Dimension theDimension) {
        int newHeight = height;
        int newWidth = width;
        
        if( isHeightFixed()  ) {
            newHeight = getFixedHeight(); //DiagramModelNode.DEFAULT_FIXED_HEIGHT;
        } else {
            newHeight = theDimension.height;
        }
        newWidth = theDimension.width;
        if( newWidth != width || newHeight != height ) {
            width = newWidth;
            height = newHeight;
            firePropertyChange(DiagramUiConstants.DiagramNodeProperties.SIZE, null, theDimension);
        }
    }
}
