/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.SchemaModelImpl;

public class RelationshipProcessorImpl extends BaseRelationshipProcessor {

    int c_threshold;

    int p_threshold;

    int f_threshold;

    public boolean DEBUG_SOUT_REPRESENTATIONS = true;

    /**
     * Creates a new releationship processor.
     * 
     * @param c_threshold Defines the upper bound for an element's maxoccurs value beyond which it will not be folded.
     * @param p_threshold Defines the upper bound for an element's number of different parents beyond which it will not be folded.
     * @param f_threshold Defines the upper bound for an element's number of fields beyond which it will not be folded.
     */
    public RelationshipProcessorImpl( int c_threshold,
                                      int p_threshold,
                                      int f_threshold ) {
        super();
        this.c_threshold = c_threshold;
        this.p_threshold = p_threshold;
        this.f_threshold = f_threshold;
    }

    // ////////////////////////////////////////////
    // Methods to modify the schema resources
    // ////////////////////////////////////////////
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessor#calculateRelationshipTypes(com.metamatrix.modeler.schema.tools.model.schema.SchemaModel)
     */
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.schema.tools.processing.internal.RelationshipProcessor#calculateRelationshipTypes(com.metamatrix.modeler.schema.tools.model.schema.SchemaModel)
     */
    public void calculateRelationshipTypes( SchemaModel model ) {
        setSechemaModel(model);
        List elements = model.getElements();
        PrintWriter debugWriter = null;
        if (DEBUG_SOUT_REPRESENTATIONS) {
            debugWriter = createDebugWriter(debugWriter);
        }

        qualifyDuplicateNonMergedTableNames();
        for (Iterator iter = elements.iterator(); iter.hasNext();) {
            SchemaObject element = (SchemaObject)iter.next();

            int P_value = 0; // the number of types of parent
            int C_value; // maxOccurs of the child within its parent
            // for maxOccurs, -2 means "mixed", -3 means "not set yet"
            // as as usual, -1 means "unbounded"
            boolean canBeRoot; // whether the table can be a document root
            int F_value;

            List parents = element.getParents();
            List columns = element.getAttributes();
            for (Iterator pIter = parents.iterator(); pIter.hasNext();) {
                SchemaObject parent = ((Relationship)pIter.next()).getParent();
                if (parent.isWithinSelectedHierarchy()) {
                    P_value++;
                }
            }
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

        if (DEBUG_SOUT_REPRESENTATIONS) {
            debugWriter.close();
        }
        mergeRelationships();
        removeFullyMergedTables();
        ((SchemaModelImpl)schemaModel).setTableRelationships(tableRelationships);
    }

    private PrintWriter createDebugWriter( PrintWriter debugWriter ) throws RuntimeException {
        try {
            File tempFile = File.createTempFile("info", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
            debugWriter = new PrintWriter(new FileWriter(tempFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return debugWriter;
    }

    public int C_threshold() {
        return c_threshold;
    }

    public int P_threshold() {
        return p_threshold;
    }

    public int F_threshold() {
        return f_threshold;
    }
}
