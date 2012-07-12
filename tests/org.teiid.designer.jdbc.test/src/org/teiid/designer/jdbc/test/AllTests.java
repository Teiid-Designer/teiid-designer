package org.teiid.designer.jdbc.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.jdbc.TestJdbcManagerImpl;
import org.teiid.designer.jdbc.TestJdbcPlugin;
import org.teiid.designer.jdbc.custom.TestExcelConnectionHandler;
import org.teiid.designer.jdbc.custom.TestExcelDatabaseMetaDataHandler;
import org.teiid.designer.jdbc.data.TestFakeRequest;
import org.teiid.designer.jdbc.data.TestMethodRequest;
import org.teiid.designer.jdbc.data.TestQueryRequest;
import org.teiid.designer.jdbc.jdbctest.TestJdbcMetadataClient;
import org.teiid.designer.jdbc.metadata.impl.TestFakeJdbcDatabase;
import org.teiid.designer.jdbc.metadata.impl.TestJdbcDatabaseImpl;
import org.teiid.designer.jdbc.metadata.impl.TestJdbcNodeCache;
import org.teiid.designer.jdbc.metadata.impl.TestJdbcNodeImpl;
import org.teiid.designer.jdbc.metadata.impl.TestJdbcNodeSelections;
import org.teiid.designer.jdbc.metadata.impl.TestJdbcProcedureImpl;
import org.teiid.designer.jdbc.metadata.impl.TestJdbcTableImpl;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestJdbcTableImpl.class, TestJdbcProcedureImpl.class, TestJdbcNodeSelections.class, TestJdbcNodeImpl.class,
    TestJdbcNodeCache.class, TestJdbcDatabaseImpl.class, TestFakeJdbcDatabase.class, TestQueryRequest.class,
    TestMethodRequest.class, TestFakeRequest.class, TestExcelDatabaseMetaDataHandler.class, TestExcelConnectionHandler.class,
    TestJdbcPlugin.class, TestJdbcManagerImpl.class, TestJdbcMetadataClient.class} )
public class AllTests {
    // nothing to do
}
