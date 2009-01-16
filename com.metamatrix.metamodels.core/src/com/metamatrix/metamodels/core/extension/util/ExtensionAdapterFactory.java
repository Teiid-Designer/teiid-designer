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

package com.metamatrix.metamodels.core.extension.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
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
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.core.extension.ExtensionPackage
 * @generated
 */
public class ExtensionAdapterFactory extends AdapterFactoryImpl {
    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2000-2008 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected static ExtensionPackage modelPackage;

    /**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public ExtensionAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ExtensionPackage.eINSTANCE;
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
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected ExtensionSwitch modelSwitch =
        new ExtensionSwitch() {
			@Override
            public Object caseXClass(XClass object) {
				return createXClassAdapter();
			}
			@Override
            public Object caseXPackage(XPackage object) {
				return createXPackageAdapter();
			}
			@Override
            public Object caseXAttribute(XAttribute object) {
				return createXAttributeAdapter();
			}
			@Override
            public Object caseXEnum(XEnum object) {
				return createXEnumAdapter();
			}
			@Override
            public Object caseXEnumLiteral(XEnumLiteral object) {
				return createXEnumLiteralAdapter();
			}
			@Override
            public Object caseEModelElement(EModelElement object) {
				return createEModelElementAdapter();
			}
			@Override
            public Object caseENamedElement(ENamedElement object) {
				return createENamedElementAdapter();
			}
			@Override
            public Object caseEClassifier(EClassifier object) {
				return createEClassifierAdapter();
			}
			@Override
            public Object caseEClass(EClass object) {
				return createEClassAdapter();
			}
			@Override
            public Object caseEPackage(EPackage object) {
				return createEPackageAdapter();
			}
			@Override
            public Object caseETypedElement(ETypedElement object) {
				return createETypedElementAdapter();
			}
			@Override
            public Object caseEStructuralFeature(EStructuralFeature object) {
				return createEStructuralFeatureAdapter();
			}
			@Override
            public Object caseEAttribute(EAttribute object) {
				return createEAttributeAdapter();
			}
			@Override
            public Object caseEDataType(EDataType object) {
				return createEDataTypeAdapter();
			}
			@Override
            public Object caseEEnum(EEnum object) {
				return createEEnumAdapter();
			}
			@Override
            public Object caseEEnumLiteral(EEnumLiteral object) {
				return createEEnumLiteralAdapter();
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
     * @see org.eclipse.emf.common.notify.impl.AdapterFactoryImpl#adapt(org.eclipse.emf.common.notify.Notifier, java.lang.Object)
     */
    @Override
    public Adapter adapt(Notifier target, Object type) {

        return super.adapt(target, type);
    }



    /**
	 * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.core.extension.XClass <em>XClass</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.metamatrix.metamodels.core.extension.XClass
	 * @generated
	 */
    public Adapter createXClassAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.core.extension.XPackage <em>XPackage</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.metamatrix.metamodels.core.extension.XPackage
	 * @generated
	 */
    public Adapter createXPackageAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.core.extension.XAttribute <em>XAttribute</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.metamatrix.metamodels.core.extension.XAttribute
	 * @generated
	 */
    public Adapter createXAttributeAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.core.extension.XEnum <em>XEnum</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.metamatrix.metamodels.core.extension.XEnum
	 * @generated
	 */
    public Adapter createXEnumAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.core.extension.XEnumLiteral <em>XEnum Literal</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.metamatrix.metamodels.core.extension.XEnumLiteral
	 * @generated
	 */
    public Adapter createXEnumLiteralAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EModelElement <em>EModel Element</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EModelElement
	 * @generated
	 */
    public Adapter createEModelElementAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.ENamedElement <em>ENamed Element</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.ENamedElement
	 * @generated
	 */
    public Adapter createENamedElementAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.ETypedElement <em>ETyped Element</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.ETypedElement
	 * @generated
	 */
    public Adapter createETypedElementAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EStructuralFeature <em>EStructural Feature</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EStructuralFeature
	 * @generated
	 */
    public Adapter createEStructuralFeatureAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EAttribute <em>EAttribute</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EAttribute
	 * @generated
	 */
    public Adapter createEAttributeAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EClassifier <em>EClassifier</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EClassifier
	 * @generated
	 */
    public Adapter createEClassifierAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EClass <em>EClass</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EClass
	 * @generated
	 */
    public Adapter createEClassAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EPackage <em>EPackage</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EPackage
	 * @generated
	 */
    public Adapter createEPackageAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EDataType <em>EData Type</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EDataType
	 * @generated
	 */
    public Adapter createEDataTypeAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EEnum <em>EEnum</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EEnum
	 * @generated
	 */
    public Adapter createEEnumAdapter() {
		return null;
	}

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EEnumLiteral <em>EEnum Literal</em>}'.
	 * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EEnumLiteral
	 * @generated
	 */
    public Adapter createEEnumLiteralAdapter() {
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

} //ExtensionAdapterFactory
