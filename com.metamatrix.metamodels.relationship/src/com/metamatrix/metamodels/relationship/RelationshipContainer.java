/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipContainer#getOwnedRelationships <em>Owned Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipContainer()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface RelationshipContainer extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Owned Relationships</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relationship.Relationship}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relationship.Relationship#getRelationshipContainer <em>Relationship Container</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owned Relationships</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owned Relationships</em>' containment reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipContainer_OwnedRelationships()
     * @see com.metamatrix.metamodels.relationship.Relationship#getRelationshipContainer
     * @model type="com.metamatrix.metamodels.relationship.Relationship" opposite="relationshipContainer" containment="true"
     * @generated
     */
    EList getOwnedRelationships();

} // RelationshipContainer
