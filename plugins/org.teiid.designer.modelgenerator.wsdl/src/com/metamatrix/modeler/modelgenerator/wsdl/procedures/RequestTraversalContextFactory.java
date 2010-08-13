package com.metamatrix.modeler.modelgenerator.wsdl.procedures;

public class RequestTraversalContextFactory implements ITraversalCtxFactory {
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, com.metamatrix.modeler.modelgenerator.wsdl.ResultBuilderTraversalContext, com.metamatrix.modeler.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, TraversalContext ctx, ProcedureBuilder builder) {
		return new RequestBuilderTraversalContext(procedureName, ctx, builder);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, com.metamatrix.modeler.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, ProcedureBuilder builder) {
		return new RequestBuilderTraversalContext(procedureName, builder);
	}
}
