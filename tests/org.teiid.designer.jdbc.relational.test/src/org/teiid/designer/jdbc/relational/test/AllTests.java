package org.teiid.designer.jdbc.relational.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.jdbc.relational.TestJdbcImporter;
import org.teiid.designer.jdbc.relational.TestJdbcRelationalPlugin;
import org.teiid.designer.jdbc.relational.impl.TestJdbcModelStructure;
import org.teiid.designer.jdbc.relational.impl.TestObjectMatcher;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestJdbcImporter.class, TestJdbcRelationalPlugin.class, TestObjectMatcher.class,
    TestJdbcModelStructure.class} )
public class AllTests {
    // nothing to do
}
