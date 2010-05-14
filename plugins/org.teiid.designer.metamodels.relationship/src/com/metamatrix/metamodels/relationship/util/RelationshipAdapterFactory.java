/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relationship.FileReference;
import com.metamatrix.metamodels.relationship.PlaceholderReference;
import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipContainer;
import com.metamatrix.metamodels.relationship.RelationshipEntity;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.UriReference;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code> method for each
 * class of the model. <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage
 * @generated
 */
public class RelationshipAdapterFactory extends AdapterFactoryImpl {

    /**
     * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected static RelationshipPackage modelPackage;

    /**
     * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationshipAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = RelationshipPackage.eINSTANCE;
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
    protected RelationshipSwitch modelSwitch = new RelationshipSwitch() {
        @Override
        public Object caseRelationshipType( RelationshipType object ) {
            return createRelationshipTypeAdapter();
        }

        @Override
        public Object caseRelationshipEntity( RelationshipEntity object ) {
            return createRelationshipEntityAdapter();
        }

        @Override
        public Object caseRelationship( Relationship object ) {
            return createRelationshipAdapter();
        }

        @Override
        public Object caseFileReference( FileReference object ) {
            return createFileReferenceAdapter();
        }

        @Override
        public Object caseRelationshipRole( RelationshipRole object ) {
            return createRelationshipRoleAdapter();
        }

        @Override
        public Object casePlaceholderReference( PlaceholderReference object ) {
            return createPlaceholderReferenceAdapter();
        }

        @Override
        public Object casePlaceholderReferenceContainer( PlaceholderReferenceContainer object ) {
            return createPlaceholderReferenceContainerAdapter();
        }

        @Override
        public Object caseUriReference( UriReference object ) {
            return createUriReferenceAdapter();
        }

        @Override
        public Object caseRelationshipFolder( RelationshipFolder object ) {
            return createRelationshipFolderAdapter();
        }

        @Override
        public Object caseRelationshipContainer( RelationshipContainer object ) {
            return createRelationshipContainerAdapter();
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
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.RelationshipType <em>Type</em>}
     * '. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
     * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.RelationshipType
     * @generated
     */
    public Adapter createRelationshipTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.RelationshipEntity
     * <em>Entity</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.RelationshipEntity
     * @generated
     */
    public Adapter createRelationshipEntityAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.Relationship
     * <em>Relationship</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.Relationship
     * @generated
     */
    public Adapter createRelationshipAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.FileReference
     * <em>File Reference</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.FileReference
     * @generated
     */
    public Adapter createFileReferenceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.RelationshipRole <em>Role</em>}
     * '. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
     * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole
     * @generated
     */
    public Adapter createRelationshipRoleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.PlaceholderReference
     * <em>Placeholder Reference</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
     * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.PlaceholderReference
     * @generated
     */
    public Adapter createPlaceholderReferenceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer
     * <em>Placeholder Reference Container</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
     * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer
     * @generated
     */
    public Adapter createPlaceholderReferenceContainerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.UriReference
     * <em>Uri Reference</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.UriReference
     * @generated
     */
    public Adapter createUriReferenceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.RelationshipFolder
     * <em>Folder</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.RelationshipFolder
     * @generated
     */
    public Adapter createRelationshipFolderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relationship.RelationshipContainer
     * <em>Container</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relationship.RelationshipContainer
     * @generated
     */
    public Adapter createRelationshipContainerAdapter() {
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

} // RelationshipAdapterFactory
