/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.uml;


/**
 * StagingTableUmlAspect
 */
public class StagingTableUmlAspect extends MappingClassUmlAspect {

    /**
     * Construct an instance of StagingTableUmlAspect.
     * 
     */
    public StagingTableUmlAspect() {
        super();
    }
    
    /**
     * @see org.teiid.designer.transformation.aspects.uml.MappingClassUmlAspect#getStereotype(java.lang.Object)
     */
    @Override
    public String getStereotype(Object eObject) {
        return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_StagingTable_type"); //$NON-NLS-1$
    }


}
