package org.teiid82server.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid82.runtime.ExecutionAdminTest;
import org.teiid82.runtime.TeiidTranslatorTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {ExecutionAdminTest.class, TeiidTranslatorTest.class} )
public class AllTests {
    // nothing to do
}
