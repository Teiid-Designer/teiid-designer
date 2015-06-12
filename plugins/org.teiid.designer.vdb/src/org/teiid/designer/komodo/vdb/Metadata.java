/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import org.teiid.designer.vdb.VdbUnit;


/**
 * 
 * FROM TEIID DOC
 * 
 * The metadata repository type. Defaults to INDEX for Designer VDBs and NATIVE for 
 * non-Designer VDB source models. For all other deployments/models a value must be specified. 
 * Built-in types include DDL, NATIVE, INDEX, and DDL-FILE. The usage of the raw text varies 
 * with the by type. NATIVE and INDEX (only for Designer VDBs) metadata repositories do not 
 * use the raw text. The raw text for DDL is expected to be be a series of DDL statements that 
 * define the schema - see also DDL Metadata. DDL-FILE (used only with zip deployments) is 
 * similar to DDL, except that the raw text specifies an absolute path relative to the vdb 
 * root of the location of a file containing the DDL. See also Custom Metadata Repository. 
 * Use more than 1 metadata element to define multiple sources of metadata.
 * 
 * @author blafond
 *
 */
public class Metadata extends VdbUnit {

	Type type;
	String schemaText;
    /**
     * The type of a model.
     */
	@SuppressWarnings("javadoc")
    public enum Type {

        
		DDL,
        DDL_FILE,
        NATIVE,
        INDEX;

        /**
         * The default model type. Value is {@value} .
         */
        public static final Type DEFAULT_VALUE = DDL;

        /**
         * @return actual type
         */
        public String getType() {
            String name = name().replace(
            		UNDERSCORE, 
            		DASH);
            
            return name;
        }

        /** (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return getType();
        }
    }

	/**
	 * 
	 */
	public Metadata() {
		type = Type.DDL;
		schemaText = EMPTY_STRING;
	}
	
	/**
	 * @param schemaText
	 * @param type
	 */
	public Metadata(String schemaText, String type) {
		this();
		this.schemaText = schemaText;
		this.type = getTypeForString(type);
	}

	/**
	 * @return the metadata type
	 */
	public Type getType() {
		return type;
	}
	
    /**
     * @param type
     * @return enum type
     */
    private Type getTypeForString(String type) {
    	if( type.toUpperCase().equals(Type.DDL.toString())) {
    		return Metadata.Type.DDL;
    	}
    	if( type.toUpperCase().equals(Type.DDL_FILE.toString())) {
    		return Metadata.Type.DDL_FILE;
    	}
    	if( type.toUpperCase().equals(Type.NATIVE.toString())) {
    		return Metadata.Type.NATIVE;
    	}
    	if( type.toUpperCase().equals(Type.INDEX.toString())) {
    		return Metadata.Type.INDEX;
    	}
    	
    	return Metadata.Type.DDL;
    }

	/**
	 * @param type
	 */
	public void setType(Type type) {
		if( this.type != type ) {
			this.type = type;
			setChanged(true);
		}
	}
	
	/**
	 * @param type
	 */
	public void setType(String type) {
		Type theType = getTypeForString(type);
		setType(theType);
	}

	/**
	 * @return the schema text
	 */
	public String getSchemaText() {
		return schemaText;
	}

	/**
	 * @param newSchemaText
	 */
	public void setSchemaText(String newSchemaText) {
		setChanged(this.schemaText, newSchemaText);
		this.schemaText = newSchemaText;
	}

}
