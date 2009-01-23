/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Link Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.LinkContainer#getLinks <em>Links</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.core.CorePackage#getLinkContainer()
 * @model
 * @generated
 */
public interface LinkContainer extends EObject {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Links</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.core.Link}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.core.Link#getLinkContainer <em>Link Container</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Links</em>' containment reference list isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Links</em>' containment reference list.
     * @see com.metamatrix.metamodels.core.CorePackage#getLinkContainer_Links()
     * @see com.metamatrix.metamodels.core.Link#getLinkContainer
     * @model type="com.metamatrix.metamodels.core.Link" opposite="linkContainer" containment="true"
     * @generated
     */
    EList getLinks();

} // LinkContainer
