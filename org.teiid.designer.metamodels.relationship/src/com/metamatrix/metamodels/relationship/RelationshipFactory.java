/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage
 * @generated
 */
public interface RelationshipFactory extends EFactory {

    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    RelationshipFactory eINSTANCE = new com.metamatrix.metamodels.relationship.impl.RelationshipFactoryImpl();

    /**
     * Returns a new object of class '<em>Type</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Type</em>'.
     * @generated
     */
    RelationshipType createRelationshipType();

    /**
     * Returns a new object of class '<em>Relationship</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Relationship</em>'.
     * @generated
     */
    Relationship createRelationship();

    /**
     * Returns a new object of class '<em>File Reference</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>File Reference</em>'.
     * @generated
     */
    FileReference createFileReference();

    /**
     * Returns a new object of class '<em>Role</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Role</em>'.
     * @generated
     */
    RelationshipRole createRelationshipRole();

    /**
     * Returns a new object of class '<em>Placeholder Reference Container</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Placeholder Reference Container</em>'.
     * @generated
     */
    PlaceholderReferenceContainer createPlaceholderReferenceContainer();

    /**
     * Returns a new object of class '<em>Uri Reference</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Uri Reference</em>'.
     * @generated
     */
    UriReference createUriReference();

    /**
     * Returns a new object of class '<em>Folder</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Folder</em>'.
     * @generated
     */
    RelationshipFolder createRelationshipFolder();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    RelationshipPackage getRelationshipPackage(); // NO_UCD

} // RelationshipFactory
