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

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;

/**
 * @author mdrilling
 *
 * Model Node for UML Attribute.
 */
public class UmlOperationNode extends UmlModelNode {

    public UmlOperationNode( EObject modelObject, UmlOperation aspect ) {
        super( modelObject, aspect );
    }
    
    @Override
    public String getName() {
		if( getModelObject() != null )
        	return aspect.getSignature(getModelObject(),UmlOperation.SIGNATURE_NAME);
      	return null;
    }
    
	public String getSignature() {
		if( getModelObject() != null )
			return ((UmlOperation)aspect).getSignature(getModelObject(),UmlOperation.SIGNATURE_NAME | UmlOperation.SIGNATURE_PARAMETERS | UmlOperation.SIGNATURE_RETURN);
		return null;
	}
    
    @Override
    public void setName(String name) {
        aspect.setSignature(getModelObject(),name);
//        super.setName(name);
    }

    public String getReturnType() {
		if( getModelObject() != null )
        	return aspect.getSignature(getModelObject(),UmlOperation.SIGNATURE_RETURN);
		return null;
    }
    
    @Override
    public String toString() {
        return "UmlOperationNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    public void refreshForNameChange(){
        if( getDiagramModelObject() != null && !isReadOnly()) {
            String oldName = "xxxXxxx"; //$NON-NLS-1$
			String signature = getSignature();

			DiagramEntityAdapter.setName(getDiagramModelObject(), getName());
			firePropertyChange(DiagramNodeProperties.NAME, oldName, signature);
		} else
			firePropertyChange(DiagramNodeProperties.NAME, null, null);
    }
}
