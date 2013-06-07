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
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.derby.DerbyDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;

/**
 *
 */
public class DerbyImporter extends StandardImporter {


    @Override
    protected Procedure createProcedure(AstNode procedureNode, List<EObject> roots) throws Exception {
        Procedure procedure = super.createProcedure(procedureNode, roots);

        for (AstNode child : procedureNode) {
            if (! is(child, DerbyDdlLexicon.TYPE_FUNCTION_PARAMETER))
                continue;

            ProcedureParameter prm = getFactory().createProcedureParameter();
            procedure.getParameters().add(prm);
            initialize(prm, child);
            String datatype = child.getProperty(StandardDdlLexicon.DATATYPE_NAME).toString();
            prm.setNativeType(datatype);

            EObject type = getDataType(datatype);
            prm.setType(type);

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
                prm.setNullable(prop.toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$

            prop = child.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
            if (prop != null)
                prm.setDefaultValue(prop.toString());
        }

        return procedure;
    }
    
    @Override
    protected void create(AstNode node, List<EObject> roots, Schema schema) throws Exception {
        if (is(node, DerbyDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)) {
            Index index = getFactory().createIndex();
            Info info = createInfo(node, roots);
            if (info.getSchema() == null)
                roots.add(index);
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
                Table table = find(Table.class, prop.toString(), node, null, roots);
                for (AstNode node1 : node) {
                    // Probably need to check for a simple column reference for Oracle
                    if (node1.hasMixin(DerbyDdlLexicon.TYPE_INDEX_COLUMN_REFERENCE)) {
                        try {
                            index.getColumns().add(find(Column.class, node1, table, roots));
                        } catch (EntityNotFoundException error) {
                            addProgressMessage(error.getMessage());
                        }
                    }
                }
            } catch (EntityNotFoundException error) {
                addProgressMessage(error.getMessage());
            }
        } else if (is(node, DerbyDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT))  {
            createProcedure(node, roots);
        } else if (is(node, DerbyDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)) {
            createProcedure(node, roots).setFunction(true);
        } else {
            super.create(node, roots, schema);
        }
    }
	
}
