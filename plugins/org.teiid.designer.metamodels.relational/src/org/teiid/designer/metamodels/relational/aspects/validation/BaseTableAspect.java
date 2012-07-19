/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;

/**
 * TableAspect
 *
 * @since 8.0
 */
public class BaseTableAspect extends TableAspect {
    
    /**
     * Construct an instance of TableAspect.
     * @param entity
     */
    public BaseTableAspect(MetamodelEntity entity){
        super(entity);
    }
}
