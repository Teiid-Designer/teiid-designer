/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FocusModelNode extends UmlModelNode {
	public static final int TARGET_NODE = 0;
	public static final int SOURCE_NODE = 1;
	boolean isTarget = false;

	private String role;
	private LabelModelNode labelNode;


	public FocusModelNode(String name) {
		super(null, null, null);
		setName(name);
	}

	public FocusModelNode(Diagram diagramModelObject, EObject modelObject, int nodeType) {
		super( diagramModelObject, modelObject, null);
		if( nodeType == TARGET_NODE )
			isTarget = true;

		setName(ModelerCore.getModelEditor().getName(modelObject));
		if( getName() != null ) {
			labelNode = new LabelModelNode(getName());
		}
	}

	@Override
    public String toString() {
		return "FocusModelNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
    public String getStereotype() {
		return "Type: SomeRelationshipType"; //$NON-NLS-1$
	}

	public boolean isTargetNode() {
		return isTarget;
	}

	/**
	 * @return labelNode
	 */
	public DiagramModelNode getLabelNode() {
		return labelNode;
	}

	@Override
    public String getName() {
		return ModelerCore.getModelEditor().getName(getModelObject());
	}

	public List getToolTipStrings() {
		List returnList = new ArrayList();
		returnList.add(getName());
		if (getModelObject() != null) {
			String path = ModelerCore.getModelEditor().getFullPathToParent(getModelObject()).toString();
			if( path != null )
				returnList.add("Path: " + path); //$NON-NLS-1$

			String desc = null;
			try {
				desc = ModelerCore.getModelEditor().getDescription(getModelObject());
			} catch (ModelerCoreException e) {
				// XXX Auto-generated catch block
				e.printStackTrace();
			}
			if( desc != null )
				returnList.add("Desc: " + desc); //$NON-NLS-1$
		}
		return returnList;
	}

	public void refreshForNameChange(){
		if( labelNode != null ) {
			labelNode.setName(getName());
			labelNode.update(DiagramNodeProperties.NAME);
		}
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
