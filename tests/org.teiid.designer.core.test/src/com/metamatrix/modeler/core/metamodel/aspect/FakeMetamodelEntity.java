/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.ecore.EClass;

/**
 * FakeMetamodelEntity
 */
public class FakeMetamodelEntity implements MetamodelEntity {

    /**
     * Construct an instance of FakeMetamodelEntity.
     * 
     */
    public FakeMetamodelEntity() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getDisplayName()
     */
    public String getDisplayName() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getPluralDisplayName()
     */
    public String getPluralDisplayName() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getIconPath()
     */
    public String getIconPath() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getTooltip()
     */
    public String getTooltip() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getMetamodelAspect(java.lang.String)
     */
    public MetamodelAspect getMetamodelAspect(String id) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getMetamodelAspects()
     */
    public Collection getMetamodelAspects() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getEClass()
     */
    public EClass getEClass() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getReferencedClasses()
     */
    public List getReferencedClasses() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getContainedClasses()
     */
    public List getContainedClasses() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getAttributes()
     */
    public List getAttributes() {
        return null;
    }

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity#getUniDirectionalReferences()
	 */
	public List getUniDirectionalReferences() {
		return null;
	}

}
