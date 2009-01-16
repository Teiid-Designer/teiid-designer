/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.schema.tools.model.jdbc;


import org.jdom.Namespace;

import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public interface Table extends DatabaseElement {

	/**
	 * Returns a String of all the namespace declarations in prefix:uri form as
	 * a single String with spaces between each declaration
	 * 
	 * @return the String of all the Namespace declarations
	 */
	public String getNamespaceDeclaration();

	/**
	 * 
	 * Add a namespace to the list of namespaces to declare
	 * 
	 * @param ns - the namespace to add
	 */
	public void addNamespace(Namespace ns);

	public String getCatalog();

	public void setCatalog(String catalog);
	
	
	public String getSchema();
	
	public void setSchema(String schema);

	/**
	 * 
	 * Add a column to the table
	 * 
	 * @param column
	 *            the column to add
	 */
	public void addColumn(Column column);

	/**
	 * 
	 * Returns all of the columns contained in the table
	 * 
	 * @return an array of Columns
	 */
	public Column[] getColumns();
	
	/**
	 * 
	 * Returns all of the tables that have a foreign key relationship
	 * to this table.
	 * 
	 * @return an array of Tables
	 */
	public Table[] getChildTables();
	
	/**
	 * 
	 * Returns all of the tables that this table has a foreign key relationship to.
	 * 
	 * @return an array of Tables
	 */
	public Table[] getParentTables();

	/**
	 * Returns the Relationship value that defines the tables relation to its parent.
	 * @return - the Relationship value.
	 */
	public int getRelationToParent();

	public int getMaxOccurs();

	public void setSchemaModel(SchemaModel schemaModel);

	public void setBase(boolean b);

	public SchemaObject getElement();

	public void setElement(SchemaObject element);
}
