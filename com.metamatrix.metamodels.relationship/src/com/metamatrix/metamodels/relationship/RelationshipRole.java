/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Role</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#getStereotype <em>Stereotype</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#isOrdered <em>Ordered</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#isUnique <em>Unique</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#isNavigable <em>Navigable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#getLowerBound <em>Lower Bound</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#getUpperBound <em>Upper Bound</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#getRelationshipType <em>Relationship Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#getOppositeRole <em>Opposite Role</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#getIncludeTypes <em>Include Types</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.RelationshipRole#getExcludeTypes <em>Exclude Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole()
 * @model
 * @generated
 */
public interface RelationshipRole extends RelationshipEntity{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Stereotype</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Stereotype</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The stereotype for this role in the relationship, if different than the name.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Stereotype</em>' attribute.
     * @see #setStereotype(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_Stereotype()
     * @model
     * @generated
     */
    String getStereotype();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getStereotype <em>Stereotype</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Stereotype</em>' attribute.
     * @see #getStereotype()
     * @generated
     */
    void setStereotype(String value);

    /**
     * Returns the value of the '<em><b>Ordered</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ordered</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Specifies whether the order of the objects with this role in a relationship is considered important (conceptually).  The value, however, implies nothing about automated sorting of the values (that is, the user is responsible for re-ordering).
     * <!-- end-model-doc -->
     * @return the value of the '<em>Ordered</em>' attribute.
     * @see #setOrdered(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_Ordered()
     * @model default="false"
     * @generated
     */
    boolean isOrdered();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipRole#isOrdered <em>Ordered</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ordered</em>' attribute.
     * @see #isOrdered()
     * @generated
     */
    void setOrdered(boolean value);

    /**
     * Returns the value of the '<em><b>Unique</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Unique</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Specifies whether the objects bound to this role of a relationship must be unique.  Defaults to 'true'.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Unique</em>' attribute.
     * @see #setUnique(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_Unique()
     * @model default="true"
     * @generated
     */
    boolean isUnique();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipRole#isUnique <em>Unique</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Unique</em>' attribute.
     * @see #isUnique()
     * @generated
     */
    void setUnique(boolean value);

    /**
     * Returns the value of the '<em><b>Navigable</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Navigable</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Defines whether it is conceptually correct or possible to navigate to this end of the relationship.  Some relationships are uni-directional, meaning only one end of the relationship is navigable.  The default is 'true' (navigable).
     * <!-- end-model-doc -->
     * @return the value of the '<em>Navigable</em>' attribute.
     * @see #setNavigable(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_Navigable()
     * @model default="true"
     * @generated
     */
    boolean isNavigable();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipRole#isNavigable <em>Navigable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Navigable</em>' attribute.
     * @see #isNavigable()
     * @generated
     */
    void setNavigable(boolean value);

    /**
     * Returns the value of the '<em><b>Lower Bound</b></em>' attribute.
     * The default value is <code>"1"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Lower Bound</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The minimum number of objects that must be bound to this role in a relationship.  The value should be non-negative and less than or equal to the upper bound.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Lower Bound</em>' attribute.
     * @see #setLowerBound(int)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_LowerBound()
     * @model default="1"
     * @generated
     */
    int getLowerBound();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getLowerBound <em>Lower Bound</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Lower Bound</em>' attribute.
     * @see #getLowerBound()
     * @generated
     */
    void setLowerBound(int value);

    /**
     * Returns the value of the '<em><b>Upper Bound</b></em>' attribute.
     * The default value is <code>"-1"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Upper Bound</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The maximum number of objects that must be bound to this role in a relationship.  The value should be greater than or equal to the lower bound, and generally non-negative.  However, a value of "-1" means unlimited.  The default is unlimited.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Upper Bound</em>' attribute.
     * @see #setUpperBound(int)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_UpperBound()
     * @model default="-1"
     * @generated
     */
    int getUpperBound();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getUpperBound <em>Upper Bound</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Upper Bound</em>' attribute.
     * @see #getUpperBound()
     * @generated
     */
    void setUpperBound(int value);

    /**
     * Returns the value of the '<em><b>Constraint</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Constraint</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Field that can be used to define some textual constraints for the relationship type.  Example forms might include OCL, regular expressions, or simple text.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Constraint</em>' attribute.
     * @see #setConstraint(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_Constraint()
     * @model
     * @generated
     */
    String getConstraint();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getConstraint <em>Constraint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Constraint</em>' attribute.
     * @see #getConstraint()
     * @generated
     */
    void setConstraint(String value);

    /**
     * Returns the value of the '<em><b>Relationship Type</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relationship.RelationshipType#getRoles <em>Roles</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Relationship Type</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Relationship Type</em>' container reference.
     * @see #setRelationshipType(RelationshipType)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_RelationshipType()
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getRoles
     * @model opposite="roles"
     * @generated
     */
    RelationshipType getRelationshipType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getRelationshipType <em>Relationship Type</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Relationship Type</em>' container reference.
     * @see #getRelationshipType()
     * @generated
     */
    void setRelationshipType(RelationshipType value);

    /**
     * Returns the value of the '<em><b>Include Types</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.emf.ecore.EClass}.
     * <!-- begin-user-doc -->
     * <p>
     * An object can be a participant in the role of a relationship if it's metaclass is included
     * by the role and not excluded by the role. 
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Include Types</em>' reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_IncludeTypes()
     * @model type="org.eclipse.emf.ecore.EClass"
     * @generated
     */
    EList getIncludeTypes();

    /**
     * Returns the value of the '<em><b>Exclude Types</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.emf.ecore.EClass}.
     * <!-- begin-user-doc -->
     * <p>
     * An object can be a participant in the role of a relationship if it's metaclass is included
     * by the role and not excluded by the role. 
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Exclude Types</em>' reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_ExcludeTypes()
     * @model type="org.eclipse.emf.ecore.EClass"
     * @generated
     */
    EList getExcludeTypes();

    /**
     * Returns the value of the '<em><b>Opposite Role</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Opposite Role</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Opposite Role</em>' reference.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipRole_OppositeRole()
     * @model resolveProxies="false" required="true" transient="true" changeable="false" volatile="true"
     * @generated
     */
    RelationshipRole getOppositeRole();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Convenience method to determine whether this role is considered the 'source' role of the RelationshipType that owns this role.
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    boolean isSourceRole();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Convenience method to determine whether this role is considered the 'target' role of the RelationshipType that owns this role.
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    boolean isTargetRole();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Method to determine whether this relationship entity is considered valid.  The result is an IStatus that contains a message that can be displayed to the user, as well as a status code designating "OK", "WARNING", or "ERROR".
     * <!-- end-model-doc -->
     * @model dataType="com.metamatrix.metamodels.relationship.IStatus" 
     * @generated
     */
    IStatus isValidParticipant(EObject participant);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Method to determine whether this relationship entity is considered valid.  The result is an IStatus that contains a message that can be displayed to the user, as well as a status code designating "OK", "WARNING", or "ERROR".
     * <!-- end-model-doc -->
     * @model dataType="com.metamatrix.metamodels.relationship.IStatus" 
     * @generated
     */
    IStatus isValidParticipant(EClassifier participantType);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Method to compute whether the supplied EClassifier is considered valid or allowed by the includeTypes and excludeTypes features.  An object is valid for this role if 'instanceof' returns true for one of the EClassifiers in the 'includedTypes' feature AND 'instanceof' returns false for all of the EClassifiers in the 'excludedTypes' feature.  This method takes into account all overridden roles.
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    boolean isAllowed(EClassifier type);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Method to compute whether the supplied EClassifier is considered valid or allowed by the includeTypes and excludeTypes features.  An object is valid for this role if 'instanceof' returns true for one of the EClassifiers in the 'includedTypes' feature AND 'instanceof' returns false for all of the EClassifiers in the 'excludedTypes' feature.  This method takes into account all overridden roles.
     * <!-- end-model-doc -->
     * @model
     * @generated
     */
    boolean isAllowed(EObject particpant);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Obtain the corresponding RelationshipRole in the superType of this object's relationshipType, that is overridden by this role.  Will return null if there is no corresponding role.
     * <!-- end-model-doc -->
     * @model parameters=""
     * @generated
     */
    RelationshipRole getOverriddenRole();

} // RelationshipRole
