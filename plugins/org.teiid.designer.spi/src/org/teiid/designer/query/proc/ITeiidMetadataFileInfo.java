/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.proc;

import java.util.List;
import org.teiid.designer.query.sql.ISQLConstants;



/**
 *
 */
public interface ITeiidMetadataFileInfo extends ITeiidFileInfo, ISQLConstants {

    int DEFAULT_HEADER_LINE_NUMBER = 1;
    
    /**
     * 
     * @return headerLineNumber the data file's line number containing the header info
     */
    int getHeaderLineNumber();

    /**
     * 
     * @return firstDataRow the line number of first data row
     */
    int getFirstDataRow();

    /**
     * 
     * @return the data file's delimiter character 
     */
    String getDelimiter();
    
    /**
     * 
     * @return the data file's delimiter character 
     */
    int getRowDelimiterOption();
    
    /**
     * 
     * @return the data file's delimiter character 
     */
    String getRowDelimiter();

    /**
     * 
     * @return the data file's quote character
     */
    String getQuote();

    /**
     * 
     * @return the data file's escape character
     */
    String getEscape();

    /**
     * @return the header string
     */
    String getHeaderString();

    /**
     * 
     * @return cachedFirstLines the <code>String[]</code> array from the data file
     */
    String[] getCachedFirstLines();

    /**
     * 
     * @return columnInfoList the <code>TeiidColumnInfo[]</code> list parsed from the header in the data file
     */
    List<ITeiidColumnInfo> getColumnInfoList();

    /**
     * 
     * @return doProcess the boolean indicator that the user wishes to create view table from this object
     */
    boolean doProcess();

    /**
     * 
     * @return includeHeader the boolean indicator that the generated view table SQL should include the HEADER parameter
     */
    boolean doIncludeHeader();

    /**
     * 
     * @return includeSkip the boolean indicator that the generated view table SQL should include the SKIP parameter
     */
    boolean doIncludeSkip();

    /**
     * 
     * @return includeEscape the boolean indicator that the generated view table SQL should include the SKIP parameter
     */
    boolean doIncludeEscape();

    /**
     * 
     * @return includeQuote the boolean indicator that the generated view table SQL should include the QUOTE parameter
     */
    boolean doIncludeQuote();

    /**
     * 
     * @return includeNoTrim the boolean indicator that the generated view table SQL should include the NO TRIM parameter
     */
    boolean doIncludeNoTrim();

    /**
     * 
     * @return useHeader the boolean indicator that the data file contains a header with column names and should 
     * be used to load column names
     */
    boolean doUseHeaderForColumnNames();

    /**
     * 
     * @return fixedWidthColumns the boolean indicator that the data file contains a header with column names
     */
    boolean isFixedWidthColumns();

    /**
     * 
     * @return numberOfFixedWidthColumns the number of fixed with columns
     */
    int getNumberOfFixedWidthColumns();

    /**
     * 
     * @return delimitedColumns the boolean indicator that the data file contains columns separated by a delimiter
     */
    boolean doUseDelimitedColumns();

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
     * 
     * @return the Data file filter, if specified
     */
    String getDataFileFilter();
    
    /**
     * 
     * @return 'true' if remote url file, 'false' otherwise
     */
    boolean isUrl();

    /**
     * 
     * @return url string if this is a remote file
     */
    String getFileUrl();

	/**
	 * @return the char set if this is a remote file
	 */
	String getCharSet();

	/**
     * Returns the current generated SQL string based on an unknown relational model name
     * @return the generated SQL string
     */
    String getSqlStringTemplate();

    /**
     * Get the SQL string representation of this information
     * 
     * @param relationalModelName
     * 
     * @return SQL DDL string
     */
    String getSqlString(String relationalModelName);

}
