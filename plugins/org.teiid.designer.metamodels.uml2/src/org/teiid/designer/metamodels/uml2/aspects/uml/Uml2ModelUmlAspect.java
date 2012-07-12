/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.uml2.aspects.uml;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.uml2.Uml2Plugin;


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
