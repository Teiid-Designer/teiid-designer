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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

/**
 * Process double clicks in the SQL content.
 */
public class SqlDoubleClickStrategy implements ITextDoubleClickStrategy {
	protected ITextViewer fText;
	protected int fPos;
	protected int fStartPos;
	protected int fEndPos;

	/**
	 * Constructor for SQLDoubleClickStrategy.
	 */
	public SqlDoubleClickStrategy() {
		super();
	}

	/**
	 * A simple method that selects the word when double clicked.
	 * @see org.eclipse.jface.text.ITextDoubleClickStrategy#doubleClicked(ITextViewer)
	 */
	public void doubleClicked(ITextViewer viewer) {
		fPos = viewer.getSelectedRange().x;

		if (fPos < 0)
			return;

		fText = viewer;
		selectWord();
				

	}
	/**
	 * Select the word at the current selection. 
	 */
	 protected void selectWord() {
		if (matchWord()) {

			if (fStartPos == fEndPos)
				fText.setSelectedRange(fStartPos, 0);
			else
				fText.setSelectedRange(fStartPos + 1, fEndPos - fStartPos - 1);
		}
	}
	/**
	 * Select the word at the current selection. Return true if successful,
	 * otherwise false.
	 */
	protected boolean matchWord() {

		IDocument doc = fText.getDocument();

		try {

			int pos = fPos;
			char c;

			while (pos >= 0) {
				c = doc.getChar(pos);
				// Yes we know this isn't Java code we are parsing but the 
				// but the Java identifier rule works for this example 
				if (!Character.isJavaIdentifierPart(c))
					break;
				--pos;
			}

			fStartPos = pos;

			pos = fPos;
			int length = doc.getLength();

			while (pos < length) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				++pos;
			}

			fEndPos = pos;

			return true;

		} catch (BadLocationException x) {
		}

		return false;
	}
}
