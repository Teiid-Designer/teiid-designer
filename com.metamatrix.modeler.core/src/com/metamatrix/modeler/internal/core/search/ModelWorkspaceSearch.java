/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.core.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.index.IEntryResult;
import com.metamatrix.core.index.IQueryResult;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.search.runtime.ReferencesRecord;
import com.metamatrix.modeler.core.search.runtime.ResourceImportRecord;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.core.search.runtime.ResourceRecord;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceSearchIndexSelector;
import com.metamatrix.modeler.internal.core.search.runtime.SearchRuntimeAdapter;

/**
 * ModelWorkspaceSearch
 */
public class ModelWorkspaceSearch {
    private static final IQueryResult[] EMTPY_QUERY_RESULT_ARRAY = new IQueryResult[0];
	private static final IEntryResult[] EMTPY_ENTRY_RESULT_ARRAY = new IEntryResult[0];
    private IndexSelector selector;
    private IProgressMonitor monitor;
    private int iIncrement = -1;
    private static final int NOMINAL_WORK = 100;
    private static final int START_UNITS = 10;


    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of ModelWorkspaceSearch.
     */
    public ModelWorkspaceSearch() {}

    // jhNOTE:  essential fix to pass monitor
    public ModelWorkspaceSearch( IProgressMonitor monitor ) {
        this.monitor = monitor;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

	/**
	 * Return an array of IPath instances corresponding to the workspace relative
	 * paths to all model resources containing an EObject with the specified identifier.
	 */
	public IPath[] getResourcesContainingObjectId(final ObjectID id) {
		return this.getResourcesContainingObjectId(id.toString());
	}

    /**
     * Return an array of IPath instances corresponding to the workspace relative
     * paths to all model resources containing an EObject with the specified identifier.
     */
    public IPath[] getResourcesContainingObjectId(final String id) {
		String prefix = IndexUtil.getPrefixPattern(IndexConstants.SEARCH_RECORD_TYPE.OBJECT, id);
		IQueryResult[] results = this.queryPrefix(prefix);
        final ArrayList tmp = new ArrayList(results.length);
        for (int i = 0; i < results.length; i++) {
            IQueryResult result = results[i];
            String resultPath   = result.getPath();
            int beginIndex = resultPath.indexOf(IPath.SEPARATOR);
            if (beginIndex > 0) {
                resultPath = resultPath.substring(beginIndex);
            }
            IPath path = new Path(resultPath);
            tmp.add(path);
        }

        IPath[] paths = new IPath[tmp.size()];
        tmp.toArray(paths);

        return paths;
    }

	/**
	 * Return an array of IPath instances corresponding to the workspace relative
	 * paths to all resources containing an EObject with the specified identifier.
	 */
	public IPath[] getResourcesWithId(final ObjectID id) {
		return this.getResourcesWithId(id.toString());
	}

	/**
	 * Return an array of IPath instances corresponding to the workspace relative
	 * paths to all model resources containing an EObject with the specified identifier.
	 */
	public IPath[] getResourcesWithId(final String id) {

        String prefix = IndexUtil.getPrefixPattern(IndexConstants.SEARCH_RECORD_TYPE.RESOURCE, id);
		IEntryResult[] results = this.queryWithPrefix(prefix);
		final ArrayList tmp = new ArrayList(results.length);
		for (int i = 0; i < results.length; i++) {
			IEntryResult result = results[i];
			ResourceRecord record   = (ResourceRecord) SearchRuntimeAdapter.getSearchRecord(result.getWord());
			tmp.add(new Path(record.getPath()));
		}

		IPath[] paths = new IPath[tmp.size()];
		tmp.toArray(paths);

		return paths;
	}

	/**
	 * Return a collection of {@link com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord}
	 * objects.
	 * @return Collection of records containing info about eobjects in the workspace
	 */
	public Collection getAllModelObjectRecords() {
        // comment this out so we only use the monitoring in IndexUtil...
        initMonitor();  // Better, set it to UNKNOWN and see what happens.

		String prefix = IndexUtil.getPrefixPattern(IndexConstants.SEARCH_RECORD_TYPE.OBJECT, null);
        IEntryResult[] results = null;

        if ( monitor != null ) {
            results = this.queryWithPrefix(prefix, monitor);
        } else {
            results = this.queryWithPrefix(prefix);
        }

        Collection objRecords = new HashSet(results.length);

        int iRecordCount = results.length;

        for (int i = 0; i < iRecordCount; i++) {
            IEntryResult result = results[i];
            ResourceObjectRecord record   = (ResourceObjectRecord) SearchRuntimeAdapter.getSearchRecord(result.getWord());
            objRecords.add(record);

            // update the monitor
            try {
                updateMonitor( i, iRecordCount );
            } catch ( InterruptedException ie ) {
                // this is a cancel, so quit and return null
                return null;
            }
        }

        return objRecords;
	}

    private void initMonitor()  {
        if ( monitor == null ) {
            return;
        }
        monitor.beginTask( null, NOMINAL_WORK );
    }

    private void updateMonitor( int iCurrentIndex, int iTotalRecords )
        throws InterruptedException {
        if ( monitor == null || iTotalRecords == 0) {
            return;
        }

        // just need to calc the increment once
        if ( iIncrement == -1 ) {
            int iRemainingUnits = NOMINAL_WORK - START_UNITS;
            iIncrement = ( iTotalRecords / iRemainingUnits );
            if( iIncrement == 0 )
                iIncrement = 1;
        }

        if ( iCurrentIndex % iIncrement == 0 ) {

            if (monitor.isCanceled())
                throw new InterruptedException();

            monitor.worked( iIncrement );
        }
    }

	/**
	 * Return a collection of {@link com.metamatrix.modeler.core.search.runtime.ResourceImportRecord}
	 * objects.
	 * @param path The workspace relative path of the model that is being imported.
	 * @return Collection of records containing import info for the model
	 */
	public Collection getModelsImportingResources(String folderOrFilePath) {

		// construct the pattern string
		final String prefix = "" //$NON-NLS-1$
						  + IndexConstants.SEARCH_RECORD_TYPE.MODEL_IMPORT
						  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
						  + folderOrFilePath.trim();

		IEntryResult[] results = this.queryWithPrefix(prefix);
		Collection refRecords = new HashSet(results.length);
		for (int i = 0; i < results.length; i++) {
			IEntryResult result = results[i];
			ResourceImportRecord record   = (ResourceImportRecord) SearchRuntimeAdapter.getSearchRecord(result.getWord());
			refRecords.add(record);
		}

		return refRecords;
	}

	/**
	 * Return a collection of {@link com.metamatrix.modeler.core.search.runtime.ResourceImportRecord}
	 * objects.
	 * @param path The workspace relative path of the model that is importing resources.
	 * @return Collection of records containing import info for the model
	 */
	public Collection getResourcesImportedByModel(String folderOrFilePath) {
		// construct the pattern string
		final String pattern = "" //$NON-NLS-1$
						  + IndexConstants.SEARCH_RECORD_TYPE.MODEL_IMPORT
						  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
						  + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING
						  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
						  + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING
						  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
						  + folderOrFilePath.trim()
						  + IndexConstants.RECORD_STRING.RECORD_DELIMITER;

		IEntryResult[] results = this.queryWithPattern(pattern);
		Collection refRecords = new HashSet(results.length);
		for (int i = 0; i < results.length; i++) {
			IEntryResult result = results[i];
			ResourceImportRecord record   = (ResourceImportRecord) SearchRuntimeAdapter.getSearchRecord(result.getWord());
			refRecords.add(record);
		}

		return refRecords;
	}

	/**
	 * Return a collection of {@link com.metamatrix.modeler.core.search.runtime.ReferencesRecord}
	 * objects.
	 * @param id UUID of the object that has uni-directional references to other objects
	 * @return Collection of records containing reference info
	 */
	public Collection getUniDirectionalReferencesTo(String id) {

		// construct the pattern string
		String prefix = "" //$NON-NLS-1$
						  + IndexConstants.SEARCH_RECORD_TYPE.OBJECT_REF
						  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
						  + id.trim()
		  				  + IndexConstants.RECORD_STRING.RECORD_DELIMITER;

		IEntryResult[] results = this.queryWithPrefix(prefix);
		Collection refRecords = new HashSet(results.length);
		for (int i = 0; i < results.length; i++) {
			IEntryResult result = results[i];
			ReferencesRecord record   = (ReferencesRecord) SearchRuntimeAdapter.getSearchRecord(result.getWord());
			refRecords.add(record);
		}

		return refRecords;
	}

	public Collection getUniDirectionalReferencesFrom(String id) {
		// construct the pattern string
		String pattern = "" //$NON-NLS-1$
						  + IndexConstants.SEARCH_RECORD_TYPE.OBJECT_REF
						  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
						  + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING
						  + IndexConstants.RECORD_STRING.RECORD_DELIMITER
						  + id.trim()
		  				  + IndexConstants.RECORD_STRING.RECORD_DELIMITER;

		IEntryResult[] results = this.queryWithPattern(pattern);
		Collection refRecords = new HashSet(results.length);
		for (int i = 0; i < results.length; i++) {
			IEntryResult result = results[i];
			ReferencesRecord record   = (ReferencesRecord) SearchRuntimeAdapter.getSearchRecord(result.getWord());
			refRecords.add(record);
		}

		return refRecords;
	}

    /**
     * Set the IndexSelector reference
     */
    public void setIndexSelector(final IndexSelector indexSelector) {
        this.selector = indexSelector;
    }

    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================

    protected IndexSelector getIndexSelector() {
        return getIndexSelector( null );
    }

    protected IndexSelector getIndexSelector( IProgressMonitor monitor ) {
        if (this.selector == null) {
            this.selector = createIndexSelector(monitor);
        }
        if(this.selector instanceof ModelWorkspaceSearchIndexSelector) {
            ((ModelWorkspaceSearchIndexSelector)this.selector).setMonitor(monitor);
        }
        return this.selector;
    }

    protected IndexSelector createIndexSelector(IProgressMonitor monitor) {
        return new ModelWorkspaceSearchIndexSelector(monitor);
    }

    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================

	private IEntryResult[] queryWithPrefix(final String prefix) {
        return queryWithPrefix(prefix, null);
	}

    private IEntryResult[] queryWithPrefix(final String prefix, IProgressMonitor monitor) {
        try {

            Index[] indexes = null;
            if (monitor != null) {
                indexes = this.getIndexSelector( monitor ).getIndexes();
            } else {
                indexes = this.getIndexSelector().getIndexes();
            }

            return  IndexUtil.queryIndex(monitor, indexes, prefix.toCharArray(), true, false);
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(IStatus.ERROR,e,e.getMessage());
        } catch (IOException e) {
            ModelerCore.Util.log(IStatus.ERROR,e,ModelerCore.Util.getString("ModelWorkspaceSearch.Error_trying_to_index_files_from_selector_1")); //$NON-NLS-1$
        }

        return EMTPY_ENTRY_RESULT_ARRAY;
    }

	private IEntryResult[] queryWithPattern(final String pattern) {
		try {
			Index[] indexes = this.getIndexSelector().getIndexes();
			return IndexUtil.queryIndex(indexes, pattern.toCharArray(), false, false);
		} catch (ModelerCoreException e) {
			ModelerCore.Util.log(IStatus.ERROR,e,e.getMessage());
		} catch (IOException e) {
			ModelerCore.Util.log(IStatus.ERROR,e,ModelerCore.Util.getString("ModelWorkspaceSearch.Error_trying_to_index_files_from_selector_1")); //$NON-NLS-1$
		}

		return EMTPY_ENTRY_RESULT_ARRAY;
	}

    private IQueryResult[] queryPrefix(final String prefix) {
        try {
            Index[] indexes = this.getIndexSelector().getIndexes();
            return this.queryPrefix(indexes,prefix.toCharArray());
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(IStatus.ERROR,e,e.getMessage());
        } catch (IOException e) {
            ModelerCore.Util.log(IStatus.ERROR,e,ModelerCore.Util.getString("ModelWorkspaceSearch.Error_trying_to_index_files_from_selector_1")); //$NON-NLS-1$
        }
        return EMTPY_QUERY_RESULT_ARRAY;
    }

    private IQueryResult[] queryPrefix(final Index[] indexes, final char[] prefix) throws ModelerCoreException {
        final ArrayList queryResult = new ArrayList();

        try {
            for (int i = 0; i < indexes.length; i++) {

                IQueryResult[] partialResult = indexes[i].queryPrefix(prefix);
                if (partialResult != null && partialResult.length > 0) {
                    queryResult.addAll(Arrays.asList(partialResult));
                }
            }
        } catch(IOException e) {
            Object[] params = new Object[]{new String(prefix)};
            throw new ModelerCoreException(e, ModelerCore.Util.getString("ModelWorkspaceSearch.Error_trying_to_query_index_files_for_prefix_2",params));  //$NON-NLS-1$
        }

        IQueryResult[] result = new IQueryResult[queryResult.size()];
        queryResult.toArray(result);

        return result;
    }

}
