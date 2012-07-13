/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.core.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.metamodels.core.CoreFactory;
import org.teiid.designer.metamodels.core.CorePackage;
import org.teiid.designer.metamodels.core.Link;
import org.teiid.designer.metamodels.core.LinkContainer;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.core.ModelType;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class CoreFactoryImpl extends EFactoryImpl implements CoreFactory {

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public CoreFactoryImpl() {
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
            case CorePackage.ANNOTATION:
                return createAnnotation();
            case CorePackage.ANNOTATION_CONTAINER:
                return createAnnotationContainer();
            case CorePackage.MODEL_ANNOTATION:
                return createModelAnnotation();
            case CorePackage.LINK:
                return createLink();
            case CorePackage.LINK_CONTAINER:
                return createLinkContainer();
            case CorePackage.MODEL_IMPORT:
                return createModelImport();
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
            case CorePackage.MODEL_TYPE: {
                ModelType result = ModelType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
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
            case CorePackage.MODEL_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
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
	public Annotation createAnnotation() {
        AnnotationImpl annotation = new AnnotationImpl();
        return annotation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public AnnotationContainer createAnnotationContainer() {
        AnnotationContainerImpl annotationContainer = new AnnotationContainerImpl();
        return annotationContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ModelAnnotation createModelAnnotation() {
        ModelAnnotationImpl modelAnnotation = new ModelAnnotationImpl();
        return modelAnnotation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Link createLink() {
        LinkImpl link = new LinkImpl();
        return link;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public LinkContainer createLinkContainer() {
        LinkContainerImpl linkContainer = new LinkContainerImpl();
        return linkContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ModelImport createModelImport() {
        ModelImportImpl modelImport = new ModelImportImpl();
        return modelImport;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public CorePackage getCorePackage() {
        return (CorePackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static CorePackage getPackage() { // NO_UCD
        return CorePackage.eINSTANCE;
    }

} // CoreFactoryImpl
