/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.util;

import com.metamatrix.metamodels.wsdl.soap.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage
 * @generated
 */
public class SoapAdapterFactory extends AdapterFactoryImpl {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached model package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected static SoapPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = SoapPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
	@Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SoapSwitch modelSwitch =
        new SoapSwitch() {
            @Override
            public Object caseSoapAddress(SoapAddress object) {
                return createSoapAddressAdapter();
            }
            @Override
            public Object caseSoapHeaderFault(SoapHeaderFault object) {
                return createSoapHeaderFaultAdapter();
            }
            @Override
            public Object caseSoapFault(SoapFault object) {
                return createSoapFaultAdapter();
            }
            @Override
            public Object caseSoapHeader(SoapHeader object) {
                return createSoapHeaderAdapter();
            }
            @Override
            public Object caseSoapBody(SoapBody object) {
                return createSoapBodyAdapter();
            }
            @Override
            public Object caseSoapOperation(SoapOperation object) {
                return createSoapOperationAdapter();
            }
            @Override
            public Object caseSoapBinding(SoapBinding object) {
                return createSoapBindingAdapter();
            }
            @Override
            public Object defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
	@Override
    public Adapter createAdapter(Notifier target) {
        return (Adapter)modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.soap.SoapAddress <em>Address</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapAddress
     * @generated
     */
	public Adapter createSoapAddressAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault <em>Header Fault</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault
     * @generated
     */
	public Adapter createSoapHeaderFaultAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.soap.SoapFault <em>Fault</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapFault
     * @generated
     */
	public Adapter createSoapFaultAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader <em>Header</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader
     * @generated
     */
	public Adapter createSoapHeaderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody <em>Body</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody
     * @generated
     */
	public Adapter createSoapBodyAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.soap.SoapOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapOperation
     * @generated
     */
	public Adapter createSoapOperationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBinding
     * @generated
     */
	public Adapter createSoapBindingAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
	public Adapter createEObjectAdapter() {
        return null;
    }

} //SoapAdapterFactory
