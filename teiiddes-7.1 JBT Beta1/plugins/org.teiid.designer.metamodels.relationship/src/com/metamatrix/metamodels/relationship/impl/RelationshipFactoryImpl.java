/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.impl;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.metamodels.relationship.FileReference;
import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.RelationshipTypeStatus;
import com.metamatrix.metamodels.relationship.UriReference;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class RelationshipFactoryImpl extends EFactoryImpl implements RelationshipFactory {

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationshipFactoryImpl() {
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
            case RelationshipPackage.RELATIONSHIP_TYPE:
                return createRelationshipType();
            case RelationshipPackage.RELATIONSHIP:
                return createRelationship();
            case RelationshipPackage.FILE_REFERENCE:
                return createFileReference();
            case RelationshipPackage.RELATIONSHIP_ROLE:
                return createRelationshipRole();
            case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER:
                return createPlaceholderReferenceContainer();
            case RelationshipPackage.URI_REFERENCE:
                return createUriReference();
            case RelationshipPackage.RELATIONSHIP_FOLDER:
                return createRelationshipFolder();
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
            case RelationshipPackage.RELATIONSHIP_TYPE_STATUS: {
                RelationshipTypeStatus result = RelationshipTypeStatus.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case RelationshipPackage.ISTATUS:
                return createIStatusFromString(eDataType, initialValue);
            case RelationshipPackage.LIST:
                return createListFromString(eDataType, initialValue);
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
            case RelationshipPackage.RELATIONSHIP_TYPE_STATUS:
                return instanceValue == null ? null : instanceValue.toString();
            case RelationshipPackage.ISTATUS:
                return convertIStatusToString(eDataType, instanceValue);
            case RelationshipPackage.LIST:
                return convertListToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationshipType createRelationshipType() {
        RelationshipTypeImpl relationshipType = new RelationshipTypeImpl();
        return relationshipType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Relationship createRelationship() {
        RelationshipImpl relationship = new RelationshipImpl();
        return relationship;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public FileReference createFileReference() {
        FileReferenceImpl fileReference = new FileReferenceImpl();
        return fileReference;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationshipRole createRelationshipRole() {
        RelationshipRoleImpl relationshipRole = new RelationshipRoleImpl();
        return relationshipRole;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public PlaceholderReferenceContainer createPlaceholderReferenceContainer() {
        PlaceholderReferenceContainerImpl placeholderReferenceContainer = new PlaceholderReferenceContainerImpl();
        return placeholderReferenceContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public UriReference createUriReference() {
        UriReferenceImpl uriReference = new UriReferenceImpl();
        return uriReference;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationshipFolder createRelationshipFolder() {
        RelationshipFolderImpl relationshipFolder = new RelationshipFolderImpl();
        return relationshipFolder;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public IStatus createIStatusFromString( EDataType eDataType,
                                            String initialValue ) {
        return (IStatus)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertIStatusToString( EDataType eDataType,
                                          Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List createListFromString( EDataType eDataType,
                                      String initialValue ) {
        return (List)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertListToString( EDataType eDataType,
                                       Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationshipPackage getRelationshipPackage() {
        return (RelationshipPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static RelationshipPackage getPackage() { // NO_UCD
        return RelationshipPackage.eINSTANCE;
    }

} // RelationshipFactoryImpl
