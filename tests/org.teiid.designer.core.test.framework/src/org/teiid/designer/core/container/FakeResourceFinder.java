/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.container;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchemaDirective;
import org.teiid.core.designer.id.ObjectID;
import org.teiid.designer.metamodels.core.ModelImport;



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
     * @see org.teiid.designer.core.container.ResourceFinder#findByEObject(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public Resource findByEObject(EObject eObject) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByImport(org.eclipse.xsd.XSDSchemaDirective, boolean)
     * @since 4.3
     */
    @Override
	public Resource findByImport(XSDSchemaDirective theImport,
                                 boolean searchExternal) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByImport(org.eclipse.xsd.XSDSchemaDirective, org.eclipse.emf.ecore.resource.Resource[])
     * @since 4.3
     */
    @Override
	public Resource findByImport(XSDSchemaDirective theImport,
                                 Resource[] scope) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByImport(org.teiid.designer.metamodels.core.ModelImport, boolean)
     * @since 4.3
     */
    @Override
	public Resource findByImport(ModelImport theImport,
                                 boolean searchExternal) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByImport(org.teiid.designer.metamodels.core.ModelImport, org.eclipse.emf.ecore.resource.Resource[])
     * @since 4.3
     */
    @Override
	public Resource findByImport(ModelImport theImport,
                                 Resource[] scope) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByName(java.lang.String, boolean, boolean)
     * @since 4.3
     */
    @Override
	public Resource[] findByName(String theName,
                                 boolean caseSensitive,
                                 boolean searchExternal) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByURI(org.eclipse.emf.common.util.URI, boolean)
     * @since 4.3
     */
    @Override
	public Resource findByURI(URI theUri,
                              boolean searchExternal) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByUUID(ObjectID, boolean)
     * @since 4.3
     */
    @Override
	public Resource findByUUID(ObjectID uuid,
                               boolean searchExternal) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByUUID(ObjectID, Resource[])
     * @since 4.3
     */
    @Override
	public Resource findByUUID(ObjectID uuid,
                               Resource[] scope) {
        return null;
    }
    
    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findByWorkspaceUri(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    @Override
	public Resource findByWorkspaceUri(URI theUri, Resource eResource) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#isBuiltInResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    @Override
	public boolean isBuiltInResource(Resource theResource) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#isBuiltInResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    @Override
	public boolean isBuiltInResource(URI theUri) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#isBuiltInSystemResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    @Override
	public boolean isBuiltInSystemResource(Resource theResource) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#isBuiltInSystemResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    @Override
	public boolean isBuiltInSystemResource(URI theUri) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#isExternalResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    @Override
	public boolean isExternalResource(Resource theResource) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#isExternalResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    @Override
	public boolean isExternalResource(URI theUri) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findReferencesFrom(org.eclipse.emf.ecore.resource.Resource, boolean, boolean)
     * @since 4.3
     */
    @Override
	public Resource[] findReferencesFrom(Resource theResource,
                                         boolean recurse,
                                         boolean includeExternal) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findReferencesTo(org.eclipse.emf.ecore.resource.Resource, boolean)
     * @since 4.3
     */
    @Override
	public Resource[] findReferencesTo(Resource theResource,
                                       boolean recurse) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findUnresolvedResourceLocations(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    @Override
	public String[] findUnresolvedResourceLocations(Resource theResource) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.container.ResourceFinder#findMissingImportLocations(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    @Override
	public String[] findMissingImportLocations(Resource theResource) {
        return null;
    }


}
