/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.modeler.schema.tools.common.SchemaTestUtil;
import com.metamatrix.modeler.schema.tools.model.jdbc.Column;
import com.metamatrix.modeler.schema.tools.model.jdbc.Table;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessorImpl;

/**
 * @author Jdoyle This class provides some base facilities for testing the processing of the schemaTools subsystem. Classes that
 *         extend this class must implement the abstract methods to provide the correct/expected results. The test that is
 *         executed asserts that the structure of the object graph is as expected. Specifically it asserts the following. The
 *         expected number of potential root elements. The number of tables produced. The number of attributes per table. The
 *         number of children per table. The number of parents per table.
 */
public abstract class BaseSchemaProcessingTestClass extends TestCase {

    SchemaTestUtil util;
    XSDSchema[] schemas;
    SchemaProcessor schemaProcessor;

    public BaseSchemaProcessingTestClass() {
        super();
    }

    public BaseSchemaProcessingTestClass( String arg0 ) {
        super(arg0);
    }

    protected abstract boolean representTypes();

    protected abstract List getSchemaPaths();

    protected abstract int getPotentialRootCount();

    protected abstract int getTableCount();

    protected String[] getTableXPaths() {
        return null;
    }

    // Don't forget the mmid's, they are added by the schema processor.
    protected abstract int[] getAttributeCounts();

    protected abstract int[] getChildCounts();

    protected abstract int[] getParentCounts();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SchemaTestUtil util = new SchemaTestUtil();
        schemas = util.importSchemas(getSchemaPaths());
        schemaProcessor = new SchemaProcessorImpl(null);
        schemaProcessor.representTypes(representTypes());
    }

    public void testGraphGeometry() {
        try {
            schemaProcessor.processSchemas(schemas);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        SchemaModel model = schemaProcessor.getSchemaModel();
        List qRoots = model.getPotentialRootElements();
        assertEquals("The number of potential root elements is incorrect", getPotentialRootCount(), qRoots.size()); //$NON-NLS-1$
        HashSet roots = selectRootElements(qRoots);

        // assertEquals("The number of selected root elements is incorrect", 1 , roots.size());
        SchemaModel clone = model.copy();
        RelationshipProcessor relationshipProcessor;
        if (processAsRequest()) {
            relationshipProcessor = RelationshipProcessorFactory.getRequestProcessor();
        } else {
            relationshipProcessor = RelationshipProcessorFactory.getQueryOptimizingProcessor(4, 3, 10);
        }
        clone.setSelectedRootElements(roots);
        relationshipProcessor.calculateRelationshipTypes(clone);

        String[] tableXPaths = getTableXPaths();
        int[] attributeCount = getAttributeCounts();
        int[] childCount = getChildCounts();
        int[] parentCount = getParentCounts();
        int resultIndex = 0;
        List tables = clone.getTables();
        assertEquals("The number of tables is incorrect", getTableCount(), tables.size()); //$NON-NLS-1$
        for (Iterator iter = tables.iterator(); iter.hasNext();) {
            Table table = (Table)iter.next();
            if (null != tableXPaths) {
                assertEquals("The table XPath is not correct for table " + table.getName(), //$NON-NLS-1$
                             tableXPaths[resultIndex],
                             table.getOutputXPath());
            }
            Column[] columns = table.getColumns();
            if (null != attributeCount) {
                assertEquals("The number of attributes/columns is incorrect for table " + table.getName(), //$NON-NLS-1$
                             attributeCount[resultIndex],
                             columns.length);
            }
            for (int i = 0; i < columns.length; i++) {
            }

            Table[] helloChildren = table.getChildTables();
            if (null != childCount) {
                assertEquals("The number of children is incorrect for table " + table.getName(), //$NON-NLS-1$
                             childCount[resultIndex],
                             helloChildren.length);
            }
            for (int i = 0; i < helloChildren.length; i++) {
            }

            Table[] parents = table.getParentTables();
            if (null != parentCount) {
                assertEquals("The number of parents is incorrect for table " + table.getName(), //$NON-NLS-1$
                             parentCount[resultIndex],
                             parents.length);
            }
            for (int i = 0; i < parents.length; i++) {
            }
            ++resultIndex;
        }
    }

    protected HashSet selectRootElements( List qRoots ) {
        HashSet roots = new HashSet();
        for (Iterator iter = qRoots.iterator(); iter.hasNext();) {
            RootElement elem = (RootElement)iter.next();
            if (elem.isUseAsRoot()) {
                roots.add(elem);
            }
        }
        return roots;
    }

    protected boolean processAsRequest() {
        return false;
    }
}
