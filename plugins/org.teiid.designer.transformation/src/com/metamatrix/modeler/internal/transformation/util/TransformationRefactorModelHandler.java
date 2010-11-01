/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.refactor.IRefactorModelHandler;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.mapping.factory.MappingRefactorModelHandler;
import com.metamatrix.modeler.transformation.TransformationPlugin;


/**
 * This class provides the transformation plugin a mechanism to affect changes internal to virtual models during
 * Model refactoring operations. (i.e. Move & Rename)
 * 
 * In particular, a Rename of a model could result in mis-named models in USER SQL.
 * 
 *
 */
public class TransformationRefactorModelHandler extends
		MappingRefactorModelHandler {

	public TransformationRefactorModelHandler() {
	}
	
	@Override
	public void helpUpdateModelContents(int type, ModelResource modelResource,
			Map refactoredPaths, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(refactoredPaths.values(), "refactoredPaths"); //$NON-NLS-1$
		// Need to fix any queries within a virtual model that has transformation sources internal to the same model
		
		try {
			switch( type ) {
				case IRefactorModelHandler.RENAME: {
                    boolean sqlChanged = regenerateUserSql(modelResource, monitor, refactoredPaths);
                    
                    if( sqlChanged ) {
	                    // Send notification for transformation roots to invalidate any transformation cache
	                    final Resource resrc = modelResource.getEmfResource();
	                    if (resrc instanceof EmfResource) {
	                        final List xformations = ((EmfResource)resrc).getModelContents().getTransformations();
	                        for (final Iterator rootIter = xformations.iterator(); rootIter.hasNext();) {
	                            final TransformationMappingRoot root = (TransformationMappingRoot)rootIter.next();
	                            if( root instanceof SqlTransformationMappingRoot ) {
		                            final Notification notification = new ENotificationImpl(
		                                                                                    (InternalEObject)root,
		                                                                                    Notification.SET,
		                                                                                    TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TARGET,
		                                                                                    refactoredPaths.keySet(),
		                                                                                    refactoredPaths.values());
		                            root.eNotify(notification);
	                            }
	                        }
	                    }
                    }
				}break;
				case IRefactorModelHandler.DELETE: {
					
				}break;
				case IRefactorModelHandler.MOVE: {
					
				}break;
				default: break;
			}
		} catch (ModelWorkspaceException e) {
			TransformationPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
		}

	}
	
	@Override
	public void helpUpdateDependentModelContents(int type, ModelResource modelResource,
			Map refactoredPaths, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(refactoredPaths.values(), "refactoredPaths"); //$NON-NLS-1$
		
		helpUpdateModelContents(type, modelResource, refactoredPaths, monitor);
	}

	protected boolean regenerateUserSql(ModelResource modelResource,
			IProgressMonitor monitor, Map refactoredPaths)
			throws ModelWorkspaceException {
		final Resource r = modelResource.getEmfResource();
		
		boolean sqlChanged = false;
		
		// If the model resource being represents a virtual model with
		// transformations ...
		if (r instanceof EmfResource
				&& ((EmfResource) r).getModelType() == ModelType.VIRTUAL_LITERAL) {

			// Ensure that this model resource is loaded so that we can retrieve
			// and update its contents
			if (!r.isLoaded()) {
				Map options = (r.getResourceSet() != null ? r.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
				try {
					r.load(options);
				} catch (IOException e) {
					TransformationPlugin.Util.log(IStatus.ERROR, e, e
							.getLocalizedMessage());
					return false;
				}
			}

			// Process all transformations in the TransformationContainer
			final List transformations = ((EmfResource) r).getModelContents()
					.getTransformations();
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
							}
						}
					}
					if (nested != null) {
						// Convert select SQL
						String userFormSql = nested.getSelectSql();
						String convertedSql = null;
						if (!CoreStringUtil.isEmpty(userFormSql)) {
							convertedSql = refactorUserSql(userFormSql,
									refactoredPaths);
							if( !userFormSql.equalsIgnoreCase(convertedSql)) {
								nested.setSelectSql(convertedSql);
								sqlChanged = true;
							}
						}

						// Convert insert SQL
						userFormSql = nested.getInsertSql();
						if (!CoreStringUtil.isEmpty(userFormSql)) {
							convertedSql = refactorUserSql(userFormSql,
									refactoredPaths);
							if( !userFormSql.equalsIgnoreCase(convertedSql)) {
								nested.setInsertSql(convertedSql);
								sqlChanged = true;
							}
						}

						// Convert update SQL
						userFormSql = nested.getUpdateSql();
						if (!CoreStringUtil.isEmpty(userFormSql)) {
							convertedSql = refactorUserSql(userFormSql,
									refactoredPaths);
							if( !userFormSql.equalsIgnoreCase(convertedSql)) {
								nested.setUpdateSql(convertedSql);
								sqlChanged = true;
							}
						}

						// Convert delete SQL
						userFormSql = nested.getDeleteSql();
						if (!CoreStringUtil.isEmpty(userFormSql)) {
							convertedSql = refactorUserSql(userFormSql,
									refactoredPaths);
							if( !userFormSql.equalsIgnoreCase(convertedSql)) {
								nested.setDeleteSql(convertedSql);
								sqlChanged = true;
							}
						}
					}
				}
			}
		}
		
		return sqlChanged;
	}
	
	@Override
	public void helpUpdateModelContentsForDelete(Collection<Object> deletedResourcePaths, Collection<Object> directDependentResources, IProgressMonitor monitor) {
		
		for( Object nextObj : directDependentResources ) {
			IResource nextRes = (IResource)nextObj;
			
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
