/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import org.osgi.framework.Bundle;

/**
 * MappingAdapterDescriptorImpl
 */
public class MappingAdapterDescriptorImpl
    extends ExtensionDescriptorImpl
    implements MappingAdapterDescriptor {

    /**
	 * Construct an instance of MappingAdapterDescriptorImpl.
	 * 
	 * @param id
	 * @param className
	 * @param bundle
	 */
    public MappingAdapterDescriptorImpl( final Object id, final String className,
                                              final Bundle bundle ) {
		super(id, className, bundle);
    }

}
