/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;


/**
 *
 */
public class NamespaceItem {
    private String uri;
    private String prefix;

    /**
     * @param uri
     * @param prefix
     */
    public NamespaceItem(String uri, String prefix) {
        this.uri = uri;
        this.prefix = prefix;
    }

    /**
     * @param defaultNamepace
     */
    public NamespaceItem(String defaultNamepace) {
        this.uri = defaultNamepace;
    }

    /**
     * 
     */
    public NamespaceItem() {
    }

    /**
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return prefix
     */
    public String getPrefix() {
        return prefix;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.prefix == null) ? 0 : this.prefix.hashCode());
        result = prime * result + ((this.uri == null) ? 0 : this.uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NamespaceItem other = (NamespaceItem)obj;
        if (this.prefix == null) {
            if (other.prefix != null) return false;
        } else if (!this.prefix.equals(other.prefix)) return false;
        if (this.uri == null) {
            if (other.uri != null) return false;
        } else if (!this.uri.equals(other.uri)) return false;
        return true;
    }

    @Override
    public NamespaceItem clone() {
        NamespaceItem clone = new NamespaceItem(this.uri, this.prefix);
        return clone;
    }

}
