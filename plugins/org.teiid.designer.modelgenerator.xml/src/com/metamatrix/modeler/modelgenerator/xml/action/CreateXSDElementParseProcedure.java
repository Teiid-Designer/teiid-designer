package com.metamatrix.modeler.modelgenerator.xml.action;

import com.metamatrix.modeler.modelgenerator.xsd.procedures.ITraversalCtxFactory;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.RequestTraversalContextFactory;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.ResultTraversalContextFactory;


public class CreateXSDElementParseProcedure extends XSDElementProcedureBaseAction {

	@Override
	public ITraversalCtxFactory getTraversalCtxFactory() {
		return new ResultTraversalContextFactory();
	}



	
}
