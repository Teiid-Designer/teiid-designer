/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl.custom;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.relational.impl.Context;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * OracleModelProcessor
 */
public class PostgresModelProcessor extends RelationalModelProcessorImpl {

    private static final String BOOLEAN_TYPE_NAME   = "BOOL"; //$NON-NLS-1$
//    private static final String BIG_INT_TYPE_NAME   = "INT8"; //$NON-NLS-1$
//    private static final String DBL_TYPE_NAME       = "FLOAT8"; //$NON-NLS-1$
//    private static final String INT_TYPE_NAME       = "INT4"; //$NON-NLS-1$
//    private static final String FLOAT_TYPE_NAME     = "FLOAT4"; //$NON-NLS-1$
//    private static final String SMALL_INT_TYPE_NAME = "INT2"; //$NON-NLS-1$
//    private static final String TIMESTAMP_TYPE_NAME = "TIMESTAMPZ"; //$NON-NLS-1$
    private static final String TEXT_TYPE_NAME = "TEXT"; //$NON-NLS-1$
    private static final String IMAGE_TYPE_NAME = "IMAGE"; //$NON-NLS-1$
    private static final String CHAR_VARYING_TYPE_NAME = "CHARACTER VARYING"; //$NON-NLS-1$
    private static final String VARCHAR_TYPE_NAME = "VARCHAR"; //$NON-NLS-1$
    private static final String SERIAL_TYPE_NAME = "SERIAL"; //$NON-NLS-1$
    private static final String SERIAL4_TYPE_NAME = "SERIAL4"; //$NON-NLS-1$
    private static final String SERIAL8_TYPE_NAME = "SERIAL8"; //$NON-NLS-1$
    private static final String BIGSERIAL_TYPE_NAME = "BIGSERIAL"; //$NON-NLS-1$
    private static final int TEXT_TYPE_MAX_LENGTH = 4000;
    /**
     * Construct an instance of PostgresModelProcessor.
     * 
     */
    public PostgresModelProcessor() {
        super();
    }

    /**
     * Construct an instance of PostgresModelProcessor.
     * @param factory
     */
    public PostgresModelProcessor(final RelationalFactory factory) {
        super(factory);
    }
    
    /**
     * Construct an instance of PostgresModelProcessor.
     * @param factory
     */
    public PostgresModelProcessor(final RelationalFactory factory, final RelationalTypeMapping mapping) {
        super(factory,mapping);
    }
    
    /**
     * Find the type given the supplied information.  This method is called by the
     * various <code>create*</code> methods, and is currently implemented to use
     * {@link #findType(int, int, List)} when a numeric type and {@link #findType(String, List)}
     * (by name) for other types.
     * @param type
     * @param typeName
     * @return
     */
    @Override
    protected EObject findType(final int jdbcType, final String typeName, 
                                final int length, final int precision, final int scale,
                                final List problems ) {
                                    
        EObject result = null;
        // Map the Postgres type of "BOOL" to our built-in type of Boolean
        if (BOOLEAN_TYPE_NAME.equalsIgnoreCase(typeName)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.BOOLEAN,problems);
        } else if (typeName.startsWith(TEXT_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.CLOB, problems);
        } else if (typeName.startsWith(IMAGE_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.BLOB, problems);
        }
        if ( result != null ) {
            return result;
        }
        
        return super.findType(jdbcType,typeName,length,precision,scale,problems);
    }
    
    /**
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#setColumnInfo(com.metamatrix.metamodels.relational.Column,
     *      com.metamatrix.modeler.jdbc.metadata.JdbcTable, com.metamatrix.modeler.jdbc.relational.impl.Context, java.util.List,
     *      java.lang.String, int, java.lang.String, int, int, int, int, java.lang.String, int)
     */
    @Override
    protected void setColumnInfo( final Column column,
                                  final JdbcTable tableNode,
                                  final Context context,
                                  final List problems,
                                  final String name,
                                  final int type,
                                  final String typeName,
                                  final int columnSize,
                                  final int numDecDigits,
                                  final int numPrecRadix,
                                  final int nullable,
                                  final String defaultValue,
                                  final int charOctetLen ) {
        super.setColumnInfo(column,
                            tableNode,
                            context,
                            problems,
                            name,
                            type,
                            typeName,
                            columnSize,
                            numDecDigits,
                            numPrecRadix,
                            nullable,
                            defaultValue,
                            charOctetLen);
        // If the type of the column is BLOB, then set the length to 0 since the value from the driver
        // does not represent the length of the BLOB
        if (CHAR_VARYING_TYPE_NAME.equalsIgnoreCase(typeName) ||
        	VARCHAR_TYPE_NAME.equalsIgnoreCase(typeName) ) {
        	if( columnSize > TEXT_TYPE_MAX_LENGTH ) {
        		column.setLength(TEXT_TYPE_MAX_LENGTH);
        	}
        }
        
        if( column.getPrecision() > 1000 ) {
        	column.setPrecision(1000);
        }
        
        if (defaultValue != null && 
        		(SERIAL_TYPE_NAME.equalsIgnoreCase(typeName) ||
        		 SERIAL4_TYPE_NAME.equalsIgnoreCase(typeName) ||
        		 SERIAL8_TYPE_NAME.equalsIgnoreCase(typeName) ||
        		 BIGSERIAL_TYPE_NAME.equalsIgnoreCase(typeName) ) ) {
        	column.setDefaultValue(null);
        }
        		
    }
    
    /*
		The following is a DDL statement that can be used to create a table containing columns of every datatype
		available in PostgreSQL
		
CREATE TABLE ALL_TYPES (
	type_bigint 	BIGINT,
	type_int8 		INT8,
	type_bigserial 	BIGSERIAL,
	type_serial8 	SERIAL8,
	type_bit		BIT,
	type_bit_varying	BIT VARYING,
	type_varbit		VARBIT,
	type_boolean	BOOLEAN,
	type_bool		BOOL,
	type_box		BOX,
	type_bytea		BYTEA,
	type_char_varying CHARACTER VARYING,
	type_char_varying_10 CHARACTER VARYING(10),
	type_varchar	VARCHAR,
	type_varchar_10	VARCHAR(10),
	type_character	CHARACTER,
	type_character_10	CHARACTER(10),
	type_char		CHAR,
	type_char_10	CHAR(10),
	type_cidr		CIDR,
	type_circle		CIRCLE,
	type_date		DATE,
	type_double_precision	DOUBLE PRECISION,
	type_float8		float8,
	type_inet		INET,
	type_integer	INTEGER,
	type_int		INT,
	type_int4		INT4,
	type_interval	INTERVAL,
	type_interval_5 INTERVAL(5),
	type_line		LINE,
	type_lseg		LSEG,
	type_macaddr	MACADDR,
	type_money		MONEY,
	type_numeric	NUMERIC,
	type_numeric_5 	NUMERIC(5),
	type_numeric_5_5 NUMERIC(5, 5),
	type_decimal	DECIMAL,
	type_decimal_5 	DECIMAL(5),
	type_decimal_5_5 DECIMAL(5, 5),
	type_path		PATH,
	type_point		POINT,
	type_polygon	POLYGON,
	type_real		REAL,
	type_float4		FLOAT4,
	type_smallint	SMALLINT,
	type_int2		INT2,
	type_serial		SERIAL,
	type_serial4	SERIAL4,
	type_text		TEXT,
	type_time		TIME,
	type_time_2		TIME(2),
	type_time_2_WOTZ TIME(2) WITHOUT TIME ZONE,
	type_time_2_WTZ TIME(2) WITH TIME ZONE,
	type_timetz		TIMETZ,
	type_timestamp	TIMESTAMP,
	type_timestamp_2	TIMESTAMP(2),
	type_timestamp_2_WOTZ TIMESTAMP(2) WITHOUT TIME ZONE,
	type_timestamp_2_WTZ TIMESTAMP(2) WITH TIME ZONE,
	type_timestamptz	TIMESTAMPTZ
);
     */
}
