/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.navigator.DeepPreOrderNavigator;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationInfo;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.internal.transformation.util.InputSetPramReplacementVisitor;
import com.metamatrix.modeler.internal.transformation.util.SqlConverter;
import com.metamatrix.modeler.transformation.TransformationPlugin;

/**
 * SqlTransformationMappingRootSqlAspect
 */
public class SqlTransformationMappingRootSqlAspect extends TransformationMappingRootSqlAspect {

    protected SqlTransformationMappingRootSqlAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformedObject(org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getInputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        return root.getInputs();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputsForOutput(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputsForInput(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getOutputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        return root.getOutputs();
    }

    /**
     * @see com.metamatrix.modeler.transformation.aspects.sql.MappingClassObjectSqlAspect#isRecordType(char)
     */
    public boolean isRecordType( char recordType ) {
        return ((recordType == IndexConstants.RECORD_TYPE.SELECT_TRANSFORM)
                || (recordType == IndexConstants.RECORD_TYPE.INSERT_TRANSFORM)
                || (recordType == IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM)
                || (recordType == IndexConstants.RECORD_TYPE.DELETE_TRANSFORM) || (recordType == IndexConstants.RECORD_TYPE.PROC_TRANSFORM));
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable( final EObject eObject ) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getFullName(org.eclipse.emf.ecore.EObject)
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationTypes(org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformation(org.eclipse.emf.ecore.EObject,
     *      java.lang.String)
     */
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

    // /*
    // * @see
    // com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationInfo(org.eclipse.emf.ecore.EObject,
    // com.metamatrix.modeler.core.validation.ValidationContext, java.lang.String)
    // */
    // public SqlTransformationInfo getTransformationInfo(final EObject eObject, final ValidationContext context, final String
    // type) {
    // ArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
    //
    // final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;
    //
    // // Get the UUID form of the SqlTransformation
    // final SqlTransformation uuidTransformation = (SqlTransformation) root.getHelper();
    //
    // // Get the resource set to use when resolving the uuid form of the SQL
    // final Resource resource = eObject.eResource();
    // if (resource == null) {
    // final Object[] params = new Object[] {eObject};
    //            final String msg = TransformationPlugin.Util.getString("SqlTransformationMappingRootSqlAspect.EObject_has_no_resource_reference",params); //$NON-NLS-1$
    // TransformationPlugin.Util.log(IStatus.ERROR, msg);
    // return null;
    // }
    // final ResourceSet resourceSet = resource.getResourceSet();
    // if (resourceSet == null) {
    // final Object[] params = new Object[] {eObject};
    //            final String msg = TransformationPlugin.Util.getString("SqlTransformationMappingRootSqlAspect.EObject_has_not_resourceset_reference",params); //$NON-NLS-1$
    // TransformationPlugin.Util.log(IStatus.ERROR, msg);
    // return null;
    // }
    //
    // if (uuidTransformation != null) {
    // if (SqlTransformationAspect.Types.SELECT.equals(type) || SqlTransformationAspect.Types.PROCEDURE.equals(type)) {
    //
    // // Get the uuid SQL string
    // final String uuidSqlString = uuidTransformation.getSelectSql();
    //
    // // Get the target of the transformation
    // final Object target = this.getTransformedObject(eObject);
    //
    // // Convert the uuid SQL string to name form
    // final String nameSqlString = SqlConverter.convertSql(uuidSqlString, resourceSet, QueryValidator.SELECT_TRNS);
    //
    // // if the target is a Mapping class get the command for the sql transform,
    // // replace the InputSet bindings with references all collect all the bindings
    // if (nameSqlString != null && target instanceof MappingClass) {
    //
    // // proceed to find bindings, clone the command as we are changing it
    // final Command command = parseSQL(nameSqlString);
    //
    // InputSetPramReplacementVisitor visitor = new InputSetPramReplacementVisitor();
    // command.acceptVisitor(visitor);
    // // collect the parameters after replacing with references in the command
    // List parameterNames = visitor.getParameters();
    // if (!parameterNames.isEmpty()) {
    // List bindingNames = new ArrayList(parameterNames.size());
    //
    // // iterate over all the parameterNames in the SQL
    // Iterator paramIter = parameterNames.iterator();
    // while (paramIter.hasNext()) {
    // // the param name
    // String paramName = (String)paramIter.next();
    // // find the mapping class column name that each param is bounded to
    // // and collect these bindings
    // MappingClass mappingClass = (MappingClass)target;
    //
    // // iterate over the bindings on the MappingClassSet
    // Iterator bindingIter = mappingClass.getMappingClassSet().getInputBinding().iterator();
    // while (bindingIter.hasNext()) {
    // InputBinding binding = (InputBinding)bindingIter.next();
    // InputParameter inputParam = binding.getInputParameter();
    // if (inputParam != null) {
    // // get the mappingClass this parameter is defined on
    // // it could be on any of the MappingClasses on the MappingClassSet
    // // since we obtained the bindings from the MappinClassSet
    // Object paramMappingClass = inputParam.getInputSet().getMappingClass();
    // // if the parameter does not belog to
    // // this mappingClass, skip the binding
    // if (!paramMappingClass.equals(target)) {
    // continue;
    // }
    // // compare the param names from sql and inputParam name
    // String inputName = inputParam.getName();
    // if (inputName != null && inputName.equalsIgnoreCase(paramName)) {
    // MappingClassColumn mappingColumn = binding.getMappingClassColumn();
    // SqlAspect sqlAspect = AspectManager.getSqlAspect(mappingColumn);
    // String mappingColumnName = sqlAspect.getFullName(mappingColumn);
    // if (!bindingNames.contains(mappingColumnName)) {
    // bindingNames.add(mappingColumnName);
    // }
    // }
    // }
    // }
    // }
    // // info has the binding names and the sql from the movified command
    // return new SqlTransformationInfo(command.toString(), bindingNames);
    // }
    // }
    // return new SqlTransformationInfo(nameSqlString);
    // }
    // if (SqlTransformationAspect.Types.INSERT.equals(type) && uuidTransformation.isInsertAllowed()) {
    // String insertSql = SqlConverter.convertSql(uuidTransformation.getInsertSql(), resourceSet, QueryValidator.INSERT_TRNS);
    // if (!StringUtil.isEmpty(insertSql)) {
    // return new SqlTransformationInfo(insertSql);
    // }
    // }
    // if (SqlTransformationAspect.Types.UPDATE.equals(type) && uuidTransformation.isUpdateAllowed()) {
    // String updateSql = SqlConverter.convertSql(uuidTransformation.getUpdateSql(), resourceSet, QueryValidator.UPDATE_TRNS);
    // if (!StringUtil.isEmpty(updateSql)) {
    // return new SqlTransformationInfo(updateSql);
    // }
    // }
    // if (SqlTransformationAspect.Types.DELETE.equals(type) && uuidTransformation.isDeleteAllowed()) {
    // String deleteSql = SqlConverter.convertSql(uuidTransformation.getDeleteSql(), resourceSet, QueryValidator.DELETE_TRNS);
    // if (!StringUtil.isEmpty(deleteSql)) {
    // return new SqlTransformationInfo(deleteSql);
    // }
    // }
    // }
    //
    // return null;
    // }
    // /*
    // * @see
    // com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationInfo(org.eclipse.emf.ecore.EObject,
    // com.metamatrix.modeler.core.validation.ValidationContext, java.lang.String)
    // */
    // public SqlTransformationInfo getTransformationInfo(final EObject eObject,
    // final ValidationContext context,
    // final String type) {
    //
    // ArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
    // final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;
    // final TransformationValidator validator = new TransformationValidator(root, context, false, false);
    // final Collection eResources = (context != null ? context.getResourcesInContext() : Collections.EMPTY_LIST);
    //
    // // Get the UUID form of the SqlTransformation. We assume that the UUID form of the query is
    // // valid - validation rules should indicate errors if the tranformation is not. Convert the
    // // UUID form to a name form by performing a simple text substitution of UUID strings to names
    // // and then output the name form.
    // final SqlTransformation uuidTransformation = (SqlTransformation)root.getHelper();
    // if (uuidTransformation != null) {
    //
    // if (SqlTransformationAspect.Types.SELECT.equals(type) || SqlTransformationAspect.Types.PROCEDURE.equals(type)) {
    //
    // // Get the UUID SQL string
    // final String uuidSql = uuidTransformation.getSelectSql();
    // final QueryValidationResult result = validator.validateSql( uuidTransformation.getSelectSql(), QueryValidator.SELECT_TRNS,
    // true, false);
    //
    // // this is the target
    // Object target = getTransformedObject(eObject);
    //
    // // if the target is a Mapping class get the command for the sql transform,
    // // replace the InputSet bindings with references all collect all the bindings
    // if (target instanceof MappingClass) {
    //
    // final Command command = parseSQL(uuidSql);
    // if (command == null) {
    // return null;
    // }
    //
    //
    // // proceed to find bindings, clone the command as we are changing it
    // Command resolvedCommand = (Command)result.getCommand().clone();
    // InputSetPramReplacementVisitor visitor = new InputSetPramReplacementVisitor();
    // resolvedCommand.acceptVisitor(visitor);
    //
    // // collect the parameters after replacing with references in the command
    // List parameterNames = visitor.getParameters();
    // if (!parameterNames.isEmpty()) {
    // List bindingNames = new ArrayList(parameterNames.size());
    //
    // // iterate over all the parameterNames in the SQL
    // Iterator paramIter = parameterNames.iterator();
    // while (paramIter.hasNext()) {
    // // the param name
    // String paramName = (String)paramIter.next();
    // // find the mapping class column name that each param is bounded to
    // // and collect these bindings
    // MappingClass mappingClass = (MappingClass)target;
    // // iterate over the bindings on the MappingClassSet
    // Iterator bindingIter = mappingClass.getMappingClassSet().getInputBinding().iterator();
    // while (bindingIter.hasNext()) {
    // InputBinding binding = (InputBinding)bindingIter.next();
    // InputParameter inputParam = binding.getInputParameter();
    // if (inputParam != null) {
    // // get the mappingClass this parameter is defined on
    // // it could be on any of the MappingClasses on the MappingClassSet
    // // since we obtained the bindings from the MappinClassSet
    // Object paramMappingClass = inputParam.getInputSet().getMappingClass();
    // // if the parameter does not belog to
    // // this mappingClass, skip the binding
    // if (!paramMappingClass.equals(target)) {
    // continue;
    // }
    // // compare the param names from sql and inputParam name
    // String inputName = inputParam.getName();
    // if (inputName != null && inputName.equalsIgnoreCase(paramName)) {
    // MappingClassColumn mappingColumn = binding.getMappingClassColumn();
    // SqlAspect sqlAspect = AspectManager.getSqlAspect(mappingColumn);
    // String mappingColumnName = sqlAspect.getFullName(mappingColumn);
    // if (!bindingNames.contains(mappingColumnName)) {
    // bindingNames.add(mappingColumnName);
    // }
    // }
    // }
    // }
    // }
    //
    // // info has the binding names and the sql from the movified command
    // final String tranformSql = resolvedCommand.toString();
    // final String selectSql = SqlConverter.convertSql(tranformSql, eResources, QueryValidator.SELECT_TRNS);
    // return new SqlTransformationInfo(selectSql, bindingNames);
    // }
    // }
    // final String tranformSql = result.getCommand().toString();
    // final String selectSql = SqlConverter.convertUUIDsToFullNames(tranformSql, eResources);
    // return new SqlTransformationInfo(selectSql);
    // }
    // if (SqlTransformationAspect.Types.INSERT.equals(type) && transformation.isInsertAllowed()) {
    //
    // // validate the uuid form of the sql. The validated sql can then be tranformed into name form
    // final QueryValidationResult result = validator.validateSql( transformation.getInsertSql(), QueryValidator.INSERT_TRNS,
    // true, false);
    // // if (!result.isValidatable()) {
    // // return null;
    // // }
    // final String insertSql = SqlConverter.convertUUIDsToFullNames(transformation.getInsertSql(), eResources);
    // if (!StringUtil.isEmpty(insertSql)) {
    // return new SqlTransformationInfo(insertSql);
    // }
    // }
    // if (SqlTransformationAspect.Types.UPDATE.equals(type) && transformation.isUpdateAllowed()) {
    //
    // // validate the uuid form of the sql. The validated sql can then be tranformed into name form
    // final QueryValidationResult result = validator.validateSql( transformation.getUpdateSql(), QueryValidator.UPDATE_TRNS,
    // true, false);
    // // if (!result.isValidatable()) {
    // // return null;
    // // }
    // final String updateSql = SqlConverter.convertUUIDsToFullNames(transformation.getUpdateSql(), eResources);
    // if (!StringUtil.isEmpty(updateSql)) {
    // return new SqlTransformationInfo(updateSql);
    // }
    // }
    // if (SqlTransformationAspect.Types.DELETE.equals(type) && transformation.isDeleteAllowed()) {
    //
    // // validate the uuid form of the sql. The validated sql can then be tranformed into name form
    // final QueryValidationResult result = validator.validateSql( transformation.getDeleteSql(), QueryValidator.DELETE_TRNS,
    // true, false);
    // // if (!result.isValidatable()) {
    // // return null;
    // // }
    // final String deleteSql = SqlConverter.convertUUIDsToFullNames(transformation.getDeleteSql(), eResources);
    // if (!StringUtil.isEmpty(deleteSql)) {
    // return new SqlTransformationInfo(deleteSql);
    // }
    // }
    // }
    //
    // return null;
    // }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isDeleteAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isDeleteAllowed( EObject eObject ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;
        final SqlTransformation sqlTransformation = (SqlTransformation)root.getHelper();
        return sqlTransformation.isDeleteAllowed();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isInsertAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isInsertAllowed( EObject eObject ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;
        final SqlTransformation sqlTransformation = (SqlTransformation)root.getHelper();
        return sqlTransformation.isInsertAllowed();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isUpdateAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isUpdateAllowed( EObject eObject ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        final SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)eObject;
        final SqlTransformation sqlTransformation = (SqlTransformation)root.getHelper();
        return sqlTransformation.isUpdateAllowed();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationInfo(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext, java.lang.String)
     */
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
                if (target instanceof MappingClass) {
                    final Command command = parseSQL(selectSql);
                    if (command == null) {
                        return null;
                    }
                    final List inputSetParamNames = new ArrayList();
                    final InputSet inputSet = ((MappingClass)target).getInputSet();
                    if (inputSet != null) {
                        for (final Iterator iter = inputSet.getInputParameters().iterator(); iter.hasNext();) {
                            final InputParameter inputParam = (InputParameter)iter.next();
                            inputSetParamNames.add(inputParam.getName());
                        }
                    }
                    // proceed to find bindings, clone the command as we are changing it
                    InputSetPramReplacementVisitor visitor = new InputSetPramReplacementVisitor();
                    if (!inputSetParamNames.isEmpty()) {
                        visitor.setInputSetParamNames(inputSetParamNames);
                    }
                    DeepPreOrderNavigator.doVisit(command, visitor);

                    // collect the parameters after replacing with references in the command
                    List parameterNames = visitor.getParameters();
                    if (!parameterNames.isEmpty()) {
                        List bindingNames = new ArrayList(parameterNames.size());
                        try {
                            // iterate over all the parameterNames in the SQL
                            for (Iterator paramIter = parameterNames.iterator(); paramIter.hasNext();) {
                                // the param name
                                String paramName = (String)paramIter.next();
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
                                        if (inputName != null && inputName.equalsIgnoreCase(paramName)) {
                                            MappingClassColumn mappingColumn = binding.getMappingClassColumn();
                                            SqlAspect sqlAspect = AspectManager.getSqlAspect(mappingColumn);
                                            String mappingColumnName = sqlAspect.getFullName(mappingColumn);
                                            // can be duplicates Defect 17719
                                            bindingNames.add(mappingColumnName);
                                        }
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            TransformationPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                        }

                        // sql from the modified command
                        final String tranformedSql = command.toString();
                        if (!CoreStringUtil.isEmpty(tranformedSql)) {
                            return new SqlTransformationInfo(tranformedSql, bindingNames);
                        }
                    }
                }

                if (!CoreStringUtil.isEmpty(selectSql)) {
                    return new SqlTransformationInfo(selectSql);
                }
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject( EObject targetObject,
                              EObject sourceObject ) {
    }

    /**
     * Get the User SqlTransformation from a SqlTransformationMappingRoot. This is the nested SqlTransformation that is used to
     * store the "user" (or non-uuid) SQL strings.
     * 
     * @param transMappingRoot the transformation mapping root
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
     * This method attempts to parse the supplied SQL string. The result is returned as a SqlTransformationResult object.
     * 
     * @param sqlString the SQL to parse
     * @return the SqlTransformationResult object
     */
    public static Command parseSQL( final String sqlString ) {
        Command command = null;
        try {
            // QueryParser is not thread-safe, get new parser each time
            QueryParser parser = new QueryParser();
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
