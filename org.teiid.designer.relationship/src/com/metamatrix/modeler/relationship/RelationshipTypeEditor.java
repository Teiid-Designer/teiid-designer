/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;

import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * The RelationshipTypeEditor provides a mechanism to manipulate a {@link RelationshipType} instance,
 * including it's two {@link RelationshipRole} instances.  The editor can also validate a type
 * and it's role instances.  
 * <p>
 * Generally, an instance is obtained with the 
 * {@link RelationshipPlugin#createEditor(RelationshipType)} method.
 * </p>
 */
public interface RelationshipTypeEditor {
    
    /**
     * Return the RelationshipType that this editor is working on.
     * @return the relationship type; never null
     */
    RelationshipType getRelationshipType();

    /**
     * Validate the {@link RelationshipType} instance, and return the validation as a {@link IStatus}.
     * There are no problems or warnings if the resulting {@link IStatus} returns <code>true</code>
     * for {@link IStatus#isOK()}; otherwise, the {@link IStatus#getSeverity() severity} describes
     * whether there are warnings or errors.  
     * <p>
     * If there are multiple problems or messages, the resulting IStatus will be a
     * {@link org.eclipse.core.runtime.MultiStatus MultiStatus}, with the most pressing and descriptive
     * problem being the first {@link MultiStatus#getChildren() child}.
     * @return the status detailing any problems or messages; never null
     */
    IStatus validate();

    /**
     * Get the current {@link RelationshipType#getName() name} of the relationship type.
     * @return the relationship type's name; may be null or zero-length
     */
    String getName();
    
    /**
     * Set the {@link RelationshipType#getName() name} of the relationship type.
     * @param name the new name for the relationship type; may be null or zero-length
     */
    void setName( String name );

    /**
     * Get the current {@link RelationshipType#getLabel() label} of the relationship type.
     * @return the relationship type's label; may be null or zero-length
     */
    String getLabel();
    
    /**
     * Set the {@link RelationshipType#getLabel() label} of the relationship type.
     * @param label the new label for the relationship type; may be null or zero-length
     */
    void setLabel( String label );
    
    /**
     * Get the current {@link RelationshipType#getOppositeLabel() opposite label} of the relationship type.
     * @return the relationship type's opposite label; may be null or zero-length
     */
    String getOppositeLabel();
    
    /**
     * Set the {@link RelationshipType#getOppositeLabel() opposite label} of the relationship type.
     * @param oppositeLabel the new opposite label for the relationship type; may be null or zero-length
     */
    void setOppositeLabel( String oppositeLabel );
    
    /**
     * Get the current {@link RelationshipType#getStereotype() stereotype} for the relationship type.
     * @return the stereotype; may be null or zero-length
     */
    String getStereotype();
    
    /**
     * Set the {@link RelationshipType#getStereotype() stereotype} on the relationship type.
     * @param stereotype the stereotype; may be null or zero-length
     */
    void setStereotype( String stereotype );
    
    /**
     * Get the name for the {@link RelationshipType#getSourceRole() source role} of this relationship type.
     * @return the name of the source role for this relationship type; may be null or zero-length
     */
    String getSourceRoleName();
    
    /**
     * Get the name for the {@link RelationshipType#getTargetRole() target role} of this relationship type.
     * @return the name of the target role for this relationship type; may be null or zero-length
     */
    String getTargetRoleName();
    
    /**
     * Get the current {@link RelationshipType#getRelationshipFeatures() features} for the relationship type.
     * This list can be modified directly; however, {@link #validate() validation} does check to ensure that
     * no two features have the same name.
     * @return the list of {@link E; never null, but possibly empty
     */
    List getFeatures();
    
    /**
     * Get whether the relationship type is considered {@link RelationshipType#isDirected() directed}.
     * @return true if the relationship type is directed, or false otherwise
     * @see #setDirected(boolean)
     */
    boolean isDirected();
    
    /**
     * Set whether the relationship type is considered {@link RelationshipType#isDirected() directed}.
     * @param directed true if the relationship type is directed, or false otherwise
     * @see #isDirected()
     */
    void setDirected( boolean directed );
    
    /**
     * Get whether the relationship type is considered {@link RelationshipType#isExclusive() exclusive}.
     * @return true if the relationship type is exclusive, or false otherwise
     * @see #setExclusive(boolean)
     */
    boolean isExclusive();
    
    /**
     * Set whether the relationship type is considered {@link RelationshipType#isExclusive() exclusive}.
     * @param exclusive true if the relationship type is exclusive, or false otherwise
     * @see #isExclusive()
     */
    void setExclusive( boolean exclusive );
    
    /**
     * Get whether the relationship type can have participants that can
     * {@link RelationshipType#isCrossModel() exist in multiple models}.
     * @return true if the relationship type allows participants that are in separate models,
     * or false if the participants are required to be in the same model
     * @see #setCrossModel(boolean)
     */
    boolean isCrossModel();
    
    /**
     * Set whether the relationship type can have participants that can
     * {@link RelationshipType#isCrossModel() exist in multiple models}.
     * @param crossModel true if the relationship type allows participants that are in separate models, 
     * or false if the participants are required to be in the same model
     * @see #isCrossModel()
     */
    void setCrossModel( boolean crossModel );
    
    /**
     * Get whether the relationship type is considered {@link RelationshipType#isExclusive() abstract},
     * meaning no {@link Relationship} instances can have this RelationshipType as it's
     * {@link Relationship#getType() type}.
     * @return true if the relationship type is abstract, or false otherwise
     * @see #setAbstract(boolean)
     */
    boolean isAbstract();
    
    /**
     * Set whether the relationship type is considered {@link RelationshipType#isExclusive() abstract},
     * meaning no {@link Relationship} instances can have this RelationshipType as it's
     * {@link Relationship#getType() type}.
     * @param exclusive true if the relationship type is abstract, or false otherwise
     * @see #isAbstract()
     */
    void setAbstract( boolean abstractState );
    
    // =========================================================================
    //                      Inheritance-related methods
    // =========================================================================
    
    /**
     * Get the current {@link RelationshipType#getSuperType() supertype} for the relationship type.
     * @return the supertype; may be null if there is no supertype
     */
    RelationshipType getSupertype();
    
    /**
     * Set the {@link RelationshipType#getSuperType() supertype} on the relationship type.  
     * There may be no circularity of relationship type inheritance; that is, the current relationship
     * type may not have itself as a (perhaps distant) supertype.  However, this method sets the supertype
     * either way, since circularity is caught by {@link #validate() validation}.
     * @param supertype the supertype; may be null if there is to be no supertype
     * @return true if the supertype was set, or false if it could not be set
     * @see #canSetSupertype(RelationshipType)
     */
    void setSupertype( RelationshipType supertype);
    
    /**
     * Determine whether the {@link RelationshipType#getSuperType() supertype} on the relationship type
     * can be set to the supplied RelationshipType instance.  There may be no circularity of relationship
     * type inheritance; that is, the current relationship type may not have itself as a (perhaps distant) 
     * supertype.
     * @param supertype the supertype; may be null if there is to be no supertype
     * @return true if the supertype was set, or false if it could not be set
     * @see #setSupertype(RelationshipType)
     */
    boolean canSetSupertype( RelationshipType supertype);
    
    /**
     * Get the current {@link RelationshipType#getSubType() subtype} for the relationship type.
     * This list can be modified directly; however, adding a RelationshipType to the list when
     * {@link #canAddSubtype(RelationshipType)} returns false with that same type may cause a circularity
     * in the inheritance.  Such a condition will be caught with {@link #validate() validation}.
     * @return the subtypes; never null, but possibly empty
     */
    List getSubtypes();
    
    /**
     * Determine whether the supplied RelationshipType can be added as a 
     * {@link RelationshipType#getSubType() subtype} of the current relationship type.  This method
     * returns false if adding the subtype would cause a circularity in the inheritance (i.e., the
     * current relationship type would have itself as a perhaps distant supertype).
     * @param stereotype the stereotype; may be null if there is to be no supertype
     * @return true if the supertype was set, or false if it could not be set
     * @see #setSupertype(RelationshipType)
     */
    boolean canAddSubtype( RelationshipType subtype);
    
    // =========================================================================
    //                      Role-related methods
    // =========================================================================
    
    /**
     * Get the RelationshipRole that is on the {@link RelationshipType#getSourceRole() source} side of the
     * relationship.
     * @return the source role; never null
     */
    RelationshipRole getSourceRole();
    
    /**
     * Get the RelationshipRole that is on the {@link RelationshipType#getTargetRole() source} side of the
     * relationship.
     * @return the target role; never null
     */
    RelationshipRole getTargetRole();
    
    /**
     * Obtain the name of the supplied relationship role.
     * @param role the role; may not be null
     * @return the name of the role; may be null or zero-length if there is no name
     */
    String getRoleName( RelationshipRole role );
    
    /**
     * Set the name of the supplied relationship role.
     * @param role the role; may not be null
     * @param name the name of the role; may be null or zero-length if there is no name
     */
    void setRoleName( RelationshipRole role, String name );
    
    /**
     * Get whether the specified role is considered navigable
     * @param role the relationship role; may not be null
     * @return true if navigable, or false if not
     */
    boolean isNavigable( RelationshipRole role );
    
    /**
     * Set whether the specified role is considered navigable
     * @param role the relationship role; may not be null
     * @param navigable true if navigable, or false if not
     */
    void setNavigable( RelationshipRole role, boolean navigable );
    
    /**
     * Get whether the specified role orders its participants
     * @param role the relationship role; may not be null
     * @return true if ordered, or false if not
     */
    boolean isOrdered( RelationshipRole role );
    
    /**
     * Set whether the specified role orders its participants
     * @param role the relationship role; may not be null
     * @param ordered true if ordered, or false if not
     */
    void setOrdered( RelationshipRole role, boolean ordered );
    
    /**
     * Get whether the specified role is considered to require unique participants
     * @param role the relationship role; may not be null
     * @return true if unique, or false if not
     */
    boolean isUnique( RelationshipRole role );
    
    /**
     * Set whether the specified role is considered to require unique participants
     * @param role the relationship role; may not be null
     * @param unique true if unique, or false if not
     */
    void setUnique( RelationshipRole role, boolean unique );
    
    /**
     * Get the lower bound of the multiplicty for the specified role.  The role's multiplicity
     * is the number of participants that are allowed, so the lower bound is the minimum
     * number of participants.
     * @param role the relationship role; may not be null
     * @return true if unique, or false if not
     * @see #setLowerBound(RelationshipRole, int)
     * @see #getUpperBound(RelationshipRole)
     */
    int getLowerBound( RelationshipRole role );
    
    /**
     * Set the lower bound of the multiplicty for the specified role.  The role's multiplicity
     * is the number of participants that are allowed, so the lower bound is the minimum
     * number of participants.  The lower bound is valid only if the the value is 0 or more
     * and is less than or equal to the upper bound.
     * @param role the relationship role; may not be null
     * @param lowerBound the lower bound
     * @see #getLowerBound(RelationshipRole)
     * @see #getUpperBound(RelationshipRole)
     */
    void setLowerBound( RelationshipRole role, int lowerBound );

    /**
     * Get the lower bound of the multiplicty for the specified role.  The role's multiplicity
     * is the number of participants that are allowed, so the lower bound is the minimum
     * number of participants.  The upper bound is valid only if the the value is 1 or more
     * and is greater or equal to the lower bound.
     * @param role the relationship role; may not be null
     * @return true if unique, or false if not
     * @see #setLowerBound(RelationshipRole, int)
     * @see #getUpperBound(RelationshipRole)
     */
    int getUpperBound( RelationshipRole role );
    
    /**
     * Set the upper bound of the multiplicty for the specified role.  The role's multiplicity
     * is the number of participants that are allowed, so the lower bound is the maximum
     * number of participants.  The upper bound is valid only if the the value is 1 or more
     * and is greater or equal to the lower bound.
     * @param role the relationship role; may not be null
     * @param upperBound the upper bound
     * @see #getLowerBound(RelationshipRole)
     * @see #getUpperBound(RelationshipRole)
     */
    void setUpperBound( RelationshipRole role, int upperBound );
    
    /**
     * Get the list of {@link EClass metaclasses} that define the types of participants that are
     * allowed in {@link Relationship} instances in the supplied role.  An object can be a participant
     * in the role of a relationship if it's metaclass is included by the role and not excluded by
     * the role.
     * <p>
     * This list can be modified directly, but metaclasses added may not be {@link #validate() valid} if
     * {@link #canAddIncludedMetaclass(RelationshipRole, EClass)} returns false.
     * </p>
     * @param role the relationship role; may not be null
     * @return the list of metaclass instances
     * @see #getExcludedMetaclasses(RelationshipRole)
     */
    List getIncludedMetaclasses( RelationshipRole role );
    
    /**
     * Return whether the supplied metaclass can be added to the list of 
     * {@link #getIncludedMetaclasses(RelationshipRole) included metaclasses}.  If doing so would
     * cause a validation error, this method returns false.
     * @param role the relationship role; may not be null
     * @param metaclass the metaclass to be added; may not be null
     * @return true if the metaclass can be added to the included list without causing validation problems,
     * or false otherwise
     */
    boolean canAddIncludedMetaclass( RelationshipRole role, EClass metaclass );

    /**
     * Helper method to add the supplied metaclass to the list of 
     * {@link #getIncludedMetaclasses(RelationshipRole) included metaclasses}.
     * @param role the relationship role; may not be null
     * @param metaclass the metaclass to be added; may not be null
     * @see #canAddIncludedMetaclass(RelationshipRole, EClass)
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    void addIncludedMetaclass( RelationshipRole role, EClass metaclass ) throws ModelerCoreException;
    
    /**
     * Helper method to remove the supplied metaclass from the list of 
     * {@link #getIncludedMetaclasses(RelationshipRole) included metaclasses}.
     * @param role the relationship role; may not be null
     * @param metaclass the metaclass to be removed; may not be null
     * @return true if the participant was removed, or false otherwise
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    boolean removeIncludedMetaclass( RelationshipRole role, EClass metaclass ) throws ModelerCoreException;

    /**
     * Get the list of {@link EClass metaclasses} that define the types of participants that are
     * <i>not</i> allowed in {@link Relationship} instances in the supplied role.  
     * An object can be a participant in the role of a relationship if it's metaclass is included
     * by the role and not excluded by the role. 
     * <p>
     * This list can be modified directly, but metaclasses added may not be {@link #validate() valid} if
     * {@link #canAddExcludedMetaclass(RelationshipRole, EClass)} returns false.
     * </p>
     * @param role the relationship role; may not be null
     * @return the list of metaclass instances
     * @see #getExcludedMetaclasses(RelationshipRole)
     */
    List getExcludedMetaclasses( RelationshipRole role );
    
    /**
     * Return whether the supplied metaclass can be added to the list of 
     * {@link #getExcludedMetaclasses(RelationshipRole) included metaclasses}.  If doing so would
     * cause a validation error, this method returns false.
     * @param role the relationship role; may not be null
     * @param metaclass the metaclass to be added; may not be null
     * @return true if the metaclass can be added to the included list without causing validation problems,
     * or false otherwise
     */
    boolean canAddExcludedMetaclass( RelationshipRole role, EClass metaclass );
    
    /**
     * Helper method to add the supplied metaclass to the list of 
     * {@link #getExcludedMetaclasses(RelationshipRole) excluded metaclasses}.
     * @param role the relationship role; may not be null
     * @param metaclass the metaclass to be added; may not be null
     * @see #canAddIncludedMetaclass(RelationshipRole, EClass)
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    void addExcludedMetaclass( RelationshipRole role, EClass metaclass ) throws ModelerCoreException;
    
    /**
     * Helper method to remove the supplied metaclass from the list of 
     * {@link #getExcludedMetaclasses(RelationshipRole) excluded metaclasses}.
     * @param role the relationship role; may not be null
     * @param metaclass the metaclass to be removed; may not be null
     * @return true if the metaclass was removed, or false otherwise
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    boolean removeExcludedMetaclass( RelationshipRole role, EClass metaclass ) throws ModelerCoreException;
}
