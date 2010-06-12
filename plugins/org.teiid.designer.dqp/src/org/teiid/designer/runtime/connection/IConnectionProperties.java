/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.connection;

/**
 *
 */
public interface IConnectionProperties {

    String CONNECTOR_TYPE = "rar-name"; //$NON-NLS-1$
    
    String JNDI_NAME = "connection-jndi-name"; ////$NON-NLS-1$
    
    String NAME = "name"; //$NON-NLS-1$
    
    String TRANSLATOR_NAME = "translator-name"; //$NON-NLS-1$
    
}
