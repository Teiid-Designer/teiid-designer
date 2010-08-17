package com.metamatrix.modeler.modelgenerator.wsdl.procedures;

import javax.xml.namespace.QName;

public class RequestTraversalContextFactory implements ITraversalCtxFactory {
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, com.metamatrix.modeler.modelgenerator.wsdl.ResultBuilderTraversalContext, com.metamatrix.modeler.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, QName namespace, TraversalContext ctx, ProcedureBuilder builder) {
		return new RequestBuilderTraversalContext(procedureName, namespace, ctx, builder);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, com.metamatrix.modeler.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, QName namespace, ProcedureBuilder builder) {
		return new RequestBuilderTraversalContext(procedureName, namespace, builder);
	}
}
