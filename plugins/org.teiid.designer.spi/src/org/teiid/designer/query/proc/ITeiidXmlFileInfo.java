/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.proc;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.query.sql.ISQLConstants;

/**
 *
 */
public interface ITeiidXmlFileInfo<T extends ITeiidXmlColumnInfo> extends ITeiidFileInfo, ISQLConstants {

    /**
     * Is the Xml file an url
     * 
     * @return whether file is an url
     */
    boolean isUrl();

    /**
     * @return Xml file url
     */
    String getXmlFileUrl();

    /**
     * 
     * @return rootPath the root path xquery expression
     */
    String getRootPath();

    /**
     * 
     * @return cachedFirstLines the <code>String[]</code> array from the data file
     */
    String[] getCachedFirstLines();

    /**
     * 
     * @return columnInfoList the <code>TeiidXmlColumnInfo[]</code> array parsed from the header in the data file
     */
    List<T> getColumnInfoList();
    
    /**
     * 
     * @return responseType the response type from the connection profile. Can be XML or JSON.
     */
    String getResponseType();
    
    /**
     * 
     * @param responseType the response type from the connection profile. Can be XML or JSON.
     */
    void setResponseType(String responseType);
    
    /**
     * 
     * @return parameterMap the <code>Parameter</code> map from the connection profile
     */
    Map<String, Object> getParameterMap();
    
    /**
     * Sets the <code>Parameter</code> map from the connection profile
     * @param parameterMap 
     */
    void setParameterMap(Map<String, Object> parameterMap);
    
    /**
     * 
     * @return doProcess the boolean indicator that the user wishes to create view table from this object
     */
    boolean doProcess();

    /**
     * 
     * @return numberOfCachedLines the number of cached lines from data file
     */
    int getNumberOfCachedFileLines();

    /**
     * 
     * @return numberOfCachedLines the total number of lines from data file
     */
    int getNumberOfLinesInFile();

    /**
     * Returns the current generated SQL string based on an unknown relational model name
     * @return the generated SQL string
     */
    String getSqlStringTemplate();

    /**
     * Returns the current generated SQL string based on an unknown relational model name
     * 
     * @param relationalModelName 
     * 
     * @return the generated SQL string based on the values stored on this instance
     */
    String getSqlString(String relationalModelName);

    /**
     * Get the common root path
     * 
     * @return string value of the root path
     */
    String getCommonRootPath();

    /**
     * Get the parsing status
     * 
     * @return whether this XML is parsed successfully
     */
    IStatus getParsingStatus();

    /**
     * Get the Xml file's namespace
     * 
     * @return
     */
    String getNamespaceString();

}
