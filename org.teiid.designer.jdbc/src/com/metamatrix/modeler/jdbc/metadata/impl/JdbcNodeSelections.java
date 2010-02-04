/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;

/**
 * JdbcNodeSelections
 */
public class JdbcNodeSelections {
    
    public static final int SELECTED = JdbcNode.SELECTED;
    
    public static final int UNSELECTED = JdbcNode.UNSELECTED;
    
    public static final int PARTIALLY_SELECTED = JdbcNode.PARTIALLY_SELECTED;
    
    public static final int UNKNOWN = -1;
    
    private final Set selecteds;
    private final Set unselecteds;
    private final Set partiallySelected;

    /**
     * Construct an instance of JdbcNodeSelections.
     * 
     */
    public JdbcNodeSelections() {
        super();
        this.selecteds = new HashSet();
        this.unselecteds = new HashSet();
        this.partiallySelected = new HashSet();
    }
    
    /**
     * Initialize the node selections with the supplied settings, which record
     * the included catalogs and schemas and excluded leaf nodes.
     * <p>
     * This method follows the following logic: 
     * <ol>
     *   <li>Step 1: First go through all the excluded leaf nodes.  If a leaf node is excluded,
     *       then there is at least one sibling that is selected, and the parent is
     *       thus known to be partially selected.</li>
     *   <li>Step 2: Go through the included schemas and catalogs.  Any schema or catalog
     *       that is included but that has not yet been marked as partially
     *       selected means that there were no <i>excluded</i> nodes below that
     *       schema/catalog, so the schema/catalog is completely selected.</li>
     * </ol>
     * </p>
     * @param settings the settings; may not be null
     */
    public void initialize( final JdbcImportSettings settings ) {
        ArgCheck.isNotNull(settings);

        //-------------------------------------------------------------------------
        //  Step 1: Go through the excluded objects
        //-------------------------------------------------------------------------
        final Iterator iter = settings.getExcludedObjectPaths().iterator();
        while (iter.hasNext()) {
            final String pathStr = (String)iter.next();
            IPath path = new Path(pathStr);
            this.unselecteds.add(path);
        
            // Go up the path and add all ancestors as partially selected
            while ( path.segmentCount() > 1 ) {
                final IPath parentPath = path.removeLastSegments(1);
                this.partiallySelected.add(parentPath);
                path = parentPath;
            }
        }

        //-------------------------------------------------------------------------
        //  Step 2: Go through the included schemas and catalogs
        //-------------------------------------------------------------------------
        // Note that if a database has both schemas and catalogs, then the schemas
        // should be below the catalogs.  Consequently, in that case, we only want
        // to mark the schemas (and not mark any catalogs above those schemas).
        // When those schema nodes are materialized, they will get their correct
        // selection mode, and they will cause the selection mode of their parent
        // catalog to be property calculated.
        final Set ignoreCatalogsAboveSchemas = new HashSet();
        final Iterator schemaIter = settings.getIncludedSchemaPaths().iterator();
        while (schemaIter.hasNext()) {
            final String pathStr = (String)schemaIter.next();
            final IPath path = new Path(pathStr);
            // See if the path is already partially selected
            if ( !this.partiallySelected.contains(path) ) {
                // it is not, so that means the whole schema was selected ...
                this.selecteds.add(path);
                // and plan to ignore anything above the selected schema ...
                IPath tempPath = path;
                while ( tempPath.segmentCount() > 1 ) {
                    tempPath = tempPath.removeLastSegments(1);
                    ignoreCatalogsAboveSchemas.add(tempPath);
                }
            }
        }
        final Iterator catalogIter = settings.getIncludedCatalogPaths().iterator();
        while (catalogIter.hasNext()) {
            final String pathStr = (String)catalogIter.next();
            final IPath path = new Path(pathStr);
            
            // Ignore any catalog that was above a SELECTED schema ...
            if ( ignoreCatalogsAboveSchemas.contains(path) ) {
                // This catalog is above a SELECTED schema, so mark it as partially selected ...
                this.partiallySelected.add(path);
                continue;   // skip this one
            }
            
            // See if the path is already partially selected
            if ( !this.partiallySelected.contains(path) ) {
                // it is not, so that means the whole schema was selected ...
                this.selecteds.add(path);
            }
        }
    }
    
    /**
     * Return whether there are any paths that are known to be selected, unselected or partially selected.
     * @return true if there are at least some paths known to be selected, unselected or partially selected,
     * or false if there no known selection modes for any path
     */
    public boolean hasSelectionModes() {
        return this.selecteds.size() != 0 || this.partiallySelected.size() != 0 || this.unselecteds.size() != 0;
    }

    
    public int getSelectionMode( final IPath path ) {
        ArgCheck.isNotNull(path);
        if ( this.selecteds.contains(path) ) {
            return SELECTED;
        }
        if ( this.unselecteds.contains(path) ) {
            return UNSELECTED;
        }
        if ( this.partiallySelected.contains(path) ) {
            return PARTIALLY_SELECTED;
        }
        
        // -------------------------------------------------------------------
        // The path is not known.  However, the "default" can be determined by
        // looking at the ancestors.
        // -------------------------------------------------------------------
        
        // If this is the JdbcDatabase path ...
        if ( path.segmentCount() == 0 ) {
            // The default for the JdbcDatabase path should be to be PARTIALLY_SELECTED
            // so that children are figured out
            this.unselecteds.add(path);
            return UNSELECTED;
        }
        // If this path is for a root object ...
        if ( path.segmentCount() == 1 ) {
            // so the default should be to be UNSELECTED
            this.unselecteds.add(path);
            return UNSELECTED;
        }
        
        // Get the parent path and see what it's selection mode is ...
        final IPath parentPath = path.removeLastSegments(1);
        final int parentMode = getSelectionMode(parentPath);    // recursive!!!
        if ( parentMode == SELECTED ) {
            // The parent is fully selected, so should this node ...
            this.selecteds.add(path);
            return SELECTED;
        }
        if ( parentMode == PARTIALLY_SELECTED ) {
            // The parent is partially selected, so we don't know what to assume 
            return UNKNOWN;
//            // The parent is partially selected, so we'll assume nodes underneath a partially-selected
//            // node should be fully selected
//            this.selecteds.add(path);
//            return SELECTED;
        }
        // Parent is unselected, so this node should be as well ...
        return UNSELECTED;
    }
    
    public void setSelected( final IPath path, final int selectionMode ) {
        ArgCheck.isNotNull(path);
        if ( selectionMode == SELECTED ) {
            this.selecteds.add(path);
            this.unselecteds.remove(path);
            this.partiallySelected.remove(path);
        } else if ( selectionMode == UNSELECTED ) {
            this.selecteds.remove(path);
            this.unselecteds.add(path);
            this.partiallySelected.remove(path);
        } else if ( selectionMode == PARTIALLY_SELECTED ) {
            this.selecteds.remove(path);
            this.unselecteds.remove(path);
            this.partiallySelected.add(path);
        }
    }

}
