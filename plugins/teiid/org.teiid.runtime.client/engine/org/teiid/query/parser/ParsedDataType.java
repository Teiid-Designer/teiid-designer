/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.parser;

public class ParsedDataType {

    private String type;

    private Integer length;

    private Integer scale;

    private Integer precision;

    public ParsedDataType(String type) {
        this.type = type;
    }

    public ParsedDataType(String type, int length, boolean precision) {
        this.type = type;

        if (precision) {
            this.precision = length;
        } else {
            this.length = length;
        }
    }

    public ParsedDataType(String type, int length, int scale, boolean precision) {
        this.type = type;
        this.scale = scale;
        if (precision) {
            this.precision = length;
        } else {
            this.length = length;
        }
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the length
     */
    public Integer getLength() {
        return this.length;
    }

    /**
     * @return the scale
     */
    public Integer getScale() {
        return this.scale;
    }

    /**
     * @return the precision
     */
    public Integer getPrecision() {
        return this.precision;
    }
}
