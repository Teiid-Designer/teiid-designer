/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.core.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.metamodels.core.CorePackage;
import org.teiid.designer.metamodels.core.Datatype;
import org.teiid.designer.metamodels.core.Identifiable;
import org.teiid.designer.metamodels.core.Link;
import org.teiid.designer.metamodels.core.LinkContainer;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelImport;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code> method for each
 * class of the model. <!-- end-user-doc -->
 * 
 * @see org.teiid.designer.metamodels.core.CorePackage
 * @generated
 *
 * @since 8.0
 */
public class CoreAdapterFactory extends AdapterFactoryImpl {

    /**
     * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected static CorePackage modelPackage;

    /**
     * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public CoreAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = CorePackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation returns
     * <code>true</code> if the object is either the model's package or is an instance object of the model. <!-- end-user-doc -->
     * 
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType( Object object ) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected CoreSwitch modelSwitch = new CoreSwitch() {
        @Override
        public Object caseAnnotation( Annotation object ) {
            return createAnnotationAdapter();
        }

        @Override
        public Object caseAnnotationContainer( AnnotationContainer object ) {
            return createAnnotationContainerAdapter();
        }

        @Override
        public Object caseModelAnnotation( ModelAnnotation object ) {
            return createModelAnnotationAdapter();
        }

        @Override
        public Object caseLink( Link object ) {
            return createLinkAdapter();
        }

        @Override
        public Object caseLinkContainer( LinkContainer object ) {
            return createLinkContainerAdapter();
        }

        @Override
        public Object caseDatatype( Datatype object ) {
            return createDatatypeAdapter();
        }

        @Override
        public Object caseIdentifiable( Identifiable object ) {
            return createIdentifiableAdapter();
        }

        @Override
        public Object caseModelImport( ModelImport object ) {
            return createModelImportAdapter();
        }

        @Override
        public Object defaultCase( EObject object ) {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter( Notifier target ) {
        return (Adapter)modelSwitch.doSwitch((EObject)target);
    }

    /**
     * Creates a new adapter for an object of class '{@link org.teiid.designer.metamodels.core.Annotation <em>Annotation</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a
     * case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.teiid.designer.metamodels.core.Annotation
     * @generated
     */
    public Adapter createAnnotationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.teiid.designer.metamodels.core.AnnotationContainer
     * <em>Annotation Container</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
     * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.teiid.designer.metamodels.core.AnnotationContainer
     * @generated
     */
    public Adapter createAnnotationContainerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.teiid.designer.metamodels.core.ModelAnnotation
     * <em>Model Annotation</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.teiid.designer.metamodels.core.ModelAnnotation
     * @generated
     */
    public Adapter createModelAnnotationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.teiid.designer.metamodels.core.Link <em>Link</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a
     * case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.teiid.designer.metamodels.core.Link
     * @generated
     */
    public Adapter createLinkAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.teiid.designer.metamodels.core.LinkContainer <em>Link Container</em>}
     * '. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
     * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.teiid.designer.metamodels.core.LinkContainer
     * @generated
     */
    public Adapter createLinkContainerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.teiid.designer.metamodels.core.Datatype <em>Datatype</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a
     * case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.teiid.designer.metamodels.core.Datatype
     * @generated
     */
    public Adapter createDatatypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.teiid.designer.metamodels.core.Identifiable <em>Identifiable</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore
     * a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.teiid.designer.metamodels.core.Identifiable
     * @generated
     */
    public Adapter createIdentifiableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.teiid.designer.metamodels.core.ModelImport <em>Model Import</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore
     * a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.teiid.designer.metamodels.core.ModelImport
     * @generated
     */
    public Adapter createModelImportAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!--
     * end-user-doc -->
     * 
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} // CoreAdapterFactory
