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

package com.metamatrix.modeler.vdb.ui;

import org.osgi.framework.BundleContext;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IWorkbenchPage;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.AbstractActionService;
import com.metamatrix.ui.actions.ActionService;


/** 
 * @since 4.2
 */
public class VdbUiPlugin extends AbstractUiPlugin implements VdbUiConstants {
    //============================================================================================================================
    // Static Variables

    /**
     * The shared instance of this class.
     * 
     * @since 4.2
     */
    private static VdbUiPlugin plugin;

    //============================================================================================================================
    // Static Methods

    /**
     * Returns the shared instance of this class.
     * 
     * @return
     * @since 4.2
     */
    public static VdbUiPlugin getDefault() {
        return VdbUiPlugin.plugin;
    }

    /**
     * Has the benign side-effect of adding a section for the specified object to the dialog settings if that section does not
     * already exist.
     * 
     * @param object
     * @return The dialog settings for the specified object.
     * @since 4.2
     */
    public static IDialogSettings getDialogSettings(final Object object) {
        // Get dialog settings, creating section if necessary
        final IDialogSettings settings = getDefault().getDialogSettings();
        final String name = object.getClass().getName();
        IDialogSettings section = settings.getSection(name);
        if (section == null) {
            section = settings.addNewSection(name);
        }
        return section;
    }

    //============================================================================================================================
    // Constructors

    /**
     * @since 4.2
     */
    public VdbUiPlugin() {
        // Save this instance to be shared via the getDefault() method.
        VdbUiPlugin.plugin = this;
    }

    //============================================================================================================================
    // Overridden Methods

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        // Initialize logging/i18n/debugging utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }

    //============================================================================================================================
    // Utility Methods

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }
    
    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        return new AbstractActionService(this, page) {
            public IAction getDefaultAction(String theActionId) {
                return null;
            }
        };
    }
}
