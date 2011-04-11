/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.teiid.designer.extension.cnd.CndNamespace;
import org.teiid.designer.extension.cnd.CndTypeDefinition;

public class ModelExtensionDefinition {
	
	CndNamespace extensionNamespace;
	CndNamespace metamodelNamespace;

	Map<String, CndTypeDefinition> typeDefinitions;

	public ModelExtensionDefinition() {
		super();
		this.typeDefinitions = new HashMap<String, CndTypeDefinition>();
	}

	public CndNamespace getExtensionNamespace() {
		return extensionNamespace;
	}

	public void setExtensionNamespace(CndNamespace extensionNamespace) {
		this.extensionNamespace = extensionNamespace;
	}

	public CndNamespace getMetamodelNamespace() {
		return metamodelNamespace;
	}

	public void setMetamodelNamespace(CndNamespace metamodelNamespace) {
		this.metamodelNamespace = metamodelNamespace;
	}

	public Collection<CndTypeDefinition> getTypeDefinitions() {
		return this.typeDefinitions.values();
	}

	public void addTypeDefinition(CndTypeDefinition typeDefinition) {
		this.typeDefinitions.put(typeDefinition.getPrefixedType(), typeDefinition);
	}
	
	public void removeTypeDefinition(CndTypeDefinition typeDefinition) {
		this.typeDefinitions.remove(typeDefinition.getPrefixedType());
	}
}
