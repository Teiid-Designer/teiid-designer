package org.teiid.designer.vdb.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.vdb.VdbFileEntryTest;
import org.teiid.designer.vdb.VdbModelEntryTest;
import org.teiid.designer.vdb.VdbTest;
import org.teiid.designer.vdb.dynamic.TestAllowLanguages;
import org.teiid.designer.vdb.dynamic.TestDynamicVdbExport;
import org.teiid.designer.vdb.dynamic.TestDynamicVdbImport;

@RunWith( Suite.class )
@Suite.SuiteClasses( {
    VdbTest.class,
    VdbModelEntryTest.class,
    VdbFileEntryTest.class,
    TestAllowLanguages.class,
    TestDynamicVdbExport.class,
    TestDynamicVdbImport.class
    } )
public class AllTests {
    // nothing to do
}
