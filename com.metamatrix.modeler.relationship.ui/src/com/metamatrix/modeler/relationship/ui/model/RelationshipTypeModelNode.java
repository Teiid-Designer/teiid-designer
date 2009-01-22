/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.model;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipTypeModelNode extends UmlModelNode {
//	private RelationshipTypeAspect thisAspect;
	boolean logging = true;
	    
	public RelationshipTypeModelNode(String name) {
		super(null, null, null);
		setName(name);
	}
	    
	public RelationshipTypeModelNode(Diagram diagramModelObject, EObject modelObject ) {
		super( diagramModelObject, modelObject, null);
//		thisAspect = (RelationshipTypeAspect)someAspect;
		setName(ModelerCore.getModelEditor().getName(modelObject));
	}
	    
	@Override
    public String toString() {
		return "RelationshipTypeModelNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private String getDirectionalString() {
		String dirString = " <-> "; //$NON-NLS-1$
		if( getModelObject() != null && ((RelationshipType)getModelObject()).isDirected() ) {
			dirString = " --> "; //$NON-NLS-1$
		}
		return dirString;
	}
	
	public String getRoleString() {
		String someString = "ROLES UNDEFINED"; //$NON-NLS-1$
		if( getModelObject() != null ) {
			someString = 
				getSourceRoleString()
				+ getDirectionalString()
				+ getTargetRoleString();
		}
		return someString;
	}
	
	private String getSourceRoleString() {
		String someString = "UNDEFINED"; //$NON-NLS-1$
		if( getModelObject() != null &&
			((RelationshipType)getModelObject()).getSourceRole() != null ) {
			someString = ((RelationshipType)getModelObject()).getSourceRole().getName();
		}
		return someString;
	}
	
	private String getTargetRoleString() {
		String someString = "UNDEFINED"; //$NON-NLS-1$
		if( getModelObject() != null &&
			((RelationshipType)getModelObject()).getTargetRole() != null ) {
			someString = ((RelationshipType)getModelObject()).getTargetRole().getName();
		}
		return someString;
	}
	
	public void refreshForNameChange(){
		if( getDiagramModelObject() != null ) {
			String oldName = "xxxXxxx"; //$NON-NLS-1$

			DiagramEntityAdapter.setName(getDiagramModelObject(), getName());
        
			firePropertyChange(DiagramNodeProperties.NAME, oldName, getName());
			firePropertyChange(DiagramNodeProperties.SIZE, null, null);
		}
	}
	
	@Override
    public String getName() {
		return ModelerCore.getModelEditor().getName(getModelObject());
	}
	
	@Override
    public void setName(String name) {
		ModelObjectUtilities.rename(getModelObject(), name, this);
	}
}
