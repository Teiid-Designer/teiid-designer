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
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @author mdrilling
 *
 * Model Node for UML Association.
 */
public class UmlAssociationNode extends UmlModelNode {
    public static final int SOURCE = 0;
    public static final int TARGET = 1;
    
    public UmlAssociationNode( EObject modelObject, UmlAssociation aspect ) {
        super(modelObject, aspect );
    }
    
    @Override
    public String getName() {
		if( getModelObject() != null )
        	return aspect.getSignature(getModelObject(),UmlAssociation.SIGNATURE_NAME);
		return null;
    }
    
    @Override
    public void setName(String name) {
//        aspect.setSignature(getModelObject(),name);
        ModelObjectUtilities.rename(getModelObject(), name, this);
//        super.setName(name);
    }
    
    @Override
    public String toString() {
        return "UmlAssociationNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public String getSignature() {
		if( getModelObject() != null )
        	return ((UmlAssociation)aspect).getSignature(getModelObject(),UmlAssociation.SIGNATURE_NAME);
		return null;
    }
    public void refreshForNameChange(){
        if( getDiagramModelObject() != null ) {
            String oldName = "xxxXxxx"; //$NON-NLS-1$
            String signature = getSignature();
            
            getDiagramModelObject().setName(signature);
			firePropertyChange(DiagramNodeProperties.NAME, oldName, signature);
		} else
			firePropertyChange(DiagramNodeProperties.NAME, null, null);
    }
    
    
    @Override
    public List getDependencies() {
        List dependentObjects = new ArrayList();
        // we have a UmlAssociation here  let's get some end info here...

		if( getModelObject() != null ) {
	        UmlAssociation umlAspect = (UmlAssociation)aspect;
	        EObject targetEnd = umlAspect.getEnd(getModelObject(), TARGET);
	        EObject sourceEnd = umlAspect.getEnd(getModelObject(), SOURCE);
	        if( targetEnd == null || targetEnd.equals(getModelObject()) ) {
	            if( sourceEnd != null ) {
	                dependentObjects.add(sourceEnd);
	            }
	        } else {
	            dependentObjects.add(targetEnd);
	        }
	
	        // Add feature properties.
	        if( targetEnd != null ) {
	            List featureEObjects = ModelObjectUtilities.getFeaturePropertyList(targetEnd);
	            dependentObjects.addAll(featureEObjects);
	        }
	        if( sourceEnd != null ) {
	            List featureEObjects = ModelObjectUtilities.getFeaturePropertyList(sourceEnd);
	            dependentObjects.addAll(featureEObjects);
	        }
	        
	        if( targetEnd == null && sourceEnd == null ) {
	            List featureEObjects = ModelObjectUtilities.getFeaturePropertyList(getModelObject());
	            dependentObjects.addAll(featureEObjects);
	        }
		}
        if( dependentObjects.isEmpty() )
        	return Collections.EMPTY_LIST;
        
        return dependentObjects;
    }
    
}
