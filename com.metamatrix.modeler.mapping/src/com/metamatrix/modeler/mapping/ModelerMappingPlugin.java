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

package com.metamatrix.modeler.mapping;

import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.Plugin;

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
        savePluginPreferences();
        super.stop(context);
    }
    
}
