/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit;


/**
 * This interface is to be implemented by components that generate additional
 * artifacts (e.g., files) to be included in the VDB definition archives.
 *
 * @since 4.2
 */
public interface VdbArtifactGenerator {

    /**
	 * Execute the generator, using the supplied context.
	 * 
	 * @param context the VDB context that contains all information about the VDB definition, including models; never null
	 * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
	 */
    void execute( VdbGenerationContext context );

}
