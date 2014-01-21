package org.teiid.runtime.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.runtime.client.lang.ast.TestCriteriaOperator;
import org.teiid.runtime.client.sql.v7.Test7Cloning;
import org.teiid.runtime.client.sql.v7.TestQuery7Parser;
import org.teiid.runtime.client.sql.v7.TestSQLString7Visitor;
import org.teiid.runtime.client.sql.v8.Test8Cloning;
import org.teiid.runtime.client.sql.v8.TestQuery8Parser;
import org.teiid.runtime.client.sql.v8.TestSQLString8Visitor;

@RunWith( Suite.class )
@Suite.SuiteClasses( {
                                        TestCriteriaOperator.class,
                                        TestQuery8Parser.class, TestQuery7Parser.class,
                                        TestSQLString8Visitor.class, TestSQLString7Visitor.class,
                                        Test8Cloning.class, Test7Cloning.class
                                    } )
public class AllTests {
    // nothing to do
}
