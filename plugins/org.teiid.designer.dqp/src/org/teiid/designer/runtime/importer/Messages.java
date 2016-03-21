/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.importer;

import org.eclipse.osgi.util.NLS;

/**
 *
 *
 * @since 8.0
 */
public final class Messages extends NLS {

    public static String ImportManagerGetDatasourceError;
    public static String ImportManagerUndeployVdbError;
    public static String ImportManagerDeployVdbError;
    public static String ImportManagerVdbLoadingError;
    public static String ImportManagerVdbLoadingInterruptedError;
    public static String ImportManagerVdbGetStateError;
    public static String ImportManagerVdbInactiveStateError;
    public static String ImportManagerVdbLoadingNotCompleteError;
    public static String ImportManagerDynamicVdbTextCannotBeGeneratedError;
    public static String DependentObjectHelper_getRealEObjectFromProxyError;
    
    static {
        NLS.initializeMessages("org.teiid.designer.runtime.importer.messages", Messages.class); //$NON-NLS-1$
    }

}
