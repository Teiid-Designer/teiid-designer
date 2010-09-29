/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.datatools.models.teiidsqlmodel;

import org.eclipse.datatools.modelbase.sql.tables.ViewTable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.datatools.models.teiidsqlmodel.Document#getSchema <em>Schema</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidsqlmodelPackage#getDocument()
 * @model
 * @generated
 */
public interface Document extends ViewTable {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Teiid Designer (http://www.jboss.org/projects/teiiddesigner.html) is copyright 2000-2010 MetaMatrix, Inc. and Red Hat, Inc.  This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which is available at http://www.eclipse.org/legal/epl-v10.html.  Some portions may be licensed to Red Hat, Inc. under one or more contributor license agreements."; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.teiid.datatools.models.teiidsqlmodel.TeiidSchema#getDocuments <em>Documents</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Schema</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' reference.
	 * @see #setSchema(TeiidSchema)
	 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidsqlmodelPackage#getDocument_Schema()
	 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidSchema#getDocuments
	 * @model opposite="Documents" required="true"
	 * @generated
	 */
	TeiidSchema getSchema();

	/**
	 * Sets the value of the '{@link org.teiid.datatools.models.teiidsqlmodel.Document#getSchema <em>Schema</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' reference.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(TeiidSchema value);

} // Document
