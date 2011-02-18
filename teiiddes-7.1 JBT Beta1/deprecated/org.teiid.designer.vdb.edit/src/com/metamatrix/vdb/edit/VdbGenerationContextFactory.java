/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @since 5.0
 */
public interface VdbGenerationContextFactory {

    VdbGenerationContext createVdbGenerationContext( VdbGenerationContextParameters parameters,
	                                                 IProgressMonitor monitor );

}
