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

    private String shortName;

    /** 
     * upper case of name
     */
    @Removed("8.0.0")
    private String canonicalShortName;

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
     * @return Short name of the symbol (un-dotted)
     */
    public final String getShortName() { 
        return shortName;
    }

    /**
     * Change the symbol's name.  This will change the symbol's hash code
     * and canonical name!!!!!!!!!!!!!!!!!  If this symbol is in a hashed
     * collection, it will be lost!
     * @param name New name
     */
    public void setShortName(String name) {
        if(name == null) {
            throw new IllegalArgumentException(); //$NON-NLS-1$
        }
        this.shortName = name;
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
        return this.canonicalShortName;
    }

    /**
     * @param canonicalShortName the canonicalShortName to set
     */
    @Removed("8.0.0")
    public void setCanonicalShortName(String canonicalShortName) {
        this.canonicalShortName = canonicalShortName;
    }
}
