/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.wsdl.soap;

import org.jdom.Namespace;


import com.metamatrix.modeler.schema.tools.model.jdbc.Column;
import com.metamatrix.modeler.schema.tools.model.jdbc.Table;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.TableImpl;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public class SOAPTable implements Table {
	private Table table;
	boolean m_isRequestTable = false;
	String m_soapAction;
	SoapBindingInfo m_bindingInfo;
	
	public SOAPTable(Table table, boolean isRequest, String soapAction, SoapBindingInfo info) {
		this.table = table;
		m_isRequestTable = isRequest;
		m_soapAction = soapAction;
		m_bindingInfo = info;
	}
	
	public SOAPTable() {
		table = new TableImpl();
	}

	public SOAPTable(Table table) {
		this.table = table;
	}

	public Table getTable() {
		return table;
	}
	
	public boolean isRequest() {
		return m_isRequestTable;
	}
	
	public String getSoapAction() {
		return m_soapAction;
	}
	
	public SoapBindingInfo getSoapBindingInfo() {
		return m_bindingInfo;
	}
	
	public String getName() {
		return table.getName();
	}

	public int getMaxOccurs() {
		return table.getMaxOccurs();
	}

	public String getNamespaceDeclaration() {
		return table.getNamespaceDeclaration();
	}

	public Table[] getParentTables() {
		return table.getParentTables();
	}

	public int getRelationToParent() {
		return table.getRelationToParent();
	}

	public String getSchema() {
		return table.getSchema();
	}

	public void setSchema(String schema) {
		table.setSchema(schema);
	}

	public String getInputXPath() {
		return table.getInputXPath();
	}

	public String getOutputXPath() {
		return table.getOutputXPath();
	}

	public void setInputXPath(String xpathIn) {
		table.setInputXPath(xpathIn);
	}

	public void setName(String name) {
		table.setName(name);
	}

	public void setOutputXPath(String xpath) {
		table.setOutputXPath(xpath);
	}

	public Table[] getChildTables() {
		return table.getChildTables();
	}

	public void addColumn(Column column) {
		table.addColumn(column);
	}

	public void addNamespace(Namespace ns) {
		table.addNamespace(ns);
	}

	public String getCatalog() {
		return table.getCatalog();
	}

	public Column[] getColumns() {
		return table.getColumns();
	}

	public void setCatalog(String catalog) {
		table.setCatalog(catalog);
	}

	public void setSchemaModel(SchemaModel schemaModel) {
		table.setSchemaModel(schemaModel);
	}

	public SchemaObject getElement() {
		return table.getElement();
	}

	public void setBase(boolean b) {
		table.setBase(b);
	}

	public void setElement(SchemaObject element) {
		table.setElement(element);
	}
}
