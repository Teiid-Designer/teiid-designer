/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.convertor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.teiid.designer.extension.convertor.mxd.DisplayType;
import org.teiid.designer.extension.convertor.mxd.MetaclassType;
import org.teiid.designer.extension.convertor.mxd.ObjectFactory;
import org.teiid.designer.extension.convertor.mxd.PropertyType;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;

/**
 *
 */
public class TeiidPropertyDefinitionConvertor implements MxdConstants {

    private final ObjectFactory factory = new ObjectFactory();

    private Map<String, MetaclassType> metaclassTypeMap;

    private MetaclassType getMetaclassType(String name) {
        if (name == null)
            throw new IllegalStateException("The MetaclassType name cannot be null"); //$NON-NLS-1$

        MetaclassType metaclassType = metaclassTypeMap.get(name);
        if (metaclassType == null) {
            /*
             * <p:extendedMetaclass name="org.teiid.designer.metamodels.relational.impl.ColumnImpl">
             */
            metaclassType = factory.createMetaclassType();
            if (TargetObjectMappings.TABLE.getTeiidClass().equals(name))
                metaclassType.setName(TargetObjectMappings.TABLE.getDesignerClass());
            else if (TargetObjectMappings.PROCEDURE.getTeiidClass().equals(name))
                metaclassType.setName(TargetObjectMappings.PROCEDURE.getDesignerClass());
            else if (TargetObjectMappings.COLUMN.getTeiidClass().equals(name))
                metaclassType.setName(TargetObjectMappings.COLUMN.getDesignerClass());
            else if (TargetObjectMappings.PROCEDUREPARAMETER.getTeiidClass().equalsIgnoreCase(name))
            	metaclassType.setName(TargetObjectMappings.PROCEDUREPARAMETER.getDesignerClass());
            else if( name.equalsIgnoreCase("org.teiid.metadata.FunctionMethod") ) {
            	return null;
            } else {
                throw new IllegalStateException("Unsupported MetaclassType " + name); //$NON-NLS-1$
            }
            metaclassTypeMap.put(name, metaclassType);
        }

        return metaclassType;
    }

    /**
     * @param extensions
     * @return resulting collection of {@link MetaclassType}s
     */
    public Collection<MetaclassType> getMetaclasses(Collection<TeiidPropertyDefinition> extensions) {
        metaclassTypeMap = new HashMap<String, MetaclassType>();

        for(TeiidPropertyDefinition defn : extensions) {
            String owner = defn.getOwner();
            if (owner == null)
                throw new IllegalStateException("A translator property definition " + defn.getName() + " does not contain an owner property"); //$NON-NLS-1$ //$NON-NLS-2$

            String[] appClasses = owner.split(COMMA);

            for (String appClass : appClasses) {
                MetaclassType metaclassType = getMetaclassType(appClass);
                // type may be NULL if FunctionMethod, so need to just continue
                if( metaclassType == null ) {
                	continue;
                }
                
                PropertyType propertyType = factory.createPropertyType();
                metaclassType.getProperty().add(propertyType);

                /*
                *<p:property advanced="false" index="true" masked="false" name="JoinColumn" required="false" type="string">
                *  <p:display locale="en_US">Join Column</p:display>
                *</p:property>
                */
                // Teiid tends to have an url prefixed to the literal
                String name = defn.getName();
                {
                    String[] segments = name.split("}"); //$NON-NLS-1$
                    name = segments[segments.length - 1];
                }

                propertyType.setName(name);
                propertyType.setAdvanced(defn.isAdvanced());
                propertyType.setRequired(defn.isRequired());
                propertyType.setMasked(defn.isMasked());

                if (defn.isConstrainedToAllowedValues()) {
                    for (String allowedValue : defn.getAllowedValues()) {
                        propertyType.getAllowedValue().add(allowedValue);
                    }
                }

                String description = defn.getDescription();
                if (description != null && description.length() > 0) {
                    DisplayType descriptionType = factory.createDisplayType();
                    descriptionType.setValue(description);
                    propertyType.getDescription().add(descriptionType);
                }

                String displayName = defn.getDisplayName();
                if (displayName != null && displayName.length() > 0) {
                    displayName = displayName.replaceAll("\"", EMPTY_STRING); //$NON-NLS-1$
                    DisplayType displayType = factory.createDisplayType();
                    displayType.setValue(displayName);
                    propertyType.getDisplay().add(displayType);
                }

                String dataType = defn.getPropertyTypeClassName();
                if (dataType != null && dataType.length() > 0) {
                    // Remove package prefix
                    String[] segments = dataType.toLowerCase().split("\\."); //$NON-NLS-1$
                    dataType = segments[segments.length - 1];
                    ValidDataTypes.validateDataType(dataType);
                    propertyType.setType(dataType);
                }

                Object defaultValue = defn.getDefaultValue();
                if (defaultValue != null)
                    propertyType.setDefaultValue(defaultValue.toString());
            }
        }

        return metaclassTypeMap.values();
    }

}
