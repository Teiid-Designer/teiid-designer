package org.teiid.designer.modelgenerator.xml.action;

import org.teiid.designer.modelgenerator.xsd.procedures.ITraversalCtxFactory;
import org.teiid.designer.modelgenerator.xsd.procedures.RequestTraversalContextFactory;


/**
 * @since 8.0
 */
public class CreateXSDElementCreateProcedure extends XSDElementProcedureBaseAction {

	@Override
	public ITraversalCtxFactory getTraversalCtxFactory() {
		return new RequestTraversalContextFactory();
	}
}
