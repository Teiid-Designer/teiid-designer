/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.config;


/** 
 * @since 5.0
 */
public interface ConfigurationConstants {

    public static final int UNKNOWN_CHANGED = -1;
    public static final int CONNECTOR_TYPE_CHANGE = 0;
    public static final int CONNECTOR_BINDINGS_CHANGED = 1;
    
    public static final int UNKNOWN_EVENT = -1;
    public static final int CHANGED_EVENT = 0;
    public static final int ADDED_EVENT = 1;
    public static final int REMOVED_EVENT = 2;
    public static final int REPLACED_EVENT = 3;
}
