package org.teiid83.server.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid83.runtime.ExecutionAdminTest;
import org.teiid83.runtime.TeiidTranslatorTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {ExecutionAdminTest.class, TeiidTranslatorTest.class} )
public class AllTests {
    // nothing to do
}
