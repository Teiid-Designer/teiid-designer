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
