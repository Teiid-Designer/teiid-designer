package org.teiid.runtime.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.query.sql.lang.TestCriteriaOperator;
import org.teiid.query.sql.v7.Test7Cloning;
import org.teiid.query.sql.v7.TestQuery7Parser;
import org.teiid.query.sql.v7.TestSQLString7Visitor;
import org.teiid.query.sql.v8.Test8Cloning;
import org.teiid.query.sql.v8.TestQuery8Parser;
import org.teiid.query.sql.v8.TestSQLString8Visitor;

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
