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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
 
/**
 * Used to scan and detect for SQL keywords  
 */
public class WordPartDetector {
	String wordPart = ""; //$NON-NLS-1$
	int docOffset;
	
	/**
	 * Method WordPartDetector.
	 * @param viewer is a text viewer 
	 * @param documentOffset into the SQL document
	 */
	public WordPartDetector(ITextViewer viewer, int documentOffset) {
		docOffset = documentOffset - 1;		
		try {
			while (((docOffset) >= viewer.getTopIndexStartOffset())   && Character.isLetterOrDigit(viewer.getDocument().getChar(docOffset))) {
				docOffset--;
			}
			//we've been one step too far : increase the offset
			docOffset++;
			wordPart = viewer.getDocument().get(docOffset, documentOffset - docOffset);
		} catch (BadLocationException e) {
			// do nothing
		}
	}
	
	/**
	 * Method getString.
	 * @return String
	 */
	public String getString() {
		return wordPart;
	}
	
	public int getOffset() {
		return docOffset;
	}

}
