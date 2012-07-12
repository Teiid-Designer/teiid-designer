package org.teiid.designer.schema.tools.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.schema.tools.NameUtilTest;
import org.teiid.designer.schema.tools.model.jdbc.internal.ColumnImplTest;
import org.teiid.designer.schema.tools.model.jdbc.internal.DataTypeImplTest;
import org.teiid.designer.schema.tools.model.jdbc.internal.DatabaseElementImplTest;
import org.teiid.designer.schema.tools.model.jdbc.internal.TableImplTest;
import org.teiid.designer.schema.tools.model.schema.impl.BaseRelationshipTest;
import org.teiid.designer.schema.tools.model.schema.impl.RootElementImplTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {RootElementImplTest.class, BaseRelationshipTest.class, TableImplTest.class, DataTypeImplTest.class,
    DatabaseElementImplTest.class, ColumnImplTest.class, NameUtilTest.class} )
public class AllTests {
    // nothing to do
}
