/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.views;

/**
 * Execution Plan Attribute Object
 */
public class PlanAttribute {
    private String name;
    private String value;

    public PlanAttribute( String name ) {
        super();
        this.name = name;
    }

    public PlanAttribute( String name,
                         String value ) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
