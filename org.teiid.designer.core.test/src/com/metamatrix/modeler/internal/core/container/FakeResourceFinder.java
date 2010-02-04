/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchemaDirective;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.container.ResourceFinder;


/** 
 * @since 4.2
 */
public class FakeResourceFinder implements  ResourceFinder {

    /** 
     * 
     * @since 4.2
     */
    public FakeResourceFinder() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByEObject(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Resource findByEObject(EObject eObject) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByImport(org.eclipse.xsd.XSDSchemaDirective, boolean)
     * @since 4.3
     */
    public Resource findByImport(XSDSchemaDirective theImport,
                                 boolean searchExternal) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByImport(org.eclipse.xsd.XSDSchemaDirective, org.eclipse.emf.ecore.resource.Resource[])
     * @since 4.3
     */
    public Resource findByImport(XSDSchemaDirective theImport,
                                 Resource[] scope) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByImport(com.metamatrix.metamodels.core.ModelImport, boolean)
     * @since 4.3
     */
    public Resource findByImport(ModelImport theImport,
                                 boolean searchExternal) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByImport(com.metamatrix.metamodels.core.ModelImport, org.eclipse.emf.ecore.resource.Resource[])
     * @since 4.3
     */
    public Resource findByImport(ModelImport theImport,
                                 Resource[] scope) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByName(java.lang.String, boolean, boolean)
     * @since 4.3
     */
    public Resource[] findByName(String theName,
                                 boolean caseSensitive,
                                 boolean searchExternal) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByURI(org.eclipse.emf.common.util.URI, boolean)
     * @since 4.3
     */
    public Resource findByURI(URI theUri,
                              boolean searchExternal) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByUUID(com.metamatrix.core.id.ObjectID, boolean)
     * @since 4.3
     */
    public Resource findByUUID(ObjectID uuid,
                               boolean searchExternal) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByUUID(com.metamatrix.core.id.ObjectID, org.eclipse.emf.ecore.resource.Resource[])
     * @since 4.3
     */
    public Resource findByUUID(ObjectID uuid,
                               Resource[] scope) {
        return null;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByWorkspaceUri(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    public Resource findByWorkspaceUri(URI theUri, Resource eResource) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#isBuiltInResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public boolean isBuiltInResource(Resource theResource) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#isBuiltInResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    public boolean isBuiltInResource(URI theUri) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#isBuiltInSystemResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public boolean isBuiltInSystemResource(Resource theResource) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#isBuiltInSystemResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    public boolean isBuiltInSystemResource(URI theUri) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#isExternalResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public boolean isExternalResource(Resource theResource) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#isExternalResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    public boolean isExternalResource(URI theUri) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findReferencesFrom(org.eclipse.emf.ecore.resource.Resource, boolean, boolean)
     * @since 4.3
     */
    public Resource[] findReferencesFrom(Resource theResource,
                                         boolean recurse,
                                         boolean includeExternal) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findReferencesTo(org.eclipse.emf.ecore.resource.Resource, boolean)
     * @since 4.3
     */
    public Resource[] findReferencesTo(Resource theResource,
                                       boolean recurse) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findUnresolvedResourceLocations(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public String[] findUnresolvedResourceLocations(Resource theResource) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findMissingImportLocations(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public String[] findMissingImportLocations(Resource theResource) {
        return null;
    }


}
