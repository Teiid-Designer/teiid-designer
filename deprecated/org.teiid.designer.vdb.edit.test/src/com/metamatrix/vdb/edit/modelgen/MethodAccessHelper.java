/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.modelgen;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.ecore.resource.Resource;


/** 
 * @since 4.1
 */
public class MethodAccessHelper {

    /**
     * Public method to expose protected execute method of the generator, for access to tests
     * outside of the plugin.
     */
    public MultiStatus execute(final MaterializedViewModelGenerator generator, final Resource inputResource) {
        return generator.execute(inputResource);
    }
}
