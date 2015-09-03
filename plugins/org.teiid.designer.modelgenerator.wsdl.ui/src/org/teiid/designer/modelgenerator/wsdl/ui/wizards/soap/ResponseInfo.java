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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.query.IProcedureService;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.proc.wsdl.IWsdlResponseInfo;


/**
 * @since 8.0
 */
public class ResponseInfo extends ProcedureInfo implements IWsdlResponseInfo {
	
	public ResponseInfo(Operation operation, ProcedureGenerator generator) {
		super(operation, ProcedureType.RESPONSE, generator);
		setProcedureName(getDefaultProcedureName());
	}
	
	@Override
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

		if( getBodyColumnInfoList().length == 0 ) {
			status.add(new Status(IStatus.ERROR, ProcedureGenerator.PLUGIN_ID,
				NLS.bind(Messages.Error_NoColumnsDefinedForResponseProcedureForOperation_0, 
					getOperation().getName(), getOperation().getName())));
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
	public String getSqlStringTemplate() {
		return getSqlString(new Properties());
	}

	@Override
	public String getSqlString(Properties properties) {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IProcedureService procedureService = queryService.getProcedureService();
        return procedureService.getSQLStatement(this, properties);
	}
}
