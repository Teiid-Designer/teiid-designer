/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.teiid.designer.vdb.AllowedLanguages;

public class TestAllowLanguages {


    @Test
    public void testAllowLanguagesConstructor_1() {
    	String languageString = "javascript, perl";
    	
    	AllowedLanguages lang = new AllowedLanguages(languageString);
    	String[] values = lang.getAllowedLanguageValues();
    	assertEquals(2,  values.length);
    	assertEquals("javascript", values[0]);
    	assertEquals("perl", values[1]);
    	assertEquals(languageString, lang.getOutputString());
    }
    
    @Test
    public void testAllowLanguagesConstructor_2() {
    	String[] languages = {"javascript", "perl", "fortran"};
    	String languageString = "javascript, perl, fortran";
    	
    	AllowedLanguages lang = new AllowedLanguages(languages);
    	String[] values = lang.getAllowedLanguageValues();
    	assertEquals(3,  values.length);
    	assertEquals("javascript", values[0]);
    	assertEquals("perl", values[1]);
    	assertEquals("fortran", values[2]);
    	assertEquals(languageString, lang.getOutputString());
    }
    
    @Test
    public void testAddAllowLanguages() {
    	String languageString = "javascript, perl";
    	String lang1 = "javascript";
    	String lang2 = "perl";
    	
    	AllowedLanguages lang = new AllowedLanguages();
    	lang.addAllowedLanguage(lang1);
    	lang.addAllowedLanguage(lang2);
    	
    	String[] values = lang.getAllowedLanguageValues();
    	assertEquals(2,  values.length);
    	assertEquals("javascript", values[0]);
    	assertEquals("perl", values[1]);
    	assertEquals(languageString, lang.getOutputString());
    }
}
