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

package com.metamatrix.modeler.internal.webservice;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.io.DelegatingResourceSet;
import com.metamatrix.metamodels.wsdl.io.WsdlResourceFactoryImpl;
import com.metamatrix.metamodels.wsdl.io.WsdlResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;


/** 
 * @since 4.2
 */
public class WebServiceResources {

    private final ResourceSet resourceSet;
    
    public class Reference {
        public final String namespace;
        public final String location;
        public final Resource resource;
        public Reference( final String namespace, final String location, final Resource resource ) {
            this.namespace = namespace;
            this.location = location;
            this.resource = resource;
        }
    }
    
    /** 
     * 
     * @since 4.2
     */
    public WebServiceResources() {
        super();

        // Create a resource set that can delegate to the XSDSchema's global resource set ...
        this.resourceSet = new DelegatingResourceSet();
        final ResourceSet xsdGlobalResourceSet = XSDSchemaImpl.getGlobalResourceSet();
        ((DelegatingResourceSet)this.resourceSet).addDelegateResourceSet(xsdGlobalResourceSet);
        
        // Register the resource factory for each of the 4 WSDL metamodels ...
        final Resource.Factory.Registry registry = this.resourceSet.getResourceFactoryRegistry();
        
        Map map = registry.getExtensionToFactoryMap();
        if (!map.containsKey(ModelUtil.EXTENSION_WSDL)) {
            map.put(ModelUtil.EXTENSION_WSDL, new WsdlResourceFactoryImpl());
        }
        
        if (!map.containsKey(ModelUtil.EXTENSION_XSD)) {
            map.put(ModelUtil.EXTENSION_XSD, new XSDResourceFactoryImpl());
        }
        
        registry.getProtocolToFactoryMap().put(WsdlPackage.eNS_URI, new WsdlResourceFactoryImpl());
    }
    
    public Resource add( final URI uri ) {
        final Resource resource = this.resourceSet.getResource(uri,true);
        return resource;
    }
    
    public Resource get( final URI uri ) {
        final Resource resource = this.resourceSet.getResource(uri,true);
        return resource;
    }
    
    public void refresh( final Resource resource ) throws IOException {
        resource.unload();
        Map options = (resource.getResourceSet() != null ? resource.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
        resource.load(options);
    }
    
    public void remove( final URI uri ) {
        final Resource existing = this.resourceSet.getResource(uri,false);
        if ( existing != null ) {
            this.resourceSet.getResources().remove(existing);
        }
    }
    
    public void remove( final Resource resource ) {
        this.resourceSet.getResources().remove(resource);
    }
    
    public boolean isWsdl( final Resource resource ) {
        if ( resource instanceof WsdlResourceImpl ) {
            return true;
        }
        return false;
    }
    
    public boolean isXsd( final Resource resource ) {
        if ( resource instanceof XSDResourceImpl ) {
            return true;
        }
        return false;
    }

    
    public String getTargetNamespace( final Resource resource ) {
        if ( isWsdl(resource) ) {
            return doGetTargetNamespace((WsdlResourceImpl)resource);
        }
        if ( isXsd(resource) ) {
            return doGetTargetNamespace((XSDResourceImpl)resource);
        }
        return null;
    }
    
    protected String doGetTargetNamespace( final WsdlResourceImpl resource ) {
        final List roots = resource.getContents();
        final Iterator iter = roots.iterator();
        while (iter.hasNext()) {
            final Object object = iter.next();
            if ( object instanceof Definitions ) {
                final Definitions defns = (Definitions)object;
                return defns.getTargetNamespace();
            }
        }
        return null;
    }

    protected String doGetTargetNamespace( final XSDResourceImpl resource ) {
        // Get the XSDSchema ...
        final XSDSchema schema = resource.getSchema();
        return schema.getTargetNamespace();
    }


}
