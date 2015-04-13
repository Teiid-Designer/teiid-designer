/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.model;

import org.teiid.designer.metamodels.core.ModelType;

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