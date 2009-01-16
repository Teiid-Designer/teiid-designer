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

package com.metamatrix.metamodels.wsdl.io;

import org.eclipse.xsd.util.XSDConstants;

import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;


/** 
 * @since 4.2
 */
public interface WsdlConstants {
    
    // ------------------------------------------------------------------------
    // WSDL 1.1 Namespace constants
    // ------------------------------------------------------------------------
    
    public static final String NAMESPACE                = WsdlPackage.eNS_URI; 
    public static final String NAMESPACE_PREFIX         = WsdlPackage.eNS_PREFIX; 
    
    public static final String DEFINITIONS              = "definitions"; //$NON-NLS-1$
    public static final String DEFINITIONS_TARGETNAMESPACE = "targetNamespace"; //$NON-NLS-1$
    public static final String DEFINITIONS_NAME         = "name"; //$NON-NLS-1$

    public static final String DOCUMENTATION            = "documentation"; //$NON-NLS-1$

    public static final String IMPORT                   = "import"; //$NON-NLS-1$
    public static final String INCLUDE                  = "include"; //$NON-NLS-1$
    public static final String IMPORT_NAMESPACE         = "namespace"; //$NON-NLS-1$
    public static final String IMPORT_LOCATION          = "location"; //$NON-NLS-1$

    public static final String TYPES                    = "types"; //$NON-NLS-1$

    public static final String MESSAGE                  = "message"; //$NON-NLS-1$
    public static final String MESSAGE_NAME             = "name"; //$NON-NLS-1$

    public static final String PART                     = "part"; //$NON-NLS-1$
    public static final String PART_NAME                = "name"; //$NON-NLS-1$
    public static final String PART_TYPE                = "type"; //$NON-NLS-1$
    public static final String PART_ELEMENT             = "element"; //$NON-NLS-1$

    public static final String PORTTYPE                 = "portType"; //$NON-NLS-1$
    public static final String PORTTYPE_NAME            = "name"; //$NON-NLS-1$

    public static final String OPERATION                = "operation"; //$NON-NLS-1$
    public static final String OPERATION_NAME           = "name"; //$NON-NLS-1$
    public static final String OPERATION_PARAMETER_ORDER= "parameterOrder"; //$NON-NLS-1$

    public static final String INPUT                    = "input"; //$NON-NLS-1$
    public static final String INPUT_NAME               = "name"; //$NON-NLS-1$
    public static final String INPUT_MESSAGE            = "message"; //$NON-NLS-1$

    public static final String OUTPUT                   = "output"; //$NON-NLS-1$
    public static final String OUTPUT_NAME              = "name"; //$NON-NLS-1$
    public static final String OUTPUT_MESSAGE           = "message"; //$NON-NLS-1$

    public static final String FAULT                    = "fault"; //$NON-NLS-1$
    public static final String FAULT_NAME               = "name"; //$NON-NLS-1$
    public static final String FAULT_MESSAGE            = "message"; //$NON-NLS-1$

    public static final String BINDING                  = "binding"; //$NON-NLS-1$
    public static final String BINDING_NAME             = "name"; //$NON-NLS-1$
    public static final String BINDING_TYPE             = "type"; //$NON-NLS-1$

    public static final String BINDINGOPERATION         = "operation"; //$NON-NLS-1$
    public static final String BINDINGOPERATION_NAME    = "name"; //$NON-NLS-1$

    public static final String BINDINGINPUT             = "input"; //$NON-NLS-1$
    public static final String BINDINGINPUT_NAME        = "name"; //$NON-NLS-1$

    public static final String BINDINGOUTPUT            = "output"; //$NON-NLS-1$
    public static final String BINDINGOUTPUT_NAME       = "name"; //$NON-NLS-1$

    public static final String BINDINGFAULT             = "fault"; //$NON-NLS-1$
    public static final String BINDINGFAULT_NAME        = "name"; //$NON-NLS-1$

    public static final String SERVICE                  = "service"; //$NON-NLS-1$
    public static final String SERVICE_NAME             = "name"; //$NON-NLS-1$

    public static final String PORT                     = "port"; //$NON-NLS-1$
    public static final String PORT_NAME                = "name"; //$NON-NLS-1$
    public static final String PORT_BINDING             = "binding"; //$NON-NLS-1$

    public static final String ARRAY_TYPE               = "arrayType"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // WSDL-SOAP 1.1 Namespace constants
    // ------------------------------------------------------------------------  
    
    static final String NCNAMES_DELIM = " "; //$NON-NLS-1$
    static final String ANYURI_DELIM = " "; //$NON-NLS-1$
    
    public static interface Soap {
        public static final String NAMESPACE                = SoapPackage.eNS_URI; 
        public static final String NAMESPACE_PREFIX         = SoapPackage.eNS_PREFIX; 

        public static final String BINDING                  = "binding"; //$NON-NLS-1$
        public static final String BINDING_TRANSPORT        = "transport"; //$NON-NLS-1$
        public static final String BINDING_STYLE            = "style"; //$NON-NLS-1$

        public static final String ENUM_STYLE_RPC           = "rpc"; //$NON-NLS-1$
        public static final String ENUM_STYLE_DOC           = "document"; //$NON-NLS-1$
        public static final String ENUM_STYLE_DEFAULT       = ENUM_STYLE_DOC;

        public static final String OPERATION                = "operation"; //$NON-NLS-1$
        public static final String OPERATION_SOAPACTION     = "soapAction"; //$NON-NLS-1$
        public static final String OPERATION_STYLE          = "style"; //$NON-NLS-1$

        public static final String ENUM_USE_LITERAL         = "literal"; //$NON-NLS-1$
        public static final String ENUM_USE_ENCODED         = "encoded"; //$NON-NLS-1$

        public static final String BODY                     = "body"; //$NON-NLS-1$
        public static final String BODY_ENCODINGSTYLE       = "encodingStyle"; //$NON-NLS-1$
        public static final String BODY_PARTS               = "parts"; //$NON-NLS-1$
        public static final String BODY_USE                 = "use"; //$NON-NLS-1$
        public static final String BODY_NAMESPACE           = "namespace"; //$NON-NLS-1$

        public static final String HEADER                   = "header"; //$NON-NLS-1$
        public static final String HEADER_ENCODINGSTYLE     = "encodingStyle"; //$NON-NLS-1$
        public static final String HEADER_MESSAGE           = "message"; //$NON-NLS-1$
        public static final String HEADER_PARTS             = "parts"; //$NON-NLS-1$
        public static final String HEADER_USE               = "use"; //$NON-NLS-1$
        public static final String HEADER_NAMESPACE         = "namespace"; //$NON-NLS-1$

        public static final String HEADERFAULT              = "headerfault"; //$NON-NLS-1$
        public static final String HEADERFAULT_ENCODINGSTYLE= "encodingStyle"; //$NON-NLS-1$
        public static final String HEADERFAULT_MESSAGE      = "message"; //$NON-NLS-1$
        public static final String HEADERFAULT_PARTS        = "parts"; //$NON-NLS-1$
        public static final String HEADERFAULT_USE          = "use"; //$NON-NLS-1$
        public static final String HEADERFAULT_NAMESPACE    = "namespace"; //$NON-NLS-1$

        public static final String FAULT                    = "fault"; //$NON-NLS-1$
        public static final String FAULT_ENCODINGSTYLE      = "encodingStyle"; //$NON-NLS-1$
        public static final String FAULT_USE                = "use"; //$NON-NLS-1$
        public static final String FAULT_NAMESPACE          = "namespace"; //$NON-NLS-1$

        public static final String ADDRESS                  = "address"; //$NON-NLS-1$
        public static final String ADDRESS_LOCATION         = "location"; //$NON-NLS-1$
    }

    // ------------------------------------------------------------------------
    // WSDL-HTTP 1.1 Namespace constants
    // ------------------------------------------------------------------------
        
    public static interface Http {
        public static final String NAMESPACE                = HttpPackage.eNS_URI; 
        public static final String NAMESPACE_PREFIX         = HttpPackage.eNS_PREFIX; 

        public static final String BINDING                  = "binding"; //$NON-NLS-1$
        public static final String BINDING_VERB             = "verb"; //$NON-NLS-1$

        public static final String ADDRESS                  = "address"; //$NON-NLS-1$
        public static final String ADDRESS_LOCATION         = "location"; //$NON-NLS-1$

        public static final String OPERATION                = "operation"; //$NON-NLS-1$
        public static final String OPERATION_LOCATION       = "location"; //$NON-NLS-1$

        public static final String URLREPLACEMENT           = "urlReplacement"; //$NON-NLS-1$

        public static final String URLENCODED               = "urlEncoded"; //$NON-NLS-1$
}

    // ------------------------------------------------------------------------
    // WSDL-MIME 1.1 Namespace constants
    // ------------------------------------------------------------------------
    
    public static interface Mime {
        public static final String NAMESPACE                = MimePackage.eNS_URI; 
        public static final String NAMESPACE_PREFIX         = MimePackage.eNS_PREFIX; 

        public static final String CONTENT                  = "content"; //$NON-NLS-1$
        public static final String CONTENT_TYPE             = "type"; //$NON-NLS-1$
        public static final String CONTENT_PART             = "part"; //$NON-NLS-1$

        public static final String MULTIPARTRELATED         = "multipartRelated"; //$NON-NLS-1$

        public static final String PART                     = "part"; //$NON-NLS-1$
        public static final String PART_NAME                = "name"; //$NON-NLS-1$

        public static final String MIMEXML                  = "mimeXml"; //$NON-NLS-1$
        public static final String MIMEXML_PART             = "part"; //$NON-NLS-1$

    }
    
    // ------------------------------------------------------------------------
    // XML Schema Namespace constants
    // ------------------------------------------------------------------------
    
    public static interface Xsd {
        public static final String NAMESPACE_2001 = XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001;
        public static final String NAMESPACE_2000 = XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10;
        public static final String NAMESPACE_1999 = XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999;

        public static final String SCHEMA                   = "schema"; //$NON-NLS-1$
    }
    
}
