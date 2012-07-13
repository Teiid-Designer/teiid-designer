/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.teiid.core.id.ObjectID;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IEntryResult;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.core.index.ModelWorkspaceSearchIndexSelector;
import org.teiid.designer.core.resource.xmi.MtkXmiResourceImpl;
import org.teiid.designer.core.search.runtime.SearchRuntimeAdapter;


/**
 * FindRelatedObjectsCommandImpl.java
 */
public class FindRelatedObjectsCommandImpl implements FindRelatedObjectsCommand {

	private String modelObjectUri;

	private Collection relatedObjInfo;

	private IndexSelector selector;

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.commands.RelationshipSearchCommand#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return true;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.commands.RelationshipSearchCommand#execute()
	 */
	@Override
	public IStatus execute() {

		// uuid of the object whose related record is lookedup
		String objectUUID = null;

		// find the uuid of the object
		if(!CoreStringUtil.isEmpty(this.modelObjectUri)) {
			URI objectUri = URI.createURI(this.modelObjectUri);
			// fragment of the uri is an UUID
			objectUUID = objectUri.fragment();
			// get the correct form of uuid(replace / with : delimiter)
			objectUUID = objectUUID.replace(MtkXmiResourceImpl.UUID_PROTOCOL_DELIMITER, ObjectID.DELIMITER);
		}

		// search the indexes based on the UUID of the object 
		String searchPrefix = IndexUtil.getPrefixPattern(IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT, objectUUID);
		// get all related node info based of the UUID of the object
		try {
			IEntryResult[] results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), searchPrefix.toCharArray(), true, true);
			relatedObjInfo = new ArrayList(results.length);
			for(int i=0; i < results.length; i++) {
				IEntryResult result = results[i];
				relatedObjInfo.add(SearchRuntimeAdapter.createRelatedObjectRecord(result.getWord()));
			}
		} catch (Exception e) {
			ModelerCore.Util.log(e);
			return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, ModelerCore.Util.getString("FindRelatedObjectsCommandImpl.Error_trying_to_execute_command,_problem_trying_to_query_relationship_search_indexes._1"), e); //$NON-NLS-1$
		}

		return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.commands.FindRelatedObjectsCommand#getRelatedObjectInfo()
	 */
	@Override
	public Collection getRelatedObjectInfo() {
		if(this.relatedObjInfo == null) {
			return Collections.EMPTY_LIST;			
		}
		return this.relatedObjInfo;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.commands.FindRelatedObjectsCommand#setModelObjectUri(String)
	 */
	@Override
	public void setModelObjectUri(String object) {
		CoreArgCheck.isNotEmpty(object);
		this.modelObjectUri = object;
	}

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

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.search.commands.FindRelatedObjectsCommand#setIndexSelector(org.teiid.designer.core.index.IndexSelector)
	 */
	@Override
	public void setIndexSelector(IndexSelector selector) {
		this.selector = selector;
	}

}
