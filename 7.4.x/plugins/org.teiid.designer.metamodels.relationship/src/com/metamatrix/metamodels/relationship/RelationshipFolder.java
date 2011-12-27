/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Folder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipTypes <em>Owned Relationship Types</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipFolders <em>Owned Relationship Folders</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipFolder()
 * @model
 * @generated
 */
public interface RelationshipFolder extends RelationshipEntity, PlaceholderReferenceContainer, RelationshipContainer{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Owned Relationship Types</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relationship.RelationshipType}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relationship.RelationshipType#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owned Relationship Types</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owned Relationship Types</em>' containment reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipFolder_OwnedRelationshipTypes()
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getOwner
     * @model type="com.metamatrix.metamodels.relationship.RelationshipType" opposite="owner" containment="true"
     * @generated
     */
    EList getOwnedRelationshipTypes();

    /**
     * Returns the value of the '<em><b>Owned Relationship Folders</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relationship.RelationshipFolder}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owned Relationship Folders</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owned Relationship Folders</em>' containment reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipFolder_OwnedRelationshipFolders()
     * @see com.metamatrix.metamodels.relationship.RelationshipFolder#getOwner
     * @model type="com.metamatrix.metamodels.relationship.RelationshipFolder" opposite="owner" containment="true"
     * @generated
     */
    EList getOwnedRelationshipFolders();

    /**
     * Returns the value of the '<em><b>Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipFolders <em>Owned Relationship Folders</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owner</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owner</em>' container reference.
     * @see #setOwner(RelationshipFolder)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipFolder_Owner()
     * @see com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipFolders
     * @model opposite="ownedRelationshipFolders"
     * @generated
     */
    RelationshipFolder getOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwner <em>Owner</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Owner</em>' container reference.
     * @see #getOwner()
     * @generated
     */
    void setOwner(RelationshipFolder value);

} // RelationshipFolder
