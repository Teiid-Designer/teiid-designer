/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.impl;

import java.util.Date;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ModelAccessibility;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.ProblemMarker;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class ManifestFactoryImpl extends EFactoryImpl implements ManifestFactory {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ManifestFactoryImpl() {
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
            case ManifestPackage.VIRTUAL_DATABASE:
                return createVirtualDatabase();
            case ManifestPackage.MODEL_REFERENCE:
                return createModelReference();
            case ManifestPackage.PROBLEM_MARKER:
                return createProblemMarker();
            case ManifestPackage.MODEL_SOURCE:
                return createModelSource();
            case ManifestPackage.MODEL_SOURCE_PROPERTY:
                return createModelSourceProperty();
            case ManifestPackage.WSDL_OPTIONS:
                return createWsdlOptions();
            case ManifestPackage.NON_MODEL_REFERENCE:
                return createNonModelReference();
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
            case ManifestPackage.SEVERITY: {
                Severity result = Severity.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case ManifestPackage.MODEL_ACCESSIBILITY: {
                ModelAccessibility result = ModelAccessibility.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case ManifestPackage.JAVA_DATE:
                return createJavaDateFromString(eDataType, initialValue);
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
            case ManifestPackage.SEVERITY:
                return instanceValue == null ? null : instanceValue.toString();
            case ManifestPackage.MODEL_ACCESSIBILITY:
                return instanceValue == null ? null : instanceValue.toString();
            case ManifestPackage.JAVA_DATE:
                return convertJavaDateToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public VirtualDatabase createVirtualDatabase() {
        VirtualDatabaseImpl virtualDatabase = new VirtualDatabaseImpl();
        return virtualDatabase;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ModelReference createModelReference() {
        ModelReferenceImpl modelReference = new ModelReferenceImpl();
        return modelReference;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ProblemMarker createProblemMarker() {
        ProblemMarkerImpl problemMarker = new ProblemMarkerImpl();
        return problemMarker;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ModelSource createModelSource() {
        ModelSourceImpl modelSource = new ModelSourceImpl();
        return modelSource;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ModelSourceProperty createModelSourceProperty() {
        ModelSourcePropertyImpl modelSourceProperty = new ModelSourcePropertyImpl();
        return modelSourceProperty;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public WsdlOptions createWsdlOptions() {
        WsdlOptionsImpl wsdlOptions = new WsdlOptionsImpl();
        return wsdlOptions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NonModelReference createNonModelReference() {
        NonModelReferenceImpl nonModelReference = new NonModelReferenceImpl();
        return nonModelReference;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Date createJavaDateFromString( EDataType eDataType,
                                          String initialValue ) {
        return (Date)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertJavaDateToString( EDataType eDataType,
                                           Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ManifestPackage getManifestPackage() {
        return (ManifestPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ManifestPackage getPackage() {
        return ManifestPackage.eINSTANCE;
    }

} // ManifestFactoryImpl
