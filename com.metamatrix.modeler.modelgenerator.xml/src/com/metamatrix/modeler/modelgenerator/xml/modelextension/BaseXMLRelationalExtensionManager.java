/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.modelextension;

import com.metamatrix.metamodels.relational.RelationalEntity;

/**
 * 
 * Adds namespace metadata extensions to the Table class.
 *
 */
public interface BaseXMLRelationalExtensionManager extends ExtensionManager {

	public void setNamespacePrefixesAttribute(RelationalEntity table, String prefixes);

}
