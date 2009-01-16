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

package com.metamatrix.core.modeler;

import java.util.ResourceBundle;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.aspects.DeclarativeTransactionManager;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * CorePlugin
 */
public class CoreModelerPlugin extends Plugin {
    //
    // Class Constants:
    //
    /**
     * The plug-in identifier of this plugin
     */
    public static final String PLUGIN_ID = "com.metamatrix.core.modeler" ; //$NON-NLS-1$

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,BUNDLE_NAME,ResourceBundle.getBundle(BUNDLE_NAME));

    /**The shared instance.*/
    private static CoreModelerPlugin plugin;

    /**TransactinManager instance used for declarative transaction management (May be NULL)*/
    private static DeclarativeTransactionManager transactionMgr;

    /**
     * Setter for the TransactionManager instance to be used by aspects for declarative txn management.
     * @param txnManager
     */
    public static void setTransactionManager(final DeclarativeTransactionManager txnManager) {
        transactionMgr = txnManager;
    }

    /**
     * Accessor for the TransactionManager instance to be used by aspects for declarative txn management.
     * This instance may be null.
     * @return TransactionManager instance
     */
    public static DeclarativeTransactionManager getTransactionManager() {
        return transactionMgr;
    }

    /**
     * Returns the shared instance.
     * @since 4.0
     */
    public static CoreModelerPlugin getDefault() {
        return plugin;
    }

	/**
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 * @since 4.3.2
	 */
	@Override
    public void start( BundleContext context ) throws Exception {
		super.start(context);
		plugin = this;
		((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
		// Initialize the IDGenerator, which is an asynchronous call so should return quickly.
		// Calls to create IDs will block if not initialized.
		IDGenerator.getInstance();
    }

    @Override
    public void stop( BundleContext context ) throws Exception {
		super.stop(context);
		plugin = null;
	}
}
