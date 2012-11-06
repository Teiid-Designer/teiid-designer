package org.teiid8server.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid8.runtime.ExecutionAdminTest;
import org.teiid8.runtime.TeiidTranslatorTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {ExecutionAdminTest.class, TeiidTranslatorTest.class} )
public class AllTests {
    // nothing to do
}
