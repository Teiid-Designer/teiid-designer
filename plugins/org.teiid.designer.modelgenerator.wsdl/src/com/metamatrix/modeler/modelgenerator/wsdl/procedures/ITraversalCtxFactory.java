package com.metamatrix.modeler.modelgenerator.wsdl.procedures;

public interface ITraversalCtxFactory {

	public abstract TraversalContext getTraversalContext(String procedureName,
			TraversalContext ctx, ProcedureBuilder builder);

	public abstract TraversalContext getTraversalContext(String procedureName,
			ProcedureBuilder builder);

}