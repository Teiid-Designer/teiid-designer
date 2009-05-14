/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational;

import java.io.PrintStream;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

/** 
 * @since 4.3
 */
public interface CostAnalyzer {

    void setOutputStream(PrintStream outputStream);    
    
    /** 
     * Collect statistics for the specified tables
     * @return List of TableInfo
     * @since 4.3
     */
    void collectStatistics(Map tblStats, IProgressMonitor monitor) throws Exception;
}
