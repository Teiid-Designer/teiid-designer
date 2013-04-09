/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.refactor.AbstractRefactorModelHandler;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.transformation.SqlTransformation;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;



/**
 * This class provides the transformation plugin a mechanism to affect changes internal to virtual models during
 * Model refactoring operations. (i.e. Move & Rename)
 * 
 * In particular, a Rename of a model could result in mis-named models in USER SQL.
 * 
 *
 *
 * @since 8.0
 */
public class TransformationRefactorModelHandler extends AbstractRefactorModelHandler {

	@Override
	public void helpUpdateModelContentsForDelete(Collection<IResource> deletedResourcePaths, Collection<IResource> directDependentResources, IProgressMonitor monitor) {
		
		for( IResource nextRes : directDependentResources ) {
			
			try {
				cleanUpTransformationReferences(nextRes, deletedResourcePaths, monitor);
			} catch (ModelWorkspaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 
	}
	
	protected boolean cleanUpTransformationReferences(IResource depResource, Collection deletedPaths, IProgressMonitor monitor)
			throws ModelWorkspaceException {
		
		ModelResource mr = ModelUtil.getModelResource((IFile)depResource, true);
		final Resource r = mr.getEmfResource();
		
		if( mr.getModelType() != ModelType.VIRTUAL_LITERAL) {
			return false;
		}
		
		
		
		// Process all transformations in the TransformationContainer
		final List transformations = ((EmfResource) r).getModelContents().getTransformations();
		
		for (Iterator i = transformations.iterator(); i.hasNext();) {
			EObject eObj = (EObject) i.next();
			if (eObj instanceof SqlTransformationMappingRoot) {
				SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot) eObj;
				SqlTransformation helper = (SqlTransformation) mappingRoot
						.getHelper();
				SqlTransformation nested = null;
				if (helper != null) {
					for (Iterator j = helper.getNested().iterator(); j
							.hasNext();) {
						eObj = (EObject) j.next();
						if (eObj instanceof SqlTransformation) {
							nested = (SqlTransformation) eObj;
							break;
						}
					}
				}
				if (nested != null) {
					// Check inputs
					List<EObject> delObjList = new ArrayList<EObject>();
					List inputs = helper.getMapper().getInputs();
					
					for (Iterator j = inputs.iterator(); j.hasNext();) {
						EObject input = (EObject)j.next();
						if( isDeletedInput(input, deletedPaths)) {
							delObjList.add(input);
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
			}
		}
		
		
		return true;
	}
	
	private boolean isDeletedInput(EObject input, Collection deletedPaths) {
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
