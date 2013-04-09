/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.refactor.AbstractRefactorModelHandler;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.diagram.DiagramEntity;


/**
 * This class provides the mapping plugin a mechanism to affect changes internal to any model during
 * Model refactoring operations. (i.e. Move & Rename)
 * 
 * In particular, a Rename of model could result in mis-named hrefs in diagram entity references that could become
 * stale.
 * 
 *
 *
 * @since 8.0
 */
public class DiagramRefactorModelHandler extends AbstractRefactorModelHandler {
	
	@Override
	public boolean preProcess(RefactorType refactorType, IResource refactoredResource, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(refactoredResource, "modelResource"); //$NON-NLS-1$
		
		try {
			if(! ModelUtil.isXmiFile(refactoredResource))
			    return true;
			    
			ModelResource modelResource = ModelUtil.getModel(refactoredResource);
			    
			for( Object diagram : modelResource.getModelDiagrams().getDiagrams() ) {
			    DiagramEntityManager.cleanDiagramEntities((Diagram)diagram);
					
			    DiagramEntityManager.cleanUpDiagram((Diagram)diagram);
			}
			
		} catch (ModelWorkspaceException e) {
			DiagramUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
			return false;
		}

		return true;
	}
	
    @Override
	public void helpUpdateModelContentsForDelete(
			Collection<IResource> deletedResourcePaths,
			Collection<IResource> directDependentResources,
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
