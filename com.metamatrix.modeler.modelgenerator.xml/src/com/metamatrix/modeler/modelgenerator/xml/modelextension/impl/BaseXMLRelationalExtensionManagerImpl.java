/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.modelextension.impl;

import org.eclipse.emf.ecore.EcorePackage;

import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.ObjectExtension;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.BaseXMLRelationalExtensionManager;

public abstract class BaseXMLRelationalExtensionManagerImpl extends
		ExtensionManagerImpl implements BaseXMLRelationalExtensionManager {

	static final String TABLE_NAMESPACE_PREFIXES = "NamespacePrefixes"; //$NON-NLS-1$

	private XAttribute namespacePrefixesTableAttribute;

	@Override
    public void createTableExtensions(ExtensionFactory factory, XClass table) {
		namespacePrefixesTableAttribute = factory.createXAttribute();
		namespacePrefixesTableAttribute.setName(TABLE_NAMESPACE_PREFIXES);
		namespacePrefixesTableAttribute.setEType(EcorePackage.eINSTANCE
				.getEString());
		table.getEStructuralFeatures().add(namespacePrefixesTableAttribute);
	}

	@Override
    public void assignAttribute(XAttribute attribute) {
		if (attribute.getName().equals(TABLE_NAMESPACE_PREFIXES)) {
			namespacePrefixesTableAttribute = attribute;
		}
	}

	public void setNamespacePrefixesAttribute(RelationalEntity table,
			String prefixes) {
		ObjectExtension extension = new ObjectExtension(table, theTableXClass,
				ModelerCore.getModelEditor());
		extension.eDynamicSet(namespacePrefixesTableAttribute, prefixes);
	}

}
