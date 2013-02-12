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
public interface IWsdlWrapperInfo extends IWsdlConstants {

    IWsdlRequestInfo getRequestInfo();

    IWsdlResponseInfo getResponseInfo();

    String getViewModelName();

    String getWrapperProcedureName();

    String getSoapAction();
    
    String getBindingType();
    
    String getSourceModelName();

    String getNamespaceURI();

    String getWrapperSqlString();
    
    String getWrapperProcedureSqlString(Properties properties);

}
