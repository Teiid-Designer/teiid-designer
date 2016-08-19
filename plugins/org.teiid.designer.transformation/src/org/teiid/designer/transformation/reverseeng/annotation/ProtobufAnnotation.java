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
public class ProtobufAnnotation implements AnnotationType {
	public static final String TABLE_INDEX = "@ProtoDoc(@Indexed)";
	public static final String PROTODOC = "@ProtoDoc";
	public static final String PROTOFIELD = "@ProtoField";
	
	public static final String INDEXED_YES = "index = true";
	public static final String INDEXED_NO = "index = false";
	
	private static List<String> IMPORTS;
	
	static {
		IMPORTS = new ArrayList<String>();
		
		IMPORTS.add("import org.infinispan.protostream.annotations.ProtoDoc;");
		IMPORTS.add("import org.infinispan.protostream.annotations.ProtoField;");

	}	

	public String getClassAnnotation(Table t) {
//		@ProtoDoc("@Indexed")
		return null;
//		return TABLE_INDEX;
	}
	
	public String getAttributeAnnotation(Column c) {
		return "";	
	}
	

	public String getGetterMethodAnnotation(Column c) {
		
//		 @ProtoDoc("@IndexedField(index = true, store = false)")
//		 @ProtoField(number = 2, required = true)

			if(c.isIndexed()) {
				return getAnnotation(INDEXED_YES, "true", c);
//						PROTODOC + "\"(@IndexedField(" + INDEXED_YES + ", store = true)\r\t" + PROTOFIELD + "(number = " + c.getOrder() + ")\")";
			} 
			
			String sql_type = TypesMapping.getSqlNameByType(c.getType());
			// don't index;  Blob, Clob and Arrays
			if (sql_type.equals(TypesMapping.SQL_BLOB) || sql_type.equals(TypesMapping.SQL_ARRAY) ||
					 sql_type.equals(TypesMapping.SQL_CLOB)) {
				return getAnnotation(INDEXED_NO, "false", c);
						//PROTODOC + "(@IndexedField(" + INDEXED_NO + ", store = false)\r\t" + PROTOFIELD + "(number = " + c.getOrder() + ")";
			} 
			
			return getAnnotation(INDEXED_YES, "true", c);
					//PROTODOC + "(@IndexedField(" + INDEXED_YES + ", store = true)\r\t" + PROTOFIELD + "(number = " + c.getOrder() + ")";
	}
	private String getAnnotation(String index, String store, Column col) {
//		return PROTODOC + "(\"@IndexedField(" + index + ", store = " + store + ")\")\r\t" + PROTOFIELD + "(number = " + order + ")";
		return "\t" + PROTOFIELD + "(number = " + col.getOrder() + ", required = " +  (col.isRequired() ? col.isRequired() : col.isMandatory()) + ")";

	}
	
	public List<String> getImports() {
		return IMPORTS;
	}
	
}
