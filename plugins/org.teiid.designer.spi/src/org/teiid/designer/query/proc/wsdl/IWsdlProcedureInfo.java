/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.proc.wsdl;

import java.util.Map;
import org.teiid.designer.query.proc.wsdl.model.IOperation;

/**
 *
 */
public interface IWsdlProcedureInfo extends IWsdlConstants {
    
    String getDefaultProcedureName();

    Map<String, String> getNamespaceMap();

    ProcedureType getType();

    IWsdlColumnInfo[] getBodyColumnInfoList();

    IWsdlColumnInfo[] getHeaderColumnInfoList();

    String getProcedureName();

    /**
     * 
     * @return rootPath the root path xquery expression
     */
    String getRootPath();

    IOperation getOperation();

    String getUniqueBodyColumnName(String proposedName);

    String getUniqueHeaderColumnName(String proposedName);

    IWsdlWrapperInfo getWrapperProcedure();

}
