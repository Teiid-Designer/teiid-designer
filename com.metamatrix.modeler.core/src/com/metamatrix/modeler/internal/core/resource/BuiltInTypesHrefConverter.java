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

package com.metamatrix.modeler.internal.core.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.resource.EObjectHrefConverter;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.util.ModelObjectCollector;


/** 
 * @since 5.0
 */
public class BuiltInTypesHrefConverter implements EObjectHrefConverter {
    
    /** Delimiter used to separate the URI string from the URI fragment */
    protected static final String URI_REFERENCE_DELIMITER = "#"; //$NON-NLS-1$

    protected static final String SCHEMA_FOR_SCHEMA_URI_2001_SUFFIX = "www.w3.org/2001/XMLSchema.xsd"; //$NON-NLS-1$
    protected static final String SCHEMA_INSTANCE_URI_2001_SUFFIX   = "www.w3.org/2001/XMLSchema-instance.xsd"; //$NON-NLS-1$
    protected static final String MAGIC_SCHEMA_URI_2001_SUFFIX      = "www.w3.org/2001/MagicXMLSchema.xsd"; //$NON-NLS-1$
    
    protected static final URI SCHEMA_FOR_SCHEMA_URI = URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
    protected static final URI SCHEMA_INSTANCE_URI   = URI.createURI(XSDConstants.SCHEMA_INSTANCE_URI_2001);
    protected static final URI XML_MAGIC_SCHEMA_URI  = URI.createURI("http://www.w3.org/2001/MagicXMLSchema"); //$NON-NLS-1$
    protected static final URI XML_SCHEMA_URI        = URI.createURI("http://www.w3.org/2001/xml.xsd"); //$NON-NLS-1$
    protected static final URI BUILTIN_DATATYPES_URI = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);
    
    private static final boolean DEBUG = false;
    
    private Map logicalUriMap = null;
    private Map physicalUriMap = null;
    
    private ResourceSet eResourceSet = null;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /** 
     * 
     * @since 5.0
     */
    public BuiltInTypesHrefConverter(final ResourceSet theResourceSet) {
        ArgCheck.isNotNull(theResourceSet);
        this.eResourceSet   = theResourceSet;
        this.logicalUriMap  = new HashMap();
        this.physicalUriMap = new HashMap();
        doInit();
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.modeler.core.resource.EObjectHrefConverter#getLogicalURI(org.eclipse.emf.ecore.EObject)
     * @since 5.0
     */
    public URI getLogicalURI(final EObject eObject) {
        ArgCheck.isNotNull(eObject);
        URI physicalURI = createPhysicalURI(eObject);
        return (URI)this.logicalUriMap.get(physicalURI);
    }

    /** 
     * @see com.metamatrix.modeler.core.resource.EObjectHrefConverter#getLogicalURI(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    public URI getLogicalURI(final URI physicalURI) {
        ArgCheck.isNotNull(physicalURI);
        return (URI)this.logicalUriMap.get(physicalURI);
    }

    /** 
     * @see com.metamatrix.modeler.core.resource.EObjectHrefConverter#getPhysicalURI(org.eclipse.emf.ecore.EObject)
     * @since 5.0
     */
    public URI getPhysicalURI(final EObject eObject) {
        ArgCheck.isNotNull(eObject);
        URI logicalURI = createLogicalURI(eObject);
        return (URI)this.physicalUriMap.get(logicalURI);
    }

    /** 
     * @see com.metamatrix.modeler.core.resource.EObjectHrefConverter#getPhysicalURI(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    public URI getPhysicalURI(final URI logicalURI) {
        ArgCheck.isNotNull(logicalURI);
        return (URI)this.physicalUriMap.get(logicalURI);
    }
    
    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================
    
    /**
     * Populate the URI map with the logical to physical URI EObject mappings for all built-in
     * datatypes along with the inverse mappings of physical to logical URI.
     */
    protected synchronized void doInit() {
            
        // Create URI mappings for the MetaMatrix built-in datatypes resource
        // "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
        XSDResourceImpl xsdResource = getBuiltInTypesResource(BUILTIN_DATATYPES_URI);
        if (xsdResource != null) {
            XSDSchema schema = xsdResource.getSchema();
            Assertion.isNotNull(schema);
            addMappings( getXsdComponents(schema) );
        } else {
            String msg = ModelerCore.Util.getString("BuiltInTypesHrefConverter.BuiltInTypes_resource_not_loaded",BUILTIN_DATATYPES_URI); //$NON-NLS-1$
            ModelerCore.Util.log(msg);
        }
        
        // Create URI mappings for the XML schema for schema resource
        // "http://www.w3.org/2001/XMLSchema"
        xsdResource = getBuiltInTypesResource(SCHEMA_FOR_SCHEMA_URI);
        if (xsdResource != null) {
            XSDSchema schema = xsdResource.getSchema();
            Assertion.isNotNull(schema);
            addMappings( getXsdComponents(schema) );
        } else {
            String msg = ModelerCore.Util.getString("BuiltInTypesHrefConverter.BuiltInTypes_resource_not_loaded",SCHEMA_FOR_SCHEMA_URI); //$NON-NLS-1$
            ModelerCore.Util.log(msg);
        }
        
        // Create URI mappings for the XML schema instance resource
        // "http://www.w3.org/2001/XMLSchema-instance"
        xsdResource = getBuiltInTypesResource(SCHEMA_INSTANCE_URI);
        if (xsdResource != null) {
            XSDSchema schema = xsdResource.getSchema();
            Assertion.isNotNull(schema);
            addMappings( getXsdComponents(schema) );
        } else {
            String msg = ModelerCore.Util.getString("BuiltInTypesHrefConverter.BuiltInTypes_resource_not_loaded",SCHEMA_INSTANCE_URI); //$NON-NLS-1$
            ModelerCore.Util.log(msg);
        }
        
        // Create URI mappings for the xml.xsd resource
        // "http://www.w3.org/2001/xml.xsd"
        xsdResource = getBuiltInTypesResource(XML_SCHEMA_URI);
        if (xsdResource != null) {
            XSDSchema schema = xsdResource.getSchema();
            Assertion.isNotNull(schema);
            addMappings( getXsdComponents(schema) );
        } else {
            String msg = ModelerCore.Util.getString("BuiltInTypesHrefConverter.BuiltInTypes_resource_not_loaded",XML_SCHEMA_URI); //$NON-NLS-1$
            ModelerCore.Util.log(msg);
        }
        
        // Create URI mappings for the XML magic schema resource (contains xs:anyType, xs:anySimpleType)
        // "http://www.w3.org/2001/MagicXMLSchema"
        xsdResource = getBuiltInTypesResource(XML_MAGIC_SCHEMA_URI);
        if (xsdResource != null) {
            XSDSchema schema = xsdResource.getSchema();
            Assertion.isNotNull(schema);
            addMappings( getXsdComponents(schema) );
        } else {
            String msg = ModelerCore.Util.getString("BuiltInTypesHrefConverter.BuiltInTypes_resource_not_loaded",XML_MAGIC_SCHEMA_URI); //$NON-NLS-1$
            ModelerCore.Util.log(msg);
        }
        
        if (DEBUG) {
            ModelerCore.Util.log("BuiltInTypesHrefConverter.doInit(): uriMap.size() = "+this.logicalUriMap.size()); //$NON-NLS-1$
        }
    }
    
    /**
     * Return the built-in type resource specified by the URI 
     * @param logicalUri
     * @return
     * @since 5.0
     */
    protected XSDResourceImpl getBuiltInTypesResource(final URI logicalUri) {
        ArgCheck.isNotNull(logicalUri);
        XSDResourceImpl r = (XSDResourceImpl)eResourceSet.getResource(logicalUri, false);
        if (r != null && !r.isLoaded()) {
            try {
                r.load(eResourceSet.getLoadOptions());
            } catch (Throwable e) {
                ModelerCore.Util.log(e);
            }
        }
        return r;
    }
    
    /**
     * Create URI mappings for the apecified array of XSDComponent instances 
     * @param components
     * @since 5.0
     */
    protected void addMappings(final XSDComponent[] components) {
        ArgCheck.isNotNull(components);
        for (int i = 0; i != components.length; ++i) {
            final URI logicalURI  = createLogicalURI(components[i]);
            final URI physicalURI = createPhysicalURI(components[i]);
            this.physicalUriMap.put(logicalURI, physicalURI);
            this.logicalUriMap.put(physicalURI, logicalURI);
            
            if (DEBUG) {
                ModelerCore.Util.log("BuiltInTypesHrefConverter.addMappings(): " + logicalURI + " -> " + physicalURI); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
    
    /**
     * Return an array of all XSDComponent instances found in the specified schema 
     * @param schema
     * @return
     * @since 5.0
     */
    protected XSDComponent[] getXsdComponents(final XSDSchema schema) {
        ArgCheck.isNotNull(schema);
        
        // Collect all the EObject instances in this resource using the
        // ModelObjectCollector class to avoid a ConcurrentModificationException
        // that may occur when using the TreeIterator (i.e. super.getAllContents())        
        Assertion.isNotNull(schema.eResource());
        final ModelObjectCollector moc = new ModelObjectCollector(schema.eResource());
        
        final List result = moc.getEObjects();
        for (final Iterator i = result.iterator(); i.hasNext();) {
            final EObject eObject = (EObject)i.next();
            if ( !(eObject instanceof XSDComponent) ) {
                i.remove();
            }
        }
        return (XSDComponent[])result.toArray(new XSDComponent[result.size()]);
    }
    
    /**
     * Return the logical URI for the specified XSD component.  The URIs are derived in a manner
     * that is consistent with the EResourceXmiSaveImpl method of writing href values representing
     * references to these XSD components 
     * @param component
     * @return
     * @since 5.0
     */
    protected URI createLogicalURI(final EObject eObject) {
        URI logicalURI = null;
        if (eObject instanceof XSDSimpleTypeDefinition) {
            logicalURI = URI.createURI( ((XSDSimpleTypeDefinition)eObject).getURI() );
        } else {
            // Get the URI for the object (this works if it's a proxy) ...
            final URI uri = EcoreUtil.getURI(eObject);
            final String uriString = uri.trimFragment().toString();

            // MagicXMLSchema.xsd suffix on the resource URI
            if (uriString.endsWith(MAGIC_SCHEMA_URI_2001_SUFFIX)) {
                logicalURI = XML_MAGIC_SCHEMA_URI.appendFragment(uri.fragment());
            }
            // XMLSchema.xsd suffix on the resource URI
            else if (uriString.endsWith(SCHEMA_FOR_SCHEMA_URI_2001_SUFFIX)) {
                logicalURI = SCHEMA_FOR_SCHEMA_URI.appendFragment(uri.fragment());
            }
            // XMLSchema-instance.xsd suffix on the resource URI
            else if (uriString.endsWith(SCHEMA_INSTANCE_URI_2001_SUFFIX)) {
                logicalURI = SCHEMA_INSTANCE_URI.appendFragment(uri.fragment());
            }
            
        }
        return logicalURI;
    }
    
    /**
     * Return the physical URI for the specified EObject.
     * @param eObject
     * @return
     * @since 5.0
     */
    protected URI createPhysicalURI(final EObject eObject) {
        return EcoreUtil.getURI(eObject);
    }

}
