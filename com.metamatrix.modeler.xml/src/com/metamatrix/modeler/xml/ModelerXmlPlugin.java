/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.xml;

import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.Plugin;

import com.metamatrix.core.util.PluginUtilImpl;

/**
 * ModelerXmlPlugin is the plugin class for com.metamatrix.modeler.mapping
 */
public class ModelerXmlPlugin extends Plugin implements PluginConstants {

    // =========================================================
    // Static

    public static ModelerXmlPlugin INSTANCE;

    public static ModelerXmlPlugin getDefault() {
        return INSTANCE;
    }

    // =========================================================
    // Constructor

    /**
     * Construct an instance of ModelerXmlPlugin.
     */
    public ModelerXmlPlugin() {
        INSTANCE = this;
    }
    
    // =========================================================
    // Methods

    /** 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }
}
