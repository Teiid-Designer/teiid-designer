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

package com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.modeler.schema.tools.model.schema.ISchemaModelCopyTraversalContext;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.ElementImpl;

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
