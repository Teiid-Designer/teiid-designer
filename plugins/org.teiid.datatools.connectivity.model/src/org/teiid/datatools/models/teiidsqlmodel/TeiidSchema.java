/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.models.teiidsqlmodel;

import org.eclipse.datatools.modelbase.sql.schema.Schema;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Teiid Schema</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.datatools.models.teiidsqlmodel.TeiidSchema#getDocuments <em>Documents</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidsqlmodelPackage#getTeiidSchema()
 * @model
 * @generated
 */
public interface TeiidSchema extends Schema {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Teiid Designer (http://www.jboss.org/projects/teiiddesigner.html) is copyright 2000-2010 MetaMatrix, Inc. and Red Hat, Inc.  This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which is available at http://www.eclipse.org/legal/epl-v10.html.  Some portions may be licensed to Red Hat, Inc. under one or more contributor license agreements."; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Documents</b></em>' reference list.
	 * The list contents are of type {@link org.teiid.datatools.models.teiidsqlmodel.Document}.
	 * It is bidirectional and its opposite is '{@link org.teiid.datatools.models.teiidsqlmodel.Document#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Documents</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Documents</em>' reference list.
	 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidsqlmodelPackage#getTeiidSchema_Documents()
	 * @see org.teiid.datatools.models.teiidsqlmodel.Document#getSchema
	 * @model type="org.teiid.datatools.models.teiidsqlmodel.Document" opposite="schema"
	 * @generated
	 */
	EList getDocuments();

} // TeiidSchema
