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

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;

public class ResponseInfo extends ProcedureInfo {

	private String defaultNSPrefix;
	public static String SOAPENVELOPE_ROOTPATH = "/soap:Envelope";//$NON-NLS-1$
	public static String SOAPHEADER_ROOTPATH = "/soap:Header";//$NON-NLS-1$
	public static String SOAPBODY_ROOTPATH = "/soap:Body";//$NON-NLS-1$
	public static String DEFAULT_NS = "ns";//$NON-NLS-1$
	
	public ResponseInfo(Operation operation, ProcedureGenerator generator) {
		super(operation, RESPONSE, generator);
		setProcedureName(getDefaultProcedureName());
	}
	
	public String getDefaultProcedureName() {
		return getOperation().getName() + "_response";//$NON-NLS-1$
	}

	@Override
	public IStatus validate() {
		MultiStatus status = new MultiStatus(ProcedureGenerator.PLUGIN_ID, 0, null, null);
		
		// Go through objects and look for problems
		if (getProcedureName() == null) {
			status.add(new Status(IStatus.ERROR, ProcedureGenerator.PLUGIN_ID,
				NLS.bind(Messages.Error_Operation_0_ResponseProcedureNameCannotBeNullOrEmpty,
					getOperation().getName())));
		}

		IStatus nameStatus = getGenerator().getNameStatus(getProcedureName());
		if (nameStatus.getSeverity() > IStatus.INFO) {
			status.merge(nameStatus);
		}

		// Look at all element names
		for (ColumnInfo info : getBodyColumnInfoList()) {
			IStatus colNameStatus = getGenerator().getNameStatus(info.getName());
			if (colNameStatus.getSeverity() > IStatus.INFO) {
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
			status.add(new Status(IStatus.ERROR, ProcedureGenerator.PLUGIN_ID,
				NLS.bind(Messages.Error_NoColumnsDefinedForResponseProcedureForOperation_0, 
					getOperation().getName())));
		}

		// Look at all element xpaths
		 for( ColumnInfo info : getBodyColumnInfoList() ) {
			 if( info.getStatus().getSeverity() > IStatus.INFO) {
				 return info.getStatus();
			 }
		 }
		
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
		// Generated SQL example for extract:
		//
		// CREATE VIRTUAL PROCEDURE
		// BEGIN
		//     SELECT t.* FROM XMLTABLE(XMLNAMESPACES(DEFAULT
		// 'http://quickstart.samples/xsd'), '/getPriceResponse' PASSING xml_in
		// COLUMNS return_ double) AS t;
		// END
		// =============================================
		// Generated SQL example for extract:
		//
		// CREATE VIRTUAL PROCEDURE
		// BEGIN
		//     SELECT t.* FROM
		// XMLTABLE( XMLNAMESPACES(DEFAULT
		// 'http://www.oorsprong.org/websamples.countryinfo'), 
		//           '/CapitalCityResponse' 
		//                PASSING
		// COUNTRYINFOSERVICEXML.CAPITALCITY.EXTRACT_CAPITALCITYRESPONSE.xml_in 
		//                     COLUMNS CapitalCityResult string) 
		//      AS t;
		// END
		//
		// CREATE VIRTUAL PROCEDURE
		// BEGIN
		// END
		//

		String alias = "t"; //$NON-NLS-1$
		StringBuffer sb = new StringBuffer();
		int i = 0;
		sb.append(SQL_BEGIN);
		sb.append(TAB).append(SELECT).append(SPACE).append(alias).append(DOT)
				.append(STAR).append(SPACE).append(FROM).append(RETURN);
		sb.append(TAB).append(TAB).append(XMLTABLE).append(L_PAREN);

		sb.append(getXmlTableString(properties));

		sb.append(R_PAREN);
		sb.append(SPACE).append(AS).append(SPACE).append(alias)
				.append(SEMI_COLON);
		sb.append(SQL_END);

		return sb.toString();
	}

	private String getXmlTableString(Properties properties) {
		
		// Response procedure name may have been overridden
		String responseProcedureName = properties.getProperty(ProcedureGenerator.KEY_RESPONSE_PROCEDURE_NAME);
		if( responseProcedureName == null ) {
			responseProcedureName = this.getProcedureName();
		}
		
		StringBuilder sb = new StringBuilder();
		String namespaceStr = getNamespaceString();

		if (namespaceStr != null) {
			sb.append(namespaceStr);
		}

		String xQueryExp = DEFAULT_XQUERY;
		if (getRootPath() != null && getRootPath().length() > 0) {
			xQueryExp = getRootPath();
		}
		sb.append(S_QUOTE).append(xQueryExp).append(S_QUOTE).append(SPACE);

		sb.append(PASSING)
				.append(SPACE)
				.append(getGenerator().convertSqlNameSegment(
						getResponseProcedureParameter(responseProcedureName))).append(RETURN);

		sb.append(TAB).append(COLUMNS).append(SPACE).append(RETURN);

		int i = 0;
		int nColumns = getBodyColumnInfoList().length;

		for (ColumnInfo columnInfo : getBodyColumnInfoList()) {
			if (columnInfo.getOrdinality()) {
				sb.append(columnInfo.getName()).append(SPACE)
						.append(FOR_ORDINALITY);
			} else {
				sb.append(TAB)
						.append(TAB)
						.append(getGenerator().convertSqlNameSegment(
								columnInfo.getName())).append(SPACE)
						.append(columnInfo.getDatatype());

				String defValue = columnInfo.getDefaultValue();
				if (defValue != null && defValue.length() > 0) {
					sb.append(SPACE).append(DEFAULT).append(SPACE)
							.append(S_QUOTE).append(defValue).append(S_QUOTE);
				}

				String relPath = columnInfo.getRelativePath();
				if (relPath != null && relPath.length() > 1) {
					//Strip out default namespace prefix
					relPath = relPath.replace(this.defaultNSPrefix + COLON, StringUtilities.EMPTY_STRING);
					sb.append(SPACE).append(PATH).append(SPACE).append(S_QUOTE)
							.append(relPath).append(S_QUOTE);
				}

			}
			if (i < (nColumns - 1)) {
				sb.append(COMMA).append(SPACE).append(RETURN);
			}

			i++;
		}

		return sb.toString();
	}

	private String getNamespaceString() {
		//
		// EXAMPLE: XMLNAMESPACES('http://www.kaptest.com/schema/1.0/party' AS
		// pty)
		//

		if (getNamespaceMap().isEmpty()) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		sb.append(XMLNAMESPACES).append(L_PAREN);
		int i = 0;
		for (String prefix : getNamespaceMap().keySet()) {
			if (prefix.equalsIgnoreCase(XSI_NAMESPACE_PREFIX)) {
				continue;
			}
			if (i > 0) {
				sb.append(COMMA).append(SPACE);
			}
			String uri = getNamespaceMap().get(prefix);
//			Object defaultNs = this.getTreeModel().getDefaultNamespace();
//			if (uri.equals(defaultNs)) {
//				//This is the default NS
//				sb.append(DEFAULT).append(SPACE).append(S_QUOTE).append(uri).append(S_QUOTE);
//				this.defaultNSPrefix = prefix;
//			} else {
				sb.append(S_QUOTE).append(uri).append(S_QUOTE).append(SPACE)
						.append(AS).append(SPACE).append(prefix);
//			}
			i++;
		}
		sb.append(R_PAREN).append(SPACE).append(COMMA).append(SPACE);

		return sb.toString();
	}

	private String getResponseProcedureParameter(String procedureName) {
		StringBuilder sb = new StringBuilder();
		sb.append(getGenerator().getViewModelName()).append(DOT)
				.append(procedureName).append(DOT)
				.append("xml_in").append(SPACE); //$NON-NLS-1$
		return sb.toString();
	}
}
