/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.symbol;

import org.teiid.designer.annotation.Removed;
import org.teiid.designer.query.sql.symbol.ISymbol;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.lang.SimpleNode;
import org.teiid.runtime.client.Messages;


/**
 *
 */
public class Symbol extends SimpleNode implements ISymbol<LanguageVisitor> {

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
    @Removed(Version.TEIID_8_0)
    private String canonicalShortName;

    /**
     * @param ex
     * @return short name of given expression if a symbol
     */
    public static String getShortName(Expression ex) {
        if (ex instanceof Symbol) {
            return ((Symbol)ex).getShortName();
        }
        return "expr"; //$NON-NLS-1$
    }

    /**
     * @param name
     * @return shortname of string using {@link Symbol#SEPARATOR} as delimiter
     */
    public static String getShortName(String name) {
        int index = name.lastIndexOf(Symbol.SEPARATOR);
        if(index >= 0) {
            return name.substring(index+1);
        }
        return name;
    }

    /**
     * @param p
     * @param i
     */
    public Symbol(TeiidParser p, int i) {
        super(p, i);
    }

    /**
     * @param name
     */
    public void setName(String name) {
        setShortName(name);
    }

    /**
     * Get the short name of the element
     *
     * @return Short name of the symbol (un-dotted)
     */
    @Override
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
    @Override
    public void setShortName(String name) {
        if(name == null) {
            throw new IllegalArgumentException(Messages.getString(Messages.ERR.ERR_015_010_0017));
        }
        this.shortName = name;
        this.outputName = null;
    }

    /**
     * Get the name of the symbol
     * @return Name of the symbol, never null
     */
    @Override
    public String getName() {
        return getShortName();
    }

    /**
     * @return the canonicalShortName
     */
    @Removed(Version.TEIID_8_0)
    public String getShortCanonicalName() {
        if (canonicalShortName == null && shortName != null) {
            canonicalShortName = shortName.toUpperCase();
        }
        return this.canonicalShortName;
    }

    /**
     * @param canonicalShortName the canonicalShortName to set
     */
    @Removed(Version.TEIID_8_0)
    public void setShortCanonicalName(String canonicalShortName) {
        this.canonicalShortName = canonicalShortName;
    }

    @Override
    public String getOutputName() {
        return this.outputName == null ? getName() : this.outputName;
    }

    @Override
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        if (getTeiidVersion().isLessThan(Version.TEIID_8_0))
            result = prime * result + ((this.getShortCanonicalName() == null) ? 0 : this.getShortCanonicalName().hashCode());
        else
            result = prime * result + ((this.getShortName() == null) ? 0 : this.getShortName().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Symbol other = (Symbol)obj;
        if (this.getShortCanonicalName() == null) {
            if (other.getShortCanonicalName() != null) return false;
        } else if (!this.getShortCanonicalName().equalsIgnoreCase(other.getShortCanonicalName())) return false;
        if (this.getName() == null) {
            if (other.getName() != null) return false;
        } else if (!this.getName().equalsIgnoreCase(other.getName())) return false;
        return true;
    }

    /** Accept the visitor. **/
    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Symbol clone() {
        Symbol clone = new Symbol(this.parser, this.id);

        if(getShortCanonicalName() != null)
            clone.setShortCanonicalName(getShortCanonicalName());
        if(outputName != null)
            clone.outputName = outputName;
        if(getShortName() != null)
            clone.setShortName(getShortName());
        if(getName() != null)
            clone.setName(getName());

        return clone;
    }

}
