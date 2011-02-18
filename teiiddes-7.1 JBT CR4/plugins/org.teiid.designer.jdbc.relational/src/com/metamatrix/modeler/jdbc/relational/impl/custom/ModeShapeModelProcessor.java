/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl.custom;

import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.relational.impl.Context;
import com.metamatrix.modeler.jdbc.relational.impl.JdbcModelStructure;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * The <code>ModeShapeModelProcessor</code> performs special processing when performing an import using the ModeShape JDBC driver.
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
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#createNewObject(com.metamatrix.modeler.jdbc.metadata.JdbcNode,
     *      com.metamatrix.metamodels.relational.RelationalEntity, com.metamatrix.modeler.jdbc.relational.impl.JdbcModelStructure,
     *      java.util.Map, com.metamatrix.modeler.jdbc.relational.impl.Context, int, int, int,
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
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#setColumnInfo(com.metamatrix.metamodels.relational.Column,
     *      com.metamatrix.modeler.jdbc.metadata.JdbcTable, com.metamatrix.modeler.jdbc.relational.impl.Context, java.util.List,
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

}
