/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.convertor;

@SuppressWarnings( "javadoc" )
public class ExpProperty {

    private String name;
    private String type;
    private String displayName;
    private boolean advanced;
    private boolean index;
    private boolean masked;
    private boolean required;

    public ExpProperty(String name, String type, String displayName, boolean advanced, boolean index, boolean masked, boolean required) {
        this.name = name;
        this.type = type;
        this.displayName = displayName;
        this.advanced = advanced;
        this.index = index;
        this.masked = masked;
        this.required = required;
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

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return this.displayName;
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
