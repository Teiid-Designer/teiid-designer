/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.sequencer.ddl.dialect.mysql.MySqlDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.Schema;

/**
 *
 */
public class MySQLImporter extends StandardImporter {

    @Override
    protected void create(AstNode node, List<EObject> roots, Schema schema) throws Exception {
        if (is(node, MySqlDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)) {
            Index index = getFactory().createIndex();
            Info info = createInfo(node, roots);
            if (info.getSchema() == null)
                roots.add(index);
            else
                info.getSchema().getIndexes().add(index);

            initialize(index, node, info.getName());
        } else if (is(node, MySqlDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT)) {
            createProcedure(node, roots);
        } else if (is(node, MySqlDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)) {
            createProcedure(node, roots).setFunction(true);
        } else
            super.create(node, roots, schema);
    }
}
