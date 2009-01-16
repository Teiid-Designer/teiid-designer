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

package com.metamatrix.modeler.relationship.ui.diagram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.PresentationEntity;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.IDiagramProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.PluginConstants;

/**
 * <p>TransformationDiagramContentProvider is the specific ContentProvider for all package diagram model
 *  objects inside ModelResources.</p>
 * <p>TransformationDiagramContentProvider implements ITreeContentProvider because this interface has the right
 * methods for the functionality this class provides.</p>
 */
final public class RelationshipDiagramContentProvider implements ITreeContentProvider, IDiagramProvider {

	// ===========================================
	// Static

	private static final Object[] NO_CHILDREN = new Object[0];

	// ===========================================
	// Constructors

	/**
	 * Construct an instance of ModelObjectContentProvider.
	 */
	public RelationshipDiagramContentProvider() {
	}
    
	// ===========================================
	// Methods

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public synchronized Object[] getChildren(Object parentElement) {

		Object[] children = NO_CHILDREN;
            
		if ( parentElement instanceof PresentationEntity ) {
			// then NO_CHILDREN is the right answer

		} else if ( parentElement instanceof EObject ) {
                        
			// get resource, and get Diagrams for resource.
			EObject eObject = (EObject) parentElement;
			ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                
			List diagramList = new ArrayList();
			ArrayList allChildren = new ArrayList();
			
			if ( modelResource != null && eObject instanceof RelationshipFolder ) {
				// make sure this object contains a Relationship Diagram
				Diagram relDiagram =  
					DiagramUiPlugin.getDiagramTypeManager()
						.getDiagram(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID)
						.getPackageDiagramProvider()
						.getPackageDiagram(modelResource, eObject, false);
				diagramList.add(relDiagram);
			}  else if ( parentElement instanceof ModelResource ) {
				modelResource = (ModelResource) parentElement;
				
				Diagram relDiagram =  
					DiagramUiPlugin.getDiagramTypeManager()
						.getDiagram(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID)
						.getPackageDiagramProvider()
						.getPackageDiagram(modelResource, null, false);
				diagramList.add(relDiagram);
	
				if ( !diagramList.isEmpty() ) {
					allChildren.addAll(diagramList);
					children = allChildren.toArray();
				}
			}
	                
			if ( !diagramList.isEmpty() ) {
				allChildren.addAll(diagramList);
				children = allChildren.toArray();
			}
		}

		return children;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		Object result = null;
		if ( element instanceof Diagram ) {
			Object target = ((Diagram) element).getTarget();
			if ( target instanceof EObject ) {
				result = target;
			} else if ( target instanceof ModelAnnotation ) {
				// the diagram is underneath the model file node - return the IResource
				result = ModelUtilities.getModelResourceForModelObject((ModelAnnotation) target);
			}
		}
        
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		Object[] children= getChildren(element);
		return (children != null) && children.length > 0;
	}
    

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramProvider#canDelete(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public boolean canDelete(Diagram diagram) {
		return false;
	}
}
