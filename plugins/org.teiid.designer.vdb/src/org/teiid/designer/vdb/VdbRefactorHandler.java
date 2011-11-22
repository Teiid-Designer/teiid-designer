/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.modeler.core.refactor.IRefactorModelHandler;
import com.metamatrix.modeler.core.refactor.IRefactorNonModelResourceHandler;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * 
 */
public class VdbRefactorHandler implements IRefactorNonModelResourceHandler {

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.refactor.IRefactorModelHandler#helpUpdateDependentModelContents(int,
     *      com.metamatrix.modeler.core.workspace.ModelResource, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void helpUpdateDependentModelContents( int type,
                                                  ModelResource modelResource,
                                                  Map refactoredPaths,
                                                  IProgressMonitor monitor ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.refactor.IRefactorModelHandler#helpUpdateModelContents(int,
     *      com.metamatrix.modeler.core.workspace.ModelResource, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void helpUpdateModelContents( int type,
                                         ModelResource refactoredModelResource,
                                         Map refactoredPaths,
                                         IProgressMonitor monitor ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.refactor.IRefactorModelHandler#helpUpdateModelContentsForDelete(java.util.Collection,
     *      java.util.Collection, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void helpUpdateModelContentsForDelete( Collection<Object> deletedResourcePaths,
                                                  Collection<Object> directDependentResources,
                                                  IProgressMonitor monitor ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.refactor.IRefactorNonModelResourceHandler#processNonModel(int,
     *      org.eclipse.core.resources.IResource, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void processNonModel( int type,
                                 IResource refactoredResource,
                                 Map refactoredPaths,
                                 IProgressMonitor monitor ) {
        // only care about renames
        if ((type == IRefactorModelHandler.RENAME) && (refactoredResource.getType() == IResource.FILE)
                && Vdb.FILE_EXTENSION_NO_DOT.equals(((IFile)refactoredResource).getFileExtension())) {
            // just save VDB to get new manifest written out
            Vdb renamedVdb = new Vdb((IFile)refactoredResource, monitor);
            renamedVdb.save(monitor);
        }
    }

}
