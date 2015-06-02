/*************************************************************************************
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership. Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 ************************************************************************************/
package org.teiid.designer.transformation.ui.teiidddl;

/**
 * This class provides a mapping between the built-in types and the JDBC types.
 */
public interface RelationalTypeMapping {

    @SuppressWarnings("javadoc")
	public static class SQL_TYPE_NAMES {
        public static final String ARRAY = "ARRAY"; //$NON-NLS-1$ // NO_UCD
        public static final String BIGINT = "BIGINT"; //$NON-NLS-1$
        public static final String BINARY = "BINARY"; //$NON-NLS-1$
        public static final String BIT = "BIT"; //$NON-NLS-1$
        public static final String BLOB = "BLOB"; //$NON-NLS-1$
        public static final String CHAR = "CHAR"; //$NON-NLS-1$
        public static final String CLOB = "CLOB"; //$NON-NLS-1$
        public static final String DATE = "DATE"; //$NON-NLS-1$
        public static final String DECIMAL = "DECIMAL"; //$NON-NLS-1$
        public static final String DISTINCT = "DISTINCT"; //$NON-NLS-1$ // NO_UCD
        public static final String DOUBLE = "DOUBLE"; //$NON-NLS-1$
        public static final String FLOAT = "FLOAT"; //$NON-NLS-1$
        public static final String INTEGER = "INTEGER"; //$NON-NLS-1$
        public static final String JAVA_OBJECT = "JAVA_OBJECT"; //$NON-NLS-1$ // NO_UCD
        public static final String LONGVARBINARY = "LONGVARBINARY"; //$NON-NLS-1$
        public static final String LONGVARCHAR = "LONGVARCHAR"; //$NON-NLS-1$
        public static final String NCHAR = "NCHAR"; //$NON-NLS-1$
        public static final String NVARCHAR = "NVARCHAR"; //$NON-NLS-1$
        public static final String NTEXT = "NTEXT"; //$NON-NLS-1$
        public static final String NULL = "NULL"; //$NON-NLS-1$ // NO_UCD
        public static final String NUMERIC = "NUMERIC"; //$NON-NLS-1$
        public static final String OTHER = "OTHER"; //$NON-NLS-1$
        public static final String REAL = "REAL"; //$NON-NLS-1$
        public static final String REF = "REF"; //$NON-NLS-1$
        public static final String SMALLINT = "SMALLINT"; //$NON-NLS-1$
        public static final String SQLXML = "SQLXML"; //$NON-NLS-1$
        public static final String STRUCT = "STRUCT"; //$NON-NLS-1$
        public static final String TIME = "TIME"; //$NON-NLS-1$
        public static final String TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
        public static final String TINYINT = "TINYINT"; //$NON-NLS-1$
        public static final String VARBINARY = "VARBINARY"; //$NON-NLS-1$
        public static final String VARCHAR = "VARCHAR"; //$NON-NLS-1$
    }

}
