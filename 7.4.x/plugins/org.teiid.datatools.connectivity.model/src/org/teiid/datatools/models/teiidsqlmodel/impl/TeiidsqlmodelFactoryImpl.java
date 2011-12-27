/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.models.teiidsqlmodel.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.teiid.datatools.models.teiidsqlmodel.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TeiidsqlmodelFactoryImpl extends EFactoryImpl implements TeiidsqlmodelFactory {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Teiid Designer (http://www.jboss.org/projects/teiiddesigner.html) is copyright 2000-2010 MetaMatrix, Inc. and Red Hat, Inc.  This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which is available at http://www.eclipse.org/legal/epl-v10.html.  Some portions may be licensed to Red Hat, Inc. under one or more contributor license agreements."; //$NON-NLS-1$

	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static TeiidsqlmodelFactory init() {
		try {
			TeiidsqlmodelFactory theTeiidsqlmodelFactory = (TeiidsqlmodelFactory)EPackage.Registry.INSTANCE.getEFactory("http:///org/teiid/datatools/connectivity/teiidsqlmodel.ecore");  //$NON-NLS-1$
			if (theTeiidsqlmodelFactory != null) {
				return theTeiidsqlmodelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new TeiidsqlmodelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TeiidsqlmodelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case TeiidsqlmodelPackage.DOCUMENT: return createDocument();
			case TeiidsqlmodelPackage.TEIID_SCHEMA: return createTeiidSchema();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TeiidSchema createTeiidSchema() {
		TeiidSchemaImpl teiidSchema = new TeiidSchemaImpl();
		return teiidSchema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Document createDocument() {
		DocumentImpl document = new DocumentImpl();
		return document;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TeiidsqlmodelPackage getTeiidsqlmodelPackage() {
		return (TeiidsqlmodelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static TeiidsqlmodelPackage getPackage() {
		return TeiidsqlmodelPackage.eINSTANCE;
	}

} //TeiidsqlmodelFactoryImpl
