/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.jdbc.metadata.impl;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.metadata.JdbcCatalog;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor;
import com.metamatrix.modeler.jdbc.metadata.JdbcSchema;

/**
 * The ImportSettingsSelectionVisitor navigates a tree and records which nodes were selected, saving this information to the
 * supplied {@link com.metamatrix.modeler.jdbc.JdbcImportSettings JdbcImportSettings} object.
 * <p>
 * Note that upon visiting the first node, this visitor ensures that the existing
 * {@link com.metamatrix.modeler.jdbc.JdbcImportSettings JdbcImportSettings} are cleared of
 * {@link JdbcImportSettings#getIncludedCatalogPaths() included catalog paths},
 * {@link JdbcImportSettings#getIncludedSchemaPaths() included schema paths}, and
 * {@link JdbcImportSettings#getExcludedObjectPaths() excluded object paths}.
 * </p>
 * <p>
 * When this visitor visits the tree, it will navigate down branches that are either partially selected or selected. So if such
 * branches were not navigated to yet, the act of visiting will cause the loading of those nodes. However, because they are
 * selected (or partially selected), those nodes will have to be loaded anyway to get the metadata to be imported.
 * </p>
 */
public class ImportSettingsSelectionVisitor implements JdbcNodeVisitor {

    private final JdbcImportSettings settings;
    private boolean settingsHaveBeenCleared = false;

    /**
     * Construct an instance of ImportSettingsSelectionVisitor.
     */
    public ImportSettingsSelectionVisitor( final JdbcImportSettings settings ) {
        super();
        ArgCheck.isNotNull(settings);
        this.settings = settings;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor#visit(com.metamatrix.modeler.jdbc.metadata.JdbcNode)
     */
    public boolean visit( final JdbcNode node ) {
        if (node == null) {
            return false;
        }
        if (!this.settingsHaveBeenCleared) {
            this.settings.getIncludedCatalogPaths().clear();
            this.settings.getIncludedSchemaPaths().clear();
            this.settings.getExcludedObjectPaths().clear();
            settingsHaveBeenCleared = true;
        }

        // If the node is the database node
        if (node instanceof JdbcDatabase) {
            // do nothing with it and return true so that its children are processed
            return true;
        }

        // If the node is a catalog or schema, then record whether it was selected ...
        if (node instanceof JdbcCatalog) {
            if (node.getSelectionMode() == JdbcNode.UNSELECTED) {
                return false; // nothing more to see below this!
            }
            // It is selected or partially selected, so record that
            final String pathStr = node.getPath().toString();
            settings.getIncludedCatalogPaths().add(pathStr);
            return true;
        }
        if (node instanceof JdbcSchema) {
            if (node.getSelectionMode() == JdbcNode.UNSELECTED) {
                return false; // nothing more to see below this!
            }
            // It is selected or partially selected, so record that
            final String pathStr = node.getPath().toString();
            settings.getIncludedSchemaPaths().add(pathStr);
            return true;
        }

        // If the node allows children, then it is not a leaf node so it should be ignored ...
        if (node.allowsChildren()) {
            // However, first determine whether we should skip this branch completely
            if (node.getSelectionMode() == JdbcNode.UNSELECTED) {
                return false; // no need to visit branch
            }
            // Otherwise, it is either SELECTED or PARTIALLY_SELECTED
            // so we should visit this branch. Note that this may actually cause
            // branches that have not yet been loaded to be loaded. That's okay,
            // since those branches were selected and will have to be loaded anyway
            // during the import.
            return true;
        }

        // At this point, we know the node is a leaf node (i.e., JdbcTable or JdbcProcedure, although we have
        // to handle unknown types, too). For leaf nodes, we only need to record
        // those nodes that are NOT selected. The reason is that we will then by default
        // import the other nodes. So if any new tables, procedures, etc. show up,
        // they will by default be imported.
        final int selectionMode = node.getSelectionMode();
        if (selectionMode != JdbcNode.SELECTED) {
            final String pathStr = node.getPath().toString();
            settings.getExcludedObjectPaths().add(pathStr);
        }
        return false;
    }

}
