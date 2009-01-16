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

package com.metamatrix.metamodels.wsdl.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import com.metamatrix.modeler.core.resource.XResource;
import com.metamatrix.modeler.core.resource.XmlXResourceDelegate;

/**
 * <!-- begin-user-doc --> The <b>Resource </b> associated with the package. <!-- end-user-doc -->
 *
 * @see com.metamatrix.metamodels.wsdl.io.WsdlResourceFactoryImpl
 * @generated
 */
public class WsdlResourceImpl extends XMLResourceImpl implements XResource {

    /**
	 * Option flag for {@link org.eclipse.emf.ecore.resource.Resource#save(java.util.Map) saving} that defines whether to insert
	 * newlines into the output XML file. By default, newlines are added. This can be set to 'false' if the file is to be only
	 * processed by machine.
	 */
	public static final String OPTION_INSERT_NEWLINES = "OPTION_INSERT_NEWLINES"; //$NON-NLS-1$

    /**
	 * Option flag for {@link org.eclipse.emf.ecore.resource.Resource#save(java.util.Map) saving} that defines how many characters
	 * each successive line of XML be indented. The default is '2'.
	 *
	 * @see #DEFAULT_INDENTATION
	 */
	public static final String OPTION_INDENTATION = "OPTION_INDENTATION"; //$NON-NLS-1$

    /**
	 * The default indentation for each successive level of XML is 2 spaces.
	 *
	 * @see #OPTION_INDENTATION
	 */
	public static final String DEFAULT_INDENTATION = "  "; //$NON-NLS-1$

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

	private XmlXResourceDelegate delegate = new XmlXResourceDelegate();

	/**
	 * Creates an instance of the resource. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param uri the URI of the new resource.
	 * @generated
	 */
	public WsdlResourceImpl( URI uri ) {
		super(uri);

		delegate.initialize(this);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#attachedHelper(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void attachedHelper( EObject eObject ) {
		if (isTrackingModification()) {
			eObject.eAdapters().add(modificationTrackingAdapter);
		}
		delegate.attachedHelper(this, eObject);
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#createXMLLoad()
	 * @since 4.2
	 */
	@Override
    protected XMLLoad createXMLLoad() {
		return new WsdlLoader();
	}

    /**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#createXMLSave()
	 * @since 4.2
	 */
	@Override
    protected XMLSave createXMLSave() {
		return new WsdlWriter();
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#detachedHelper(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void detachedHelper( EObject eObject ) {
		delegate.detachedHelper(this, eObject);
		if (isTrackingModification()) {
			eObject.eAdapters().remove(modificationTrackingAdapter);
		}
	}

    /**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#doLoad(java.io.InputStream, java.util.Map)
	 * @since 5.0.3
	 */
	@Override
    public void doLoad( InputStream inputStream,
	                    Map options ) throws IOException {
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
		} finally {
			delegate.setUnloading(false);
		}
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see com.metamatrix.modeler.core.resource.XResource#getUuid(org.eclipse.emf.ecore.EObject)
	 */
	public String getUuid( EObject object ) {
	    return getID(object);
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#isAttachedDetachedHelperRequired()
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
	 * @see com.metamatrix.modeler.core.resource.XResource#isUnloading()
	 * @since 5.0.3
	 */
	public boolean isUnloading() {
		return delegate.isUnloading();
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see com.metamatrix.modeler.core.resource.XResource#setUuid(org.eclipse.emf.ecore.EObject, java.lang.String)
	 */
	public void setUuid( EObject object,
	                     String uuid ) {
		setID(object, uuid);
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#useUUIDs()
	 */
	@Override
	protected boolean useUUIDs() {
		return true;
	}
} // WsdlResourceFactoryImpl
