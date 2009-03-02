/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.Plugin;


/**
 * Base class for Plugins.  Sub-classes are expected to use the PluginUtilFactory for storing their PluginUtil objects.
 */
public abstract class AbstractPlugin extends Plugin {
    
    /**
     * Template method to be implemented by sub-classes.
     */
    protected abstract PluginUtilImpl getPluginUtilImpl();
    
    /** 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        getPluginUtilImpl().initializePlatformLogger(this);   // This must be called to initialize the platform logger!  
    }
}
