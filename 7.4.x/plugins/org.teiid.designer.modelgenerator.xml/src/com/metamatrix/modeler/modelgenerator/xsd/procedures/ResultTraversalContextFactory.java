package com.metamatrix.modeler.modelgenerator.xsd.procedures;

import javax.xml.namespace.QName;

import com.metamatrix.core.util.CoreArgCheck;

public class ResultTraversalContextFactory implements ITraversalCtxFactory {
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, com.metamatrix.modeler.modelgenerator.wsdl.ResultBuilderTraversalContext, com.metamatrix.modeler.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, QName namespace, TraversalContext ctx, ProcedureBuilder builder) {
		CoreArgCheck.isNotNull(procedureName, "procedure name is null"); //$NON-NLS-1$
		return new ResultBuilderTraversalContext(procedureName, namespace, ctx, builder);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, com.metamatrix.modeler.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, QName namespace, ProcedureBuilder builder) {
		CoreArgCheck.isNotNull(procedureName, "procedure name is null"); //$NON-NLS-1$
		return new ResultBuilderTraversalContext(procedureName, namespace, builder);
	}
}
