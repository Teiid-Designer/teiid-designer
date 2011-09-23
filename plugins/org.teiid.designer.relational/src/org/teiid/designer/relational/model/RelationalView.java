/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;


/**
 * 
 */
public class RelationalView extends RelationalTable {

    public RelationalView() {
        super();
        setType(TYPES.VIEW);
    }
    /**
     * @param name
     */
    public RelationalView( String name ) {
        super(name);
        setType(TYPES.VIEW);
    }

}
