/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */ 
package com.metamatrix.query.internal.ui.sqleditor.sql;

/**
 * SQL Syntax words (upper and lower cases).
 */
public interface ISqlSyntax {
	public static final String[] reservedwords =
		{
            "SELECT",   //$NON-NLS-1$
            "FROM",     //$NON-NLS-1$
            "select",   //$NON-NLS-1$
			"from" };   //$NON-NLS-1$

	public static final String[] predicates =
		{
			"LIKE",      //$NON-NLS-1$
			"< >",       //$NON-NLS-1$
			"AND",       //$NON-NLS-1$
            "OR",       //$NON-NLS-1$
            "like",     //$NON-NLS-1$
            "and",      //$NON-NLS-1$
            "or" };     //$NON-NLS-1$
	public static final String[] types =
		{
			"INTEGER",   //$NON-NLS-1$
			"DECIMAL",   //$NON-NLS-1$
			"integer",   //$NON-NLS-1$
			"decimal" }; //$NON-NLS-1$
	public static final String[] constants =
		{ 
            "FALSE",    //$NON-NLS-1$
            "NULL",     //$NON-NLS-1$
            "TRUE",     //$NON-NLS-1$
            "false",    //$NON-NLS-1$
            "true",     //$NON-NLS-1$
            "null" };   //$NON-NLS-1$
	public static final String[] functions =
		{
			"ABS",       //$NON-NLS-1$
			"COUNT",     //$NON-NLS-1$
			"DATE",      //$NON-NLS-1$
			"abs",       //$NON-NLS-1$
			"count",     //$NON-NLS-1$
			"date" };    //$NON-NLS-1$
	Object[] allWords =
		{ reservedwords, predicates, types, constants, functions };

}
