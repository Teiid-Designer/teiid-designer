/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * DifferenceProcessor
 */
public interface DifferenceProcessor {
    
    /**
     * Return the list of {@link EObjectMatcherFactory} instances that will be used by this processor
     * during {@link #execute(IProgressMonitor) execution}.
     * @return the mapping adapters; never null
     * @see #addEObjectMatcherFactories(List)
     */
    public List getEObjectMatcherFactories();
    
    /**
     * Helper method to add to the list of {@link EObjectMatcherFactory} instances that will be used 
     * by this processor during {@link #execute(IProgressMonitor) execution}.
     * @param adapters the new mapping adapters; may not be null
     * @see #getEObjectMatcherFactories()
     */
    public void addEObjectMatcherFactories( List adapters );
    
    /**
     * Return the difference guidelines that are used.
     * @return the difference guidelines; may be null
     */
    public DifferenceGuidelines getDifferenceGuidelines();
    
    /**
     * Set the difference guidelines that are used.
     * @param guidelines the difference guidelines; may be null
     */
    public void setDifferenceGuidelines( DifferenceGuidelines guidelines );
    
    /**
     * Perform the difference analysis, and return the status denoting whether the
     * execution was successful.
     * @param monitor the progress monitor; may be null
     * @return the status of the execution that contains any errors or warnings that were encountered
     * during the execution
     */
    public IStatus execute( IProgressMonitor monitor );
    
    /**
     * Obtain the difference report.
     * @return the DifferenceReport; may be null if {@link #execute(IProgressMonitor)} has not yet
     * been called
     */
    public DifferenceReport getDifferenceReport();
    
    /**
     * Close this processor and release any resources that have been acquired.
     * Once closed, the processor may not be used again.
     */
    public void close();

}
