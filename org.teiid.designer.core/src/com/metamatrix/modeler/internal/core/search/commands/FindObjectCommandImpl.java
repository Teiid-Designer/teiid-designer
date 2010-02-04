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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.index.IEntryResult;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.search.MetadataSearch;
import com.metamatrix.modeler.core.search.commands.FindObjectCommand;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;
import com.metamatrix.modeler.core.search.runtime.TypedObjectRecord;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceSearchIndexSelector;
import com.metamatrix.modeler.internal.core.search.runtime.SearchRuntimeAdapter;

/**
 * FindRelationshipsCommandImpl.java
 */
public class FindObjectCommandImpl implements FindObjectCommand {
    
    private EClass metaClass;
    
    private String featureName;
    private String textPattern;
    private boolean containsPattern;

	private Collection recordInfos = Collections.EMPTY_LIST;

    private IndexSelector selector;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    
    /** 
     * 
     * @since 4.1
     */
    public FindObjectCommandImpl() {
        super();
        this.metaClass = null;
        this.featureName = null;
        this.textPattern = null;
        this.containsPattern = true;
        this.selector = null;
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.modeler.core.search.commands.FindTypedObjectCommand#getRecordInfo()
     * @since 4.1
     */
    public Collection getRecordInfo() {
        return this.recordInfos;
    }

    /** 
     * @see com.metamatrix.modeler.core.search.commands.FindObjectCommand#setFeatureCriteria(java.lang.String, java.lang.String, boolean, boolean)
     * @since 4.1
     */
    public void setFeatureCriteria(final String featureName,
                                   final String textPattern,
                                   final boolean containsPattern) {
        this.featureName = featureName;
        this.textPattern = textPattern;
        this.containsPattern = containsPattern;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.search.commands.FindObjectCommand#setMetaClass(org.eclipse.emf.ecore.EClass)
     * @since 4.1
     */
    public void setMetaClass(final EClass metaClass) {
        this.metaClass = metaClass;
    }
    
    /**
     * @see com.metamatrix.modeler.core.search.commands.FindRelationshipsCommand#setIndexSelector(com.metamatrix.modeler.core.index.IndexSelector)
     */
    public void setIndexSelector(final IndexSelector selector) {
        this.selector = selector;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.search.commands.SearchCommand#canExecute()
     * @since 4.1
     */
    public boolean canExecute() {
        return (this.isMetaclassSearch() || this.isFeatureSearch());
    }
    
    private IStatus executeObjectUriSearch() {
        IStatus result = null;

        try {
            Object obj = ModelerCore.getModelContainer().getEObjectFinder().find(this.textPattern);
            
            if ((obj != null) && (obj instanceof EObject)) {
                this.recordInfos = Collections.singletonList(SearchRuntimeAdapter.createResourceObjectRecord((EObject)obj));
            }

            // ok result
            result = new Status(IStatus.OK, ModelerCore.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
        } catch (CoreException theException) {
            ModelerCore.Util.log(theException);
            String msg = ModelerCore.Util.getString("FindObjectCommandImpl.objectUriSearchError", this.textPattern); //$NON-NLS-1$
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, IStatus.OK, msg, theException);
        }

        return result;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.search.commands.SearchCommand#execute()
     * @since 4.1
     */
    public IStatus execute() {
        if (!canExecute()) {
            return null;
        }
        
        if (isFeatureSearch() && this.featureName.equals(MetadataSearch.OBJECT_URI_FEATURE)) {
            return executeObjectUriSearch();
        }

        try {
            String metaclassUri = getMetaclassUri(metaClass);           
            String matchPattern = this.getMatchPattern(this.featureName, this.textPattern, metaclassUri);
            IEntryResult[] results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), 
                                                          matchPattern.toCharArray(), 
                                                          IndexConstants.RECORD_STRING.RECORD_DELIMITER);
            this.recordInfos = new ArrayList(results.length);
            for(int i=0; i < results.length; i++) {
                IEntryResult result = results[i];
                this.recordInfos.add(SearchRuntimeAdapter.getSearchRecord(result.getWord()));
            }

            // If we are trying to exclude matching records ...
            if (this.isFeatureSearch() && !this.containsPattern) {
                if (this.featureName.equals(MetadataSearch.DESCRIPTION_SEARCH_FEATURE)) {
                    // defect 15660: if we are using the description field for exclusion, we want ALL objects back;
                    // therefore, we retrieve all objects by using the name feature and any-string text pattern
                    matchPattern = this.getMatchPattern(MetadataSearch.NAME_SEARCH_FEATURE, MetadataSearch.TEXT_PATTERN_ANY_STRING, metaclassUri);            
                } else {
                    matchPattern = this.getMatchPattern(this.featureName, IndexConstants.RECORD_STRING.MATCH_CHAR_STRING, metaclassUri);
                }
                results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), 
                                               matchPattern.toCharArray(), 
                                               IndexConstants.RECORD_STRING.RECORD_DELIMITER);
                
                // Store the results of the query without matching criteria 
                final Map tmp = new HashMap(results.length);
                for(int i=0; i < results.length; i++) {
                    IEntryResult result = results[i];
                    SearchRecord record = SearchRuntimeAdapter.getSearchRecord(result.getWord());
                    String mapKey = getMapKey(record);
                    if (mapKey != null) {
                        tmp.put(mapKey, record);
                    }
                }
                
                // Minus the result of the query with matching criteria 
                for (final Iterator iter = this.recordInfos.iterator(); iter.hasNext();) {
                    SearchRecord record = (SearchRecord)iter.next();
                    String mapKey = getMapKey(record);
                    if (tmp.containsKey(mapKey)) {
                        tmp.remove(mapKey);
                    }
                }
                this.recordInfos = tmp.values();

            }
            
        } catch (Exception e) {
            ModelerCore.Util.log(e);
            final String msg = ModelerCore.Util.getString("FindObjectCommandImpl.Error_trying_to_execute_command"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID,0,msg,e);
        }
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
    }

	/*
	 * Get the match pattern based on the specified criteria used to search 
     * for matching index records
     */
	private String getMatchPattern(final String feature, final String text, final String metaClassUri) {
		String pattern = null;

        //-------------------------------------------------------------------------------
        //                   If searching by description ...
        // recordType|objectID|name|fullname|uri|tags|description|modelPath|metaclassURI|
        //-------------------------------------------------------------------------------
        if (feature != null && feature.equals(MetadataSearch.DESCRIPTION_SEARCH_FEATURE)) {
            String descCriteria = (text != null && text.length() != 0) ? 
                                   text.toUpperCase() : 
                                   IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
            
            final String metaclassCriteria = (metaClassUri != null && metaClassUri.length() != 0) ? 
                                              metaClassUri : 
                                              IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
            
            pattern = StringUtil.Constants.EMPTY_STRING 
                    + IndexConstants.SEARCH_RECORD_TYPE.ANNOTATION    // recordType
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // objectID
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // name
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // fullname
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // uri
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // tags
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + descCriteria                                    // description
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // modelPath
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + metaclassCriteria                               // metaclassURI
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER;
        } 
        
        //-------------------------------------------------------------------------
        //                   If searching by name or uuid ...
        // recordType|objectID|upperName|name|fullname|uri|modelPath|metaclassURI|
        //-------------------------------------------------------------------------
        else {
            String nameCriteria = IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
            if (feature != null && feature.equals(MetadataSearch.NAME_SEARCH_FEATURE)) {
                nameCriteria = (text != null && text.length() != 0) ? 
                                text.toUpperCase() : 
                                IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
            }

            String uuidCriteria = IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
            if (feature != null && feature.equals(MetadataSearch.UUID_SEARCH_FEATURE)) {
                uuidCriteria = (text != null && text.length() != 0) ? 
                                text : 
                                IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
            }
            
            final String metaclassCriteria = (metaClassUri != null && metaClassUri.length() != 0) ? 
                                              metaClassUri : 
                                              IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
            
            pattern = StringUtil.Constants.EMPTY_STRING 
                    + IndexConstants.SEARCH_RECORD_TYPE.OBJECT        // recordType
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + uuidCriteria                                    // objectID
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + nameCriteria                                    // uppername
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // name
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // fullname
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // uri
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // modelPath
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                    + metaclassCriteria                               // metaclassURI
                    + IndexConstants.RECORD_STRING.RECORD_DELIMITER;
        }

		return pattern;
	}
    
    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================

	private boolean isFeatureSearch() {
        return (this.featureName != null && this.textPattern != null && this.textPattern.length() > 0);
    }
    
    private boolean isMetaclassSearch() {
        return (this.metaClass != null);
    }
    
    private IndexSelector getIndexSelector() {
        // selector used to lookup relationship indexes     
        this.selector = this.selector != null ?
                                       this.selector :
                                       new ModelWorkspaceSearchIndexSelector();
        return this.selector;
    }

    private static String getMetaclassUri(final EClass eClass) {
        if (eClass != null) {
            return ModelerCore.getModelEditor().getUri(eClass).toString();
        }
        return null;
    }
    
    private String getMapKey(final SearchRecord record) {
        if (record != null) {
            String key = record.getUUID();
            if (key != null) {
                return key;
            } else if (record instanceof ResourceObjectRecord) {
                key = ((ResourceObjectRecord)record).getObjectURI();
                return key;
            } else if (record instanceof TypedObjectRecord) {
                key = ((TypedObjectRecord)record).getObjectURI();
                return key;
            }
        }
        return null;
    }

}
