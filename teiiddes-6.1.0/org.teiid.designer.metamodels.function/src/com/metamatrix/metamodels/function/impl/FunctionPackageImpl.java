/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import com.metamatrix.metamodels.function.Function;
import com.metamatrix.metamodels.function.FunctionFactory;
import com.metamatrix.metamodels.function.FunctionPackage;
import com.metamatrix.metamodels.function.FunctionParameter;
import com.metamatrix.metamodels.function.PushDownType;
import com.metamatrix.metamodels.function.ReturnParameter;
import com.metamatrix.metamodels.function.ScalarFunction;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class FunctionPackageImpl extends EPackageImpl implements FunctionPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass functionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass scalarFunctionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass functionParameterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass returnParameterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum pushDownTypeEEnum = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.function.FunctionPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private FunctionPackageImpl() {
        super(eNS_URI, FunctionFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static FunctionPackage init() {
        if (isInited) return (FunctionPackage)EPackage.Registry.INSTANCE.getEPackage(FunctionPackage.eNS_URI);

        // Obtain or create and register package
        FunctionPackageImpl theFunctionPackage = (FunctionPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof FunctionPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new FunctionPackageImpl());

        isInited = true;

        // Create package meta-data objects
        theFunctionPackage.createPackageContents();

        // Initialize created meta-data
        theFunctionPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theFunctionPackage.freeze();

        return theFunctionPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getFunction() {
        return functionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFunction_Name() {
        return (EAttribute)functionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFunction_Category() {
        return (EAttribute)functionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFunction_PushDown() {
        return (EAttribute)functionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getScalarFunction() {
        return scalarFunctionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getScalarFunction_InvocationClass() {
        return (EAttribute)scalarFunctionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getScalarFunction_InvocationMethod() {
        return (EAttribute)scalarFunctionEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getScalarFunction_Deterministic() {
        return (EAttribute)scalarFunctionEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getScalarFunction_InputParameters() {
        return (EReference)scalarFunctionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getScalarFunction_ReturnParameter() {
        return (EReference)scalarFunctionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getFunctionParameter() {
        return functionParameterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFunctionParameter_Name() {
        return (EAttribute)functionParameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFunctionParameter_Type() {
        return (EAttribute)functionParameterEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getFunctionParameter_Function() {
        return (EReference)functionParameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getReturnParameter() {
        return returnParameterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getReturnParameter_Type() {
        return (EAttribute)returnParameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getReturnParameter_Function() {
        return (EReference)returnParameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getPushDownType() {
        return pushDownTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public FunctionFactory getFunctionFactory() {
        return (FunctionFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        functionEClass = createEClass(FUNCTION);
        createEAttribute(functionEClass, FUNCTION__NAME);
        createEAttribute(functionEClass, FUNCTION__CATEGORY);
        createEAttribute(functionEClass, FUNCTION__PUSH_DOWN);

        scalarFunctionEClass = createEClass(SCALAR_FUNCTION);
        createEReference(scalarFunctionEClass, SCALAR_FUNCTION__INPUT_PARAMETERS);
        createEReference(scalarFunctionEClass, SCALAR_FUNCTION__RETURN_PARAMETER);
        createEAttribute(scalarFunctionEClass, SCALAR_FUNCTION__INVOCATION_CLASS);
        createEAttribute(scalarFunctionEClass, SCALAR_FUNCTION__INVOCATION_METHOD);
        createEAttribute(scalarFunctionEClass, SCALAR_FUNCTION__DETERMINISTIC);

        functionParameterEClass = createEClass(FUNCTION_PARAMETER);
        createEReference(functionParameterEClass, FUNCTION_PARAMETER__FUNCTION);
        createEAttribute(functionParameterEClass, FUNCTION_PARAMETER__NAME);
        createEAttribute(functionParameterEClass, FUNCTION_PARAMETER__TYPE);

        returnParameterEClass = createEClass(RETURN_PARAMETER);
        createEReference(returnParameterEClass, RETURN_PARAMETER__FUNCTION);
        createEAttribute(returnParameterEClass, RETURN_PARAMETER__TYPE);

        // Create enums
        pushDownTypeEEnum = createEEnum(PUSH_DOWN_TYPE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Add supertypes to classes
        scalarFunctionEClass.getESuperTypes().add(this.getFunction());

        // Initialize classes and features; add operations and parameters
        initEClass(functionEClass, Function.class, "Function", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getFunction_Name(), ecorePackage.getEString(), "name", null, 0, 1, Function.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getFunction_Category(), ecorePackage.getEString(), "category", null, 0, 1, Function.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getFunction_PushDown(), this.getPushDownType(), "pushDown", "ALLOWED", 0, 1, Function.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass(scalarFunctionEClass, ScalarFunction.class, "ScalarFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getScalarFunction_InputParameters(), this.getFunctionParameter(), this.getFunctionParameter_Function(), "inputParameters", null, 0, -1, ScalarFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getScalarFunction_ReturnParameter(), this.getReturnParameter(), this.getReturnParameter_Function(), "returnParameter", null, 1, 1, ScalarFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getScalarFunction_InvocationClass(), ecorePackage.getEString(), "invocationClass", null, 0, 1, ScalarFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getScalarFunction_InvocationMethod(), ecorePackage.getEString(), "invocationMethod", null, 0, 1, ScalarFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getScalarFunction_Deterministic(), ecorePackage.getEBoolean(), "deterministic", "true", 0, 1, ScalarFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass(functionParameterEClass, FunctionParameter.class, "FunctionParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getFunctionParameter_Function(), this.getScalarFunction(), this.getScalarFunction_InputParameters(), "function", null, 1, 1, FunctionParameter.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getFunctionParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, FunctionParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getFunctionParameter_Type(), ecorePackage.getEString(), "type", null, 0, 1, FunctionParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(returnParameterEClass, ReturnParameter.class, "ReturnParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getReturnParameter_Function(), this.getScalarFunction(), this.getScalarFunction_ReturnParameter(), "function", null, 1, 1, ReturnParameter.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getReturnParameter_Type(), ecorePackage.getEString(), "type", null, 0, 1, ReturnParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(pushDownTypeEEnum, PushDownType.class, "PushDownType"); //$NON-NLS-1$
        addEEnumLiteral(pushDownTypeEEnum, PushDownType.REQUIRED_LITERAL);
        addEEnumLiteral(pushDownTypeEEnum, PushDownType.ALLOWED_LITERAL);
        addEEnumLiteral(pushDownTypeEEnum, PushDownType.NOT_ALLOWED_LITERAL);

        // Create resource
        createResource(eNS_URI);
    }

} //FunctionPackageImpl
