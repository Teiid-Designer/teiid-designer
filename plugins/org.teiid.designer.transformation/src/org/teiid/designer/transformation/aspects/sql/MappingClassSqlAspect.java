/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.sql;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.common.vdb.SystemVdbUtility;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.StagingTable;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * MappingClassSqlAspect
 *
 * @since 8.0
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
     * @see org.teiid.designer.transformation.aspects.sql.MappingClassObjectSqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.TABLE);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isQueryable(final EObject eObject) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#supportsUpdate(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsUpdate(EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isVirtual(EObject eObject) {
        CoreArgCheck.isInstanceOf(MappingClass.class, eObject);
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
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isMaterialized(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isMaterialized(EObject eObject) {
        return false;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getMaterializedTableId(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getMaterializedTableId(EObject eObject) {
        return null;
    } 

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isSystem(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSystem(EObject eObject) {
        CoreArgCheck.isInstanceOf(MappingClass.class, eObject);
        String modelName = getModelName(eObject);
        if (modelName != null && SystemVdbUtility.isSystemModelWithSystemTableType(modelName)) {
            return true;
        }
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getColumns(EObject eObject) {
        CoreArgCheck.isInstanceOf(MappingClass.class, eObject); 
        MappingClass mappingClass = (MappingClass) eObject;       
        return mappingClass.getColumns();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getIndexes(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getIndexes(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getUniqueKeys(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getUniqueKeys(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getForeignKeys(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getForeignKeys(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getPrimaryKey(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getPrimaryKey(EObject eObject) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getAccessPatterns(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getAccessPatterns(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getCardinality(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getCardinality(EObject eObject) {
        return 0;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getTableType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getTableType(EObject eObject) {
        CoreArgCheck.isInstanceOf(MappingClass.class, eObject);
        if(eObject instanceof StagingTable) {
            return MetadataConstants.TABLE_TYPES.XML_STAGING_TABLE_TYPE;
        }

        return MetadataConstants.TABLE_TYPES.XML_MAPPING_CLASS_TYPE;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumnSetType()
     */
    @Override
	public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.TABLE;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public boolean isMappable(EObject eObject, int mappingType) {
        if (isVirtual(eObject)) {
            return (mappingType == SqlTableAspect.MAPPINGS.SQL_TRANSFORM);
        }
        
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean canAcceptTransformationSource(EObject target, EObject source) {
        CoreArgCheck.isInstanceOf(MappingClass.class, target);
        CoreArgCheck.isNotNull(source);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        SqlAspect sourceSqlAspect = org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(source);
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
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean canBeTransformationSource(EObject source, EObject target) {
        CoreArgCheck.isInstanceOf(MappingClass.class, source);
        CoreArgCheck.isNotNull(target);
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
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#setSupportsUpdate(org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
	public void setSupportsUpdate(EObject eObject, boolean supportsUpdate) {
        // do nothis mapping class is never updatable
    }

}
