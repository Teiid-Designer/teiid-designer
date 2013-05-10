package org.teiid84.server.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid84.runtime.ExecutionAdminTest;
import org.teiid84.runtime.TeiidTranslatorTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {ExecutionAdminTest.class, TeiidTranslatorTest.class} )
public class AllTests {
    // nothing to do
}
