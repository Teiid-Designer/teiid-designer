package org.teiid84.server.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid84.parser.TestParser;
import org.teiid84.resolver.TestAccessPattern;
import org.teiid84.resolver.TestAlterResolving;
import org.teiid84.resolver.TestCreateDrop;
import org.teiid84.resolver.TestFunctionResolving;
import org.teiid84.resolver.TestProcedureResolving;
import org.teiid84.resolver.TestResolver;
import org.teiid84.resolver.TestXMLResolver;
import org.teiid84.runtime.ExecutionAdminTest;
import org.teiid84.runtime.TeiidTranslatorTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {
                                        ExecutionAdminTest.class, TeiidTranslatorTest.class,
                                        TestCreateDrop.class, TestParser.class,
                                        TestResolver.class, TestAccessPattern.class,
                                        TestAlterResolving.class, TestFunctionResolving.class,
                                        TestProcedureResolving.class, TestXMLResolver.class
                                  } )
public class AllTests {
    // nothing to do
}
