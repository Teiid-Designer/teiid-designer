/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.modeler.core.refactor.ReferenceUpdator;
import com.metamatrix.modeler.transformation.validation.SqlTransformationResult;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.navigator.PreOrderNavigator;

/**
 * @since 4.2
 */
public class TransformationReferenceUpdator implements ReferenceUpdator {

    /** 
     * @see com.metamatrix.modeler.core.refactor.ReferenceUpdator#updateEObject(org.eclipse.emf.ecore.EObject, java.util.Map)
     * @since 4.2
     */
    public void updateEObject(final EObject eObject, final Map oldToNewObjects) {

        // reset the select/insert/update/delete sql on SqlTransformation        
        if(eObject instanceof SqlTransformation) {
            // visitor that update the sql
            UpdateLanguageObjectNameVisitor visitor = new UpdateLanguageObjectNameVisitor(oldToNewObjects);
            
            SqlTransformation sqlTransformation = (SqlTransformation) eObject;
            // update the sql transformation
            updateSqlTransformation(sqlTransformation, visitor);
        }
    }

    private void updateSqlTransformation(final SqlTransformation sqlTransformation, final UpdateLanguageObjectNameVisitor visitor) {
        // get the mapping root for select transformation
        Object mappingRoot = sqlTransformation.getMapper();
        // update the select sql on the transformation
        final String selectSql = sqlTransformation.getSelectSql();
        if(selectSql != null) {
            final SqlTransformationResult selectResult = TransformationValidator.parseSQL(selectSql);
            if(selectResult.isParsable()) {
                final Command selectCmd = selectResult.getCommand();
                // update the command with the new language object names
                PreOrderNavigator.doVisit(selectCmd, visitor);
                String newSelect = selectCmd.toString();
                if(!selectSql.equalsIgnoreCase(newSelect)) {
                    // invalidate the select cache
                    SqlMappingRootCache.invalidateSelectStatus(mappingRoot, true, null);
                    sqlTransformation.setSelectSql(newSelect);
                }
            }
        }

        // update the insert sql on the transformation
        String insertSql = sqlTransformation.getInsertSql();
        if(insertSql != null) {
            final SqlTransformationResult insertResult = TransformationValidator.parseSQL(insertSql);
            if(insertResult.isParsable()) {
                final Command insertCmd = insertResult.getCommand();
                // update the command with the new language object names
                PreOrderNavigator.doVisit(insertCmd, visitor);
                String newInsert = insertCmd.toString();
                if(!insertSql.equalsIgnoreCase(newInsert)) {
                    // invalidate the insert cache
                    SqlMappingRootCache.invalidateInsertStatus(mappingRoot, true, null);
                    sqlTransformation.setInsertSql(newInsert);
                }
            }
        }

        // update the update sql on the transformation
        final String updateSql = sqlTransformation.getUpdateSql();
        if(updateSql != null) {
            final SqlTransformationResult updateResult = TransformationValidator.parseSQL(updateSql);
            if(updateResult.isParsable()) {
                final Command updateCmd = updateResult.getCommand();
                // update the command with the new language object names
                PreOrderNavigator.doVisit(updateCmd, visitor);
                String newUpdate = updateCmd.toString();
                if(!updateSql.equalsIgnoreCase(newUpdate)) {
                    // invalidate the update cache
                    SqlMappingRootCache.invalidateUpdateStatus(mappingRoot, true, null);
                    sqlTransformation.setUpdateSql(newUpdate);
                }
            }
        }

        // update the delete sql on the transformation
        final String deleteSql = sqlTransformation.getDeleteSql();
        if(deleteSql != null) {
            final SqlTransformationResult deleteResult = TransformationValidator.parseSQL(deleteSql);
            if(deleteResult.isParsable()) {
                final Command deleteCmd = deleteResult.getCommand();
                // delete the command with the new language object names
                PreOrderNavigator.doVisit(deleteCmd, visitor);
                String newDelete = deleteCmd.toString();
                if(!deleteSql.equalsIgnoreCase(newDelete)) {
                    // invalidate the delete cache
                    SqlMappingRootCache.invalidateDeleteStatus(mappingRoot, true, null);
                    sqlTransformation.setDeleteSql(newDelete);
                }
            }
        }        
    }

}
