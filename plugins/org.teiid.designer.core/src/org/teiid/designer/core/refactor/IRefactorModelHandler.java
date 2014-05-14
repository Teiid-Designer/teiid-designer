/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.refactor;

import java.util.Collection;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * This interface specifies methods to be used specifically by Refactor actions to perform additional updates to 
 * models as a result of the refactor operation.
 * 
 *
 *
 * @since 8.0
 */
public interface IRefactorModelHandler {

    /**
     * Type of refactoring being performed
     */
    public enum RefactorType {
	    RENAME,
	    MOVE,
	    DELETE;
    }

	/**
	 * Method which delegates to all handlers the ability to update or perform internal refactoring for the deleted models
	 * 
	 * @param deletedResourcePaths
	 * @param directDependentResources
	 * @param monitor
	 */
	void helpUpdateModelContentsForDelete(Collection<IResource> deletedResourcePaths, Collection<IResource> directDependentResources, IProgressMonitor monitor);
	
	/**
	 * Method to allow approving the refactoring before execution.
	 * 
	 * @param refactorType
	 * @param refactoredResource
	 * @param monitor
	 * @return true if preprocessing confirms that refactoring should continue
	 */
	boolean preProcess(RefactorType refactorType, final IResource refactoredResource, IProgressMonitor monitor);
	
	/**
	 * Method to allow post-processing after refactoring
	 * 
	 * @param refactorType
	 * @param refactoredResource
	 * @throws Exception
	 */
	void postProcess(RefactorType refactorType, final IResource refactoredResource) throws Exception;
}
