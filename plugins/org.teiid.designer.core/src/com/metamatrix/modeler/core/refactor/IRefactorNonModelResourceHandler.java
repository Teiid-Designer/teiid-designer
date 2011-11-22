/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A <code>IRefactorNonModelResourceHandler</code> will proces model-related files that do not have a <code>ModelResource</code>.
 */
public interface IRefactorNonModelResourceHandler extends IRefactorModelHandler {

    /**
     * @param type the type of refactor operation
     * @param refactoredResource the resource involved (cannot be <code>null</code>)
     * @param refactoredPaths the before (key) and after (value) resource path
     * @param monitor the progress monitor (can be <code>null</code>)
     * @throws Exception if there is a problem processing the resource
     */
    void processNonModel( int type,
                          IResource refactoredResource,
                          Map refactoredPaths,
                          IProgressMonitor monitor ) throws Exception;

}
