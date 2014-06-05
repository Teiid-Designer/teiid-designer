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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMapping;
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
public class OracleImporter extends StandardImporter {
	
	private static final String VARCHAR2_TYPE_NAME = "VARCHAR2"; //$NON-NLS-1$
	private static final String NVARCHAR2_TYPE_NAME = "NVARCHAR2"; //$NON-NLS-1$
	private static final String NUMBER_TYPE_NAME = "NUMBER"; //$NON-NLS-1$
			
    @Override
    protected RelationalProcedure createProcedure(AstNode procedureNode, RelationalModel model) throws Exception {
    	RelationalProcedure procedure = super.createProcedure(procedureNode, model);

        for (AstNode child : procedureNode) {
            if (! is(child, OracleDdlLexicon.TYPE_FUNCTION_PARAMETER))
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
            	prm.setNullable(prop.toString()); 

            prop = child.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
            if (prop != null)
                prm.setDefaultValue(prop.toString());

            prop = child.getProperty(OracleDdlLexicon.IN_OUT_NO_COPY);
            if (prop != null) {
                String direction = prop.toString();
                prm.setDirection(direction);
            }
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

        if (is(node, OracleDdlLexicon.TYPE_CREATE_TABLE_INDEX_STATEMENT)) {
      		deferredMap.put(node, null);
        } else if (is(node, OracleDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT)) {
            createProcedure(node, model);
        } else if (is(node, OracleDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)) {
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
            if (is(node, OracleDdlLexicon.TYPE_CREATE_TABLE_INDEX_STATEMENT)) {
                RelationalIndex index = getFactory().createIndex();
                Info info = createInfo(node, model);
                if (info.getSchema() == null)
                    model.addChild(index);
                else
                    info.getSchema().getIndexes().add(index);

                initialize(index, node, info.getName());

                Object prop = node.getProperty(OracleDdlLexicon.UNIQUE_INDEX);
                if (prop != null) index.setUnique((Boolean)prop);
                
                // Get Table referenced
                String tableName = (String)node.getProperty(OracleDdlLexicon.TABLE_NAME);
				RelationalTable table = find(RelationalTable.class, tableName, node, null, allRefs);

				// Get columns referenced and add them to the index
				if(table!=null) {
					List<AstNode> childNodes = node.getChildren();
					for(AstNode child : childNodes) {
						if(is(child, StandardDdlLexicon.TYPE_COLUMN_REFERENCE)) {
							try {
								RelationalColumn col = find(RelationalColumn.class, child, table, allRefs);
								if(col!=null) {
									index.getColumns().add(col);
								}
							} catch (EntityNotFoundException error) {
								addProgressMessage(error.getMessage());
							}
						}
					}
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
    
	/**
	 * @param jdbcTypeName
	 *
	 * @return {@link EObject} represented by the given data type id
	 * @throws Exception
	 */
	@Override
	protected String getTeiidDataTypeName(String jdbcTypeName) throws Exception {
	    String standardName = jdbcTypeName;
	    if (VARCHAR2_TYPE_NAME.equalsIgnoreCase(jdbcTypeName) || NVARCHAR2_TYPE_NAME.equalsIgnoreCase(jdbcTypeName)) {
	        standardName = RelationalTypeMapping.SQL_TYPE_NAMES.VARCHAR;
	    }
	    
	    if (NUMBER_TYPE_NAME.equalsIgnoreCase(jdbcTypeName)) {
	        standardName = RelationalTypeMapping.SQL_TYPE_NAMES.NUMERIC;
	    }
	    
	    return super.getTeiidDataTypeName(standardName);
	}
   
}
