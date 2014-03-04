package org.teiid772.server.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid772.parser.TestParser;
import org.teiid772.resolver.TestAccessPattern;
import org.teiid772.resolver.TestAlterResolving;
import org.teiid772.resolver.TestFunctionResolving;
import org.teiid772.resolver.TestProcedureResolving;
import org.teiid772.resolver.TestResolver;
import org.teiid772.resolver.TestXMLResolver;
import org.teiid772.runtime.ExecutionAdminTest;
import org.teiid772.runtime.TeiidTranslatorTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {
                                        ExecutionAdminTest.class, TeiidTranslatorTest.class,
                                        TestParser.class,
                                        TestResolver.class, TestAccessPattern.class,
                                        TestAlterResolving.class, TestFunctionResolving.class,
                                        TestProcedureResolving.class, TestXMLResolver.class
                                  } )
public class AllTests {
    // nothing to do
}
