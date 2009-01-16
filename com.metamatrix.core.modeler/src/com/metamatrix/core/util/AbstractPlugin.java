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
