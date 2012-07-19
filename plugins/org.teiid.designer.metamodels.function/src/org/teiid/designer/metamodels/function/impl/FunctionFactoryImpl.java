/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.teiid.designer.metamodels.function.FunctionFactory;
import org.teiid.designer.metamodels.function.FunctionPackage;
import org.teiid.designer.metamodels.function.FunctionParameter;
import org.teiid.designer.metamodels.function.PushDownType;
import org.teiid.designer.metamodels.function.ReturnParameter;
import org.teiid.designer.metamodels.function.ScalarFunction;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 *
 * @since 8.0
 */
public class FunctionFactoryImpl extends EFactoryImpl implements FunctionFactory {
    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public FunctionFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EObject create( EClass eClass ) {
        switch (eClass.getClassifierID()) {
            case FunctionPackage.SCALAR_FUNCTION:
                return createScalarFunction();
            case FunctionPackage.FUNCTION_PARAMETER:
                return createFunctionParameter();
            case FunctionPackage.RETURN_PARAMETER:
                return createReturnParameter();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object createFromString( EDataType eDataType,
                                    String initialValue ) {
        switch (eDataType.getClassifierID()) {
            case FunctionPackage.PUSH_DOWN_TYPE: {
                PushDownType result = PushDownType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String convertToString( EDataType eDataType,
                                   Object instanceValue ) {
        switch (eDataType.getClassifierID()) {
            case FunctionPackage.PUSH_DOWN_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ScalarFunction createScalarFunction() {
        ScalarFunctionImpl scalarFunction = new ScalarFunctionImpl();
        return scalarFunction;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public FunctionParameter createFunctionParameter() {
        FunctionParameterImpl functionParameter = new FunctionParameterImpl();
        return functionParameter;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ReturnParameter createReturnParameter() {
        ReturnParameterImpl returnParameter = new ReturnParameterImpl();
        return returnParameter;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public FunctionPackage getFunctionPackage() {
        return (FunctionPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static FunctionPackage getPackage() { // NO_UCD
        return FunctionPackage.eINSTANCE;
    }

} // FunctionFactoryImpl
