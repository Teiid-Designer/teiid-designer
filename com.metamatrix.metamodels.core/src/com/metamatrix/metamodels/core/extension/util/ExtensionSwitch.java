/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension.util;

import java.util.List;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XEnum;
import com.metamatrix.metamodels.core.extension.XEnumLiteral;
import com.metamatrix.metamodels.core.extension.XPackage;

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
 * @see com.metamatrix.metamodels.core.extension.ExtensionPackage
 * @generated
 */
public class ExtensionSwitch {
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
    protected static ExtensionPackage modelPackage;

    /**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public ExtensionSwitch() {
		if (modelPackage == null) {
			modelPackage = ExtensionPackage.eINSTANCE;
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
			case ExtensionPackage.XCLASS: {
				XClass xClass = (XClass)theEObject;
				Object result = caseXClass(xClass);
				if (result == null) result = caseEClass(xClass);
				if (result == null) result = caseEClassifier(xClass);
				if (result == null) result = caseENamedElement(xClass);
				if (result == null) result = caseEModelElement(xClass);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ExtensionPackage.XPACKAGE: {
				XPackage xPackage = (XPackage)theEObject;
				Object result = caseXPackage(xPackage);
				if (result == null) result = caseEPackage(xPackage);
				if (result == null) result = caseENamedElement(xPackage);
				if (result == null) result = caseEModelElement(xPackage);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ExtensionPackage.XATTRIBUTE: {
				XAttribute xAttribute = (XAttribute)theEObject;
				Object result = caseXAttribute(xAttribute);
				if (result == null) result = caseEAttribute(xAttribute);
				if (result == null) result = caseEStructuralFeature(xAttribute);
				if (result == null) result = caseETypedElement(xAttribute);
				if (result == null) result = caseENamedElement(xAttribute);
				if (result == null) result = caseEModelElement(xAttribute);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ExtensionPackage.XENUM: {
				XEnum xEnum = (XEnum)theEObject;
				Object result = caseXEnum(xEnum);
				if (result == null) result = caseEEnum(xEnum);
				if (result == null) result = caseEDataType(xEnum);
				if (result == null) result = caseEClassifier(xEnum);
				if (result == null) result = caseENamedElement(xEnum);
				if (result == null) result = caseEModelElement(xEnum);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ExtensionPackage.XENUM_LITERAL: {
				XEnumLiteral xEnumLiteral = (XEnumLiteral)theEObject;
				Object result = caseXEnumLiteral(xEnumLiteral);
				if (result == null) result = caseEEnumLiteral(xEnumLiteral);
				if (result == null) result = caseENamedElement(xEnumLiteral);
				if (result == null) result = caseEModelElement(xEnumLiteral);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>XClass</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>XClass</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseXClass(XClass object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>XPackage</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>XPackage</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseXPackage(XPackage object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>XAttribute</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>XAttribute</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseXAttribute(XAttribute object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>XEnum</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>XEnum</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseXEnum(XEnum object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>XEnum Literal</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>XEnum Literal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseXEnumLiteral(XEnumLiteral object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EModel Element</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EModel Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEModelElement(EModelElement object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>ENamed Element</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>ENamed Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseENamedElement(ENamedElement object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>ETyped Element</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>ETyped Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseETypedElement(ETypedElement object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EStructural Feature</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EStructural Feature</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEStructuralFeature(EStructuralFeature object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EAttribute</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EAttribute</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEAttribute(EAttribute object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EClassifier</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EClassifier</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEClassifier(EClassifier object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EClass</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EClass</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEClass(EClass object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EPackage</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EPackage</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEPackage(EPackage object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EData Type</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EData Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEDataType(EDataType object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EEnum</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EEnum</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEEnum(EEnum object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EEnum Literal</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EEnum Literal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public Object caseEEnumLiteral(EEnumLiteral object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
    public Object defaultCase(EObject object) {
		return null;
	}

} //ExtensionSwitch
