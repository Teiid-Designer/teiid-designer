/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ddl;

import com.metamatrix.core.xslt.Style;
import com.metamatrix.modeler.ddl.DdlOptions;

/**
 * DdlOptionsImpl
 */
public class DdlOptionsImpl implements DdlOptions {

    private Style style;
    private boolean generateSchema;
    private boolean generateDropStatements;
    private boolean generateComments;
    private boolean nameInSourceUsed;
    private boolean nativeTypeUsed;
    private boolean enforceUniqueNames;

    /**
     * Construct an instance of DdlOptionsImpl.
     * 
     */
    public DdlOptionsImpl() {
        super();
        this.generateComments = DEFAULT_GENERATE_COMMENTS;
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
    public Style getStyle() {
        return this.style;
    }
    /**
     * Get the style of DDL that is to be written out.
     * @param style the style, or null if the current style is to be cleared
     * @see DdlOptions#setStyle(Style)
     */
    public void setStyle( final Style style ) {
        this.style = style;
    }

    /**
     * Return whether the DDL should contain create statements for the schema(s).
     * @return true if schema creation statements are to be included in the DDL, or
     * false otherwise.
     * @see DdlOptions#isGenerateSchema()
     */
    public boolean isGenerateSchema() {
        return generateSchema;
    }

    /**
     * Set whether the DDL should contain create statements for the schema(s).
     * @param includeSchemas true if schema creation statements are to be included in the DDL, or
     * false otherwise.
     * @see DdlOptions#setGenerateSchema(boolean)
     */
    public void setGenerateSchema(final boolean generateSchema) {
        this.generateSchema = generateSchema;
    }

    /**
     * Return whether comments are to be put into the DDL
     * @return true if comments are to be generated in the DDL, or false otherwise
     * @see DdlOptions#isGenerateComments()
     */
    public boolean isGenerateComments() {
        return generateComments;
    }

    /**
     * Return whether drop statements are to be put into the DDL
     * @return true if drop statements are to be generated in the DDL, or false otherwise
     * @see DdlOptions#isGenerateDropStatements()
     */
    public boolean isGenerateDropStatements() {
        return generateDropStatements;
    }

    /**
     * Set whether comments are to be put into the DDL
     * @param generateDrops true if comments are to be generated in the DDL, or false otherwise
     * @see DdlOptions#setGenerateComments(boolean)
     */
    public void setGenerateComments(final boolean generateComments) {
        this.generateComments = generateComments;
    }

    /**
     * Set whether drop statements are to be put into the DDL
     * @param generateDrops true if drop statements are to be generated in the DDL, or false otherwise
     * @see DdlOptions#setGenerateDropStatements(boolean)
     */
    public void setGenerateDropStatements(final boolean generateDrops) {
        this.generateDropStatements = generateDrops;
    }

    /**
     * Determine whether names in source should be used in the DDL, or whether only names should be used.
     * This option does not apply when there are no names in source.
     * @return true if entities' name in source rather than their name should be used for the object
     * names in the DDL, or false if only their names should be used.
     */
    public boolean isNameInSourceUsed() {
        return this.nameInSourceUsed;
    }

    /**
     * Set whether names in source should be used in the DDL, or whether only names should be used.
     * This option does not apply when there are no names in source.
     * @param useNameInSource true if entities' name in source rather than their name should be used for the object
     * names in the DDL, or false if only their names should be used.
     */
    public void setNameInSourceUsed( final boolean useNameInSource) {
        this.nameInSourceUsed = useNameInSource;
    }

    /**
     * @see com.metamatrix.modeler.ddl.DdlOptions#isNativeTypeUsed()
     */
    public boolean isNativeTypeUsed() {
        return this.nativeTypeUsed;
    }

    /**
     * @see com.metamatrix.modeler.ddl.DdlOptions#setNativeTypeUsed(boolean)
     */
    public void setNativeTypeUsed(boolean useNativeType) {
        this.nativeTypeUsed = useNativeType;
    }

    /**
     * @see com.metamatrix.modeler.ddl.DdlOptions#isUniqueNamesEnforced()
     */
    public boolean isUniqueNamesEnforced() {
        return this.enforceUniqueNames;
    }

    /**
     * @see com.metamatrix.modeler.ddl.DdlOptions#setUniqueNamesEnforced(boolean)
     */
    public void setUniqueNamesEnforced(boolean useUniqueNames) {
        this.enforceUniqueNames = useUniqueNames;        
    }

}
