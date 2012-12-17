/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid772.runtime;

import org.eclipse.osgi.util.NLS;

/**
 *
 *
 * @since 8.0
 */
public final class Messages extends NLS {

    public static String JarDeploymentJarNotFound;
    public static String JarDeploymentFailed;
    public static String JarDeploymentJarNotReadable;
    
    public static String jdcbSourceForClassNameNotFound;
    public static String dataSourceTypeDoesNotExist;
    public static String errorCreatingDataSource;
    public static String invalidPropertyValue;
    public static String cannotConnectToServer;
    public static String serverDeployUndeployProblemPingingTeiidJdbc;
    
    public static String invalidPropertyEditorConstrainedValue;
    public static String invalidPropertyEditorValue;
    public static String invalidNullPropertyValue;
    public static String missingPropertyDefinition;
    public static String unknownPropertyType;
    public static String connectorDetailedName;

    public static String failedToGetDriverMappings;
    public static String cannotLoadDriverClass;
    
    static {
        NLS.initializeMessages("org.teiid772.runtime.messages", Messages.class); //$NON-NLS-1$
    }

}
