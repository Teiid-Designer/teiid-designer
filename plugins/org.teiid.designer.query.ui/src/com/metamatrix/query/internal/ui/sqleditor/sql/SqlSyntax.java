/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.teiid.core.types.DataTypeManager;
import org.teiid.designer.udf.UdfManager;
import org.teiid.language.SQLConstants;
import org.teiid.query.function.FunctionForm;
import org.teiid.query.function.FunctionLibrary;

import com.metamatrix.query.ui.UiPlugin;

/**
 * This class provides static Lists of SQL syntax words. RESERVED_WORDS contains reserved SQL words, FUNCTION_NAMES contains
 * system and user defined function names, DATATYPE_NAMES contains datatypes, and ALL_WORDS is a combination of all. All lists are
 * sorted alphabetically.
 */
public class SqlSyntax {
	public static final String DEBUG = "DEBUG"; //$NON-NLS-1$
	public static final String PLANONLY = "PLANONLY"; //$NON-NLS-1$
	public static final String SHOWPLAN = "SHOWPLAN"; //$NON-NLS-1$
	public static final String VIRTUAL_DEP_JOIN = "VIRTUALDEP";  //$NON-NLS-1$  

    public static final String[] ALL_RESERVED_WORDS = new String[] {
    	SQLConstants.Reserved.ALL,
    	SQLConstants.Tokens.ALL_COLS,
        SQLConstants.Reserved.AND,
        SQLConstants.Reserved.ANY,
        SQLConstants.Reserved.AS,
        SQLConstants.Reserved.ASC,
        SQLConstants.NonReserved.AVG,
        SQLConstants.Reserved.BEGIN,
        SQLConstants.Reserved.BETWEEN,
        SQLConstants.Reserved.BIGINTEGER,
        SQLConstants.Reserved.BIGDECIMAL,
        SQLConstants.Reserved.BLOB,
        SQLConstants.Reserved.BREAK,
        SQLConstants.Reserved.BY,
        SQLConstants.Reserved.BYTE,
        SQLConstants.Reserved.CASE,
        SQLConstants.Reserved.CAST,
        SQLConstants.Reserved.CHAR,
        SQLConstants.Reserved.CLOB,
        SQLConstants.Reserved.CONVERT,
        SQLConstants.Reserved.CONTINUE,
        SQLConstants.NonReserved.COUNT,
        SQLConstants.Reserved.CREATE,
        SQLConstants.Reserved.CRITERIA,
        SQLConstants.Reserved.CROSS,
        SQLConstants.Reserved.DATE,
        DEBUG,
        SQLConstants.Reserved.DECLARE,
        SQLConstants.Reserved.DELETE,
        SQLConstants.Reserved.DESC,
        SQLConstants.Reserved.DISTINCT,
        SQLConstants.Reserved.DOUBLE,
        SQLConstants.Reserved.DROP,
        SQLConstants.Reserved.ELSE,
        SQLConstants.Reserved.END,
        SQLConstants.Reserved.ERROR,        
        SQLConstants.Reserved.ESCAPE,
        SQLConstants.Reserved.EXCEPT,
        SQLConstants.Reserved.EXEC,
        SQLConstants.Reserved.EXECUTE,
        SQLConstants.Reserved.EXISTS,
        SQLConstants.Reserved.FALSE,
        SQLConstants.Reserved.FLOAT,
        SQLConstants.Reserved.FOR,
        SQLConstants.Reserved.FROM,
        SQLConstants.Reserved.FULL,
        SQLConstants.Reserved.GROUP,
        SQLConstants.Reserved.HAS,
        SQLConstants.Reserved.HAVING,
        SQLConstants.Reserved.IF,
        SQLConstants.Reserved.IN,
        SQLConstants.Reserved.INNER,
        SQLConstants.Reserved.INSERT,
        SQLConstants.Reserved.INTEGER,
        SQLConstants.Reserved.INTERSECT,
        SQLConstants.Reserved.INTO,
        SQLConstants.Reserved.IS,    
        SQLConstants.Reserved.JOIN,
        SQLConstants.Reserved.LEFT,
        SQLConstants.Reserved.LIKE,
        SQLConstants.Reserved.LIMIT,
        SQLConstants.Reserved.LOCAL,
        SQLConstants.Reserved.LONG,
        SQLConstants.Reserved.LOOP,
        SQLConstants.Reserved.MAKEDEP,
        SQLConstants.Reserved.MAKENOTDEP,
        SQLConstants.NonReserved.MIN,
        SQLConstants.NonReserved.MAX,
        SQLConstants.Reserved.NOCACHE,
        SQLConstants.Reserved.NOT,
        SQLConstants.Reserved.NULL,
        SQLConstants.Reserved.OBJECT,
        SQLConstants.Reserved.ON,
        SQLConstants.Reserved.OR,
        SQLConstants.Reserved.ORDER,
        SQLConstants.Reserved.OPTION,
        SQLConstants.Reserved.OUTER,
        PLANONLY,
        SQLConstants.Reserved.PROCEDURE,
        SQLConstants.Reserved.RIGHT,
        SQLConstants.Reserved.SELECT,
        SQLConstants.Reserved.SET,
        SQLConstants.Reserved.SHORT,
        SHOWPLAN,
        SQLConstants.Reserved.SOME,
        SQLConstants.NonReserved.SQL_TSI_FRAC_SECOND,
        SQLConstants.NonReserved.SQL_TSI_SECOND,
        SQLConstants.NonReserved.SQL_TSI_MINUTE,
        SQLConstants.NonReserved.SQL_TSI_HOUR,
        SQLConstants.NonReserved.SQL_TSI_DAY,
        SQLConstants.NonReserved.SQL_TSI_WEEK,
        SQLConstants.NonReserved.SQL_TSI_MONTH,
        SQLConstants.NonReserved.SQL_TSI_QUARTER,
        SQLConstants.NonReserved.SQL_TSI_YEAR,        
        SQLConstants.Reserved.STRING,
        SQLConstants.NonReserved.SUM,
        SQLConstants.Reserved.TABLE,
        SQLConstants.Reserved.TEMPORARY,
        SQLConstants.Reserved.THEN,
        SQLConstants.Reserved.TIME,
        SQLConstants.Reserved.TIMESTAMP,
        SQLConstants.NonReserved.TIMESTAMPADD,
        SQLConstants.NonReserved.TIMESTAMPDIFF,
        SQLConstants.Reserved.TRANSLATE,
        SQLConstants.Reserved.TRUE,
        SQLConstants.Reserved.UNION,
        SQLConstants.Reserved.UNKNOWN,
        SQLConstants.Reserved.UPDATE,
        SQLConstants.Reserved.USING,   
        SQLConstants.Reserved.VALUES,
        SQLConstants.Reserved.VIRTUAL,
        VIRTUAL_DEP_JOIN,
        SQLConstants.Reserved.WHEN,
        SQLConstants.Reserved. WITH,    
        SQLConstants.Reserved.WHERE,
        SQLConstants.Reserved.WHILE,
        SQLConstants.Reserved.XML,
    };
	

    // Word Lists
    public static final List<String> RESERVED_WORDS = new ArrayList<String>();
    public static final List<String> FUNCTION_NAMES = new ArrayList<String>();
    public static final List<String> DATATYPE_NAMES = new ArrayList<String>();
    public static final List<String> ALL_WORDS = new ArrayList<String>();

    // String with valid starting characters
    public static final List<String> RESERVED_WORD_START_CHARS = new ArrayList<String>();
    public static final List<String> FUNCTION_NAME_START_CHARS = new ArrayList<String>();
    public static final List<String> DATATYPE_NAME_START_CHARS = new ArrayList<String>();
    public static final List<String> ALL_WORD_START_CHARS = new ArrayList<String>();

    static {
    	
    	
        try {
			// RESERVED WORDS List
			for (int i = 0; i != ALL_RESERVED_WORDS.length; ++i) {
			    String reservedWord = ALL_RESERVED_WORDS[i];
			    RESERVED_WORDS.add(reservedWord);
			    String start = reservedWord.substring(0, 1);
			    if (!RESERVED_WORD_START_CHARS.contains(start)) {
			        RESERVED_WORD_START_CHARS.add(start);
			    }
			}
			Collections.sort(RESERVED_WORDS);
		} catch (Exception e) {
			UiPlugin.Util.log(e);
		}

        Set allFunctionNames = new HashSet();
		Iterator iter;
		try {
			// FUNCTION NAMES List
			FunctionLibrary functionLib = UdfManager.INSTANCE.getFunctionLibrary();
			List allCategories = functionLib.getFunctionCategories();
			iter = allCategories.iterator();
			while (iter.hasNext()) {
			    String catName = (String)iter.next();
			    List catFunctions = functionLib.getFunctionForms(catName);
			    Iterator funcIter = catFunctions.iterator();
			    while (funcIter.hasNext()) {
			        FunctionForm fForm = (FunctionForm)funcIter.next();
			        String fName = fForm.getName();
			        allFunctionNames.add(fName);
			        String start = fName.substring(0, 1);
			        if (!FUNCTION_NAME_START_CHARS.contains(start)) {
			            FUNCTION_NAME_START_CHARS.add(start);
			        }
			    }
			}
			FUNCTION_NAMES.addAll(allFunctionNames);
			Collections.sort(FUNCTION_NAMES);
		} catch (Exception e) {
			UiPlugin.Util.log(e);
		}

        // DATATYPE NAMES List
		Set<String> dataTypes = new HashSet<String>();
		try {
			dataTypes = DataTypeManager.getAllDataTypeNames();
			iter = dataTypes.iterator();
			while (iter.hasNext()) {
			    String dtypeName = (String)iter.next();
			    DATATYPE_NAMES.add(dtypeName);
			    String start = dtypeName.substring(0, 1);
			    if (!DATATYPE_NAME_START_CHARS.contains(start)) {
			        DATATYPE_NAME_START_CHARS.add(start);
			    }
			}
			Collections.sort(DATATYPE_NAMES);
		} catch (Exception e) {
			UiPlugin.Util.log(e);
		}

        // ALL WORDS List
        Set allWords = new HashSet();
        allWords.addAll(RESERVED_WORDS);
        allWords.addAll(allFunctionNames);
        allWords.addAll(dataTypes);
        ALL_WORDS.addAll(allWords);
        Collections.sort(ALL_WORDS);

        // All Starting Char String List
        Set allStartStrs = new HashSet();
        allStartStrs.addAll(RESERVED_WORD_START_CHARS);
        allStartStrs.addAll(FUNCTION_NAME_START_CHARS);
        allStartStrs.addAll(DATATYPE_NAME_START_CHARS);
        ALL_WORD_START_CHARS.addAll(allStartStrs);
    }

    /**
     * Determine if a character is a valid word start
     * 
     * @param c the character to test
     * @return 'true' if the character is a valid word start, 'false' if not.
     */
    public static boolean isSqlWordStart( char c ) {
        // Iterate thru the list of valid starting characters
        Iterator iter = ALL_WORD_START_CHARS.iterator();
        while (iter.hasNext()) {
            String charStr = (String)iter.next();
            if (charStr != null) {
                char start = charStr.charAt(0);
                if (c == start) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if a character is a valid word part
     * 
     * @param c the character to test
     * @return 'true' if the character is a valid word part, 'false' if not.
     */
    public static boolean isSqlWordPart( char c ) {
        // Valid word parts are letters, digits, underscores
        boolean isLetter = Character.isLetter(c);
        boolean isDigit = Character.isDigit(c);
        boolean isUnderscore = c == '_';
        if (isLetter || isDigit || isUnderscore) {
            return true;
        }
        return false;
    }

}
