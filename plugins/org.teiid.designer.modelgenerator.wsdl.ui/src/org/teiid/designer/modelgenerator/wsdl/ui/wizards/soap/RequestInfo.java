/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;

public class RequestInfo extends ProcedureInfo {

	public RequestInfo(Operation operation, ProcedureGenerator generator) {
		super(operation, REQUEST, generator);
		setProcedureName(getDefaultProcedureName());
	}
	
	
	private String getFullParameterName(String procedureName, String name) {
		StringBuilder builder = new StringBuilder();
		builder.append(getGenerator().getViewModelName());
		builder.append('.').append(procedureName).append('.').append(getGenerator().convertSqlNameSegment(name));
		
		return builder.toString();
	}
	
	public String getDefaultProcedureName() {
		return getOperation().getName() + "_request";//$NON-NLS-1$
	}
	
	@Override
	public IStatus validate() {
		IStatus status = Status.OK_STATUS;
		// Go through objects and look for problems
		if( getProcedureName() == null) {	
			return new Status(IStatus.ERROR, ProcedureGenerator.PLUGIN_ID, 
				Messages.Error_RequestProcedureNameCannotBeNullOrEmpty);
		}
		
		IStatus nameStatus = getGenerator().getNameStatus(getProcedureName());
		if( nameStatus.getSeverity() > IStatus.INFO) {
			return nameStatus;
		}
		
		// Look at all element names
		for( ColumnInfo info : getBodyColumnInfoList() ) {
			IStatus colNameStatus = getGenerator().getNameStatus(info.getName());
			if( colNameStatus.getSeverity() > IStatus.INFO) {
				return colNameStatus;
			}
		}
		// Check Request Info
		
		return status;
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
//		CREATE VIRTUAL PROCEDUREthis.operation.getName()
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
		
		// Request procedure name may have been overridden
		String requestProcedureName = properties.getProperty(ProcedureGenerator.KEY_REQUEST_PROCEDURE_NAME);
		if( requestProcedureName == null ) {
			requestProcedureName = this.getProcedureName();
		}
		
    	String alias = "xml_out"; //$NON-NLS-1$
    	StringBuffer sb = new StringBuffer();
    	int i=0;
    	sb.append(SQL_BEGIN);
    	sb.append(TAB).append(SELECT).append(RETURN);
    	String header = getHeaderString();
    	if( header.length() > 0 ) {
    		sb.append(TAB).append(TAB).append(header).append(RETURN);
    	}
    	sb.append(TAB).append(TAB).append(XMLELEMENT);
    	sb.append(L_PAREN);
    	
    	OUTER_PARENTH : {
	    	sb.append(NAME).append(SPACE);
	    	sb.append(getGenerator().convertSqlNameSegment(getOperation().getName())).append(COMMA).append(SPACE);
	    	String nsString = getNamespaceString();
	    	if( nsString != null && !nsString.isEmpty() ) {
	    		sb.append(nsString);
	    	}
	    	int nColumns = getBodyColumnInfoList().length;
	    	if( nColumns > 0 ) {
	    		sb.append(COMMA);
	    	}
	    	sb.append(SPACE).append(RETURN);
	    	// EXAMPLE:       XMLELEMENT(NAME FromCurrency, CurrencyConvertorView.request_ConversionRate.FromCurrency)
	    	
	    	for( ColumnInfo columnInfo : getBodyColumnInfoList()) {
	    		String name = columnInfo.getName();
	    		sb.append(TAB).append(TAB).append(TAB).append(XMLELEMENT);
	    		sb.append(L_PAREN);
	    		sb.append(NAME).append(SPACE).append(getGenerator().convertSqlNameSegment(name));
	    		sb.append(COMMA).append(SPACE).append(getFullParameterName(requestProcedureName, name));
	    		sb.append(R_PAREN);
	    		if(i < (nColumns-1)) {
	    			sb.append(COMMA).append(SPACE).append(RETURN);
	    		}
	    		i++;
	    	}
    	}
    	sb.append(R_PAREN);
    	sb.append(SPACE).append(AS).append(SPACE).append(alias).append(SEMI_COLON);
    	sb.append(SQL_END);

		
		return sb.toString();
	}
	
    private String getNamespaceString() {
    	
    	if( getGenerator().getNamespaceURI() == null ) {
    		return null;
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(XMLNAMESPACES).append(L_PAREN);
    	sb.append(DEFAULT).append(SPACE).append(S_QUOTE).append(getGenerator().getNamespaceURI()).append(S_QUOTE);
    	sb.append(R_PAREN);
    	
    	return sb.toString();
    }
    
    private String getHeaderString() {
    	/* EXAMPLE
    	 XMLELEMENT(NAME "soapenv:Envelope", 
    	 	XMLNAMESPACES('http://schemas.xmlsoap.org/soap/envelope/' AS soapenv), 
    	 	XMLELEMENT(NAME "soapenv:Header", XMLELEMENT(NAME quote_timestamp, StockServiceServiceView.getLastSellPrice_request.quote_timestamp)), 
    	 */
    	if( this.getGenerator().getImportManager().isMessageServiceMode()) {
    		return "XMLELEMENT(NAME \"soapenv:Envelope\", " + 
    	 	"XMLNAMESPACES('http://schemas.xmlsoap.org/soap/envelope/' AS soapenv), " +
    	 	"XMLELEMENT(NAME \"soapenv:Header\", XMLELEMENT(NAME quote_timestamp, StockServiceServiceView.getLastSellPrice_request.quote_timestamp)), ";
    	}
    	return "";
    }

}
