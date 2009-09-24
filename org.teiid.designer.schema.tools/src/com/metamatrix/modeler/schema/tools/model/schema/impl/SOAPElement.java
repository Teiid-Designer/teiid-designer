/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.modeler.schema.tools.model.schema.ISchemaModelCopyTraversalContext;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public class SOAPElement extends ElementImpl {

    public SOAPElement( XSDElementDeclaration elem,
                        String namespacePrefix,
                        XSDTypeDefinition type,
                        XSDSchema schema ) {
        super(elem, namespacePrefix, type, schema);
    }

    // Element that are defined by a SOAP array are returning xpaths that
    // sometimes have a training slash, not sure why it's inconsistent but this
    // will handle both cases.
    @Override
    public String getOutputXPath() {
        String xPath = super.getOutputXPath();
        if (!xPath.endsWith("/")) { //$NON-NLS-1$
            xPath += "/*"; //$NON-NLS-1$
        } else {
            xPath += "*"; //$NON-NLS-1$
        }
        return xPath;
    }

    @Override
    public SchemaObject copy( ISchemaModelCopyTraversalContext ctx ) {
        return new SOAPElement(elem, getNamespacePrefix(), type, schema);
    }
}
