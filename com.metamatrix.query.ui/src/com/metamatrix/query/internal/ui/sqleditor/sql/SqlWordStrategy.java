/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.sql;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */
import java.util.StringTokenizer;

import org.eclipse.jface.text.formatter.IFormattingStrategy;

//import com.metamatrix.ui.internal.widget.sqleditor.sql.WordPartDetector;

/**
 * The formatting strategy that transforms SQL keywords to upper case
 */
public class SqlWordStrategy implements IFormattingStrategy {

	/**
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStarts(String)
	 */
	public void formatterStarts(String initialIndentation) {
	}

	/**
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategy#format(String, boolean, String, int[])
	 */
	public String format(
		String content,
		boolean isLineStart,
		String indentation,
		int[] positions) {

		return keyWordsToUpper(content);
	}

	/**
	 * Method keyWordsToUpper.
	 * @param content
	 * @return String
	 */
	private String keyWordsToUpper(String content) {
		StringTokenizer st = new StringTokenizer(content, " \n", true); //$NON-NLS-1$
		String token = ""; //$NON-NLS-1$ 
		String newContent = ""; //$NON-NLS-1$
		boolean isDone = false;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			for (int j = 0; j < SqlSyntax.ALL_WORDS.size(); j++) {
				if (token.equals(" ") | token.equals("\n")) //$NON-NLS-1$ //$NON-NLS-2$
					break;
				if (token.toUpperCase().equals(SqlSyntax.ALL_WORDS.get(j))) {
					token = token.toUpperCase();
					isDone = true;
					break;
				}
			}
			if (isDone == true)
				break;
			newContent = newContent + token;
		}
		return newContent;
		
	} 

    /**
     * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStops()
     */
	public void formatterStops() {
	}

}
