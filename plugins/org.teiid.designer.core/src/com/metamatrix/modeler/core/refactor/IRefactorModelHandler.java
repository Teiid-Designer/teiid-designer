/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * This interface specifies methods to be used specifically by Refactor actions to perform additional updates to 
 * models as a result of the refactor operation.
 * 
 *
 */
public interface IRefactorModelHandler {
	public final static int RENAME = 0;
	public final static int MOVE = 1;
	public final static int DELETE = 2;
	
	/**
	 * Method which delegates to all handlers the ability to update models that are dependent on the refactored models
	 * 
	 * @param type the type of the refactor operations (see <code>IRefactorModelHandler</code>
	 * @param modelResource the dependent model
	 * @param refactoredPaths a Map containing original and new model paths
	 * @param monitor the ProgressMonitor
	 */
	void helpUpdateDependentModelContents(int type, ModelResource modelResource, Map refactoredPaths, IProgressMonitor monitor);
	
	/**
	 * Method which delegates to all handlers the ability to update or perform internal refactoring for the refactored models
	 * 
	 * @param type
	 * @param refactoredModelResource
	 * @param refactoredPaths
	 * @param monitor
	 */
	void helpUpdateModelContents(int type, ModelResource refactoredModelResource, Map refactoredPaths, IProgressMonitor monitor);
	
	void helpUpdateModelContentsForDelete(Collection<Object> deletedResourcePaths, Collection<Object> directDependentResources, IProgressMonitor monitor);
	
}
