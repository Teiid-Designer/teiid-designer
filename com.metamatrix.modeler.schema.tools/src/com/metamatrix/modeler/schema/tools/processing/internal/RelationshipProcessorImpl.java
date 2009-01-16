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

            if (DEBUG_SOUT_REPRESENTATIONS) {
                DebugPrintRepresentations(element.getNamespace(),
                                          element.getSimpleName(),
                                          element.getType().getName(),
                                          element.getElementTypeNamespace(),
                                          parents,
                                          P_value,
                                          C_value,
                                          F_value,
                                          canBeRoot,
                                          representation,
                                          debugWriter);
            }

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

    private void DebugPrintRepresentations( String namespace,
                                            String elementName,
                                            String typeNamespace,
                                            String typeName,
                                            List parents,
                                            int P_value,
                                            int C_value,
                                            int F_value,
                                            boolean root,
                                            int representation,
                                            PrintWriter debugWriter ) {
        debugWriter.print("ElementImpl:\t" + namespace + "#" + elementName + ":\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        debugWriter.print("type:\t" + typeNamespace + "#" + typeName + ":\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        debugWriter.print("P: " + P_value + "\tC: " + C_value + "\tF: " + F_value + "\troot: " + (root ? "Y" : "N")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        debugWriter.print("\tRepresentation = " + representation + " ("); //$NON-NLS-1$ //$NON-NLS-2$
        switch (representation) {
            case Relationship.KEY_IN_CHILD:
                debugWriter.print("KEY_IN_CHILD"); //$NON-NLS-1$
                break;
            case Relationship.KEY_IN_PARENT_MULTIPLE:
                debugWriter.print("KEY_IN_PARENT_MULTIPLE"); //$NON-NLS-1$
                break;
            case Relationship.KEY_IN_PARENT_SINGLE:
                debugWriter.print("KEY_IN_PARENT_SINGLE"); //$NON-NLS-1$
                break;
            case Relationship.MERGE_IN_PARENT_MULTIPLE:
                debugWriter.print("MERGE_IN_PARENT_MULTIPLE"); //$NON-NLS-1$
                break;
            case Relationship.MERGE_IN_PARENT_SINGLE:
                debugWriter.print("MERGE_IN_PARENT_SINGLE"); //$NON-NLS-1$
                break;
            case Relationship.RELATIONSHIP_TABLE:
                debugWriter.print("RELATIONSHIP_TABLE"); //$NON-NLS-1$
                break;
        }
        debugWriter.print(")\t"); //$NON-NLS-1$
        boolean first = true;
        for (Iterator iter = parents.iterator(); iter.hasNext();) {
            Object o = iter.next();
            Relationship tableRelationship = (Relationship)o;
            String name = tableRelationship.getParent().getSimpleName();
            if (!first) {
                debugWriter.print(", "); //$NON-NLS-1$
            }
            first = false;
            debugWriter.print(name);
        }
        debugWriter.println(")"); //$NON-NLS-1$
        debugWriter.flush();
    }
}
