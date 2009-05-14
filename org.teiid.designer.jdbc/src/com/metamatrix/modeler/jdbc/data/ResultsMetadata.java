/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.data;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ResultsMetadata
 */
public class ResultsMetadata {

    public static final int NULLABLE_UNKNOWN = ResultSetMetaData.columnNullableUnknown;
    public static final int NULLABLE = ResultSetMetaData.columnNullable;
    public static final int NOT_NULLABLE = ResultSetMetaData.columnNoNulls;

    public class ColumnMetadata {
        private String name;
        private String label;
        private int type;
        private String typeName;
        private String className;
        private int displaySize;
        private int precision;
        private int scale;
        private String tableName;
        private String schemaName;
        private String catalogName;
        private boolean autoIncrement;
        private boolean caseSensitive;
        private boolean currency;
        private boolean definitelyWritable;
        private int nullable;
        private boolean readOnly;
        private boolean searchable;
        private boolean signed;
        private boolean writable;

        protected ColumnMetadata() {
        }

        /**
         * @return
         */
        public boolean isAutoIncrement() {
            return autoIncrement;
        }

        /**
         * @return
         */
        public boolean isCaseSensitive() {
            return caseSensitive;
        }

        /**
         * @return
         */
        public String getCatalogName() {
            return catalogName;
        }

        /**
         * @return
         */
        public String getClassName() {
            return className;
        }

        /**
         * @return
         */
        public boolean isCurrency() {
            return currency;
        }

        /**
         * @return
         */
        public boolean isDefinitelyWritable() {
            return definitelyWritable;
        }

        /**
         * @return
         */
        public int getDisplaySize() {
            return displaySize;
        }

        /**
         * @return
         */
        public String getLabel() {
            return label;
        }

        /**
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * @return
         */
        public int getNullable() {
            return nullable;
        }

        /**
         * @return
         */
        public int getPrecision() {
            return precision;
        }

        /**
         * @return
         */
        public boolean isReadOnly() {
            return readOnly;
        }

        /**
         * @return
         */
        public int getScale() {
            return scale;
        }

        /**
         * @return
         */
        public String getSchemaName() {
            return schemaName;
        }

        /**
         * @return
         */
        public boolean isSearchable() {
            return searchable;
        }

        /**
         * @return
         */
        public boolean isSigned() {
            return signed;
        }

        /**
         * @return
         */
        public String getTableName() {
            return tableName;
        }

        /**
         * @return
         */
        public int getType() {
            return type;
        }

        /**
         * @return
         */
        public String getTypeName() {
            return typeName;
        }

        /**
         * @return
         */
        public boolean isWritable() {
            return writable;
        }

        /**
         * @param b
         */
        protected void setAutoIncrement( boolean b ) {
            autoIncrement = b;
        }

        /**
         * @param b
         */
        protected void setCaseSensitive( boolean b ) {
            caseSensitive = b;
        }

        /**
         * @param string
         */
        protected void setCatalogName( String string ) {
            catalogName = string;
        }

        /**
         * @param string
         */
        protected void setClassName( String string ) {
            className = string;
        }

        /**
         * @param b
         */
        protected void setCurrency( boolean b ) {
            currency = b;
        }

        /**
         * @param b
         */
        protected void setDefinitelyWritable( boolean b ) {
            definitelyWritable = b;
        }

        /**
         * @param i
         */
        protected void setDisplaySize( int i ) {
            displaySize = i;
        }

        /**
         * @param string
         */
        protected void setLabel( String string ) {
            label = string;
        }

        /**
         * @param string
         */
        protected void setName( String string ) {
            name = string;
        }

        /**
         * @param i
         */
        protected void setNullable( int i ) {
            nullable = i;
        }

        /**
         * @param i
         */
        protected void setPrecision( int i ) {
            precision = i;
        }

        /**
         * @param b
         */
        protected void setReadOnly( boolean b ) {
            readOnly = b;
        }

        /**
         * @param i
         */
        protected void setScale( int i ) {
            scale = i;
        }

        /**
         * @param string
         */
        protected void setSchemaName( String string ) {
            schemaName = string;
        }

        /**
         * @param b
         */
        protected void setSearchable( boolean b ) {
            searchable = b;
        }

        /**
         * @param b
         */
        protected void setSigned( boolean b ) {
            signed = b;
        }

        /**
         * @param string
         */
        protected void setTableName( String string ) {
            tableName = string;
        }

        /**
         * @param i
         */
        protected void setType( int i ) {
            type = i;
        }

        /**
         * @param string
         */
        protected void setTypeName( String string ) {
            typeName = string;
        }

        /**
         * @param b
         */
        protected void setWritable( boolean b ) {
            writable = b;
        }

    }

    private List columnMetadata;

    /**
     * Construct an instance of ResultsMetadata.
     */
    protected ResultsMetadata() {
        super();
        this.columnMetadata = new ArrayList();
    }

    protected void set( final ResultSetMetaData rsMetadata ) throws SQLException {

        final int numColumns = rsMetadata.getColumnCount();
        for (int i = 1; i <= numColumns; ++i) {
            final ColumnMetadata m = new ColumnMetadata();
            m.setCatalogName(rsMetadata.getCatalogName(i));
            m.setClassName(rsMetadata.getColumnClassName(i));
            m.setDisplaySize(rsMetadata.getColumnDisplaySize(i));
            m.setLabel(rsMetadata.getColumnLabel(i));
            m.setName(rsMetadata.getColumnName(i));
            m.setNullable(rsMetadata.isNullable(i));
            m.setPrecision(rsMetadata.getPrecision(i));
            m.setScale(rsMetadata.getScale(i));
            m.setSchemaName(rsMetadata.getSchemaName(i));
            m.setTableName(rsMetadata.getTableName(i));
            m.setType(rsMetadata.getColumnType(i));
            m.setAutoIncrement(rsMetadata.isAutoIncrement(i));
            m.setCaseSensitive(rsMetadata.isCaseSensitive(i));
            m.setCurrency(rsMetadata.isCurrency(i));
            m.setDefinitelyWritable(rsMetadata.isDefinitelyWritable(i));
            m.setReadOnly(rsMetadata.isReadOnly(i));
            m.setSearchable(rsMetadata.isSearchable(i));
            m.setSigned(rsMetadata.isSigned(i));
            m.setWritable(rsMetadata.isWritable(i));
            this.columnMetadata.add(m);
        }
        this.columnMetadata = Collections.unmodifiableList(this.columnMetadata);
    }

    protected void set( final String catalogName,
                        final String schemaName,
                        final String tableName,
                        final Object objectValue ) {
        final int length = objectValue.toString().length();
        final ColumnMetadata m = new ColumnMetadata();
        m.setCatalogName(catalogName);
        m.setClassName(objectValue.getClass().getName());
        m.setDisplaySize(length);
        m.setLabel(""); //$NON-NLS-1$
        m.setName(m.getLabel());
        m.setNullable(NOT_NULLABLE);
        m.setPrecision(length);
        m.setScale(0);
        m.setSchemaName(schemaName);
        m.setTableName(tableName);
        m.setAutoIncrement(false);
        m.setCaseSensitive(false);
        m.setCurrency(false);
        m.setDefinitelyWritable(false);
        m.setReadOnly(true);
        m.setSearchable(false);
        m.setSigned(false);
        m.setWritable(false);
        if (objectValue instanceof Integer) {
            m.setType(Types.INTEGER);
        } else if (objectValue instanceof Long) {
            m.setType(Types.NUMERIC);
        } else if (objectValue instanceof Short) {
            m.setType(Types.SMALLINT);
        } else if (objectValue instanceof Double) {
            m.setType(Types.DOUBLE);
        } else if (objectValue instanceof Float) {
            m.setType(Types.FLOAT);
        } else if (objectValue instanceof Boolean) {
            m.setType(Types.BIT);
        } else if (objectValue instanceof String) {
            m.setType(Types.VARCHAR);
        } else {
            m.setType(Types.VARCHAR);
        }
        this.columnMetadata.add(columnMetadata);
        this.columnMetadata = Collections.unmodifiableList(this.columnMetadata);
    }

    public List getColumnMetadata() {
        return this.columnMetadata;
    }

    public ColumnMetadata getColumnMetadata( int columnIndex ) {
        return (ColumnMetadata)this.columnMetadata.get(columnIndex);
    }

    public static ResultsMetadata create( final ResultSetMetaData rsMetadata ) throws SQLException {
        final ResultsMetadata metadata = new ResultsMetadata();
        metadata.set(rsMetadata);
        return metadata;
    }

    public static ResultsMetadata create( final String catalogName,
                                          final String schemaName,
                                          final String tableName,
                                          final Object objectValue ) {
        final ResultsMetadata metadata = new ResultsMetadata();
        metadata.set(catalogName, schemaName, tableName, objectValue);
        return metadata;
    }

}
