/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.udf.IFunctionForm;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.udf.UdfManager;
import org.teiid.query.ui.UiConstants;

/**
 * This class provides static Lists of SQL syntax words. RESERVED_WORDS contains reserved SQL words, FUNCTION_NAMES contains
 * system and user defined function names, DATATYPE_NAMES contains datatypes, and ALL_WORDS is a combination of all. All lists are
 * sorted alphabetically.
 *
 * @since 8.0
 */
public class SqlSyntax {
    
    IExecutionConfigurationListener configurationListener = new IExecutionConfigurationListener() {
        
        @Override
        public void configurationChanged(ExecutionConfigurationEvent event) {
            // TODO Auto-generated method stub
            
        }
    };
    
    // Word Lists
    private List<String> RESERVED_WORDS = new ArrayList<String>();
    private List<String> FUNCTION_NAMES = new ArrayList<String>();
    private List<String> DATATYPE_NAMES = new ArrayList<String>();
    private List<String> ALL_WORDS = new ArrayList<String>();

    // String with valid starting characters
    private List<String> RESERVED_WORD_START_CHARS = new ArrayList<String>();
    private List<String> FUNCTION_NAME_START_CHARS = new ArrayList<String>();
    private List<String> DATATYPE_NAME_START_CHARS = new ArrayList<String>();
    private List<String> ALL_WORD_START_CHARS = new ArrayList<String>();

    /**
     * Create a new instance
     */
    public SqlSyntax() {
        init();
    }
    
    private void init() {
        IQueryService sqlSyntaxService = ModelerCore.getTeiidQueryService();
        IDataTypeManagerService dataTypeManagerService = ModelerCore.getTeiidDataTypeManagerService();
        
        try {
			// RESERVED WORDS List
			for (String reservedWord : sqlSyntaxService.getReservedWords()) {
			    RESERVED_WORDS.add(reservedWord);
			    String start = reservedWord.substring(0, 1);
			    if (!RESERVED_WORD_START_CHARS.contains(start)) {
			        RESERVED_WORD_START_CHARS.add(start);
			    }
			}
			for (String reservedWord : sqlSyntaxService.getNonReservedWords()) {
                RESERVED_WORDS.add(reservedWord);
                String start = reservedWord.substring(0, 1);
                if (!RESERVED_WORD_START_CHARS.contains(start)) {
                    RESERVED_WORD_START_CHARS.add(start);
                }
            }
			Collections.sort(RESERVED_WORDS);
		} catch (Exception e) {
			UiConstants.Util.log(e);
		}

        Set allFunctionNames = new HashSet();
		Iterator iter;
		try {
			// FUNCTION NAMES List
			IFunctionLibrary functionLib = UdfManager.getInstance().getSystemFunctionLibrary();
			List<String> allCategories = functionLib.getFunctionCategories();
			for (String category : allCategories) {
			    for (IFunctionForm fForm : functionLib.getFunctionForms(category)) {
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
			UiConstants.Util.log(e);
		}

        // DATATYPE NAMES List
		Set<String> dataTypes = new HashSet<String>();
		try {
			dataTypes = dataTypeManagerService.getAllDataTypeNames();
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
			UiConstants.Util.log(e);
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
    public boolean isSqlWordStart( char c ) {
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
    public boolean isSqlWordPart( char c ) {
        // Valid word parts are letters, digits, underscores
        boolean isLetter = Character.isLetter(c);
        boolean isDigit = Character.isDigit(c);
        boolean isUnderscore = c == '_';
        if (isLetter || isDigit || isUnderscore) {
            return true;
        }
        return false;
    }

    /**
     * Get the reserved words
     * 
     * @return list of reserved words
     */
    public List<String> getReservedWords() {
        return Collections.unmodifiableList(RESERVED_WORDS);
    }
    
    /**
     * Get the data type names
     * 
     * @return list of data type names
     */
    public List<String> getDataTypeNames() {
        return Collections.unmodifiableList(DATATYPE_NAMES);
    }

    /**
     * Get the function names
     * 
     * @return list of function names
     */
    public List<String> getFunctionNames() {
        return Collections.unmodifiableList(FUNCTION_NAMES);
    }

    /**
     * Get all the words
     * 
     * @return list of all the words
     */
    public List<String> getAllWords() {
        return Collections.unmodifiableList(ALL_WORDS);
    }
}
