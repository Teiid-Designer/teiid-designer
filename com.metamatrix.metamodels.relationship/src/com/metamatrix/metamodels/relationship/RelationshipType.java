/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import java.util.List;
import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Type</b></em>'. <!-- end-user-doc --> <!-- begin-model-doc
 * --> A definition of the form for a relationship. In particular, it defines constraints on the types (metaclasses) and numbers
 * of objects that can be bound to a relationship. Typically, many relationships have the same type. Types can restrict or extend
 * other types (that is, support generalization or specialization). RelationshipType instances are created (modeled) by users and
 * customers to define the allowable types for associations. Examples of instances might include those named "Generalization",
 * "Inheritance", "Parent Of", "Uses", "Depends on", "Supplier of", etc. RelationshipType are basically "instantiated" as
 * Relationship objects that reference multiple objects. Constraints on which objects can be associated given an RelationshipType
 * are dictated by the RelationshipRoles. <!-- end-model-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#isDirected <em>Directed</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#isExclusive <em>Exclusive</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#isCrossModel <em>Cross Model</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#isAbstract <em>Abstract</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#isUserDefined <em>User Defined</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getStatus <em>Status</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getStereotype <em>Stereotype</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getConstraint <em>Constraint</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getLabel <em>Label</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getOppositeLabel <em>Opposite Label</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getRelationshipFeatures <em>Relationship Features</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getSuperType <em>Super Type</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getSubType <em>Sub Type</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getRoles <em>Roles</em>}</li>
 * <li>{@link com.metamatrix.metamodels.relationship.RelationshipType#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType()
 * @model
 * @generated
 */
public interface RelationshipType extends RelationshipEntity {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Directed</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * A relationship type that is directed simply implies that the sources and targets are sufficiently different such that a
     * single object must be placed in one side or the other. Most relationship types are directed. "Parent Of", "Author Of",
     * "Uses", "Depends On" are all examples of directed relationship types.
     * </p>
     * <p>
     * A relationship type that is not directed denotes that it doesn't really matter as to which side an object participates in a
     * relationship. Non-directed relationships are typically loose associations. For example, "sibling" might be a relationship
     * type that is not directed.
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> Specifies whether the relationship type has the concept of direction.
     * Defaults to 'true'. <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Directed</em>' attribute.
     * @see #setDirected(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Directed()
     * @model default="true"
     * @generated
     */
    boolean isDirected();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#isDirected <em>Directed</em>}'
     * attribute. <!-- begin-user-doc -->
     * <p>
     * A relationship type that is directed simply implies that the sources and targets are sufficiently different such that a
     * single object must be placed in one side or the other. Most relationship types are directed. "Parent Of", "Author Of",
     * "Uses", "Depends On" are all examples of directed relationship types. Directed relationship types also have an
     * {@link #getOppositeName() opposite name} that differs from the {@link #getName()}. A directed relationship type with a name
     * "Author Of" might have an opposite name of "Written by".
     * </p>
     * <p>
     * A relationship type that is not directed denotes that it doesn't really matter as to which side an object participates in a
     * relationship. Non-directed relationships are typically loose associations, and typically there is no need for an
     * {@link #getOppositeName() opposite name} (since it would be the same as the {@link #getName()}). For example, "sibling"
     * might be a relationship type that is not directed.
     * </p>
     * <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Directed</em>' attribute.
     * @see #isDirected()
     * @generated
     */
    void setDirected( boolean value );

    /**
     * Returns the value of the '<em><b>Exclusive</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If a relationship type is exclusive, then an object participating in a relationship must participate either in the
     * {@link #getSourceRole() source role} or in the {@link #getTargetRole() target role} of the relationship, but not both.
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> Defines whether it is legal for one object to be in both the 'sources' and a
     * 'targets' references. A value of 'true' means an object may appear in one or the other, but not both. The default is
     * 'true'. <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Exclusive</em>' attribute.
     * @see #setExclusive(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Exclusive()
     * @model default="true"
     * @generated
     */
    boolean isExclusive();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#isExclusive <em>Exclusive</em>}'
     * attribute. <!-- begin-user-doc -->
     * <p>
     * If a relationship type is exclusive, then an object participating in a relationship must participate either in the
     * {@link #getSourceRole() source role} or in the {@link #getTargetRole() target role} of the relationship, but not both.
     * </p>
     * <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Exclusive</em>' attribute.
     * @see #isExclusive()
     * @generated
     */
    void setExclusive( boolean value );

    /**
     * Returns the value of the '<em><b>Cross Model</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * A relationship type that allows participants in {@link Relationship} to exist in different models is considered to be
     * cross-model. Thus, a relationship type that requires participants in a relationship to exist in the same model is
     * considered to be not cross-model.
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> Defines whether it is legal for relationship instances with this type to
     * reference objects that are in different models. The default is 'true'. <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Cross Model</em>' attribute.
     * @see #setCrossModel(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_CrossModel()
     * @model default="true"
     * @generated
     */
    boolean isCrossModel();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#isCrossModel <em>Cross Model</em>}'
     * attribute. <!-- begin-user-doc -->
     * <p>
     * A relationship type that allows participants in {@link Relationship} to exist in different models is considered to be
     * cross-model. Thus, a relationship type that requires participants in a relationship to exist in the same model is
     * considered to be not cross-model.
     * </p>
     * <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Cross Model</em>' attribute.
     * @see #isCrossModel()
     * @generated
     */
    void setCrossModel( boolean value );

    /**
     * Returns the value of the '<em><b>Abstract</b></em>' attribute. The default value is <code>"false"</code>. <!--
     * begin-user-doc -->
     * <p>
     * An abstract relationship type cannot be referenced as the {@link Relationship#getType() type} of a {@link Relationship}. In
     * other words, it cannot be "instantiated".
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> Defines whether instances can be created of this relationship type. An
     * abstract relationship type cannot be instantiated. <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Abstract</em>' attribute.
     * @see #setAbstract(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Abstract()
     * @model default="false"
     * @generated
     */
    boolean isAbstract();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#isAbstract <em>Abstract</em>}'
     * attribute. <!-- begin-user-doc -->
     * <p>
     * An abstract relationship type cannot be referenced as the {@link Relationship#getType() type} of a {@link Relationship}. In
     * other words, it cannot be "instantiated".
     * </p>
     * <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Abstract</em>' attribute.
     * @see #isAbstract()
     * @generated
     */
    void setAbstract( boolean value );

    /**
     * Returns the value of the '<em><b>User Defined</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * All relationship types defined by users are considered to be user-defined. Thus, the only relationship types that would
     * return false from this method are those that are "built-in" to the application.
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> Defines whether a user has defined this type. The only relationship types
     * that can have a value set to 'false' are those types created by Federate Designer and managed as built-in relationship
     * types. <!-- end-model-doc -->
     * 
     * @return the value of the '<em>User Defined</em>' attribute.
     * @see #setUserDefined(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_UserDefined()
     * @model default="true"
     * @generated
     */
    boolean isUserDefined();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#isUserDefined <em>User Defined</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>User Defined</em>' attribute.
     * @see #isUserDefined()
     * @generated
     */
    void setUserDefined( boolean value );

    /**
     * Returns the value of the '<em><b>Status</b></em>' attribute. The default value is <code>"STANDARD"</code>. The literals are
     * from the enumeration {@link com.metamatrix.metamodels.relationship.RelationshipTypeStatus}. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Status</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> The status of a relationship type defines whether it is valid for instances
     * to exist. Example values include "Prototype" (i.e., "use with care"), "Standard", "Deprecated" (i.e.,
     * "don't use anymore, but some may exist"), or "Invalid" (i.e., "should not be used anymore"). <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Status</em>' attribute.
     * @see com.metamatrix.metamodels.relationship.RelationshipTypeStatus
     * @see #setStatus(RelationshipTypeStatus)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Status()
     * @model default="STANDARD"
     * @generated
     */
    RelationshipTypeStatus getStatus();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getStatus <em>Status</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Status</em>' attribute.
     * @see com.metamatrix.metamodels.relationship.RelationshipTypeStatus
     * @see #getStatus()
     * @generated
     */
    void setStatus( RelationshipTypeStatus value );

    /**
     * Returns the value of the '<em><b>Stereotype</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Stereotype</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Stereotype</em>' attribute.
     * @see #setStereotype(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Stereotype()
     * @model
     * @generated
     */
    String getStereotype();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getStereotype <em>Stereotype</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Stereotype</em>' attribute.
     * @see #getStereotype()
     * @generated
     */
    void setStereotype( String value );

    /**
     * Returns the value of the '<em><b>Constraint</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Constraint</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> Field that can be used to define some textual constraints for the
     * relationship type. Example forms might include OCL, regular expressions, or simple text. <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Constraint</em>' attribute.
     * @see #setConstraint(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Constraint()
     * @model
     * @generated
     */
    String getConstraint();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getConstraint <em>Constraint</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Constraint</em>' attribute.
     * @see #getConstraint()
     * @generated
     */
    void setConstraint( String value );

    /**
     * Returns the value of the '<em><b>Label</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Label</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> If the RelationshipType has implied direction, the label generally reflects
     * the verbage for such relationships in that direction. For example, if a RelationshipType might have a label of "Author of",
     * then the opposite label might be "Written by". <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Label</em>' attribute.
     * @see #setLabel(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Label()
     * @model
     * @generated
     */
    String getLabel();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getLabel <em>Label</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Label</em>' attribute.
     * @see #getLabel()
     * @generated
     */
    void setLabel( String value );

    /**
     * Returns the value of the '<em><b>Opposite Label</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Opposite Label</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc --> <!-- begin-model-doc --> If the RelationshipType has implied direction, the label generally reflects
     * the verbage for such relationships in that direction. This field defines an alternative label from the opposite
     * perspective. For example, if a RelationshipType has a label of "Author of", then the opposite label might be "Written by".
     * <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Opposite Label</em>' attribute.
     * @see #setOppositeLabel(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_OppositeLabel()
     * @model
     * @generated
     */
    String getOppositeLabel();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getOppositeLabel
     * <em>Opposite Label</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Opposite Label</em>' attribute.
     * @see #getOppositeLabel()
     * @generated
     */
    void setOppositeLabel( String value );

    /**
     * Returns the value of the '<em><b>Relationship Features</b></em>' reference list. The list contents are of type
     * {@link org.eclipse.emf.ecore.EStructuralFeature}. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Relationship Features</em>' reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Relationship Features</em>' reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_RelationshipFeatures()
     * @model type="org.eclipse.emf.ecore.EStructuralFeature"
     * @generated
     */
    EList getRelationshipFeatures();

    /**
     * Returns the value of the '<em><b>Super Type</b></em>' reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.relationship.RelationshipType#getSubType <em>Sub Type</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Super Type</em>' reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Super Type</em>' reference.
     * @see #isSetSuperType()
     * @see #unsetSuperType()
     * @see #setSuperType(RelationshipType)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_SuperType()
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getSubType
     * @model opposite="subType" unsettable="true"
     * @generated
     */
    RelationshipType getSuperType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getSuperType <em>Super Type</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Super Type</em>' reference.
     * @see #isSetSuperType()
     * @see #unsetSuperType()
     * @see #getSuperType()
     * @generated
     */
    void setSuperType( RelationshipType value );

    /**
     * Unsets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getSuperType <em>Super Type</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isSetSuperType()
     * @see #getSuperType()
     * @see #setSuperType(RelationshipType)
     * @generated
     */
    void unsetSuperType();

    /**
     * Returns whether the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getSuperType
     * <em>Super Type</em>}' reference is set. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Super Type</em>' reference is set.
     * @see #unsetSuperType()
     * @see #getSuperType()
     * @see #setSuperType(RelationshipType)
     * @generated
     */
    boolean isSetSuperType();

    /**
     * Returns the value of the '<em><b>Sub Type</b></em>' reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.relationship.RelationshipType}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.relationship.RelationshipType#getSuperType <em>Super Type</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sub Type</em>' reference list isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Sub Type</em>' reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_SubType()
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getSuperType
     * @model type="com.metamatrix.metamodels.relationship.RelationshipType" opposite="superType"
     * @generated
     */
    EList getSubType();

    /**
     * Returns the value of the '<em><b>Roles</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.relationship.RelationshipRole}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.relationship.RelationshipRole#getRelationshipType <em>Relationship Type</em>}'. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Roles</em>' containment reference list isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Roles</em>' containment reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Roles()
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getRelationshipType
     * @model type="com.metamatrix.metamodels.relationship.RelationshipRole" opposite="relationshipType" containment="true"
     *        lower="2" upper="2"
     * @generated
     */
    EList getRoles();

    /**
     * Returns the value of the '<em><b>Owner</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipTypes
     * <em>Owned Relationship Types</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owner</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Owner</em>' container reference.
     * @see #setOwner(RelationshipFolder)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipType_Owner()
     * @see com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipTypes
     * @model opposite="ownedRelationshipTypes"
     * @generated
     */
    RelationshipFolder getOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.RelationshipType#getOwner <em>Owner</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Owner</em>' container reference.
     * @see #getOwner()
     * @generated
     */
    void setOwner( RelationshipFolder value );

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> Convenience method to obtain the 'source'
     * RelationshipRole in this RelationshipType. <!-- end-model-doc -->
     * 
     * @model parameters=""
     * @generated
     */
    RelationshipRole getSourceRole();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> Convenience method to obtain the 'source'
     * RelationshipRole in this RelationshipType. <!-- end-model-doc -->
     * 
     * @model parameters=""
     * @generated
     */
    RelationshipRole getTargetRole();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> Method to obtain all of the relationship features
     * from this type and it's superType (recursively). <!-- end-model-doc -->
     * 
     * @model dataType="com.metamatrix.metamodels.relationship.List" parameters=""
     * @generated
     */
    List getAllRelationshipFeatures();

} // RelationshipType
