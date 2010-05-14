/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * SchemaAspect
 */
public class Uml2ModelUmlAspect extends Uml2PackageUmlAspect {
    /**
     * Construct an instance of SchemaAspect.
     * @param entity
     */
    public Uml2ModelUmlAspect(MetamodelEntity entity){
        super(entity);
    }
    
	@Override
    public String getStereotype(Object eObject) {
		return Uml2Plugin.getPluginResourceLocator().getString("_UI_Model_type"); //$NON-NLS-1$
	}
    
}
