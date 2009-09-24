/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import org.eclipse.core.runtime.IPath;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;

/**
 * FakeJdbcNode
 */
public class FakeJdbcNode extends JdbcNodeImpl {

    public static final int FAKE_NODE_TYPE = 10000;

    /**
     * Construct an instance of FakeJdbcNode.
     * 
     * @param type
     * @param name
     * @param parent
     */
    public FakeJdbcNode( String name,
                         JdbcNode parent ) {
        super(FAKE_NODE_TYPE, name, parent);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#getPathInSource()
     */
    @Override
    public IPath getPathInSource() {
        return getPath();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getPathInSource(boolean, boolean)
     */
    public IPath getPathInSource( boolean includeCatalog,
                                  boolean includeSchema ) {
        return getPath();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#getParentDatabaseObject(boolean, boolean)
     */
    @Override
    public JdbcNode getParentDatabaseObject( boolean includeCatalog,
                                             boolean includeSchema ) {
        return null;
    }

    /**
     * In general, the
     * 
     * @param node
     * @throws JdbcException
     */
    public void addChildNode( final JdbcNode node ) throws JdbcException {
        super.addChild(node);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#computeChildren()
     */
    @Override
    protected JdbcNode[] computeChildren() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#getTypeName()
     */
    public String getTypeName() {
        return "FakeJdbcNode"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getFullyQualifiedName()
     */
    public String getFullyQualifiedName() {
        return getName();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#getJdbcDatabase()
     */
    public JdbcDatabase getJdbcDatabase() {
        return (getParent() != null ? getParent().getJdbcDatabase() : null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNode#refresh()
     */
    @Override
    public void refresh() {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeImpl#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

}
