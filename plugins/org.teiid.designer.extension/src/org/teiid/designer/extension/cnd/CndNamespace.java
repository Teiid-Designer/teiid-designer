/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.cnd;


/**
 * This class provides a non-Modeshape equivalent of a Namespace object. Provides simple prefix & namespace values and
 * a toString() override that combines the two separated by a ':'
 */
public class CndNamespace {
	private final String prefix;
    private final String namespaceUri;

    /**
     * Create a name-space instance.
     * 
     * @param prefix the name-space prefix; may not be null (this is not checked)
     * @param namespaceUri the name-space URI; may not be null (this is not checked)
     */
    public CndNamespace( String prefix,
                           String namespaceUri ) {
        assert prefix != null;
        assert namespaceUri != null;
        this.prefix = prefix;
        this.namespaceUri = namespaceUri;
    }

    /**
     * Simple getter for the value of the Namespace URI
     * 
     * @return the namespace URI
     */
    public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * Simple getter for the value of the Namespace prefix
     * 
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return namespaceUri.hashCode();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( CndNamespace that ) {
        if (that == null) return 1;
        if (this == that) return 0;
        return this.getNamespaceUri().compareTo(that.getNamespaceUri());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == this) return true;
        if (obj instanceof CndNamespace) {
        	CndNamespace that = (CndNamespace)obj;
            if (!this.namespaceUri.equals(that.getNamespaceUri())) return false;
            // if (!this.prefix.equals(that.getPrefix())) return false;
            return true;
        }
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return prefix + '=' + namespaceUri;
    }
}