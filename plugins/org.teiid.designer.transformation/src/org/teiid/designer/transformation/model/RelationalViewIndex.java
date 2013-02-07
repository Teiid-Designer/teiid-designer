/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.model;

import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.model.RelationalIndex;

/**
 *
 */
public class RelationalViewIndex  extends RelationalIndex {
    /**
     * 
     */
    public RelationalViewIndex() {
        super();
        setModelType(ModelType.VIRTUAL);
    }
}