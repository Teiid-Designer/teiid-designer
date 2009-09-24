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
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * The RelationshipEditor provides a mechanism to manipulate a {@link Relationship} instance
 * and to perform validation of that instance.
 * <p>
 * Generally, an instance is obtained with the 
 * {@link RelationshipPlugin#createEditor(Relationship)} method.
 * </p>
 */
public interface RelationshipEditor {
    
    /**
     * Return the Relationship that this editor is working on.
     * @return the relationship; never null
     */
    Relationship getRelationship();

    /**
     * Validate the {@link Relationship} instance, and return the validation as a {@link IStatus}.
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
     * Get the current name of the relationship.
     * @return the relationship's name; may be null or zero-length
     */
    String getName();
    
    /**
     * Set the name of the relationship.
     * @param name the new name for the relationship; may be null or zero-length
     */
    void setName( String name );
    
    /**
     * Get the {@link RelationshipType type} for the relationship.
     * @return the relationship's type; may be null if there is no type associated
     * with the relationship
     */
    RelationshipType getRelationshipType();
    
    /**
     * Set the {@link RelationshipType type} for the relationship.
     * @param type the relationship's type; may be null if there is to be no type
     * associated with the relationship
     */
    void setRelationshipType( RelationshipType type );
    
    /**
     * Get the name for the source role of this relationship.  This is obtained by getting
     * the {@link RelationshipType#getSourceRole() source role}'s name, if this relationship
     * has a {@link #getRelationshipType() type}.  If this relationship has no
     * {@link #getRelationshipType() type}, this method returns null.
     * @return the name of the source role for this relationship's type, or null if this relationship
     * has no type.
     */
    String getSourceRoleName();
    
    /**
     * Get the name for the target role of this relationship.  This is obtained by getting
     * the {@link RelationshipType#getTargetRole() target role}'s name, if this relationship
     * has a {@link #getRelationshipType() type}.  If this relationship has no
     * {@link #getRelationshipType() type}, this method returns null.
     * @return the name of the target role for this relationship's type, or null if this relationship
     * has no type.
     */
    String getTargetRoleName();
    
    /**
     * Return the objects that are participants on the {@link RelationshipType#getSourceRole() source role}
     * side of the relationship.  This list can be modified directly.
     * @return the (modifiable) list of objects that are participating on the source role of the relationship;
     * never null, but possibly empty
     * @see #getTargetParticipants()
     */
    List getSourceParticipants();
    
    /**
     * Return the objects that are participants on the {@link RelationshipType#getTargetRole()() target role}
     * side of the relationship.  This list can be modified directly.
     * @return the (modifiable) list of objects that are participating on the source role of the relationship;
     * never null, but possibly empty
     * @see #getTargetParticipants()
     */
    List getTargetParticipants();
    
    /**
     * Helper method to add the supplied object as a participant on the source side of the relationship.
     * @param newSourceParticipant the object that is to be the new participant; may be null
     * (in which case this method does nothing)
     * @see #canAddToSourceParticipants(EObject)
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    void addSourceParticipant( EObject newSourceParticipant ) throws ModelerCoreException;
    
    /**
     * Helper method to add the supplied objects as participants on the source side of the relationship.
     * @param newSourceParticipants the objects that are to be the new participant; may be null or empty
     * (in which case this method does nothing)
     * @see #canAddToSourceParticipants(List)
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    void addSourceParticipants( List newSourceParticipants ) throws ModelerCoreException;
    
    /**
     * Helper method to remove the supplied object as a participant on the source side of the relationship.
     * @param sourceParticipant the object that is to no longer be a participant; may be null
     * (in which case this method does nothing)
     * @return true if the participant was removed, or false otherwise
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    boolean removeSourceParticipant( EObject sourceParticipant ) throws ModelerCoreException;

    /**
     * Helper method to remove the supplied objects as participants on the source side of the relationship.
     * All the participants are removed, or none of them are.
     * @param sourceParticipants the objects that are to no longer be participants; may be null or empty
     * (in which case this method does nothing)
     * @return true if all of the participants were removed, or false otherwise
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    boolean removeSourceParticipants( List sourceParticipants ) throws ModelerCoreException;
    
    /**
     * Helper method to add the supplied object as a participant on the target side of the relationship.
     * @param newTargetParticipant the object that is to be the new participant; may be null
     * (in which case this method does nothing)
     * @see #canAddToTargetParticipants(EObject)
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    void addTargetParticipant( EObject newTargetParticipant ) throws ModelerCoreException;
    
    /**
     * Helper method to add the supplied objects as participants on the target side of the relationship.
     * @param newTargetParticipants the objects that are to be the new participant; may be null or empty
     * (in which case this method does nothing)
     * @see #canAddToTargetParticipants(List)
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    void addTargetParticipants( List newTargetParticipants ) throws ModelerCoreException;
    
    /**
     * Helper method to remove the supplied object as a participant on the target side of the relationship.
     * @param targetParticipant the object that is to no longer be a participant; may be null
     * (in which case this method does nothing)
     * @return true if the participant was removed, or false otherwise
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    boolean removeTargetParticipant( EObject targetParticipant ) throws ModelerCoreException;

    /**
     * Helper method to remove the supplied objects as participants on the target side of the relationship.
     * All the participants are removed, or none of them are.
     * @param targetParticipants the objects that are to no longer be participants; may be null or empty
     * (in which case this method does nothing)
     * @return true if all of the participants were removed, or false otherwise
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    boolean removeTargetParticipants( List targetParticipants ) throws ModelerCoreException;
    
    /**
     * Move an object that is currently a participant on the {@link #getSourceParticipants() source}
     * side of the relationship to be a participant on the {@link #getTargetParticipants() target}
     * side of the relationship.  This will fail if the object cannot be moved to the target side.
     * @param sourceParticipant the participant in the source side; may not be null
     * @return true if the specified object was removed from the 
     * {@link #getSourceParticipants() source participants} and placed into the
     * {@link #getTargetParticipants() target participants}, or false otherwise
     * @see #canMoveSourceParticipantToTargetParticipant(EObject)
     */
    boolean moveSourceParticipantToTargetParticipant( EObject sourceParticipant );

    /**
     * Move an object that is currently a participant on the {@link #getTargetParticipants() target}
     * side of the relationship to be a participant on the {@link #getSourceParticipants() source}
     * side of the relationship.  This will fail if the object cannot be moved to the source side.
     * @param targetParticipant the participant in the target side; may not be null
     * @return true if the specified object was removed from the 
     * {@link #getTargetParticipants() target participants} and placed into the
     * {@link #getSourceParticipants() source participants}, or false otherwise
     * @see #canMoveTargetParticipantToSourceParticipant(EObject)
     */
    boolean moveTargetParticipantToSourceParticipant( EObject targetParticipant );

    /**
     * Move objects that are currently participants on the {@link #getSourceParticipants() source}
     * side of the relationship to be participants on the {@link #getTargetParticipants() target}
     * side of the relationship.  This will fail if the objects cannot be moved to the target side.
     * @param sourceParticipants the participants in the source side; may not be null
     * @return true if the specified objects were removed from the 
     * {@link #getSourceParticipants() source participants} and placed into the
     * {@link #getTargetParticipants() target participants}, or false otherwise
     * @see #canMoveSourceParticipantToTargetParticipant(List)
     */
    boolean moveSourceParticipantToTargetParticipant( List sourceParticipants );

    /**
     * Move objects that are currently participants on the {@link #getTargetParticipants() target}
     * side of the relationship to be participants on the {@link #getSourceParticipants() source}
     * side of the relationship.  This will fail if the objects cannot be moved to the source side.
     * @param targetParticipants the participants in the target side; may not be null
     * @return true if the specified objects were removed from the 
     * {@link #getTargetParticipants() target participants} and placed into the
     * {@link #getSourceParticipants() source participants}, or false otherwise
     * @see #canMoveTargetParticipantToSourceParticipant(List)
     */
    boolean moveTargetParticipantToSourceParticipant( List targetParticipants );

    /**
     * Return whether the supplied object can be moved from the {@link #getSourceParticipants() source}
     * side of the relationship to the {@link #getTargetParticipants() target} side of the relationship.
     * @param object the object; may not be null
     * @return true if the object can potentially be moved to the target participants,
     * or false if it cannot
     * @see #moveSourceParticipantToTargetParticipant(EObject)
     */
    boolean canMoveSourceParticipantToTargetParticipant( EObject object );

    /**
     * Return whether the supplied object can be moved from the {@link #getTargetParticipants() target}
     * side of the relationship to the {@link #getSourceParticipants() source} side of the relationship.
     * @param object the object; may not be null
     * @return true if the object can potentially be moved to the source participants,
     * or false if it cannot
     * @see #moveTargetParticipantToSourceParticipant(EObject)
     */
    boolean canMoveTargetParticipantToSourceParticipant( EObject object );

    /**
     * Return whether the supplied objects can be moved from the {@link #getSourceParticipants() source}
     * side of the relationship to the {@link #getTargetParticipants() target} side of the relationship.
     * @param objects the objects; may not be null
     * @return true if the objects can potentially be moved to the target participants,
     * or false if they cannot
     * @see #moveSourceParticipantToTargetParticipant(List)
     */
    boolean canMoveSourceParticipantToTargetParticipant( List objects );

    /**
     * Return whether the supplied objects can be moved from the {@link #getTargetParticipants() target}
     * side of the relationship to the {@link #getSourceParticipants() source} side of the relationship.
     * @param objects the objects; may not be null
     * @return true if the objects can potentially be moved to the source participants,
     * or false if they cannot
     * @see #moveTargetParticipantToSourceParticipant(List)
     */
    boolean canMoveTargetParticipantToSourceParticipant( List objects );

    /**
     * Return whether the supplied object can be added to the {@link #getTargetParticipants() target}
     * side of the relationship.
     * @param object the object; may not be null
     * @return true if the object can potentially be added to the target participants,
     * or false if it cannot
     * @see #moveSourceParticipantToTargetParticipant(EObject)
     * @see #canMoveSourceParticipantToTargetParticipant(EObject)
     */
    boolean canAddToTargetParticipants( EObject object );

    /**
     * Return whether the supplied object can be added to the {@link #getSourceParticipants() source}
     * side of the relationship.
     * @param object the object; may not be null
     * @return true if the object can potentially be added to the source participants,
     * or false if it cannot
     * @see #moveTargetParticipantToSourceParticipant(EObject)
     * @see #canMoveTargetParticipantToSourceParticipant(EObject)
     */
    boolean canAddToSourceParticipants( EObject object );

    /**
     * Return whether the supplied objects can be added to the {@link #getTargetParticipants() target}
     * side of the relationship.
     * @param objects the objects; may not be null
     * @return true if the objects can potentially be added to the target participants,
     * or false if they cannot
     * @see #moveSourceParticipantToTargetParticipant(List)
     * @see #canMoveSourceParticipantToTargetParticipant(List)
     */
    boolean canAddToTargetParticipants( List object );

    /**
     * Return whether the supplied objects can be added to the {@link #getSourceParticipants() source}
     * side of the relationship.
     * @param objects the objects; may not be null
     * @return true if the objects can potentially be added to the source participants,
     * or false if they cannot
     * @see #moveTargetParticipantToSourceParticipant(List)
     * @see #canMoveTargetParticipantToSourceParticipant(List)
     */
    boolean canAddToSourceParticipants( List object );

    /**
     * Return whether it is possible for the source participants and target participants to be swapped.
     * @return true if the participants can be swapped, or false if they cannot
     * @see #swapParticipants()
     */
    boolean canSwapParticipants();
    
    /**
     * Swap the source participants and target participants.
     * @see #canSwapParticipants()
     * @throws ModelerCoreException if there is a problem performing the operation
     */
    void swapParticipants() throws ModelerCoreException;
}
