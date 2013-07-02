/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.modeshape.sequencer.ddl.dialect.mysql.MySqlDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalSchema;

/**
 *
 */
public class MySQLImporter extends StandardImporter {

    /**
     * Create RelationalReference objects
     * @param node the provided AstNode
     * @param model the RelationalModel being created
     * @param schema the schema
     * @return the map of AstNodes which need to be deferred
     * @throws Exception 
     */
    @Override
    protected Map<AstNode,RelationalReference> createObject(AstNode node, RelationalModel model, RelationalSchema schema) throws Exception {
      	Map<AstNode,RelationalReference> deferredMap = new HashMap<AstNode,RelationalReference>();

        if (is(node, MySqlDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)) {
      		deferredMap.put(node, null);
        } else if (is(node, MySqlDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT)) {
            createProcedure(node, model);
        } else if (is(node, MySqlDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)) {
            createProcedure(node, model).setFunction(true);
        } else
            return super.createObject(node, model, schema);
      	return deferredMap;
    }
    
    /**
     * Create deferred objects using the supplied map
     * @param deferredNodes the map of deferred AstNodes
     * @param model the RelationalModel being created
     * @throws Exception 
     */
    @Override
	protected void createDeferredObjects(Map<AstNode,RelationalReference> deferredNodes, RelationalModel model) throws Exception {

    	Set<AstNode> astNodes = deferredNodes.keySet();
    	for(AstNode node:astNodes) {
            if (is(node, MySqlDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)) {
                RelationalIndex index = getFactory().createIndex();
                Info info = createInfo(node, model);
                if (info.getSchema() == null)
                    model.addChild(index);
                else
                    info.getSchema().getIndexes().add(index);

                initialize(index, node, info.getName());
            } 
    	}
    }
    
}
