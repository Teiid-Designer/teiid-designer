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

package com.metamatrix.modeler.relationship.ui.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.diagram.RelationshipDiagramUtil;

/**
 * @author mdrilling
 *
 * Model Node for UML Package.
 */
public class RelationshipFolderModelNode extends UmlModelNode {

	public RelationshipFolderModelNode( Diagram diagramModelObject, EObject modelObject ) {
		super( diagramModelObject, modelObject, null );
	}

	@Override
    public String getName() {
		return ModelerCore.getModelEditor().getName(getModelObject());
	}
    
	public void refreshForNameChange(){
		if( getDiagramModelObject() != null ) {
			String oldName = "xxxXxxx"; //$NON-NLS-1$

			DiagramEntityAdapter.setName(getDiagramModelObject(), getName());
        
			firePropertyChange(DiagramNodeProperties.NAME, oldName, getName());
		}
	}
    
	@Override
    public String toString() {
		return "RelationshipFolderModelNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
    
	@Override
    public String getLocation() {
		return getPath();
	}
    
	@Override
    public String getPath() {
		// First check to see if package diagram. if so, we return null
		EObject diagramEObject = getParent().getModelObject();
		if( !RelationshipDiagramUtil.isRelationshipDiagram(diagramEObject)) {
			// Check to see if the modelResource for this class is same as diagram.
			if( diagramEObject != null &&
				! ModelUtilities.areModelResourcesSame(getModelObject(), diagramEObject) ) {
					return ModelObjectUtilities.getTrimmedFullPath(getModelObject());
				}
            
			IPath relativePath = ModelerCore.getModelEditor().getModelRelativePath(getModelObject());
			String relativePathString = relativePath.toString();
			int indexOfLastDot = relativePathString.lastIndexOf('/');
			if( indexOfLastDot >= 0 )
				return relativePathString.substring(0, indexOfLastDot);
		}
        
		return null;
	}
	
	@Override
    public void setName(String name) {
//		  aspect.setSignature(getModelObject(),name);
		ModelObjectUtilities.rename(getModelObject(), name, this);
//		  super.setName(name);
	}
}
