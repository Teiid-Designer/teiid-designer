/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;

/**
 * SqlFormattingStrategy
 *   This class will take a valid sql string and rerender it with indentation
 *   based on our indenting rules and corresponding user prefs.  It is not intended to work within the 
 *   jface Document/DocumentCommand scheme.  That scheme is intended to deal with
 *   incremental changes (one keystroke at a time), and is quite inefficient.  This class will apply the 
 *   formatting rules efficiently to a whole string. (jh)
 *   
 */

public class SqlFormattingStrategy
 
  implements SqlFormattingConstants,
             UiConstants {

    private String sInSql;
    private String sFormattedSql;
    
    private HashMap hmParseResult;
    private ArrayList arylKeywordInstances;
                                             
    
    /**
     * Construct an instance of SqlFormattingStrategy.
     * 
     */
    public SqlFormattingStrategy() {
        super();
        
    }

    private void init() {
        sInSql                  = null;
        sFormattedSql           = null;
    
        hmParseResult           = null;
        arylKeywordInstances    = null;        
    }

    private HashMap getParseMap() {
        
        if (hmParseResult == null ) {  
            hmParseResult = new HashMap();
        }
        return hmParseResult;   
    }
    
    private ArrayList getOffsetList( String sKeyword ) {
        if( getParseMap().get( sKeyword ) == null ) {
            getParseMap().put( sKeyword, new ArrayList() );
        }
        return (ArrayList)getParseMap().get( sKeyword );
    }

    private ArrayList getKeywordOffsetsArray() {
        
        if (arylKeywordInstances == null ) {  
            arylKeywordInstances = new ArrayList();
        }
        return arylKeywordInstances;   
    }
            
    public String format( String sInSql ) {
        init();
                     
        this.sInSql     = sInSql;
        sFormattedSql   = sInSql;
        
        if ( sInSql != null && sInSql.trim().length() > 0 ) {
                        
            if ( okToProcess( sInSql ) ) {                         
                sInSql = filter( sInSql );
                
                parse( sInSql );
                
                markupTheKeywordInstances();
        
                sFormattedSql = formatTheString( sInSql );
            }
        } else {            
//            System.out.println("[SqlFormattingStrategy.format] empty string; no action"); //$NON-NLS-1$             
        }
        
        return sFormattedSql;
    }

    /*
     * parse and filter the sql string in a single pass 
     */
     private String filter( String s ) {
    
                    
         StringBuffer sbTarget = new StringBuffer( s.length() );
         char chCurrentChar = SPACE;
//         boolean bOkToProcess = 
             okToProcess( s );
        
         // parse the sql string looking for the keywords; store the results in the map
         for ( int iPos =  0; iPos < sInSql.length(); iPos++ ) {
             chCurrentChar = s.charAt( iPos );
            
             //  2. Filter out certain whitespace characters (mostly the ones our process might re-add)
             if (  chCurrentChar == chNEWLINE 
                || chCurrentChar == chINDENT  ) {
                 // skip the char
             } else {
                 // pass it on
                 sbTarget.append( chCurrentChar );
             }                        
         }
         
         return sbTarget.toString();
     }

   /*
    * parse and filter the sql string in a single pass 
    */
    private String parse( String s ) {
    
        boolean bMatch = false;
        StringBuffer sbTarget = new StringBuffer( s.length() );
        int iUnclosedParenCount = 0;
        char chCurrentChar = ' ';
        
        // parse the sql string looking for the keywords; store the results in the map
        for ( int iPos =  0; iPos < s.length(); iPos++ ) {
            chCurrentChar = s.charAt( iPos );
            
            //  0. '(' and ')'    
            // Rule: no formatting for keywords that appear in subselects.
            // Implementation: Keep track of open parens found that have not yet been
            //                 matched by a close paren.  When this is non-zero, we are
            //                 in subselect territory.
            if ( chCurrentChar == PAREN_OPEN ) {
                iUnclosedParenCount++;
                continue;
            }

            if ( chCurrentChar == PAREN_CLOSE ) {
                iUnclosedParenCount--;                
                continue;
            }
            
            //  1. Capture keywords
            // only try to match keywords at breaks after words
            if ( iUnclosedParenCount == 0 && chCurrentChar == SPACE ) {
                for ( int ixKeyword = 0; ixKeyword < KEYWORDS.length; ixKeyword++ ) {
                    
                    int iWordStart  = iPos - KEYWORDS[ixKeyword].length();
                    String sKeyword = KEYWORDS[ixKeyword];
                    
                    bMatch = s.regionMatches( true, 
                                              iWordStart,
                                              sKeyword, 
                                              0, 
                                              sKeyword.length() );
                                                                
                    if( bMatch ) {
                        // store it
                        ArrayList arylOffsets = getOffsetList( sKeyword );
                        arylOffsets.add( new Integer( iWordStart ) );
                        
                        KeywordInstance kwi = new KeywordInstance( sKeyword, iWordStart );
//                        System.out.println("[SqlFormattingStrategy.parseAndFilter] About to add kwi: " + kwi.sKeyword ); //$NON-NLS-1$             
                        
                        getKeywordOffsetsArray().add( kwi );
                        
                        // quit this loop when you find and process a match 
                        break;                                               
                    }
                }                
            }
                        
        }
        
        return sbTarget.toString();        
    }
    
    private boolean okToProcess( String s ) {
        boolean bOk = true;     // default to true
        
        if ( s.indexOf( CREATE_PROCEDURE ) > -1 ) {
            bOk = false;
        }
  
        return bOk;
    }
    
    private void markupTheKeywordInstances() {
        /*
         * Process the parse map.  The general idea is to work out a strategy,
         *  then apply it backwards to the input string (in a StringBuffer). This
         *  way the meaning of the offsets in the parse map is not damaged as mods
         *  are applied to the result string.  however...to truly apply them backwards
         *  we must sort them by offset value!  And the simplest way to do that is
         *  to have a second store that is just a pure array we can read backwards...
         *  Hence the arylKeywordOffsets array.
         */
                 
        Iterator it = getKeywordOffsetsArray().iterator();
        
        while( it.hasNext() ) {
            KeywordInstance kwiTemp = (KeywordInstance)it.next();
//            System.out.println("[SqlFormattingStrategy.markupTheKewordInstances] About to setApplyFlag on: " + kwiTemp ); //$NON-NLS-1$

            // special case: FROM in a DELETE clause should NOT be set applicable
            if( kwiTemp.sKeyword.equals( FROM ) && getParseMap().get( SELECT ) == null ) {
                // skip this FROM, since it goes with the DELETE                              
            } else {            
                kwiTemp.setApplyFlag( true );
            }                                                           
        }                             
    }
    
    private String formatTheString( String sSql ) {

        String sResultString = null;
        StringBuffer sb = new StringBuffer( sSql );
         
         
        // apply the mods required by each 'applicable' keywordinstance
        //  do this backwards so the the meaning of the remaining
        //  offsets is not mangled.            
        ArrayList arylKwis = getKeywordOffsetsArray();      
        int iSize = arylKwis.size();
        
        for( int i = iSize - 1; i >= 0; i-- ) { 
            KeywordInstance kwi = (KeywordInstance)arylKwis.get( i );
            
            if( kwi.isApplicable() ) {
                formatKeyword( sb, kwi );
            }            
        }
         
        // convert the string
        sResultString = sb.toString();                                     
//        System.out.println("[SqlFormattingStrategy.formatTheString] Final string: " + sResultString ); //$NON-NLS-1$             
        return sResultString;
    }
    
    private void formatKeyword( StringBuffer sb, KeywordInstance kwi ) {
        
        // 0. construct replacement string
        String sNewString = EMPTY_STRING;  
 
        // 1. put the keyword on a new line, depending on preference
        if  ( kwi.iOffset > 0 && startClausesOnNewLine() ) {
            sNewString = NEWLINE;
        }
        
        // 2. add in the keyword 
        sNewString += kwi.sKeyword
                   +  NEWLINE;
                   
        // 3. indent the content, depending on preference
        //    jh note: the indent makes no sense unless the newline is also requested,
        //             so I am linking them.                     
        if ( startClausesOnNewLine() && indentClauseContent() ) {                   
            sNewString += INDENT;
        }
                 
        // mod the buffer        
//        System.out.println("[SqlFormattingStrategy.formatKeyword] About to replace, kwi.iOffset / kwi.sKeyword.length() / sNewString / sb.length() "  //$NON-NLS-1$ 
//                        + kwi.iOffset 
//                        + "/"                       //$NON-NLS-1$
//                        + kwi.sKeyword.length() 
//                        + "/"                       //$NON-NLS-1$
//                        +  sNewString 
//                        + "/"                       //$NON-NLS-1$
//                        +  sb.length()  ); 
//                                     
//        System.out.println("[SqlFormattingStrategy.formatKeyword] About to replace: " + sNewString ); //$NON-NLS-1$
             
        sb.replace( kwi.iOffset, 
                    kwi.iOffset + kwi.sKeyword.length(), 
                    sNewString );
        
    }
    
    private boolean startClausesOnNewLine() {
        return UiPlugin.getDefault().getPreferenceStore()
                    .getBoolean( UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE );
    }   
    
    private boolean indentClauseContent() {
        return UiPlugin.getDefault().getPreferenceStore()
                    .getBoolean( UiConstants.Prefs.INDENT_CLAUSE_CONTENT );
    }   
    
    // =================================
    //  inner class: KeywordInstance
    // =================================
    
    
    class KeywordInstance {
        
        String sKeyword;
        int iOffset;
        boolean bApply;
        
        KeywordInstance( String sKeyword, int iOffset ) {
            this.sKeyword = sKeyword;
            this.iOffset  = iOffset;
        }
        
        public String getKeyword() {
            return sKeyword;
        }
        
        public int getOffset() {
            return iOffset;
        }
        
        public void setApplyFlag( boolean b ) {
            bApply = b;
        }
        
        public boolean isApplicable() {
            return bApply;
        }
                        
    }
    
    
}
