/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */
package com.metamatrix.query.internal.ui.sqleditor.sql;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Determines whether a given character is valid as part of an SQL keyword in 
 * the current context.
 */
public class SqlWordDetector implements IWordDetector {

	/**
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c) {
        return SqlSyntax.isSqlWordStart(c);
	}

	/**
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c) {
        return SqlSyntax.isSqlWordPart(c);
	}

}
