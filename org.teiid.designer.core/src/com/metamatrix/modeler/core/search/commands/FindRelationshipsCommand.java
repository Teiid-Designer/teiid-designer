/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.search.commands;

import java.util.Collection;
import java.util.List;

import com.metamatrix.modeler.core.index.IndexSelector;

/**
 * This interface is used to find relationships between models based on specified criteria.
 */
public interface FindRelationshipsCommand	extends SearchCommand {

	/**
	 * Return a collection of {@link com.metamatrix.modeler.relationship.search.index.RelationshipRecord}
	 * objects that are found on this command execution. 
	 * @return a collection of relationship records
	 */
	Collection getRelationShipInfo();

    /**
     * Set the IndexSelector that will be used to obtain models that will be searched.
     * @param selector the index selector that should be used, or null if the 
     * {@link com.metamatrix.modeler.internal.core.index.ModelWorkspaceIndexSelector} should be used
     */
    void setIndexSelector( IndexSelector selector );

    /**
     * Set the Case sensitivity boolean that will be used to obtain relationships that will 
     * be searched. This is used when a name pattern name is specified.
     * @param  caseSensitive
     */
    void setCaseSensitive( boolean caseSensitive );

	/**
	 * Set the uuid to the relationship that is being searched.
	 * @param The the uuid to the relationship that is being searched.
	 */
	void setRelationshipUUID(String uuid);

    /**
     * Set the search string to match with relationship name. 
     * name is specified.
     * @param namePattern
     */
    void setNamePattern( String namePattern );

    /**
     * Set the Name of the RelationshipType to search for. 
     * name is specified.
     * @param  
     */
    void setRelationshipTypeName( String RelationshipName );

    /**
     * Set the List of paths to the Participant model workspace resources to search in.
     * The objects from/to which there are relationships should be among these resources.
     * If the list is not set or a null value is set, the whole workspace is searched,
     * if an empty list is set then the command cannot be executed since there are no
     * resources to search in. 
     * @param participantList;list containing   
     */
    void setParticipantList( List participantList );

    /**
     * Set the List of paths to the model workspace relationship resources to search in.
     * The objects from which there are relationships should be among these resources.
     * If the list is not set or a null value is set, the whole workspace is searched,
     * if an empty list is set then the command cannot be executed since there are no
     * resources to search in. 
     * @param participantList;list containing   
     */
    void setRelationshipResourceScopeList( List participantList );

    /**
     * Set the boolean indicating whether sub types should be included in results. 
     * @param includeSubtypes 
     */
    void setIncludeSubtypes( boolean includeSubtypes );
}
