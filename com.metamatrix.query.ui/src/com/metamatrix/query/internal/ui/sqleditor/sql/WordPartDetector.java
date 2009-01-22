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
