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

package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.xsd.XSDSchema;
import org.jdom.Namespace;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;

public class ModelImpl implements Model {

    private Service[] m_services;
    private Map m_namespaces;
    private Map m_reverseNamespaceLookup;
    private XSDSchema[] m_schemas;

    public Service[] getServices() {
        // defensive copy of Service array
        Service[] retSvc = new Service[m_services.length];
        for (int i = 0; i < m_services.length; i++) {
            retSvc[i] = (Service)m_services[i].copy();
        }
        return retSvc;
    }

    public void setServices( Service[] services ) {
        m_services = services;
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < m_services.length; i++) {
            buff.append("\n"); //$NON-NLS-1$
            buff.append(m_services[i].toString());
        }
        buff.append("\n"); //$NON-NLS-1$
        return buff.toString();
    }

    public XSDSchema[] getSchemas() {
        return m_schemas;
    }

    public void setSchemas( XSDSchema[] schemas ) {
        m_schemas = schemas;
    }

    public Map getNamespaces() {
        return m_namespaces;
    }

    public void setNamespaces( Map collection ) {
        m_namespaces = collection;
        m_reverseNamespaceLookup = new HashMap(collection.size());
        for (Iterator nsIter = collection.keySet().iterator(); nsIter.hasNext();) {
            Object key = nsIter.next();
            Object payload = collection.get(key);
            m_reverseNamespaceLookup.put(payload, key);
        }
    }

    public void addNamespaceToMap( Namespace ns ) {
        String uri = ns.getURI();
        String prefix = ns.getPrefix();
        addNamespaceToMap(prefix, uri);
    }

    public void addNamespaceToMap( String prefix,
                                   String namespaceURI ) {
        if (m_reverseNamespaceLookup.get(namespaceURI) != null) return;
        if (m_namespaces.get(prefix) != null) {
            int pre = 0;
            final String nsPre = "mmn"; //$NON-NLS-1$
            while (m_namespaces.get(nsPre + pre) != null)
                ++pre;
            prefix = nsPre + pre;
        }
        m_namespaces.put(prefix, namespaceURI);
        m_reverseNamespaceLookup.put(namespaceURI, prefix);
    }

    public void addNamespaceToMap( String namespaceURI ) {
        addNamespaceToMap("mmn0", namespaceURI); //$NON-NLS-1$
    }

}
