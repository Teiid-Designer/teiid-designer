/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping;

import static com.metamatrix.modeler.mapping.PluginConstants.PLUGIN_ID;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * ModelerMappingPlugin is the plugin class for com.metamatrix.modeler.mapping
 */
public class ModelerMappingPlugin extends Plugin {

    // =========================================================
    // Static

    public static ModelerMappingPlugin INSTANCE;

    public static ModelerMappingPlugin getDefault() {
        return INSTANCE;
    }
    
    // =========================================================
    // Methods

    /** 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        INSTANCE = this;
    }

    /** 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     * @since 5.0.1
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        ModelerCore.savePreferences(PLUGIN_ID);
        super.stop(context);
    }
    
}
