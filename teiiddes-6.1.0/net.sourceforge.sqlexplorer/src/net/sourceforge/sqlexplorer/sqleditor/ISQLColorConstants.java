package net.sourceforge.sqlexplorer.sqleditor;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * Color keys used for syntax highlighting Java code and JavaDoc compliant comments. A <code>IColorManager</code> is responsible
 * for mapping concrete colors to these keys.
 * <p>
 * This interface declares static final fields only; it is not intended to be implemented.
 * </p>
 * 
 * @see IColorManager
 */
public interface ISQLColorConstants {

    /** The prefix all color constants start with */
    //String PREFIX= "SQL_"; //$NON-NLS-1$
    /** The color key for multi-line comments in Java code. */
    String SQL_MULTILINE_COMMENT = "sql_multi_line_comment"; //$NON-NLS-1$
    /** The color key for single-line comments in Java code. */
    String SQL_SINGLE_LINE_COMMENT = "sql_single_line_comment"; //$NON-NLS-1$
    /** The color key for SQL keywords in Java code. */
    String SQL_KEYWORD = "sql_keyword"; //$NON-NLS-1$
    /** The color key for string and character literals in Java code. */
    String SQL_STRING = "sql_string"; //$NON-NLS-1$
    /** The color key for database tables names */
    String SQL_TABLE = "sql_table"; //$NON-NLS-1$
    /** The color key for database tables column names */
    String SQL_COLUMS = "sql_columns"; //$NON-NLS-1$

    /** The color key for everthing in SQL code for which no other color is specified. */
    String SQL_DEFAULT = "sql_default"; //$NON-NLS-1$

    /**
     * The color key for the SQL built-in types such as int and char in Java code.
     * 
     * @deprecated no longer used, use <code>SQL_KEYWORD</code> instead
     */
    @Deprecated
    String SQL_TYPE = "sql_type"; //$NON-NLS-1$

}
