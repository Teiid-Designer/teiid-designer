/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relationship</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A connection or affiliation between a set of objects.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.Relationship#getFeatureValues <em>Feature Values</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.Relationship#getTargets <em>Targets</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.Relationship#getSources <em>Sources</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.Relationship#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.Relationship#getRelationshipContainer <em>Relationship Container</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationship()
 * @model
 * @generated
 */
public interface Relationship extends RelationshipEntity, RelationshipContainer{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Feature Values</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Feature Values</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Feature Values</em>' map.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationship_FeatureValues()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String"
     * @generated
     */
    EMap getFeatureValues();

    /**
     * Returns the value of the '<em><b>Targets</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Targets</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Targets</em>' reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationship_Targets()
     * @model type="org.eclipse.emf.ecore.EObject"
     * @generated
     */
    EList getTargets();

    /**
     * Returns the value of the '<em><b>Sources</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sources</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Sources</em>' reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationship_Sources()
     * @model type="org.eclipse.emf.ecore.EObject"
     * @generated
     */
    EList getSources();

    /**
     * Returns the value of the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' reference.
     * @see #isSetType()
     * @see #unsetType()
     * @see #setType(RelationshipType)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationship_Type()
     * @model unsettable="true"
     * @generated
     */
    RelationshipType getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.Relationship#getType <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' reference.
     * @see #isSetType()
     * @see #unsetType()
     * @see #getType()
     * @generated
     */
    void setType(RelationshipType value);

    /**
     * Unsets the value of the '{@link com.metamatrix.metamodels.relationship.Relationship#getType <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetType()
     * @see #getType()
     * @see #setType(RelationshipType)
     * @generated
     */
    void unsetType();

    /**
     * Returns whether the value of the '{@link com.metamatrix.metamodels.relationship.Relationship#getType <em>Type</em>}' reference is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Type</em>' reference is set.
     * @see #unsetType()
     * @see #getType()
     * @see #setType(RelationshipType)
     * @generated
     */
    boolean isSetType();

    /**
     * Returns the value of the '<em><b>Relationship Container</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relationship.RelationshipContainer#getOwnedRelationships <em>Owned Relationships</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Relationship Container</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Relationship Container</em>' container reference.
     * @see #setRelationshipContainer(RelationshipContainer)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationship_RelationshipContainer()
     * @see com.metamatrix.metamodels.relationship.RelationshipContainer#getOwnedRelationships
     * @model opposite="ownedRelationships"
     * @generated
     */
    RelationshipContainer getRelationshipContainer();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.Relationship#getRelationshipContainer <em>Relationship Container</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Relationship Container</em>' container reference.
     * @see #getRelationshipContainer()
     * @generated
     */
    void setRelationshipContainer(RelationshipContainer value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Convenience method to obtain the 'source' RelationshipRole in this object's RelationshipType.
     * <!-- end-model-doc -->
     * @model parameters=""
     * @generated
     */
    RelationshipRole getSourceRole();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Convenience method to obtain the 'target' RelationshipRole in this object's RelationshipType.
     * <!-- end-model-doc -->
     * @model parameters=""
     * @generated
     */
    RelationshipRole getTargetRole();

} // Relationship
