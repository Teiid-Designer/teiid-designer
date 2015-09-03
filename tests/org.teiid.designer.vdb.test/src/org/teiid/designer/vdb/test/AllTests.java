package org.teiid.designer.vdb.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.komodo.vdb.TestAllowLanguages;
import org.teiid.designer.komodo.vdb.TestDynamicVdbExport;
import org.teiid.designer.komodo.vdb.TestDynamicVdbImport;
import org.teiid.designer.komodo.vdb.TestModelToDdlGenerator;
import org.teiid.designer.vdb.VdbFileEntryTest;
import org.teiid.designer.vdb.VdbModelEntryTest;
import org.teiid.designer.vdb.VdbTest;
import org.teiid.designer.vdb.VdbUtilTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {
    VdbTest.class,
    VdbModelEntryTest.class,
    VdbFileEntryTest.class,
    TestAllowLanguages.class,
    TestDynamicVdbExport.class,
    TestDynamicVdbImport.class,
    VdbUtilTest.class,
    TestModelToDdlGenerator.class
    } )
public class AllTests {
    // nothing to do
}
