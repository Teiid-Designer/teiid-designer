package org.teiid.designer.schema.tools.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.schema.tools.NameUtilTest;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.ColumnImplTest;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.DataTypeImplTest;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.DatabaseElementImplTest;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.TableImplTest;
import com.metamatrix.modeler.schema.tools.model.schema.impl.BaseRelationshipTest;
import com.metamatrix.modeler.schema.tools.model.schema.impl.RootElementImplTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {RootElementImplTest.class, BaseRelationshipTest.class, TableImplTest.class, DataTypeImplTest.class,
    DatabaseElementImplTest.class, ColumnImplTest.class, NameUtilTest.class} )
public class AllTests {
    // nothing to do
}
