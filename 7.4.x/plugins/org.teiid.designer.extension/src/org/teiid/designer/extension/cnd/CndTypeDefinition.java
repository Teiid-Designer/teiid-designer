/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.cnd;

import java.util.ArrayList;
import java.util.Collection;

import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.extension.properties.PrefixedName;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * Structural object to hold state of a CND Node Type Definition. Information includes both the prefixed type name
 * and extended (super type) name as well as an array or {@link PropertyDefinition}s
 */
public class CndTypeDefinition {
	
	/**
	 * prefixedTypeName refers to CND node type value. Example: "<strong>salesforce:tableCapabilities</strong>"
	 */
	PrefixedName prefixedTypeName;
	
	/**
	 * prefixedSuperTypeName refers to CND node type extended name value. Example: "<strong>relational:baseTable</strong>"
	 */
	PrefixedName prefixedSuperTypeName;
	
	PropertyDefinition[] propertyDefinitions;
	
	public CndTypeDefinition() {
		super();
		this.propertyDefinitions = new PropertyDefinition[0];
	}
	
	public CndTypeDefinition(String typeId, String prefixedSuperTypeName, PropertyDefinition[] propertyDefinitions) {
		super();
		
		this.prefixedTypeName = new PrefixedName(typeId);
		this.prefixedSuperTypeName = new PrefixedName(prefixedSuperTypeName);
		this.propertyDefinitions = propertyDefinitions;
	}
	
	/**
	 * @param prefixedTypeName the prefixed node type name. Cannot be null
	 */
	public void setType(PrefixedName prefixedTypeName) {
		CoreArgCheck.isNotNull(prefixedTypeName, "prefixedTypeName"); //$NON-NLS-1$
		
		this.prefixedTypeName = prefixedTypeName;
	}
	
	/**
	 * @param prefixedSuperTypeName the prefixed super type name. Cannot be null
	 */
	public void setSuperType(PrefixedName prefixedSuperTypeName) {
		CoreArgCheck.isNotNull(prefixedSuperTypeName, "prefixedSuperTypeName"); //$NON-NLS-1$
		
		this.prefixedSuperTypeName = prefixedSuperTypeName;
	}

	/**
	 * @return prefixedTypeName the type name
	 */
	public String getPrefixedType() {
		return this.prefixedTypeName.toString();
	}
	
	/**
	 * @return prefixedSuperTypeName the super type name prefix
	 */
	public String getPrefixedSuperType() {
		return this.prefixedSuperTypeName.toString();
	}

	/**
	 * @return prefixedTypeName the type name prefix
	 */
	public String getExtensionNamespacePrefix() {
		return this.prefixedTypeName.getPrefix();
	}

	/**
	 * @return prefixedSuperTypeName the type name prefix
	 */
	public String getExtendedMetamodelPrefix() {
		return this.prefixedSuperTypeName.getPrefix();
	}

	public String getExtendedMetamodelObjectTypeName() {
		return this.prefixedSuperTypeName.getName();
	}

	public PropertyDefinition[] getPropertyDefinitions() {
		return this.propertyDefinitions;
	}
	
	/**
	 * Method to add a property definition.
	 * 
	 * @param propDef the {@link PropertyDefinition} to add
	 */
	public void addPropertyDefinition(CndPropertyDefinition propDef) {
		CoreArgCheck.isNotNull(propDef, "propDef"); //$NON-NLS-1$
		
		Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
		for( PropertyDefinition prop : this.propertyDefinitions ) {
			props.add(prop);
		}
		props.add(propDef);
		this.propertyDefinitions = props.toArray(new PropertyDefinition[props.size()]);
	}
	
	/**
	 * Method to remove a property definition.
	 * 
	 * @param propDef the {@link PropertyDefinition} to remove
	 */
	public void removeTypeDefinition(CndPropertyDefinition propDef) {
		CoreArgCheck.isNotNull(propDef, "propDef"); //$NON-NLS-1$
		
		Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
		for( PropertyDefinition prop : this.propertyDefinitions ) {
			if( ! prop.getId().equals(propDef.getCndKey())) {
				props.add(prop);
			}
		}
		this.propertyDefinitions = props.toArray(new PropertyDefinition[props.size()]);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getSimpleName());
		sb.append("\n  id\t\t\t\t\t = ").append(prefixedTypeName.toString()) //$NON-NLS-1$
		.append("\n  extended class name\t = ").append(prefixedSuperTypeName.toString()); //$NON-NLS-1$
		
		return sb.toString();
	}
}
