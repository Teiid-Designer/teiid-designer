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
package com.metamatrix.metamodels.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.id.UUID;
import com.metamatrix.modeler.core.resource.XResource;
import com.metamatrix.modeler.core.resource.XResourceDelegate;

/**
 * @since 5.0.3
 */
public final class XsdResourceFactory extends XSDResourceFactoryImpl {

    /**
     * @see org.eclipse.xsd.util.XSDResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
     * @since 5.0.3
     */
    @Override
    public Resource createResource( URI uri ) {
        return new XsdResource(uri);
    }

    private class XsdResource extends XSDResourceImpl implements XResource {

        private XResourceDelegate delegate = new XResourceDelegate();

        XsdResource( URI uri ) {
            super(uri);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#attachedHelper(org.eclipse.emf.ecore.EObject)
         */
        @Override
        protected void attachedHelper( EObject eObject ) {
            delegate.attachedHelper(eObject);
            super.attachedHelper(eObject);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#detachedHelper(org.eclipse.emf.ecore.EObject)
         */
        @Override
        protected void detachedHelper( EObject eObject ) {
            delegate.detachedHelper(eObject);
            super.detachedHelper(eObject);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.xsd.util.XSDResourceImpl#doLoad(java.io.InputStream, java.util.Map)
         */
        @Override
        public void doLoad( InputStream inputStream,
                            Map<?, ?> options ) throws IOException {
            if (delegate.isLoading()) {
                return;
            }
            delegate.setLoading(true);
            try {
                super.doLoad(inputStream, options);
            } finally {
                delegate.setLoading(false);
            }
        }

        @Override
        protected void doUnload() {
            if (delegate.isUnloading()) {
                return;
            }
            delegate.setUnloading(true);
            try {
                super.doUnload();
                delegate.doUnload();
            } finally {
                delegate.setUnloading(false);
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.xsd.util.XSDResourceImpl#getEObject(java.lang.String)
         */
        @Override
        public EObject getEObject( String uriFragment ) {
            // if the URI fragment has the UUID protocol we need to look up the object using
            // the resources maps
            if ((uriFragment != null) && uriFragment.startsWith(UUID.PROTOCOL)) {
                return getEObjectByID(uriFragment);
            }

            return super.getEObject(uriFragment);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#getEObjectByID(java.lang.String)
         */
        @Override
        protected EObject getEObjectByID( String uuid ) {
            EObject eObject = delegate.getEObjectById(uuid);
            if (eObject != null) {
                return eObject;
            }
            return super.getEObjectByID(uuid);
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.modeler.core.resource.XResource#getUuid(org.eclipse.emf.ecore.EObject)
         */
        public String getUuid( EObject object ) {
            return delegate.getUuid(object);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#isAttachedDetachedHelperRequired()
         */
        @Override
        protected boolean isAttachedDetachedHelperRequired() {
            return true;
        }

        /**
         * @see com.metamatrix.modeler.core.resource.XResource#isLoading()
         * @since 5.0.3
         */
        @Override
        public boolean isLoading() {
            return delegate.isLoading();
        }

        /**
         * @see com.metamatrix.modeler.core.resource.XResource#isLoading()
         * @since 5.0.3
         */
        public boolean isUnloading() {
            return delegate.isUnloading();
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.modeler.core.resource.XResource#setUuid(org.eclipse.emf.ecore.EObject, java.lang.String)
         */
        public void setUuid( EObject object,
                             String uuid ) {
            delegate.setUuid(object, uuid);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#toKeyString()
         */
        @Override
        public String toKeyString() {
            return delegate.toKeyString();
        }
    }
}
