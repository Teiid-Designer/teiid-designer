/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.vdb.runtime;

/**
 */
public interface URIModelResource {

    String getURI();

    String getAuthLevel();

    boolean isXMLDocType();

    boolean isPhysicalBindingAllowed();

    public class AUTH_LEVEL {
        public static final String ALL = "all";//$NON-NLS-1$
        public static final String MODEL = "model";//$NON-NLS-1$
        public static final String GROUP = "group";//$NON-NLS-1$

    }

}
