package com.metamatrix.modeler.modelgenerator.wsdl.procedures;

import org.eclipse.xsd.XSDTypeDefinition;

import com.metamatrix.modeler.core.ModelerCoreException;

public interface TraversalContext {

	public abstract boolean isReachedResultNode();

	public abstract void setReachedResultNode(boolean reachedResultNode);

	public abstract void appendToPath(String path);

	public abstract void addColumn(String name, XSDTypeDefinition type)
			throws ModelerCoreException;

	public abstract String getPath();

	public abstract void createTransformation();

}