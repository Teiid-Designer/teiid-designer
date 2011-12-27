package com.metamatrix.modeler.modelgenerator.xml.action;

import com.metamatrix.modeler.modelgenerator.xsd.procedures.ITraversalCtxFactory;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.RequestTraversalContextFactory;


public class CreateXSDElementCreateProcedure extends XSDElementProcedureBaseAction {

	@Override
	public ITraversalCtxFactory getTraversalCtxFactory() {
		return new RequestTraversalContextFactory();
	}
}
