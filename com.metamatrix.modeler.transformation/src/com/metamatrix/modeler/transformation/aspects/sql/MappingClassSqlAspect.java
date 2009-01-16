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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.common.vdb.api.SystemVdbUtility;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.transformation.TransformationPlugin;

/**
 * MappingClassSqlAspect
 */
public class MappingClassSqlAspect extends MappingClassObjectSqlAspect implements SqlTableAspect {

    /**
     * Construct an instance of MappingClassSqlAspect.
     * 
     */
    public MappingClassSqlAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.transformation.aspects.sql.MappingClassObjectSqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.TABLE);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#supportsUpdate(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsUpdate(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    public boolean isVirtual(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClass.class, eObject);
        MappingClass operation = (MappingClass) eObject;    
        try {    
            Resource eResource = operation.eResource();
            if (eResource != null && eResource instanceof EmfResource) {
                return (((EmfResource)eResource).getModelType() == ModelType.VIRTUAL_LITERAL);
            }
        } catch(Exception e) {
            TransformationPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isMaterialized(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isMaterialized(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isSystem(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSystem(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClass.class, eObject);
        String modelName = getModelName(eObject);
        if (modelName != null && SystemVdbUtility.isSystemModelWithSystemTableType(modelName)) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    public List getColumns(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClass.class, eObject); 
        MappingClass mappingClass = (MappingClass) eObject;       
        return mappingClass.getColumns();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getIndexes(org.eclipse.emf.ecore.EObject)
     */
    public Collection getIndexes(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getUniqueKeys(org.eclipse.emf.ecore.EObject)
     */
    public Collection getUniqueKeys(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getForeignKeys(org.eclipse.emf.ecore.EObject)
     */
    public Collection getForeignKeys(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getPrimaryKey(org.eclipse.emf.ecore.EObject)
     */
    public Object getPrimaryKey(EObject eObject) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getAccessPatterns(org.eclipse.emf.ecore.EObject)
     */
    public Collection getAccessPatterns(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getCardinality(org.eclipse.emf.ecore.EObject)
     */
    public int getCardinality(EObject eObject) {
        return 0;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getTableType(org.eclipse.emf.ecore.EObject)
     */
    public int getTableType(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClass.class, eObject);
        if(eObject instanceof StagingTable) {
            return MetadataConstants.TABLE_TYPES.XML_STAGING_TABLE_TYPE;
        }

        return MetadataConstants.TABLE_TYPES.XML_MAPPING_CLASS_TYPE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumnSetType()
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.TABLE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     */
    public boolean isMappable(EObject eObject, int mappingType) {
        if (isVirtual(eObject)) {
            return (mappingType == SqlTableAspect.MAPPINGS.SQL_TRANSFORM);
        }
        
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canAcceptTransformationSource(EObject target, EObject source) {
        ArgCheck.isInstanceOf(MappingClass.class, target);
        ArgCheck.isNotNull(source);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        SqlAspect sourceSqlAspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(source);
        if(sourceSqlAspect instanceof SqlTableAspect) {
            if(source instanceof StagingTable) {
                return true;
            } else if(source instanceof MappingClass) {
                return false;
            } else if(source instanceof InputSet && target instanceof StagingTable) {
                return false;
            }
            return true;
        } else if(sourceSqlAspect instanceof SqlProcedureAspect) {
            if(target instanceof StagingTable || target instanceof MappingClass) {
                return true;
            }
        }
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canBeTransformationSource(EObject source, EObject target) {
        ArgCheck.isInstanceOf(MappingClass.class, source);
        ArgCheck.isNotNull(target);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        if(source instanceof StagingTable && target instanceof MappingClass) {
            return true;
        }
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#setSupportsUpdate(org.eclipse.emf.ecore.EObject, boolean)
     */
    public void setSupportsUpdate(EObject eObject, boolean supportsUpdate) {
        // do nothis mapping class is never updatable
    }

}
