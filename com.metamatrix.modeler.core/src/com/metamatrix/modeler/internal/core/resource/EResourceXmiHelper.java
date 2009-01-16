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

package com.metamatrix.modeler.internal.core.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;

/**
 * MtkXmiHelper
 */
public class EResourceXmiHelper extends XMIHelperImpl {
    
    private Map prefixesToNamespaceMap;

    /**
     * Construct an instance of MtkXmiHelper.
     * 
     */
    public EResourceXmiHelper() {
        super();
        this.prefixesToNamespaceMap = new HashMap();
    }

    /**
     * Construct an instance of MtkXmiHelper.
     * @param resource
     */
    public EResourceXmiHelper(XMLResource resource) {
        super(resource);
        this.prefixesToNamespaceMap = new HashMap();
    }
    
    public Map getPrefixesToURIs() {
        this.prefixesToNamespaceMap.clear();
        for (final Iterator iter = getPrefixToNamespaceMap().keySet().iterator(); iter.hasNext();) {
            final String prefix = (String)iter.next();
            final String uri    = prefixesToURIs.get(prefix);
            this.prefixesToNamespaceMap.put(prefix, uri);
        }
        return prefixesToNamespaceMap;
    }

}
