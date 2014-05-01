/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.convertor;

import org.teiid.designer.extension.convertor.MxdConstants.TargetObjectMappings;

@SuppressWarnings( "javadoc" )
public class ExpectedProperty {

    private String name;
    private TargetObjectMappings target;
    private Class<?> type;
    private boolean advanced;
    private boolean index;
    private boolean masked;
    private boolean required;
    private String displayName;
    private String description;

    public ExpectedProperty(String name, TargetObjectMappings target, Class<?> type,
                            boolean advanced, boolean index, boolean masked, boolean required,
                            String displayName, String description) {
        this.name = name;
        this.target = target;
        this.type = type;
        this.advanced = advanced;
        this.index = index;
        this.masked = masked;
        this.required = required;
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the mapping
     */
    public TargetObjectMappings getTarget() {
        return this.target;
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return this.type;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the advanced
     */
    public boolean isAdvanced() {
        return this.advanced;
    }

    /**
     * @return the index
     */
    public boolean isIndex() {
        return this.index;
    }

    /**
     * @return the masked
     */
    public boolean isMasked() {
        return this.masked;
    }

    /**
     * @return the required
     */
    public boolean isRequired() {
        return this.required;
    }
}
