/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose;


/**
 * "Global" Constants with respect to this plug-in that may be used by other plug-ins.
 * 
 * @since 4.1
 */
public interface IRoseConstants {
    //============================================================================================================================
    // Constants

    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.1
     */
    
    String PLUGIN_ID = "org.teiid.designer.rose"; //$NON-NLS-1$
    
    String PACKAGE_ID = IRoseConstants.class.getPackage().getName();
}
