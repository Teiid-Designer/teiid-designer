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

package com.metamatrix.metamodels.core;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.core.CorePackage
 * @generated
 */
public interface CoreFactory extends EFactory{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     */
    CoreFactory eINSTANCE = new com.metamatrix.metamodels.core.impl.CoreFactoryImpl();

    /**
     * Returns a new object of class '<em>Annotation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Annotation</em>'.
     * @generated
     */
    Annotation createAnnotation();

    /**
     * Returns a new object of class '<em>Annotation Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Annotation Container</em>'.
     * @generated
     */
    AnnotationContainer createAnnotationContainer();

    /**
     * Returns a new object of class '<em>Model Annotation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Model Annotation</em>'.
     * @generated
     */
    ModelAnnotation createModelAnnotation();

    /**
     * Returns a new object of class '<em>Link</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Link</em>'.
     * @generated
     */
    Link createLink();

    /**
     * Returns a new object of class '<em>Link Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Link Container</em>'.
     * @generated
     */
    LinkContainer createLinkContainer();

    /**
     * Returns a new object of class '<em>Model Import</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Model Import</em>'.
     * @generated
     */
    ModelImport createModelImport();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    CorePackage getCorePackage();

} //CoreFactory
