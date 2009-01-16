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

package com.metamatrix.metamodels.xsd;

import org.eclipse.xsd.util.XSDConstants;

import com.metamatrix.modeler.core.types.DatatypeConstants;


/** 
 * @since 4.3
 */
public class XsdConstants extends XSDConstants {

    /**
     * The value <code>"http://www.metamatrix.com/2005/XmlSchema/EnterpriseDatatypes"</code>.
     */  
    public static final String SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005 = DatatypeConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005;

    /**
     * The value <code>"http://www.metamatrix.com/2005/XmlSchema/EnterpriseDatatypes"</code>.
     */  
    public static final String PREFIX_FOR_ENTERPRISE_DATATYPES_URI_2005 = DatatypeConstants.PREFIX_FOR_ENTERPRISE_DATATYPES_URI_2005;

    /**
     * Returns whether the given namespace is the XML Schema Enterprise Datatype namespace.
     * @param namespace a namespace.
     * @return whether the given namespace is the XML Schema Enterprise Datatype namespace.
     */
    public static boolean isSchemaEnterpriseDatatypeNamespace(final String namespace)
    {
      return 
        DatatypeConstants.isSchemaEnterpriseDatatypeNamespace(namespace);
    }

    /**
     * Returns whether the given namespace prefix is the XML Schema Enterprise Datatype namespace prefix.
     * @param prefix a namespace prefix.
     * @return whether the given namespace prefix is the XML Schema Enterprise Datatype namespace prefix.
     */
    public static boolean isSchemaEnterpriseDatatypeNamespacePrefix(final String prefix)
    {
      return 
        DatatypeConstants.isSchemaEnterpriseDatatypeNamespacePrefix(prefix);
    }
    
    
}
