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

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @author mdrilling
 *
 * Model Node for UML Attribute.
 */
public class UmlAttributeNode extends UmlModelNode {

    public UmlAttributeNode( EObject modelObject, UmlProperty aspect ) {
        super( modelObject, aspect );
    }
    
    @Override
    public String toString() {
        return "UmlAttributeNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public String getType() {
		if( getModelObject() != null )
        	return ((UmlProperty)aspect).getSignature(getModelObject(),UmlProperty.SIGNATURE_TYPE);
       	return null;
    }
    
    @Override
    public String getName() {
		if( getModelObject() != null )
        	return ((UmlProperty)aspect).getSignature(getModelObject(),UmlProperty.SIGNATURE_NAME);
       	return null;
    }
    
    @Override
    public void setName(String name) {
        try {
           boolean wasRenamed = DatatypeUtilities.renameSqlColumn(getModelObject(), name);
           if( !wasRenamed ) {
               ModelObjectUtilities.rename(getModelObject(), name, this);
           }
        } catch (ModelerCoreException theException) {
        }
    }
    
    public String getSignature() {
    	if( getModelObject() != null ) {
    	    String signature = ((UmlProperty)aspect).getSignature(getModelObject(),UmlProperty.SIGNATURE_NAME | UmlProperty.SIGNATURE_TYPE);
            return signature;
        }
       	return null;
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
    
    public void rename() {
        firePropertyChange(DiagramNodeProperties.RENAME, null, null);
    }
}
