package org.teiid.runtime.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.query.resolver.v7.Test7AccessPattern;
import org.teiid.query.resolver.v7.Test7AlterResolving;
import org.teiid.query.resolver.v7.Test7FunctionResolving;
import org.teiid.query.resolver.v7.Test7ProcedureResolving;
import org.teiid.query.resolver.v7.Test7Resolver;
import org.teiid.query.resolver.v7.Test7XMLResolver;
import org.teiid.query.resolver.v8.Test8AccessPattern;
import org.teiid.query.resolver.v8.Test8AlterResolving;
import org.teiid.query.resolver.v8.Test8FunctionResolving;
import org.teiid.query.resolver.v8.Test8ProcedureResolving;
import org.teiid.query.resolver.v8.Test8Resolver;
import org.teiid.query.resolver.v8.Test8XMLResolver;
import org.teiid.query.sql.lang.TestCriteriaOperator;
import org.teiid.query.sql.v7.Test7Cloning;
import org.teiid.query.sql.v7.Test7Create;
import org.teiid.query.sql.v7.Test7CreateDrop;
import org.teiid.query.sql.v7.TestQuery7Parser;
import org.teiid.query.sql.v7.TestSQLString7Visitor;
import org.teiid.query.sql.v8.Test8Cloning;
import org.teiid.query.sql.v8.Test8Create;
import org.teiid.query.sql.v8.Test8CreateDrop;
import org.teiid.query.sql.v8.TestQuery8Parser;
import org.teiid.query.sql.v8.TestSQLString8Visitor;

@RunWith( Suite.class )
@Suite.SuiteClasses( {
                                        TestCriteriaOperator.class,
                                        Test8Cloning.class, Test7Cloning.class,
                                        Test8Create.class, Test7Create.class,
                                        Test8CreateDrop.class, Test7CreateDrop.class,
                                        TestQuery8Parser.class, TestQuery7Parser.class,
                                        TestSQLString8Visitor.class, TestSQLString7Visitor.class,
                                        Test8Resolver.class, Test7Resolver.class,
                                        Test8AccessPattern.class, Test7AccessPattern.class,
                                        Test8AlterResolving.class, Test7AlterResolving.class,
                                        Test8FunctionResolving.class, Test7FunctionResolving.class,
                                        Test8ProcedureResolving.class, Test7ProcedureResolving.class,
                                        Test8XMLResolver.class, Test7XMLResolver.class
                                    } )
public class AllTests {
    // nothing to do
}
