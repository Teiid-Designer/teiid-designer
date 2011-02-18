/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.diagram;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * MappingDiagramProvider
 */
public class RelationshipDiagramProvider implements IPackageDiagramProvider {

	/**
	 * Construct an instance of MappingDiagramProvider.
	 * 
	 */
	public RelationshipDiagramProvider() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider#getPackageDiagram(com.metamatrix.modeler.core.workspace.ModelResource, org.eclipse.emf.ecore.EObject)
	 */
	public Diagram getPackageDiagram(ModelResource modelResource, EObject eObject, boolean forceCreate) {
		Diagram relationshipDiagram = null;
		
		if( RelationshipDiagramUtil.isRelationshipModelResource(modelResource)) {
			relationshipDiagram = RelationshipDiagramUtil.getRelationshipDiagram(modelResource, eObject, this, forceCreate);
		}
		return relationshipDiagram;
	}
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider#getPackageDiagram(java.lang.Object)
	 */
	public Diagram getPackageDiagram(Object targetObject, boolean forceCreate) {
		Diagram relationshipDiagram = null;
		        
		if( targetObject instanceof EObject) {
			ModelResource modelResource =  null;
			EObject eObject = (EObject)targetObject;
			modelResource =  ModelUtilities.getModelResourceForModelObject(eObject);
			if( modelResource != null && RelationshipDiagramUtil.isRelationshipModelResource(modelResource) )
				relationshipDiagram = RelationshipDiagramUtil.getRelationshipDiagram(modelResource, eObject, this, forceCreate);
		}
        
		return relationshipDiagram;
	}
    

}
