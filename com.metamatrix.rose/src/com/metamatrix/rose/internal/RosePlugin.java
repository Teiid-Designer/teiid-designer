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

package com.metamatrix.rose.internal;

import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.Plugin;

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
