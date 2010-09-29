/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.models.teiidsqlmodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidsqlmodelPackage
 * @generated
 */
public interface TeiidsqlmodelFactory extends EFactory {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Teiid Designer (http://www.jboss.org/projects/teiiddesigner.html) is copyright 2000-2010 MetaMatrix, Inc. and Red Hat, Inc.  This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which is available at http://www.eclipse.org/legal/epl-v10.html.  Some portions may be licensed to Red Hat, Inc. under one or more contributor license agreements."; //$NON-NLS-1$

	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TeiidsqlmodelFactory eINSTANCE = org.teiid.datatools.models.teiidsqlmodel.impl.TeiidsqlmodelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Teiid Schema</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Teiid Schema</em>'.
	 * @generated
	 */
	TeiidSchema createTeiidSchema();

	/**
	 * Returns a new object of class '<em>Document</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Document</em>'.
	 * @generated
	 */
	Document createDocument();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	TeiidsqlmodelPackage getTeiidsqlmodelPackage();

} //TeiidsqlmodelFactory
