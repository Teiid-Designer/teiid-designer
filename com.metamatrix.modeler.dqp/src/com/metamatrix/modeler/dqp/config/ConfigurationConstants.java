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
