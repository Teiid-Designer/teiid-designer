package org.teiid.designer.modelgenerator.xsd.procedures;

import javax.xml.namespace.QName;

import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.metamodels.relational.RelationalFactory;


/**
 * @since 8.0
 */
public class BaseTraversalContext {
	
	protected RelationalFactory factory = org.teiid.designer.metamodels.relational.RelationalPackage.eINSTANCE
	.getRelationalFactory();
	protected DatatypeManager datatypeManager = ModelerCore.getBuiltInTypesManager();

	
	protected StringBuffer elementPath = new StringBuffer();
	protected  String procedureName;
	protected  ProcedureBuilder builder;
	private boolean reachedResultNode = false;
	protected QName namespace;
	
	public BaseTraversalContext(String procedureName, QName namespace,
			TraversalContext ctx, ProcedureBuilder builder) {
		this.procedureName = procedureName;
		this.namespace = namespace;
		this.builder = builder;
		this.elementPath = new StringBuffer(ctx.getPath());

	}

	public BaseTraversalContext(String procedureName, QName namespace,
			ProcedureBuilder builder) {
		this.procedureName = procedureName;
		this.namespace = namespace;
		this.builder = builder;
	}
	
	/* (non-Javadoc)
	 * @See org.teiid.designer.modelgenerator.wsdl.TraversalContext#isReachedResultNode()
	 */
	public boolean isReachedResultNode() {
		return reachedResultNode;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.modelgenerator.wsdl.TraversalContext#setReachedResultNode(boolean)
	 */
	public void setReachedResultNode(boolean reachedResultNode) {
		this.reachedResultNode = reachedResultNode;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.modelgenerator.wsdl.TraversalContext#appendToPath(java.lang.String)
	 */
	public void appendToPath(String path) {
		elementPath.append('/').append(path);
	}

	public String getPath() {
		String result = elementPath.toString();
		if(null == result || result.isEmpty()) {
			result = "/"; //$NON-NLS-1$
		}
		return result;
	}

	public QName getNamespace() {
		return namespace;
	}

	public void setNamespace(QName namespace) {
		this.namespace = namespace;
	}
}
