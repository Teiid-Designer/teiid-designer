/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.util;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.SampleFile;
import com.metamatrix.metamodels.webservice.SampleFromXsd;
import com.metamatrix.metamodels.webservice.SampleMessages;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.metamodels.webservice.WebServicePackage;

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
 * @see com.metamatrix.metamodels.webservice.WebServicePackage
 * @generated
 */
public class WebServiceSwitch {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached model package
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected static WebServicePackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public WebServiceSwitch() {
        if (modelPackage == null) {
            modelPackage = WebServicePackage.eINSTANCE;
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
            case WebServicePackage.OPERATION: {
                Operation operation = (Operation)theEObject;
                Object result = caseOperation(operation);
                if (result == null) result = caseWebServiceComponent(operation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WebServicePackage.MESSAGE: {
                Message message = (Message)theEObject;
                Object result = caseMessage(message);
                if (result == null) result = caseWebServiceComponent(message);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WebServicePackage.WEB_SERVICE_COMPONENT: {
                WebServiceComponent webServiceComponent = (WebServiceComponent)theEObject;
                Object result = caseWebServiceComponent(webServiceComponent);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WebServicePackage.INPUT: {
                Input input = (Input)theEObject;
                Object result = caseInput(input);
                if (result == null) result = caseMessage(input);
                if (result == null) result = caseWebServiceComponent(input);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WebServicePackage.OUTPUT: {
                Output output = (Output)theEObject;
                Object result = caseOutput(output);
                if (result == null) result = caseMessage(output);
                if (result == null) result = caseWebServiceComponent(output);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WebServicePackage.INTERFACE: {
                Interface interface_ = (Interface)theEObject;
                Object result = caseInterface(interface_);
                if (result == null) result = caseWebServiceComponent(interface_);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WebServicePackage.SAMPLE_MESSAGES: {
                SampleMessages sampleMessages = (SampleMessages)theEObject;
                Object result = caseSampleMessages(sampleMessages);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WebServicePackage.SAMPLE_FILE: {
                SampleFile sampleFile = (SampleFile)theEObject;
                Object result = caseSampleFile(sampleFile);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case WebServicePackage.SAMPLE_FROM_XSD: {
                SampleFromXsd sampleFromXsd = (SampleFromXsd)theEObject;
                Object result = caseSampleFromXsd(sampleFromXsd);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
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
	public Object caseOperation(Operation object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Message</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Message</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseMessage(Message object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Component</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Component</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseWebServiceComponent(WebServiceComponent object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Input</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Input</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseInput(Input object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Output</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Output</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseOutput(Output object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Interface</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Interface</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseInterface(Interface object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Sample Messages</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Sample Messages</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSampleMessages(SampleMessages object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Sample File</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Sample File</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSampleFile(SampleFile object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Sample From Xsd</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Sample From Xsd</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseSampleFromXsd(SampleFromXsd object) {
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

} //WebServiceSwitch
