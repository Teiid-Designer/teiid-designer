/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * MergeProcessor
 */
public interface MergeProcessor {

    /**
     * Perform the merge, and return the status denoting whether the
     * execution was successful.
     * @param monitor the progress monitor; may be null
     * @return the status of the execution that contains any errors or warnings that were encountered
     * during the execution
     */
    public IStatus execute( IProgressMonitor monitor );
    
    /**
     * Optional method that is used when multiple MergeProcessors are used together (in sequence) to work
     * on multiple models (see {@link com.metamatrix.modeler.compare.generator.CompositeModelGenerator}).
     * When such models have references between them, the merge processor may have to reresolve some references
     * (e.g., after another MergeProcessor has merged some models that are referenced by this processor's model).
     * @param monitor
     * @since 4.2
     */
    public void reresolve( IProgressMonitor monitor);
    
    /**
     * Close this processor and release any resources that have been acquired.
     * Once closed, the processor may not be used again.
     */
    public void close();

}
