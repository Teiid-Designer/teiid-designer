package org.teiid.designer.modelgenerator.xsd.procedures;

import javax.xml.namespace.QName;

/**
 * @since 8.0
 */
public class RequestTraversalContextFactory implements ITraversalCtxFactory {
	
	/* (non-Javadoc)
	 * @See org.teiid.designer.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, org.teiid.designer.modelgenerator.wsdl.ResultBuilderTraversalContext, org.teiid.designer.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, QName namespace, TraversalContext ctx, ProcedureBuilder builder) {
		return new RequestBuilderTraversalContext(procedureName, namespace, ctx, builder);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.modelgenerator.wsdl.IResultTraversalFactory#getTraversalContext(java.lang.String, org.teiid.designer.modelgenerator.wsdl.ProcedureResultBuilder)
	 */
	@Override
	public TraversalContext getTraversalContext(String procedureName, QName namespace, ProcedureBuilder builder) {
		return new RequestBuilderTraversalContext(procedureName, namespace, builder);
	}
}
