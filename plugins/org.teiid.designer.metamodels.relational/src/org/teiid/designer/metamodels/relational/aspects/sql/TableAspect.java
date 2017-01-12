/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueKey;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.metamodels.relational.util.RelationalUtil;


/**
 * BaseTableAspect
 *
 * @since 8.0
 */
public class TableAspect extends RelationalEntityAspect implements SqlTableAspect {

    public TableAspect(final MetamodelEntity entity) {
        super(entity);   
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlBaseTableAspect#supportsUpdate(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsUpdate(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;       
        return table.isSupportsUpdate();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getTableType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getTableType(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;
        if(table instanceof View) {
            return MetadataConstants.TABLE_TYPES.VIEW_TYPE;
        }
        
        // Check if the table belongs to a materialized view model ...
        final Resource resource = eObject.eResource();
        if (resource instanceof EmfResource) {
            final ModelAnnotation annot = ((EmfResource)resource).getModelAnnotation();
            if (annot.getModelType() == ModelType.MATERIALIZATION_LITERAL) {
                return MetadataConstants.TABLE_TYPES.MATERIALIZED_TYPE;
            }
            
            if( annot.getModelType() == ModelType.VIRTUAL_LITERAL && RelationalUtil.isGlobalTempTable(eObject)) {
            	return MetadataConstants.TABLE_TYPES.GLOBAL_TEMPORARY_TABLE_TYPE;
            }
        }
        return MetadataConstants.TABLE_TYPES.TABLE_TYPE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isSystem(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSystem(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;       
        return table.isSystem();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getMaterializedTableId(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getMaterializedTableId(EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;
        if( table != null ) {
        	Table materializedTable = table.getMaterializedTable();
        	if( materializedTable != null ) {
        		return ModelerCore.getObjectId(materializedTable).toString();
        	}
        }
        
        return null;
    }    

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlBaseTableAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isVirtual(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;
    	try {    
			ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(table);
            return (ma != null && ma.getModelType().getValue() == ModelType.VIRTUAL);
    	} catch(Exception e) {
			RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
    	}

    	return false;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isMaterialized(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isMaterialized(EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;       
        return table.isMaterialized();
    }   
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isMaterialized(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isGlobalTempTable(EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        // Find extension property, if it doesnt exist, it's "false"
        // else check the value and return proper boolean
        Properties props = new Properties();
        
        try {
			props = ExtensionPlugin.getInstance().getModelExtensionAssistantAggregator().getPropertyValues(eObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if( props == null || props.isEmpty() ) return false;
        
        for( Object key : props.keySet() ) {
        	String keyStr = (String)key;
        	if( keyStr.equalsIgnoreCase("global-temp-table")) {
        		String value = props.getProperty(keyStr);
        		return Boolean.parseBoolean(value);
        	}
        }
        return false;
    }   

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlBaseTableAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getColumns(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table baseTable = (Table) eObject;       
        return baseTable.getColumns();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlBaseTableAspect#getIndexes(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getIndexes(final EObject eObject) {
        // Go through the columns and grab the indexes used by each column
        final List results = new ArrayList();
        if ( eObject instanceof BaseTable ) {
            final BaseTable baseTable = (BaseTable)eObject;
            final Iterator iter = baseTable.getColumns().iterator();
            while (iter.hasNext()) {
                final Column column = (Column)iter.next();
                // For each column, get the indexes that reference the column
                final List indexesUsingColumn = column.getIndexes();
                
                // Add indexes to the results, ensuring not to add the same index more than once
                final Iterator indexIter = indexesUsingColumn.iterator();
                while (indexIter.hasNext()) {
                    final Index index = (Index)indexIter.next();
                    if (!results.contains(index) ) {
                        results.add(index);
                    }
                }
            }
        }
        return results;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlBaseTableAspect#getForeignKeys(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getForeignKeys(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject);
        if(eObject instanceof BaseTable){ 
            BaseTable baseTable = (BaseTable) eObject;        
            return baseTable.getForeignKeys();
        }
        
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlBaseTableAspect#getPrimaryKey(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getPrimaryKey(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
		if(eObject instanceof BaseTable){ 
			BaseTable baseTable = (BaseTable) eObject;        
			return baseTable.getPrimaryKey();
		}
		return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getUniqueKeys(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getUniqueKeys(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        // Go through the columns and grab the indexes used by each column
        final List results = new ArrayList();
        if ( eObject instanceof BaseTable ) {
            final BaseTable table = (BaseTable)eObject;
            final Iterator iter = table.getColumns().iterator();
            while (iter.hasNext()) {
                final Column column = (Column)iter.next();
                // For each column, get the unique keys that reference the column
                final List ukeysUsingColumn = column.getUniqueKeys();
                
                // Add indexes to the results, ensuring not to add the same index more than once
                final Iterator uKIter = ukeysUsingColumn.iterator();
                while (uKIter.hasNext()) {
                    final UniqueKey uniqueKey = (UniqueKey) uKIter.next();
                    if (!results.contains(uniqueKey) ) {
                        results.add(uniqueKey);
                    }
                }
            }
        }
        return results;
    }    

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlBaseTableAspect#getAccessPatterns(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getAccessPatterns(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table baseTable = (Table) eObject;       
        return baseTable.getAccessPatterns();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlBaseTableAspect#getCardinality(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getCardinality(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject);
        Table baseTable = (Table) eObject;       
        return baseTable.getCardinality();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getType()
     */
    @Override
	public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.TABLE;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.TABLE);
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public boolean isMappable(final EObject eObject, final int mappingType) {
        if(isVirtual(eObject)) {
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
        CoreArgCheck.isInstanceOf(Table.class, target);
        CoreArgCheck.isNotNull(source);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        if(isVirtual(target)) {
            SqlAspect sourceAspect = SqlAspectHelper.getSqlAspect(source);            
            if(sourceAspect instanceof SqlTableAspect || sourceAspect instanceof SqlProcedureAspect) {
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
        CoreArgCheck.isInstanceOf(Table.class, source);
        CoreArgCheck.isNotNull(target);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        SqlAspect targetAspect = SqlAspectHelper.getSqlAspect(target);            
        if(targetAspect instanceof SqlTableAspect) {
            return ((SqlTableAspect) targetAspect).isVirtual(target);
        } else if(targetAspect instanceof SqlProcedureAspect) {
            return ((SqlProcedureAspect) targetAspect).isVirtual(target);
        }
        return false;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(final EObject targetObject, final EObject sourceObject) {

    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#setSupportsUpdate(org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
	public void setSupportsUpdate(final EObject eObject, final boolean supportsUpdate) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table baseTable = (Table) eObject;
        baseTable.setSupportsUpdate(supportsUpdate);
    }

}
