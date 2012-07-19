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

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.common.vdb.SystemVdbUtility;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.StagingTable;


/**
 * MappingClassSqlAspect
 *
 * @since 8.0
 */
public class InputSetSqlAspect extends AbstractTransformationSqlAspect implements SqlTableAspect {

    private static final String INPUT_SET_FULL_NAME = "INPUT"; //$NON-NLS-1$

    /**
     * Construct an instance of MappingClassSqlAspect.
     * 
     */
    public InputSetSqlAspect(MetamodelEntity entity) {
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
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isSystem(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSystem(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputSet.class, eObject);
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
        CoreArgCheck.isInstanceOf(InputSet.class, eObject); 
        InputSet inputSet = (InputSet) eObject;       
        return inputSet.getInputParameters();
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
        CoreArgCheck.isInstanceOf(InputSet.class, eObject); 
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
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean canAcceptTransformationSource(EObject target, EObject source) {
        CoreArgCheck.isInstanceOf(InputSet.class, target);
        CoreArgCheck.isNotNull(source);
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean canBeTransformationSource(EObject source, EObject target) {
        CoreArgCheck.isInstanceOf(InputSet.class, source);
        CoreArgCheck.isNotNull(target);
        if(target instanceof MappingClass && !(target instanceof StagingTable)) {
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

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputSet.class, eObject); 
        return INPUT_SET_FULL_NAME;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getNameInSource(EObject eObject) {
        return null;
    }

}
