/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd;

import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.core.ModelInitializer;

/**
 * AbstractXsdInitializer
 */
public abstract class AbstractXsdInitializer implements ModelInitializer {

    /**
     * Recommended namespace qualifier for the XML Schema namespace.  Value is "xsd".
     */
    public static final String XSD_NS_PREFIX = "xsd"; //$NON-NLS-1$

    /**
     * Construct an instance of AbstractXsdInitializer.
     * 
     */
    public AbstractXsdInitializer() {
        super();
    }
    
    protected abstract String getXsdNamespace();

    /**
     * @see com.metamatrix.modeler.core.ModelInitializer#execute(org.eclipse.emf.ecore.resource.Resource)
     */
    public IStatus execute( final Resource model) {
        ArgCheck.isNotNull(model);
        // Check the resource type ...
        if ( !(model instanceof XSDResourceImpl) ) {
            final Object[] params = new Object[]{model.getClass().getName(),XSDResourceImpl.class.getName()};
            final String msg = XsdPlugin.Util.getString("AbstractXsdInitializer.Unexpected_resource_type__{0}_(expected_{1})",params); //$NON-NLS-1$
            return new Status(IStatus.WARNING,XsdPlugin.PLUGIN_ID,0,msg,null);
        }
        
        // See if there is already a schema object ...
        final List roots = model.getContents();
        final EObject existingRoot = roots.isEmpty() ? null : (EObject) roots.get(0);
        XSDSchema xsdSchema = null;
        if ( existingRoot == null ) {
            // Create the schema 
            xsdSchema = XSDFactory.eINSTANCE.createXSDSchema();
            roots.add(xsdSchema);
        } else if ( existingRoot instanceof XSDSchema ) {
            xsdSchema = (XSDSchema)existingRoot;
        }
        
        // Initialize the schema object 
        if ( xsdSchema != null ) {
            // If you want schema tags and references to schema types to be qualified, 
            // which is recommend, this is the recommended qualifier.
            xsdSchema.setSchemaForSchemaQNamePrefix(XSD_NS_PREFIX);
            
            // Add the schema of schemas namespace ...
            final Map qNamePrefixToNamespaceMap = xsdSchema.getQNamePrefixToNamespaceMap();
            final String schemaOfSchemaNamespace = getXsdNamespace();
            qNamePrefixToNamespaceMap.put(xsdSchema.getSchemaForSchemaQNamePrefix(),schemaOfSchemaNamespace);
        }
        
        final String msg = XsdPlugin.Util.getString("AbstractXsdInitializer.Initialized_the_schema"); //$NON-NLS-1$
        return new Status(IStatus.OK,XsdPlugin.PLUGIN_ID,0,msg,null);
    }

}
