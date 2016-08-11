/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.language;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.ProcedureReservedWords;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class TestReservedWords {

    ITeiidServerVersion TEIID_VERSION_9 = Version.TEIID_9_0.get();

    @Test
    public void testGetNonReservedWords() {
        Set<String> nineWords = SQLConstants.getNonReservedWords(TEIID_VERSION_9);
        assertTrue(nineWords.contains(SQLConstants.NonReserved.SELECTOR));
        assertTrue(nineWords.contains(SQLConstants.NonReserved.SKIP));
        assertTrue(nineWords.contains(SQLConstants.NonReserved.AUTO_INCREMENT));
    }

    @Test
    public void testGetReservedWords() {
        Set<String> nineWords = SQLConstants.getReservedWords(TEIID_VERSION_9);
        assertTrue(nineWords.contains(SQLConstants.Reserved.OPTIONS));
    }

    @Test
    public void testIsSQLReservedWord() {
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_9, "limit"));
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_9, "LOOP"));
        assertTrue(SQLConstants.isReservedWord(TEIID_VERSION_9, "Options"));
    }

    @Test
    public void testIsProcedureReservedWord() {
        assertFalse(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_9, "input"));
        assertFalse(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_9, "inputs"));
        assertTrue(ProcedureReservedWords.isProcedureReservedWord(TEIID_VERSION_9, "rowcount"));
    }
}
