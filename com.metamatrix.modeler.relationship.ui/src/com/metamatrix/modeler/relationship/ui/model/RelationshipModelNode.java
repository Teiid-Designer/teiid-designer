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

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipModelNode extends UmlModelNode {
	public static final int TARGET_NODE = 0;
	public static final int SOURCE_NODE = 1;
	boolean isTarget = false;
	boolean isComplete = true;
    
    public RelationshipModelNode(String name) {
		super(null, null, null);
    	setName(name);
    }
    
	public RelationshipModelNode(Diagram diagramModelObject, EObject modelObject, int nodeType) {
		super( diagramModelObject, modelObject, null);
		
		if( nodeType == TARGET_NODE )
			isTarget = true;
			
		setName(ModelerCore.getModelEditor().getName(modelObject));
	}
    
	@Override
    public String toString() {
		return "RelationshipModelNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
    public String getStereotype() {
		String someType = "UNKNOWN"; //$NON-NLS-1$
		if( getModelObject() != null && ((Relationship)getModelObject()).getType() != null) {
			someType = ((Relationship)getModelObject()).getType().getName(); 
		}
		return someType;
	}
	
	public String getSourceRoleName() {
		String sourceRole = "NO SOURCE ROLE"; //$NON-NLS-1$
		if( getModelObject() != null && ((Relationship)getModelObject()).getSourceRole() != null) {
			sourceRole = ((Relationship)getModelObject()).getSourceRole().getName();
		}
		return sourceRole;
	}
	
	public EObject getSourceRole() {
		EObject sourceRole = null;
		if( getModelObject() != null ) {
			sourceRole = ((Relationship)getModelObject()).getSourceRole();
		}
		return sourceRole;
	}
	
	public String getTargetRoleName() {
		String targetRole = "NO TARGET ROLE"; //$NON-NLS-1$
		if( getModelObject() != null && ((Relationship)getModelObject()).getTargetRole() != null) {
			targetRole = ((Relationship)getModelObject()).getTargetRole().getName();
		}
		return targetRole;
	}
	
	public EObject getTargetRole() {
		EObject targetRole = null;
		if( getModelObject() != null ) {
			targetRole = ((Relationship)getModelObject()).getTargetRole();
		}
		return targetRole;
	}
	
	public void refreshForNameChange(){
		if( getDiagramModelObject() != null ) {
			String oldName = "xxxXxxx"; //$NON-NLS-1$

			DiagramEntityAdapter.setName(getDiagramModelObject(), getName());
        
			firePropertyChange(DiagramNodeProperties.NAME, oldName, getName());
		}
	}
	
	public void refreshForPropChange(){
		refreshForNameChange();
	}
	
	@Override
    public void setName(String name) {
		ModelObjectUtilities.rename(getModelObject(), name, this);
	}
	
	@Override
    public String getName() {
		return ModelerCore.getModelEditor().getName(getModelObject());
	}
	
	public boolean isTargetNode() {
		return isTarget;
	}
	
	public void setIsComplete(boolean completeness) {
		isComplete = completeness;
	}
	
	public boolean getIsComplete() {
		return isComplete;
	}
	
	public void updateForButtons() {
		firePropertyChange(DiagramUiConstants.DiagramNodeProperties.BUTTONS, null, null);
	}
}
