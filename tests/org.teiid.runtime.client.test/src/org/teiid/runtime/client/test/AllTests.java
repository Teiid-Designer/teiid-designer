package org.teiid.runtime.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.runtime.client.lang.ast.TestCriteriaOperator;
import org.teiid.runtime.client.sql.v8.TestQuery8Parser;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestCriteriaOperator.class, TestQuery8Parser.class} )
public class AllTests {
    // nothing to do
}
