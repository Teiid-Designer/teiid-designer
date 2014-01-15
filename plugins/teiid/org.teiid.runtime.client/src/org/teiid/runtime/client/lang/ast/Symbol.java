/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.ast;

import org.teiid.designer.annotation.Removed;
import org.teiid.runtime.client.lang.parser.TeiidParser;


/**
 *
 */
public class Symbol extends SimpleNode {

    /**
     * Character used to delimit name components in a symbol
     */
    public static final String SEPARATOR = "."; //$NON-NLS-1$

    private String shortName;

    /**
     * Prior to resolving null, after resolving it is the exact string
     * entered in the query.
     *
     * The AliasGenerator can also set this value as necessary for the data tier.
     */
    protected String outputName;

    /** 
     * upper case of name
     */
    @Removed("8.0.0")
    private String canonicalShortName;

    public static String getShortName(Expression ex) {
        if (ex instanceof Symbol) {
            return ((Symbol)ex).getShortName();
        }
        return "expr"; //$NON-NLS-1$
    }

    public static String getShortName(String name) {
        int index = name.lastIndexOf(Symbol.SEPARATOR);
        if(index >= 0) {
            return name.substring(index+1);
        }
        return name;
    }

    /**
     * @param id
     */
    public Symbol(int id) {
        super(id);
    }

    /**
     * @param p
     * @param i
     */
    public Symbol(TeiidParser p, int i) {
        super(p, i);
    }

    public void setName(String name) {
        setShortName(name);
    }

    /**
     * Get the short name of the element
     *
     * @return Short name of the symbol (un-dotted)
     */
    public final String getShortName() { 
        return shortName;
    }

    /**
     * Change the symbol's name.  This will change the symbol's hash code
     * and canonical name!!!!!!!!!!!!!!!!!  If this symbol is in a hashed
     * collection, it will be lost!
     *
     * @param name New name
     */
    public void setShortName(String name) {
        if(name == null) {
            throw new IllegalArgumentException(); //$NON-NLS-1$
        }
        this.shortName = name;
        this.outputName = null;
    }

    /**
     * Get the name of the symbol
     * @return Name of the symbol, never null
     */
    public String getName() {
        return getShortName();
    }

    /**
     * @return the canonicalShortName
     */
    @Removed("8.0.0")
    public String getCanonicalShortName() {
        if (canonicalShortName == null) {
            canonicalShortName = shortName.toUpperCase();
        }
        return this.canonicalShortName;
    }

    /**
     * @param canonicalShortName the canonicalShortName to set
     */
    @Removed("8.0.0")
    public void setCanonicalShortName(String canonicalShortName) {
        this.canonicalShortName = canonicalShortName;
    }

    public String getOutputName() {
        return this.outputName == null ? getName() : this.outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.canonicalShortName == null) ? 0 : this.canonicalShortName.hashCode());
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Symbol other = (Symbol)obj;
        if (this.canonicalShortName == null) {
            if (other.canonicalShortName != null) return false;
        } else if (!this.canonicalShortName.equals(other.canonicalShortName)) return false;
        if (this.getName() == null) {
            if (other.getName() != null) return false;
        } else if (!this.getName().equalsIgnoreCase(other.getName())) return false;
        return true;
    }

    /** Accept the visitor. **/
    public void jjtAccept(Teiid8ParserVisitor visitor, Object data) {
        visitor.visit(this, data);
    }
}
