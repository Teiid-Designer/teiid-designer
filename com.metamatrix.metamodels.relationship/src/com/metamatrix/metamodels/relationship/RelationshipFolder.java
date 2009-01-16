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
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

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
