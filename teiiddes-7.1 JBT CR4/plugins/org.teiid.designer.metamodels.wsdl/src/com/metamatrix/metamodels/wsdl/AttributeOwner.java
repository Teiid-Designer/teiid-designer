/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Attribute Owner</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.AttributeOwner#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getAttributeOwner()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface AttributeOwner extends NamespaceDeclarationOwner {

    /**
     * Returns the value of the '<em><b>Attributes</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.wsdl.Attribute}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.wsdl.Attribute#getAttributeOwner <em>Attribute Owner</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Attributes</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getAttributeOwner_Attributes()
     * @see com.metamatrix.metamodels.wsdl.Attribute#getAttributeOwner
     * @model type="com.metamatrix.metamodels.wsdl.Attribute" opposite="attributeOwner" containment="true"
     * @generated
     */
    EList getAttributes();

} // AttributeOwner
