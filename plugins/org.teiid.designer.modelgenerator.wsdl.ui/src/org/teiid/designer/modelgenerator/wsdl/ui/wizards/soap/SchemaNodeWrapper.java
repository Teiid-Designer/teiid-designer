/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.List;

import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;

public class SchemaNodeWrapper {
	List<SchemaNode> schemaNodeList;
	
	public SchemaNodeWrapper(List<SchemaNode> nodeList) {
		super();
		this.schemaNodeList = nodeList;
	}
	
	public List<SchemaNode> getChildren() {
		return this.schemaNodeList;
	}
}
