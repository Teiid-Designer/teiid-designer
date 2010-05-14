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
import java.util.Set;
import junit.framework.TestCase;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.modeler.schema.tools.common.SchemaTestUtil;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessorImpl;

public class AnonymousTypeNoTypesTest extends TestCase {

    SchemaTestUtil util;
    XSDSchema[] schemas;
    SchemaProcessor schemaProcessor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            SchemaTestUtil util = new SchemaTestUtil();
            List paths = new ArrayList();
            paths.add("./src/sources/Anonymous_Type.xsd"); //$NON-NLS-1$
            schemas = util.importSchemas(paths);
            schemaProcessor = new SchemaProcessorImpl(null);
            schemaProcessor.processSchemas(schemas);
        } catch (Exception e) {
            fail("Exception in AnonymousTypeNoTypesTest setUp: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    public void testRootElementsSize() {
        try {
            SchemaModel model = schemaProcessor.getSchemaModel();
            List roots = model.getPotentialRootElements();
            assertTrue("Roots size should be 3 not " + roots.size(), roots.size() == 3); //$NON-NLS-1$
        } catch (Exception e) {
            fail("Exception in AnonymousTypeNoTypesTest testRootElementsSize: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    public void testSelectedRootElementsSize() {
        try {
            SchemaModel model = schemaProcessor.getSchemaModel();
            List roots = model.getPotentialRootElements();
            int count = 0;
            for (Iterator iter = roots.iterator(); iter.hasNext();) {
                RootElement root = (RootElement)iter.next();
                if (root.isUseAsRoot()) {
                    count += 1;
                }
            }
            assertTrue("Selected roots size should be 1 not " + count, 1 == count); //$NON-NLS-1$
        } catch (Exception e) {
            fail("Exception in AnonymousTypeNoTypesTest testRootElementsSize: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    public void testCountFoldedElements() {
        try {
            SchemaModel model = schemaProcessor.getSchemaModel();
            RelationshipProcessor rp = RelationshipProcessorFactory.getQueryOptimizingProcessor(4, 3, 10);
            List roots = getDefaultRootSelections(model.getPotentialRootElements());
            Set rootsSet = new HashSet(roots);
            model.setSelectedRootElements(rootsSet);
            rp.calculateRelationshipTypes(model);
            List tables = model.getTables();
            assertTrue("tables size should be 2 not " + tables.size(), 2 == tables.size()); //$NON-NLS-1$
        } catch (Exception e) {
            fail("Exception in AnonymousTypeNoTypesTest testCountFoldedElements: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    public List getDefaultRootSelections( List roots ) {
        List result = new ArrayList();
        for (Iterator iter = roots.iterator(); iter.hasNext();) {
            RootElement root = (RootElement)iter.next();
            if (root.isUseAsRoot()) {
                result.add(root);
            }
        }
        return result;
    }

}
