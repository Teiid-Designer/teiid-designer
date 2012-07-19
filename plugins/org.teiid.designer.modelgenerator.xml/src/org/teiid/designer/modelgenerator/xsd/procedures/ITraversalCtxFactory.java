package org.teiid.designer.modelgenerator.xsd.procedures;

import javax.xml.namespace.QName;

/**
 * @since 8.0
 */
public interface ITraversalCtxFactory {

	public abstract TraversalContext getTraversalContext(String procedureName, QName namespace,
			TraversalContext ctx, ProcedureBuilder builder);

	public abstract TraversalContext getTraversalContext(String procedureName, QName namespace,
			ProcedureBuilder builder);

}