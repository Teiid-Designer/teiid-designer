/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.wst.wsdl.internal.util.WSDLResourceFactoryImpl;
import org.eclipse.wst.wsdl.util.WSDLResourceImpl;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;

import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.ui.ICredentialsCommon.SecurityType;

/**
 * This code is a hodgepodge of WSDL4J and Eclipse XSD. The WSDL to Relational modelgenerator uses WSDL4J to read the WSDL. Schema
 * tools uses Eclipse XSD to understand schema. This class bridges the gap by creating schemas in a way that Eclipse XSD does as
 * much of the work of resolving schema dependencies as possible. This is largely accomplished through the behind the scenes work
 * of Resources and ResourceSets.
 */
public class WSDLSchemaExtractor {

    private ResourceSet resourceSet;
    private HashSet schemas;

    /**
     * Create a WSDLSchemaExtractor with the URI to the user selected WSDL. 
     * The URI will be used by the ResourceSet to find the included Schema.
     */
    public WSDLSchemaExtractor() {
        resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("wsdl", new WSDLResourceFactoryImpl()); //$NON-NLS-1$
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl()); //$NON-NLS-1$
        schemas = new HashSet();
    }

    public void findSchema( String wsdlUriString, SecurityType securityType, String userName, String password ) throws IOException {
        URI uri;
        InputStream inputStream = null;

        URI wsdlURI = URI.createURI(wsdlUriString);
        if (wsdlURI.isFile()) {
            File testWsdl = new File(wsdlURI.devicePath());
            uri = URI.createFileURI(testWsdl.getCanonicalPath().toString());
        } else {
            uri = URI.createURI(wsdlUriString);

            /*
             * Loading the wsdl resource fails if authentication of
             * the http connection is required so we can avoid that
             * by asking the resource to load from an established 
             * inputstream instead.
             *
             * Establish the inputstream.
             */
            URL remoteURL = new URL(wsdlUriString);
            URLConnection urlConn = remoteURL.openConnection();

            if (securityType != null && ! SecurityType.None.equals(securityType)) {        		
                URLHelper.setCredentials(urlConn, userName, password);
            }

            inputStream = urlConn.getInputStream();
        }
        WSDLResourceFactoryImpl fac = (WSDLResourceFactoryImpl)resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().get("wsdl"); //$NON-NLS-1$
        Resource res = fac.createResource(uri);
        if (res instanceof WSDLResourceImpl) {
            WSDLResourceImpl wsdlResource = (WSDLResourceImpl)res;
            wsdlResource.basicSetResourceSet(resourceSet, null);
            if (!wsdlResource.isLoaded()) {
                if (inputStream == null) {
                    wsdlResource.load(null);
                } else {
                    wsdlResource.load(inputStream, null);
                 }
            }
            Definition def = wsdlResource.getDefinition();
            Types types = def.getETypes();
            if (null != types) {
                schemas.addAll(types.getSchemas());
            }
        }

        if (inputStream != null) {
            inputStream.close();
        }
    }

    public XSDSchema[] getSchemas() {
        // The ResourceSet will load all of the schemas imported by xsd:import
        // but not the embedded schema. That schema is already in the schemas HashSet.
        Iterator resources = resourceSet.getResources().iterator();
        while (resources.hasNext()) {
            Resource res = (Resource)resources.next();
            if (res instanceof XSDResourceImpl) {
                XSDResourceImpl schema = (XSDResourceImpl)res;
                schemas.add(schema.getSchema());
            }
        }
        XSDSchema[] retVal = new XSDSchema[schemas.size()];
        schemas.toArray(retVal);
        return retVal;
    }
}
