/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.proc.wsdl;

import java.util.Properties;

/**
 *
 */
public interface IWsdlResponseInfo extends IWsdlProcedureInfo {
    
    String SOAPENVELOPE_ROOTPATH = "/soap:Envelope";//$NON-NLS-1$
    
    String SOAPHEADER_ROOTPATH = "/soap:Header";//$NON-NLS-1$
    
    String SOAPBODY_ROOTPATH = "/soap:Body";//$NON-NLS-1$
    
    String DEFAULT_NS = "ns";//$NON-NLS-1$

    String getDefaultProcedureName();

    String getSqlStringTemplate();

    String getSqlString(Properties properties);

}
