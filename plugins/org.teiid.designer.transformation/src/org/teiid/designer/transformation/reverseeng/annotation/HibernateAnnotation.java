/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng.annotation;


import java.util.ArrayList;
import java.util.List;

import org.teiid.designer.transformation.reverseeng.api.AnnotationType;
import org.teiid.designer.transformation.reverseeng.api.Column;
import org.teiid.designer.transformation.reverseeng.api.Table;
import org.teiid.designer.transformation.reverseeng.api.TypesMapping;

/**
 * @author vanhalbert
 *
 */
public class HibernateAnnotation implements AnnotationType {
	public static final String TABLE_INDEX = "@Indexed";
	public static final String COLUMN_INDEX = "@Field";
	
	public static final String INDEXED_YES = "index=Index.YES";
	public static final String INDEXED_NO = "index=Index.NO";
	
	private static List<String> IMPORTS;
	
	static {
		IMPORTS = new ArrayList<String>();
		
		IMPORTS.add("import org.hibernate.search.annotations.*;");

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.transformation.reverseeng.api.AnnotationType#getClassAnnotation(org.teiid.designer.transformation.reverseeng.api.Table)
	 */
	public String getClassAnnotation(Table t) {
//		@Indexed(index="Trade")
		return TABLE_INDEX ;
		//+ ";" "(index=" + t.getClassName() + ")";
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.transformation.reverseeng.api.AnnotationType#getAttributeAnnotation(org.teiid.designer.transformation.reverseeng.api.Column)
	 */
	public String getAttributeAnnotation(Column c) {
//@Field(index=Index.Yes, store=Store.YES, analyze=Analyze.NO)
		if(c.isIndexed()) {
			return COLUMN_INDEX + "(" + INDEXED_YES + ", store=Store.YES, analyze=Analyze.NO)";
		} 
		
		String sql_type = TypesMapping.getSqlNameByType(c.getType());
		if (sql_type == null) {
			throw new UnsupportedOperationException("Column type " + c.getType() + "[" + c.getTypeName() + "]" + "is not defined in the TypesMappings");
		}
		// don't index;  Blob, Clob and Arrays
		if (sql_type == null || sql_type.equals(TypesMapping.SQL_BLOB) || sql_type.equals(TypesMapping.SQL_ARRAY) ||
				 sql_type.equals(TypesMapping.SQL_CLOB)) {
			return COLUMN_INDEX + "(" + INDEXED_NO + ", store=Store.YES, analyze=Analyze.NO)";
			
		} else if (sql_type.equals(TypesMapping.SQL_DATE)) {
			return COLUMN_INDEX + " @DateBridge(resolution=Resolution.MINUTE)";
//			@Field @DateBridge(resolution=Resolution.MINUTE)
		}
	
		return COLUMN_INDEX + "(" + INDEXED_YES + ", store=Store.YES, analyze=Analyze.NO)";
	
	}
	
	public String getGetterMethodAnnotation(Column c) {
		return null;
	}

	
	/**
	 * {@inheritDoc}
	 *
	 * @see org.teiid.designer.transformation.reverseeng.api.AnnotationType#getImports()
	 */
	public List<String> getImports() {
		return IMPORTS;
	}
}
