/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Set;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.query.sql.ProcedureReservedWords;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class TestReservedWords {

    ITeiidServerVersion TEIID_VERSION_7 = new TeiidServerVersion("7.7.0");

    ITeiidServerVersion TEIID_VERSION_8 = new TeiidServerVersion("8.0.0");

    ITeiidServerVersion TEIID_VERSION_9 = new TeiidServerVersion("9.1.0");

    @Test
    public void testGetNonReservedWords() {
        Set<String> sevenWords = SQLConstants.getNonReservedWords(TEIID_VERSION_7);
        assertFalse(sevenWords.isEmpty());
        assertFalse(sevenWords.contains(SQLConstants.NonReserved.SELECTOR));
        assertFalse(sevenWords.contains(SQLConstants.NonReserved.SKIP));
        
        Set<String> eightWords = SQLConstants.getNonReservedWords(TEIID_VERSION_8);
        assertFalse(eightWords.isEmpty());
        assertTrue(eightWords.contains(SQLConstants.NonReserved.SELECTOR));
        assertTrue(eightWords.contains(SQLConstants.NonReserved.SKIP));

        // Retest to ensure that the cached teiid version is updated and the sets re-initialised
        sevenWords = SQLConstants.getNonReservedWords(TEIID_VERSION_7);
        assertFalse(sevenWords.isEmpty());
        assertFalse(sevenWords.contains(SQLConstants.NonReserved.SELECTOR));
        assertFalse(sevenWords.contains(SQLConstants.NonReserved.SKIP));

        Set<String> nineWords = SQLConstants.getNonReservedWords(TEIID_VERSION_9);
        assertEquals(eightWords, nineWords);
    }

    @Test
    public void testGetReservedWords() {
        Set<String> sevenWords = SQLConstants.getReservedWords(TEIID_VERSION_7);
        assertFalse(sevenWords.isEmpty());
        assertFalse(sevenWords.contains(SQLConstants.Reserved.OPTIONS));
        
        Set<String> eightWords = SQLConstants.getReservedWords(TEIID_VERSION_8);
        assertFalse(eightWords.isEmpty());
        assertTrue(eightWords.contains(SQLConstants.Reserved.OPTIONS));

        // Retest to ensure that the cached teiid version is updated and the sets re-initialised
        sevenWords = SQLConstants.getReservedWords(TEIID_VERSION_7);
        assertFalse(sevenWords.isEmpty());
        assertFalse(sevenWords.contains(SQLConstants.Reserved.OPTIONS));

        Set<String> nineWords = SQLConstants.getReservedWords(TEIID_VERSION_9);
        assertEquals(eightWords, nineWords);
    }

    @Test
    public void testIsSQLReservedWord() {
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_7, "limit"));
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_7, "LOOP"));
        assertFalse(SQLConstants.isReservedWord(TEIID_VERSION_7, "Options"));

        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_8, "limit"));
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_8, "LOOP"));
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_8, "Options"));

        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_9, "limit"));
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_9, "LOOP"));
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_9, "Options"));
    }

    @Test
    public void testIsProcedureReservedWord() {
        assertTrue(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_7, "input"));
        assertTrue(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_7, "inputs"));
        assertTrue(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_7, "rowcount"));

        assertFalse(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_8, "input"));
        assertFalse(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_8, "inputs"));
        assertTrue(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_8, "rowcount"));

        assertFalse(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_9, "input"));
        assertFalse(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_9, "inputs"));
        assertTrue(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_9, "rowcount"));
    }
}
