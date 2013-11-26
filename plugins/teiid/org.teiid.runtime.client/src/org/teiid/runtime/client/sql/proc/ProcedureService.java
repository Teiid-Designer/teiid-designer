/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.sql.proc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.teiid.designer.query.IProcedureService;
import org.teiid.designer.query.proc.ITeiidColumnInfo;
import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.query.proc.ITeiidXmlColumnInfo;
import org.teiid.designer.query.proc.ITeiidXmlFileInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlRequestInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlResponseInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlWrapperInfo;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.sql.proc.wsdl.WsdlRequestProcedureHelper;
import org.teiid.runtime.client.sql.proc.wsdl.WsdlResponseProcedureHelper;
import org.teiid.runtime.client.sql.proc.wsdl.WsdlWrapperHelper;

/**
 *
 */
public class ProcedureService implements IProcedureService, ISQLConstants {

    @Override
    public String getSQLStatement(ITeiidMetadataFileInfo metadataFileInfo, String relationalModelName) {
        /*
         * 
         * TEXTTABLE(expression COLUMNS <COLUMN>, ... [DELIMITER char] [(QUOTE|ESCAPE) char] [HEADER [integer]] [SKIP integer]) AS name
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
            sb.append(alias).append(DOT).append(columnStr.getSymbolName());
            
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
            }
            if(i < (nColumns-1)) {
                sb.append(COMMA).append(SPACE);
            }

            i++;
        }
        
        String proc = S_QUOTE + metadataFileInfo.getDataFile().getName() + S_QUOTE;
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
        
        String delimiter = metadataFileInfo.getDelimiter();
        if( metadataFileInfo.doUseDelimitedColumns() && ! DEFAULT_DELIMITER.equals(delimiter) ) {
            sb.append("DELIMITER"); //$NON-NLS-1$
            sb.append(SPACE).append('\'').append(delimiter).append('\'');
        }
        
        if( metadataFileInfo.doIncludeQuote() ) {
            String quote = metadataFileInfo.getQuote();
            if( ! DEFAULT_QUOTE.equals(quote)) {
                sb.append("QUOTE"); //$NON-NLS-1$
                sb.append(SPACE).append('\'').append(quote).append('\'');
            }
        } else if(metadataFileInfo.doIncludeEscape() ) {
            String escape = metadataFileInfo.getEscape();
            if( ! DEFAULT_ESCAPE.equals(escape)) {
                sb.append("ESCAPE"); //$NON-NLS-1$
                sb.append(SPACE).append('\'').append(escape).append('\'');
            }
        }
        
        if( metadataFileInfo.doIncludeHeader() ) {
            sb.append(SPACE).append("HEADER"); //$NON-NLS-1$
            if( metadataFileInfo.getHeaderLineNumber() > 1 ) {
                sb.append(SPACE).append(Integer.toString(metadataFileInfo.getHeaderLineNumber()));
            }
        }
        
        int firstDataRow = metadataFileInfo.getFirstDataRow();
        if( firstDataRow > 1 && (metadataFileInfo.doIncludeSkip() || metadataFileInfo.isFixedWidthColumns()) ) {
            sb.append(SPACE).append("SKIP"); //$NON-NLS-1$
            sb.append(SPACE).append(Integer.toString(firstDataRow-1));
        }
        
        if( metadataFileInfo.doIncludeNoTrim() && firstDataRow > 1 ) {
            sb.append(SPACE).append("NO TRIM"); //$NON-NLS-1$
        }
        tokens.add(sb.toString());
        tokens.add(alias);
        
        String finalSQLString = null;
        if(metadataFileInfo.isUrl()) {
        	finalSQLString = Messages.getString("procedureServiceTextInvokeHttpTableSqlTemplate", tokens.toArray(new Object[0])); //$NON-NLS-1$
        } else {
        	finalSQLString = Messages.getString("procedureServiceTextTableSqlTemplate", tokens.toArray(new Object[0])); //$NON-NLS-1$
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
            finalSQLString = Messages.getString("procedureServiceXmlInvokeHttpTableSqlTemplate", tokens.toArray(new Object[0])); //$NON-NLS-1$
        } else {
            finalSQLString = Messages.getString("procedureServiceXmlGetTextFilesTableSqlTemplate", tokens.toArray(new Object[0])); //$NON-NLS-1$
        }
        return finalSQLString;
    }
    
    @Override
    public String getSQLStatement(IWsdlWrapperInfo wrapperInfo) {
        WsdlWrapperHelper helper = new WsdlWrapperHelper(wrapperInfo);
        return helper.getWrapperStatement();
    }
    
    @Override
    public String getSQLStatement(IWsdlWrapperInfo wrapperInfo, Properties properties) {
        WsdlWrapperHelper helper = new WsdlWrapperHelper(wrapperInfo);
        return helper.getWrapperProcedureStatement(properties);
    }
    
    @Override
    public String getSQLStatement(IWsdlRequestInfo requestInfo, Properties properties) {
        WsdlRequestProcedureHelper helper = new WsdlRequestProcedureHelper(requestInfo, properties);
        return helper.getSQLStatement();
    }
    
    @Override
    public String getSQLStatement(IWsdlResponseInfo responseInfo, Properties properties) {
        WsdlResponseProcedureHelper helper = new WsdlResponseProcedureHelper(responseInfo, properties);
        return helper.getSQLStatement();
    }

}
