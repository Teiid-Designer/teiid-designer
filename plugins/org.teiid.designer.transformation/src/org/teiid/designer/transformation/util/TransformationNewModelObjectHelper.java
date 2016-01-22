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

    /**
     * Property used by {@link #helpCreate(Object, Map, List)} to determine whether
     * to generate default sql in the transformation
     */
    public static final String CREATE_OBJECT_GENERATE_DEFAULT_SQL_PROPERTY = "generateDefaultSQL"; //$NON-NLS-1$

    /**
     * Property used by {@link #helpCreate(Object, Map, List)} to determine whether
     * to validate the sql in the transformation
     */
    public static final String CREATE_OBJECT_VALIDATE_PROPERTY = "validate"; //$NON-NLS-1$

    /**
     * Property used by {@link #helpCreate(Object, Map, List)} to carry any
     * sql to insert in the transformation
     */
    public static final String CREATE_OBJECT_PROVIDED_SQL_PROPERTY = "providedSQL"; //$NON-NLS-1$

    /**
     *
     */
    public static final String VIRTUAL_TABLE_CLEAR_SUPPORTS_UPDATE = "clearSupportsUpdate"; //$NON-NLS-1$

    /**
     *
     */
    public static final String VIRTUAL_PROCEDURE_TEMPLATE_SQL = "BEGIN\n <--insert SQL here-->;\nEND"; //$NON-NLS-1$

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
     * @param newTarget
     * @param tableAspect
     */
    private String generateDefaultSQL(EObject newTarget, SqlTableAspect tableAspect) {
        List columns = tableAspect.getColumns(newTarget);
        int count = 0;
        StringBuilder sb = new StringBuilder();
        if (!columns.isEmpty()) {
            sb.append("SELECT"); //$NON-NLS-1$
            for (Object col : columns) {
                String colName = ModelerCore.getModelEditor().getName((EObject)col);
                if (count > 0)
                    sb.append(',');
                String seg = " null AS " + colName; //$NON-NLS-1$
                sb.append(seg);
                count++;
            }
        }

        return sb.toString();
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
        boolean defineDefaultTableSQL = properties.get(CREATE_OBJECT_GENERATE_DEFAULT_SQL_PROPERTY) != null;
        boolean doValidate = properties.get(CREATE_OBJECT_VALIDATE_PROPERTY) != null;
        String providedSQL = (String) properties.get(CREATE_OBJECT_PROVIDED_SQL_PROPERTY);

        if (! (newObject instanceof EObject))
            return false;

        EObject newTarget = (EObject)newObject;
        if (! TransformationHelper.isVirtual(newTarget))
            return false;

        // If the createdObject is VirtualTable, set supportsUpdate to false & create T-Root if it doesn't exist
        MetamodelAspect aspect = AspectManager.getSqlAspect(newTarget);
        if (aspect != null && aspect instanceof SqlTableAspect) {
            // Add T-Root
            SqlTableAspect tableAspect = (SqlTableAspect)aspect;
            if (!TransformationHelper.hasSqlTransformationMappingRoot(newTarget)) {
                EObject newRoot = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(newTarget, false, this);
                // Add Sql Mapping Helper under it.
                ModelResourceContainerFactory.addMappingHelper(newRoot);
                // defect 19675 - allow a straight-up copy of a table if desired
                if (isMapValueTrue(VIRTUAL_TABLE_CLEAR_SUPPORTS_UPDATE, properties, true)) { // default to clear
                    tableAspect.setSupportsUpdate(newTarget, false);
                }
            }

            EObject tRoot = TransformationHelper.getMappingRoot(newTarget);

            String selectSql = TransformationHelper.getSelectSqlString(tRoot);
            if (selectSql != null)
                return true; // Already have an sql statement

            if (defineDefaultTableSQL)
                selectSql = generateDefaultSQL(newTarget, tableAspect);
            else if (providedSQL != null) {
                selectSql = providedSQL;
            }

            if (selectSql == null)
                return false;

            TransformationHelper.setSelectSqlString(tRoot, selectSql, false, this);
            TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, null);
            TransformationMappingHelper.reconcileTargetAttributes(tRoot, null);

            if (doValidate) {
                QueryValidator validator = new TransformationValidator((SqlTransformationMappingRoot)tRoot);
                validator.validateSql(selectSql, QueryValidator.SELECT_TRNS, true);
            }

            return true;

        } else if (TransformationHelper.isSqlProcedure(newTarget) && !TransformationHelper.isOperation(newTarget)) {
            // Add T-Root
            if (!TransformationHelper.hasSqlTransformationMappingRoot(newTarget)) {
                EObject newRoot = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(newTarget, false, this);
                // Add Sql Mapping Helper under it.
                ModelResourceContainerFactory.addMappingHelper(newRoot);

            }

            EObject tRoot = TransformationHelper.getMappingRoot(newTarget);

            String selectSql = TransformationHelper.getSelectSqlString(tRoot);
            if (selectSql != null)
                return true; // Already have an sql statement

            if (defineDefaultTableSQL)
                selectSql = VIRTUAL_PROCEDURE_TEMPLATE_SQL;
            else if (providedSQL != null) {
                selectSql = providedSQL;
            }

            if (selectSql == null)
                return false;

            TransformationHelper.setSelectSqlString(tRoot, selectSql, false, this);
            return true;
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
