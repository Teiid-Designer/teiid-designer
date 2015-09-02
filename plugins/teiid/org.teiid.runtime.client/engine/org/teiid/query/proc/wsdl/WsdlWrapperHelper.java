/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.proc.wsdl;

import java.util.Properties;
import org.teiid.designer.query.proc.wsdl.IWsdlAttributeInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlColumnInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlConstants;
import org.teiid.designer.query.proc.wsdl.IWsdlProcedureInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlRequestInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlResponseInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlWrapperInfo;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 *
 */
public class WsdlWrapperHelper extends AbstractWsdlHelper implements IWsdlConstants, ISQLConstants {

    private final IWsdlWrapperInfo wrapperInfo;
    private final IWsdlRequestInfo requestInfo;
    private final IWsdlResponseInfo responseInfo;

    /**
     * @param teiidVersion 
     * @param wrapperInfo
     */
    public WsdlWrapperHelper(ITeiidServerVersion teiidVersion, IWsdlWrapperInfo wrapperInfo) {
        super(teiidVersion);
        this.wrapperInfo = wrapperInfo;
        this.requestInfo = wrapperInfo.getRequestInfo();
        this.responseInfo = wrapperInfo.getResponseInfo();
    }
    
    private String getWrapperProcedureParameterName(String parameterName) {
        StringBuilder builder = new StringBuilder();
        builder.append(wrapperInfo.getViewModelName());

        builder.append(DOT).append(wrapperInfo.getWrapperProcedureName()).append(DOT).append(parameterName);

        return builder.toString();
    }
    
    private String getProcedureFullName(IWsdlProcedureInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append(wrapperInfo.getViewModelName());
        builder.append('.').append(info.getProcedureName());
        
        return builder.toString();
    }

    private String getModelNameWithoutExtension(String modelName) {
        String name = modelName;
        if (name.endsWith(XMI_EXTENSION)) {
            name = name.substring(0, name.lastIndexOf(XMI_EXTENSION));
        }
        return name;
    }

    private String getParameterFullName(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(wrapperInfo.getViewModelName());
        builder.append('.').append(wrapperInfo.getWrapperProcedureName()).append('.').append(convertSqlNameSegment(name));

        return builder.toString();
    }

    /**
     * @return the sql wrapper statement
     */
    public String getWrapperStatement() {
        /**
        CREATE VIRTUAL PROCEDURE
        BEGIN
             SELECT t.* FROM 
                  TABLE(EXEC CountryInfoServiceXML.CapitalCity.create_CapitalCity(OPS.GETCAPITALCITY.countryISOCode)) 
             AS request, 
             TABLE(EXEC CountryInfoService.invoke('SOAP11', null, REQUEST.xml_out, null, TRUE)) 
             AS response, 
             TABLE(EXEC CountryInfoServiceXML.CapitalCity.extract_CapitalCityResponse(RESPONSE.result)) 
             AS t;
         END
             
         CREATE VIRTUAL PROCEDURE
         BEGIN
             SELECT t.* FROM 
                TABLE(EXEC <view-model-name>.<request_procedure>(OPS.GETCAPITALCITY.countryISOCode)) 
             AS request, 
                TABLE(EXEC <source-model-name>.invoke('SOAP11', null, REQUEST.xml_out, null, true)) 
             AS response, 
                TABLE(EXEC <view-model-name>.<response_procedure>(RESPONSE.result)) 
             AS t;
         END
        */
        
        IWsdlColumnInfo[] reqBodyColumnInfoList = requestInfo.getBodyColumnInfoList();

        StringBuilder sb = new StringBuilder();

        String tableAlias = "t"; //$NON-NLS-1$

        sb.append(SQL_BEGIN);
        // SELECT t.* FROM 
        sb.append(TAB).append(SELECT).append(SPACE).append(tableAlias)
            .append(DOT).append(STAR).append(SPACE).append(FROM).append(RETURN);
        
        // TABLE(EXEC 
        sb.append(TAB).append(TAB).append(TABLE_EXEC);
        // <view-model-name>.<request_procedure>
        sb.append(getModelNameWithoutExtension(wrapperInfo.getViewModelName()));
        sb.append(DOT).append(requestInfo.getProcedureName());

        // (OPS.GETCAPITALCITY.countryISOCode))
        sb.append(L_PAREN);
        int nColumns = reqBodyColumnInfoList.length;
        int i = 0;
        for (IWsdlColumnInfo columnInfo : reqBodyColumnInfoList) {
            String name = columnInfo.getSymbolName();
            sb.append(getParameterFullName(name));

            int nAttributes = columnInfo.getAttributeInfoArray().length;
            if (nAttributes > 0) {
                int index = 0;
                sb.append(COMMA).append(SPACE);
                for (IWsdlAttributeInfo attrInfo : columnInfo.getAttributeInfoArray()) {
                    sb.append(getParameterFullName(attrInfo.getSymbolName()));
                    if (nAttributes > 1 && index < nAttributes - 1) {
                        sb.append(COMMA).append(SPACE);
                    }
                    index++;
                }
            }

            if (i < (nColumns - 1)) {
                sb.append(COMMA).append(SPACE);
            }
            i++;
        }
        sb.append(R_PAREN).append(RETURN);

        // AS request,
        sb.append(TAB).append(AS).append(SPACE)
            .append(REQUEST_LOWER).append(COMMA).append(RETURN);

        // TABLE(EXEC <source-model-name>.invoke(binding in STRING, action in STRING, request in XML, endpoint in STRING, stream in BOOLEAN))
        sb.append(TAB).append(TAB).append(TABLE_EXEC)
            .append(getModelNameWithoutExtension(wrapperInfo.getSourceModelName()))
            .append(DOT).append(INVOKE_SEGMENT_1).append(wrapperInfo.getBindingType())
            .append(INVOKE_SEGMENT_2).append(RETURN);

        // AS response,
        sb.append(TAB).append(AS).append(SPACE).append(RESPONSE_LOWER)
            .append(COMMA).append(RETURN);

        // TABLE(EXEC <view-model-name>.<response_procedure>(RESPONSE.result))  
        sb.append(TAB).append(TAB).append(TABLE_EXEC)
            .append(getModelNameWithoutExtension(wrapperInfo.getViewModelName()))
            .append(DOT).append(responseInfo.getProcedureName())
            .append(L_PAREN).append(RESPONSE).append(DOT)
            .append(RESULT_LOWER).append(R_PAREN).append(R_PAREN).append(RETURN);

        // AS t;
        sb.append(TAB).append(AS).append(SPACE)
            .append(tableAlias).append(SEMI_COLON).append(RETURN);

        sb.append(SQL_END);

        return sb.toString();
    }

    /**
     * @param properties 
     * @return the wrapper procedure statement
     */
    public String getWrapperProcedureStatement(Properties properties) {
        /*
        CREATE VIRTUAL PROCEDURE
        BEGIN
            SELECT t.* FROM 
                TABLE(EXEC PriceServiceView.GetPrice_request(
                        PriceServiceView.GetPrice_procedure.productID,
                        PriceServiceView.GetPrice_procedure.productName)) 
                    AS request, 
                TABLE(EXEC PriceService.invoke('SOAP11', null, REQUEST.xml_out, null, true)) 
                    AS response, 
                TABLE(EXEC PriceServiceView.GetPrice_response(RESPONSE.result)) 
                    AS t;
        END
        */

        IWsdlColumnInfo[] reqBodyColumnInfoList = requestInfo.getBodyColumnInfoList();

        StringBuilder sb = new StringBuilder();

        String tableAlias = "t"; //$NON-NLS-1$

        sb.append(SQL_BEGIN);
        // SELECT t.* FROM 
        sb.append(TAB).append(SELECT).append(SPACE)
            .append(tableAlias).append(DOT).append(STAR)
            .append(SPACE).append(FROM).append(RETURN);

        // Request TABLE
        sb.append(TAB2).append(TABLE).append(L_PAREN)
            .append(EXEC).append(SPACE);

        sb.append(getProcedureFullName(requestInfo));
        sb.append(L_PAREN);
        
        int i = 0;
        int nColumns = reqBodyColumnInfoList.length;
        int hColumns = requestInfo.getHeaderColumnInfoList().length;

        for (IWsdlColumnInfo columnInfo : requestInfo.getHeaderColumnInfoList()) {
            String nameSegment = convertSqlNameSegment(columnInfo.getSymbolName());
            sb.append(TAB4).append(getWrapperProcedureParameterName(nameSegment));
            if (i < (hColumns - 1)) {
                sb.append(COMMA).append(SPACE).append(RETURN);
            }
            i++;
        }

        i = 0;
        nColumns = reqBodyColumnInfoList.length;
        if (hColumns > 0) sb.append(COMMA);
        for (IWsdlColumnInfo columnInfo : reqBodyColumnInfoList) {
            int nAttributes = columnInfo.getAttributeInfoArray().length;
            String nameSegment = convertSqlNameSegment(columnInfo.getSymbolName());
            sb.append(TAB4).append(getWrapperProcedureParameterName(nameSegment));
            if (i < (nColumns - 1)) {
                sb.append(COMMA).append(SPACE).append(RETURN);
            }
            i++;
            if (nAttributes > 0) {
                int index = 0;
                sb.append(COMMA).append(SPACE);
                for (IWsdlAttributeInfo attrInfo : columnInfo.getAttributeInfoArray()) {
                    String attrNameSegment = convertSqlNameSegment(attrInfo.getSymbolName());
                    sb.append(TAB4).append(getWrapperProcedureParameterName(attrNameSegment));
                    if (nAttributes > 1 && index < nAttributes - 1) {
                        sb.append(COMMA).append(SPACE);
                    }
                    index++;
                }
            }
        }
        
        sb.append(R_PAREN);
        sb.append(R_PAREN).append(RETURN).append(TAB4)
            .append(AS).append(SPACE).append(REQUEST_LOWER)
            .append(COMMA).append(RETURN);
        
        //invoke(binding in STRING, action in STRING,
        //request in XML, endpoint in STRING, stream in BOOLEAN)

        // Response TABLE
        sb.append(TAB2).append(TABLE).append(L_PAREN)
            .append(EXEC).append(SPACE);
        sb.append(getModelNameWithoutExtension(wrapperInfo.getSourceModelName()))
            .append(DOT);
        
        String actionStr = S_QUOTE + wrapperInfo.getSoapAction() + S_QUOTE;
        sb.append(FUNCTION_INVOKE);

        sb.append(L_PAREN).append(BINDING_PARAM).append(PARAM_ASSIGNMENT).append(S_QUOTE).append(wrapperInfo.getBindingType())
            .append(S_QUOTE).append(COMMA).append(SPACE).append(ACTION_PARAM).append(PARAM_ASSIGNMENT).append(SPACE).append(actionStr)
            .append(COMMA).append(SPACE).append(REQUEST_PARAM).append(PARAM_ASSIGNMENT).append(REQUEST).append(DOT).append(XML_OUT)
            .append(COMMA).append(SPACE).append(ENDPOINT_PARAM).append(PARAM_ASSIGNMENT).append(NULL_LOWER)
            .append(COMMA).append(SPACE).append(STREAM_PARAM).append(PARAM_ASSIGNMENT).append(TRUE)
            .append(R_PAREN);
        
        sb.append(R_PAREN).append(RETURN).append(TAB4).append(AS)
            .append(SPACE).append(RESPONSE_LOWER)
            .append(COMMA).append(RETURN);

        // Request TABLE:  [        TABLE(EXEC PriceServiceView.GetPrice_response(RESPONSE.result)) ]
        sb.append(TAB2).append(TABLE).append(L_PAREN).append(EXEC).append(SPACE);
        sb.append(getProcedureFullName(responseInfo));
        sb.append(L_PAREN).append(RESPONSE).append(DOT)
            .append(RESULT_LOWER).append(R_PAREN)
            .append(R_PAREN).append(RETURN);
        sb.append(TAB4).append(AS).append(SPACE).append(tableAlias)
            .append(SEMI_COLON);

        sb.append(SQL_END);

        return sb.toString();
    }

}
