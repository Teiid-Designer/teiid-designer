/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.internal.core.container.DefaultResourceFinder;
import com.metamatrix.vdb.edit.VdbEditPlugin;

/**
 * @since 4.3
 */
public class VdbContainerResourceFinder extends DefaultResourceFinder {

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * @param container
     * @since 4.3
     */
    public VdbContainerResourceFinder( final Container container ) {
        super(container);
    }

    // ==================================================================================
    // O V E R R I D D E N M E T H O D S
    // ==================================================================================

    /**
     * Walk through all XSDDirectives for the specified XSDResource and attempt to resolve those that are undefined.
     * 
     * @param eResource
     * @param recurse
     * @param visited
     * @since 4.3
     */
    @Override
    protected void resolveSchemaDirectives( final XSDResourceImpl eResource,
                                            final boolean recurse,
                                            final Set visited,
                                            final Set unresolvedResourceURIs ) {

        if (eResource != null && !visited.contains(eResource)) {
            // The resource must be loaded to retrieve its contents
            if (!eResource.isLoaded()) {
                try {
                    eResource.load(getContainer().getLoadOptions());
                } catch (IOException err) {
                    String msg = ModelerCore.Util.getString("DefaultResourceFinder.Error_loading_resource", eResource); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, msg);
                }
            }
            // Add this resource to the list of those visited
            visited.add(eResource);

            // Check all imports to see if they were resolved
            for (final Iterator i = eResource.getSchema().eContents().iterator(); i.hasNext();) {
                EObject eObj = (EObject)i.next();
                if (eObj instanceof XSDSchemaDirective) {
                    XSDSchema resolvedSchema = ((XSDSchemaDirective)eObj).getResolvedSchema();

                    // Directive is not yet resolved, attempt to locate the reference ...
                    if (resolvedSchema == null) {
                        XSDSchemaDirective directive = (XSDSchemaDirective)eObj;
                        XSDResourceImpl refdResource = null;

                        // If the location is of the form "http://vdb.metamatrix.com/...?vdbToken=..." then
                        // we need to find the underlying file location in order to resolve the import
                        String location = directive.getSchemaLocation();
                        if (isWebServicelURL(location)) {
                            refdResource = (XSDResourceImpl)getContainer().getResource(URI.createURI(location), false);
                        } else {
                            URI baseLocationURI = eResource.getURI();
                            URI schemaLocationURI = (baseLocationURI.isFile() ? URI.createURI(location, false) : URI.createURI(location));
                            if (baseLocationURI.isHierarchical() && !baseLocationURI.isRelative()
                                && schemaLocationURI.isRelative()) {
                                schemaLocationURI = schemaLocationURI.resolve(baseLocationURI);
                            }
                            refdResource = (XSDResourceImpl)getContainer().getResource(schemaLocationURI, false);
                        }
                        if (refdResource != null) {
                            resolvedSchema = refdResource.getSchema();
                            directive.setResolvedSchema(resolvedSchema);
                            if (directive instanceof XSDImport) {
                                ((XSDSchemaImpl)resolvedSchema).imported((XSDImport)directive);
                            } else if (directive instanceof XSDInclude) {
                                ((XSDSchemaImpl)resolvedSchema).included((XSDInclude)directive);
                            } else if (directive instanceof XSDRedefine) {
                                ((XSDSchemaImpl)resolvedSchema).redefined((XSDRedefine)directive);
                            }
                        }
                    }

                    // If the reference is a proxy, attempt to resolve it
                    if (resolvedSchema != null && resolvedSchema.eIsProxy()) {
                        resolvedSchema = (XSDSchema)EcoreUtil.resolve(resolvedSchema, eResource.getResourceSet());
                    }

                    // Log any unresolved schema directives
                    if (resolvedSchema == null || resolvedSchema.eResource() == null
                        || resolvedSchema.eResource().getResourceSet() == null) {
                        String location = ((XSDSchemaDirective)eObj).getSchemaLocation();
                        int endIndex = location.lastIndexOf(VdbEditPlugin.URL_SUFFIX_FOR_VDB);
                        int beginIndex = VdbEditPlugin.URL_ROOT_FOR_VDB.length();
                        if (endIndex > 0 && beginIndex < endIndex) {
                            location = location.substring(VdbEditPlugin.URL_ROOT_FOR_VDB.length(), endIndex);
                        }
                        URI unresolvedURI = URI.createURI(location);

                        unresolvedResourceURIs.add(unresolvedURI);
                        continue;
                    }

                    // Follow the chain and resolve all directives for the schema being imported
                    if (recurse) resolveSchemaDirectives((XSDResourceImpl)resolvedSchema.eResource(),
                                                         recurse,
                                                         visited,
                                                         unresolvedResourceURIs);
                }
            }
        }
    }

    // protected XSDSchema resolveSchemaImport(final XSDResourceImpl eResource, XSDImportImpl xsdImport) {
    // XSDSchema resolvedSchema = xsdImport.getResolvedSchema();
    // if (resolvedSchema == null) {
    // String location = xsdImport.getSchemaLocation();
    //            
    // // If the location is of the form "http://vdb.metamatrix.com/...?vdbToken=..." then
    // // we need to find the underlying file location in order to resolve the import
    // if (isWebServicelURL(location)) {
    // Resource refdResource = getContainer().getResource(URI.createURI(location), false);
    // if (refdResource != null) {
    // URI resourceURI = eResource.getURI();
    // URI importURI = refdResource.getURI();
    // if (importURI.isFile()) {
    // boolean deresolve = (resourceURI != null && !resourceURI.isRelative() && resourceURI.isHierarchical());
    // if (deresolve && !importURI.isRelative()) {
    // URI deresolvedURI = importURI.deresolve(resourceURI, true, true, false);
    // if (deresolvedURI.hasRelativePath()) {
    // importURI = deresolvedURI;
    // }
    // }
    // xsdImport.setSchemaLocation(importURI.toString());
    // resolvedSchema = xsdImport.importSchema();
    // xsdImport.setSchemaLocation(location);
    // System.out.println("resolveSchemaDirectives() "+location+" -> "+importURI.toString());
    // }
    // }
    // } else {
    // resolvedSchema = xsdImport.importSchema();
    // }
    // }
    // return resolvedSchema;
    // }

    /**
     * Return true if the url string if of the form "http://vdb.metamatrix.com/...?vdbToken=..." otherwise return false;
     * 
     * @param url
     * @return
     * @since 4.3
     */
    protected boolean isWebServicelURL( final String url ) {
        if (!StringUtil.isEmpty(url)) {
            int endIndex = url.lastIndexOf(VdbEditPlugin.URL_SUFFIX_FOR_VDB);
            int beginIndex = VdbEditPlugin.URL_ROOT_FOR_VDB.length();

            // If the location is of the form "http://vdb.metamatrix.com/...?vdbToken=..." then
            // we need to find the underlying file location in order to resolve the import
            if (endIndex > 0 && beginIndex < endIndex) {
                return true;
            }
        }
        return false;
    }

}
