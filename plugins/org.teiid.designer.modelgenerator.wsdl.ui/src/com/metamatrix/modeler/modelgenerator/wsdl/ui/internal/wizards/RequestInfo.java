/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;

public class RequestInfo extends ProcedureInfo {

	public RequestInfo(Operation operation, ProcedureGenerator generator) {
		super(operation, REQUEST, generator);
		setProcedureName("create_" + operation.getName());
	}

	@Override
	String getSqlStringTemplate() {
		return getSqlString(new Properties());
	}

	@SuppressWarnings("unused")
	@Override
	String getSqlString(Properties properties) {
//		CREATE VIRTUAL PROCEDURE
//		BEGIN
//			SELECT 
//				XMLELEMENT(NAME "update", XMLNAMESPACES(DEFAULT 'http://quickstart.samples/xsd'), 
//					XMLELEMENT(NAME symbol, StockQuoteServiceXML."update".create_update_.symbol), 
//					XMLELEMENT(NAME price, StockQuoteServiceXML."update".create_update_.price)
//					) AS xml_out;
//		END
//		
//		CREATE VIRTUAL PROCEDURE
//		BEGIN
//			SELECT 								   | ------------- element-namespace------------------|
//				XMLELEMENT(NAME "<operation-name>", XMLNAMESPACES(DEFAULT 'http://<schema-namespace>'),
//				| ------------- element-content-expression ----------------------|	
//					XMLELEMENT(NAME <element-name-1>, <element-content-value>. ...), 
//					XMLELEMENT(NAME <element-name-2>, <element-content-value>. ...),
//					...
//					) AS xml_out;
//		END
//	
		
    	String alias = "xml_out"; //$NON-NLS-1$
    	StringBuffer sb = new StringBuffer();
    	int i=0;
    	sb.append(SQL_BEGIN);
    	sb.append(TAB).append(SELECT).append(RETURN);
    	sb.append(TAB).append(TAB).append(XMLELEMENT);
    	sb.append(L_PAREN);
    	
    	OUTER_PARENTH : {
	    	sb.append(NAME).append(SPACE);
	    	sb.append(getOperation().getName()).append(COMMA).append(SPACE);
	    	String nsString = getNamespaceString();
	    	if( nsString != null && !nsString.isEmpty() ) {
	    		sb.append(nsString).append(COMMA).append(SPACE);
	    	}
	    	sb.append(RETURN);
	    	int nColumns = getColumnInfoList().length;
	    	for( ColumnInfo columnInfo : getColumnInfoList()) {
	    		String name = columnInfo.getName();
	    		sb.append(TAB).append(TAB).append(TAB).append(XMLELEMENT);
	    		sb.append(L_PAREN);
	    		sb.append(NAME).append(SPACE).append(name).append(COMMA).append(SPACE).append(getViewColumnName(name));
	    		
	    		if(i < (nColumns-1)) {
	    			sb.append(COMMA).append(SPACE).append(RETURN);
	    		}
	    		i++;
	    	}
    	}
    	sb.append(R_PAREN);
    	sb.append(SPACE).append(AS).append(SPACE).append(alias);
    	sb.append(SQL_END);

		
		return sb.toString();
	}
	
    private String getNamespaceString() {
    	//
    	// EXAMPLE:  XMLNAMESPACES('http://www.kaptest.com/schema/1.0/party' AS pty)
    	//
    	
    	Map namespaces = new HashMap();
    	
    	try {
			namespaces = getGenerator().getImportManager().getWSDLModel().getNamespaces();
		} catch (ModelGenerationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
    	
    	if( namespaces.isEmpty() ) {
    		return null;
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(XMLNAMESPACES).append(L_PAREN);
    	int i=0;
    	for( Object obj : namespaces.keySet() ) {
    		if( obj instanceof String) {
    			String prefix = (String)obj;
	    		if( prefix.equalsIgnoreCase(XSI_NAMESPACE_PREFIX)) {
	    			continue;
	    		}
	    		if( i > 0 ) {
	    			sb.append(COMMA).append(SPACE);
	    		}
	    		String uri = (String)namespaces.get(obj);
	    		sb.append(S_QUOTE).append(uri).append(S_QUOTE).append(SPACE).append(AS).append(SPACE).append(prefix);
    		}
    		i++;
    	}
    	sb.append(R_PAREN).append(SPACE).append(COMMA).append(SPACE);
    	
    	return sb.toString();
    }

}
