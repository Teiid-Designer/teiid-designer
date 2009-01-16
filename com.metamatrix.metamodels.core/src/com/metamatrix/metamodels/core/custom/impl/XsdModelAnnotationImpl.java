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

package com.metamatrix.metamodels.core.custom.impl;

import java.util.Iterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.core.impl.ModelAnnotationImpl;

/**
 * mmDefect_12555 - Created XsdModelAnnotationImpl so that an Resource reference was available in the ModelAnnotationItemProvider
 * since ModelAnnotation instances associated with XSD resources return null for the eResource reference.
 * 
 * @since 4.2
 */
public class XsdModelAnnotationImpl extends ModelAnnotationImpl {

    protected Resource xsdResource;

    /**
     * @since 4.2
     */
    public XsdModelAnnotationImpl() {
        super();
    }

    /**
     * @since 4.2
     */
    public XsdModelAnnotationImpl( final Resource xsdResource ) {
        super();
        this.xsdResource = xsdResource;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.impl.BasicEObjectImpl#eResource()
     */
    @Override
    public Resource eResource() {
        // since this object is NOT added to the resource contents it does not have an eResource. Our framework for finding
        // an object given a UUID (and vice versa) requires a resource.
        return getResource();
    }

    /**
     * @see com.metamatrix.metamodels.core.xsd.XsdModelAnnotation#getResource()
     * @since 4.2
     */
    public Resource getResource() {
        return this.xsdResource;
    }

    /**
     * @see com.metamatrix.metamodels.core.xsd.XsdModelAnnotation#setResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public void setResource( final Resource xsdResource ) {
        this.xsdResource = xsdResource;
    }

    @Override
    public void setExtensionPackage( XPackage newExtensionPackage ) {
        super.setExtensionPackage(newExtensionPackage);

        // mmDefect_12555 - Perform some get/set trickery on the xsd resource so that it is marked as requiring save
        if (this.xsdResource != null) {
            this.xsdResource.setModified(true);
            EObject schema = this.getSchema(this.xsdResource);
            if (schema != null) {
                EAttribute eAttrib = this.getTargetNamespaceAttribute(schema);
                if (eAttrib != null) {
                    Object origValue = schema.eGet(eAttrib);
                    schema.eSet(eAttrib, null);
                    schema.eSet(eAttrib, origValue);
                }
            }
        }
    }

    private EObject getSchema( final Resource resource ) {
        for (Iterator iter = resource.getContents().iterator(); iter.hasNext();) {
            Object root = iter.next();
            if (root instanceof EObject) {
                final EClass eClass = ((EObject)root).eClass();
                if (eClass.getName().equalsIgnoreCase("XSDSchema")) { //$NON-NLS-1$
                    return (EObject)root;
                }
            }
        }
        return null;
    }

    private EAttribute getTargetNamespaceAttribute( final EObject eObject ) {
        final EClass eClass = eObject.eClass();
        return (EAttribute)eClass.getEStructuralFeature("targetNamespace"); //$NON-NLS-1$
    }
}
