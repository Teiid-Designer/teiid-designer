/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import org.teiid.designer.relational.RelationalConstants;

/**
 * 
 */
public class RelationalReference implements RelationalConstants {
    public static final String KEY_NAME = "NAME"; //$NON-NLS-1$
    public static final String KEY_NAME_IN_SOURCE = "NAMEINSOURCE"; //$NON-NLS-1$
    public static final String KEY_DESCRIPTION = "DESCRIPTION"; //$NON-NLS-1$
    
    private int type = TYPES.UNDEFINED;
    private RelationalReference parent;
    private String  name;
    private String  nameInSource;
    private String  description;
    
    
    public RelationalReference() {
        super();
    }
    /**
     * @param name
     */
    public RelationalReference( String name ) {
        super();
        this.name = name;
    }

    /**
     * @return parent
     */
    public RelationalReference getParent() {
        return parent;
    }

    /**
     * @param parent Sets parent to the specified value.
     */
    public void setParent( RelationalReference parent ) {
        this.parent = parent;
    }
    /**
     * @return name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name Sets name to the specified value.
     */
    public void setName( String name ) {
        this.name = name;
    }
    /**
     * @return nameInSource
     */
    public String getNameInSource() {
        return nameInSource;
    }
    /**
     * @param nameInSource Sets nameInSource to the specified value.
     */
    public void setNameInSource( String nameInSource ) {
        this.nameInSource = nameInSource;
    }
    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param name Sets name to the specified value.
     */
    public void setDescription( String description ) {
        this.description = description;
    }
    
    /**
     * @return type
     */
    public int getType() {
        return type;
    }
    /**
     * @param name Sets name to the specified value.
     */
    protected void setType( int type ) {
        this.type = type;
    }
}
