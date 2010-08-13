package com.metamatrix.modeler.modelgenerator.wsdl.procedures;

import com.metamatrix.core.util.CoreArgCheck;

public class ResultTraversalContextFactory implements ITraversalCtxFactory {
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, com.metamatrix.modeler.modelgenerator.wsdl.ResultBuilderTraversalContext, com.metamatrix.modeler.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, TraversalContext ctx, ProcedureBuilder builder) {
		CoreArgCheck.isNotNull(procedureName, "procedure name is null");
		return new ResultBuilderTraversalContext(procedureName, ctx, builder);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, com.metamatrix.modeler.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, ProcedureBuilder builder) {
		CoreArgCheck.isNotNull(procedureName, "procedure name is null");
		return new ResultBuilderTraversalContext(procedureName, builder);
	}
}
