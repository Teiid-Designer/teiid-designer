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
import org.teiid.designer.query.proc.wsdl.IWsdlRequestInfo;


/**
 * @since 8.0
 */
public class RequestInfo extends ProcedureInfo implements IWsdlRequestInfo {

	public RequestInfo(Operation operation, ProcedureGenerator generator) {
		super(operation, ProcedureType.REQUEST, generator);
		setProcedureName(getDefaultProcedureName());
	}
	
	@Override
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
		
		if( getBodyColumnInfoList().length == 0 ) {
			status.add(new Status(IStatus.WARNING, ProcedureGenerator.PLUGIN_ID,
				NLS.bind(Messages.Error_NoElementsDefinedForRequestProcedureForOperation_0, 
					getOperation().getName(), getOperation().getName())));
		}

		// Check Request Info
		setChanged(false);
		return status;
	}

	@Override
	public String getSqlStringTemplate() {
		return getSqlString(new Properties());
	}

    @SuppressWarnings("unused")
	@Override
	public String getSqlString(Properties properties) {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IProcedureService procedureService = queryService.getProcedureService();
        return procedureService.getSQLStatement(this, properties);
	}
    
    public boolean isMessageServiceMode() {
        return getGenerator().getImportManager().isMessageServiceMode();
    }

}
