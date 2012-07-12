/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Element Owner</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.ElementOwner#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getElementOwner()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ElementOwner extends NamespaceDeclarationOwner, Documented {

    /**
     * Returns the value of the '<em><b>Elements</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.Element}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Element#getElementOwner <em>Element Owner</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Elements</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getElementOwner_Elements()
     * @see org.teiid.designer.metamodels.wsdl.Element#getElementOwner
     * @model type="org.teiid.designer.metamodels.wsdl.Element" opposite="elementOwner" containment="true"
     * @generated
     */
    EList getElements();

} // ElementOwner
