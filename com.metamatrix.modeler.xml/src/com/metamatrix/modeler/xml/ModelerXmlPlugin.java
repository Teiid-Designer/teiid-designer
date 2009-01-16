/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
