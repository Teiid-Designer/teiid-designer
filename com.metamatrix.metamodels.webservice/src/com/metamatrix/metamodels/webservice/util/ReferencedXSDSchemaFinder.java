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

package com.metamatrix.metamodels.webservice.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlRoot;

/**
 * @since 4.2
 */
public class ReferencedXSDSchemaFinder extends WebServiceComponentFinder {

    private final Set xsds;
    private final Set targetNamespaces;

    /**
     * @since 4.2
     */
    public ReferencedXSDSchemaFinder() {
        super();
        this.xsds = new HashSet();
        this.targetNamespaces = new HashSet();
    }

    /**
     * This method accumulates the {@link Interface} instances. The implementation takes as many shortcuts as possible to prevent
     * unnecessarily visiting unrelated objects.
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        // Interface are contained by Resources
        if (object instanceof Message) {
            final Message msg = (Message)object;
            found(msg.getContentComplexType());
            found(msg.getContentSimpleType());
            found(msg.getContentElement());
            if (msg instanceof Output) {
                found(((Output)msg).getXmlDocument());
            }
        }
        return true;
    }

    protected void found( XSDComponent xsdComponent ) {
        if (xsdComponent != null) {
            final XSDSchema schema = xsdComponent.getSchema();
            if (schema != null) {
                this.xsds.add(schema);
                final String ns = schema.getTargetNamespace();
                if (ns != null && ns.trim().length() != 0) {
                    this.targetNamespaces.add(ns);
                }
            }
        }
    }

    protected void found( XmlDocument xmlDocument ) {
        if (xmlDocument != null) {
            final XmlRoot root = xmlDocument.getRoot();
            if (root != null) {
                found(root.getXsdComponent());
            }
        }
    }

    public Set getXsdSchemas() {
        return this.xsds;
    }

    public Set getXsdTargetNamespaces() {
        return this.targetNamespaces;
    }

}
