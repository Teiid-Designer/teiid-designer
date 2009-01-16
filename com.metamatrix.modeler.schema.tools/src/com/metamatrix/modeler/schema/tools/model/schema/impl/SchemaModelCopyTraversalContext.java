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

package com.metamatrix.modeler.schema.tools.model.schema.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.metamatrix.modeler.schema.tools.ToolsPlugin;
import com.metamatrix.modeler.schema.tools.model.schema.ISchemaModelCopyTraversalContext;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public class SchemaModelCopyTraversalContext implements ISchemaModelCopyTraversalContext {

    // The running list of resultElements that have been copied.
    private Map copiedElements = new HashMap();

    // The results of the copy operation.
    private List resultElements;

    private Set resultRoots;

    // The resultElements passed in to copy
    private List originalElements;

    public SchemaModelCopyTraversalContext( List schemaElements,
                                            Set roots ) {
        this.originalElements = schemaElements;
        this.resultElements = new ArrayList(originalElements.size());

        process();
        if (null != roots) {
            this.resultRoots = new HashSet(roots);
        } else {
            this.resultRoots = new HashSet();
        }
    }

    private void process() {
        for (Iterator i = originalElements.iterator(); i.hasNext();) {
            SchemaObject original = (SchemaObject)i.next();
            SchemaObject copy = original.copy(this);
            copiedElements.put(original, copy);
            resultElements.add(copy);
        }
        for (int i = 0; i < originalElements.size(); i++) {
            SchemaObject original = (SchemaObject)originalElements.get(i);
            SchemaObject copy = (SchemaObject)resultElements.get(i);
            ((BaseSchemaObject)original).copy((BaseSchemaObject)copy, this);
        }
    }

    /**
     * Adds an ElementImpl and its copy to the Map of copied resultElements.
     * 
     * @param element The original ElementImpl
     * @param copy The copy
     */
    public void addElement( SchemaObject element,
                            SchemaObject copy ) {
        if (!copiedElements.containsKey(element)) {
            copiedElements.put(element, copy);
            resultElements.add(copy);
        }
    }

    /**
     * Returns an existing copy of an ElementImpl, or creates and returns one if one does not exist.
     * 
     * @param element The ElementImpl to copy
     * @return The copy of the element
     */
    public SchemaObject getElement( SchemaObject element ) {
        SchemaObject copy;
        if (!copiedElements.containsKey(element)) {
            throw new RuntimeException(ToolsPlugin.Util.getString("SchemaModelCopyTraversalContext.copiedElementNotFound")); //$NON-NLS-1$
        }
        copy = (SchemaObject)copiedElements.get(element);
        return copy;
    }

    public List getCopiedElements() {
        if (resultElements.size() != originalElements.size()) {
            throw new RuntimeException(ToolsPlugin.Util.getString("SchemaModelCopyTraversalContext.invalidCopiedElementTotal")); //$NON-NLS-1$
        }
        return resultElements;
    }

    public Set getCopiedRoots() {
        return resultRoots;
    }

}
