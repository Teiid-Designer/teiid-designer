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

import java.util.Iterator;
import java.util.Vector;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * The SQL content assis processor. This content assist processor proposes 
 * text completions and computes context information for a SQL content type.
 */
public class SqlCompletionProcessor
	implements IContentAssistProcessor {

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(ITextViewer, int)
	 */
	protected Vector proposalList = new Vector();
	protected IContextInformationValidator fValidator = new Validator();
/**
 * This method returns a list of completion proposals as ICompletionProposal 
 * objects. The proposals are based on the word at the offset in the document 
 * where the cursor is positioned. In this implementation, we find the word at 
 * the document offset and compare it to our list of SQL reserved words. 
 * The list is a subset, of those words that match what the user has entered. 
 * For example, the text or proposes the SQL keywords OR and ORDER. The list is 
 * returned as an array of completion proposals. 
 * 
 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(ITextViewer, int)
 */
 

	public ICompletionProposal[] computeCompletionProposals(
		ITextViewer viewer,
		int documentOffset) {
            
		WordPartDetector wordPart =
			new WordPartDetector(viewer, documentOffset);

		// iterate over all the different categories
		for (int i = 0; i < SqlSyntax.ALL_WORDS.size(); i++) {
			if ( ((String)SqlSyntax.ALL_WORDS.get(i)).startsWith(wordPart.getString().toUpperCase()))
			     proposalList.add(SqlSyntax.ALL_WORDS.get(i));
		}

		return turnProposalVectorIntoAdaptedArray(wordPart);

	}
	/*
	 * Turns the vector into an Array of ICompletionProposal objects
	 */
	protected ICompletionProposal[] turnProposalVectorIntoAdaptedArray(WordPartDetector word) {
		ICompletionProposal[] result =
			new ICompletionProposal[proposalList.size()];

		int index = 0;

		for (Iterator i = proposalList.iterator(); i.hasNext();) {
			String keyWord = (String) i.next();

			IContextInformation info =
				new ContextInformation(keyWord, getContentInfoString(keyWord));
			//Creates a new completion proposal. 
				result[index] =
					new CompletionProposal(keyWord, //replacementString
		word.getOffset(),
			//replacementOffset the offset of the text to be replaced
		word.getString().length(),
			//replacementLength the length of the text to be replaced
		keyWord.length(),
			//cursorPosition the position of the cursor following the insert relative to replacementOffset
		null, //image to display
		keyWord, //displayString the string to be displayed for the proposal
		info,
			//contntentInformation the context information associated with this proposal
	getContentInfoString(keyWord));
			index++;
		}
		// System.out.println("result : " + result.length);
		proposalList.removeAllElements();
		return result;
	}

	/**
	 * Method getContentInfoString.
	 * @param keyWord
	 */
	private String getContentInfoString(String keyWord) {
//		String resourceString;
//		String resourceKey = "ContextString." + keyWord;
//		resourceString =
//			SQLEditorPlugin.getDefault().getResourceString(resourceKey);
//		if (resourceString == keyWord) {
//			resourceString = "No Context Info String";
//		}
//		return resourceString;
        //return "Context Info String for word"; //$NON-NLS-1$
        return ""; //$NON-NLS-1$
	}

	/**
	 * This method is incomplete in that it does not implement logic to produce 
	 * some context help relevant to SQL.  It jsut hard codes two strings to
	 * demonstrate the action  
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(
		ITextViewer viewer,
		int documentOffset) {
//		WordPartDetector wordPart =
//			new WordPartDetector(viewer, documentOffset);

		IContextInformation[] result = new IContextInformation[2];
		result[0] =
			new ContextInformation(
				"contextDisplayString", //$NON-NLS-1$
				"informationDisplayString"); //$NON-NLS-1$
		result[1] =
			new ContextInformation(
				"contextDisplayString2", //$NON-NLS-1$
				"informationDisplayString2"); //$NON-NLS-1$

		return result;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return fValidator;
	}
	/**
	 * Simple content assist tip closer. The tip is valid in a range
	 * of 5 characters around its popup location.
	 */
	protected static class Validator
		implements IContextInformationValidator, IContextInformationPresenter {

		protected int fInstallOffset;

		/*
		 * @see IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int offset) {
			return Math.abs(fInstallOffset - offset) < 5;
		}

		/*
		 * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
		 */
		public void install(
			IContextInformation info,
			ITextViewer viewer,
			int offset) {
			fInstallOffset = offset;
		}

		/*
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int, TextPresentation)
		 */
		public boolean updatePresentation(
			int documentPosition,
			TextPresentation presentation) {
			return false;
		}
	}
}
