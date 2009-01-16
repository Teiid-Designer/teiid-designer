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

package com.metamatrix.modeler.xsd;

import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.Plugin;

import com.metamatrix.core.util.PluginUtilImpl;

public class ModelerXsdPlugin extends Plugin implements PluginConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public static ModelerXsdPlugin INSTANCE;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public static ModelerXsdPlugin getDefault() {
        return INSTANCE;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Construct an instance of ModelerXsdPlugin.
     * @since 5.0.2
     */
    public ModelerXsdPlugin() {
        INSTANCE = this;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 5.0.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }

}
