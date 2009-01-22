/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.modeler.schema.tools.model.schema.ComplexSchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.ISchemaModelCopyTraversalContext;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObjectKey;

public class TypeDefinition extends BaseSchemaObject implements ComplexSchemaObject {

    private boolean doesNotHaveUniqueName;
    private XSDSchema schema;

    public TypeDefinition( XSDTypeDefinition type,
                           String namespacePrefix,
                           XSDSchema schema ) {
        super(namespacePrefix, type, schema);
        this.doesNotHaveUniqueName = false;
    }

    public SchemaObjectKey getKey() {
        return new TypeDefinitionKey(type);
    }

    public XSDTypeDefinition getType() {
        return type;
    }

    public String getNamespace() {
        return type.getTargetNamespace();
    }

    public String getName() {
        return type.getName();
    }

    public String getSimpleName() {
        String elemName = type.getName();
        String uniqueName;
        if (doesNotHaveUniqueName) {
            String typeName = type.getAliasName();
            uniqueName = elemName + '(' + typeName + ')';
        } else {
            uniqueName = elemName;
        }
        return uniqueName;
    }

    @Override
    public String toString() {
        String retval = type.getName();
        if (type.getTargetNamespace() != null) {
            retval += " (" + type.getTargetNamespace() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return retval;
    }

    public int getMinOccurs() {
        XSDParticle particle = type.getComplexType();
        if (particle == null) return 1;
        return particle.getMinOccurs();
    }

    public int getMaxOccurs() {
        XSDParticle particle = type.getComplexType();
        if (particle == null) return 1;
        return particle.getMaxOccurs();
    }

    public SchemaObject copy( ISchemaModelCopyTraversalContext ctx ) {
        TypeDefinition copy = new TypeDefinition(type, getNamespacePrefix(), schema);
        return copy;
    }

    public String getElementTypeNamespace() {
        return type.getTargetNamespace();
    }

    public RootElement getRootRepresentation() {
        return new RootElementImpl(getKey(), getName(), getNamespace(), true);
    }

    public String getCatalog() {
        return null;
    }

    @Override
    public boolean equals( Object obj ) {
        boolean result = false;
        if (obj instanceof TypeDefinition) {
            TypeDefinition other = (TypeDefinition)obj;
            if (this.type == other.type) {
                result = true;
            }
        }
        return result;
    }
}
