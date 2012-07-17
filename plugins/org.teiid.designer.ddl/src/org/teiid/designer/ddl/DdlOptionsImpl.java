/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl;

import org.teiid.designer.core.xslt.Style;


/**
 * DdlOptionsImpl
 */
public class DdlOptionsImpl implements DdlOptions {

    private Style style;
    private boolean generateSchema;
    private boolean generateDropStatements;
	private boolean generateTableComments;
    private boolean generateColumnComments;
    private boolean generateInfoComments;
    private boolean nameInSourceUsed;
    private boolean nativeTypeUsed;
    private boolean enforceUniqueNames;

    /**
     * Construct an instance of DdlOptionsImpl.
     * 
     */
    public DdlOptionsImpl() {
        super();
        this.generateInfoComments = DEFAULT_GENERATE_INFO_COMMENTS;
        this.generateTableComments = DEFAULT_GENERATE_TABLE_COMMENTS;
        this.generateColumnComments = DEFAULT_GENERATE_COLUMN_COMMENTS;
        this.generateDropStatements = DEFAULT_GENERATE_DROPS;
        this.generateSchema = DEFAULT_GENERATE_SCHEMA;
        this.nameInSourceUsed = DEFAULT_USE_NAME_IN_SOURCE;
        this.nativeTypeUsed = DEFAULT_USE_NATIVE_TYPE;
        this.enforceUniqueNames = DEFAULT_ENFORCE_UNIQUE_NAMES;
    }

    /**
     * Get the style of DDL that is to be written out.
     * @return the style, or null if no style has been specified
     * @see DdlOptions#getStyle()
     */
    @Override
	public Style getStyle() {
        return this.style;
    }
    /**
     * Get the style of DDL that is to be written out.
     * @param style the style, or null if the current style is to be cleared
     * @see DdlOptions#setStyle(Style)
     */
    @Override
	public void setStyle( final Style style ) {
        this.style = style;
    }

    /**
     * Return whether the DDL should contain create statements for the schema(s).
     * @return true if schema creation statements are to be included in the DDL, or
     * false otherwise.
     * @see DdlOptions#isGenerateSchema()
     */
    @Override
	public boolean isGenerateSchema() {
        return generateSchema;
    }

    /**
     * Set whether the DDL should contain create statements for the schema(s).
     * @param includeSchemas true if schema creation statements are to be included in the DDL, or
     * false otherwise.
     * @see DdlOptions#setGenerateSchema(boolean)
     */
    @Override
	public void setGenerateSchema(final boolean generateSchema) {
        this.generateSchema = generateSchema;
    }

    @Override
	public boolean isGenerateTableComments() {
		return generateTableComments;
	}

	@Override
	public void setGenerateTableComments(boolean generateTableComments) {
		this.generateTableComments = generateTableComments;
	}

	@Override
	public boolean isGenerateColumnComments() {
		return generateColumnComments;
	}

   /**
    * Return whether comments are to be put into the DDL
    * @return true if comments are to be generated in the DDL, or false otherwise
    * @see DdlOptions#isGenerateComments()
    */
	@Override
	public void setGenerateColumnComments(final boolean generateColumnComments) {
		this.generateColumnComments = generateColumnComments;
	}

   /**
    * Return whether informational comments are to be put into the DDL
    * @return true if comments are to be generated in the DDL, or false otherwise
    * @see DdlOptions#isGenerateInfoComments()
    */
	@Override
	public boolean isGenerateInfoComments() {
		return generateInfoComments;
	}

   /**
    * Set whether informational comments are to be put into the DDL
    * @see DdlOptions#setGenerateInfoComments()
    */
	@Override
	public void setGenerateInfoComments(final boolean generateInfoComments) {
		this.generateInfoComments = generateInfoComments;
	}

    /**
     * Return whether drop statements are to be put into the DDL
     * @return true if drop statements are to be generated in the DDL, or false otherwise
     * @see DdlOptions#isGenerateDropStatements()
     */
    @Override
	public boolean isGenerateDropStatements() {
        return generateDropStatements;
    }

    /**
     * Set whether drop statements are to be put into the DDL
     * @param generateDrops true if drop statements are to be generated in the DDL, or false otherwise
     * @see DdlOptions#setGenerateDropStatements(boolean)
     */
    @Override
	public void setGenerateDropStatements(final boolean generateDrops) {
        this.generateDropStatements = generateDrops;
    }

    /**
     * Determine whether names in source should be used in the DDL, or whether only names should be used.
     * This option does not apply when there are no names in source.
     * @return true if entities' name in source rather than their name should be used for the object
     * names in the DDL, or false if only their names should be used.
     */
    @Override
	public boolean isNameInSourceUsed() {
        return this.nameInSourceUsed;
    }

    /**
     * Set whether names in source should be used in the DDL, or whether only names should be used.
     * This option does not apply when there are no names in source.
     * @param useNameInSource true if entities' name in source rather than their name should be used for the object
     * names in the DDL, or false if only their names should be used.
     */
    @Override
	public void setNameInSourceUsed( final boolean useNameInSource) {
        this.nameInSourceUsed = useNameInSource;
    }

    /**
     * @see org.teiid.designer.ddl.DdlOptions#isNativeTypeUsed()
     */
    @Override
	public boolean isNativeTypeUsed() {
        return this.nativeTypeUsed;
    }

    /**
     * @see org.teiid.designer.ddl.DdlOptions#setNativeTypeUsed(boolean)
     */
    @Override
	public void setNativeTypeUsed(boolean useNativeType) {
        this.nativeTypeUsed = useNativeType;
    }

    /**
     * @see org.teiid.designer.ddl.DdlOptions#isUniqueNamesEnforced()
     */
    @Override
	public boolean isUniqueNamesEnforced() {
        return this.enforceUniqueNames;
    }

    /**
     * @see org.teiid.designer.ddl.DdlOptions#setUniqueNamesEnforced(boolean)
     */
    @Override
	public void setUniqueNamesEnforced(boolean useUniqueNames) {
        this.enforceUniqueNames = useUniqueNames;        
    }


}
