/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;

/**
 * @since 5.0
 */
public class TransformationSearchHelper {

    private boolean caseSensitive = false;
    private boolean wholeWordMatch = false;

    /**
     * @since 5.0
     */
    public TransformationSearchHelper() {
        super();
    }

    public Collection findTransformations( String searchStr ) throws CoreException {
        ModelWorkspaceManager manager = ModelWorkspaceManager.getModelWorkspaceManager();

        ModelResource[] mResources = manager.getModelWorkspace().getModelResources();

        Collection allAffectedTransforms = new ArrayList();

        for (int i = 0; i < mResources.length; i++) {
            Collection transforms = findTransformations(searchStr, mResources[i]);
            if (!transforms.isEmpty()) {
                allAffectedTransforms.addAll(transforms);
            }
        }

        return allAffectedTransforms;
    }

    public Collection findTransformationTargets( String searchStr ) throws CoreException {
        ModelWorkspaceManager manager = ModelWorkspaceManager.getModelWorkspaceManager();

        ModelResource[] mResources = manager.getModelWorkspace().getModelResources();

        Collection allAffectedTransformTargets = new ArrayList();

        for (int i = 0; i < mResources.length; i++) {
            Collection transformTargets = findTransformationTargets(searchStr, mResources[i]);
            if (!transformTargets.isEmpty()) {
                allAffectedTransformTargets.addAll(transformTargets);
            }
        }

        return allAffectedTransformTargets;
    }

    public Collection findTransformations( String searchStr,
                                           ModelResource mr ) throws ModelWorkspaceException {
        Collection allAffectedTransforms = new ArrayList(1);

        if (mr != null && mr.getModelTransformations() != null) {
            Collection allTransforms = mr.getModelTransformations().getTransformations();

            Object nextObj = null;
            for (Iterator iter = allTransforms.iterator(); iter.hasNext();) {
                nextObj = iter.next();
                if (TransformationHelper.isSqlTransformationMappingRoot(nextObj)) {
                    SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)nextObj;
                    if (searchTransformationRoot(searchStr, mappingRoot)) {
                        allAffectedTransforms.add(nextObj);
                    }
                }
            }
        }

        return allAffectedTransforms;
    }

    public Collection findTransformations( final String searchStr,
                                           final Resource resource ) {
        Collection allAffectedTransforms = new ArrayList(1);

        Collection allTransforms = getTransformations(resource);

        Object nextObj = null;
        for (Iterator iter = allTransforms.iterator(); iter.hasNext();) {
            nextObj = iter.next();
            if (TransformationHelper.isSqlTransformationMappingRoot(nextObj)) {
                SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)nextObj;
                if (searchTransformationRoot(searchStr, mappingRoot)) {
                    allAffectedTransforms.add(nextObj);
                }
            }
        }

        return allAffectedTransforms;
    }

    public Collection findTransformationTargets( final String searchStr,
                                                 final Resource resource ) {
        Collection allAffectedTransforms = findTransformations(searchStr, resource);

        return getAllTransformationTargets(allAffectedTransforms);
    }

    public Collection findTransformationTargets( final String searchStr,
                                                 final ModelResource resource ) throws ModelWorkspaceException {
        Collection allAffectedTransforms = findTransformations(searchStr, resource);

        return getAllTransformationTargets(allAffectedTransforms);
    }

    public boolean searchTransformationRoot( String searchStr,
                                             SqlTransformationMappingRoot mappingRoot ) {
        // Check if INSERT/UPDATE/DELETE should be searched
        boolean supportsUpdates = false;
        EObject mRootTarget = mappingRoot.getTarget();
        SqlAspect sqlAspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(mRootTarget);
        if (sqlAspect != null && (sqlAspect instanceof SqlTableAspect)) {
            supportsUpdates = ((SqlTableAspect)sqlAspect).supportsUpdate(mRootTarget);
        }

        boolean foundMatch = false;
        if (!caseSensitive) {
            String lowerCaseSearchStr = searchStr.toLowerCase();
            String lowerCaseStr;

            String sqlStr = getUserSql(mappingRoot, QueryValidator.SELECT_TRNS);

            if (sqlStr != null) {
                lowerCaseStr = sqlStr.toLowerCase();
                foundMatch = lowerCaseStr.indexOf(lowerCaseSearchStr) > -1;
            }
            if (!foundMatch && supportsUpdates) {
                sqlStr = getUserSql(mappingRoot, QueryValidator.INSERT_TRNS);
                if (sqlStr != null) {
                    lowerCaseStr = sqlStr.toLowerCase();
                    foundMatch = lowerCaseStr.indexOf(lowerCaseSearchStr) > -1;
                }
                if (!foundMatch) {
                    sqlStr = getUserSql(mappingRoot, QueryValidator.UPDATE_TRNS);
                    if (sqlStr != null) {
                        lowerCaseStr = sqlStr.toLowerCase();
                        foundMatch = lowerCaseStr.indexOf(lowerCaseSearchStr) > -1;
                    }
                    if (!foundMatch) {
                        sqlStr = getUserSql(mappingRoot, QueryValidator.DELETE_TRNS);
                        if (sqlStr != null) {
                            lowerCaseStr = sqlStr.toLowerCase();
                            foundMatch = lowerCaseStr.indexOf(lowerCaseSearchStr) > -1;
                        }
                    }
                }
            }
        } else {
            String sqlStr = getUserSql(mappingRoot, QueryValidator.SELECT_TRNS);

            if (sqlStr != null) {
                foundMatch = sqlStr.indexOf(searchStr) > -1;
            }
            if (!foundMatch && supportsUpdates) {
                sqlStr = getUserSql(mappingRoot, QueryValidator.INSERT_TRNS);
                if (sqlStr != null) {
                    foundMatch = sqlStr.indexOf(searchStr) > -1;
                }
                if (!foundMatch) {
                    sqlStr = getUserSql(mappingRoot, QueryValidator.UPDATE_TRNS);
                    if (sqlStr != null) {
                        foundMatch = sqlStr.indexOf(searchStr) > -1;
                    }
                    if (!foundMatch) {
                        sqlStr = getUserSql(mappingRoot, QueryValidator.DELETE_TRNS);
                        if (sqlStr != null) {
                            foundMatch = sqlStr.indexOf(searchStr) > -1;
                        }
                    }
                }
            }
        }

        return foundMatch;
    }

    public static Collection getTransformations( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);
        Collection transformations = new ArrayList();
        Collection contents = resource.getContents();
        Iterator cIter = contents.iterator();
        while (cIter.hasNext()) {
            Object obj = cIter.next();
            if (obj instanceof TransformationContainer) {
                Collection mappings = ((TransformationContainer)obj).getTransformationMappings();
                transformations.addAll(mappings);
                break;
            }
        }

        return transformations;
    }

    public static boolean hasUserSql( SqlTransformationMappingRoot mappingRoot,
                                      int sqlType ) {
        SqlTransformation sqlTransform = TransformationHelper.getUserSqlTransformation(mappingRoot);
        if (sqlTransform != null) {
            switch (sqlType) {
                case QueryValidator.SELECT_TRNS: {
                    return sqlTransform.getSelectSql() != null;
                }
                case QueryValidator.UPDATE_TRNS: {
                    return sqlTransform.getUpdateSql() != null;
                }
                case QueryValidator.INSERT_TRNS: {
                    return sqlTransform.getInsertSql() != null;
                }
                case QueryValidator.DELETE_TRNS: {
                    return sqlTransform.getDeleteSql() != null;
                }

            }
        }

        return false;
    }

    public static String getUserSql( SqlTransformationMappingRoot mappingRoot,
                                     int sqlType ) {
        SqlTransformation sqlTransform = TransformationHelper.getUserSqlTransformation(mappingRoot);
        if (sqlTransform != null) {
            switch (sqlType) {
                case QueryValidator.SELECT_TRNS: {
                    return sqlTransform.getSelectSql();
                }
                case QueryValidator.UPDATE_TRNS: {
                    return sqlTransform.getUpdateSql();
                }
                case QueryValidator.INSERT_TRNS: {
                    return sqlTransform.getInsertSql();
                }
                case QueryValidator.DELETE_TRNS: {
                    return sqlTransform.getDeleteSql();
                }

            }
        }
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    public Collection getAllTransformationTargets( Collection allTransforms ) {
        int nTransforms = allTransforms.size();
        Collection allTargets = new ArrayList(nTransforms);

        for (Iterator iter = allTransforms.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof SqlTransformationMappingRoot) {
                EObject table = ((SqlTransformationMappingRoot)obj).getTarget();
                if (table != null) {
                    allTargets.add(table);
                }
            }
        }

        return allTargets;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive( boolean value ) {
        caseSensitive = value;
    }

    public boolean isWholeWordMatch() {
        return wholeWordMatch;
    }

    public void setWwholeWordMatch( boolean value ) {
        caseSensitive = wholeWordMatch;
    }
}
