/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IEntryResult;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.core.index.ModelWorkspaceSearchIndexSelector;
import org.teiid.designer.core.search.runtime.RelatedObjectRecord;
import org.teiid.designer.core.search.runtime.RelationshipRecord;
import org.teiid.designer.core.search.runtime.RelationshipTypeRecord;
import org.teiid.designer.core.search.runtime.SearchRuntimeAdapter;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;


/**
 * FindRelationshipsCommandImpl.java
 *
 * @since 8.0
 */
public class FindRelationshipsCommandImpl implements FindRelationshipsCommand {

    private String relationshipTypeName = null;
    private String namePattern = null;
    private String relationshipUUID = null;
    private List participantList = null;
    private List relationshipScope = null;
    private boolean caseSensitive = false;
    private boolean includeSubtypes = false;

	private Collection relationsInfo = Collections.EMPTY_LIST;

    private IndexSelector selector;

    /**
     * @see org.teiid.designer.core.search.commands.FindRelationshipsCommand#getIndexSelector()
     */
    private IndexSelector getIndexSelector() {
		// selector used to lookup relationship indexes		
		this.selector = this.selector != null ?
									   this.selector :
									   new ModelWorkspaceSearchIndexSelector();
        return this.selector;
    }

    /**
     * @see org.teiid.designer.core.search.commands.FindRelationshipsCommand#setIndexSelector(org.teiid.designer.core.index.IndexSelector)
     */
    @Override
	public void setIndexSelector(final IndexSelector selector) {
        this.selector = selector;
    }

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.commands.RelationshipSearchCommand#canExecute()
	 */
	@Override
	public boolean canExecute() {
	    if(this.participantList != null && this.participantList.isEmpty()) {
	        return false;
	    }
		return true;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.commands.RelationshipSearchCommand#execute()
	 */
	@Override
	public IStatus execute() {
		if(!canExecute()) {
			return null;
		}

 		try {
 			// search by UUID
			if(!CoreStringUtil.isEmpty(this.relationshipUUID)) {
				String prefix = IndexUtil.getPrefixPattern(IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP, this.relationshipUUID);
				IEntryResult[] results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), prefix.toCharArray(), true, true);
				relationsInfo = new HashSet(results.length);
				for(int i=0; i < results.length; i++) {
					IEntryResult result = results[i];
					relationsInfo.add(SearchRuntimeAdapter.createRelationshipRecord(result.getWord()));               
				}
			} else {
				// search by name
				IEntryResult[] results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), getNameSearchPattern().toCharArray(), false, false);
				relationsInfo = new HashSet(results.length);
				for(int i=0; i < results.length; i++) {
					IEntryResult result = results[i];
					relationsInfo.add(SearchRuntimeAdapter.createRelationshipRecord(result.getWord()));               
				}
				// filter by type info
				postFilter();
			}
		} catch (Exception e) {
			ModelerCore.Util.log(e);
			return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, ModelerCore.Util.getString("FindRelationshipsCommandImpl.Error_trying_to_execute_command,_problem_trying_to_query_relationship_search_indexes._1"), e); //$NON-NLS-1$
		}
		return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
	}

	/*
	 * Get the Search pattern based on the case sensitivity and name.
	 */
	private String getNameSearchPattern() {
		String pattern = null;
		
		if(this.namePattern == null) {
			this.namePattern = CoreStringUtil.Constants.EMPTY_STRING;
		}

		if(this.caseSensitive) {
			pattern = ""+IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP  //$NON-NLS-1$
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING // uuid
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ this.namePattern
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
		} else {
			pattern = ""+IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP  //$NON-NLS-1$
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING // uuid
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER 
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING // name
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ this.namePattern.toUpperCase()
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING // upper name
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING // object UUID of relationship type
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING // name of relationship type
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING // relationship uri
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER
					+ IndexConstants.RECORD_STRING.MATCH_CHAR_STRING // path to the relationship model
					+ IndexConstants.RECORD_STRING.RECORD_DELIMITER;
		}
		
		return pattern;
	}

    /**
     * Filter the relationship results based on the relationship type, resources participating
     * in the relationship information.
     */
    public void postFilter() {
        Collection filteredResults = new HashSet();
        // for each relationship record, check the type
        Relationships:
        for (final Iterator iter = this.relationsInfo.iterator();iter.hasNext();) {
            RelationshipRecord record = (RelationshipRecord)iter.next();
			// check if the resource of the object is in scope
			if(this.relationshipScope != null) {
			    // check if the relationship record is in scope
            	for(Iterator relIter = this.relationshipScope.iterator();relIter.hasNext();) {
            	    ModelWorkspaceItem item = (ModelWorkspaceItem) relIter.next();
            		String resourcePath = item.getPath().toString();
					if(!record.getResourcePath().equals(resourcePath)) {
					    continue;
					}
            	}
			}
            if (!CoreStringUtil.isEmpty(this.relationshipTypeName)) {
                FindRelationshipTypesCommand command = new FindRelationshipTypesCommandImpl();
				command.setIndexSelector(this.selector);
                command.setRelationshipTypeName(this.relationshipTypeName);
                command.setIncludeSubTypes(this.includeSubtypes);
                
                // executes the command
				IStatus status = command.execute();
				//Filter relationship types that match the criteria
				Collection typeInfo = null;
                // get the types if execution is successful
                if (status == null || status.isOK()) {
                    typeInfo = command.getRelationShipTypeInfo();   
                } else {
					continue;
                }
                
                // collect relationships whose types match the searched types
                boolean foundMatch = false;
                for (final Iterator typeIter = typeInfo.iterator();typeIter.hasNext();) {
                    RelationshipTypeRecord typeRecord = (RelationshipTypeRecord)typeIter.next();
                    if (record.getTypeName().equals(typeRecord.getName())) {
						foundMatch = true;
						break;
                    }
                }
                if(!foundMatch) {
                	continue;
                }
            }

            // filter based on participant list
            if(this.participantList != null) {
				FindRelatedObjectsCommand command = new FindRelatedObjectsCommandImpl();
				command.setIndexSelector(this.selector);

				// executes the command
				IStatus status = command.execute();
				//Filter relationship types that match the criteria
				Collection relInfo = null;
				// get the types if execution is successful
				if (status == null || status.isOK()) {
					relInfo = command.getRelatedObjectInfo();
				}
				// check if the resource of the object or in related object in participant scope				
				if(this.participantList != null) {
	            	for(Iterator partIter = this.participantList.iterator();partIter.hasNext();) {
	            		String participantPath = (String) partIter.next();
						// collect relationships whose types match the searched types
						boolean foundMatch = false;					
						for (final Iterator relIter = relInfo.iterator();relIter.hasNext();) {
							RelatedObjectRecord relRecord = (RelatedObjectRecord)relIter.next();
							if (record.getUUID().equals(relRecord.getRelationshipUUID())) {
								if(relRecord.getResourcePath().equals(participantPath) 
								  || relRecord.getRelatedResourcePath().equals(participantPath)) {
									foundMatch = true;
									break;
								}
							}
						}
						if(!foundMatch) {
							continue Relationships;
						}
	            	}
				}				
            }

            // record satisfies all the filter conditions
            // collect it
			filteredResults.add(record);            
        }
        relationsInfo.clear();
        relationsInfo.addAll(filteredResults);
    }

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.commands.FindRelationshipsCommand#getRelationShipInfo()
	 */
	@Override
	public Collection getRelationShipInfo() {
		return this.relationsInfo;
	}

    /* (non-Javadoc)
     * @See org.teiid.designer.core.search.commands.FindRelationshipsCommand#setCaseSensitive(boolean)
     */
    @Override
	public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.search.commands.FindRelationshipsCommand#setNamePattern(java.lang.String)
     */
    @Override
	public void setNamePattern(String namePattern) {
        this.namePattern = namePattern;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.search.commands.FindRelationshipsCommand#setParticipantList(java.util.List)
     */
    @Override
	public void setParticipantList(List participantList) {
        this.participantList = participantList;
    }

    /** 
     * @see org.teiid.designer.core.search.commands.FindRelationshipsCommand#setScopeResourceList(java.util.List)
     * @since 4.2
     */
    @Override
	public void setRelationshipResourceScopeList(List scopeResourceList) {
        this.relationshipScope = scopeResourceList;        
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.search.commands.FindRelationshipsCommand#setIncludeSubtypes(boolean)
     */
    @Override
	public void setIncludeSubtypes(boolean includeSubtypes) {
        this.includeSubtypes = includeSubtypes;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.search.commands.FindRelationshipsCommand#setRelationshipTypeName(String)
     */
    @Override
	public void setRelationshipTypeName(String relationshipTypeName) {
        this.relationshipTypeName = relationshipTypeName;
    }
   
	/* (non-Javadoc)
	 * @See org.teiid.designer.core.search.commands.FindRelationshipsCommand#setRelationshipUUID(java.lang.String)
	 */
	@Override
	public void setRelationshipUUID(String uuid) {
		this.relationshipUUID = uuid;
	}

}
