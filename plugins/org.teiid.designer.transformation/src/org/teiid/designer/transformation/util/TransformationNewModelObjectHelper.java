/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.core.util.INewModelObjectHelper;
import org.teiid.designer.core.util.ModelResourceContainerFactory;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.transformation.validation.TransformationValidator;


/**
 * @since 8.0
 */
public class TransformationNewModelObjectHelper implements INewModelObjectHelper {

    public static final String VIRTUAL_TABLE_CLEAR_SUPPORTS_UPDATE = "clearSupportsUpdate"; //$NON-NLS-1$
    public static final String VIRTUAL_PROCEDURE_TEMPLATE_SQL = "CREATE VIRTUAL PROCEDURE\nBEGIN\n <--insert SQL here-->;\nEND"; //$NON-NLS-1$

    /**
     * @since 4.3
     */
    public TransformationNewModelObjectHelper() {
        super();
    }

    /*
     * (non-Javadoc)
     * @See org.teiid.designer.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     */
    @Override
	public boolean canHelpCreate( Object newObject ) {
        CoreArgCheck.isNotNull(newObject);
        // First case is a standard virtual table
        // If the createdObject is VirtualTable, set supportsUpdate to false
        if (newObject instanceof EObject) {
            EObject newEObject = (EObject)newObject;
            if (TransformationHelper.isVirtual(newEObject)) {
                // If the createdObject is VirtualTable, set supportsUpdate to false & create T-Root
                if (TransformationHelper.isSqlTable(newEObject) && !TransformationHelper.isXmlDocument(newEObject)) {
                    return true;
                } else if (TransformationHelper.isSqlProcedure(newEObject)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     */
    public Object getTransactionSetting() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @See org.teiid.designer.core.util.INewModelObjectHelper#helpCreate(java.lang.Object, java.util.Map)
     */
    @SuppressWarnings("rawtypes")
	@Override
	public boolean helpCreate( Object newObject,
                               Map properties,
                               List<EObject> references ) {
        CoreArgCheck.isNotNull(newObject);
        if (properties==null) properties = new HashMap();
        boolean defineDefaultTableSQL = properties.get("generateDefaultSQL") != null;
        boolean doValidate = properties.get("validate") != null;

        if (newObject instanceof EObject) {
            EObject newTarget = (EObject)newObject;
            if (TransformationHelper.isVirtual(newTarget)  ){
                // If the createdObject is VirtualTable, set supportsUpdate to false & create T-Root if it doesn't exist
                MetamodelAspect aspect = AspectManager.getSqlAspect(newTarget);
                if (aspect != null && aspect instanceof SqlTableAspect) {
                    // Add T-Root
                	SqlTableAspect tableAspect = (SqlTableAspect)aspect;
                	if( !TransformationHelper.hasSqlTransformationMappingRoot(newTarget) ) {
	                    EObject newRoot = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(newTarget, false, this);
	                    // Add Sql Mapping Helper under it.
	                    ModelResourceContainerFactory.addMappingHelper(newRoot);
	                    // defect 19675 - allow a straight-up copy of a table if desired
	                    if (isMapValueTrue(VIRTUAL_TABLE_CLEAR_SUPPORTS_UPDATE, properties, true)) { // default to clear
	                        tableAspect.setSupportsUpdate(newTarget, false);
	                    }
	                    if( defineDefaultTableSQL ) {
	                    	EObject tRoot = TransformationHelper.getMappingRoot(newTarget);
							List columns = tableAspect.getColumns(newTarget);
							int count = 0;
	                    	StringBuilder sb = new StringBuilder();
	                    	if( !columns.isEmpty() ) {
		                    	sb.append("SELECT");
		                    	for( Object col : columns ) {
		                    		String colName = ModelerCore.getModelEditor().getName((EObject)col);
		                    		if( count > 0 ) sb.append(',');
		                    		String seg = " null AS " + colName;
		                    		sb.append(seg);
		                    		count++;
		                    	}
		                    	TransformationHelper.setSelectSqlString(tRoot, sb.toString(), false, this);
		                        TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, null);
		                        TransformationMappingHelper.reconcileTargetAttributes(tRoot, null);
	                    	}
	                    	
	                    	if(doValidate) {
	                    		QueryValidator validator = new TransformationValidator((SqlTransformationMappingRoot)tRoot);
	                            
	                            validator.validateSql(sb.toString(), QueryValidator.SELECT_TRNS, true);
	                    		//SqlMappingRootCache.getSqlTransformationStatus((SqlTransformationMappingRoot)tRoot, QueryValidator.SELECT_TRNS, true, (ValidationContext)null);
	                    	}
	                    }
	                    return true;
                	} else if( defineDefaultTableSQL ) {
                    	EObject tRoot = TransformationHelper.getMappingRoot(newTarget);
						List columns = tableAspect.getColumns(newTarget);
						int count = 0;
						StringBuilder sb = new StringBuilder();
                    	if( !columns.isEmpty() ) {
	                    	sb.append("SELECT");
	                    	for( Object col : columns ) {
	                    		String colName = ModelerCore.getModelEditor().getName((EObject)col);
	                    		if( count > 0 ) sb.append(',');
	                    		String seg = " null AS " + colName;
	                    		sb.append(seg);
	                    		count++;
	                    	}
	                    	TransformationHelper.setSelectSqlString(tRoot, sb.toString(), false, this);
	                        TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, null);
	                        TransformationMappingHelper.reconcileTargetAttributes(tRoot, null);
                    	}
                    	if(doValidate) {
                    		QueryValidator validator = new TransformationValidator((SqlTransformationMappingRoot)tRoot);
                            
                            validator.validateSql(sb.toString(), QueryValidator.SELECT_TRNS, true);
                    		//SqlMappingRootCache.getSqlTransformationStatus((SqlTransformationMappingRoot)tRoot, QueryValidator.SELECT_TRNS, true, (ValidationContext)null);
                    	}
                    }
                } else if (TransformationHelper.isSqlProcedure(newTarget) && !TransformationHelper.isOperation(newTarget)) {
                	// Add T-Root
                	if( !TransformationHelper.hasSqlTransformationMappingRoot(newTarget) ) {
	                    EObject newRoot = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(newTarget, false, this);
	                    // Add Sql Mapping Helper under it.
	                    ModelResourceContainerFactory.addMappingHelper(newRoot);

                	}
                	if( defineDefaultTableSQL ) {
	                	EObject tRoot = TransformationHelper.getMappingRoot(newTarget);
	                    TransformationHelper.setSelectSqlString(tRoot, VIRTUAL_PROCEDURE_TEMPLATE_SQL, false, this);
                	}
                    return true;
                }
            }            
        }
        return false;
    }

    private static boolean isMapValueTrue( String propertyName,
                                           Map properties,
                                           boolean defaultValue ) {
        if (properties == null) {
            return defaultValue;
        } // endif
        Boolean bool = (Boolean)properties.get(propertyName);
        if (bool == null) {
            return defaultValue;
        } // endif
        return bool.booleanValue();
    }

}
