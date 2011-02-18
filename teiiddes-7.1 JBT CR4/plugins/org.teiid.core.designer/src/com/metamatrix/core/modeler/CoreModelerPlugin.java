/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.modeler;

import java.util.ResourceBundle;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.teiid.core.id.IDGenerator;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.aspects.DeclarativeTransactionManager;
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
    public static final String PLUGIN_ID = "org.teiid.core.designer"; //$NON-NLS-1$

    public static final String PACKAGE_ID = CoreModelerPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    /** TransactinManager instance used for declarative transaction management (May be NULL) */
    private static DeclarativeTransactionManager transactionMgr;

    /**
     * Accessor for the TransactionManager instance to be used by aspects for declarative txn management. This instance may be null.
     * 
     * @return TransactionManager instance
     */
    public static DeclarativeTransactionManager getTransactionManager() {
        return transactionMgr;
    }

    /**
     * Not intended for use by any class other than {@link org.teiid.core.CoreI18n}.
     * 
     * @param key
     * @return The i18n template associated with the supplied key
     */
    static String i18n( final String key ) {
        return Util.getString(key);
    }

    /**
     * Setter for the TransactionManager instance to be used by aspects for declarative txn management.
     * 
     * @param txnManager
     */
    public static void setTransactionManager( final DeclarativeTransactionManager txnManager ) {
        transactionMgr = txnManager;
    }

    /**
     * @param error
     */
    public static RuntimeException toRuntimeException( final Throwable error ) {
        if (error instanceof Error) throw (Error)error;
        if (error instanceof RuntimeException) return (RuntimeException)error;
        if (error != null) return new RuntimeException(error);
        return null;
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
        // Initialize the IDGenerator, which is an asynchronous call so should return quickly.
        // Calls to create IDs will block if not initialized.
        IDGenerator.getInstance();
    }
}
