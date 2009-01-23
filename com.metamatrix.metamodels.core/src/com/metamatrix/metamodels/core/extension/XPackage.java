/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>XPackage</b></em>'. <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.core.extension.ExtensionPackage#getXPackage()
 * @model
 * @generated
 */
public interface XPackage extends EPackage {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @model
     * @generated
     */
    XClass findXClass( EClass extendedClass );

} // XPackage
