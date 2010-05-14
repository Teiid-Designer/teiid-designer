/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.http;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.wsdl.Binding;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Binding</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getBinding <em>Binding</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getVerb <em>Verb</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpBinding()
 * @model
 * @generated
 */
public interface HttpBinding extends EObject {

    /**
     * Returns the value of the '<em><b>Verb</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Verb</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Verb</em>' attribute.
     * @see #setVerb(String)
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpBinding_Verb()
     * @model
     * @generated
     */
    String getVerb();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getVerb <em>Verb</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Verb</em>' attribute.
     * @see #getVerb()
     * @generated
     */
    void setVerb( String value );

    /**
     * Returns the value of the '<em><b>Binding</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.wsdl.Binding#getHttpBinding <em>Http Binding</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Binding</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Binding</em>' container reference.
     * @see #setBinding(Binding)
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpBinding_Binding()
     * @see com.metamatrix.metamodels.wsdl.Binding#getHttpBinding
     * @model opposite="httpBinding" required="true"
     * @generated
     */
    Binding getBinding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getBinding <em>Binding</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Binding</em>' container reference.
     * @see #getBinding()
     * @generated
     */
    void setBinding( Binding value );

} // HttpBinding
