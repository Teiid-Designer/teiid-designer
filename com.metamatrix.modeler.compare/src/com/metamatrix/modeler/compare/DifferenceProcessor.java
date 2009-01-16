/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
