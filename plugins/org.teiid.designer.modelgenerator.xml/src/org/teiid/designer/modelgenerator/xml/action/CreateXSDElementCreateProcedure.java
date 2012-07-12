package org.teiid.designer.modelgenerator.xml.action;

import org.teiid.designer.modelgenerator.xsd.procedures.ITraversalCtxFactory;
import org.teiid.designer.modelgenerator.xsd.procedures.RequestTraversalContextFactory;


public class CreateXSDElementCreateProcedure extends XSDElementProcedureBaseAction {

	@Override
	public ITraversalCtxFactory getTraversalCtxFactory() {
		return new RequestTraversalContextFactory();
	}
}
