package org.teiid.designer.jdbc.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.jdbctest.TestJdbcMetadataClient;
import com.metamatrix.modeler.internal.jdbc.TestJdbcManagerImpl;
import com.metamatrix.modeler.jdbc.TestJdbcPlugin;
import com.metamatrix.modeler.jdbc.custom.TestExcelConnectionHandler;
import com.metamatrix.modeler.jdbc.custom.TestExcelDatabaseMetaDataHandler;
import com.metamatrix.modeler.jdbc.data.TestFakeRequest;
import com.metamatrix.modeler.jdbc.data.TestMethodRequest;
import com.metamatrix.modeler.jdbc.data.TestQueryRequest;
import com.metamatrix.modeler.jdbc.metadata.impl.TestFakeJdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.impl.TestJdbcDatabaseImpl;
import com.metamatrix.modeler.jdbc.metadata.impl.TestJdbcNodeCache;
import com.metamatrix.modeler.jdbc.metadata.impl.TestJdbcNodeImpl;
import com.metamatrix.modeler.jdbc.metadata.impl.TestJdbcNodeSelections;
import com.metamatrix.modeler.jdbc.metadata.impl.TestJdbcProcedureImpl;
import com.metamatrix.modeler.jdbc.metadata.impl.TestJdbcTableImpl;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestJdbcTableImpl.class, TestJdbcProcedureImpl.class, TestJdbcNodeSelections.class, TestJdbcNodeImpl.class,
    TestJdbcNodeCache.class, TestJdbcDatabaseImpl.class, TestFakeJdbcDatabase.class, TestQueryRequest.class,
    TestMethodRequest.class, TestFakeRequest.class, TestExcelDatabaseMetaDataHandler.class, TestExcelConnectionHandler.class,
    TestJdbcPlugin.class, TestJdbcManagerImpl.class, TestJdbcMetadataClient.class} )
public class AllTests {
    // nothing to do
}
