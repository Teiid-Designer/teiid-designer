/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>XClass</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.extension.XClass#getExtendedClass <em>Extended Class</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.core.extension.ExtensionPackage#getXClass()
 * @model
 * @generated
 */
public interface XClass extends EClass {

    /**
     * Returns the value of the '<em><b>Extended Class</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Extended Class</em>' reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Extended Class</em>' reference.
     * @see #setExtendedClass(EClass)
     * @see com.metamatrix.metamodels.core.extension.ExtensionPackage#getXClass_ExtendedClass()
     * @model
     * @generated
     */
    EClass getExtendedClass();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.extension.XClass#getExtendedClass <em>Extended Class</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Extended Class</em>' reference.
     * @see #getExtendedClass()
     * @generated
     */
    void setExtendedClass( EClass value );

} // XClass
