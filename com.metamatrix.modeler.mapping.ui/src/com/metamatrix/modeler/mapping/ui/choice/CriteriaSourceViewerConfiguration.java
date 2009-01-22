/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.choice;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 *
 * (C) Copyright International Business Machines Corporation, 2003.
 * All Rights Reserved.
 *
 * Code or samples provided herein are provided without warranty of any kind.
 */
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;
import com.metamatrix.query.internal.ui.sqleditor.sql.ColorManager;
import com.metamatrix.query.internal.ui.sqleditor.sql.SqlAutoIndentStrategy;
import com.metamatrix.query.internal.ui.sqleditor.sql.SqlCompletionProcessor;
import com.metamatrix.query.internal.ui.sqleditor.sql.SqlPartitionScanner;
import com.metamatrix.query.internal.ui.sqleditor.sql.SqlWordStrategy;

/**
 * This class defines the editor add-ons; content assist, content formatter,
 *  highlighting, auto-indent strategy, double click strategy.
 *
 */
public class CriteriaSourceViewerConfiguration
	extends SourceViewerConfiguration {

    private ColorManager colorManager;
    private List lstReservedWords;
    private CriteriaCodeScanner scanner;



    public CriteriaSourceViewerConfiguration( ColorManager colorManager, List lstReservedWords ) {
        this.colorManager = colorManager;
        this.lstReservedWords = lstReservedWords;
    }

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(ISourceViewer)
	 */
	@Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();

		assistant.setContentAssistProcessor(
			new SqlCompletionProcessor(),
			IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(
			new SqlCompletionProcessor(),
			SqlPartitionScanner.SQL_CODE);

		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant.setContextInformationPopupOrientation(
            IContentAssistant.CONTEXT_INFO_BELOW);
		//Set to Carolina blue
		assistant.setContextInformationPopupBackground(colorManager.getColor(new RGB(0, 191, 255)));

		return assistant;
	}

	/**
	 * Configure the double click strategy here.
	 *
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(ISourceViewer, String)
	 */

	@Override
    public ITextDoubleClickStrategy getDoubleClickStrategy( ISourceViewer sourceViewer,
		                                                    String contentType) {
//		return new SqlDoubleClickStrategy();
        return null;
	}

    public CriteriaCodeScanner getCodeScanner() {
        return scanner;
    }

    public void setReservedWords( List lstReservedWords ) {
        getCodeScanner().setReservedWords( lstReservedWords );
    }

	/**
	 * Configure a presentation reconciler for syntax highlighting
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
	 */
	@Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {


		PresentationReconciler reconciler= new PresentationReconciler();
        scanner = new CriteriaCodeScanner( colorManager, lstReservedWords);

		// rule for default text
		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		// rule for multiline comments
		// We jsut need a scanner that does nothing but returns a token with the corrresponding text attributes
		RuleBasedScanner multiLineScanner = new RuleBasedScanner();
		multiLineScanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(ColorManager.MULTI_LINE_COMMENT))));
		dr= new DefaultDamagerRepairer(multiLineScanner);
		reconciler.setDamager(dr, SqlPartitionScanner.SQL_MULTILINE_COMMENT);
		reconciler.setRepairer(dr, SqlPartitionScanner.SQL_MULTILINE_COMMENT);

		// rule for SQL comments for documentation
		dr= new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, SqlPartitionScanner.SQL_CODE);
		reconciler.setRepairer(dr, SqlPartitionScanner.SQL_CODE);

		return reconciler;
	}

	/**
	 * Configure the content formatter with two formatting strategies
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentFormatter(ISourceViewer)
	 */
	@Override
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
    	ContentFormatter formatter  = new ContentFormatter();
    	IFormattingStrategy keyword = new SqlWordStrategy();
    	formatter.setFormattingStrategy( keyword, IDocument.DEFAULT_CONTENT_TYPE );

    	return formatter;
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies( ISourceViewer sourceViewer,
	                                                  String contentType ) {
	    return new IAutoEditStrategy[] {new SqlAutoIndentStrategy()};
	}

    /* (non-Javadoc)
     * Method declared on SourceViewerConfiguration
     */
    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return null;
//        return new SqlTextHover();
    }

    // =======================================
    //  inner class: ChoiceWordStrategy
    // =======================================

    /**
     * The formatting strategy that transforms SQL keywords to upper case
     */
    class ChoiceWordStrategy implements IFormattingStrategy {

        List lstKeywords;

        public void setKeywords( List lstKeywords ) {
            this.lstKeywords = lstKeywords;
        }

        public List getKeywords() {
            return lstKeywords;
        }

        public void removeAllKeywords() {
            lstKeywords.removeAll( lstKeywords );
        }

        // ======================================
        //  interface methods: IFormattingStrategy
        // ======================================

        /**
         * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStarts(String)
         */
        public void formatterStarts(String initialIndentation) {

        }

        /**
         * @see org.eclipse.jface.text.formatter.IFormattingStrategy#format(String, boolean, String, int[])
         */
        public String format( String content,
                              boolean isLineStart,
                              String indentation,
                              int[] positions) {

            return keyWordsToColor( content );
        }

        /**
         * Method keyWordsToColor
         * @param content
         * @return String
         */
        private String keyWordsToColor(String content) {

            List lstKeywords = getKeywords();
            StringTokenizer st = new StringTokenizer(content, " \n", true); //$NON-NLS-1$
            String token = ""; //$NON-NLS-1$
            String newContent = ""; //$NON-NLS-1$
            boolean isDone = false;
            while ( st.hasMoreTokens() ) {
                token = st.nextToken();

                for ( int j = 0; j < lstKeywords.size(); j++ ) {
                    if ( token.equals(" ") | token.equals("\n")) //$NON-NLS-1$ //$NON-NLS-2$
                        break;
                    if ( token.toUpperCase().equals( lstKeywords.get( j ) ) ) {

                        // ok, so how do we color it?
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


}
