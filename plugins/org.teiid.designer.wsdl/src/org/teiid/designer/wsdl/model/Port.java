/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.wsdl.model;


/**
 * This class represents a port as defined in a WSDL
 */
public class Port extends WSDLElement {

    private Service m_parent;
    private Binding m_binding;
    private String m_locationURI;
    private String namespaceURI;

    public Port( Service parent ) {
        m_parent = parent;
    }

    public Binding getBinding() {
        // defensive copy of bindings
        return (Binding)m_binding.copy();
    }

    public void setBinding( Binding binding ) {
        m_binding = binding;
    }

    public Service getService() {
        return m_parent;
    }

    public void setLocationURI( String uri ) {
        m_locationURI = uri;
    }

    public String getLocationURI() {
        return m_locationURI;
    }

    public WSDLElement copy() {
    	Port impl = new Port(getService());
        impl.setName(getName());
        impl.setId(getId());
        impl.setBinding(getBinding());
        impl.setNamespaceURI(getNamespaceURI());
        return impl;
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("<port name='"); //$NON-NLS-1$
        buff.append(getName());
        buff.append("' id='"); //$NON-NLS-1$
        buff.append(getId());
        buff.append("'>"); //$NON-NLS-1$
        buff.append(m_binding.toString());
        buff.append("</port>"); //$NON-NLS-1$
        return buff.toString();
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI( String namespaceURI ) {
        this.namespaceURI = namespaceURI;
    }

}
