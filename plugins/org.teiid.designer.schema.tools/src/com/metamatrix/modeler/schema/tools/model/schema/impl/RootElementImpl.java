/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObjectKey;

public class RootElementImpl implements RootElement {

    private SchemaObjectKey key;

    private String name;

    private String targetNamespace;

    private boolean useAsRoot;

    public RootElementImpl( SchemaObjectKey key,
                            String name,
                            String targetNamespace,
                            boolean root ) {
        this.key = key;
        this.name = name;
        this.targetNamespace = targetNamespace;
        this.useAsRoot = root;
    }

    @Override
    public String toString() {
        String retval = name;
        if (targetNamespace != null) {
            retval += " (" + targetNamespace + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return retval;
    }

    public boolean isUseAsRoot() {
        return useAsRoot;
    }

    public SchemaObjectKey getKey() {
        return key;
    }

    public String getName() {
        if (null != name) return name;
        return ""; //$NON-NLS-1$
    }

    public String getNamespace() {
        if (null != targetNamespace) return targetNamespace;
        return ""; //$NON-NLS-1$
    }
}
