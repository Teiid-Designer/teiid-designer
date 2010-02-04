/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.index.IEntryResult;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.search.commands.FindRelationshipTypesCommand;
import com.metamatrix.modeler.core.search.runtime.RelationshipTypeRecord;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceSearchIndexSelector;
import com.metamatrix.modeler.internal.core.search.runtime.SearchRuntimeAdapter;

/**
 * FindRelationshipTypesCommandImpl.java
 */
public class FindRelationshipTypesCommandImpl implements FindRelationshipTypesCommand {

	// name of the relationship type
	private String typeNamePattern = null;
	// boolean indicating if seatch results should include
	// subtypes of a given relationship type
	private boolean includeSubtypes = false;	

	// collection of relationship type records
	private Collection relationTypeInfo = Collections.EMPTY_LIST;

	private IndexSelector selector;

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.commands.RelationshipSearchCommand#canExecute()
	 */
	public boolean canExecute() {
		return true;
	}

	/**
	 * @see com.metamatrix.modeler.core.search.commands.FindRelationshipsCommand#getIndexSelector()
	 */
	public IndexSelector getIndexSelector() {
		// selector used to lookup relationship indexes		
		this.selector = this.selector != null ?
									   this.selector :
									   new ModelWorkspaceSearchIndexSelector();
		return this.selector;									   
	}

	/**
	 * @see com.metamatrix.modeler.core.search.commands.FindRelationshipsCommand#setIndexSelector(com.metamatrix.modeler.core.index.IndexSelector)
	 */
	public void setIndexSelector(final IndexSelector selector) {
		this.selector = selector;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.commands.RelationshipSearchCommand#execute()
	 */
	public IStatus execute() {
		if(!canExecute()) {
			return null;
		}
		// get all related node info based of the UUID of the object
		try {
			Collection types = getRelationshipTypes(this.typeNamePattern);
			// if all types are not searched for
			// and if sub types need to be included in the results
			if(!StringUtil.isEmpty(this.typeNamePattern) && this.includeSubtypes) {
				Iterator typeIter = types.iterator();
				// collect all children
				Collection children = new ArrayList();
				while(typeIter.hasNext()) {
					RelationshipTypeRecord record = (RelationshipTypeRecord) typeIter.next();
					String typeUUID = record.getUUID();
					children.addAll(getChildTypes(typeUUID));					
				}
				// add children to to all types
				types.addAll(children);
			}

			// these are all the types that need to be returned			
			this.relationTypeInfo = types;
		} catch (Exception e) {
			ModelerCore.Util.log(e);
			return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, ModelerCore.Util.getString("FindRelationshipTypesCommandImpl.Error_trying_to_execute_command,_problem_trying_to_query_relationship_search_indexes._1"), e); //$NON-NLS-1$
		}

		return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
	}
	
	/**
	 * Get all the types that match the given pattern
	 */
	private Collection getRelationshipTypes(String namePattern) throws Exception {
		
		// search the indexes based on the name of the type
		String searchPrefix = IndexUtil.getPrefixPattern(IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_TYPE, this.typeNamePattern);
		// get all related node info based of the UUID of the object
		IEntryResult[] results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), searchPrefix.toCharArray(), true, false);
		Collection relationTypes = new ArrayList(results.length);
		for(int i=0; i < results.length; i++) {
			IEntryResult result = results[i];
			relationTypes.add(SearchRuntimeAdapter.createRelationshipTypeRecord(result.getWord()));
		}
		
		return relationTypes;
	}
	
	/**
	 * Get all the child types for the given type UUID
	 */
	private Collection getChildTypes(String typeUUID) throws Exception {
		
		// search the indexes based on the name of the type
		String searchPrefix = ""+IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_TYPE  //$NON-NLS-1$
							  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
							  + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING
							  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
							  + typeUUID + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
		// get all related node info based of the UUID of the object
		IEntryResult[] results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), searchPrefix.toCharArray(), false, false);
		Collection relationTypes = new ArrayList(results.length);
		for(int i=0; i < results.length; i++) {
			IEntryResult result = results[i];
			relationTypes.add(SearchRuntimeAdapter.createRelationshipTypeRecord(result.getWord()));
		}
		
		return relationTypes;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.commands.FindRelationshipTypesCommand#getRelationShipInfo()
	 */
	public Collection getRelationShipTypeInfo() {
		return this.relationTypeInfo;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.commands.FindRelationshipTypesCommand#setIncludeSubTypes(boolean)
	 */
	public void setIncludeSubTypes(boolean includeSubTypes) {
		this.includeSubtypes = includeSubTypes;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.commands.FindRelationshipTypesCommand#setRelationshipTypeName(java.lang.String)
	 */
	public void setRelationshipTypeName(String namePattern) {
		this.typeNamePattern = namePattern;
	}

}
