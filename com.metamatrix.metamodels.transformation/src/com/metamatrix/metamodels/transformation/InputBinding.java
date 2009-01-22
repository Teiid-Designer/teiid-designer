/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Input Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.InputBinding#getMappingClassSet <em>Mapping Class Set</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.InputBinding#getInputParameter <em>Input Parameter</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.InputBinding#getMappingClassColumn <em>Mapping Class Column</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getInputBinding()
 * @model
 * @generated
 */
public interface InputBinding extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Mapping Class Set</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.MappingClassSet#getInputBinding <em>Input Binding</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping Class Set</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mapping Class Set</em>' container reference.
     * @see #setMappingClassSet(MappingClassSet)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getInputBinding_MappingClassSet()
     * @see com.metamatrix.metamodels.transformation.MappingClassSet#getInputBinding
     * @model opposite="inputBinding" required="true"
     * @generated
     */
    MappingClassSet getMappingClassSet();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.InputBinding#getMappingClassSet <em>Mapping Class Set</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mapping Class Set</em>' container reference.
     * @see #getMappingClassSet()
     * @generated
     */
    void setMappingClassSet(MappingClassSet value);

    /**
     * Returns the value of the '<em><b>Input Parameter</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Parameter</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Parameter</em>' reference.
     * @see #setInputParameter(InputParameter)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getInputBinding_InputParameter()
     * @model required="true"
     * @generated
     */
    InputParameter getInputParameter();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.InputBinding#getInputParameter <em>Input Parameter</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Input Parameter</em>' reference.
     * @see #getInputParameter()
     * @generated
     */
    void setInputParameter(InputParameter value);

    /**
     * Returns the value of the '<em><b>Mapping Class Column</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping Class Column</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mapping Class Column</em>' reference.
     * @see #setMappingClassColumn(MappingClassColumn)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getInputBinding_MappingClassColumn()
     * @model required="true"
     * @generated
     */
    MappingClassColumn getMappingClassColumn();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.InputBinding#getMappingClassColumn <em>Mapping Class Column</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mapping Class Column</em>' reference.
     * @see #getMappingClassColumn()
     * @generated
     */
    void setMappingClassColumn(MappingClassColumn value);

} // InputBinding
