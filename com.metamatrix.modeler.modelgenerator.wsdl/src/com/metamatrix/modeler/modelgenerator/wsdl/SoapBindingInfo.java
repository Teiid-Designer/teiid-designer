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
package com.metamatrix.modeler.modelgenerator.wsdl;

import java.util.Collection;
import java.util.Iterator;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

public class SoapBindingInfo {

    public static final int STYLE_RPC_ENCODED = 0;
    public static final int STYLE_RPC_LITERAL = 1;
    public static final int STYLE_DOCUMENT_ENCODED = 2;
    public static final int STYLE_DOCUMENT_LITERAL = 3;

    private static final String CONNECTOR_NAME = "XML-Relational SOAP Connector"; //$NON-NLS-1$
    private static final String RPC_ENCODED = "RPC - Encoded"; //$NON-NLS-1$
    private static final String RPC_LITERAL = "RPC - Literal"; //$NON-NLS-1$
    private static final String DOCUMENT_ENCODED = "Document - Encoded"; //$NON-NLS-1$
    private static final String DOCUMENT_LITERAL = "Document - Literal"; //$NON-NLS-1$

    private static final String STYLE = "EncodingStyle"; //$NON-NLS-1$
    private static final String DESTINATION = "Uri"; //$NON-NLS-1$

    private String m_destinationURL;
    private String m_operName;
    private int m_style;

    public SoapBindingInfo() {

    }

    public void setDestinationURL( String destinationURL ) {
        m_destinationURL = destinationURL;
    }

    public String getDestinationURL() {
        return m_destinationURL;
    }

    public void setStyle( int style ) {
        m_style = style;
    }

    public int getStyle() {
        return m_style;
    }

    public void setOperationName( String name ) {
        m_operName = name;
    }

    public String getStyleString() {
        String strStyle;
        switch (m_style) {
            case STYLE_RPC_ENCODED:
                strStyle = RPC_ENCODED;
                break;
            case STYLE_RPC_LITERAL:
                strStyle = RPC_LITERAL;
                break;
            case STYLE_DOCUMENT_ENCODED:
                strStyle = DOCUMENT_ENCODED;
                break;
            case STYLE_DOCUMENT_LITERAL:
                strStyle = DOCUMENT_LITERAL;
                break;
            default:
                strStyle = null;
        }
        return strStyle;
    }

    public void createConnectorBinding( String modelName,
                                        String name ) throws Exception {
        final String suffix = ".xmi"; //$NON-NLS-1$
        if (modelName.endsWith(suffix)) {
            modelName = modelName.substring(0, modelName.length() - suffix.length());
        }
        ConfigurationManager impl = DqpPlugin.getInstance().getConfigurationManager();
        Collection types = DqpPlugin.getInstance().getConfigurationManager().getConnectorTypes();
        ComponentType type = null;
        for (Iterator iter = types.iterator(); iter.hasNext();) {
            ComponentType tp = (ComponentType)iter.next();
            if (tp.getName().equals(CONNECTOR_NAME)) {
                type = tp;
                break;
            }
        }
        if (type == null) return;
        ConnectorBinding bind = impl.createConnectorBinding(type, name, false);
        ModelerDqpUtils.setPropertyValue(bind, DESTINATION, getDestinationURL());
        ModelerDqpUtils.setPropertyValue(bind, STYLE, getStyleString());
        impl.addBinding(bind);
    }

    @Override
    public boolean equals( Object other ) {
        if (!(other instanceof SoapBindingInfo)) return false;
        SoapBindingInfo test = (SoapBindingInfo)other;
        return (getDestinationURL().equalsIgnoreCase(test.getDestinationURL()) && getStyle() == test.getStyle());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + (m_destinationURL == null ? 0 : m_destinationURL.hashCode());
        hash = hash * 31 + m_style;
        return hash;
    }

    public String generateUniqueName() {
        String candidate = m_operName;
        int num = 1;
        while (!ModelerDqpUtils.isUniqueBindingName(candidate)) {
            candidate = m_operName + "_" + num++; //$NON-NLS-1$
        }
        return candidate;
    }

}
