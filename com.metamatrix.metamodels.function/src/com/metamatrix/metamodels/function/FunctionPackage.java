/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.function;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.function.FunctionFactory
 * @generated
 */
public interface FunctionPackage extends EPackage{
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "function"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/MetaMatrixFunction"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "mmfunction"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    FunctionPackage eINSTANCE = com.metamatrix.metamodels.function.impl.FunctionPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.function.impl.FunctionImpl <em>Function</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.function.impl.FunctionImpl
     * @see com.metamatrix.metamodels.function.impl.FunctionPackageImpl#getFunction()
     * @generated
     */
    int FUNCTION = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FUNCTION__NAME = 0;

    /**
     * The feature id for the '<em><b>Category</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FUNCTION__CATEGORY = 1;

    /**
     * The feature id for the '<em><b>Push Down</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FUNCTION__PUSH_DOWN = 2;

    /**
     * The number of structural features of the the '<em>Function</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FUNCTION_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.function.impl.ScalarFunctionImpl <em>Scalar Function</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.function.impl.ScalarFunctionImpl
     * @see com.metamatrix.metamodels.function.impl.FunctionPackageImpl#getScalarFunction()
     * @generated
     */
    int SCALAR_FUNCTION = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION__NAME = FUNCTION__NAME;

    /**
     * The feature id for the '<em><b>Category</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION__CATEGORY = FUNCTION__CATEGORY;

    /**
     * The feature id for the '<em><b>Push Down</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION__PUSH_DOWN = FUNCTION__PUSH_DOWN;

    /**
     * The feature id for the '<em><b>Input Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION__INPUT_PARAMETERS = FUNCTION_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Return Parameter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION__RETURN_PARAMETER = FUNCTION_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Invocation Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION__INVOCATION_CLASS = FUNCTION_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Invocation Method</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION__INVOCATION_METHOD = FUNCTION_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Deterministic</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION__DETERMINISTIC = FUNCTION_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Scalar Function</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCALAR_FUNCTION_FEATURE_COUNT = FUNCTION_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.function.impl.FunctionParameterImpl <em>Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.function.impl.FunctionParameterImpl
     * @see com.metamatrix.metamodels.function.impl.FunctionPackageImpl#getFunctionParameter()
     * @generated
     */
    int FUNCTION_PARAMETER = 2;

    /**
     * The feature id for the '<em><b>Function</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FUNCTION_PARAMETER__FUNCTION = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FUNCTION_PARAMETER__NAME = 1;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FUNCTION_PARAMETER__TYPE = 2;

    /**
     * The number of structural features of the the '<em>Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FUNCTION_PARAMETER_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.function.impl.ReturnParameterImpl <em>Return Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.function.impl.ReturnParameterImpl
     * @see com.metamatrix.metamodels.function.impl.FunctionPackageImpl#getReturnParameter()
     * @generated
     */
    int RETURN_PARAMETER = 3;

    /**
     * The feature id for the '<em><b>Function</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RETURN_PARAMETER__FUNCTION = 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RETURN_PARAMETER__TYPE = 1;

    /**
     * The number of structural features of the the '<em>Return Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RETURN_PARAMETER_FEATURE_COUNT = 2;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.function.PushDownType <em>Push Down Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.function.PushDownType
     * @see com.metamatrix.metamodels.function.impl.FunctionPackageImpl#getPushDownType()
     * @generated
     */
    int PUSH_DOWN_TYPE = 4;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.function.Function <em>Function</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Function</em>'.
     * @see com.metamatrix.metamodels.function.Function
     * @generated
     */
    EClass getFunction();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.Function#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.function.Function#getName()
     * @see #getFunction()
     * @generated
     */
    EAttribute getFunction_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.Function#getCategory <em>Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Category</em>'.
     * @see com.metamatrix.metamodels.function.Function#getCategory()
     * @see #getFunction()
     * @generated
     */
    EAttribute getFunction_Category();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.Function#getPushDown <em>Push Down</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Push Down</em>'.
     * @see com.metamatrix.metamodels.function.Function#getPushDown()
     * @see #getFunction()
     * @generated
     */
    EAttribute getFunction_PushDown();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.function.ScalarFunction <em>Scalar Function</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Scalar Function</em>'.
     * @see com.metamatrix.metamodels.function.ScalarFunction
     * @generated
     */
    EClass getScalarFunction();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.ScalarFunction#getInvocationClass <em>Invocation Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Invocation Class</em>'.
     * @see com.metamatrix.metamodels.function.ScalarFunction#getInvocationClass()
     * @see #getScalarFunction()
     * @generated
     */
    EAttribute getScalarFunction_InvocationClass();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.ScalarFunction#getInvocationMethod <em>Invocation Method</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Invocation Method</em>'.
     * @see com.metamatrix.metamodels.function.ScalarFunction#getInvocationMethod()
     * @see #getScalarFunction()
     * @generated
     */
    EAttribute getScalarFunction_InvocationMethod();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.ScalarFunction#isDeterministic <em>Deterministic</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Deterministic</em>'.
     * @see com.metamatrix.metamodels.function.ScalarFunction#isDeterministic()
     * @see #getScalarFunction()
     * @generated
     */
    EAttribute getScalarFunction_Deterministic();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.function.ScalarFunction#getInputParameters <em>Input Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Input Parameters</em>'.
     * @see com.metamatrix.metamodels.function.ScalarFunction#getInputParameters()
     * @see #getScalarFunction()
     * @generated
     */
    EReference getScalarFunction_InputParameters();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.function.ScalarFunction#getReturnParameter <em>Return Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Return Parameter</em>'.
     * @see com.metamatrix.metamodels.function.ScalarFunction#getReturnParameter()
     * @see #getScalarFunction()
     * @generated
     */
    EReference getScalarFunction_ReturnParameter();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.function.FunctionParameter <em>Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Parameter</em>'.
     * @see com.metamatrix.metamodels.function.FunctionParameter
     * @generated
     */
    EClass getFunctionParameter();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.FunctionParameter#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.function.FunctionParameter#getName()
     * @see #getFunctionParameter()
     * @generated
     */
    EAttribute getFunctionParameter_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.FunctionParameter#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.metamodels.function.FunctionParameter#getType()
     * @see #getFunctionParameter()
     * @generated
     */
    EAttribute getFunctionParameter_Type();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.function.FunctionParameter#getFunction <em>Function</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Function</em>'.
     * @see com.metamatrix.metamodels.function.FunctionParameter#getFunction()
     * @see #getFunctionParameter()
     * @generated
     */
    EReference getFunctionParameter_Function();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.function.ReturnParameter <em>Return Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Return Parameter</em>'.
     * @see com.metamatrix.metamodels.function.ReturnParameter
     * @generated
     */
    EClass getReturnParameter();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.function.ReturnParameter#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.metamodels.function.ReturnParameter#getType()
     * @see #getReturnParameter()
     * @generated
     */
    EAttribute getReturnParameter_Type();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.function.ReturnParameter#getFunction <em>Function</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Function</em>'.
     * @see com.metamatrix.metamodels.function.ReturnParameter#getFunction()
     * @see #getReturnParameter()
     * @generated
     */
    EReference getReturnParameter_Function();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.function.PushDownType <em>Push Down Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Push Down Type</em>'.
     * @see com.metamatrix.metamodels.function.PushDownType
     * @generated
     */
    EEnum getPushDownType();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    FunctionFactory getFunctionFactory();

} //FunctionPackage
