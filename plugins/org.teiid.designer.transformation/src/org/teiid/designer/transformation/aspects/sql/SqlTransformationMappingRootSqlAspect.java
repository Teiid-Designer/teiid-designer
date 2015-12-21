/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexingContext;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationInfo;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.metamodels.transformation.InputBinding;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.metamodels.transformation.SqlTransformation;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TransformationContainer;
import org.teiid.designer.metamodels.transformation.TransformationMappingRoot;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.util.SqlConverter;

/**
 * SqlTransformationMappingRootSqlAspect
 *
 * @since 8.0
 */
public class SqlTransformationMappingRootSqlAspect extends TransformationMappingRootSqlAspect {

    protected SqlTransformationMappingRootSqlAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformedObject(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getTransformedObject( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        EObject targetEObj = root.getTarget();
        if (targetEObj == null) {
            TransformationPlugin.Util.log(IStatus.WARNING,
                                          TransformationPlugin.Util.getString("SqlTransformationMappingRootSqlAspect.0", ModelerCore.getObjectIdString(root))); //$NON-NLS-1$
        }
        return targetEObj;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getInputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        return root.getInputs();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getNestedInputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        if (root.getNested() == null) {
            return Collections.EMPTY_LIST;
        }
        final List result = new ArrayList();
        for (final Iterator iter = root.getNested().iterator(); iter.hasNext();) {
            final Mapping mapping = (Mapping)iter.next();
            if (mapping != null) {
                result.addAll(mapping.getInputs());
            }
        }
        return result;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getNestedOutputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        if (root.getNested() == null) {
            return Collections.EMPTY_LIST;
        }
        final List result = new ArrayList();
        for (final Iterator iter = root.getNested().iterator(); iter.hasNext();) {
            final Mapping mapping = (Mapping)iter.next();
            if (mapping != null) {
                result.addAll(mapping.getInputs());
            }
        }
        return result;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputsForOutput(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getNestedInputsForOutput( EObject eObject,
                                          EObject output ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        if (root.getNested() != null) {
            for (final Iterator iter = root.getNested().iterator(); iter.hasNext();) {
                final Mapping mapping = (Mapping)iter.next();
                if (mapping != null && mapping.getOutputs().contains(output)) {
                    return mapping.getInputs();
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputsForInput(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getNestedOutputsForInput( EObject eObject,
                                          EObject input ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        if (root.getNested() != null) {
            for (final Iterator iter = root.getNested().iterator(); iter.hasNext();) {
                final Mapping mapping = (Mapping)iter.next();
                if (mapping != null && mapping.getInputs().contains(input)) {
                    return mapping.getOutputs();
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getOutputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        return root.getOutputs();
    }

    /**
     * @see org.teiid.designer.transformation.aspects.sql.MappingClassObjectSqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType( char recordType ) {
        return ((recordType == IndexConstants.RECORD_TYPE.SELECT_TRANSFORM)
                || (recordType == IndexConstants.RECORD_TYPE.INSERT_TRANSFORM)
                || (recordType == IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM)
                || (recordType == IndexConstants.RECORD_TYPE.DELETE_TRANSFORM) || (recordType == IndexConstants.RECORD_TYPE.PROC_TRANSFORM));
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isQueryable( final EObject eObject ) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName( EObject eObject ) {
        final EObject transformedObject = (EObject)getTransformedObject(eObject);
        if (transformedObject == null) {
            return null;
        }
        final SqlAspect sqlAspect = AspectManager.getSqlAspect(transformedObject);
        if (sqlAspect != null) {
            return sqlAspect.getName(transformedObject);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName( EObject eObject ) {
        final EObject transformedObject = (EObject)getTransformedObject(eObject);
        if (transformedObject == null) {
            return null;
        }
        final SqlAspect sqlAspect = AspectManager.getSqlAspect(transformedObject);
        if (sqlAspect != null) {
            return sqlAspect.getFullName(transformedObject);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getNameInSource( EObject eObject ) {
        final EObject transformedObject = (EObject)getTransformedObject(eObject);
        if (transformedObject == null) {
            return null;
        }
        final SqlAspect sqlAspect = AspectManager.getSqlAspect(transformedObject);
        if (sqlAspect != null) {
            return sqlAspect.getNameInSource(transformedObject);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationTypes(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String[] getTransformationTypes( final EObject eObject ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;

        // get the UUID form of the SqlTransformation
        SqlTransformation transformation = (SqlTransformation)root.getHelper();
        if (transformation != null) {
            final String selectSql = getSelectSqlUserString(root);
            final String insertSql = getInsertSqlUserString(root);
            final String updateSql = getUpdateSqlUserString(root);
            final String deleteSql = getDeleteSqlUserString(root);
            final List result = new ArrayList(4);
            if (!CoreStringUtil.isEmpty(selectSql)) {
                final EObject transformedObject = (EObject)getTransformedObject(eObject);
                if (transformedObject == null) {
                    return null;
                }
                final SqlAspect sqlAspect = AspectManager.getSqlAspect(transformedObject);
                if (sqlAspect == null || sqlAspect instanceof SqlTableAspect) {
                    result.add(SqlTransformationAspect.Types.SELECT);
                } else if (sqlAspect instanceof SqlProcedureAspect) {
                    result.add(SqlTransformationAspect.Types.PROCEDURE);
                }
            }
            if (!CoreStringUtil.isEmpty(insertSql) && !transformation.isInsertSqlDefault()) {
                result.add(SqlTransformationAspect.Types.INSERT);
            }
            if (!CoreStringUtil.isEmpty(updateSql) && !transformation.isUpdateSqlDefault()) {
                result.add(SqlTransformationAspect.Types.UPDATE);
            }
            if (!CoreStringUtil.isEmpty(deleteSql) && !transformation.isDeleteSqlDefault()) {
                result.add(SqlTransformationAspect.Types.DELETE);
            }
            return (String[])result.toArray(new String[result.size()]);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformation(org.eclipse.emf.ecore.EObject,
     *      java.lang.String)
     */
    @Override
	public String getTransformation( EObject eObject,
                                     String type ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;

        // get the UUID form of the SqlTransformation
        final SqlTransformation transformation = (SqlTransformation)root.getHelper();
        if (transformation != null) {
            if (SqlTransformationAspect.Types.SELECT.equals(type) || SqlTransformationAspect.Types.PROCEDURE.equals(type)) {
                return SqlConverter.convertToString(transformation.getSelectSql(), root, QueryValidator.SELECT_TRNS, true);
            }
            if (SqlTransformationAspect.Types.INSERT.equals(type)) {
                return SqlConverter.convertToString(transformation.getInsertSql(), root, QueryValidator.INSERT_TRNS, true);
            }
            if (SqlTransformationAspect.Types.UPDATE.equals(type)) {
                return SqlConverter.convertToString(transformation.getUpdateSql(), root, QueryValidator.UPDATE_TRNS, true);
            }
            if (SqlTransformationAspect.Types.DELETE.equals(type)) {
                return SqlConverter.convertToString(transformation.getDeleteSql(), root, QueryValidator.DELETE_TRNS, true);
            }
        }
        return null;
    }


    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isDeleteAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isDeleteAllowed( EObject eObject ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;
        final SqlTransformation sqlTransformation = (SqlTransformation)root.getHelper();
        return sqlTransformation.isDeleteAllowed();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isInsertAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isInsertAllowed( EObject eObject ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;
        final SqlTransformation sqlTransformation = (SqlTransformation)root.getHelper();
        return sqlTransformation.isInsertAllowed();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isUpdateAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isUpdateAllowed( EObject eObject ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;
        final SqlTransformation sqlTransformation = (SqlTransformation)root.getHelper();
        return sqlTransformation.isUpdateAllowed();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationInfo(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext, java.lang.String)
     */
    @Override
	public SqlTransformationInfo getTransformationInfo( final EObject eObject,
                                                        final IndexingContext context,
                                                        final String type ) {

        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);

        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;

        // Get the UUID form of the SqlTransformation. We assume that the UUID form of the query is
        // valid - validation rules should indicate errors if the tranformation is not. Convert the
        // UUID form to a name form by performing a simple text substitution of UUID strings to names
        // and then output the name form.
        final SqlTransformation uuidTransformation = (SqlTransformation)root.getHelper();
        if (uuidTransformation != null) {

            // -------------------------------------------------------------------------
            // SELECT transformation
            // -------------------------------------------------------------------------
            if (SqlTransformationAspect.Types.SELECT.equals(type) || SqlTransformationAspect.Types.PROCEDURE.equals(type)) {

                final String selectSql = getSelectSqlUserString(root); // SqlConverter.convertUUIDsToFullNames(uuidSql, eResources);

                // Get the target of the transformation
                final Object target = getTransformedObject(eObject);

                // if the target is a Mapping class get the command for the sql transform,
                // replace the InputSet bindings with references all collect all the bindings
                List bindingNames = null;
                if (target instanceof MappingClass) {
                    final ICommand command = parseSQL(selectSql);
                    final InputSet inputSet = ((MappingClass)target).getInputSet();
                    if (inputSet != null) {
                        bindingNames = new ArrayList(inputSet.getInputParameters().size());
                        try {
                            // iterate over all the parameterNames in the SQL
                            for (Iterator paramIter = inputSet.getInputParameters().iterator(); paramIter.hasNext();) {
                                // the param name
                                InputParameter param = (InputParameter)paramIter.next();
                                // find the mapping class column name that each param is bounded to
                                // and collect these bindings
                                MappingClass mappingClass = (MappingClass)target;
                                // iterate over the bindings on the MappingClassSet
                                Iterator bindingIter = mappingClass.getMappingClassSet().getInputBinding().iterator();
                                while (bindingIter.hasNext()) {
                                    InputBinding binding = (InputBinding)bindingIter.next();
                                    InputParameter inputParam = binding.getInputParameter();
                                    if (inputParam != null) {
                                        // get the mappingClass this parameter is defined on
                                        // it could be on any of the MappingClasses on the MappingClassSet
                                        // since we obtained the bindings from the MappinClassSet
                                        Object paramMappingClass = inputParam.getInputSet().getMappingClass();
                                        // if the parameter does not belog to
                                        // this mappingClass, skip the binding
                                        if (!paramMappingClass.equals(target)) {
                                            continue;
                                        }
                                        // compare the param names from sql and inputParam name
                                        String inputName = inputParam.getName();
                                        if (inputName != null && inputName.equalsIgnoreCase(param.getName())) {
                                            MappingClassColumn mappingColumn = binding.getMappingClassColumn();
                                            SqlAspect sqlAspect = AspectManager.getSqlAspect(mappingColumn);
                                            String mappingColumnName = sqlAspect.getFullName(mappingColumn);
                                            
                                            IQueryService queryService = ModelerCore.getTeiidQueryService();
                                            IQueryFactory factory = queryService.createQueryFactory();
                                            bindingNames.add(factory.createAliasSymbol(inputName, factory.createElementSymbol(mappingColumnName)).toString());
                                        }
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            TransformationPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                        }
                    }
                    String tranformedSql = null;
                    if (command != null) {
                        // sql from the modified command
                        tranformedSql = command.toString();
                    }

                    return new SqlTransformationInfo(tranformedSql, bindingNames);
                }

                return new SqlTransformationInfo(selectSql);
            }

            // -------------------------------------------------------------------------
            // INSERT transformation
            // -------------------------------------------------------------------------
            if (SqlTransformationAspect.Types.INSERT.equals(type) && uuidTransformation.isInsertAllowed()) {
                final String insertSql = getInsertSqlUserString(root);
                if (!CoreStringUtil.isEmpty(insertSql)) {
                    return new SqlTransformationInfo(insertSql);
                }
            }

            // -------------------------------------------------------------------------
            // UPDATE transformation
            // -------------------------------------------------------------------------
            if (SqlTransformationAspect.Types.UPDATE.equals(type) && uuidTransformation.isUpdateAllowed()) {
                final String updateSql = getUpdateSqlUserString(root);
                if (!CoreStringUtil.isEmpty(updateSql)) {
                    return new SqlTransformationInfo(updateSql);
                }
            }

            // -------------------------------------------------------------------------
            // DELETE transformation
            // -------------------------------------------------------------------------
            if (SqlTransformationAspect.Types.DELETE.equals(type) && uuidTransformation.isDeleteAllowed()) {
                final String deleteSql = getDeleteSqlUserString(root);
                if (!CoreStringUtil.isEmpty(deleteSql)) {
                    return new SqlTransformationInfo(deleteSql);
                }
            }
        }

        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject( EObject targetObject,
                              EObject sourceObject ) {
        // Nothing to do
    }

    /**
     * Get the User SqlTransformation from a SqlTransformationMappingRoot. This is the nested SqlTransformation that is used to
     * store the "user" (or non-uuid) SQL strings.
     * 
     * @param root the transformation mapping root
     * @return the mapping helper
     */
    public static SqlTransformation getUserSqlTransformation( final SqlTransformationMappingRoot root ) {
        SqlTransformation nestedSqlTrans = null;

        if (root != null) {
            final MappingHelper helper = root.getHelper();

            if (helper != null) {
                // Get the nested Helpers, find User SqlTransformation
                for (final Iterator iter = helper.getNested().iterator(); iter.hasNext();) {
                    final Object obj = iter.next();
                    if (obj != null && obj instanceof SqlTransformation) {
                        nestedSqlTrans = (SqlTransformation)obj;
                        break;
                    }
                }
            }
        }
        return nestedSqlTrans;
    }

    /**
     * Replaces all occurrences of oldModelName with newModelName in the given sql
     *
     * @param sql
     * @param oldLiteral
     * @param newLiteral
     */
    private static String updateSql(String sql, String oldLiteral, String newLiteral) {
        if (sql == null)
            return null;

        if (oldLiteral == null || newLiteral == null)
            return sql;

        // Remove the xml extension if it exists
        oldLiteral = oldLiteral.replace(StringConstants.DOT_XMI, StringConstants.EMPTY_STRING);
        newLiteral = newLiteral.replace(StringConstants.DOT_XMI, StringConstants.EMPTY_STRING);

        return sql.replace(oldLiteral, newLiteral);
    }

    /**
     * Replaces the given old literal in the transformation SQL
     * with the new literal
     *
     * @param mappingRoot the mapping root
     * @param oldLiteral the old literal
     * @param newLiteral the new literal
     */
    public static void replaceTransformationLiteral(SqlTransformationMappingRoot mappingRoot,
                                                                                        String oldLiteral, String newLiteral) {
        SqlTransformation sqlTransformation = getUserSqlTransformation(mappingRoot);
        if (sqlTransformation == null)
            return;

        String selectSql = sqlTransformation.getSelectSql();
        String deleteSql = sqlTransformation.getDeleteSql();
        String updateSql = sqlTransformation.getUpdateSql();
        String insertSql = sqlTransformation.getInsertSql();

        selectSql = updateSql(selectSql, oldLiteral, newLiteral);
        deleteSql = updateSql(deleteSql, oldLiteral, newLiteral);
        updateSql = updateSql(updateSql, oldLiteral, newLiteral);
        insertSql = updateSql(insertSql, oldLiteral, newLiteral);

        if (selectSql != null)
            sqlTransformation.setSelectSql(selectSql);
        if (deleteSql != null)
            sqlTransformation.setDeleteSql(deleteSql);
        if (updateSql != null)
            sqlTransformation.setUpdateSql(updateSql);
        if (insertSql != null)
            sqlTransformation.setInsertSql(insertSql);
    }

    /**
     * Iterates the collection of target roots and replaces the given
     * old literal in the transformation SQL with the new literal
     *
     * @param targetRoots collection of target roots
     * @param oldLiteral the old literal
     * @param newLiteral the new literal
     */
    public static void replaceTransformationLiteral(Collection targetRoots, String oldLiteral, String newLiteral) {
        if (targetRoots == null)
            return;

        for (Object childCopy : targetRoots) {
            if (! (childCopy instanceof TransformationContainer))
                continue;

            TransformationContainer trContainer = (TransformationContainer) childCopy;
            EList mappings = trContainer.getTransformationMappings();
            for (Object mapping : mappings.toArray()) {
                if (! (mapping instanceof SqlTransformationMappingRoot))
                    continue;

                SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot) mapping;
                SqlTransformationMappingRootSqlAspect.replaceTransformationLiteral(
                                                                                   mappingRoot,
                                                                                   oldLiteral,
                                                                                   newLiteral);
            }
        }
    }

    /**
     * This method attempts to parse the supplied SQL string. The result is returned as a SqlTransformationResult object.
     * 
     * @param sqlString the SQL to parse
     * @return the SqlTransformationResult object
     */
    public static ICommand parseSQL( final String sqlString ) {
        ICommand command = null;
        try {
            // QueryParser is not thread-safe, get new parser each time
            IQueryParser parser = ModelerCore.getTeiidQueryService().getQueryParser();
            command = parser.parseDesignerCommand(sqlString);
        } catch (Exception e) {
            TransformationPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
        }

        return command;
    }
    
    /**
     * Get the SQL Select String, given a SqlTransformationMappingRoot
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Select String
     */
    private static String getSelectSqlUserString(final SqlTransformationMappingRoot root) {
        SqlTransformation userSqlTransformation = getUserSqlTransformation(root);
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
    private static String getInsertSqlUserString(final SqlTransformationMappingRoot root) {
        SqlTransformation userSqlTransformation = getUserSqlTransformation(root);
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
    private static String getUpdateSqlUserString(final SqlTransformationMappingRoot root) {
        SqlTransformation userSqlTransformation = getUserSqlTransformation(root);
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
    private static String getDeleteSqlUserString(final SqlTransformationMappingRoot root) {
        SqlTransformation userSqlTransformation = getUserSqlTransformation(root);
        String result = null;
        if(userSqlTransformation!=null) {
            result = userSqlTransformation.getDeleteSql();
        }
        return result;
    }

}
