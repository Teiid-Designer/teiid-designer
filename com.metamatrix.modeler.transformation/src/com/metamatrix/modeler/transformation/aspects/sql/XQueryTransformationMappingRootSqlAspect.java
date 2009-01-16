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

package com.metamatrix.modeler.transformation.aspects.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.XQueryTransformation;
import com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationInfo;
import com.metamatrix.modeler.transformation.TransformationPlugin;

/**
 * XQueryTransformationMappingRootSqlAspect
 */
public class XQueryTransformationMappingRootSqlAspect extends TransformationMappingRootSqlAspect {

    protected XQueryTransformationMappingRootSqlAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformedObject(org.eclipse.emf.ecore.EObject)
     */
    public Object getTransformedObject( EObject eObject ) {
        ArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        EObject targetEObj = root.getTarget();
        if (targetEObj == null) {
            TransformationPlugin.Util.log(IStatus.WARNING,
                                          TransformationPlugin.Util.getString("XQueryTransformationMappingRootSqlAspect.0", ModelerCore.getObjectIdString(root))); //$NON-NLS-1$
        }
        return targetEObj;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getInputObjects( EObject eObject ) {
        ArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        return root.getInputs();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedInputObjects( EObject eObject ) {
        ArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
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
        ArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
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
        ArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
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
        ArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
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
        ArgCheck.isInstanceOf(TransformationMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        return root.getOutputs();
    }

    /**
     * @see com.metamatrix.modeler.transformation.aspects.sql.MappingClassObjectSqlAspect#isRecordType(char)
     */
    public boolean isRecordType( char recordType ) {
        return (recordType == IndexConstants.RECORD_TYPE.PROC_TRANSFORM);
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
        ArgCheck.isInstanceOf(XQueryTransformationMappingRoot.class, eObject);
        final XQueryTransformationMappingRoot root = (XQueryTransformationMappingRoot)eObject;

        // get the XQuery expression of the XQueryTransformation
        final XQueryTransformation transformation = (XQueryTransformation)root.getHelper();
        if (transformation != null) {
            final String expression = transformation.getExpression();
            if (!StringUtil.isEmpty(expression)) {
                final EObject transformedObject = (EObject)getTransformedObject(eObject);
                if (transformedObject == null) {
                    return null;
                }
                final SqlAspect sqlAspect = AspectManager.getSqlAspect(transformedObject);
                if (sqlAspect instanceof SqlProcedureAspect) {
                    return new String[] {SqlTransformationAspect.Types.PROCEDURE};
                }
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformation(org.eclipse.emf.ecore.EObject,
     *      java.lang.String)
     */
    public String getTransformation( EObject eObject,
                                     String type ) {
        ArgCheck.isInstanceOf(XQueryTransformationMappingRoot.class, eObject);
        final XQueryTransformationMappingRoot root = (XQueryTransformationMappingRoot)eObject;

        // get the XQuery expression of the XQueryTransformation
        final XQueryTransformation transformation = (XQueryTransformation)root.getHelper();
        if (transformation != null) {
            if (SqlTransformationAspect.Types.SELECT.equals(type) || SqlTransformationAspect.Types.PROCEDURE.equals(type)) {
                return transformation.getExpression();
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isDeleteAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isDeleteAllowed( EObject eObject ) {
        ArgCheck.isInstanceOf(XQueryTransformationMappingRoot.class, eObject);
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isInsertAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isInsertAllowed( EObject eObject ) {
        ArgCheck.isInstanceOf(XQueryTransformationMappingRoot.class, eObject);
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isUpdateAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isUpdateAllowed( EObject eObject ) {
        ArgCheck.isInstanceOf(XQueryTransformationMappingRoot.class, eObject);
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationInfo(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext, java.lang.String)
     */
    public SqlTransformationInfo getTransformationInfo( final EObject eObject,
                                                        final IndexingContext context,
                                                        final String type ) {

        ArgCheck.isInstanceOf(XQueryTransformationMappingRoot.class, eObject);

        final XQueryTransformationMappingRoot root = (XQueryTransformationMappingRoot)eObject;

        // get the XQuery expression of the XQueryTransformation
        final XQueryTransformation transformation = (XQueryTransformation)root.getHelper();

        if (transformation != null) {

            // -------------------------------------------------------------------------
            // SELECT transformation
            // -------------------------------------------------------------------------
            if (SqlTransformationAspect.Types.SELECT.equals(type) || SqlTransformationAspect.Types.PROCEDURE.equals(type)) {

                // Get the expression
                final String expression = transformation.getExpression();
                if (StringUtil.isEmpty(expression)) {
                    return null;
                }
                return new SqlTransformationInfo(expression);
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

}
