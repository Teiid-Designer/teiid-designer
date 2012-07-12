package org.teiid.designer.modelgenerator.xsd.procedures;

import javax.xml.namespace.QName;

import org.teiid.core.util.CoreArgCheck;


public class ResultTraversalContextFactory implements ITraversalCtxFactory {
	
	/* (non-Javadoc)
	 * @See org.teiid.designer.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, org.teiid.designer.modelgenerator.wsdl.ResultBuilderTraversalContext, org.teiid.designer.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, QName namespace, TraversalContext ctx, ProcedureBuilder builder) {
		CoreArgCheck.isNotNull(procedureName, "procedure name is null"); //$NON-NLS-1$
		return new ResultBuilderTraversalContext(procedureName, namespace, ctx, builder);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, org.teiid.designer.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, QName namespace, ProcedureBuilder builder) {
		CoreArgCheck.isNotNull(procedureName, "procedure name is null"); //$NON-NLS-1$
		return new ResultBuilderTraversalContext(procedureName, namespace, builder);
	}
}
