/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.io;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

/**
 * <!-- begin-user-doc -->
 * The <b>Resource Factory</b> associated with the package.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.io.WsdlResourceImpl
 */
public class WsdlResourceFactoryImpl extends XMLResourceFactoryImpl {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	//protected XMLResource.XMLMap xmlMap = new XMLMapImpl();

	/**
	 * Creates an instance of the resource factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WsdlResourceFactoryImpl()
	{
		super();
	}

	/**
	 * Creates an instance of the resource.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
    public Resource createResource(URI uri)
	{
		XMLResource result = new WsdlResourceImpl(uri);
		//result.getDefaultSaveOptions().put(XMLResource.OPTION_XML_MAP, xmlMap);
		//result.getDefaultLoadOptions().put(XMLResource.OPTION_XML_MAP, xmlMap);
		return result;
	}
} //WsdlResourceFactoryImpl
