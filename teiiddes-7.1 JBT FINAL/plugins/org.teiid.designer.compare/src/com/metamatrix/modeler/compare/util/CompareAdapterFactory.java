/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.MappingHelper;
import com.metamatrix.modeler.compare.ComparePackage;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.PropertyDifference;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.modeler.compare.ComparePackage
 * @generated
 */
public class CompareAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ComparePackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CompareAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ComparePackage.eINSTANCE;
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
    protected CompareSwitch modelSwitch =
        new CompareSwitch() {
            @Override
            public Object caseDifferenceDescriptor(DifferenceDescriptor object) {
                return createDifferenceDescriptorAdapter();
            }
            @Override
            public Object caseDifferenceReport(DifferenceReport object) {
                return createDifferenceReportAdapter();
            }
            @Override
            public Object casePropertyDifference(PropertyDifference object) {
                return createPropertyDifferenceAdapter();
            }
            @Override
            public Object caseMappingHelper(MappingHelper object) {
                return createMappingHelperAdapter();
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
     * Creates a new adapter for an object of class '{@link com.metamatrix.modeler.compare.DifferenceDescriptor <em>Difference Descriptor</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.modeler.compare.DifferenceDescriptor
     * @generated
     */
    public Adapter createDifferenceDescriptorAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.modeler.compare.DifferenceReport <em>Difference Report</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.modeler.compare.DifferenceReport
     * @generated
     */
    public Adapter createDifferenceReportAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.modeler.compare.PropertyDifference <em>Property Difference</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.modeler.compare.PropertyDifference
     * @generated
     */
    public Adapter createPropertyDifferenceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.mapping.MappingHelper <em>Helper</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.mapping.MappingHelper
     * @generated
     */
    public Adapter createMappingHelperAdapter() {
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

} //CompareAdapterFactory
