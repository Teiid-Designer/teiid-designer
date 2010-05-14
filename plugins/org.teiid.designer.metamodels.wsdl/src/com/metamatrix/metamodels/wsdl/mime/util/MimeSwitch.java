/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime.util;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.wsdl.mime.MimeContent;
import com.metamatrix.metamodels.wsdl.mime.MimeElement;
import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;
import com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.mime.MimePart;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call {@link #doSwitch
 * doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model, starting with the actual class of the
 * object and proceeding up the inheritance hierarchy until a non-null result is returned, which is the result of the switch. <!--
 * end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.wsdl.mime.MimePackage
 * @generated
 */
public class MimeSwitch { // NO_UCD

    /**
     * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected static MimePackage modelPackage;

    /**
     * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MimeSwitch() {
        if (modelPackage == null) {
            modelPackage = MimePackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch( EObject theEObject ) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch( EClass theEClass,
                               EObject theEObject ) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        }
        List eSuperTypes = theEClass.getESuperTypes();
        return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch( int classifierID,
                               EObject theEObject ) {
        switch (classifierID) {
            case MimePackage.MIME_CONTENT: {
                MimeContent mimeContent = (MimeContent)theEObject;
                Object result = caseMimeContent(mimeContent);
                if (result == null) result = caseMimeElement(mimeContent);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case MimePackage.MIME_MULTIPART_RELATED: {
                MimeMultipartRelated mimeMultipartRelated = (MimeMultipartRelated)theEObject;
                Object result = caseMimeMultipartRelated(mimeMultipartRelated);
                if (result == null) result = caseMimeElement(mimeMultipartRelated);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case MimePackage.MIME_PART: {
                MimePart mimePart = (MimePart)theEObject;
                Object result = caseMimePart(mimePart);
                if (result == null) result = caseMimeElementOwner(mimePart);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case MimePackage.MIME_ELEMENT_OWNER: {
                MimeElementOwner mimeElementOwner = (MimeElementOwner)theEObject;
                Object result = caseMimeElementOwner(mimeElementOwner);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case MimePackage.MIME_ELEMENT: {
                MimeElement mimeElement = (MimeElement)theEObject;
                Object result = caseMimeElement(mimeElement);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default:
                return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Content</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Content</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMimeContent( MimeContent object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Multipart Related</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Multipart Related</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMimeMultipartRelated( MimeMultipartRelated object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Part</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Part</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMimePart( MimePart object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Element Owner</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Element Owner</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMimeElementOwner( MimeElementOwner object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Element</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMimeElement( MimeElement object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch, but this is the last case anyway. <!--
     * end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase( EObject object ) {
        return null;
    }

} // MimeSwitch
