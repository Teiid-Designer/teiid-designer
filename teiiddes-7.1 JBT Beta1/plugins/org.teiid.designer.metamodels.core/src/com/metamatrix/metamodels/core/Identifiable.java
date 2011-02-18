/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Identifiable</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.Identifiable#getUuid <em>Uuid</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.core.CorePackage#getIdentifiable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Identifiable extends EObject {

    /**
     * Returns the value of the '<em><b>Uuid</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Uuid</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Uuid</em>' attribute.
     * @see #setUuid(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getIdentifiable_Uuid()
     * @model
     * @generated
     */
    String getUuid();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.Identifiable#getUuid <em>Uuid</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Uuid</em>' attribute.
     * @see #getUuid()
     * @generated
     */
    void setUuid( String value ); // NO_UCD

} // Identifiable
