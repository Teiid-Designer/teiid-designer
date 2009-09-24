/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */ 
package com.metamatrix.modeler.mapping.ui.choice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import com.metamatrix.query.internal.ui.sqleditor.sql.ColorManager;
import com.metamatrix.query.internal.ui.sqleditor.sql.SqlWhiteSpaceDetector;
import com.metamatrix.query.internal.ui.sqleditor.sql.SqlWordDetector;

/**
 * The SQLCodeScanner is a RuleBaseScanner.This class finds SQL comments and
 * keywords, as the user edits the document. It is "programmed" with a sequence
 * of rules that evaluates and returns the offset and the length of the last
 * found token.
 */
public class CriteriaCodeScanner extends RuleBasedScanner {



    private List rules;
    private ColorManager colorManager;
    private List lstReservedWords;
    
    
    // tokens
    IToken keyword;
    IToken datatype;
    IToken function;
    IToken string;
    IToken comment;
    IToken other;


	/**
	 * Constructor for SQLCodeScanner.
	 * The SQLCodeScanner, is a RuleBaseScanner. The code scanner creates tokens 
	 * for keywords, types, and constants. The token is constructed with a 
	 * TextAttribute. The TextAttribute is constructed with a color and font. 
	 * A list of rules with the corresponding token are created. The method ends
	 * with setting the scanner�s set of rules
	 */
	public CriteriaCodeScanner(ColorManager colorManager, List lstReservedWords ) {
        
        this.colorManager = colorManager;            
        this.lstReservedWords = lstReservedWords;            

        createTokens();
        setDefaultReturnToken( other );                    
                    
        init();
	}
    
    private void init() {
        
        rules = new ArrayList();
        
        createStandardRules();       
        createReservedWordRules();
        
        applyRules();
    }
    
    private void createStandardRules() {

        // Add rule for single line comments.
        rules.add(new EndOfLineRule("//", comment)); //$NON-NLS-1$

        // Add rule for strings and character constants.
        rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-1$ //$NON-NLS-2$ 
        rules.add(new SingleLineRule("'", "'", string, '\\')); //$NON-NLS-1$ //$NON-NLS-2$ 
 
        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new SqlWhiteSpaceDetector()));

        // Add word rule for keywords, datatypes, and function names.
        WordRule wordRule = new WordRule( new SqlWordDetector(), other );
        
//        for (int i = 0; i < SqlSyntax.DATATYPE_NAMES.size(); i++)
//            wordRule.addWord((String)SqlSyntax.DATATYPE_NAMES.get(i), datatype);
//        for (int i = 0; i < SqlSyntax.FUNCTION_NAMES.size(); i++)
//            wordRule.addWord((String)SqlSyntax.FUNCTION_NAMES.get(i), function);

        rules.add(wordRule);
    }
    
    private void createReservedWordRules() {
        WordRule wordRule = new WordRule( new SqlWordDetector(), other );

        Iterator it = lstReservedWords.iterator();
        
        while( it.hasNext() ) {
            String sReservedWord = (String)it.next();
            wordRule.addWord( sReservedWord, keyword );
        }
        
        rules.add(wordRule);
    }
    
    private void applyRules() {
        
        IRule[] result = new IRule[rules.size()];
        rules.toArray( result );
        setRules( result );        
    }

    
    public void setReservedWords( List lstReservedWords ) {
        this.lstReservedWords = lstReservedWords;
        init();        
    }
    
    
    
    public void createTokens() {
        
        //--------------------------------------
        // Keyword TextAttributes       IToken keyword =

        //--------------------------------------
        keyword =
            new Token(
                new TextAttribute(
                    colorManager.getColor(ColorManager.KEYWORD),
                    null,
                    SWT.BOLD));
        //--------------------------------------
        // Datatype TextAttributes        IToken datatype =

        //--------------------------------------
        datatype =
            new Token(
                new TextAttribute(
                    colorManager.getColor(ColorManager.DATATYPE),
                    null,
                    SWT.BOLD));
        //--------------------------------------
        // Function TextAttributes
        //--------------------------------------
        function =
            new Token(
                new TextAttribute(
                    colorManager.getColor(ColorManager.FUNCTION),
                    null,
                    SWT.BOLD));
        //--------------------------------------
        // String TextAttributes
        //--------------------------------------
        string =
            new Token(
                new TextAttribute(
                    colorManager.getColor(ColorManager.STRING),
                    null,
                    SWT.NORMAL));
        //--------------------------------------
        // Comment TextAttributes
        //--------------------------------------
        comment =
            new Token(
                new TextAttribute(
                    colorManager.getColor(ColorManager.SINGLE_LINE_COMMENT),
                    null,
                    SWT.NORMAL));
        //--------------------------------------
        // Default TextAttributes
        //--------------------------------------
        other =
            new Token(
                new TextAttribute(colorManager.getColor(ColorManager.DEFAULT),
                    null,
                    SWT.NORMAL));

    }
    
    

}
