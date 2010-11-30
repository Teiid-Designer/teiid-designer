package org.teiid.designer.vdb.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.vdb.VdbEntryTest;
import org.teiid.designer.vdb.VdbModelEntryTest;
import org.teiid.designer.vdb.VdbTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {VdbTest.class, VdbModelEntryTest.class, VdbEntryTest.class} )
public class AllTests {
    // nothing to do
}
