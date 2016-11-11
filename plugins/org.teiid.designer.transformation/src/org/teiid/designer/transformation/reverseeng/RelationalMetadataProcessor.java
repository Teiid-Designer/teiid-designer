/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.xsd.aspects.sql.XsdSimpleTypeDefinitionAspect;
import org.teiid.designer.runtime.spi.TeiidExecutionException;
import org.teiid.designer.transformation.reverseeng.api.Column;
import org.teiid.designer.transformation.reverseeng.api.Column.NullType;
import org.teiid.designer.transformation.reverseeng.api.MetadataProcessor;
import org.teiid.designer.transformation.reverseeng.api.Options;
import org.teiid.designer.transformation.reverseeng.api.RuntimeTypesConstants;
import org.teiid.designer.transformation.reverseeng.api.Table;
/**
 * @author vanhalbert
 *
 */
public final class RelationalMetadataProcessor implements MetadataProcessor {
    private static SqlDatatypeAspect sqlDatatypeAspect;
    
	private List<Table> tableMetadata = new ArrayList<Table>();
	
	@Override
	public List<Table> getTableMetadata() {
		return this.tableMetadata;
	}
	
	
	@Override
	public void loadMetadata( Object metadataSource, Options options) throws TeiidExecutionException {
		performLoad( (org.teiid.designer.metamodels.relational.Table) metadataSource, options);
	}
		
	private void performLoad(org.teiid.designer.metamodels.relational.Table table, Options options) throws TeiidExecutionException {
		
		Table relTable = new Table(table.getName());
		
		this.tableMetadata.add(relTable);
		
		List<org.teiid.designer.metamodels.relational.Column> columns = table.getColumns();
		int orderValue = 1;
		for( org.teiid.designer.metamodels.relational.Column col : columns ) {
			addColumn(col, relTable, orderValue);
			orderValue++;
		}		
			
	}
	
	
	/**
	 * Add a column to the given table based upon the current Column
	 * @param column
	 * @param reltable 
	 */
	private void addColumn(org.teiid.designer.metamodels.relational.Column column, Table reltable, int orderValue)  {

		Column relColumn = reltable.createColumn(column.getName());
		
		SqlDatatypeAspect aspect = getSqlAspect(column.getType());
		
		String runtimeTypeName = aspect.getRuntimeTypeName(column.getType());
		String javaType = RuntimeTypesConstants.getJavaType(runtimeTypeName);
		
//		relColumn.setType(aspect.getRuntimeTypeName(column.getType()));
		relColumn.setTypeName(runtimeTypeName);
		relColumn.setJavaType(javaType);
		relColumn.setOrder(orderValue);
		relColumn.setPrecision(column.getPrecision());
//		relColumn.setMaxLength(aspect.get);
		relColumn.setScale(column.getScale());
		
		if( column.getNullable().getLiteral().equalsIgnoreCase(NullableType.NULLABLE_LITERAL.toString()) ) {
			relColumn.setNullType(NullType.Nullable);
		} else if( column.getNullable().getLiteral().equalsIgnoreCase(NullableType.NO_NULLS_LITERAL.toString()) ) {
			relColumn.setNullType(NullType.No_Nulls);
		} else {
			relColumn.setNullType(NullType.Unknown);
		}
		
		try {
			String desc = ModelerCore.getModelEditor().getDescription(column);
			if( ! StringUtilities.isEmpty(desc) ) {
				relColumn.setRemarks(desc);
			}
		} catch (ModelerCoreException e) {
			e.printStackTrace();
			relColumn.setRemarks(null);
		}
		
		relColumn.setDefaultValue(column.getDefaultValue());
		relColumn.setIsIndexed(true); //column.isIndexed());
		
		// Check if column has PK reference
		boolean isReq = false;
		for( Object eObj : column.getUniqueKeys().toArray() ) {
			if( eObj instanceof PrimaryKey ) {
				isReq = true;
			}
		}

		relColumn.setIsRequired(isReq); 
		
	}
	
    protected SqlDatatypeAspect getSqlAspect( final EObject eObject ) {
        if (eObject != null && eObject instanceof XSDSimpleTypeDefinition) {
            if (ModelerCore.getPlugin() == null) {
                if (sqlDatatypeAspect == null) {
                    sqlDatatypeAspect = new XsdSimpleTypeDefinitionAspect(null);
                }
                return sqlDatatypeAspect;
            }
            // Defect 23839 - rather than calling the metamodel registry to get aspect, use the AspectManager which is caching
            // these aspects!!!!
            SqlAspect sqlAspect = AspectManager.getSqlAspect(eObject);
            if (sqlAspect instanceof SqlDatatypeAspect) {
                return (SqlDatatypeAspect)sqlAspect;
            }
        }
        return null;
    }

}
