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
package com.metamatrix.modeler.schema.tools.processing.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.SchemaModelImpl;

public class RequestProcessorImpl extends BaseRelationshipProcessor {

    public boolean DEBUG_SOUT_REPRESENTATIONS = true;

    private Map tableRelationships; // key: Relationship, value Integer

    public void calculateRelationshipTypes( SchemaModel model ) {
        setSechemaModel(model);
        List elements = model.getElements();

        for (Iterator iter = elements.iterator(); iter.hasNext();) {
            SchemaObject element = (SchemaObject)iter.next();

            int P_value; // the number of types of parent
            int C_value; // maxOccurs of the child within its parent
            // for maxOccurs, -2 means "mixed", -3 means "not set yet"
            // as as usual, -1 means "unbounded"
            boolean canBeRoot; // whether the table can be a document root
            int F_value;

            List parents = element.getParents();
            List columns = element.getAttributes();
            P_value = parents.size();
            F_value = columns.size();
            canBeRoot = element.isUseAsRoot();

            C_value = calculateCValue(parents);

            int representation = rules.calculateRelationship(P_value, C_value, canBeRoot, F_value);

            if (representation != Relationship.MERGE_IN_PARENT_MULTIPLE || representation != Relationship.MERGE_IN_PARENT_SINGLE) {
                element.setRepresentAsTable(true);
            }
            element.setAllParentRepresentations(representation, this);
        }

        removeRecursiveMerges(elements);

        qualifyDuplicateMergedTableNames();
        mergeRelationships();
        removeFullyMergedTables();
        qualifyDuplicateNonMergedTableNames();
        ((SchemaModelImpl)schemaModel).setTableRelationships(tableRelationships);

    }

    public int C_threshold() {
        return Integer.MAX_VALUE;
    }

    public int P_threshold() {
        return Integer.MAX_VALUE;
    }

    public int F_threshold() {
        return Integer.MAX_VALUE;
    }

    /**
     * Merges the columns and TableRelationships of a child into its parent(s).
     * 
     * @param parent The table to merge into.
     * @param tableRelationship The Relationship to the child being merged.
     */
    @Override
    protected void mergeChild( SchemaObject parent,
                               Relationship tableRelationship ) {
        SchemaObject child = tableRelationship.getChild();
        child.setWithinSelectedHierarchy(false);
        for (Iterator iter = child.getAttributes().iterator(); iter.hasNext();) {
            Object o = iter.next();
            Column col = (Column)o;
            col.mergeIntoParent(tableRelationship, -1);
        }
        pullUpGrandChildRelationships(child.getParents(), child.getChildren());
    }
}
