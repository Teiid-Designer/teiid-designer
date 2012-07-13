/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl;

import org.jdom.Namespace;
import org.teiid.designer.schema.tools.model.jdbc.Column;
import org.teiid.designer.schema.tools.model.jdbc.Table;
import org.teiid.designer.schema.tools.model.jdbc.internal.TableImpl;
import org.teiid.designer.schema.tools.model.schema.SchemaModel;
import org.teiid.designer.schema.tools.model.schema.SchemaObject;



/**
 * 
 * This class decorates the Table class with additional SOAP properties.
 *
 */
class SOAPTableImpl implements SOAPTable {
	private Table table;
	boolean m_isRequestTable = false;
	String m_soapAction;
	SoapBindingInfo m_bindingInfo;
	
	SOAPTableImpl(Table table, boolean isRequest, String soapAction, SoapBindingInfo info) {
		this.table = table;
		m_isRequestTable = isRequest;
		m_soapAction = soapAction;
		m_bindingInfo = info;
	}
	
	public SOAPTableImpl() {
		table = new TableImpl();
	}

	public SOAPTableImpl(Table table) {
		this.table = table;
	}

	public Table getTable() {
		return table;
	}
	
	@Override
	public boolean isRequest() {
		return m_isRequestTable;
	}
	
	@Override
	public String getSoapAction() {
		return m_soapAction;
	}
	
	@Override
	public SoapBindingInfo getSoapBindingInfo() {
		return m_bindingInfo;
	}
	
	@Override
	public String getName() {
		return table.getName();
	}

	@Override
	public int getMaxOccurs() {
		return table.getMaxOccurs();
	}

	@Override
	public String getNamespaceDeclaration() {
		return table.getNamespaceDeclaration();
	}

	@Override
	public Table[] getParentTables() {
		return table.getParentTables();
	}

	@Override
	public int getRelationToParent() {
		return table.getRelationToParent();
	}

	@Override
	public String getSchema() {
		return table.getSchema();
	}

	@Override
	public void setSchema(String schema) {
		table.setSchema(schema);
	}

	@Override
	public String getInputXPath() {
		return table.getInputXPath();
	}

	@Override
	public String getOutputXPath() {
		return table.getOutputXPath();
	}

	@Override
	public void setInputXPath(String xpathIn) {
		table.setInputXPath(xpathIn);
	}

	@Override
	public void setName(String name) {
		table.setName(name);
	}

	@Override
	public void setOutputXPath(String xpath) {
		table.setOutputXPath(xpath);
	}

	@Override
	public Table[] getChildTables() {
		return table.getChildTables();
	}

	@Override
	public void addColumn(Column column) {
		table.addColumn(column);
	}

	@Override
	public void addNamespace(Namespace ns) {
		table.addNamespace(ns);
	}

	@Override
	public String getCatalog() {
		return table.getCatalog();
	}

	@Override
	public Column[] getColumns() {
		return table.getColumns();
	}

	@Override
	public void setCatalog(String catalog) {
		table.setCatalog(catalog);
	}

	@Override
	public void setSchemaModel(SchemaModel schemaModel) {
		table.setSchemaModel(schemaModel);
	}

	@Override
	public SchemaObject getElement() {
		return table.getElement();
	}

	@Override
	public void setBase(boolean b) {
		table.setBase(b);
	}

	@Override
	public void setElement(SchemaObject element) {
		table.setElement(element);
	}
}
