/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interface</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.webservice.Interface#getOperations <em>Operations</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getInterface()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface Interface extends WebServiceComponent{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Operations</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.webservice.Operation}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.webservice.Operation#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Operations</em>' containment reference list.
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getInterface_Operations()
     * @see org.teiid.designer.metamodels.webservice.Operation#getInterface
     * @model type="org.teiid.designer.metamodels.webservice.Operation" opposite="interface" containment="true"
     * @generated
     */
	EList getOperations();

} // Interface
