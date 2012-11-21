/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.udf;

/**
 *
 */
public class FunctionParameterDescriptor {

    private String name;
    private String type;

    /**
     * @param name
     * @param type
     */
    public FunctionParameterDescriptor(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }
}
