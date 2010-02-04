/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * Initializes the logging, internationalization(i18n), and debugging facilities.
 * 
 * @since 4.1
 */
public final class RosePlugin extends Plugin implements IRoseConstants {
    //============================================================================================================================
    // Static Variables

    /**
     * The shared instance of this class.
     * 
     * @since 4.1
     */
    private static RosePlugin plugin;

    //============================================================================================================================
    // Static Methods

    /**
     * Returns the shared instance of this class.
     * 
     * @return
     * @since 4.0
     */
    public static RosePlugin getDefault() {
        return RosePlugin.plugin;
    }

    //============================================================================================================================
    // Constructors

    /**
     * @since 4.1
     */
    public RosePlugin() {
        // Save this instance to be shared via the getDefault() method.
        RosePlugin.plugin = this;
    }

    //============================================================================================================================
    // Overridden Methods

    /** 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        // Initialize logging/i18n/debugging utility
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
    }
}
