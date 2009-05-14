/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import java.sql.DriverPropertyInfo;


/** 
 * @since 4.2
 */
public class JdbcDriverProperty {
    
    private final String name;
    private final String desc;
    private final String[] allowableValues;
    private final boolean required;
    private final String currentValue;

    /** 
     * @param name
     * @param desc
     * @param allowableValues
     * @param required
     * @param currentValue
     * @since 4.2
     */
    public JdbcDriverProperty(final String name,
                              final String desc,
                              final String[] allowableValues,
                              final boolean required,
                              final String currentValue ) {
        super();
        this.name = name;
        this.desc = desc == null ? "" : desc; //$NON-NLS-1$
        this.allowableValues = allowableValues;
        this.required = required;
        this.currentValue = currentValue;
    }
    
    /** 
     * @param name
     * @param desc
     * @param allowableValues
     * @param required
     * @since 4.2
     */
    public JdbcDriverProperty(final String name,
                              final String desc,
                              final String[] allowableValues,
                              final boolean required) {
        this(name,desc,allowableValues,required,null);
    }
    
    public JdbcDriverProperty( final DriverPropertyInfo info ) {
        super();
        this.name = info.name;
        this.desc = info.description != null ? info.description : ""; //$NON-NLS-1$
        this.allowableValues = info.choices;
        this.required = info.required;
        this.currentValue = info.value;
    }
    
    /** 
     * @return Returns the allowableValues.
     * @since 4.2
     */
    public String[] getAllowableValues() {
        return this.allowableValues;
    }
    /** 
     * @return Returns the description.
     * @since 4.2
     */
    public String getDescription() {
        return this.desc;
    }
    /** 
     * @return Returns the name.
     * @since 4.2
     */
    public String getName() {
        return this.name;
    }
    /** 
     * @return Returns the required.
     * @since 4.2
     */
    public boolean isRequired() {
        return this.required;
    }
    /** 
     * @return Returns the currentValue.
     * @since 4.2
     */
    public String getCurrentValue() {
        return this.currentValue;
    }
}
