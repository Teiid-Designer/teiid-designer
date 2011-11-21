/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.Properties;


/**
 * 
 */
public class RelationalProcedureResultSet extends RelationalTable {

    
    public RelationalProcedureResultSet() {
        super();
        setType(TYPES.RESULT_SET);
    }
    
    /**
     * @param name
     */
    public RelationalProcedureResultSet( String name ) {
        super(name);
        setType(TYPES.RESULT_SET);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#addAccessPattern(org.teiid.designer.relational.model.RelationalAccessPattern)
     */
    @Override
    public void addAccessPattern( RelationalAccessPattern ap ) {
        throw new UnsupportedOperationException("addAccessPattern() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#addForeignKey(org.teiid.designer.relational.model.RelationalForeignKey)
     */
    @Override
    public void addForeignKey( RelationalForeignKey fk ) {
        throw new UnsupportedOperationException("addForeignKey() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setPrimaryKey(org.teiid.designer.relational.model.RelationalPrimaryKey)
     */
    @Override
    public void setPrimaryKey( RelationalPrimaryKey pk ) {
        throw new UnsupportedOperationException("addPrimaryKey() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setUniqueConstraint(org.teiid.designer.relational.model.RelationalUniqueConstraint)
     */
    @Override
    public void setUniqueConstraint( RelationalUniqueConstraint uc ) {
        throw new UnsupportedOperationException("addUniqueConstraint() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setCardinality(int)
     */
    @Override
    public void setCardinality( int cardinality ) {
        throw new UnsupportedOperationException("setCardinality() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setMaterialized(boolean)
     */
    @Override
    public void setMaterialized( boolean materialized ) {
        throw new UnsupportedOperationException("setMaterialized() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setMaterializedTable(org.teiid.designer.relational.model.RelationalReference)
     */
    @Override
    public void setMaterializedTable( RelationalReference materializedTable ) {
        throw new UnsupportedOperationException("setMaterializedTable() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setSupportsUpdate(boolean)
     */
    @Override
    public void setSupportsUpdate( boolean supportsUpdate ) {
        throw new UnsupportedOperationException("setSupportsUpdate() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setSystem(boolean)
     */
    @Override
    public void setSystem( boolean system ) {
        throw new UnsupportedOperationException("setSystem() not supported for Procedure Result Sets"); //$NON-NLS-1$
    }

    public void setProperties(Properties props) {
        for( Object key : props.keySet() ) {
            String keyStr = (String)key;
            String value = props.getProperty(keyStr);

            if( value != null && value.length() == 0 ) {
                continue;
            }
            
            if( keyStr.equalsIgnoreCase(KEY_NAME) ) {
                setName(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NAME_IN_SOURCE) ) {
                setNameInSource(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DESCRIPTION) ) {
                setDescription(value);
            } 
        }
    }
}
