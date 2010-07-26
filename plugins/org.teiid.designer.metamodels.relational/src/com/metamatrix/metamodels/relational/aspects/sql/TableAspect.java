/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.View;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * BaseTableAspect
 */
public class TableAspect extends RelationalEntityAspect implements SqlTableAspect {

    public TableAspect(final MetamodelEntity entity) {
        super(entity);   
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlBaseTableAspect#supportsUpdate(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsUpdate(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;       
        return table.isSupportsUpdate();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getTableType(org.eclipse.emf.ecore.EObject)
     */
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
        }
        return MetadataConstants.TABLE_TYPES.TABLE_TYPE;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isSystem(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSystem(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;       
        return table.isSystem();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getMaterializedTableId(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlBaseTableAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isMaterialized(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isMaterialized(EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table table = (Table) eObject;       
        return table.isMaterialized();
    }   

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlBaseTableAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    public List getColumns(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table baseTable = (Table) eObject;       
        return baseTable.getColumns();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlBaseTableAspect#getIndexes(org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlBaseTableAspect#getForeignKeys(org.eclipse.emf.ecore.EObject)
     */
    public Collection getForeignKeys(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject);
        if(eObject instanceof BaseTable){ 
            BaseTable baseTable = (BaseTable) eObject;        
            return baseTable.getForeignKeys();
        }
        
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlBaseTableAspect#getPrimaryKey(org.eclipse.emf.ecore.EObject)
     */
    public Object getPrimaryKey(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
		if(eObject instanceof BaseTable){ 
			BaseTable baseTable = (BaseTable) eObject;        
			return baseTable.getPrimaryKey();
		}
		return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getUniqueKeys(org.eclipse.emf.ecore.EObject)
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlBaseTableAspect#getAccessPatterns(org.eclipse.emf.ecore.EObject)
     */
    public Collection getAccessPatterns(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table baseTable = (Table) eObject;       
        return baseTable.getAccessPatterns();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlBaseTableAspect#getCardinality(org.eclipse.emf.ecore.EObject)
     */
    public int getCardinality(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Table.class, eObject);
        Table baseTable = (Table) eObject;       
        return baseTable.getCardinality();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getType()
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.TABLE;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.TABLE);
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     */
    public boolean isMappable(final EObject eObject, final int mappingType) {
        if(isVirtual(eObject)) {
            return (mappingType == SqlTableAspect.MAPPINGS.SQL_TRANSFORM);
        }
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(final EObject targetObject, final EObject sourceObject) {

    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#setSupportsUpdate(org.eclipse.emf.ecore.EObject, boolean)
     */
    public void setSupportsUpdate(final EObject eObject, final boolean supportsUpdate) {
        CoreArgCheck.isInstanceOf(Table.class, eObject); 
        Table baseTable = (Table) eObject;
        baseTable.setSupportsUpdate(supportsUpdate);
    }

}
