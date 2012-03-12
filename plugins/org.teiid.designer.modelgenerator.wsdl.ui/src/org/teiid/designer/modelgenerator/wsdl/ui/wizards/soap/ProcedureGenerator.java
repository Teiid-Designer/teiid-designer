/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.io.ObjectInputStream.GetField;

import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;

/** This class provides state information for the create and extract procedures that will be generated during
 * WSDL import and model/procedure generation
 * 
 * There will be both Request and Response information managed by this class.
 * 
 * 
 */
public class ProcedureGenerator implements SqlConstants {
	private static final String SQL_BEGIN = "CREATE VIRTUAL PROCEDURE\nBEGIN\n"; //$NON-NLS-1$
	private static final String SQL_END = "\nEND"; //$NON-NLS-1$
	private static final String REQUEST = "REQUEST"; //$NON-NLS-1$
	private static final String RESPONSE = "RESPONSE"; //$NON-NLS-1$
	private static final String REQUEST_LOWER = "request"; //$NON-NLS-1$
	private static final String RESPONSE_LOWER = "response"; //$NON-NLS-1$
	private static final String TABLE_EXEC = "TABLE(EXEC "; //$NON-NLS-1$
	private static final String XMI_EXTENSION = ".xmi";  //$NON-NLS-1$
	private static final String RESULT_LOWER = "result";  //$NON-NLS-1$
	private static final String INVOKE_SEGMENT = "invoke('SOAP11', null, REQUEST.xml_out, null))"; //$NON-NLS-1$
	private static final String NULL_LOWER = "null";  //$NON-NLS-1$

	private ProcedureInfo requestInfo;
	private ProcedureInfo responseInfo;
	
	private boolean generateWrapperProcedure;
	
	private String wrapperProcedureName;
	
	private boolean overwriteExistingProcedures;
	
	private Operation operation;
	
	private String viewModelName;
	
	private String namespaceURI;
	
	private WSDLImportWizardManager importManager;

	public ProcedureGenerator(Operation operation, WSDLImportWizardManager importManager) {
		super();
		this.operation = operation;
		this.requestInfo = new RequestInfo(operation, this);
		this.responseInfo = new ResponseInfo(operation, this);
		this.viewModelName = operation.getBinding().getPort().getService().getName() + "View"; //$NON-NLS-1$
		this.importManager = importManager;
		this.generateWrapperProcedure = true;
		this.namespaceURI = operation.getBinding().getPort().getNamespaceURI();
	}

	public WSDLImportWizardManager getImportManager() {
		return this.importManager;
	}
	
	public ProcedureInfo getRequestInfo() {
		return this.requestInfo;
	}

	public ProcedureInfo getResponseInfo() {
		return this.responseInfo;
	}

	public Operation getOperation() {
		return this.operation;
	}
	
	public void setRequestProcedureName(String name ) {
		this.requestInfo.setProcedureName(name);
	}
	
	public void setResponseProcedureName(String name ) {
		this.responseInfo.setProcedureName(name);
	}
	
	public String getRequestProcedureName() {
		return this.requestInfo.getProcedureName();
	}
	
	public String getResponseProcedureName() {
		return this.responseInfo.getProcedureName();
	}
	
	public String getViewModelName() {
		return this.viewModelName;
	}

	public void setGenerateWrapperProcedure(boolean value) {
		this.generateWrapperProcedure = value;
	}
	
	public boolean doGenerateWrapperProcedure() {
		return this.generateWrapperProcedure;
	}
	
	public void setWrapperProcedureName(String name ) {
		this.wrapperProcedureName = name;
	}
	
	public String getWrappedProcedureName() {
		if( this.wrapperProcedureName == null ) {
			this.wrapperProcedureName = getOperation().getName(); //$NON-NLS-1$
		}
		return this.wrapperProcedureName;
	}
	
	
	public void setOverwriteExistingProcedures(boolean value) {
		this.overwriteExistingProcedures = value;
	}
	
	public boolean doOverwriteExistingProcedures() {
		return this.overwriteExistingProcedures;
	}
	
	public String getNamespaceURI() {
		return this.namespaceURI;
	}
	
	public String getWrapperSqlString() {
		/**
            CREATE VIRTUAL PROCEDURE
            BEGIN
                 SELECT t.* FROM 
                      TABLE(EXEC CountryInfoServiceXML.CapitalCity.create_CapitalCity(OPS.GETCAPITALCITY.countryISOCode)) 
                 AS request, 
                 TABLE(EXEC CountryInfoService.invoke('SOAP11', null, REQUEST.xml_out, null)) 
                 AS response, 
                 TABLE(EXEC CountryInfoServiceXML.CapitalCity.extract_CapitalCityResponse(RESPONSE.result)) 
                 AS t;
             END
                 
             CREATE VIRTUAL PROCEDURE
             BEGIN
                 SELECT t.* FROM 
                    TABLE(EXEC <view-model-name>.<request_procedure>(OPS.GETCAPITALCITY.countryISOCode)) 
                 AS request, 
                 	TABLE(EXEC <source-model-name>.invoke('SOAP11', null, REQUEST.xml_out, null)) 
                 AS response, 
                 	TABLE(EXEC <view-model-name>.<response_procedure>(RESPONSE.result)) 
                 AS t;
             END
		 */
		
		StringBuilder sb = new StringBuilder();
		
    	String tableAlias = "t"; //$NON-NLS-1$

    	sb.append(SQL_BEGIN);
    	// SELECT t.* FROM 
    	sb.append(TAB).append(SELECT).append(SPACE).append(tableAlias).append(DOT).append(STAR).append(SPACE).append(FROM).append(RETURN);
    	// TABLE(EXEC 
    	sb.append(TAB).append(TAB).append(TABLE_EXEC);
    	// <view-model-name>.<request_procedure>
    	sb.append(getModelNameWithoutExtension(importManager.getSourceModelName()));
    	sb.append(DOT).append(getRequestProcedureName());
    	
    	// (OPS.GETCAPITALCITY.countryISOCode))
    	sb.append(L_PAREN);
    	int nColumns = this.requestInfo.getColumnInfoList().length;
    	int i=0;
    	for( ColumnInfo columnInfo : this.requestInfo.getColumnInfoList()) {
    		String name = columnInfo.getName();
    		sb.append(getParamaterFullName(name));
    		
    		if(i < (nColumns-1)) {
    			sb.append(COMMA).append(SPACE);
    		}
    		i++;
    	}
    	sb.append(R_PAREN).append(RETURN);
    	
    	// AS request,
    	sb.append(TAB).append(AS).append(SPACE).append(REQUEST_LOWER).append(COMMA).append(RETURN);
    	
    	// TABLE(EXEC <source-model-name>.invoke('SOAP11', null, REQUEST.xml_out, null))
    	sb.append(TAB).append(TAB)
    		.append(TABLE_EXEC)
    		.append(getModelNameWithoutExtension(importManager.getSourceModelName())).append(DOT)
    		.append(INVOKE_SEGMENT).append(RETURN);
    	
    	// AS response,
    	sb.append(TAB).append(AS).append(SPACE).append(RESPONSE_LOWER).append(COMMA).append(RETURN);
    	
    	// TABLE(EXEC <view-model-name>.<response_procedure>(RESPONSE.result))  
    	sb.append(TAB).append(TAB)
    		.append(TABLE_EXEC)
    		.append(getModelNameWithoutExtension(importManager.getViewModelName()))
    		.append(DOT).append(getResponseProcedureName())
    		.append(L_PAREN).append(RESPONSE).append(DOT).append(RESULT_LOWER)
    		.append(R_PAREN).append(R_PAREN).append(RETURN);
    	
    	// AS t;
    	sb.append(TAB).append(AS).append(SPACE).append(tableAlias).append(SEMI_COLON).append(RETURN);
    	
    	sb.append(SQL_END);

    	
		return sb.toString();
	}
	
	private String getModelNameWithoutExtension(String modelName) {
		String name = modelName;
        if (name.endsWith(XMI_EXTENSION)) {
        	name = name.substring(0, name.lastIndexOf(XMI_EXTENSION));
        }
        return name;
	}
	
	public String getParamaterFullName(String name) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getViewModelName());
		builder.append('.').append(this.operation.getName()).append('.').append(name);
		
		return builder.toString();
	}
	
	public String getProcedureFullName(ProcedureInfo info) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getViewModelName());
		builder.append('.').append(info.getProcedureName());
		
		return builder.toString();
	}
	
	private String getWrapperProcedureParameterName(String parameterName) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getViewModelName());
		builder.append(DOT).append(getWrappedProcedureName()).append(DOT).append(parameterName);
		
		return builder.toString();
	}
	
	@SuppressWarnings("unused")
	public String getWrapperProcedureSqlString() {
		/*
    	CREATE VIRTUAL PROCEDURE
    	BEGIN
    		SELECT t.* FROM 
    			TABLE(EXEC PriceServiceView.GetPrice_request(
    					PriceServiceView.GetPrice_procedure.productID,
    					PriceServiceView.GetPrice_procedure.productName)) 
    				AS request, 
    			TABLE(EXEC PriceService.invoke('SOAP11', null, REQUEST.xml_out, null)) 
    				AS response, 
    			TABLE(EXEC PriceServiceView.GetPrice_response(RESPONSE.result)) 
    				AS t;
    	END
    	*/
		StringBuilder sb = new StringBuilder();
		
    	String tableAlias = "t"; //$NON-NLS-1$

    	sb.append(SQL_BEGIN);
    	// SELECT t.* FROM 
    	sb.append(TAB).append(SELECT).append(SPACE).append(tableAlias).append(DOT).append(STAR).append(SPACE).append(FROM).append(RETURN);

    	// Request TABLE
    	sb.append(TAB2).append(TABLE).append(L_PAREN).append(EXEC).append(SPACE);
    	sb.append(getProcedureFullName(getRequestInfo()));
    	sb.append(L_PAREN);
    	REQUEST_PARAMETERS : {
    		int i=0;
    		int nColumns = getRequestInfo().getColumnInfoList().length;
    		
    		for ( ColumnInfo columnInfo : getRequestInfo().getColumnInfoList() ) {
    			sb.append(TAB4).append(getWrapperProcedureParameterName(columnInfo.getName()));
        		if(i < (nColumns-1)) {
        			sb.append(COMMA).append(SPACE).append(RETURN);
        		}
        		i++;
    		}
    	}
    	sb.append(R_PAREN);
    	sb.append(R_PAREN).append(RETURN).append(TAB4).append(AS).append(SPACE).append(REQUEST_LOWER).append(COMMA).append(RETURN);
    	
    	// Response TABLE
    	sb.append(TAB2).append(TABLE).append(L_PAREN).append(EXEC).append(SPACE);
    	sb.append(getModelNameWithoutExtension(this.importManager.getSourceModelName())).append(DOT);
    	INVOKE_CALL : { 
    		sb.append(FUNCTION_INVOKE);
    		sb.append(L_PAREN).append("'SOAP11'").append(COMMA).append(SPACE).append(NULL_LOWER).append(COMMA).append(SPACE); //$NON-NLS-1$
    		sb.append(REQUEST).append(DOT).append("xml_out").append(COMMA).append(SPACE).append(NULL_LOWER); //$NON-NLS-1$
    		sb.append(R_PAREN);
    	}
    	sb.append(R_PAREN).append(RETURN).append(TAB4).append(AS).append(SPACE).append(RESPONSE_LOWER).append(COMMA).append(RETURN);
    	
    	// Request TABLE:  [		TABLE(EXEC PriceServiceView.GetPrice_response(RESPONSE.result)) ]
    	sb.append(TAB2).append(TABLE).append(L_PAREN).append(EXEC).append(SPACE);
    	sb.append(getProcedureFullName(getResponseInfo()));
    	sb.append(L_PAREN).append(RESPONSE).append(DOT).append(RESULT_LOWER).append(R_PAREN).append(R_PAREN).append(RETURN);
    	sb.append(TAB4).append(AS).append(SPACE).append(tableAlias).append(SEMI_COLON);
    	
    	sb.append(SQL_END);
		
    	return sb.toString();
	}
}
