/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.derby.DerbyDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalSchema;
import org.teiid.designer.relational.model.RelationalTable;

/**
 *
 */
public class DerbyImporter extends StandardImporter {


    @Override
    protected RelationalProcedure createProcedure(AstNode procedureNode, RelationalModel model) throws Exception {
        RelationalProcedure procedure = super.createProcedure(procedureNode, model);

        for (AstNode child : procedureNode) {
            if (! is(child, DerbyDdlLexicon.TYPE_FUNCTION_PARAMETER))
                continue;

            RelationalParameter prm = getFactory().createParameter();
            procedure.getParameters().add(prm);
            initialize(prm, child);
            String datatype = child.getProperty(StandardDdlLexicon.DATATYPE_NAME).toString();
            prm.setNativeType(datatype);

            String teiidType = getTeiidDataTypeName(datatype);
            prm.setDatatype(teiidType);

            Object prop = child.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
            if (prop != null)
                prm.setLength(Integer.parseInt(prop.toString()));

            prop = child.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
            if (prop != null)
                prm.setPrecision(Integer.parseInt(prop.toString()));

            prop = child.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
            if (prop != null)
                prm.setScale(Integer.parseInt(prop.toString()));

            prop = child.getProperty(StandardDdlLexicon.NULLABLE);
            if (prop != null)
                //prm.setNullable(prop.toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$
            	prm.setNullable(prop.toString()); 

            prop = child.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
            if (prop != null)
                prm.setDefaultValue(prop.toString());
        }

        return procedure;
    }
    
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

      	if (is(node, DerbyDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)) {
      		deferredMap.put(node, null);
        } else if (is(node, DerbyDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT))  {
            createProcedure(node, model);
        } else if (is(node, DerbyDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)) {
            createProcedure(node, model).setFunction(true);
        } else {
            return super.createObject(node, model, schema);
        }
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
		Collection<RelationalReference> allRefs = model.getAllReferences();

		// Make first pass to create the PKs
		Set<AstNode> astNodes = deferredNodes.keySet();
		for(AstNode node:astNodes) {
			if (is(node, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT)) {
				RelationalTable table = (RelationalTable)deferredNodes.get(node);
				createPrimaryKey(node, table, allRefs);
			} else if (is(node, StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT)) {
				RelationalTable table = find(RelationalTable.class, node, null, allRefs);
				for (AstNode node1 : node) {
					if (is(node1, StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION)) 
						createPrimaryKey(node1, table, allRefs);
				}
			}
		}

		// Second pass create other constraints
		for(AstNode node:astNodes) {
          	if (is(node, DerbyDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)) {
                RelationalIndex index = getFactory().createIndex();
                Info info = createInfo(node, model);
                if (info.getSchema() == null)
                    model.addChild(index);
                else
                    info.getSchema().getIndexes().add(index);

                initialize(index, node, info.getName());
                Object prop = node.getProperty(DerbyDdlLexicon.UNIQUE_INDEX);
                if (prop != null)
                    index.setUnique((Boolean) prop);
     
                prop = node.getProperty(DerbyDdlLexicon.TABLE_NAME);
                if (prop == null)
                    return;

                try {
                    RelationalTable table = find(RelationalTable.class, prop.toString(), node, null, allRefs);
                    for (AstNode node1 : node) {
                        // Probably need to check for a simple column reference for Oracle
                        if (node1.hasMixin(DerbyDdlLexicon.TYPE_INDEX_COLUMN_REFERENCE)) {
                            try {
                                index.getColumns().add(find(RelationalColumn.class, node1, table, allRefs));
                            } catch (EntityNotFoundException error) {
                                addProgressMessage(error.getMessage());
                            }
                        }
                    }
                } catch (EntityNotFoundException error) {
                    addProgressMessage(error.getMessage());
                }
            } else if (is(node, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT)) {
				RelationalTable table = (RelationalTable)deferredNodes.get(node);
				createConstraint(node, table, allRefs);
			} else if (is(node, StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT)) {
				RelationalTable table = find(RelationalTable.class, node, null, allRefs);
				for (AstNode node1 : node) {
					if (is(node1, StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION)) 
						createConstraint(node1, table, allRefs);
					else if (is(node1, StandardDdlLexicon.TYPE_ADD_COLUMN_DEFINITION))
						createColumn(node1, table);
				}
			}
		}
    }
    	
}
