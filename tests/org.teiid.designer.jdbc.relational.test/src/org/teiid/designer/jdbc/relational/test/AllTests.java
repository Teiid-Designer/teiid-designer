package org.teiid.designer.jdbc.relational.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.internal.jdbc.relational.TestJdbcImporter;
import com.metamatrix.modeler.jdbc.relational.TestJdbcRelationalPlugin;
import com.metamatrix.modeler.jdbc.relational.impl.TestJdbcModelStructure;
import com.metamatrix.modeler.jdbc.relational.impl.TestObjectMatcher;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestJdbcImporter.class, TestJdbcRelationalPlugin.class, TestObjectMatcher.class,
    TestJdbcModelStructure.class} )
public class AllTests {
    // nothing to do
}
