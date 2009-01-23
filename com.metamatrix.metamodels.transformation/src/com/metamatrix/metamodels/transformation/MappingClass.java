/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapping Class</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClass#isRecursive <em>Recursive</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClass#isRecursionAllowed <em>Recursion Allowed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionCriteria <em>Recursion Criteria</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionLimit <em>Recursion Limit</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionLimitErrorMode <em>Recursion Limit Error Mode</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClass#getColumns <em>Columns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClass#getMappingClassSet <em>Mapping Class Set</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClass#getInputSet <em>Input Set</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass()
 * @model
 * @generated
 */
public interface MappingClass extends MappingClassObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Recursive</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Recursive</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Recursive</em>' attribute.
     * @see #setRecursive(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass_Recursive()
     * @model default="false"
     * @generated
     */
    boolean isRecursive();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClass#isRecursive <em>Recursive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Recursive</em>' attribute.
     * @see #isRecursive()
     * @generated
     */
    void setRecursive(boolean value);

    /**
     * Returns the value of the '<em><b>Recursion Allowed</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Recursion Allowed</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Recursion Allowed</em>' attribute.
     * @see #setRecursionAllowed(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass_RecursionAllowed()
     * @model default="false"
     * @generated
     */
    boolean isRecursionAllowed();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClass#isRecursionAllowed <em>Recursion Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Recursion Allowed</em>' attribute.
     * @see #isRecursionAllowed()
     * @generated
     */
    void setRecursionAllowed(boolean value);

    /**
     * Returns the value of the '<em><b>Recursion Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Recursion Criteria</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Recursion Criteria</em>' attribute.
     * @see #setRecursionCriteria(String)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass_RecursionCriteria()
     * @model
     * @generated
     */
    String getRecursionCriteria();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionCriteria <em>Recursion Criteria</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Recursion Criteria</em>' attribute.
     * @see #getRecursionCriteria()
     * @generated
     */
    void setRecursionCriteria(String value);

    /**
     * Returns the value of the '<em><b>Recursion Limit</b></em>' attribute.
     * The default value is <code>"5"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Recursion Limit</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Recursion Limit</em>' attribute.
     * @see #setRecursionLimit(int)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass_RecursionLimit()
     * @model default="5"
     * @generated
     */
    int getRecursionLimit();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionLimit <em>Recursion Limit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Recursion Limit</em>' attribute.
     * @see #getRecursionLimit()
     * @generated
     */
    void setRecursionLimit(int value);

    /**
     * Returns the value of the '<em><b>Recursion Limit Error Mode</b></em>' attribute.
     * The default value is <code>"THROW"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.transformation.RecursionErrorMode}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Recursion Limit Error Mode</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Recursion Limit Error Mode</em>' attribute.
     * @see com.metamatrix.metamodels.transformation.RecursionErrorMode
     * @see #setRecursionLimitErrorMode(RecursionErrorMode)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass_RecursionLimitErrorMode()
     * @model default="THROW"
     * @generated
     */
    RecursionErrorMode getRecursionLimitErrorMode();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionLimitErrorMode <em>Recursion Limit Error Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Recursion Limit Error Mode</em>' attribute.
     * @see com.metamatrix.metamodels.transformation.RecursionErrorMode
     * @see #getRecursionLimitErrorMode()
     * @generated
     */
    void setRecursionLimitErrorMode(RecursionErrorMode value);

    /**
     * Returns the value of the '<em><b>Columns</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.MappingClassColumn}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.MappingClassColumn#getMappingClass <em>Mapping Class</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Columns</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Columns</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass_Columns()
     * @see com.metamatrix.metamodels.transformation.MappingClassColumn#getMappingClass
     * @model type="com.metamatrix.metamodels.transformation.MappingClassColumn" opposite="mappingClass" containment="true" required="true"
     * @generated
     */
    EList getColumns();

    /**
     * Returns the value of the '<em><b>Mapping Class Set</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.MappingClassSet#getMappingClasses <em>Mapping Classes</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping Class Set</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mapping Class Set</em>' container reference.
     * @see #setMappingClassSet(MappingClassSet)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass_MappingClassSet()
     * @see com.metamatrix.metamodels.transformation.MappingClassSet#getMappingClasses
     * @model opposite="mappingClasses"
     * @generated
     */
    MappingClassSet getMappingClassSet();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClass#getMappingClassSet <em>Mapping Class Set</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mapping Class Set</em>' container reference.
     * @see #getMappingClassSet()
     * @generated
     */
    void setMappingClassSet(MappingClassSet value);

    /**
     * Returns the value of the '<em><b>Input Set</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.InputSet#getMappingClass <em>Mapping Class</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Set</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Set</em>' containment reference.
     * @see #setInputSet(InputSet)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClass_InputSet()
     * @see com.metamatrix.metamodels.transformation.InputSet#getMappingClass
     * @model opposite="mappingClass" containment="true"
     * @generated
     */
    InputSet getInputSet();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClass#getInputSet <em>Input Set</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Input Set</em>' containment reference.
     * @see #getInputSet()
     * @generated
     */
    void setInputSet(InputSet value);

} // MappingClass
