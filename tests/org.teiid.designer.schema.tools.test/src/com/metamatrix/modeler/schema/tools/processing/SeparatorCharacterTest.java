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
import com.metamatrix.modeler.schema.tools.model.jdbc.Column;
import com.metamatrix.modeler.schema.tools.model.jdbc.Table;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessorImpl;

public class SeparatorCharacterTest extends TestCase {

    public void testDefaultSep() {
        SchemaTestUtil util;
        XSDSchema[] schemas;
        SchemaProcessor schemaProcessor;

        try {
            util = new SchemaTestUtil();
            List paths = new ArrayList();
            paths.add("./src/sources/CollapseAll.xsd"); //$NON-NLS-1$
            schemas = util.importSchemas(paths);
            schemaProcessor = new SchemaProcessorImpl(null);
            Table tab = getTables(schemas, schemaProcessor);
            Column[] columns = tab.getColumns();
            Column col = columns[1];
            assertEquals("Column name is wrong", "yankee_zulu_end", col.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (Exception e) {
            fail("Exception in AnonymousTypeNoTypesTest setUp: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    public void testOtherSep() {
        SchemaTestUtil util;
        XSDSchema[] schemas;
        SchemaProcessor schemaProcessor;

        try {
            util = new SchemaTestUtil();
            List paths = new ArrayList();
            paths.add("./src/sources/CollapseAll.xsd"); //$NON-NLS-1$
            schemas = util.importSchemas(paths);
            schemaProcessor = new SchemaProcessorImpl("-"); //$NON-NLS-1$
            Table tab = getTables(schemas, schemaProcessor);
            Column[] columns = tab.getColumns();
            Column col = columns[1];
            assertEquals("Column name is wrong", "yankee-zulu-end", col.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (Exception e) {
            fail("Exception in AnonymousTypeNoTypesTest setUp: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    private Table getTables( XSDSchema[] schemas,
                             SchemaProcessor schemaProcessor ) throws Exception {
        schemaProcessor.processSchemas(schemas);
        SchemaModel model = schemaProcessor.getSchemaModel();
        RelationshipProcessor rp = RelationshipProcessorFactory.getQueryOptimizingProcessor(4, 3, 10);
        List roots = getDefaultRootSelections(model.getPotentialRootElements());
        Set rootsSet = new HashSet(roots);
        model.setSelectedRootElements(rootsSet);
        rp.calculateRelationshipTypes(model);
        List tables = model.getTables();
        Table tab = (Table)tables.get(0);
        return tab;
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
