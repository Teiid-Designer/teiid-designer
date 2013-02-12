/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query;

import java.util.Properties;
import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.query.proc.ITeiidXmlFileInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlRequestInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlResponseInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlWrapperInfo;

/**
 * Provides getters for sql syntax strings that differ between runtime client versions
 */
public interface IProcedureService {

    /**
     * Prepare an SQL statement for the given file-based metdata
     * 
     * @param metadataFileInfo
     * @param relationalModelName 
     * 
     * @return SQL statement based on the given metadata information
     */
    String getSQLStatement(ITeiidMetadataFileInfo metadataFileInfo, String relationalModelName);

    /**
     * Prepare an SQL statement for the given xml-based metdata
     * 
     * @param xmlFileInfo
     * @param relationalModelName 
     * 
     * @return SQL statement based on the given metadata information
     */
    String getSQLStatement(ITeiidXmlFileInfo xmlFileInfo, String relationalModelName);
    
    /**
     * Prepare an SQL statement for the given wsdl-based wrapper metadata
     *
     * @param wrapperInfo
     * 
     * @return SQL statement based on the given metadata information
     */
    String getSQLStatement(IWsdlWrapperInfo wrapperInfo);
    
    /**
     * Prepare an SQL statement for the given wsdl-based wrapper procedure metadata
     * 
     * @param wrapperInfo 
     * @param properties
     * 
     * @return SQL statement based on the given metadata information
     */
    String getSQLStatement(IWsdlWrapperInfo wrapperInfo, Properties properties);
    
    /**
     * Prepare an SQL statement for the given wsdl-based request metadata
     * 
     * @param requestInfo 
     * @param properties
     *  
     * @return SQL statement based on the given metadata information 
     */
    String getSQLStatement(IWsdlRequestInfo requestInfo, Properties properties);

    /**
     * Prepare an SQL statement for the given wsdl-based response metadata
     * 
     * @param responseInfo
     * @param properties
     * 
     * @return SQL statement based on the given metadata information
     */
    String getSQLStatement(IWsdlResponseInfo responseInfo, Properties properties);

}
