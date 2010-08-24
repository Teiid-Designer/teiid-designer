package com.metamatrix.modeler.modelgenerator.xsd.procedures;

import javax.xml.namespace.QName;

public interface ITraversalCtxFactory {

	public abstract TraversalContext getTraversalContext(String procedureName, QName namespace,
			TraversalContext ctx, ProcedureBuilder builder);

	public abstract TraversalContext getTraversalContext(String procedureName, QName namespace,
			ProcedureBuilder builder);

}