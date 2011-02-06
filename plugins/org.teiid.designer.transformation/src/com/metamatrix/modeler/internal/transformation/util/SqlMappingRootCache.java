/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.query.sql.lang.Command;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.refactor.ModelResourceCollectorVisitor;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.validation.SqlTransformationResult;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;

/**
 * SqlMappingRootCache
 */
public class SqlMappingRootCache implements SqlConstants {

    // Caches for each command type
    private static HashMap selectSqlCache = new HashMap();
    private static HashMap insertSqlCache = new HashMap();
    private static HashMap updateSqlCache = new HashMap();
    private static HashMap deleteSqlCache = new HashMap();

    /** List of listeners registered for cache events */
    private static List eventListeners;

    private static final SqlMappingRootCache INSTANCE = new SqlMappingRootCache();

    /**
     * Get the SqlMappingRootCache instance for this VM.
     * @return the singleton instance for this VM; never null
     */
    public static SqlMappingRootCache getInstance() {
        return INSTANCE;
    }
    /**
     * Invalidate the entire cache for all command types and all transformation mappings roots 
     */
    public static void invalidateCache() {
        getCache(QueryValidator.SELECT_TRNS).clear();
        getCache(QueryValidator.INSERT_TRNS).clear();
        getCache(QueryValidator.UPDATE_TRNS).clear();
        getCache(QueryValidator.DELETE_TRNS).clear();
    }

    /**
     * Invalidate the SELECT Status, if it exists in the cache.  This means that the next
     * time status is requested, the query will need to be parsed/resolved/validated.
     */
    public static void invalidateSelectStatus(final Object transMappingRoot, final boolean notifyListeners, final Object source) {
        invalidateSelectStatus(transMappingRoot, notifyListeners, source, false);
    }

    /**
     * Invalidate the SELECT Status, if it exists in the cache.  This means that the next
     * time status is requested, the query will need to be parsed/resolved/validated.
     */
    public static void invalidateSelectStatus(final Object transMappingRoot, final boolean notifyListeners, final Object source, boolean overwriteDirty) {
        invalidateStatus(transMappingRoot,QueryValidator.SELECT_TRNS,notifyListeners,source,overwriteDirty);
    }

    /**
     * Invalidate the INSERT Status, if it exists in the cache.  This means that the next
     * time status is requested, the query will need to be parsed/resolved/validated.
     */
    public static void invalidateInsertStatus(final Object transMappingRoot, final boolean notifyListeners, final Object source) {
        invalidateStatus(transMappingRoot,QueryValidator.INSERT_TRNS,notifyListeners,source,false);
    }

    /**
     * Invalidate the UPDATE Status, if it exists in the cache.  This means that the next
     * time status is requested, the query will need to be parsed/resolved/validated.
     */
    public static void invalidateUpdateStatus(final Object transMappingRoot, final boolean notifyListeners, final Object source) {
        invalidateStatus(transMappingRoot,QueryValidator.UPDATE_TRNS,notifyListeners,source,false);
    }

    /**
     * Invalidate the DELETE Status, if it exists in the cache.  This means that the next
     * time status is requested, the query will need to be parsed/resolved/validated.
     */
    public static void invalidateDeleteStatus(final Object transMappingRoot, final boolean notifyListeners, final Object source) {
        invalidateStatus(transMappingRoot,QueryValidator.DELETE_TRNS,notifyListeners,source,false);
    }
    
    public static void invalidateStatus(final Object transMappingRoot, final boolean notifyListeners, final Object source) {
    	invalidateStatus(transMappingRoot,QueryValidator.SELECT_TRNS,notifyListeners,source, false);
    	invalidateStatus(transMappingRoot,QueryValidator.UPDATE_TRNS,notifyListeners,source, false);
    	invalidateStatus(transMappingRoot,QueryValidator.INSERT_TRNS,notifyListeners,source, false);
        invalidateStatus(transMappingRoot,QueryValidator.DELETE_TRNS,notifyListeners,source, false);
    }

    /**
     * Invalidate any Status that uses the provided sourceGroup.  This means that the next
     * time status is requested, the query will need to be parsed/resolved/validated.
     */
    public static void invalidateRootsWithSourceGroups(final Set sourceGroups) {
        // Go thru all the cached select MappingRoots
        
        HashMap currentSelectSqlCache = new HashMap(getCache(QueryValidator.SELECT_TRNS));

        Iterator selectIter = currentSelectSqlCache.keySet().iterator();

        while (selectIter.hasNext()) {
            EObject selectRoot = (EObject)selectIter.next();
            boolean invalidateRoot = false;
            Iterator grpIter = sourceGroups.iterator();
            while(grpIter.hasNext()) {
                Object sourceGroup = grpIter.next();
                if(TransformationHelper.isSqlProcedureResultSet(sourceGroup)) {
                    sourceGroup = TransformationHelper.getSqlProcedureForResultSet(sourceGroup);
                }
                if(containsStatus(selectRoot,QueryValidator.SELECT_TRNS)) {
                    SqlTransformationResult selectStatus = getStatus(selectRoot,QueryValidator.SELECT_TRNS,false);
                    if(selectStatus != null && selectStatus.hasSourceGroup(sourceGroup)) {
                        invalidateRoot = true;
                        break;
                    }
                    if(containsStatus(selectRoot,QueryValidator.INSERT_TRNS)) {
                        SqlTransformationResult insertStatus = getStatus(selectRoot,QueryValidator.INSERT_TRNS,false);
                            if(insertStatus != null && insertStatus.hasSourceGroup(sourceGroup)) {
                                invalidateRoot = true;
                                break;
                            }
                        }
                    if(containsStatus(selectRoot,QueryValidator.UPDATE_TRNS)) {
                        SqlTransformationResult updateStatus = getStatus(selectRoot,QueryValidator.UPDATE_TRNS,false);
                            if(updateStatus != null && updateStatus.hasSourceGroup(sourceGroup)) {
                                invalidateRoot = true;
                                break;
                            }
                        }
                    if(containsStatus(selectRoot,QueryValidator.DELETE_TRNS)) {
                        SqlTransformationResult deleteStatus = getStatus(selectRoot,QueryValidator.DELETE_TRNS,false);
                            if(deleteStatus != null && deleteStatus.hasSourceGroup(sourceGroup)) {
                                invalidateRoot = true;
                                break;
                            }
                        }
                    }
                }
            if(invalidateRoot) {
                invalidateSelectStatus(selectRoot,true,null);
            }
        }
    }

    /**
     * Invalidate any Status that has any of the provided groups as its target.  This means that 
     * the next time status is requested, the query will need to be parsed/resolved/validated.
     */
    public static void invalidateRootsWithTargetGroups(final Set groups) {
        // Go thru all the cached select MappingRoots

        HashMap currentSelectSqlCache = new HashMap(getCache(QueryValidator.SELECT_TRNS));
        Iterator selectIter = currentSelectSqlCache.keySet().iterator();
        while (selectIter.hasNext()) {
            EObject mappingRoot = (EObject)selectIter.next();
            if(containsStatus(mappingRoot,QueryValidator.SELECT_TRNS)) {
                EObject targetGrp = TransformationHelper.getTransformationLinkTarget(mappingRoot);
                if(groups!=null && groups.contains(targetGrp)) {
                    invalidateSelectStatus(mappingRoot,true,null);
                }
            }
        }
    }

    /**
     * Invalidate any cached status that is for a mappingRoot within the supplied project.
     * @param proj the supplied project.
     */
    public static void invalidateCacheForProject(final IProject proj) {
    	if(proj!=null && proj.isOpen()) {
    		// Get the ModelResources within the supplied project
    		List modelResources = new ArrayList();
            ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();   
			try {
				proj.getProject().accept(visitor);
				modelResources.addAll(visitor.getModelResources());
			} catch (CoreException e) {
				// If unable to get ModelResources, just return.
				return;
			}
    		
	        // Iterate thru all the cached SELECT MappingRoots
	        HashMap currentSelectSqlCache = new HashMap(getCache(QueryValidator.SELECT_TRNS));
	        Iterator selectIter = currentSelectSqlCache.keySet().iterator();
	        while (selectIter.hasNext()) {
	            EObject mappingRoot = (EObject)selectIter.next();
            	// If mapping root is within a model in the supplied project, invalidate it.
            	// Invalidating the SELECT, will cause INS/UPD/DEL to invalidate also.
                ModelResource mdlRsrc = ModelerCore.getModelEditor().findModelResource(mappingRoot);
                if (mdlRsrc != null && mdlRsrc.exists() && modelResources.contains(mdlRsrc)) {
                    invalidateSelectStatus(mappingRoot,true,null);
                }
	        }
    	}
    }

    /**
     * This method invalidates mappingRoots upon closing a project or removing a model from
     * the workspace.  All of the cached mapping roots are checked - if the target table or
     * any of the source table resource cannot be found, the mapping root status is invalidated.
     */
    public static void invalidateRootsOnProjectOrModelRemove() {
        // Go thru all the cached select MappingRoots
        HashMap currentSelectSqlCache = new HashMap(getCache(QueryValidator.SELECT_TRNS));

        Iterator selectIter = currentSelectSqlCache.keySet().iterator();

        while (selectIter.hasNext()) {
            EObject selectRoot = (EObject)selectIter.next();
            EObject realEObj = null;
            if( selectRoot != null ) {
                // Defect 19658 - This code was getting the URI for a root where the eContainer == NULL and the
                // URI was coming back "#//" and getEObject() was being called with this eObject causing
                // an StringIndexOutOfBoundsException.  Simply checking the eResource() is sufficient
                // to verify if the root is out of scope (i.e. unloaded/closed/removed from workspace)
                if( selectRoot.eResource() != null ) 
                    realEObj = selectRoot;
//                
//                URI uri = ModelerCore.getModelEditor().getUri(selectRoot);
//                try {
//                    realEObj = ModelerCore.getModelContainer().getEObject(uri, false);
//                } catch (CoreException err) {
//                    err.printStackTrace();
//                }
            }
            
            ModelResource mdlRsrc =  null;
            
            boolean invalidateRoot = false;
            if (realEObj == null) {
                invalidateRoot = true;
            } else {
                mdlRsrc = ModelerCore.getModelEditor().findModelResource(selectRoot);
                if (mdlRsrc == null || !mdlRsrc.exists()) {
                    invalidateRoot = true;

                } else if (containsStatus(selectRoot, QueryValidator.SELECT_TRNS)) {
                    SqlTransformationResult selectStatus = getStatus(selectRoot, QueryValidator.SELECT_TRNS, false);

                    if (selectStatus != null 
                     && (areAnySourceGroupsProxies(selectStatus) // this check looks for proxies without resolving them
                         || !selectStatus.areSrcGroupMdlResourcesValid())) {
                        invalidateRoot = true;
                    } else {
                        if (containsStatus(selectRoot, QueryValidator.INSERT_TRNS)) {
                            SqlTransformationResult insertStatus = getStatus(selectRoot, QueryValidator.INSERT_TRNS, false);
                            if (insertStatus != null && !insertStatus.areSrcGroupMdlResourcesValid()) {
                                invalidateRoot = true;
                            }
                        }
                        if (!invalidateRoot && containsStatus(selectRoot, QueryValidator.UPDATE_TRNS)) {
                            SqlTransformationResult updateStatus = getStatus(selectRoot, QueryValidator.UPDATE_TRNS, false);
                            if (updateStatus != null && !updateStatus.areSrcGroupMdlResourcesValid()) {
                                invalidateRoot = true;
                            }
                        }
                        if (!invalidateRoot && containsStatus(selectRoot, QueryValidator.DELETE_TRNS)) {
                            SqlTransformationResult deleteStatus = getStatus(selectRoot, QueryValidator.DELETE_TRNS, false);
                            if (deleteStatus != null && !deleteStatus.areSrcGroupMdlResourcesValid()) {
                                invalidateRoot = true;
                            }
                        }
                    }
                }
            }
            if (invalidateRoot) {
                invalidateSelectStatus(selectRoot, true, null);
            }
        } // endwhile
    }

    /** Scan the source groups, looking for proxies, without actually
      *  resolving the proxies.
      *  
      * @param status The status to use.
      * @return true if eIsProxy returns true for any source group.
      */
    private static boolean areAnySourceGroupsProxies(SqlTransformationResult status) {
    	// We need to wrap this in a NON_UNDOABLE transaction here to cover problems associated with re-loading
    	// EMF resources which are creating CompoundCommand Undoables...
    	Object[] srcGroups = null;
    	
        boolean started = ModelerCore.startTxn(false, false, "SqlMappingRootCache_ProxyCheck", status); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	srcGroups= status.getSourceGroups().toArray();
        	succeeded = true;
        }finally {
            if ( started ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        
        for (int i = 0; i < srcGroups.length; i++) {
            EObject eoj = (EObject) srcGroups[i];
            if (eoj.eIsProxy()) {
                return true;
            } // endif
        } // endfor

        return false;
    }

    /**
     * Get the SELECT SQL Parsable State for the supplied TransformationMappingRoot.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isSelectParsable(final Object transMappingRoot) {
        return isParsable(transMappingRoot,QueryValidator.SELECT_TRNS);
    }

    /**
     * Get the SELECT SQL Resolvable State for the supplied TransformationMappingRoot.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isSelectResolvable(final Object transMappingRoot) {
        return isResolvable(transMappingRoot,QueryValidator.SELECT_TRNS);
    }

    /**
     * Get the SELECT SQL Valid State for the supplied TransformationMappingRoot.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isSelectValid(final Object transMappingRoot) {
        return isValid(transMappingRoot,QueryValidator.SELECT_TRNS);
    }

    /**
     * Get the INSERT SQL Valid State for the supplied TransformationMappingRoot.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isInsertValid(final Object transMappingRoot) {
        return isValid(transMappingRoot,QueryValidator.INSERT_TRNS);
    }

    /**
     * Get the UPDATE SQL Valid State for the supplied TransformationMappingRoot.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isUpdateValid(final Object transMappingRoot) {
        return isValid(transMappingRoot,QueryValidator.UPDATE_TRNS);
    }

    /**
     * Get the DELETE SQL Valid State for the supplied TransformationMappingRoot.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isDeleteValid(final Object transMappingRoot) {
        return isValid(transMappingRoot,QueryValidator.DELETE_TRNS);
    }

    /**
     * Get the SELECT Command language object for the supplied TransformationMappingRoot.  
     * @param transMappingRoot the transformation MappingRoot
     * @return the Command languageObject
     */
    public static Command getSelectCommand(final Object transMappingRoot) {
        return getCommand(transMappingRoot,QueryValidator.SELECT_TRNS);
    }
    
    /**
     * Get the INSERT Command language object for the supplied TransformationMappingRoot.  
     * @param transMappingRoot the transformation MappingRoot
     * @return the Command languageObject
     */
    public static Command getInsertCommand(final Object transMappingRoot) {
        return getCommand(transMappingRoot,QueryValidator.INSERT_TRNS);
    }
    
    /**
     * Get the UPDATE Command language object for the supplied TransformationMappingRoot.  
     * @param transMappingRoot the transformation MappingRoot
     * @return the Command languageObject
     */
    public static Command getUpdateCommand(final Object transMappingRoot) {
        return getCommand(transMappingRoot,QueryValidator.UPDATE_TRNS);
    }
    
    /**
     * Get the DELETE Command language object for the supplied TransformationMappingRoot.  
     * @param transMappingRoot the transformation MappingRoot
     * @return the Command languageObject
     */
    public static Command getDeleteCommand(final Object transMappingRoot) {
        return getCommand(transMappingRoot,QueryValidator.DELETE_TRNS);
    }

    /**
     * Get the SELECT Command language object for the supplied TransformationMappingRoot.  
     * @param transMappingRoot the transformation MappingRoot
     * @return the Command languageObject
     */
    public static String getSelectSql(final EObject transMappingRoot) {
        return getSqlString(transMappingRoot,QueryValidator.SELECT_TRNS);
    }

    /**
     * Determine if either the supplied user SQL is different than the cached Status SQL.  If the cached
     * status is a uuid status, they are considered different.  Otherwise a string comparison is done to determine
     * if they are different.
     * @param transMappingRoot the transformation MappingRoot
     * @param cmdType the command type (SELECT, INSERT, UPDATE, DELETE)
     * @param userSql the user SQL to compare to the cache
     * @return 'true' if the strings are different or cached is uuid, 'false' otherwise.
     */
    public static boolean isSqlDifferent(final Object transMappingRoot,final int cmdType,
                                            final String userSql) {
        boolean isDifferent = true;
        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            if(containsStatus((EObject)transMappingRoot,cmdType)) {
                // get status from the cache
                HashMap cache = getCache(cmdType);
                SqlTransformationResult status = (SqlTransformationResult)cache.get(transMappingRoot);
                
                // check whether the status is a uuid status or user status when doing comparison
                String cachedSql = status.getSqlString();
                // If uuid status, use uuidSql for the comparison
                isDifferent = stringsDifferent(userSql,cachedSql);
            }
        }
        return isDifferent;
    }

    /**
     * determine if the supplied sql Strings are different
     * @param newSql the new SQL String
     * @param oldSql the old SQL String
     * @return 'true' if strings differ, 'false' if same
     */
    private static boolean stringsDifferent(String newSql, String oldSql) {
        boolean isDifferent = true;
        if(newSql==null) {
            if(oldSql==null) {
                isDifferent = false;
            }
        } else if(oldSql!=null) {
            StringBuffer newSb = new StringBuffer(newSql.trim());
            StringBuffer oldSb = new StringBuffer(oldSql.trim());
            CoreStringUtil.replaceAll(newSb,CR,BLANK);
            CoreStringUtil.replaceAll(newSb,TAB,BLANK);
            CoreStringUtil.replaceAll(newSb,DBL_SPACE,SPACE);
            CoreStringUtil.replaceAll(oldSb,CR,BLANK);
            CoreStringUtil.replaceAll(oldSb,TAB,BLANK);
            CoreStringUtil.replaceAll(oldSb,DBL_SPACE,SPACE);
            if(newSb.toString().equals(oldSb.toString())) {
                isDifferent=false;
            }
        }
        return isDifferent;
    }

    /**
     * Invalidate the Status for the cmd type, if it exists in the cache.  This means that the next
     * time status is requested, the query will need to be parsed/resolved/validated.
     */
    private static synchronized void invalidateStatus(final Object transMappingRoot,
                                                      final int cmdType,
    		                                          final boolean notifyListeners,
                                                      Object source, 
                                                      boolean overwriteDirty) {
        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            if(containsStatus((EObject)transMappingRoot,cmdType)) {
                // Remove the status for the supplied command type
                removeStatus((EObject)transMappingRoot,cmdType);
                // If the command is a SELECT, then invalidate the others since they usually depend on SELECT
                if(cmdType==QueryValidator.SELECT_TRNS) {
                    removeStatus((EObject)transMappingRoot,QueryValidator.INSERT_TRNS);
                    removeStatus((EObject)transMappingRoot,QueryValidator.UPDATE_TRNS);
                    removeStatus((EObject)transMappingRoot,QueryValidator.DELETE_TRNS);
                }
                if(source==null) {
                    source = getInstance();
                }
            } 
            
            if(notifyListeners) {
                notifyEventListeners(new SqlTransformationStatusChangeEvent((EObject)transMappingRoot,source,overwriteDirty));
            }
        }
    }

    /**
     * Get the Parsable State for the cmdType.  If the 
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isParsable(final Object transMappingRoot,final int cmdType) {
        boolean isParsable = false;
        
        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
                isParsable = status.isParsable();
            }
        }
        return isParsable;
    }

    /**
     * Get the Resolvable State for the cmdType.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isResolvable(final Object transMappingRoot,final int cmdType) {
        boolean isResolvable = false;
        
        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
                isResolvable = status.isResolvable();
            }
        }
        return isResolvable;
    }

    /**
     * Get the Valid State for the cmdType.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isValid(final Object transMappingRoot,final int cmdType) {
        boolean isValid = false;
        
        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
                isValid = status.isValidatable();
            }
        }
        return isValid;
    }

    /**
     * Determine if the transformation has the supplied source group.
     * @param transMappingRoot the transformation MappingRoot
     * @param sourceGroup the source group to look for
     * @param cmdType the command type to look in.
     * @return 'true' if the transformation command type has the sourceGroup, 'false' if not.
     */
    public static boolean hasSourceGroup(final Object transMappingRoot,final Object sourceGroup,final int cmdType) {
        boolean hasSourceGroup = false;

        if(transMappingRoot!=null && sourceGroup!=null && 
           TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot) &&
           TransformationHelper.isSqlTable(sourceGroup) ) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
                Collection sourceGroups = status.getSourceGroups();
                if(sourceGroups.contains(sourceGroup)) {
                    hasSourceGroup = true;
                }
            }
        }
        return hasSourceGroup;
    }

    /**
     * Get the Valid State for the cmdType.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static boolean isTargetValid(final Object transMappingRoot,final int cmdType) {
        boolean isTargetValid = false;

        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
                isTargetValid = status.isTargetValid();
            }
        }
        return isTargetValid;
    }

    /**
     * Get the Valid Status for the transformation.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     */
    public static IStatus getTargetValidStatus(final Object transMappingRoot,final int cmdType) {
        IStatus targetValidStatus = null;

        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);

            if(status!=null) {
                targetValidStatus = status.getTargetValidStatus();
            }
        }
        return targetValidStatus;
    }

    /**
     * Get the Valid Status for the transformation.  If the
     * status is not in the cache, the query will be parsed/resolved/validated and put in
     * the cache for next time.
     * 
     * @param The transformation mapping root whose status is returned
     * @param cmdType The type of sql being validated
     * @param anyStatus A boolean if true returns a SqlTransformationResult which may be a status obtained
     * by validating UUID sql, else the status always contains user sql.
     * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
     * or if the whole workspace needs to be searched 
     * @param context the ValidationContext to use; may be null
     */
    public static SqlTransformationResult getSqlTransformationStatus(final SqlTransformationMappingRoot transMappingRoot, 
                                                                     final int cmdType,
                                                                     final boolean restrictSearch, final ValidationContext context) {
        CoreArgCheck.isNotNull(transMappingRoot);
        return getStatus(transMappingRoot, cmdType, restrictSearch, context);
    }

    /**
     * Get the SELECT Command language object for the supplied TransformationMappingRoot.  
     * @param transMappingRoot the transformation MappingRoot
     * @return the Command languageObject
     */
    public static Command getCommand(final Object transMappingRoot,final int cmdType) {
        Command command = null;

        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
                command = status.getCommand();
            }
        }
        return command;
    }

    /**
     * Get the External MetadataMap for the supplied TransformationMappingRoot.  
     * @param transMappingRoot the transformation MappingRoot
     * @return the external Metadata Map
     */
    public static Map getExternalMetadataMap(final Object transMappingRoot,final int cmdType) {
        Map map = null;

        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
                map = status.getExternalMetadataMap();
            }
        }
        return map;
    }

    /**
     * Get the External MetadataMap for CreateUpdateProcedure for the supplied TransformationMappingRoot.  
     * @param transMappingRoot the transformation MappingRoot
     * @return the external Metadata Map
     */
    public static Map getExternalMapForCreateUpdateProc(final Object transMappingRoot,final int cmdType) {
        Map map = null;

        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
            	Command command = status.getCommand();
            	if(command!=null && command.getType() == Command.TYPE_UPDATE_PROCEDURE) {
					map = status.getExternalMetadataMap();
            	}
            }
        }
        return map;
    }

    public static String getSqlString(final Object transMappingRoot,final int cmdType) {
        String sqlString = null;
        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get status (goes to cache first)
            SqlTransformationResult status = getStatus((EObject)transMappingRoot,cmdType,false);
            
            if(status!=null) {
                sqlString = status.getSqlString();
            }
        }
        return sqlString;
    }

    /**
     * Determine if there is a cached result for the supplied MappingRoot and command type
     * @param transMappingRoot the mappingRoot 
     * @param cmdType the command type (SELECT, INSERT, UPDATE, DELETE)
     * @return 'true' if the cache contains a result, 'false' if not.
     */
    public static boolean containsStatus(final EObject transMappingRoot,final int cmdType) {
        HashMap cache = getCache(cmdType);
        return cache.containsKey(transMappingRoot);
    }

    /**
     * Remove the cached result for the supplied MappingRoot and command type
     * @param transMappingRoot the mappingRoot 
     * @param cmdType the command type (SELECT, INSERT, UPDATE, DELETE)
     */
    private static void removeStatus(final EObject transMappingRoot,final int cmdType) {
        HashMap cache = getCache(cmdType);
        cache.remove(transMappingRoot);
    }

    /**
     * Create a status object for the mapping roots supplied commandType
     * @param transMappingRoot the transformation mapping root
     * @param cmdType The type of sql 
     * @param statusType the type of status desired (EITHER_STATUS - either uuid or user is ok, USER_STATUS - 
     * must be user status)
     * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
     * or if the whole workspace needs to be searched 
     * @param context the ValidationContext to use; may be null
     */
    private static SqlTransformationResult createStatus(final EObject transMappingRoot,final int cmdType, 
                                                          final boolean restrictSearch,
                                                          final ValidationContext context) {
        SqlTransformationResult status = null;        

        // Parse/Resolve/Validate the SQL
        if(transMappingRoot !=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // user sql string
            String userSql = null;
            // create a validator
            final TransformationValidator validator = (context != null ? 
                            new TransformationValidator((SqlTransformationMappingRoot)transMappingRoot, context, true, restrictSearch) :
                                new TransformationValidator((SqlTransformationMappingRoot)transMappingRoot, true,  restrictSearch));            
            //if this is workspace validation, validate the UUID sql if that does not validate validate the userSql
            // Any unexpected exception here creates invalid status and logs error
            try {
	                // this is non-worspace validation just validate the user sql
	                // get user Sql
	                userSql = getSqlUserString(transMappingRoot, cmdType); //getConvertedSqlString(transMappingRoot, cmdType,  restrictSearch, context);
                if(userSql != null) {
                    status = (SqlTransformationResult) validator.validateSql(userSql, cmdType, false);
                }
            } catch (Exception e) {
                String message = TransformationPlugin.Util.getString("SqlMappingRootCache.validationError"); //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.ERROR, e, message); 
                
                final IStatus fatalErrorStatus = new Status(IStatus.ERROR,TransformationPlugin.PLUGIN_ID,0,message,e);    
            	status = new SqlTransformationResult(null,fatalErrorStatus);
            	status.setParsable(false);
            	status.setResolvable(false);
            	status.setValidatable(false);
            	status.setSqlString(getSqlUserString(transMappingRoot,cmdType));
            }
        }

        return status;
    }
    

    /**
     * Add a SELECT status object for a mappingRoot
     */
    public static void setStatus(final EObject transMappingRoot,final int cmdType,final SqlTransformationResult status) {
        HashMap cache = getCache(cmdType);
        if(status!=null ) {
            cache.put(transMappingRoot,status);
        } else {
            removeStatus(transMappingRoot,cmdType);
        }
    }

    /**
     * Get the SELECT status object (new status is created if not contained in cache)
     */
    private static synchronized SqlTransformationResult getStatus(final EObject transMappingRoot, final int cmdType, 
                                                                     final boolean restrictSearch) {
        return getStatus(transMappingRoot, cmdType, restrictSearch, null);
    }
    
    /**
     * Get the status object for the provided command type (new status is created if not contained in cache)
     * @param transMappingRoot the transformation mapping root
     * @param cmdType The type of sql command
     * @param statusType the type of status desired (EITHER_STATUS - either uuid or user is ok, USER_STATUS - 
     * must be user status)
     * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
     * or if the whole workspace needs to be searched 
     * @param context the ValidationContext to use; may be null
     */
    private static synchronized SqlTransformationResult getStatus(final EObject transMappingRoot, final int cmdType, 
                                                                  final boolean restrictSearch,
                                                                  final ValidationContext context) {
        SqlTransformationResult statusResult = null;

        // If there's a cached Status, try to use it
        if(containsStatus(transMappingRoot,cmdType)) {
            
            // get status from the cache
            HashMap cache = getCache(cmdType);
            SqlTransformationResult status = (SqlTransformationResult)cache.get(transMappingRoot);
            
            // If statusType is EITHER, use the cached status
            // If statusType is USER, check that the cached status is not a UUID Status
            if(status != null) {
//                if(statusType==EITHER_STATUS || 
//                   statusType==USER_STATUS && !status.isUUIDStatus()) {
                    statusResult = status;
//                }
            }
        }
        
        // If a cached status not found, create it
        if(statusResult==null) {
            // Cache doesnt contain status or the status is a UUID status 
            // This does a parse/resolve/validate on the SQL
            statusResult = createStatus(transMappingRoot, cmdType, restrictSearch, context);
            // when validating in editor etc context is null, cach in that case
            if(context == null || context.cacheMappingRootResults()) {
	            // Add status to the cache
	            setStatus(transMappingRoot,cmdType,statusResult);
            }
        }

        return statusResult;
    }

    /**
     * Get the Cache for the supplied command type
     */
    private static HashMap getCache(final int cmdType) {
        switch (cmdType) {
            case QueryValidator.SELECT_TRNS:
                return selectSqlCache;
            case QueryValidator.INSERT_TRNS:
                return insertSqlCache;
            case QueryValidator.UPDATE_TRNS:
				return updateSqlCache;
            case QueryValidator.DELETE_TRNS:
				return deleteSqlCache;
            default:
                return null;
        }
    }

	/**
	 * Get the SQL string, given a SqlTransformationMappingRoot and a command type
	 * @param transMappingRoot the transformation mapping root
	 * @param cmdType The command type whose user sql is to be returned.
	 * @return the SQL UUID String
	 */
	private static String getSqlUserString(final Object transMappingRoot, final int cmdType) {
		switch (cmdType) {
			case QueryValidator.SELECT_TRNS:
				return getSelectSqlUserString(transMappingRoot);
			case QueryValidator.INSERT_TRNS:
				return getInsertSqlUserString(transMappingRoot);
			case QueryValidator.UPDATE_TRNS:
				return getUpdateSqlUserString(transMappingRoot);
			case QueryValidator.DELETE_TRNS:
				return getDeleteSqlUserString(transMappingRoot);
			default:
				return getSelectSqlUserString(transMappingRoot);
		}
	}

    /**
     * Get the SQL Select String, given a SqlTransformationMappingRoot
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Select String
     */
    private static String getSelectSqlUserString(final Object transMappingRoot) {
        SqlTransformation userSqlTransformation = TransformationHelper.getUserSqlTransformation(transMappingRoot);
        String result = null;
        if(userSqlTransformation!=null) {
            result = userSqlTransformation.getSelectSql();
        }
        return result;
    }

    /**
     * Get the SQL Insert String, given a SqlTransformationMappingRoot
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Insert String
     */
    private static String getInsertSqlUserString(final Object transMappingRoot) {
        SqlTransformation userSqlTransformation = TransformationHelper.getUserSqlTransformation(transMappingRoot);
        String result = null;
        if(userSqlTransformation!=null) {
            result = userSqlTransformation.getInsertSql();
        }
        return result;
    }

    /**
     * Get the SQL Update String, given a SqlTransformationMappingRoot
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Update String
     */
    private static String getUpdateSqlUserString(final Object transMappingRoot) {
        SqlTransformation userSqlTransformation = TransformationHelper.getUserSqlTransformation(transMappingRoot);
        String result = null;
        if(userSqlTransformation!=null) {
            result = userSqlTransformation.getUpdateSql();
        }
        return result;
    }

    /**
     * Get the SQL Delete String, given a SqlTransformationMappingRoot
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Delete String
     */
    private static String getDeleteSqlUserString(final Object transMappingRoot) {
        SqlTransformation userSqlTransformation = TransformationHelper.getUserSqlTransformation(transMappingRoot);
        String result = null;
        if(userSqlTransformation!=null) {
            result = userSqlTransformation.getDeleteSql();
        }
        return result;
    }

    //-------------------------------------------------------------------------
    // Methods to Register, UnRegister, Notify Listeners to Cache Change Events
    // Status Event is fired whenever the transformation is invalidated,
    // meaning that something has changed and it needs to be refreshed.
    //-------------------------------------------------------------------------
    /**
    * This method will register the listener for all SqlEditorEvents
    * @param listener the listener to be registered
    */
    public static void addEventListener(EventObjectListener listener) {
        if (eventListeners == null) {
            eventListeners = new ArrayList();
        }
        if(!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    /**
    * This method will un-register the listener for all SqlEditorEvents
    * @param listener the listener to be un-registered
    */
    public static void removeEventListener(EventObjectListener listener) {
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
    * This method will notify the registered listeners of a SqlEditorEvent
    */
    private static void notifyEventListeners(EventObject event) {
        if (eventListeners != null) {
            Iterator iterator = eventListeners.iterator();
            while (iterator.hasNext()) {
                EventObjectListener listener = (EventObjectListener)iterator.next();
                if (listener !=null) {
                    listener.processEvent(event);
                }
            }
        }
    }

}
