/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
