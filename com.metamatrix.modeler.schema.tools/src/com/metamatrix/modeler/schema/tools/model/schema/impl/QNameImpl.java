/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import com.metamatrix.modeler.schema.tools.model.schema.QName;

public class QNameImpl implements QName {
    public String namespace;

    public String lname;

    public QNameImpl( String namespace,
                      String lname ) {
        this.namespace = namespace;
        this.lname = lname;

        int namespaceHash = namespace == null ? 0 : namespace.hashCode();
        int lnameHash = lname == null ? 0 : lname.hashCode();

        hashCode = 17;
        hashCode = 37 * hashCode + namespaceHash;
        hashCode = 37 * hashCode + lnameHash;
    }

    /**
     * @see com.metamatrix.modeler.schema.tools.model.QName#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object o ) {
        if (!(o instanceof QName)) {
            return false;
        }

        QNameImpl other = (QNameImpl)o;
        if (namespace == null && other.namespace != null) {
            return false;
        }
        if (namespace != null && other.namespace == null) {
            return false;
        }
        if (namespace != null && !(namespace.equals(other.namespace))) {
            return false;
        }

        if (lname == null && other.lname != null) {
            return false;
        }
        if (lname != null && other.lname == null) {
            return false;
        }
        if (lname != null && !(lname.equals(other.lname))) {
            return false;
        }

        return true;
    }

    int hashCode;

    /**
     * @see com.metamatrix.modeler.schema.tools.model.QName#hashCode()
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * @see com.metamatrix.modeler.schema.tools.model.QName#toString()
     */
    @Override
    public String toString() {
        if (namespace == null) return lname + "(global)"; //$NON-NLS-1$
        return namespace + "#" + lname; //$NON-NLS-1$
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace( String namespace ) {
        this.namespace = namespace;
    }

    public String getLName() {
        return lname;
    }
}
