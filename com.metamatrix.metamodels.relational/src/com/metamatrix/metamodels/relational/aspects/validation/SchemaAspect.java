/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * SchemaAspect
 */
public class SchemaAspect extends RelationalEntityAspect {
    
    /**
     * Construct an instance of SchemaAspect.
     * @param entity
     */
    public SchemaAspect(MetamodelEntity entity){
        super(entity);
    }
}
