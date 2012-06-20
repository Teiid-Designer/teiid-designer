/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.runtime.IPath;
import org.teiid.designer.jdbc.metadata.JdbcDatabase;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.metadata.JdbcNodeVisitor;
import org.teiid.designer.jdbc.metadata.JdbcProcedureType;
import org.teiid.designer.jdbc.metadata.JdbcTableType;

/**
 * JdbcNodeMethodCallingVisitor
 */
public class JdbcNodeMethodCallingVisitor implements JdbcNodeVisitor {

    private List visitedNodes = new ArrayList();

    /**
     * Construct an instance of JdbcNodeMethodCallingVisitor.
     */
    public JdbcNodeMethodCallingVisitor() {
        super();
    }

    public boolean visit( final JdbcNode node ) {
        visitedNodes.add(node);
        // Call some of the methods of JdbcNode ...
        node.allowsChildren();
        node.getFullyQualifiedName();
        Assert.assertNotNull(node.getJdbcDatabase());
        Assert.assertNotNull(node.getName());
        if (!(node instanceof JdbcDatabase)) {
            Assert.assertNotNull(node.getParent());
        }
        node.getParentDatabaseObject(true, true);
        node.getParentDatabaseObject(true, false);
        node.getParentDatabaseObject(false, true);
        node.getParentDatabaseObject(false, false);
        final IPath path = node.getPath();
        Assert.assertNotNull(path);
        node.getPathInSource();
        node.getSelectionMode();
        final int type = node.getType();
        Assert.assertTrue(type >= JdbcNode.DATABASE && type <= JdbcNode.PROCEDURE_TYPE);
        final String typeName = node.getTypeName();
        Assert.assertTrue(node.hashCode() != 0);
        final boolean dbObj = node.isDatabaseObject();
        if (node instanceof JdbcDatabase || node instanceof JdbcTableType || node instanceof JdbcProcedureType) {
            Assert.assertEquals("Node " + path + " (type=" + typeName + ") expected to not be database object", false, dbObj); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        }
        return true;
    }

    public List getVisitedNodes() {
        return visitedNodes;
    }

}
