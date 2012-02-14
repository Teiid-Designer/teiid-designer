/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.wsdl;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * WsdlPlugin is the plugin class for com.metamatrix.modeler.mapping
 */
public class WsdlPlugin extends Plugin implements PluginConstants {

    // =========================================================
    // Static

    public static WsdlPlugin INSTANCE;

    public static WsdlPlugin getDefault() {
        return INSTANCE;
    }

    // =========================================================
    // Constructor

    /**
     * Construct an instance of ModelerXmlPlugin.
     */
    public WsdlPlugin() {
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
