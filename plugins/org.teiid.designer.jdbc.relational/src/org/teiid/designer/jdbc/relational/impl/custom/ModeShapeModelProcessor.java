/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational.impl.custom;

import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.designer.jdbc.JdbcException;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.metadata.JdbcTable;
import org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Processors;
import org.teiid.designer.jdbc.relational.impl.Context;
import org.teiid.designer.jdbc.relational.impl.JdbcModelStructure;
import org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.Table;


/**
 * The <code>ModeShapeModelProcessor</code> performs special processing when performing an import using the ModeShape JDBC driver.
 *
 * @since 8.0
 */
public class ModeShapeModelProcessor extends RelationalModelProcessorImpl {

    /**
     * Constructs a <code>ModeShapeModelProcessor</code> to be used by the JDBC importer.
     */
    public ModeShapeModelProcessor() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#createNewObject(org.teiid.designer.jdbc.metadata.JdbcNode,
     *      org.teiid.designer.metamodels.relational.RelationalEntity, org.teiid.designer.jdbc.relational.impl.JdbcModelStructure,
     *      java.util.Map, org.teiid.designer.jdbc.relational.impl.Context, int, int, int,
     *      org.eclipse.core.runtime.IProgressMonitor, java.util.List, java.util.List)
     */
    @Override
    protected RelationalEntity createNewObject( JdbcNode node,
                                                RelationalEntity parent,
                                                JdbcModelStructure modelStructure,
                                                Map nodesToModelObjects,
                                                Context context,
                                                int totalNum,
                                                int unitsPerModelObject,
                                                int index,
                                                IProgressMonitor monitor,
                                                List problems,
                                                List newTableObjects ) throws JdbcException {
        RelationalEntity entity = super.createNewObject(node,
                                                        parent,
                                                        modelStructure,
                                                        nodesToModelObjects,
                                                        context,
                                                        totalNum,
                                                        unitsPerModelObject,
                                                        index,
                                                        monitor,
                                                        problems,
                                                        newTableObjects);
        // make sure tables are not updateable
        if (entity instanceof Table) {
            Table table = (Table)entity;
            table.setSupportsUpdate(false);
        }

        return entity;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#setColumnInfo(org.teiid.designer.metamodels.relational.Column,
     *      org.teiid.designer.jdbc.metadata.JdbcTable, org.teiid.designer.jdbc.relational.impl.Context, java.util.List,
     *      java.lang.String, int, java.lang.String, int, int, int, int, java.lang.String, int)
     */
    @Override
    protected void setColumnInfo( Column column,
                                  JdbcTable tableNode,
                                  Context context,
                                  List problems,
                                  String name,
                                  int type,
                                  String typeName,
                                  int columnSize,
                                  int numDecDigits,
                                  int numPrecRadix,
                                  int nullable,
                                  String defaultValue,
                                  int charOctetLen ) {
        super.setColumnInfo(column,
                            tableNode,
                            context,
                            problems,
                            name,
                            type,
                            typeName,
                            columnSize,
                            numDecDigits,
                            numPrecRadix,
                            nullable,
                            defaultValue,
                            charOctetLen);
        // set all columns to not be updateable
        column.setUpdateable(false);

        // make sure mode:properties is not selectable
        if ("mode:properties".equals(name)) { //$NON-NLS-1$
            column.setSelectable(false);
        }
    }
    
	@Override
	public String getType() {
		return Processors.MODESHAPE;
	}

}
