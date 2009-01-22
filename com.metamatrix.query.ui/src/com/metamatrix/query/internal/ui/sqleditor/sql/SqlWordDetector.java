/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
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
