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

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;

public class ResponseInfo extends ProcedureInfo {

	private String defaultNSPrefix;

	public ResponseInfo(Operation operation, ProcedureGenerator generator) {
		super(operation, RESPONSE, generator);
		setProcedureName(operation.getName() + "_response"); //$NON-NLS-1$
	}

	@Override
	public IStatus validate() {
		IStatus status = Status.OK_STATUS;
		// Go through objects and look for problems
		if (getProcedureName() == null) {
			return new Status(IStatus.ERROR, ProcedureGenerator.PLUGIN_ID,
					Messages.Error_ResponseProcedureNameCannotBeNullOrEmpty);
		}

		IStatus nameStatus = getGenerator().getNameStatus(getProcedureName());
		if (nameStatus.getSeverity() > IStatus.INFO) {
			return nameStatus;
		}

		// Look at all element names
		for (ColumnInfo info : getColumnInfoList()) {
			IStatus colNameStatus = getGenerator()
					.getNameStatus(info.getName());
			if (colNameStatus.getSeverity() > IStatus.INFO) {
				return colNameStatus;
			}
		}

		// Look at all element xpaths
		// for( ColumnInfo info : getColumnInfoList() ) {
		// info.getRelativePath()
		// if( colNameStatus.getSeverity() > IStatus.INFO) {
		// return colNameStatus;
		// }
		// }

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

		sb.append(getXmlTableString());

		sb.append(R_PAREN);
		sb.append(SPACE).append(AS).append(SPACE).append(alias)
				.append(SEMI_COLON);
		sb.append(SQL_END);

		return sb.toString();
	}

	private String getXmlTableString() {
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
						getResponseProcedureParameter())).append(RETURN);

		sb.append(TAB).append(COLUMNS).append(SPACE).append(RETURN);

		int i = 0;
		int nColumns = getColumnInfoList().length;

		for (ColumnInfo columnInfo : getColumnInfoList()) {
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
			if (uri.equals(this.getGenerator().getNamespaceURI())) {
				//This is the default NS
				sb.append(DEFAULT).append(SPACE).append(S_QUOTE).append(uri).append(S_QUOTE);
				this.defaultNSPrefix = prefix;
			} else {
				sb.append(S_QUOTE).append(uri).append(S_QUOTE).append(SPACE)
						.append(AS).append(SPACE).append(prefix);
			}
			i++;
		}
		sb.append(R_PAREN).append(SPACE).append(COMMA).append(SPACE);

		return sb.toString();
	}

	private String getResponseProcedureParameter() {
		StringBuilder sb = new StringBuilder();
		sb.append(getGenerator().getViewModelName()).append(DOT)
				.append(getProcedureName()).append(DOT)
				.append("xml_in").append(SPACE); //$NON-NLS-1$
		return sb.toString();
	}
}
