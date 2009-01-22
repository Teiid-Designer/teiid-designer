/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.builder.processor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/** 
 * Processor interface
 */
public interface Processor {
	
	/**
	 * process method
	 * @param monitor the progress monitor
	 * @return the completion status
	 */
	public IStatus process(IProgressMonitor monitor);

	/**
	 * get the resultSet row count for this processor
	 * @return the row count for this processor 
	 */
	public int getRecordCount( );
}
