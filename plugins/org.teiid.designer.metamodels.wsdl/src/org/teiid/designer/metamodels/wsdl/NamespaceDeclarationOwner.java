/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Namespace Declaration Owner</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.NamespaceDeclarationOwner#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getNamespaceDeclarationOwner()
 * @model interface="true" abstract="true"
 * @generated
 *
 * @since 8.0
 */
public interface NamespaceDeclarationOwner extends EObject {

    /**
     * Returns the value of the '<em><b>Declared Namespaces</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.NamespaceDeclaration}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.NamespaceDeclaration#getOwner <em>Owner</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Declared Namespaces</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Declared Namespaces</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getNamespaceDeclarationOwner_DeclaredNamespaces()
     * @see org.teiid.designer.metamodels.wsdl.NamespaceDeclaration#getOwner
     * @model type="org.teiid.designer.metamodels.wsdl.NamespaceDeclaration" opposite="owner" containment="true"
     * @generated
     */
    EList getDeclaredNamespaces();

} // NamespaceDeclarationOwner
