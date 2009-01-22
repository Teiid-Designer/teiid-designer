/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.Properties;
import org.osgi.framework.Bundle;
import com.metamatrix.modeler.core.ExternalResourceSetDescriptor;

/**
 * ExternalResourceSetDescriptorImpl
 */
public class ExternalResourceSetDescriptorImpl extends ExtensionDescriptorImpl implements ExternalResourceSetDescriptor {

    private Properties properties;

    /**
	 * Construct an instance of ExternalResourceSetDescriptorImpl.
	 * 
	 * @param id
	 * @param className
	 * @param bundle
	 */
    public ExternalResourceSetDescriptorImpl( final Object id,
	                                          final String className,
	                                          final Bundle bundle ) {
		super(id, className, bundle);
    }

    /**
     * @see com.metamatrix.modeler.core.ExternalResourceSetDescriptor#getProperties()
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * @param properties
     */
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
}
