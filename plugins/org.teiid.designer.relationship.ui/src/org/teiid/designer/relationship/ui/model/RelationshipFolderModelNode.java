/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlModelNode;
import org.teiid.designer.diagram.ui.util.DiagramEntityAdapter;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.relationship.ui.diagram.RelationshipDiagramUtil;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


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
