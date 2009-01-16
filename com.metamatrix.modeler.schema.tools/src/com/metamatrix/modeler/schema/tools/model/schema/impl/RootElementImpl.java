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
