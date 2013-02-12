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
public interface IWsdlRequestInfo extends IWsdlProcedureInfo {
    
    String getDefaultProcedureName();

    String getSqlStringTemplate();

    String getSqlString(Properties properties);

    boolean isMessageServiceMode();

    IWsdlWrapperInfo getWrapperProcedure();

}
