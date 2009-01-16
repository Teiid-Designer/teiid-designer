/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

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
