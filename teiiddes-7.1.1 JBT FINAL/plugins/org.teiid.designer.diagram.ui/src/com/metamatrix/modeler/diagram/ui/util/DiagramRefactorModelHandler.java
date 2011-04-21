/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.refactor.IRefactorModelHandler;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * This class provides the mapping plugin a mechanism to affect changes internal to any model during
 * Model refactoring operations. (i.e. Move & Rename)
 * 
 * In particular, a Rename of model could result in mis-named hrefs in diagram entity references that could become
 * stale.
 * 
 *
 */
public class DiagramRefactorModelHandler implements IRefactorModelHandler {

	public DiagramRefactorModelHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void helpUpdateDependentModelContents(int type, ModelResource modelResource,
			Map refactoredPaths, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(refactoredPaths.values(), "refactoredPaths"); //$NON-NLS-1$
		
		
		try {
			if( ModelUtil.isXmiFile(modelResource.getCorrespondingResource())) {
				for( Object diagram : modelResource.getModelDiagrams().getDiagrams() ) {
					DiagramEntityManager.cleanDiagramEntities((Diagram)diagram);
					
					DiagramEntityManager.cleanUpDiagram((Diagram)diagram);
				}
			}
		} catch (ModelWorkspaceException e) {
			DiagramUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
		}

	}
	
	@Override
	public void helpUpdateModelContents(int type, ModelResource modelResource,
			Map refactoredPaths, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(refactoredPaths.values(), "refactoredPaths"); //$NON-NLS-1$
		
		try {
			if( ModelUtil.isXmiFile(modelResource.getCorrespondingResource())) {
				for( Object diagram : modelResource.getModelDiagrams().getDiagrams() ) {
					DiagramEntityManager.cleanDiagramEntities((Diagram)diagram);
					
					DiagramEntityManager.cleanUpDiagram((Diagram)diagram);
				}
			}
		} catch (ModelWorkspaceException e) {
			DiagramUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
		}

	}
	
    @Override
	public void helpUpdateModelContentsForDelete(
			Collection<Object> deletedResourcePaths,
			Collection<Object> directDependentResources,
			IProgressMonitor monitor) {
		
    	// For each resource, clean up diagram references from diagram entity model objects
		for( Object nextObj : directDependentResources ) {
			IResource nextRes = (IResource)nextObj;
			
			try {
				if( !ModelUtil.isXmiFile(nextRes) ) {
					cleanUpDiagramReferences(nextRes, deletedResourcePaths, monitor);
				}
			} catch (ModelWorkspaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 
	}
    
	protected boolean cleanUpDiagramReferences(IResource depResource, Collection deletedPaths, IProgressMonitor monitor)
	throws ModelWorkspaceException {

		ModelResource mr = ModelUtil.getModelResource((IFile)depResource, true);
		final Resource r = mr.getEmfResource();
		
		if( !ModelUtil.isXmiFile(mr.getCorrespondingResource()) || mr.getModelType() != ModelType.VIRTUAL_LITERAL) {
			return false;
		}
		
		// Process all transformations in the TransformationContainer
		final List diagrams = ((EmfResource) r).getModelContents().getDiagrams();
		for (Iterator i = diagrams.iterator(); i.hasNext();) {
			Diagram diagram = (Diagram) i.next();
			// Check inputs
			List<EObject> delObjList = new ArrayList<EObject>();
			
            List<EObject> diagramChildren = diagram.eContents();
            
            // For each resource, clean up diagram references from diagram entity model objects
            // Get the entity, get it's model object and check if it's URI contains deleted Path.
            for( EObject de : diagramChildren ) {
            	if( de instanceof DiagramEntity ) {
	            	if( isDeletedObject(((DiagramEntity)de).getModelObject(), deletedPaths)) {
	            		delObjList.add(de);
	            	}
            	}
            }
			
			if( !delObjList.isEmpty() ) {
				try {
					ModelerCore.getModelEditor().delete(delObjList);
				} catch (ModelerCoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	private boolean isDeletedObject(EObject input, Collection deletedPaths) {
		if( input.eIsProxy()) {
			return true;
		} else {
			for( Object nextPath : deletedPaths ) {
				IPath delPath = (IPath)nextPath;
				
				URI uri = ModelerCore.getModelEditor().getUri(input);
				if( uri.path().contains(delPath.makeRelative().toString())) {
					return true;
				}
			}
		}
		
		return false;
	}
}
