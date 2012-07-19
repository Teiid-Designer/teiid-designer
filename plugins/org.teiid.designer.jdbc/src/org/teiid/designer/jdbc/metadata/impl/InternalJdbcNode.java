/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import org.teiid.designer.jdbc.metadata.JdbcNode;

/**
 * InternalJdbcNode
 *
 * @since 8.0
 */
public interface InternalJdbcNode {

    /**
     * Recalculate the selection mode of this object based on the supplied child whose selection has changed.
     */
    void checkSelectionMode( JdbcNode nodeWithChangedSelection );
}
