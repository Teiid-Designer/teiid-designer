/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;


/**
 * 
 */
public class RelationalView extends RelationalTable {

    public RelationalView() {
        super();
        setType(TYPES.VIEW);
    }
    /**
     * @param name
     */
    public RelationalView( String name ) {
        super(name);
        setType(TYPES.VIEW);
    }
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#addForeignKey(org.teiid.designer.relational.model.RelationalForeignKey)
     */
    @Override
    public void addForeignKey( RelationalForeignKey fk ) {
        throw new UnsupportedOperationException("addForeignKey() not supported for Relational Views"); //$NON-NLS-1$
    }
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setPrimaryKey(org.teiid.designer.relational.model.RelationalPrimaryKey)
     */
    @Override
    public void setPrimaryKey( RelationalPrimaryKey pk ) {
        throw new UnsupportedOperationException("addPrimaryKey() not supported for Relational Views"); //$NON-NLS-1$
    }
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.relational.model.RelationalTable#setUniqueConstraint(org.teiid.designer.relational.model.RelationalUniqueConstraint)
     */
    @Override
    public void setUniqueConstraint( RelationalUniqueConstraint uc ) {
        throw new UnsupportedOperationException("addUniqueConstraint() not supported for Relational Views"); //$NON-NLS-1$
    }

    
}
