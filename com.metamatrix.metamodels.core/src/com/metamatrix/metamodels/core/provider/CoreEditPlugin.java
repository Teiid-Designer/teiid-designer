/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.provider;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;
import com.metamatrix.metamodels.core.CoreMetamodelPlugin;
import com.metamatrix.metamodels.core.extension.provider.ExtensionEditPlugin;

/**
 * This is the central singleton for the Core edit plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public final class CoreEditPlugin extends EMFPlugin {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Keep track of the singleton. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final CoreEditPlugin INSTANCE = new CoreEditPlugin();

    /**
     * Keep track of the singleton. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    static Implementation plugin;

    /**
     * Create the instance. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public CoreEditPlugin() {
        super(new ResourceLocator[] {ExtensionEditPlugin.INSTANCE, EcoreEditPlugin.INSTANCE,});
    }

    /**
     * Returns the singleton instance of the Eclipse plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the singleton instance.
     */
    @Override
    public ResourceLocator getPluginResourceLocator() {
        return CoreMetamodelPlugin.getPluginResourceLocator();
    }

    /**
     * Returns the singleton instance of the Eclipse plugin. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the singleton instance.
     * @generated
     */
    public static Implementation getPlugin() {
        return plugin;
    }

    /**
     * The actual implementation of the Eclipse <b>Plugin</b>. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static class Implementation extends EclipsePlugin {
        /**
         * Creates an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @param descriptor the description of the plugin.
         * @generated
         */
        public Implementation() {
            super();

            // Remember the static instance.
            //
            plugin = this;
        }
    }
}
