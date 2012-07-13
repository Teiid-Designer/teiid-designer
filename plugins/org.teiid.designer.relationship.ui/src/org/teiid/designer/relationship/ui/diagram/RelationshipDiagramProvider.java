/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.diagram;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.pakkage.IPackageDiagramProvider;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


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
	 * @See org.teiid.designer.diagram.ui.pakkage.IPackageDiagramProvider#getPackageDiagram(org.teiid.designer.core.workspace.ModelResource, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public Diagram getPackageDiagram(ModelResource modelResource, EObject eObject, boolean forceCreate) {
		Diagram relationshipDiagram = null;
		
		if( RelationshipDiagramUtil.isRelationshipModelResource(modelResource)) {
			relationshipDiagram = RelationshipDiagramUtil.getRelationshipDiagram(modelResource, eObject, this, forceCreate);
		}
		return relationshipDiagram;
	}
    
	/* (non-Javadoc)
	 * @See org.teiid.designer.diagram.ui.pakkage.IPackageDiagramProvider#getPackageDiagram(java.lang.Object)
	 */
	@Override
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
