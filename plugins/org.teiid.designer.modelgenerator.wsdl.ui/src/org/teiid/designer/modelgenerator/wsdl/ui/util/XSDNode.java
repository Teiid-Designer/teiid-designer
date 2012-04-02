/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.util;

import java.util.Collection;

public class XSDNode {
	Object schemaObject;
	String name;
	XSDNode parent;
	Collection<XSDNode> children;
	
	public XSDNode() {
		super();
	}
	
	public XSDNode(String name, Object schemaObject) {
		super();
		this.name = name;
		this.schemaObject = schemaObject;
	}

	/**
	 * @return the schemaObject
	 */
	public Object getSchemaObject() {
		return this.schemaObject;
	}

	/**
	 * @param schemaObject the schemaObject to set
	 */
	public void setSchemaObject(Object schemaObject) {
		this.schemaObject = schemaObject;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parent
	 */
	public XSDNode getParent() {
		return this.parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(XSDNode parent) {
		this.parent = parent;
	}

}
