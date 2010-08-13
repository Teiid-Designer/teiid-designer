package com.metamatrix.modeler.modelgenerator.wsdl.procedures;

import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;

public class BaseTraversalContext {
	
	protected RelationalFactory factory = com.metamatrix.metamodels.relational.RelationalPackage.eINSTANCE
	.getRelationalFactory();
	protected DatatypeManager datatypeManager = ModelerCore.getBuiltInTypesManager();

	
	protected StringBuffer elementPath = new StringBuffer();
	protected  String procedureName;
	protected  ProcedureBuilder builder;
	private boolean reachedResultNode = false;
	
	public BaseTraversalContext(String procedureName,
			TraversalContext ctx, ProcedureBuilder builder) {
		this.procedureName = procedureName;
		this.builder = builder;
		this.elementPath = new StringBuffer(ctx.getPath());

	}

	public BaseTraversalContext(String procedureName,
			ProcedureBuilder builder) {
		this.procedureName = procedureName;
		this.builder = builder;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.TraversalContext#isReachedResultNode()
	 */
	public boolean isReachedResultNode() {
		return reachedResultNode;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.TraversalContext#setReachedResultNode(boolean)
	 */
	public void setReachedResultNode(boolean reachedResultNode) {
		this.reachedResultNode = reachedResultNode;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.wsdl.TraversalContext#appendToPath(java.lang.String)
	 */
	public void appendToPath(String path) {
		elementPath.append('/').append(path);
	}

	public String getPath() {
		String result = elementPath.toString();
		if(null == result || result.isEmpty()) {
			result = "/";
		}
		return result;
	}
}
