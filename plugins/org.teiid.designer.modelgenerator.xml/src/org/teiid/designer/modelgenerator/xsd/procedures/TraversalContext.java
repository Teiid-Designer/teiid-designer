package org.teiid.designer.modelgenerator.xsd.procedures;

import org.eclipse.xsd.XSDTypeDefinition;
import org.teiid.designer.core.ModelerCoreException;


/**
 * @since 8.0
 */
public interface TraversalContext {

	public abstract boolean isReachedResultNode();

	public abstract void setReachedResultNode(boolean reachedResultNode);

	public abstract void appendToPath(String path);

	public abstract void addColumn(String name, XSDTypeDefinition type)
			throws ModelerCoreException;

	public abstract String getPath();

	public abstract void createTransformation();

}