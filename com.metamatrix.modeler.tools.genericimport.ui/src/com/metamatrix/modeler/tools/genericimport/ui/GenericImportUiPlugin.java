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

package com.metamatrix.modeler.tools.genericimport.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;


/**
 * The GenericImportUiPlugin
 */
public class GenericImportUiPlugin extends AbstractUiPlugin implements UiConstants {

	//The shared instance.
    private static GenericImportUiPlugin plugin;
    
    /**
     * The constructor.
     */
    public GenericImportUiPlugin() {
        plugin = this;
    }

    /**
     * Returns the shared instance.
     */
    public static GenericImportUiPlugin getDefault() {
        return GenericImportUiPlugin.plugin;
    }

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);   // This must be called to initialize the platform logger! 
        
        init();
    }

    private void init() {
        // does nothing yet
    }
    
    public void logMessage(int severity, String message) {
    	Util.log(severity,message);
    }
    
    public void logMessage(IStatus status) {
    	Util.log(status);
    }
 
    public void logMessage(int severity, Throwable t, String message) {
        Util.log(severity, t, message);
    }
    
    //============================================================================================================================
    // AbstractUiPlugin Methods

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        return null;
    }

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    public ActionService getActionService(IWorkbenchPage page) {
        return GenericImportUiPlugin.getDefault().getActionService(page);
    }

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }
}