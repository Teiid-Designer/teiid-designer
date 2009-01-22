/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.wsdl.soap.SoapAddress;
import com.metamatrix.metamodels.wsdl.soap.SoapBinding;
import com.metamatrix.metamodels.wsdl.soap.SoapBody;
import com.metamatrix.metamodels.wsdl.soap.SoapFault;
import com.metamatrix.metamodels.wsdl.soap.SoapHeader;
import com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault;
import com.metamatrix.metamodels.wsdl.soap.SoapOperation;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage
 * @generated
 */
public class SoapSwitch {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The cached model package
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected static SoapPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapSwitch() {
        if (modelPackage == null) {
            modelPackage = SoapPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
	public Object doSwitch(EObject theEObject) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(EClass theEClass, EObject theEObject) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        }
        List eSuperTypes = theEClass.getESuperTypes();
        return
            eSuperTypes.isEmpty() ?
                defaultCase(theEObject) :
                doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case SoapPackage.SOAP_ADDRESS: {
                SoapAddress soapAddress = (SoapAddress)theEObject;
                Object result = caseSoapAddress(soapAddress);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case SoapPackage.SOAP_HEADER_FAULT: {
                SoapHeaderFault soapHeaderFault = (SoapHeaderFault)theEObject;
                Object result = caseSoapHeaderFault(soapHeaderFault);
                if (result == null) result = caseSoapFault(soapHeaderFault);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case SoapPackage.SOAP_FAULT: {
                SoapFault soapFault = (SoapFault)theEObject;
                Object result = caseSoapFault(soapFault);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case SoapPackage.SOAP_HEADER: {
                SoapHeader soapHeader = (SoapHeader)theEObject;
                Object result = caseSoapHeader(soapHeader);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case SoapPackage.SOAP_BODY: {
                SoapBody soapBody = (SoapBody)theEObject;
                Object result = caseSoapBody(soapBody);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case SoapPackage.SOAP_OPERATION: {
                SoapOperation soapOperation = (SoapOperation)theEObject;
                Object result = caseSoapOperation(soapOperation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case SoapPackage.SOAP_BINDING: {
                SoapBinding soapBinding = (SoapBinding)theEObject;
                Object result = caseSoapBinding(soapBinding);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Address</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Address</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSoapAddress(SoapAddress object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Header Fault</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Header Fault</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSoapHeaderFault(SoapHeaderFault object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Fault</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Fault</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSoapFault(SoapFault object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Header</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Header</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSoapHeader(SoapHeader object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Body</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Body</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSoapBody(SoapBody object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Operation</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Operation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSoapOperation(SoapOperation object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Binding</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Binding</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSoapBinding(SoapBinding object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
	public Object defaultCase(EObject object) {
        return null;
    }

} //SoapSwitch
