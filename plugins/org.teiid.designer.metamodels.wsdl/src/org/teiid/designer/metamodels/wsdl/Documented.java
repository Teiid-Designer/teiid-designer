/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Documented</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Documented#getDocumentation <em>Documentation</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDocumented()
 * @model interface="true" abstract="true"
 * @generated
 *
 * @since 8.0
 */
public interface Documented extends EObject {

    /**
     * Returns the value of the '<em><b>Documentation</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Documentation#getDocumented <em>Documented</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Documentation</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Documentation</em>' containment reference.
     * @see #setDocumentation(Documentation)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDocumented_Documentation()
     * @see org.teiid.designer.metamodels.wsdl.Documentation#getDocumented
     * @model opposite="documented" containment="true"
     * @generated
     */
    Documentation getDocumentation();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Documented#getDocumentation <em>Documentation</em>}'
     * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Documentation</em>' containment reference.
     * @see #getDocumentation()
     * @generated
     */
    void setDocumentation( Documentation value );

} // Documented
