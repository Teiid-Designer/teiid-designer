/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.core.log.NullLogger;
import com.metamatrix.modeler.schema.tools.common.SchemaTestUtil;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessorImpl;

public class RequestFoldingTest extends TestCase {

    SchemaTestUtil util;
    XSDSchema[] schemas;
    SchemaProcessor schemaProcessor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SchemaTestUtil util = new SchemaTestUtil();
        List paths = new ArrayList();
        paths.add("./src/sources/personal.xsd"); //$NON-NLS-1$
        schemas = util.importSchemas(paths);
        schemaProcessor = new SchemaProcessorImpl(new NullLogger(), null);
    }

    public void testAll() {
        try {
            schemaProcessor.processSchemas(schemas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SchemaModel model = schemaProcessor.getSchemaModel();
        List qRoots = model.getPotentialRootElements();
        HashSet roots = new HashSet();
        for (Iterator iter = qRoots.iterator(); iter.hasNext();) {
            RootElement elem = (RootElement)iter.next();
            if (elem.isUseAsRoot()) {
                roots.add(elem);
            }
        }
        SchemaModel clone = model.copy();
        RelationshipProcessor relationshipProcessor = RelationshipProcessorFactory.getQueryOptimizingProcessor(4, 3, 10);
        clone.setSelectedRootElements(roots);
        relationshipProcessor.calculateRelationshipTypes(clone);
        SchemaTestUtil.printTables(clone);
    }
}
