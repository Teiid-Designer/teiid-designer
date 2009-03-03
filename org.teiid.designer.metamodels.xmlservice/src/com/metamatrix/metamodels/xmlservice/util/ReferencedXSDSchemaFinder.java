/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.metamodels.xmlservice.XmlMessage;

/**
 * @since 4.2
 */
public class ReferencedXSDSchemaFinder extends XmlServiceComponentFinder {

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
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        if (object instanceof XmlMessage) {
            final XmlMessage msg = (XmlMessage)object;
            found(msg.getContentElement());
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

    public Set getXsdSchemas() {
        return this.xsds;
    }

    public Set getXsdTargetNamespaces() {
        return this.targetNamespaces;
    }

}
