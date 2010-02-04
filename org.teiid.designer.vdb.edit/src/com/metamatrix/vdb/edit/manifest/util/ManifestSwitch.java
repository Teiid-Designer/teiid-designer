/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.util;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.ProblemMarker;
import com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;

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
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage
 * @generated
 */
public class ManifestSwitch {
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
    protected static ManifestPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ManifestSwitch() {
        if (modelPackage == null) {
            modelPackage = ManifestPackage.eINSTANCE;
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
            case ManifestPackage.VIRTUAL_DATABASE: {
                VirtualDatabase virtualDatabase = (VirtualDatabase)theEObject;
                Object result = caseVirtualDatabase(virtualDatabase);
                if (result == null) result = caseProblemMarkerContainer(virtualDatabase);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ManifestPackage.MODEL_REFERENCE: {
                ModelReference modelReference = (ModelReference)theEObject;
                Object result = caseModelReference(modelReference);
                if (result == null) result = caseModelImport(modelReference);
                if (result == null) result = caseProblemMarkerContainer(modelReference);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ManifestPackage.PROBLEM_MARKER_CONTAINER: {
                ProblemMarkerContainer problemMarkerContainer = (ProblemMarkerContainer)theEObject;
                Object result = caseProblemMarkerContainer(problemMarkerContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ManifestPackage.PROBLEM_MARKER: {
                ProblemMarker problemMarker = (ProblemMarker)theEObject;
                Object result = caseProblemMarker(problemMarker);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ManifestPackage.MODEL_SOURCE: {
                ModelSource modelSource = (ModelSource)theEObject;
                Object result = caseModelSource(modelSource);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ManifestPackage.MODEL_SOURCE_PROPERTY: {
                ModelSourceProperty modelSourceProperty = (ModelSourceProperty)theEObject;
                Object result = caseModelSourceProperty(modelSourceProperty);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ManifestPackage.WSDL_OPTIONS: {
                WsdlOptions wsdlOptions = (WsdlOptions)theEObject;
                Object result = caseWsdlOptions(wsdlOptions);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ManifestPackage.NON_MODEL_REFERENCE: {
                NonModelReference nonModelReference = (NonModelReference)theEObject;
                Object result = caseNonModelReference(nonModelReference);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Virtual Database</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Virtual Database</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseVirtualDatabase(VirtualDatabase object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Model Reference</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Model Reference</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseModelReference(ModelReference object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Problem Marker Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Problem Marker Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProblemMarkerContainer(ProblemMarkerContainer object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Problem Marker</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Problem Marker</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProblemMarker(ProblemMarker object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Model Source</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Model Source</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseModelSource(ModelSource object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Model Source Property</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Model Source Property</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseModelSourceProperty(ModelSourceProperty object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Wsdl Options</em>'.
     * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Wsdl Options</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
	public Object caseWsdlOptions(WsdlOptions object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Non Model Reference</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Non Model Reference</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseNonModelReference(NonModelReference object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Model Import</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Model Import</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseModelImport(ModelImport object) {
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

} //ManifestSwitch
