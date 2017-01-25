/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.proc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.teiid.datatools.connectivity.model.Parameter;
import org.teiid.designer.query.IProcedureService;
import org.teiid.designer.query.proc.ITeiidColumnInfo;
import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.query.proc.ITeiidXmlColumnInfo;
import org.teiid.designer.query.proc.ITeiidXmlFileInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlRequestInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlResponseInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlWrapperInfo;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.language.SQLConstants;
import org.teiid.query.proc.wsdl.WsdlRequestProcedureHelper;
import org.teiid.query.proc.wsdl.WsdlResponseProcedureHelper;
import org.teiid.query.proc.wsdl.WsdlWrapperHelper;
import org.teiid.runtime.client.Messages;

/**
 *
 */
public class ProcedureService implements IProcedureService, ISQLConstants {

    private final ITeiidServerVersion teiidVersion;
    private final static String JSON = "JSON"; //$NON-NLS-1$

    /**
     * @param teiidVersion
     */
    public ProcedureService(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
    }

    @Override
    public String getSQLStatement(ITeiidMetadataFileInfo metadataFileInfo, String relationalModelName) {
        /*
         * 
         * TEXTTABLE(expression COLUMNS <COLUMN>, ... [NO_ROW_DELIMITER | [ROW_DELIMIETER char] [DELIMITER char] [(QUOTE|ESCAPE) char] [HEADER [integer]] [SKIP integer]) AS name
         * 
         * DELIMITER sets the field delimiter character to use. Defaults to ','.
         * 
         * QUOTE sets the quote, or qualifier, character used to wrap field values. Defaults to '"'.
         * 
         * ESCAPE sets the escape character to use if no quoting character is in use. This is used in situations where the delimiter or new line characters are escaped with a preceding character, e.g. \
         * 
         * 
            SELECT A.lastName, A.firstName, A.middleName, A.AId FROM
        (EXEC EmployeeData.getTextFiles('EmployeeData.txt')) AS f, TEXTTABLE(file COLUMNS lastName string, firstName string, middleName string, HEADER 3) AS A
         
         *
         * SELECT {0} FROM (EXEC {1}.getTextFiles({2})) AS f, TEXTTABLE(file COLUMNS {3}  HEADER {4}) AS {5}
         */
        List<String> tokens = new ArrayList<String>();
        List<ITeiidColumnInfo> columnInfoList = metadataFileInfo.getColumnInfoList();
    	
        String alias = "A"; //$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        int i=0;
        int nColumns = columnInfoList.size();
        for(ITeiidColumnInfo columnStr : columnInfoList) {
        	String columnName = columnStr.getSymbolName();
        	
        	// Column names may contain spaces and/or odd characters as long as they are double-quoted
        	// Need to check for d-quotes and triple d-quote the column alias.
        	String aliasColumnName = columnName;
        	boolean aliasColumn = false;
        	if( columnName.startsWith(D_QUOTE) ) {
        		aliasColumnName = D_QUOTE + D_QUOTE + columnName + D_QUOTE + D_QUOTE;
        		aliasColumn = true;
        	}
            sb.append(alias).append(DOT).append(columnName);
            if( aliasColumn ) {
            	sb.append(SPACE).append(AS).append(SPACE).append(aliasColumnName);
            }
            
            if(i < (nColumns-1)) {
                sb.append(COMMA).append(SPACE);
            }
            i++;
        }
        tokens.add(sb.toString());
        tokens.add(relationalModelName);
        
        sb = new StringBuffer();
        i=0;
        for( ITeiidColumnInfo columnStr : columnInfoList) {
            sb.append(columnStr.getSymbolName()).append(SPACE).append(columnStr.getDatatype());
            if( metadataFileInfo.isFixedWidthColumns()) {
                sb.append(SPACE).append(WIDTH).append(SPACE).append(Integer.toString(columnStr.getWidth()));
                if(columnStr.isNoTrim()) {
                	sb.append(SPACE).append(NO_TRIM);
                }
            }
            if(i < (nColumns-1)) {
                sb.append(COMMA).append(SPACE);
            }

            i++;
        }
        
        String fileName = metadataFileInfo.getDataFile().getName();
        if( metadataFileInfo.getDataFileFilter() != null ) {
        	fileName = metadataFileInfo.getDataFileFilter();
        }
        String proc = S_QUOTE + fileName + S_QUOTE;

        if( metadataFileInfo.isUrl() ) {
            proc = S_QUOTE + GET + S_QUOTE
                    + COMMA + SPACE + NULL.toLowerCase()
                    + COMMA + SPACE + S_QUOTE + metadataFileInfo.getFileUrl() + S_QUOTE
                    + COMMA + SPACE + S_QUOTE + TRUE + S_QUOTE;
        }
        tokens.add(proc);
        
        if(metadataFileInfo.isUrl()) {
        	tokens.add(metadataFileInfo.getCharSet());
        }
        
        tokens.add(sb.toString());
        
        sb = new StringBuffer();
        
        // ROW DELIMITER
        int rowDelimiterOption = metadataFileInfo.getRowDelimiterOption();
        switch (rowDelimiterOption) {
	        case ISQLConstants.ROW_DELIMETER_OPTIONS.NONE: {
	        	sb.append(NO_ROW_DELIMIETER_STR);
	        } break;
	        	
	        case ISQLConstants.ROW_DELIMETER_OPTIONS.CUSTOM: {
	        	sb.append(ROW_DELIMIETER_STR).append(SPACE).append(S_QUOTE).append(metadataFileInfo.getRowDelimiter()).append(S_QUOTE);
	        } break;
	        	
	        default: {
	        	// value = ISQLConstants.ROW_DELIMETER_OPTIONS.DEFAULT
	        }
        	
        }

        
        
        String delimiter = metadataFileInfo.getDelimiter();
        if( metadataFileInfo.doUseDelimitedColumns() && ! DEFAULT_DELIMITER.equals(delimiter) ) {
            sb.append(SPACE).append(DELIMITER_STR); //$NON-NLS-1$
            sb.append(SPACE).append(S_QUOTE).append(delimiter).append(S_QUOTE);
        }
        
        if( metadataFileInfo.doIncludeQuote() ) {
            String quote = metadataFileInfo.getQuote();
            if( ! DEFAULT_QUOTE.equals(quote)) {
                sb.append(QUOTE_STR); //$NON-NLS-1$
                sb.append(SPACE).append(S_QUOTE).append(quote).append(S_QUOTE);
            }
        } else if(metadataFileInfo.doIncludeEscape() ) {
            String escape = metadataFileInfo.getEscape();
            if( ! DEFAULT_ESCAPE.equals(escape)) {
                sb.append(ESCAPE_STR); //$NON-NLS-1$
                sb.append(SPACE).append(S_QUOTE).append(escape).append(S_QUOTE);
            }
        }
        
        if( metadataFileInfo.doIncludeHeader() ) {
            sb.append(SPACE).append(HEADER); //$NON-NLS-1$
            if( metadataFileInfo.getHeaderLineNumber() > 1 ) {
                sb.append(SPACE).append(Integer.toString(metadataFileInfo.getHeaderLineNumber()));
            }
        }
        
        int firstDataRow = metadataFileInfo.getFirstDataRow();
        if( firstDataRow > 1 && (metadataFileInfo.doIncludeSkip() || metadataFileInfo.isFixedWidthColumns()) ) {
            sb.append(SPACE).append(SKIP); //$NON-NLS-1$
            sb.append(SPACE).append(Integer.toString(firstDataRow-1));
        }
        
        if( metadataFileInfo.doIncludeNoTrim() && firstDataRow > 1 ) {
            sb.append(SPACE).append(NO_TRIM); //$NON-NLS-1$
        }
        tokens.add(sb.toString());
        tokens.add(alias);
        
        String finalSQLString = null;
        if(metadataFileInfo.isUrl()) {
        	finalSQLString = Messages.getString(Messages.ProcedureService.procedureServiceTextInvokeHttpTableSqlTemplate, tokens.toArray(new Object[0])); 
        } else {
        	finalSQLString = Messages.getString(Messages.ProcedureService.procedureServiceTextTableSqlTemplate, tokens.toArray(new Object[0])); 
        }
        return finalSQLString;
    }
    
    @Override
    public String getSQLStatement(ITeiidXmlFileInfo xmlFileInfo, String relationalModelName) {
        /*
        ##  SELECT
        ##      title.pmid AS pmid, title.journal AS journal, title.title AS title
        ##  FROM
        ##      (EXEC getMeds.getTextFiles('medsamp2011.xml')) AS f, 
        ##           XMLTABLE('$d/MedlineCitationSet/MedlineCitation' PASSING 
        ##                 XMLPARSE(DOCUMENT f.file) AS d 
        ##                 COLUMNS pmid biginteger PATH 'PMID', journal string PATH 'Article/Journal/Title', title string PATH 'Article/ArticleTitle') AS title
        ##
        ## XMLTABLE([<NSP>,] xquery-expression [<PASSING>] [COLUMNS <COLUMN>, ... )] AS name
        ##
        ## COLUMN := name (FOR ORDINALITY | (datatype [DEFAULT expression] [PATH string]))
        ##
        ##  name     datatype  PATH            string
        ## journal    string   PATH   'Article/Journal/Title'
        ##
        ##
        ##
        ##  IF URL XML file, use invokeHTTP() method
        ##
        ## EXAMPLE:
        
            EXEC SampleSource.getTextFiles('sample.xml')) AS f, 
                XMLTABLE(XMLNAMESPACES('http://www.kaptest.com/schema/1.0/party' AS pty), 
                '/pty:students/student' PASSING XMLPARSE(DOCUMENT f.file) 
                COLUMNS firstName string PATH '/firstName', middleName string PATH '/middleName', lastName string PATH '/lastName') AS A
        ##
        ##
        ## XMLNAMESPACES('http://www.kaptest.com/schema/1.0/party' AS pty)
        
        */
    	
        List<String> tokens = new ArrayList<String>();
        List<ITeiidXmlColumnInfo> columnInfoList = xmlFileInfo.getColumnInfoList();
        
        String alias = "A"; //$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        int i=0;
        int nColumns = columnInfoList.size();
        for( ITeiidXmlColumnInfo columnInfo : columnInfoList) {
            String name = columnInfo.getSymbolName();
            sb.append(alias).append(DOT).append(name).append(SPACE).append(AS).append(SPACE).append(name);
            
            if(i < (nColumns-1)) {
                sb.append(COMMA).append(SPACE);
            }
            i++;
        }
        tokens.add(sb.toString());
        tokens.add(relationalModelName);
        
        String proc = S_QUOTE + xmlFileInfo.getDataFile().getName() + S_QUOTE;
        if( xmlFileInfo.isUrl() ) {
            proc = S_QUOTE + GET + S_QUOTE
                    + COMMA + SPACE + NULL.toLowerCase()
                    + COMMA + SPACE + S_QUOTE + xmlFileInfo.getXmlFileUrl() + S_QUOTE
                    + COMMA + SPACE + S_QUOTE + TRUE + S_QUOTE;
        }
        tokens.add(proc);
        
        String namespaceStr = xmlFileInfo.getNamespaceString();
        sb = new StringBuffer();
        
        if( namespaceStr != null ) {
            sb.append(namespaceStr);
        }
        
        String xQueryExp = DEFAULT_XQUERY;
        String rootPath = xmlFileInfo.getRootPath();
        if( rootPath != null && rootPath.length() > 0 ) {
            xQueryExp = rootPath;
        }
        sb.append(S_QUOTE).append(xQueryExp).append(S_QUOTE);
        
        tokens.add(sb.toString());
        
        sb = new StringBuffer();
        i=0;
        for( ITeiidXmlColumnInfo columnInfo : columnInfoList) {
            if( columnInfo.getOrdinality() ) {
                sb.append(columnInfo.getSymbolName()).append(SPACE).append(FOR_ORDINALITY);
            } else {
                sb.append(columnInfo.getSymbolName()).append(SPACE).append(columnInfo.getDatatype());
                
                String defValue = columnInfo.getDefaultValue();
                if( defValue != null && defValue.length() > 0) {
                    sb.append(SPACE).append(DEFAULT).append(SPACE).append(S_QUOTE).append(defValue).append(S_QUOTE);
                }
                
                String relPath = columnInfo.getRelativePath();
                if( relPath != null && relPath.length() > 1 ) {
                    sb.append(SPACE).append(PATH).append(SPACE).append(S_QUOTE).append(relPath).append(S_QUOTE);
                }
                
                
            }
            if(i < (nColumns-1)) {
                sb.append(COMMA).append(SPACE);
            }

            i++;
        }

        tokens.add(sb.toString());
        tokens.add(alias);

        String finalSQLString = null;
        
        if( xmlFileInfo.isUrl() ) {
            // SELECT {0} FROM (EXEC {1}.getTextFiles({2})) AS f, XMLTABLE('{3}' PASSING XMLPARSE(DOCUMENT f.file) AS d COLUMNS {4}) AS {5}
            finalSQLString = Messages.getString(Messages.ProcedureService.procedureServiceXmlInvokeHttpTableSqlTemplate, tokens.toArray(new Object[0])); 
        } else {
            finalSQLString = Messages.getString(Messages.ProcedureService.procedureServiceXmlGetTextFilesTableSqlTemplate, tokens.toArray(new Object[0])); 
        }
        return finalSQLString;
    }
    
    /* (non-Javadoc)
	 * @see org.teiid.designer.query.IProcedureService#getSQLStatement(org.teiid.designer.query.proc.ITeiidMetadataFileInfo, java.lang.String, java.lang.String)
	 */
	@Override
	public String getSQLStatement(ITeiidXmlFileInfo xmlFileInfo,
			String relationalSourceModelName, String relationalViewModelName, String virtualProcedureName) {
//    	CREATE VIRTUAL PROCEDURE
//    	BEGIN
//    		DECLARE STRING VARIABLES.qp = QUERYSTRING('http://ws.cdyne.com/delayedstockquote/delayedstockquote.asmx/GetQuickQuote', ViewModel.getStockPrice.symbol AS StockSymbol, 0 AS LicenseKey);
//    		SELECT A.price AS price FROM (EXEC SourceModel.invokeHttp(action=>'GET', endpoint=>VARIABLES.qp, stream=>'TRUE')) AS f, XMLTABLE(XMLNAMESPACES(DEFAULT 'http://ws.cdyne.com/'), '/decimal' PASSING XMLPARSE(DOCUMENT f.result) COLUMNS price double PATH 'text()') AS A;
//    	END
//    	
    	 
		 List<String> tokens = new ArrayList<String>();
         List<ITeiidXmlColumnInfo> columnInfoList = xmlFileInfo.getColumnInfoList();
         
         boolean isJson = false;
         if(xmlFileInfo.getResponseType()!=null && xmlFileInfo.getResponseType().toUpperCase().equals(JSON)) {
        	 isJson = true;
         }
         Map<String, Parameter> parameters = xmlFileInfo.getParameterMap();

         boolean isQueryParm = false;
         boolean hasHeaderParm = false;
         Map<String, Parameter> headerParameters = new LinkedHashMap<String, Parameter>();
         StringBuffer sb = new StringBuffer();
         
         int c = 1;
         if (parameters.isEmpty() ||
             parametersContainOnlyHeaders(parameters)){sb.append("''");} //$NON-NLS-1$
         for (Parameter param: parameters.values() ){
        	 if (param.getType().equals(Parameter.Type.Query)) {
        		 isQueryParm=true;
        		 if (c==1) tokens.add(xmlFileInfo.getXmlFileUrl());
        		 // Check for Reserved Word
        		 String paramName = param.getName();
        		 paramName = checkForReservedWord(paramName);
        		 sb.append(relationalViewModelName).append(DOT).append(virtualProcedureName).append(DOT).append(paramName).append(SPACE).append(AS).append(SPACE).append(paramName);
        		 if(c < (parameters.size())) {
                     sb.append(COMMA).append(SPACE);
                 }
        	 }else {
        		 if (param.getType().equals(Parameter.Type.Header)) {
        			 hasHeaderParm = true;
        			 headerParameters.put(param.getPropertyKey(), param);
        			 continue;
        		 }
        		 if (c==1){
        			 String url = xmlFileInfo.getXmlFileUrl();
        			 if (url.endsWith("/")){ //$NON-NLS-1$
        				 url = url.substring(0, url.lastIndexOf("/")); //$NON-NLS-1$
        			 }
        			 sb.append("'"); //$NON-NLS-1$
        			 sb.append(url); 
        			 sb.append("'"); //$NON-NLS-1$
        		 }
        	 }
        		 
        	 c++;
         }
         
         
         tokens.add(sb.toString());
         sb = new StringBuffer();
              
         String alias = "A"; //$NON-NLS-1$
         int i=0;
         int nColumns = columnInfoList.size();
         for( ITeiidXmlColumnInfo columnInfo : columnInfoList) {
             String name = columnInfo.getSymbolName();
             sb.append(alias).append(DOT).append(name).append(SPACE).append(AS).append(SPACE).append(name);
             
             if(i < (nColumns-1)) {
                 sb.append(COMMA).append(SPACE);
             }
             i++;
         }
         tokens.add(sb.toString());
         tokens.add(relationalSourceModelName);
         
         StringBuilder proc = new StringBuilder(S_QUOTE + xmlFileInfo.getDataFile().getName() + S_QUOTE);
         if( xmlFileInfo.isUrl() ) {
        	 proc = new StringBuilder();
             proc.append("action=>" + S_QUOTE + GET + S_QUOTE + COMMA + "endpoint=>VARIABLES.qp" + COMMA + "stream=>" + S_QUOTE + TRUE + S_QUOTE);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
             JSONObject jsonObject = new JSONObject();
             Map<String, String> formattedHeaderParameters = new LinkedHashMap<String, String>();
             if (hasHeaderParm){
            	int count = 1;
            	proc.append(COMMA).append("headers=>'");
            	
            	for (Parameter param: headerParameters.values()){
            		String paramValue = param.getDefaultValue();
            		String paramName = param.getName();
            		paramName = checkForReservedWord(paramName);
            		
           		    //The format of a comma delimited set of values should be, "'value1,value2','etc".
           		    //The following will clean up the parameters will ensure this format is adhered to.
            		if (paramValue.contains(",")){
            			String[] values = paramValue.split(",");
            			paramValue=EMPTY_STR;
            			int valuecount = 1;
            			int length = values.length;
            			for (String value:values){
            				value=value.trim();
            				value=value.replace("'", EMPTY_STR);
            				value=value.replace("\"", EMPTY_STR);
            				paramValue+=value;
            				if (valuecount<length){
            					paramValue+=",";
            					valuecount++;
            				}
            			}
            			formattedHeaderParameters.put(paramName, paramValue);

            		}else{

            			//The format of a single value should be, "'myvalue'".
               		    //The following will clean up the parameter string to ensure this format is adhered to.
        				paramValue=paramValue.trim();
        				paramValue=paramValue.replace("'", EMPTY_STR);
        				paramValue=paramValue.replace("\"", EMPTY_STR);
        				formattedHeaderParameters.put(paramName, paramValue);

            		}
            		count++;
            	}
            	jsonObject.putAll(formattedHeaderParameters);
            	proc.append(jsonObject.toJSONString()).append(S_QUOTE);
             }
         }
         tokens.add(proc.toString());
         
         String namespaceStr = xmlFileInfo.getNamespaceString();
         sb = new StringBuffer();
         
         if( namespaceStr != null ) {
             sb.append(namespaceStr);
         }
         
         String xQueryExp = DEFAULT_XQUERY;
         String rootPath = xmlFileInfo.getRootPath();
         if( rootPath != null && rootPath.length() > 0 ) {
             xQueryExp = rootPath;
         }
         //If this is JSON, we already have a starting quote with 'response.
         if (!isJson) {
        	 sb.append(S_QUOTE);
         }
         sb.append(xQueryExp).append(S_QUOTE);
         
         tokens.add(sb.toString());
         
         sb = new StringBuffer();
         i=0;
         for( ITeiidXmlColumnInfo columnInfo : columnInfoList) {
             if( columnInfo.getOrdinality() ) {
                 sb.append(columnInfo.getSymbolName()).append(SPACE).append(FOR_ORDINALITY);
             } else {
                 sb.append(columnInfo.getSymbolName()).append(SPACE).append(columnInfo.getDatatype());
                 
                 String defValue = columnInfo.getDefaultValue();
                 if( defValue != null && defValue.length() > 0) {
                     sb.append(SPACE).append(DEFAULT).append(SPACE).append(S_QUOTE).append(defValue).append(S_QUOTE);
                 }
                 
                 String relPath = columnInfo.getRelativePath();
                 if( relPath != null && relPath.length() > 1 ) {
                     sb.append(SPACE).append(PATH).append(SPACE).append(S_QUOTE).append(relPath).append(S_QUOTE);
                 }
                 
                 
             }
             if(i < (nColumns-1)) {
                 sb.append(COMMA).append(SPACE);
             }

             i++;
         }

         tokens.add(sb.toString());
         tokens.add(alias);

         String finalSQLString = null;
         
         // SELECT {0} FROM (EXEC {1}.getTextFiles({2})) AS f, XMLTABLE('{3}' PASSING XMLPARSE(DOCUMENT f.file) AS d COLUMNS {4}) AS {5}
         if (isJson) {
	         if (isQueryParm){
	        	 finalSQLString = Messages.getString(Messages.ProcedureService.procedureServiceXmlInvokeHttpWithQueryParametersJSONTableSqlTemplate, tokens.toArray(new Object[0])); 
	         }else{
	        	 finalSQLString = Messages.getString(Messages.ProcedureService.procedureServiceXmlInvokeHttpWithURIParametersJSONTableSqlTemplate, tokens.toArray(new Object[0])); 
	         }
         }else{
        	 if (isQueryParm){
	        	 finalSQLString = Messages.getString(Messages.ProcedureService.procedureServiceXmlInvokeHttpWithQueryParametersTableSqlTemplate, tokens.toArray(new Object[0])); 
	         }else{
	        	 finalSQLString = Messages.getString(Messages.ProcedureService.procedureServiceXmlInvokeHttpWithURIParametersTableSqlTemplate, tokens.toArray(new Object[0])); 
	         }
         }
         
         return finalSQLString;
	}

	private String checkForReservedWord(String paramName) {
		// Check for Reserved Word
		if( SQLConstants.isReservedWord(teiidVersion, paramName) ) {
			paramName = D_QUOTE + paramName + D_QUOTE;
		}
		return paramName;
	}
	
	    private boolean parametersContainOnlyHeaders(Map<String, Parameter> parameters) {
	       boolean onlyHeaders = true;
	       for (Parameter parameter:parameters.values()){
	    	   if (parameter.getType().equals(Parameter.Type.Header)){
	    		   continue;
	    	   }else{
	    		   onlyHeaders = false;
	    		   break;
	    	   }
	       }
	        return onlyHeaders;
	    }
	
    @Override
    public String getSQLStatement(IWsdlWrapperInfo wrapperInfo) {
        WsdlWrapperHelper helper = new WsdlWrapperHelper(teiidVersion, wrapperInfo);
        return helper.getWrapperStatement();
    }
    
    @Override
    public String getSQLStatement(IWsdlWrapperInfo wrapperInfo, Properties properties) {
        WsdlWrapperHelper helper = new WsdlWrapperHelper(teiidVersion, wrapperInfo);
        return helper.getWrapperProcedureStatement(properties);
    }
    
    @Override
    public String getSQLStatement(IWsdlRequestInfo requestInfo, Properties properties) {
        WsdlRequestProcedureHelper helper = new WsdlRequestProcedureHelper(teiidVersion, requestInfo, properties);
        return helper.getSQLStatement();
    }
    
    @Override
    public String getSQLStatement(IWsdlResponseInfo responseInfo, Properties properties) {
        WsdlResponseProcedureHelper helper = new WsdlResponseProcedureHelper(teiidVersion, responseInfo, properties);
        return helper.getSQLStatement();
    }

}
