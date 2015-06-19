/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.metamodels.wsdl.Definitions;
import org.teiid.designer.metamodels.wsdl.WsdlPackage;
import org.teiid.designer.metamodels.wsdl.io.DelegatingResourceSet;
import org.teiid.designer.metamodels.wsdl.io.WsdlResourceFactoryImpl;
import org.teiid.designer.metamodels.wsdl.io.WsdlResourceImpl;



/** 
 * @since 8.0
 */
public class WebServiceResources implements StringConstants {

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
        if (!map.containsKey(WSDL)) {
            map.put(WSDL, new WsdlResourceFactoryImpl());
        }
        
        if (!map.containsKey(XSD)) {
            map.put(XSD, new XSDResourceFactoryImpl());
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
