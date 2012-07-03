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
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;

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
		MultiStatus status = new MultiStatus(ProcedureGenerator.PLUGIN_ID, 0, null, null);
		// Go through objects and look for problems
		if( getProcedureName() == null) {	
			status.add(new Status(IStatus.ERROR, ProcedureGenerator.PLUGIN_ID, 
				NLS.bind(Messages.Error_Operation_0_RequestProcedureNameCannotBeNullOrEmpty,  
					getOperation().getName())));
		}
		
		IStatus nameStatus = getGenerator().getNameStatus(getProcedureName());
		if( nameStatus.getSeverity() > IStatus.INFO) {
			status.merge(nameStatus);
		}
		
		// Look at all element names
		for( ColumnInfo info : getBodyColumnInfoList() ) {
			IStatus colNameStatus = getGenerator().getNameStatus(info.getName());
			if( colNameStatus.getSeverity() > IStatus.INFO) {
				status.merge(colNameStatus);
			}
		}
		
		// Look at all element names
		for (ColumnInfo info : getHeaderColumnInfoList()) {
			IStatus colNameStatus = getGenerator().getNameStatus(info.getName());
			if (colNameStatus.getSeverity() > IStatus.INFO) {
				status.merge(colNameStatus);
			}
		}
		
		if( getBodyColumnInfoList().length == 0 ) {
			status.add(new Status(IStatus.WARNING, ProcedureGenerator.PLUGIN_ID,
				NLS.bind(Messages.Error_NoElementsDefinedForRequestProcedureForOperation_0, 
					getOperation().getName())));
		}

		// Check Request Info
		setChanged(false);
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
//					XMLELEMENT(NAME symbol, StockQuoteServiceXML."update".create_update_.symbol, 
//						XMLATTRIBUTES( symbol.empno AS "work_number"), 
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
	    	String elementName = null;
	    	Message message = null;
	    	Part[] parts = null;
	    	
	    	if (!(this.getGenerator().getImportManager().isMessageServiceMode())) {
		    	message = this.getOperation().getInputMessage();
		    	parts = message.getParts();
		    	elementName = getPartElementName(parts[0]);
	    	}
	    	
	    	sb.append(getGenerator().convertSqlNameSegment(elementName==null?getOperation().getName():elementName)).append(COMMA).append(SPACE);
	    	String nsString = (!(this.getGenerator().getImportManager().isMessageServiceMode()) ? getNamespaceString(parts) : null);
	    	//If this is MESSAGE mode, no need to add NS since we did that at the Envelope level
	    	if (!(this.getGenerator().getImportManager().isMessageServiceMode())) {
		    	if( nsString != null && !nsString.isEmpty() ) {
		    		sb.append(nsString);
		    	}
	    	}
	    	int nColumns = getBodyColumnInfoList().length;
	    	if( nColumns > 0 ) {
	    		//If this is MESSAGE mode, we don't need this extra comma
	    		if (!(this.getGenerator().getImportManager().isMessageServiceMode())) sb.append(COMMA);
	    	}
	    	sb.append(SPACE).append(RETURN);
	    	// EXAMPLE:       XMLELEMENT(NAME FromCurrency, CurrencyConvertorView.request_ConversionRate.FromCurrency)
	    	
	    	for( ColumnInfo columnInfo : getBodyColumnInfoList()) {
	    		String name = columnInfo.getName();
	    		sb.append(TAB).append(TAB).append(TAB).append(XMLELEMENT);
	    		sb.append(L_PAREN);
	    		sb.append(NAME).append(SPACE).append(getGenerator().convertSqlNameSegment(name));
	    		if( columnInfo.getAttributeInfoArray().length == 0 ) {
	    			sb.append(COMMA).append(SPACE).append(getFullParameterName(requestProcedureName, name));
	    		}
	    		
	    		addAttributesForElement(sb, columnInfo);
	    		
	    		sb.append(R_PAREN);
	    		if(i < (nColumns-1)) {
	    			sb.append(COMMA).append(SPACE).append(RETURN);
	    		}
	    		i++;
	    	}
    	}
    	sb.append(R_PAREN);
    	//Need two extra parenthesis for MESSAGE mode to wrap everything in the SOAP Envelope and Body
    	if (this.getGenerator().getImportManager().isMessageServiceMode()) sb.append(R_PAREN).append(R_PAREN);
    	sb.append(SPACE).append(AS).append(SPACE).append(alias).append(SEMI_COLON);
    	sb.append(SQL_END);

		
		return sb.toString();
	}
	
	private String getPartElementName(Part part) {
		String partElementName = null;

		partElementName = part.getTypeName();
		if (partElementName == null) {
			partElementName = part.getElementName();
		}

		return partElementName;
	}
	
	private String getPartElementNamespace(Part part) {
		String partElementNamespace = null;

		partElementNamespace = part.getTypeNamespace();
		if (partElementNamespace == null) {
			partElementNamespace = part.getElementNamespace();
		}

		return partElementNamespace;
	}
	
	private void addAttributesForElement(StringBuffer sb, ColumnInfo columnInfo) {
		if( columnInfo.getAttributeInfoArray().length > 0 ) {
			sb.append(COMMA).append(RETURN).append(TAB).append(TAB).append(TAB).append(TAB).append(XMLATTRIBUTES);
			sb.append(L_PAREN);
			int index = 0;
			for( AttributeInfo attrInfo : columnInfo.getAttributeInfoArray() ) {
				if( index > 0 ) {
					sb.append(COMMA).append(SPACE);
				}
				sb.append(attrInfo.getName());
				if( !attrInfo.getName().equalsIgnoreCase(attrInfo.getAlias())) {
					sb.append(SPACE).append(AS).append(SPACE);
					sb.append(attrInfo.getAlias());
				}
				index++;
			}
			sb.append(R_PAREN);
		}
	}
	
    private String getNamespaceString(Part[] parts) {
    	
    	if( getGenerator().getNamespaceURI() == null ) {
    		return null;
    	}
    	
    	String ns = null;
    	
    	if (parts.length==1){
    		ns = getPartElementNamespace(parts[0]);
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(XMLNAMESPACES).append(L_PAREN);
    	sb.append(DEFAULT).append(SPACE).append(S_QUOTE).append(ns==null?getGenerator().getNamespaceURI():ns).append(S_QUOTE);
    	sb.append(R_PAREN);
    	
    	return sb.toString();
    }
    
    private String getHeaderString() {
    	/* EXAMPLE
    	XMLELEMENT(NAME "soapenv:Envelope", 
    	XMLNAMESPACES('http://schemas.xmlsoap.org/soap/envelope/' AS soapenv, DEFAULT 'http://soapheader.ibm.com'), 
    	XMLELEMENT(NAME "soapenv:Header", 
    	XMLELEMENT(NAME quote_timestamp, StockServiceServiceView.getLastSellPrice_request.quote_timestamp)), 
    	XMLELEMENT(NAME "soapenv:Body", XMLELEMENT(NAME getLastSellPrice, 
    	XMLELEMENT(NAME ticker, StockServiceServiceView.getLastSellPrice_request.ticker)))) AS xml_out;
    	 */
    	
    	StringBuilder headerString = new StringBuilder();
    	
    	if( this.getGenerator().getImportManager().isMessageServiceMode()) {
    		
    		//Initial Envelope Element. 
	    	headerString.append(XMLELEMENT).append(L_PAREN).append(NAME).append(SPACE).append(D_QUOTE).append(ENVELOPE_NAME).append(D_QUOTE).append(COMMA);
	    	headerString.append(SPACE).append(XMLNAMESPACES).append(L_PAREN).append(S_QUOTE).append(ENVELOPE_NS).append(S_QUOTE).append(SPACE).append(AS).append(SPACE);
	    	headerString.append(ENVELOPE_NS_ALIAS);
	    	
	    	if (this.getNamespaceMap().size()>0 || (getGenerator().getNamespaceURI() != null &! getGenerator().getNamespaceURI().isEmpty())){
	    		headerString.append(COMMA).append(SPACE).append(DEFAULT).append(SPACE).append(S_QUOTE).append(getGenerator().getNamespaceURI());
    			headerString.append(S_QUOTE);
	    		for (String key: this.getNamespaceMap().keySet()){
	    			//If this is the default NS, skip it
	    			if (this.getNamespaceMap().get(key).equals(getGenerator().getNamespaceURI())) break;
	    			headerString.append(COMMA).append(SPACE).append(DEFAULT).append(SPACE).append(S_QUOTE).append(this.getNamespaceMap().get(key));
	    			headerString.append(S_QUOTE);
	    		}
	    	}
	    	
	    	headerString.append(R_PAREN).append(COMMA).append(SPACE).append(XMLELEMENT).append(L_PAREN).append(NAME).append(SPACE);
	    	headerString.append(D_QUOTE).append(HEADER_NAME).append(D_QUOTE);
    	
	    	for (ColumnInfo columnInfo:this.getHeaderColumnInfoList()){
	    		headerString.append(COMMA).append(SPACE).append(XMLELEMENT).append(L_PAREN).append(NAME).append(SPACE).append(columnInfo.getName());
	    		headerString.append(COMMA).append(SPACE).append(getFullParameterName(this.getProcedureName(), columnInfo.getName()));
	    		headerString.append(R_PAREN);
	    	}
	    	
	    	headerString.append(R_PAREN).append(COMMA).append(SPACE);
	    	headerString.append(XMLELEMENT).append(L_PAREN).append(NAME).append(SPACE).append(D_QUOTE).append(BODY_NAME).append(D_QUOTE).append(COMMA);
	   }
    	
    	return headerString.toString();
    }

}
