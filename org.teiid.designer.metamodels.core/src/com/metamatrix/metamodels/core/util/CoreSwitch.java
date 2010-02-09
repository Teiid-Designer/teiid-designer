/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.util;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.Datatype;
import com.metamatrix.metamodels.core.Identifiable;
import com.metamatrix.metamodels.core.Link;
import com.metamatrix.metamodels.core.LinkContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call {@link #doSwitch
 * doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model, starting with the actual class of the
 * object and proceeding up the inheritance hierarchy until a non-null result is returned, which is the result of the switch. <!--
 * end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.core.CorePackage
 * @generated
 */
public class CoreSwitch {

    /**
     * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected static CorePackage modelPackage;

    /**
     * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public CoreSwitch() {
        if (modelPackage == null) {
            modelPackage = CorePackage.eINSTANCE;
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
            case CorePackage.ANNOTATION: {
                Annotation annotation = (Annotation)theEObject;
                Object result = caseAnnotation(annotation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case CorePackage.ANNOTATION_CONTAINER: {
                AnnotationContainer annotationContainer = (AnnotationContainer)theEObject;
                Object result = caseAnnotationContainer(annotationContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case CorePackage.MODEL_ANNOTATION: {
                ModelAnnotation modelAnnotation = (ModelAnnotation)theEObject;
                Object result = caseModelAnnotation(modelAnnotation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case CorePackage.LINK: {
                Link link = (Link)theEObject;
                Object result = caseLink(link);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case CorePackage.LINK_CONTAINER: {
                LinkContainer linkContainer = (LinkContainer)theEObject;
                Object result = caseLinkContainer(linkContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case CorePackage.DATATYPE: {
                Datatype datatype = (Datatype)theEObject;
                Object result = caseDatatype(datatype);
                if (result == null) result = caseIdentifiable(datatype);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case CorePackage.IDENTIFIABLE: {
                Identifiable identifiable = (Identifiable)theEObject;
                Object result = caseIdentifiable(identifiable);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case CorePackage.MODEL_IMPORT: {
                ModelImport modelImport = (ModelImport)theEObject;
                Object result = caseModelImport(modelImport);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default:
                return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Annotation</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Annotation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseAnnotation( Annotation object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Annotation Container</em>'. <!-- begin-user-doc -->
     * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Annotation Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseAnnotationContainer( AnnotationContainer object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Model Annotation</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Model Annotation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseModelAnnotation( ModelAnnotation object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Link</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Link</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseLink( Link object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Link Container</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Link Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseLinkContainer( LinkContainer object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Datatype</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Datatype</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseDatatype( Datatype object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Identifiable</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Identifiable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIdentifiable( Identifiable object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Model Import</em>'. <!-- begin-user-doc --> This
     * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
     * 
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Model Import</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseModelImport( ModelImport object ) {
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

} // CoreSwitch
