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
